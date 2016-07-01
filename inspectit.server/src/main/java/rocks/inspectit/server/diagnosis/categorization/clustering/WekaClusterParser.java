package rocks.inspectit.server.diagnosis.categorization.clustering;

import java.text.StringCharacterIterator;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class transfers the String result of WEKA into a result tree.
 * 
 * @author Tobias Angerstein
 *
 */
public class WekaClusterParser {

	/**
	 * Iterates over the WEKA result String.
	 */
	private static StringCharacterIterator iterator;

	/**
	 * Global instances are needed for the recursive approach.
	 */
	private static Instances globalInstances;

	/**
	 * Sets the String result.
	 * 
	 * @param clusterResultString
	 *            WEKA cluster result
	 * @param instances
	 *            all unsorted instances
	 */
	private static void setStringresult(String clusterResultString, Instances instances) {
		// removes all unused information from the String
		clusterResultString = clusterResultString.replaceAll(":[^,\\)]*[,]", ",").replaceAll(":[^,\\)]*[\\)]", ")")
				.replaceAll("Cluster 0\n", "").replaceAll("\n", "");
		iterator = new StringCharacterIterator(clusterResultString);
		globalInstances = instances;
	}

	/**
	 * Parse Cluster.
	 * 
	 * @param clusterResultString
	 *            the WEKA cluster result
	 * @param instances
	 *            the unsorted instances (needed, to map the id in the WEKA result to an instance
	 *            object)
	 * @param attributeList
	 *            list of used attributes
	 * @return the root of the cluster tree
	 */
	public static ClusterNode parseCluster(String clusterResultString, Instances instances,
			ArrayList<Attribute> attributeList) {
		setStringresult(clusterResultString, instances);
		return parseCluster(null, attributeList);

	}

	/**
	 * Creates a ClusterNode from the WEKA Cluster result.
	 * 
	 * @param parent
	 *            the parent of the root node (should be null)
	 * @param attributeList
	 *            a list of all attributes
	 * @return a Cluster Node
	 */
	private static ClusterNode parseCluster(ClusterNode parent, ArrayList<Attribute> attributeList) {
		String currentID = "";
		ClusterNode newNode = new ClusterNode(parent);
		ArrayList<ClusterNode> clusterChildren = new ArrayList<ClusterNode>();
		ArrayList<Instance> currentInstancesList = new ArrayList<Instance>();
		while (iterator.current() != ')' && iterator.current() != StringCharacterIterator.DONE) {
			if (iterator.current() == '(') {
				iterator.next();
				clusterChildren.add(parseCluster(newNode, attributeList));
				if (iterator.next() != ',') {
					iterator.previous();
				}
				iterator.next();
				continue;
			} else if (Character.isDigit(iterator.current()) || iterator.current() == '.') {
				currentID += iterator.current();
			}
			char current = iterator.current();
			char next = iterator.next();
			if (current == ',' | next == ')') {
				currentInstancesList.add(globalInstances.get(((int) Double.parseDouble(currentID)) - 1));
				currentID = "";
			}
		}

		Instances instances = new Instances("Performance problems", attributeList,
				currentInstancesList.size());
		instances.addAll(currentInstancesList);
		if (clusterChildren.size() == 0) {
			return new ClusterLeaf(instances, parent);
		} else {
			if (currentInstancesList.size() != 0) {
				clusterChildren.add(new ClusterLeaf(instances, newNode));
			}
			newNode.setClusterChildren(clusterChildren);
			return newNode;
		}
	}
}
