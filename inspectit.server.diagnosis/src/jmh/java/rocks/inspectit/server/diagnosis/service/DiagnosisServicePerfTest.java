package rocks.inspectit.server.diagnosis.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import rocks.inspectit.server.processor.impl.DiagnosisCmrProcessor;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 5)
@Fork(2)
@State(Scope.Thread)
public class DiagnosisServicePerfTest {
	/**
	 * Diagnosis Service under test.
	 */
	private DiagnosisService diagnosisService;

	/**
	 * Number of invocations to be analyzed.
	 */
	@Param({ "100", "1000", "10000" })
	private int invocations;

	/**
	 * Number of session workers.
	 */
	@Param({ "2", "5", "8" })
	private int numberOfSessionWorker;

	/**
	 * Timeout between a retry to fill the invocation sequences into the queue.
	 */
	@Param({ "10", "25", "50" })
	private long timeOut;

	/**
	 * Baseline
	 */
	private static final int BASELINE= 1000;

	/**
	 * Number of calls.
	 */
	@Param({ "10", "100", "1000" })
	private int numberOfCalls;
	
	/**
	 * Queue capacity.
	 */
	@Param({ "2", "10", "30" })
	private int queueCapacity;
	
	/**
	 * InvocationSequence with iterative calls 
	 */
	private InvocationSequenceData rootIterativeInvocationSequence;
	
	/**	
	 * InvocationSequence with recursive calls 
	 */
	private InvocationSequenceData rootRecursiveInvocationSequence;

	/**
	 * Prepare diagnosis engine.
	 */
	@Setup(Level.Trial)
	public void initDiagnosisEngine() {
		diagnosisService = new DiagnosisService(
				Arrays.asList(new String[] { "rocks.inspectit.server.diagnosis.service.rules.impl" }),
				numberOfSessionWorker, timeOut, queueCapacity, new DiagnosisCmrProcessor(BASELINE));

		rootIterativeInvocationSequence = getIterativeInvocationSequence(
				new InvocationSequenceData(new Timestamp(System.currentTimeMillis()), 0, 0, 0), numberOfCalls);

		rootRecursiveInvocationSequence = getRecursiveInvocationSequence(
				new InvocationSequenceData(new Timestamp(System.currentTimeMillis()), 0, 0, 1000), 0, numberOfCalls);

	}
	
	@Benchmark
	private boolean testDiagnosisServiceWithIterativeInvocationSequences(){
		return diagnosisService.diagnose(rootIterativeInvocationSequence, BASELINE);
	}
	@Benchmark
	private boolean testDiagnosisServiceWithRecursiveInvocationSequences(){
		return diagnosisService.diagnose(rootRecursiveInvocationSequence, BASELINE);
	}

	/**
	 * Creates an invocationSequence with recursive invocations.
	 * 
	 * @param invocationSequenceData
	 *            The root invocation sequence
	 * @param currentDepth
	 *            The current Depth of the invocation sequence (should be
	 *            initially set to 0)
	 * @param maxDepth
	 *            The max depth of the invocation sequence
	 * @return
	 */
	private InvocationSequenceData getRecursiveInvocationSequence(InvocationSequenceData invocationSequenceData,
			int currentDepth, int maxDepth) {
		if (currentDepth >= maxDepth) {
			return invocationSequenceData;
		} else {
			InvocationSequenceData child = new InvocationSequenceData(
					new Timestamp(System.currentTimeMillis() + (invocationSequenceData.getTimeStamp().getTime() * 100)),
					0, 0, invocationSequenceData.getMethodIdent() + 1);
			child.setParentSequence(invocationSequenceData);
			child.setDuration(100);
			invocationSequenceData.getNestedSequences().add(child);
			return getRecursiveInvocationSequence(child, currentDepth + 1, maxDepth);
		}
	}

	/**
	 * Creates an invocationSequence which has a huge number of iterative calls
	 * 
	 * @param parentSequence
	 *            The root invocation Sequence
	 * @param numberOfChildren
	 *            the number of iterative children
	 * @return the parent sequence with added children
	 */
	private InvocationSequenceData getIterativeInvocationSequence(InvocationSequenceData parentSequence,
			int numberOfChildren) {
		List<InvocationSequenceData> nestedSequences = new ArrayList<InvocationSequenceData>();

		for (int i = 0; i < numberOfChildren; i++) {
			InvocationSequenceData child = new InvocationSequenceData(
					new Timestamp(System.currentTimeMillis() + (i * 100)), 0, 0, parentSequence.getMethodIdent() + i);
			child.setParentSequence(parentSequence);
			child.setDuration(100);
			nestedSequences.add(child);
		}

		parentSequence.getNestedSequences().addAll(nestedSequences);
		return parentSequence;
	}
}
