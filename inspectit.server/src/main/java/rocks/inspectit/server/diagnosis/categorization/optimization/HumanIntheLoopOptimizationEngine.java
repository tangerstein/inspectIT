package rocks.inspectit.server.diagnosis.categorization.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import rocks.inspectit.server.diagnosis.categorization.InstancesProvider;
import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterEngine;
import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterResult;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Human-in-the loop optimization approach: Optimizes the weights of the
 * {@link ClusterEngine}} by human decision.
 * 
 * @author Tobias Angerstein
 *
 */
public class HumanIntheLoopOptimizationEngine {

	/**
	 * The weights which has to be optimized.
	 */
	private Double[] weights;

	/**
	 * The clusterEngine.
	 */
	private ClusterEngine clustering = new ClusterEngine();

	/**
	 * The instances, which has to be clustered.
	 */
	private Instances instances;

	/**
	 * the level, at which the ClusterTree will be cut.
	 */
	private int level;

	/**
	 * The factor, which manipulates the weights.
	 */
	private double factor;

	/**
	 * Constructor.
	 * 
	 * @param instances
	 *            the instances, which has to be clustered
	 * @param weights
	 *            the initial weights, which have to be optimized.
	 */
	public HumanIntheLoopOptimizationEngine(Instances instances, Double[] weights) {
		this.factor = 1;
		this.level = 1;
		this.instances = instances;
		this.weights = weights;
	}

	/**
	 * Generates 2* numberOfAttributes new results (based on a new approach).
	 * 
	 * @return list of cluster results
	 */
	private ArrayList<ClusterResult> generateNewResults() {
		ArrayList<ClusterResult> results = new ArrayList<ClusterResult>();
		// Baseline
		results.add(new ClusterResult(clustering.createClusterList(instances, weights, level), weights));
		for (int i = 0; i < weights.length; i++) {
			// Increase attribute
			Double[] manipulatedWeights = new Double[weights.length];
			System.arraycopy(weights, 0, manipulatedWeights, 0, weights.length);
			manipulatedWeights[i] *= this.factor;
			results.add(new ClusterResult(clustering.createClusterList(instances, manipulatedWeights, level),
					manipulatedWeights));
			// Decrease Attribute
			Double[] manipulatedWeights2 = new Double[weights.length];
			System.arraycopy(weights, 0, manipulatedWeights2, 0, weights.length);
			manipulatedWeights2[i] *= (1 / this.factor);
			results.add(new ClusterResult(clustering.createClusterList(instances, manipulatedWeights2, level),
					manipulatedWeights2));

		}
		return results;
	}

	/**
	 * Generates numberOfAttributes+1 new results (based on Plackett & Burman).
	 * 
	 * @return list of cluster results
	 */
	private ArrayList<ClusterResult> generateNewResultsPlackettBurman() {
		ArrayList<ClusterResult> results = new ArrayList<ClusterResult>();
		// Baseline
		results.add(new ClusterResult(clustering.createClusterList(instances, weights, level), weights));
		ArrayList<int[]> matrix = PlackettBurmanGenerator.getMatrix(weights.length);
		// for-Schleife über die runs
		for (int[] run : matrix) {
			Double[] manipulatedWeights = new Double[weights.length];
			System.arraycopy(weights, 0, manipulatedWeights, 0, weights.length);
			for (int i = 0; i < run.length; i++) {
				if (run[i] == 1) {
					manipulatedWeights[i] *= this.factor;
				}
			}

			results.add(new ClusterResult(clustering.createClusterList(instances, manipulatedWeights, level),
					manipulatedWeights));
		}
		return results;

	}

	/**
	 * Starts next iteration and adapts the manipulation factor.
	 * 
	 * @param usePlackettBurman
	 *            true, if Plackett & Burman should be used
	 * @return a list of cluster results
	 */
	private ArrayList<ClusterResult> doNextIteration(boolean usePlackettBurman) {
		ArrayList<ClusterResult> results;
		if (!usePlackettBurman) {
			results = generateNewResults();
		} else {
			results = generateNewResultsPlackettBurman();
		}
		this.factor *= 0.99999;
		return results;

	}

