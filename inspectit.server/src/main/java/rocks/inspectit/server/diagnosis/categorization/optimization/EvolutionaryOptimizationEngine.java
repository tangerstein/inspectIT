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
import weka.core.Instance;
import weka.core.InstanceComparator;
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
		DoubleGenotype genotype = new DoubleGenotype(0, 5);
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
		EvolutionaryAlgorithmModule ea = new EvolutionaryAlgorithmModule();
		ea.setGenerations(1000);
		ea.setAlpha(10000);
		ClusterProblemModule dtlz = new ClusterProblemModule();
		ViewerModule viewer = new ViewerModule();
		viewer.setCloseOnStop(false);
		Opt4JTask task = new Opt4JTask(false);
		task.init(ea, dtlz, viewer);
		try {
			task.execute();
			Archive archive = task.getInstance(Archive.class);
			for (Individual individual : archive) {
				System.out.println(individual.getGenotype().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			task.close();
		}
	}
}

/**
 * Converts the given Cluster Solution (Genotype) into a Phenotype (ClusterResult).
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemDecoder implements Decoder<DoubleGenotype, ArrayList<Instances>> {

	@Override
	public ArrayList<Instances> decode(DoubleGenotype genotype) {
		Double[] weights = genotype.toArray(new Double[genotype.size()]);
		ClusterEngine engine = new ClusterEngine();
		return engine.createClusterList(InstancesProvider.getStaticInstances(), weights, 4);
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
		objectives.add("objective", Sign.MAX,
				fitness(phenotype, InstancesProvider.getReferenceCluster(), InstancesProvider.getStaticInstances()));
		return objectives;
	}

	/**
	 * Determines the fitness of a cluster result compared to a reference. cluster result
	 * 
	 * @param clusters
	 *            a list of clusters
	 * @param referenceClusters
	 *            the reference cluster result
	 * @param allInstances
	 *            list of all used instances
	 * @return fitness can be between Integer.MIN and Integer.Max
	 */
	public double fitness(ArrayList<Instances> clusters, ArrayList<Instances> referenceClusters,
			Instances allInstances) {
		InstanceComparator comparator = new InstanceComparator();
		if (referenceClusters.size() != clusters.size()) {
			return Double.MIN_VALUE;
		} else {
			int fitness = 0;
				for (Instance p : allInstances) {
					for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
					for (Instance instance : clusters.get(clusterIndex)) {
						if (comparator.compare(p, instance) == 0) {
							for (Instance referenceInstance : referenceClusters.get(clusterIndex)) {
								if (comparator.compare(p, referenceInstance) == 0) {
									fitness++;
								}
							}
						}
						}

				}
			}
			return fitness;
		}
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
