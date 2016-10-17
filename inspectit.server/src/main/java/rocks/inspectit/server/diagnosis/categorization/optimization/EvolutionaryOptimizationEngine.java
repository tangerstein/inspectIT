package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.Random;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Opt4JTask;
import org.opt4j.optimizers.ea.EvolutionaryAlgorithmModule;
import org.opt4j.viewer.ViewerModule;

import rocks.inspectit.server.diagnosis.categorization.InstancesProvider;
import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterEngine;
import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterResultEvaluation;
import weka.core.Instances;

/**
 * Creates a possible cluster configuration.
 * 
 * @author Tobias Angerstein
 *
 */
public class EvolutionaryOptimizationEngine implements Creator<DoubleGenotype> {

	@Override
	public DoubleGenotype create() {
		DoubleGenotype genotype = new DoubleGenotype(0, 1000);
		genotype.init(new Random(), 6);
		return genotype;
	}

	/**
	 * Main: starts the evolutionary optimization engine.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {
		startOptimization(1000, 1000, 6);
	}

	/**
	 * Start the optimization.
	 * 
	 * @param generations
	 *            the generation size
	 * @param population
	 *            the population size
	 * @param numberOfWeights
	 *            the number of weights
	 * @return the optimized weights
	 */
	public static Double[] startOptimization(int generations, int population, int numberOfWeights) {
		EvolutionaryAlgorithmModule ea = new EvolutionaryAlgorithmModule();
		ea.setGenerations(generations);
		ea.setAlpha(population);
		ClusterProblemModule dtlz = new ClusterProblemModule();
		ViewerModule viewer = new ViewerModule();
		viewer.setCloseOnStop(true);
		Opt4JTask task = new Opt4JTask(false);
		task.init(ea, dtlz, viewer);
		Double[] optimizedWeights = new Double[numberOfWeights];
		try {
			task.execute();
			Archive archive = task.getInstance(Archive.class);
			for (Individual individual : archive) {
				DoubleGenotype optimizedGenotype = (DoubleGenotype) individual.getGenotype();
				optimizedWeights = optimizedGenotype.toArray(new Double[optimizedGenotype.size()]);
				System.out.println(individual.getGenotype().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			task.close();
			return optimizedWeights;
		}
	}
}

/**
 * Converts the given Cluster Solution (Genotype) into a Phenotype
 * (ClusterResult).
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemDecoder implements Decoder<DoubleGenotype, ArrayList<Instances>> {

	@Override
	public ArrayList<Instances> decode(DoubleGenotype genotype) {
		Double[] weights = genotype.toArray(new Double[genotype.size()]);
		ClusterEngine engine = new ClusterEngine();
		return engine.createClusterList(InstancesProvider.getDVDStoreInstances(), weights, 4);
	}
}

/**
 * Evaluates a given phenotype.
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemEvaluator implements Evaluator<ArrayList<Instances>> {
	@Override
	public Objectives evaluate(ArrayList<Instances> phenotype) {
		Objectives objectives = new Objectives();
		// Objective will be maximized
		objectives.add("objective", Sign.MAX, fitness(phenotype, InstancesProvider.getReferenceCluster()));
		return objectives;
	}

	/**
	 * Determines the fitness of a cluster result compared to a reference.
	 * cluster result
	 * 
	 * @param clusters
	 *            a list of clusters
	 * @param referenceClusters
	 *            the reference cluster result
	 * @return fitness can be between Integer.MIN and Integer.Max
	 */
	public double fitness(ArrayList<Instances> clusters, ArrayList<Instances> referenceClusters) {
		Double[] errorRates = ClusterResultEvaluation.getErrorRate(clusters);
		return (1.0 / (errorRates[0] * errorRates[4]));
	}

}

/**
 * Defines the optimization problem with its components.
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemModule extends ProblemModule {
	/**
	 * {@inheritDoc}
	 */
	protected void config() {
		bindProblem(EvolutionaryOptimizationEngine.class, ClusterProblemDecoder.class, ClusterProblemEvaluator.class);
	}
}
