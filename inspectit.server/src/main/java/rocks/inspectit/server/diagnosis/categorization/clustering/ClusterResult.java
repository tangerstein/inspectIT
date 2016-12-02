package rocks.inspectit.server.diagnosis.categorization.clustering;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;

import rocks.inspectit.shared.all.spring.logger.Log;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Represents the result of the ClusterEngine and the associated weights.
 * 
 * @author Tobias Angerstein
 *
 */
public class ClusterResult {

	/**
	 * Critical value is used to compute the standard deviation.
	 */
	private static final double CRITICAL_VALUE = 1.96;

	/**
	 * The resultList of the ClusterEngine.
	 *
	 */
	private ArrayList<Instances> instances;

	/**
	 * The corresponding weights.
	 */
	private Double[] weights;

	/**
	 * The logger of this class.
	 */
	@Log
	Logger log;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param instances
	 *            the instances
	 * @param weights
	 *            the weights
	 */
	public ClusterResult(ArrayList<Instances> instances, Double[] weights) {
		super();
		this.instances = instances;
		this.weights = weights;
	}

	/**
	 * Get.
	 * 
	 * @return cluster result list
	 */
	public ArrayList<Instances> getWekaResult() {
		return instances;
	}

	/**
	 * Set.
	 * 
	 * @param instances
	 *            cluster result list
	 */
	public void setWekaResult(ArrayList<Instances> instances) {
		this.instances = instances;
	}

	/**
	 * Get.
	 * 
	 * @return weights of the current cluster result
	 */
	public Double[] getWeights() {
		return weights;
	}

	/**
	 * Set.
	 * 
	 * @param weights
	 *            the corresponding weights
	 */
	public void setWeights(Double[] weights) {
		this.weights = weights;
	}

	/**
	 * Returns a formated String of the weights.
	 * 
	 * @return weights represented as Strings
	 */
	public String getWeightsString() {
		String weightsString = "";
		for (int i = 0; i < weights.length; i++) {
			weightsString += "[" + weights[i] + "]";
		}
		return weightsString;
	}

	/**
	 * Prints the analysis of the cluster result.
	 */
	public void print() {
		System.out.println("Number of clusters: " + getWekaResult().size() + "\n");
		System.out.println("Weights: " + getWeightsString());
		int clusterCounter = 0;
		for (Instances cluster : getWekaResult()) {
			System.out.println("Cluster " + clusterCounter);
			System.out.println("Number of Instances:  " + cluster.size());
			int numberOfAttributes = getWekaResult().get(0).numAttributes();
			for (int i = 0; i < numberOfAttributes; i++) {
				System.out.println(getAttributeAnalysis(cluster.attribute(i), cluster));
			}
			clusterCounter++;
		}
	}

	/**
	 * Provides a String with analysis data.
	 * 
	 * @param attribute
	 *            the attribute, which will be analyzed
	 * @param currentCluster
	 *            the currentCluster, which will be analyzed
	 * @return String with analysis data
	 */
	private String getAttributeAnalysis(Attribute attribute, Instances currentCluster) {
		String resultString = "";
		if (attribute.isNominal()) {
			HashMap<String, Integer> numberOfOccuredNominalValues = new HashMap<String, Integer>();
			for (Instance instance : currentCluster) {
				String key = attribute.value((int) instance.value(attribute));
				if (numberOfOccuredNominalValues.containsKey(key)) {
					// increment counter in hashmap
					numberOfOccuredNominalValues.put(key, numberOfOccuredNominalValues.get(key) + 1);
				} else {
					numberOfOccuredNominalValues.put(key, 1);
				}
			}
			resultString = "[Nominal] Occured values of " + attribute.name() + ": ";
			for (String key : numberOfOccuredNominalValues.keySet()) {
				resultString += "[" + key + "(" + numberOfOccuredNominalValues.get(key) + ")] ";
			}
		} else {
			resultString = "[Numeric] Statistics of " + attribute.name() + ": ";
			ArrayList<Double> numericValueList = new ArrayList<Double>();
			for (Instance instance : currentCluster) {
				numericValueList.add(instance.value(attribute));
			}
			double[] numericValues = ArrayUtils
					.toPrimitive(numericValueList.toArray(new Double[numericValueList.size()]));
			// Compute statistics
			double std = FastMath.sqrt(StatUtils.variance(numericValues));
			double mean = StatUtils.mean(numericValues);
			double lowerBound = mean - CRITICAL_VALUE * (std / FastMath.sqrt(numericValues.length));
			double upperBound = mean + CRITICAL_VALUE * (std / FastMath.sqrt(numericValues.length));
			resultString += "[StDev: " + std + "] [Variance: " + StatUtils.variance(numericValues) + "] [Mean: " + mean
					+ "] [Confidence interval(95%): {" + lowerBound + "<->" + upperBound + "}";
		}
		return resultString;
	}

}
