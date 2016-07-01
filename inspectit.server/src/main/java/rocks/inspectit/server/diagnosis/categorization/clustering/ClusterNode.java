package rocks.inspectit.server.diagnosis.categorization.clustering;

import java.util.ArrayList;

/**
 * Represents a node of the ClusterResult.
 * 
 * @author Tobias Angerstein
 *
 */
public class ClusterNode {
	/**
	 * children of this node.
	 */
	private ArrayList<ClusterNode> clusterChildren = new ArrayList<ClusterNode>();

	/**
	 * the parent of this node; is null when current node is root.
	 */
	private ClusterNode parent;
	/**
	 * The level of the current node relative to the root of the tree.
	 */
	private int level;

	/**
	 * get parent.
	 * 
	 * @return the parent node
	 */
	public ClusterNode getParent() {
		return parent;
	}

	/**
	 * Constructor.
	 * 
	 * @param clusterChildren
	 *            all cluster children
	 * @param parent
	 *            the parent node
	 */
	public ClusterNode(ArrayList<ClusterNode> clusterChildren, ClusterNode parent) {
		this.clusterChildren = clusterChildren;
		this.parent = parent;
		setLevel();
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent node
	 */
	public ClusterNode(ClusterNode parent) {
		this.parent = parent;
		setLevel();
	}

	/**
	 * adds one child to the childList.
	 * 
	 * @param node
	 *            the child
	 */
	public void addClusterChild(ClusterNode node) {
		clusterChildren.add(node);
		node.setParent(this);

	}

	/**
	 * set whole childList.
	 * 
	 * @param clusterChildren
	 *            the clusterChildren
	 */
	public void setClusterChildren(ArrayList<ClusterNode> clusterChildren) {
		this.clusterChildren = clusterChildren;
	}

	/**
	 * removes a child.
	 * 
	 * @param node
	 *            the child, which will be removed
	 */
	public void removeClusterChild(ClusterNode node) {
		clusterChildren.remove(node);
	}

	/**
	 * get.
	 * 
	 * @param index
	 *            the index of the clusterChild
	 * @return the childNode at the given index
	 */
	public ClusterNode getClusterChild(int index) {
		return clusterChildren.get(index);
	}

	/**
	 * get.
	 * 
	 * @return whole childList
	 */
	public ArrayList<ClusterNode> getClusterChildren() {
		return clusterChildren;
	}

	/**
	 * get level.
	 * 
	 * @return level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Set the level of the current node depending on the level of the parent.
	 */
	private void setLevel() {
		if (parent == null) {
			this.level = 0;
		} else {
			this.level = parent.level + 1;
		}
	}

	/**
	 * set.
	 * 
	 * @param parent
	 *            the parent of the current node
	 */
	public void setParent(ClusterNode parent) {
		this.parent = parent;
	}

}