	/**
	 * Chooses the best solution from all generated and sets the current best solution as global
	 * baseline.
	 * 
	 * @param results
	 *            the cluster results
	 * @param index
	 *            the chosen index
	 */
	private void chooseBestResult(ArrayList<ClusterResult> results, int index) {
		this.weights = results.get(index).getWeights();
	}

	/**
	 * Starts the optimization.
	 * 
	 * @return the optimized weights
	 */
	public Double[] startOptimization() {
		Scanner scanner = new Scanner(System.in);
		String readString = "";
		System.err.println("Please choose the level of the cluster result tree!");
		this.level = Integer.parseInt(scanner.nextLine());
		System.out.println("Please choose the factor, which manipulates the weights in each iteration!");
		this.factor = Integer.parseInt(scanner.nextLine());

		int iteration = 0;
		while (readString != null) {
			System.err.println(
					"######################################################################################################################################################################################################\n"
							+ iteration + "th iteration"
							+ "\n######################################################################################################################################################################################################");
			ArrayList<ClusterResult> results = doNextIteration(true);
			printClusterResultsWithMetaInformation(results);
			System.err.println("Please choose the desired Resultnumber \n q-> quit");
			if (scanner.hasNextLine()) {
				System.err.println("Please choose the desired Resultnumber \n q-> quit");
				readString = scanner.nextLine();
			} else {
				readString = null;
			}
			if (readString.equals("q")) {
				readString = null;
				return this.weights;
			}
			if (StringUtils.isNumeric(readString)) {
				System.err.println("Solution " + readString + " was chosen!");
				chooseBestResult(results, Integer.parseInt(readString));
			}
			iteration++;
		}
		scanner.close();
		return this.weights;
	}

	/**
	 * Prints the cluster results with all instances.
	 * 
	 * @param results
	 *            the Cluster results, which are visualized
	 */
	private void printWholeClusterResults(ArrayList<ClusterResult> results) {
		for (int resultIndex = 0; resultIndex < results.size(); resultIndex++) {
			System.out.println(
					"\n----------------------------------------------------------------------------------------------\n"
							+ "Cluster Result " + resultIndex
							+ "\n----------------------------------------------------------------------------------------------");
			System.out.println("Anzahl Cluster: " + results.get(resultIndex).getWekaResult().size() + "\n");
			for (int clusterIndex = 0; clusterIndex < results.get(resultIndex).getWekaResult().size(); clusterIndex++) {
				System.out.println("Cluster " + clusterIndex + "\n");
				for (int instanceIndex = 0; instanceIndex < results.get(resultIndex).getWekaResult().get(clusterIndex)
						.size(); instanceIndex++) {
					Instance instance = results.get(resultIndex).getWekaResult().get(clusterIndex)
							.instance(instanceIndex);
					String resultString = "";
					for (int attributeIndex = 0; attributeIndex < Collections.list(instance.enumerateAttributes())
							.size(); attributeIndex++) {
						Attribute attribute = Collections.list(instance.enumerateAttributes()).get(attributeIndex);
						if (attribute.isNominal()) {
							resultString += (" [" + attribute.name() + ": "
									+ attribute.value((int) instance.value(attributeIndex)) + "],");
						} else {
							resultString += (" [" + attribute.name() + ": " + instance.value(attributeIndex) + "],");
						}
					}
					System.out.println(resultString);
				}
			}
		}
	}

	/**
	 * Prints the given cluster results with statistical data.
	 * 
	 * @param results
	 *            the generated results
	 */
	private void printClusterResultsWithMetaInformation(ArrayList<ClusterResult> results) {
		for (int resultIndex = 0; resultIndex < results.size(); resultIndex++) {
			System.out.println(
					"\n----------------------------------------------------------------------------------------------\n"
							+ "Cluster Result " + resultIndex
							+ "\n----------------------------------------------------------------------------------------------");
			results.get(resultIndex).print();
		}
	}

	/**
	 * main.
	 * 
	 * @param args
	 *            arguements.
	 */
	public static void main(String[] args) {
		Instances randomInstances = InstancesProvider.createRandomInstances(100);
		HumanIntheLoopOptimizationEngine engine = new HumanIntheLoopOptimizationEngine(randomInstances,
				new Double[] { 1., 1., 1., 1., 1., 1. });
		Double[] optimizedWeights = engine.startOptimization();
		System.out.println(optimizedWeights);

	}
}
