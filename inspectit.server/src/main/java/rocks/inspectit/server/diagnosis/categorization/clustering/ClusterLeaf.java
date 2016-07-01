package rocks.inspectit.server.diagnosis.categorization.clustering;

import weka.core.Instances;

/**
 * Represents one leaf of a cluster solution; holds the instances of the solution.
 * 
 * @author Tobias Angerstein
 *
 */
public class ClusterLeaf extends ClusterNode {
	/**
	 * The Instances of the cluster.
	 */
	private Instances instances;

	/**
	 * Constructor.
	 * 
	 * @param instances
	 *            the instances which belong to this leaf
	 * @param parent
	 *            the parentNode
	 */
	public ClusterLeaf(Instances instances, ClusterNode parent) {
		super(null, parent);
		this.instances = instances;
	}

	/**
	 * get instances.
	 * 
	 * @return allInstances
	 */
	public Instances getInstances() {
		return instances;
	}

	/***
	 * set instances.
	 * 
	 * @param instances
	 *            set the instances of this leaf
	 */
	public void setInstances(Instances instances) {
		this.instances = instances;
	}

}
