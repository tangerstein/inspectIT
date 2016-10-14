package rocks.inspectit.server.diagnosis.categorization.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import rocks.inspectit.server.diagnosis.categorization.InstancesProvider;
import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddID;

/**
 * Provides the clustering of the performance problem instances.
 * 
 * @author Tobias Angerstein
 *
 */
public class ClusterEngine {
	/**
	 * Configuration of the hierarchical clusterer and the distance function.
	 */
	// TODO: Array statt String + Kommentar erweitern
	private static final String HIERARCHICAL_CLUSTERER_CONFIG = "-N 1 -L SINGLE -P -A \"weka.core.EuclideanDistance -R first-5\"";

	/**
	 * Configuration of the EM Clusterer.
	 */
	private static final String EM_CLUSTERER_CONFIG = "-I 100 -N -1 -X 10 -max -1 -ll-cv 1.0E-6 -ll-iter 1.0E-6 -M 1.0E-6 -K 10 -num-slots 1 -S 100";

	/**
	 * Clusters the given instances and adds some weighting to the cluster
	 * algorithm.
	 * 
	 * @param data
	 *            the instances, which will be clustered
	 * @param weights
	 *            the weights of the attribute
	 * @return String representation of the cluster result, null if ... TODO
	 */
	// TODO: abfangen, wenn weight array weniger / mehr Einträge hat oder 0 ...
	// Grenzfälle ... Äquivalenzklassen
	private String cluster(Instances data, Double[] weights) {
		HierarchicalClusterer hierarchicClusterer = new HierarchicalClusterer();
		// Set the WEKA cluster result
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < data.numInstances(); j++) {
				data.instance(j).attribute(i).setWeight(weights[i]);
			}
		}
		String result = null;
		try {
			// Configuration of the cluster algorithm: filtering
			// TODO: Configuration mitgeben lassen
			String[] options = new String[2];
			options[0] = "-C";
			options[1] = "last";
			AddID addIdFilter = new AddID();
			// Add unique id to each instance
			addIdFilter.setOptions(options);
			addIdFilter.setInputFormat(data);
			Instances newData = Filter.useFilter(data, addIdFilter);

			hierarchicClusterer.setOptions(weka.core.Utils.splitOptions(HIERARCHICAL_CLUSTERER_CONFIG));
			hierarchicClusterer.buildClusterer(newData);

			result = hierarchicClusterer.toString();
			System.out.println(result);
			//
			// Matcher m = Pattern.compile("\(([^]+)]]\)").matcher(result);
			//
			// System.out.println(m.group(1));
		} catch (Exception e) {
			// TODO: Log Error
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * Hierarchical Clustering: Clusters the given instances and adds some
	 * weighting to the cluster algorithm.
	 * 
	 * @param data
	 *            the instances, which will be clustered
	 * @param weights
	 *            the weights of the attribute
	 * @return String representation of the cluster result
	 */
	public ClusterResult clusterOptimizedKMeans(Instances data, Double[] weights) {
		EM optimizedKMeans = new EM();
		ArrayList<Instances> clusterResult = new ArrayList<Instances>();
		// Set the WEKA cluster result
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < data.numInstances(); j++) {
				data.instance(j).attribute(i).setWeight(weights[i]);
			}
		}
		try {
			optimizedKMeans.setOptions(weka.core.Utils.splitOptions(EM_CLUSTERER_CONFIG));
			optimizedKMeans.buildClusterer(data);
			ArrayList<ArrayList<Instance>> clusterResultList = new ArrayList<ArrayList<Instance>>();

			for (Instance instance : data) {
				while ((clusterResultList.size()) <= (optimizedKMeans.clusterInstance(instance))) {
					clusterResultList.add(new ArrayList<Instance>());
				}
				clusterResultList.get(optimizedKMeans.clusterInstance(instance)).add(instance);

			}
			for (ArrayList<Instance> cluster : clusterResultList) {
				if (!cluster.isEmpty()) {
					Instances instances = new Instances("ProblemInstances",
							Collections.list(data.enumerateAttributes()), cluster.size());
					for (Instance instance : cluster) {
						instances.add(instance);
					}
					clusterResult.add(instances);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ClusterResult(clusterResult, weights);

	}

	/***
	 * Pulls up all leafs to a level.
	 * 
	 * @param root
	 *            The root node of the result tree
	 * @param level
	 *            the level, where all leafs will be added.
	 * @return the manipulated node
	 */
	public ClusterNode cutTreeByLevel(ClusterNode root, int level) {
		ClusterNode node = root;
		if (level < 1) {
			throw new IllegalArgumentException("Level must be at least 1");
		}
		ConcurrentLinkedDeque<ClusterNode> nodes = new ConcurrentLinkedDeque<ClusterNode>();
		nodes.addAll(node.getClusterChildren());
		ClusterNode currentNode;
		while (!nodes.isEmpty()) {
			currentNode = nodes.poll();
			if (currentNode.getLevel() == level && !(currentNode instanceof ClusterLeaf)) {
				currentNode = pullUpAllLeafs(currentNode);
				currentNode = removeAllUnusedClusterNodes(currentNode);
				currentNode = aggregateAllInstances(currentNode);
			} else if (currentNode.getClusterChildren() != null) {
				nodes.addAll(currentNode.getClusterChildren());
			}
		}
		return node;
	}

	/**
	 * Removes all child nodes, which aren't leafs.
	 * 
	 * @param root
	 *            the local root node
	 * @return the manipulated node
	 */
	private ClusterNode removeAllUnusedClusterNodes(ClusterNode root) {
		ClusterNode node = root;
		node.setClusterChildren((ArrayList<ClusterNode>) node.getClusterChildren().parallelStream()
				.filter(n -> n instanceof ClusterLeaf).collect(Collectors.toList()));
		return node;
	}

	/**
	 * Adds all Leafs to the root node.
	 * 
	 * @param root
	 *            the local root node and the future father of all Leafs
	 * @return the manipulated node
	 */
	private ClusterNode pullUpAllLeafs(ClusterNode root) {
		ClusterNode node = root;
		ConcurrentLinkedDeque<ClusterNode> nodes = new ConcurrentLinkedDeque<ClusterNode>();
		nodes.addAll(node.getClusterChildren());
		ClusterNode currentNode;
		ClusterNode currentParentNode;
		while (!nodes.isEmpty()) {
			currentNode = nodes.poll();
			currentParentNode = currentNode.getParent();
			// All Leafs found leafs under this root will be added to the root
			if (currentNode instanceof ClusterLeaf) {
				currentNode.setParent(node);
				if (currentParentNode != null) {
					currentParentNode.removeClusterChild(currentNode);
				}
				node.addClusterChild(currentNode);
			} else if (currentNode.getClusterChildren() != null) {
				nodes.addAll(currentNode.getClusterChildren());
			}
		}
		return node;
	}

	/**
	 * Adds all leafs under the given root directly under the root node.
	 * 
	 * @param root
	 *            the root node
	 * @return an aggregated root node
	 */
	private ClusterNode aggregateAllInstances(ClusterNode root) {
		ClusterNode node = root;
		ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
		ConcurrentLinkedDeque<ClusterNode> nodes = new ConcurrentLinkedDeque<ClusterNode>();
		ArrayList<Instance> currentInstancesList = new ArrayList<Instance>();
		nodes.addAll(node.getClusterChildren());
		ClusterNode currentNode;
		ClusterNode parent = node.getParent();
		while (!nodes.isEmpty()) {
			currentNode = nodes.poll();
			// All leafs found under this root will be added to the root
			if (currentNode instanceof ClusterLeaf) {
				attributeList = new ArrayList<Attribute>(
						Collections.list(((ClusterLeaf) currentNode).getInstances().enumerateAttributes()));
				currentInstancesList.addAll(((ClusterLeaf) currentNode).getInstances());
			} else if (currentNode.getClusterChildren() != null) {
				nodes.addAll(currentNode.getClusterChildren());
			}
		}
		Instances instances = new Instances("Performance problems", attributeList, currentInstancesList.size());
		instances.addAll(currentInstancesList);
		// Delete node from parent
		if (parent != null) {
			parent.removeClusterChild(node);
			// Add new ClusterChild with all accumulated ClusterChilds
			node = new ClusterLeaf(instances, parent);

			parent.addClusterChild(node);
		}
		return node;
	}

	/**
	 * Converts the result tree to a list of clusters based on a given level.
	 * The tree will be cut by the level.
	 * 
	 * @param root
	 *            the root node of the cluster result
	 * @param level
	 *            the level, until all leafs are pulled up
	 * @return list of clusters
	 */
	private ArrayList<Instances> convertToClusterList(ClusterNode root, int level) {
		// Ensures, that the given tree is really cut.
		ClusterNode node = cutTreeByLevel(root, level);
		ConcurrentLinkedDeque<ClusterNode> nodes = new ConcurrentLinkedDeque<ClusterNode>();
		ArrayList<Instances> instancesList = new ArrayList<Instances>();
		nodes.addAll(node.getClusterChildren());
		ClusterNode currentNode;
		while (!nodes.isEmpty()) {
			currentNode = nodes.poll();
			// All leafs found under this root will be added to the root
			if (currentNode instanceof ClusterLeaf) {
				instancesList.add(((ClusterLeaf) currentNode).getInstances());
			} else if (currentNode.getClusterChildren() != null) {
				nodes.addAll(currentNode.getClusterChildren());
			}
		}
		return instancesList;
	}

	/**
	 * Creates a clusterList, based on the given inputs.
	 * 
	 * @param instances
	 *            the instances which will be clustered
	 * @param weights
	 *            the weights of the attribute
	 * @param level
	 *            the level, until the tree will be cut
	 * @return the clusterList
	 */
	public ArrayList<Instances> createClusterList(Instances instances, Double[] weights, int level) {
		String result = cluster(instances, weights);
		// TODO: Debug log result
		WekaClusterParser parser = new WekaClusterParser(result, instances,
				Collections.list(instances.enumerateAttributes()));
		ClusterNode resultNode = parser.parseCluster(null, Collections.list(instances.enumerateAttributes()));
		return convertToClusterList(resultNode, level);

	}

	/**
	 * Creates ClusterResult object.
	 * 
	 * @param instances
	 *            the instances which will be clustered
	 * @param weights
	 *            the weights of the attribute
	 * @param level
	 *            the level, until the tree will be cut
	 * @return {@link ClusterResult}
	 */
	public ClusterResult createClusterResult(Instances instances, Double[] weights, int level) {
		return new ClusterResult(createClusterList(instances, weights, level), weights);
	}


	/**
	 * Main Method for test cases.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {
		ClusterEngine clustering = new ClusterEngine();
		Instances instances = InstancesProvider.createRandomInstances(100);
		clustering.createClusterResult(instances, new Double[] { 1., 1., 1., 1., 1., 1. }, 5).print();

	}
}
