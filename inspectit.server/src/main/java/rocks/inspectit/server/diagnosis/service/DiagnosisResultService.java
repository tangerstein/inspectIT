package rocks.inspectit.server.diagnosis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterEngine;
import rocks.inspectit.server.diagnosis.service.results.ProblemOccurrence;
import rocks.inspectit.server.property.PropertyManager;
import rocks.inspectit.shared.all.spring.logger.Log;
import rocks.inspectit.shared.cs.cmr.service.IInvocationDataAccessService;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Alexander Wert Tobias Angerstein
 *
 */
@Component
public class DiagnosisResultService implements IDiagnosisResultNotificationService {
	@Autowired
	PropertyManager propertyManager;
	/**
	 * List of instances.
	 */
	private ArrayList<Object[]> instancesList = new ArrayList<Object[]>();
	/**
	 * Set of all occuring nodetypes.
	 */
	private HashSet<String> nodeTypes = new HashSet<String>();
	/**
	 * Set of all occuring rootCauses.
	 */
	private HashSet<String> rootCauses = new HashSet<String>();
	/**
	 * Set of all occuring problemContexts.
	 */
	private HashSet<String> problemContexts = new HashSet<String>();
	/**
	 * Set of all occuring entryPoints.
	 */
	private HashSet<String> entryPoints = new HashSet<String>();
	/**
	 * Set of all occuring globalContexts.
	 */
	private HashSet<String> globalContexts = new HashSet<String>();
	/**
	 * All instances in the WEKA format.
	 */
	private Instances instances;

	/**
	 * The logger of this class.
	 */
	@Log
	Logger log;
	/**
	 * Access to the invocation tree.
	 */
	@Autowired
	IInvocationDataAccessService accessService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void onNewDiagnosisResult(Collection<ProblemOccurrence> problemOccurrences) {
		Properties properties = System.getProperties();
		int clusterThreshold = Integer.parseInt(properties.getProperty("clusteringThreshold", "100"));
		for (ProblemOccurrence po : problemOccurrences) {
			// Root invocation sequence ID
			long rootId = po.getRequestRoot().getInvocationId();
			// The problem invocation sequence ID
			long problemId = po.getProblemContext().getInvocationId();

			Double exclusiveDuration = po.getResponseTime();
			InvocationSequenceData invocationRootNode = null;
			while (invocationRootNode == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				invocationRootNode = accessService.getInvocationSequenceDetail(template);
			}

				InvocationSequenceData invocationNode = null;
				ConcurrentLinkedQueue<InvocationSequenceData> invocationQueue = new ConcurrentLinkedQueue<InvocationSequenceData>();
				invocationQueue.add(invocationRootNode);
				while (!invocationQueue.isEmpty()) {
					InvocationSequenceData node = invocationQueue.poll();
					if (node.getId() == problemId) {
						invocationNode = node;
						log.info("Invocation found!! -- Party :)");
						break;
					} else {
						invocationQueue.addAll(node.getNestedSequences());
					}
				}
				if (invocationNode == null) {
					log.warn("InvocationSequence not found!");
				}
				Double exclusiveDuration = invocationNode.getDuration();

			String rootCause = po.getRootCause().getMethodIdent() + "";
			rootCauses.add(rootCause);

			String nodeType = po.getCauseStructure().getCauseType().name();
			nodeTypes.add(nodeType);

			String problemContext = po.getProblemContext().getMethodIdent() + "";
			problemContexts.add(problemContext);

			String entryPoint = po.getRequestRoot().getMethodIdent() + "";
			entryPoints.add(entryPoint);

			String globalContext = po.getGlobalContext().getMethodIdent() + "";
			globalContexts.add(globalContext);

			instancesList.add(
					new Object[] { rootCause, problemContext, entryPoint, globalContext, nodeType, exclusiveDuration });

		if (instancesList.size() >= clusterThreshold) {
			ArrayList<Object[]> instancesListCopy = new ArrayList<Object[]>(instancesList);
			triggerClustering(instancesListCopy);
			instancesList.clear();
		}

	}

	/**
	 * Starts the cluster engine in a new thread and converts the stored.
	 * instances into the WEKA- instances objects
	 * 
	 * @param instancesList
	 *            list of stored instances
	 */
	private void triggerClustering(ArrayList<Object[]> instancesList) {
		Attribute nodeTypeAttribute = new Attribute("nodeType", new ArrayList<String>(nodeTypes));
		Attribute rootCauseAttribute = new Attribute("rootCause", new ArrayList<String>(rootCauses));
		Attribute problemContextAttribute = new Attribute("problemContext", new ArrayList<String>(problemContexts));
		Attribute entryPointAttribute = new Attribute("entryPoint", new ArrayList<String>(entryPoints));
		Attribute globalContextAttribute = new Attribute("globalContext", new ArrayList<String>(globalContexts));
		Attribute exclusiveDurationAttribute = new Attribute("exclusiveDuration");

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		// Declare the feature ArrayList
		attributes.add(rootCauseAttribute);
		attributes.add(problemContextAttribute);
		attributes.add(entryPointAttribute);
		attributes.add(globalContextAttribute);
		attributes.add(nodeTypeAttribute);
		attributes.add(exclusiveDurationAttribute);
		instances = new Instances("problemInstances", attributes, instancesList.size());
		if (instancesList.isEmpty()) {
			return;
		}
		for (Object[] instanceData : instancesList) {
			Instance instance = new DenseInstance(6);
			instance.setValue(attributes.get(0), (String) instanceData[0]);
			instance.setValue(attributes.get(1), (String) instanceData[1]);
			instance.setValue(attributes.get(2), (String) instanceData[2]);
			instance.setValue(attributes.get(3), (String) instanceData[3]);
			instance.setValue(attributes.get(4), (String) instanceData[4]);
			instance.setValue(attributes.get(5), (Double) instanceData[5]);
			instances.add(instance);
		}
		Properties properties = System.getProperties();
		String clusteringType = properties.getProperty("clusteringType", "k");
		if (clusteringType.equals("k")) {
			// ClusterEngine starts in new Thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					log.info("------k-Means with k-estimation:------");
					ClusterEngine cEngine = new ClusterEngine();
					log.info("Menge der übergebenen Instanzen: " + DiagnosisResultService.this.instances.size());
				cEngine.clusterOptimizedKMeans(instances, new Double[] { 1., 1., 1., 1., 1., 1. }).print();
				}
			}).start();
		} else if (clusteringType.equals("h")) {
			// ClusterEngine starts in new Thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					log.info("------Hierarchical Clustering:------");
					ClusterEngine cEngine = new ClusterEngine();
					log.info("Menge der übergebenen Instanzen: " + DiagnosisResultService.this.instances.size());
					cEngine.createClusterResult(instances, new Double[] { 1., 1., 1., 1., 1., 1. },
							Integer.parseInt(properties.getProperty("level", "4"))).print();
					;
				}
			}).start();
		} else {
			log.warn("You have typed in a wrong clusteringType");
		}
	}

	@Override
	public void onNewDiagnosisResult(ProblemOccurrence problemOccurrence) {
		// TODO Auto-generated method stub

	}

}
