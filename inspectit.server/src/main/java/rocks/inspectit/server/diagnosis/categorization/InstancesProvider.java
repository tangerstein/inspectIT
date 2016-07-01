package rocks.inspectit.server.diagnosis.categorization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import rocks.inspectit.server.diagnosis.categorization.clustering.ClusterEngine;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Provides random generated data for testing and evaluation.
 * 
 * @author Tobias Angerstein
 *
 */
public class InstancesProvider {
	/**
	 * Amount of static instances.
	 */
	private static final int NUMBER_OF_STATIC_INSTANCES = 30;

	/**
	 * Provides a default attribute list.
	 * 
	 * @return a list of predefined attributes
	 */
	public static ArrayList<Attribute> getAttributeList() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		// convert the data to "Instances" instances

		Attribute businessTransaction = new Attribute("BusinessTransaction",
				Arrays.asList("Buy", "Search", "changeID", "changeAvatar", "addUser", "deleteUser"));

		Attribute entryPoint = new Attribute("EntryPoint",
				Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
		Attribute problemContext = new Attribute("ProblemContext",
				Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
		Attribute cause = new Attribute("Cause", Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
		Attribute nodeType = new Attribute("NodeType", Arrays.asList("recursive", "iterative"));
		Attribute exclusiveTimeSum = new Attribute("ExclusiveTimeSum");

		// Declare the feature ArrayList
		attributes.add(businessTransaction);
		attributes.add(entryPoint);
		attributes.add(problemContext);
		attributes.add(cause);
		attributes.add(nodeType);
		attributes.add(exclusiveTimeSum);

		return attributes;
	}

	/**
	 * Provides a set of random generated instances.
	 * 
	 * @param numberOfProblems
	 *            the number of instances
	 * @return instances list
	 */
	public static Instances createRandomInstances(int numberOfProblems) {
		Random random = new Random();
		ArrayList<String> businesstransactions = new ArrayList<String>(
				Arrays.asList("Buy", "Search", "changeID", "changeAvatar", "addUser", "deleteUser"));
		ArrayList<String> nodeTypes = new ArrayList<String>(Arrays.asList("recursive", "iterative"));
		ArrayList<String> causes = new ArrayList<String>(
				Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
		ArrayList<String> problemContexts = new ArrayList<String>(
				Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
		ArrayList<String> entryPoints = new ArrayList<String>(
				Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));

		// Create random Data
		ArrayList<Attribute> attributes = getAttributeList();
		Instances data = new Instances("Problem Instances", attributes, numberOfProblems);
		for (int i = 0; i < numberOfProblems; i++) {
			Instance instance = new DenseInstance(6);
			instance.setValue(attributes.get(0), businesstransactions.get(random.nextInt(businesstransactions.size())));
			instance.setValue(attributes.get(1), entryPoints.get(random.nextInt(entryPoints.size())));
			instance.setValue(attributes.get(2), problemContexts.get(random.nextInt(problemContexts.size())));
			instance.setValue(attributes.get(3), causes.get(random.nextInt(causes.size())));
			instance.setValue(attributes.get(4), nodeTypes.get(random.nextInt(nodeTypes.size())));
			instance.setValue(attributes.get(5), random.nextInt(20));
			data.add(instance);
		}
		return data;
	}

	/**
	 * Provides a referenceCluster.
	 * 
	 * @return list of clusters
	 */
	public static ArrayList<Instances> getReferenceCluster() {
		File objectFile = new File("clusterResult");
		ArrayList<Instances> referenceCluster = new ArrayList<Instances>();
		if (objectFile.exists() && !objectFile.isDirectory()) {
			FileInputStream inputStream;
			try {
				inputStream = new FileInputStream(objectFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				referenceCluster = (ArrayList<Instances>) objectInputStream.readObject();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			referenceCluster = new ClusterEngine().createClusterList(getStaticInstances(),
					new Double[] { 1., 100000000., 1., 1., 1., 1. }, 4);
			FileOutputStream outputStream;
			try {
				outputStream = new FileOutputStream(objectFile);
				ObjectOutputStream objectOutputStream;
				objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(referenceCluster);
				objectOutputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return referenceCluster;
		}
		return referenceCluster;

		
		
	}

	/**
	 * Provides a list of instances which was generated ones. Returns a list,
	 * which is stored as file.
	 * 
	 * @return instances list
	 */
	public static Instances getStaticInstances() {
		Instances instances = null;
		File f = new File("instances.arff");
		if (f.exists() && !f.isDirectory()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader("instances.arff"));
				instances = new Instances(reader);
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			instances = createRandomInstances(NUMBER_OF_STATIC_INSTANCES);
			ArffSaver saver = new ArffSaver();
			saver.setInstances(instances);
			try {
				saver.setFile(new File("instances.arff"));
				saver.writeBatch();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instances;
	}
}
