package rocks.inspectit.server.diagnosis.categorization.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import org.springframework.util.comparator.InstanceComparator;

import rocks.inspectit.server.diagnosis.categorization.optimization.CohensKappa;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class provides a statistical analysis of a cluster result.
 * 
 * @author Tobias Angerstein
 *
 */
public class ClusterResultEvaluation {
	/**
	 * Computes the quality of the given cluster using kappa statistics.
	 * 
	 * @param clusters
	 *            the given cluster solution
	 * @param referenceClusters
	 *            the referencial cluster solution
	 * @return kappa value
	 */
	public static double getQuality(ArrayList<Instances> clusters, ArrayList<Instances> referenceClusters) {
		InstanceComparator<Instance> comparator = new InstanceComparator<Instance>();
		double kappa = 0;
		for (int l = 0; l < clusters.size(); l++) {
			for (int k = 0; k < referenceClusters.size(); k++) {
				HashMap<Instance, Integer[]> table = new HashMap<Instance, Integer[]>();
				ArrayList<Integer> one = new ArrayList<Integer>();
				ArrayList<Integer> two = new ArrayList<Integer>();
				for (int i = 0; i < referenceClusters.size(); i++) {
					for (Instance refInstance : referenceClusters.get(i)) {
						for (int j = 0; j < clusters.size(); j++) {
							for (Instance curInstance : clusters.get(j)) {
								if (comparator.compare(refInstance, curInstance) == 0) {
									table.put(refInstance, new Integer[] { j, i });
								}
							}
						}
					}
				}
				for (Integer[] indices : table.values()) {
					one.add(indices[0]);
					two.add(indices[1]);
				}
				List<List<Integer>> list = new ArrayList<List<Integer>>();
				list.add(one);
				list.add(two);
				double currentKappa = CohensKappa.computeKappa(list);
				if (currentKappa > kappa) {
					kappa = currentKappa;
				}
				Collections.rotate(referenceClusters, 1);
			}
			Collections.rotate(clusters, 1);
		}
		return kappa;
	}

	/**
	 * Provides some statistical information about the cluster.
	 * 
	 * @param clusters
	 *            the cluster, which has to be analyzed
	 * @return array with statistical metrics. 0-> Sum of squared errors 1->
	 *         Mean of the distances between the cluster centers 2-> Min of the
	 *         distances between the cluster centers 3-> Max of the distances
	 *         between the cluster centers 4-> Standard deviation of the
	 *         distances between the cluster centers
	 */
	public static Double[] getErrorRate(ArrayList<Instances> clusters) {
		double errorRate = 0;
		ConcurrentLinkedQueue<Instance> clusterCenters = new ConcurrentLinkedQueue<Instance>();
		for (Instances cluster : clusters) {
			SimpleKMeans kmeans = new SimpleKMeans();
			try {
				kmeans.setOptions(weka.core.Utils.splitOptions(
						"-init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N 1 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10"));
				kmeans.buildClusterer(cluster);
				clusterCenters.addAll(kmeans.getClusterCentroids());
				// System.out.println(kmeans.toString());
				errorRate += kmeans.getSquaredError();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		EuclideanDistance distanceMetric = new EuclideanDistance(clusters.get(0));
		Instance currentClusterCenter;
		ArrayList<Double> distances = new ArrayList<Double>();

		for (Instance clusterCenter : clusterCenters) {
			currentClusterCenter = clusterCenters.poll();
			for (Instance clusterCenter2 : clusterCenters) {
				distances.add(distanceMetric.distance(currentClusterCenter, clusterCenter2));
			}
		}
		double[] distancesArray = ArrayUtils.toPrimitive(distances.toArray(new Double[distances.size()]));
		// 0 -> errorrate 1 -> mean 2-> min 3- > max
		Double[] result = new Double[5];
		result[0] = errorRate;
		// Compute statistics
		result[1] = StatUtils.mean(distancesArray);
		result[2] = StatUtils.min(distancesArray);
		result[3] = StatUtils.max(distancesArray);
		result[4] = FastMath.sqrt(StatUtils.variance(distancesArray));

		return result;
	}

}
