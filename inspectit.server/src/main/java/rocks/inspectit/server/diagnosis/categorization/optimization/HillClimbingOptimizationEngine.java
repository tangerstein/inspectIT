package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.problems.sol.RandomSolutionGenerator;
import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxSteps;

import rocks.inspectit.server.diagnosis.categorization.InstancesProvider;
import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterEngine;

/**
 * Optimizes the weights of the cluster engine by an hill climbing algorithm.
 * 
 * @author Tobias Angerstein
 *
 */
public class HillClimbingOptimizationEngine {
	/**
	 * main.
	 * 
	 * @param args
	 *            arguements
	 */
	public static void main(String[] args) {
		System.out.println(startOptimization(1000));

	}

	/**
	 * Start the optimization.
	 * 
	 * @param maxSteps
	 *            defines the maximum number of generations
	 * 
	 * @return the optimized weights
	 */
	public static Double[] startOptimization(int maxSteps) {
		ClusterProblemData data = new ClusterProblemData(6);
		ClusterProblemObjective obj = new ClusterProblemObjective();
		RandomSolutionGenerator<ClusterSolution, ClusterProblemData> rsg = (r, d) -> {
			// create weights
			List<Double> weightList = new ArrayList<>();
			for (int i = 0; i < (d.getNumberOfWeights()); i++) {
				weightList.add(1.);
			}
			// manipulate weights randomly
			Set<Integer> randomIndices = getRandomNumberOfIndices(weightList.size());
			for (int i : randomIndices) {
				weightList.set(i, weightList.get(i) * (new Random().nextDouble() * 1000));
			}
			// create and return solution
			return new ClusterSolution(weightList);
		};
		// define a problem
		Problem<ClusterSolution> problem = new GenericProblem<>(data, obj, rsg);

		RandomDescent<ClusterSolution> hillClimber = new RandomDescent<>(problem, new ClusterProblemOptNeighbourhood());
		hillClimber.addStopCriterion(new MaxSteps(maxSteps));
		hillClimber.start();
		ClusterSolution bestSolution = hillClimber.getBestSolution();
		String result = "";
		for (int i = 0; i < bestSolution.getWeights().length; i++) {
			result += "[" + bestSolution.getWeights()[i] + "]";
		}
		return bestSolution.getWeights();
	}
	
	/**
	 * Creates a set of random indices.
	 * 
	 * @param n
	 *            the range of indices (e.g. size of a list)
	 * @return set of indices
	 */
	private static Set<Integer> getRandomNumberOfIndices(int n) {
		Set<Integer> indices = new HashSet<Integer>();
		int numberOfIndices = new Random().nextInt(n);
		while (indices.size() != numberOfIndices) {
			indices.add(new Random().nextInt(n));
		}
		return indices;
	}
}

/**
 * Represents one possible Cluster Solution with its corresponding weights.
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterSolution extends Solution {
	/**
	 * the weights for the ClusterEngine.
	 */
	private List<Double> weights;

	/**
	 * Constructor.
	 * 
	 * @param weights
	 *            the weights
	 */
	ClusterSolution(List<Double> weights) {
		this.weights = weights;
	}

	/**
	 * get.
	 * 
	 * @return the weights
	 */
	public Double[] getWeights() {
		Double[] weightsArray = new Double[weights.size()];
		return weights.toArray(weightsArray);
	}

	/**
	 * increases the value of weight i with the given factor.
	 * 
	 * @param i
	 *            the index of the weight
	 * @param factor
	 *            the factor
	 */
	public void increaseValue(int i, double factor) {
		weights.set(i, (weights.get(i) * factor));
	}

	@Override
	public ClusterSolution copy() {
		return new ClusterSolution(new ArrayList<>(weights));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ClusterSolution other = (ClusterSolution) obj;
		return Objects.equals(this.weights, other.weights);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(weights);
	}

}

/**
 * Data holder for the optimization problem.
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemData {
	/**
	 * The number of weights.
	 */
	private int numberOfWeights;

	/**
	 * Constructor.
	 * 
	 * @param numberOfWeights
	 *            the number of weights
	 */
	ClusterProblemData(int numberOfWeights) {
		this.numberOfWeights = numberOfWeights;
	}

	public int getNumberOfWeights() {
		return numberOfWeights;
	}

}

/**
 * Defines the rating of the current Cluster Result (uses the fitness function of the evolutionary
 * approach).
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemObjective implements Objective<ClusterSolution, ClusterProblemData> {
	/**
	 * {@inheritDoc}
	 */
	public Evaluation evaluate(ClusterSolution solution, ClusterProblemData data) {

		// Compute the fitness of the current cluster result
		double fitness = new ClusterProblemEvaluator().fitness(
				new ClusterEngine().createClusterList(InstancesProvider.getDVDStoreInstances(),
						solution.getWeights(), 4),
				InstancesProvider.getReferenceCluster());

		return SimpleEvaluation.WITH_VALUE(fitness);

	}

	public boolean isMinimizing() {
		return false;
	}

}

/**
 * Represents a random move of the hillclimbing algorithm.
 * 
 * @author tan
 *
 */
class ClusterProblemOptMove implements Move<ClusterSolution> {
	/**
	 * the index of the weight.
	 */
	private final int i;

	/**
	 * Constructor.
	 * 
	 * @param i
	 *            index of the weight
	 */
	ClusterProblemOptMove(int i) {
		this.i = i;

	}

	public int getI() {
		return i;
	}

	@Override
	public void apply(ClusterSolution solution) {
		solution.increaseValue(i, 5 * new Random().nextDouble());
	}

	@Override
	public void undo(ClusterSolution solution) {
		// solution.increaseValue(i, 0.2);
	}

}

/**
 * Provides new random moves and all possible moves.
 * 
 * @author Tobias Angerstein
 *
 */
class ClusterProblemOptNeighbourhood implements Neighbourhood<ClusterSolution> {
	/**
	 * 
	 * {@inheritDoc}
	 */
	public ClusterProblemOptMove getRandomMove(ClusterSolution solution, Random rnd) {
		int i = rnd.nextInt(solution.getWeights().length);

		return new ClusterProblemOptMove(i);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public List<ClusterProblemOptMove> getAllMoves(ClusterSolution solution) {
		List<ClusterProblemOptMove> moves = new ArrayList<>();
		for (int i = 0; i < solution.getWeights().length; i++) {
			moves.add(new ClusterProblemOptMove(i));
		}
		return moves;

	}

}
