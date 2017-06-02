package rocks.inspectit.server.diagnosis.service.rules.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import rocks.inspectit.server.diagnosis.engine.rule.annotation.Action;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.Condition;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.Rule;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.TagValue;
import rocks.inspectit.server.diagnosis.service.aggregation.AggregatedDiagnosisInvocationData;
import rocks.inspectit.server.diagnosis.service.aggregation.DiagnosisInvocationAggregator;
import rocks.inspectit.server.diagnosis.service.data.CauseCluster;
import rocks.inspectit.server.diagnosis.service.rules.RuleConstants;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.cs.communication.data.InvocationSequenceDataHelper;
import rocks.inspectit.shared.cs.indexing.aggregation.impl.AggregationPerformer;


/**
 * Rule for detecting <code>Root Causes</code> related to N+1 problems within an
 * {@link InvocationSequenceData}. One <code>Root Cause</code> is a method that characterizes a
 * performance problem, hence, whose exclusive time is very high. The <code>Root Causes</code> are
 * aggregated to an object of type {@link AggregatedDiagnosisInvocationData}. This rule is triggered
 * fourth in the rule pipeline.
 *
 * @author Alper Hidiroglu, Christian Voegele
 *
 */
@Rule(name = "ProblemCauseRuleNPlusOne")
public class ProblemCauseRuleNPlusOneDB {

	/**
	 * A <code>Root Cause</code> candidate is put into a <code>Root Cause</code> object, if the
	 * cumulative exclusive time of already found <code>Root Causes</code> is lower than 80 percent
	 * of the <code>Problem Context's</code> duration.
	 */
	private static final Double PROPORTION = 0.8;

	/**
	 * Injection of a <code>CauseCluster</code>. The common context of this cluster is the
	 * <code>Problem Context</code>.
	 */
	@TagValue(type = RuleConstants.DIAGNOSIS_TAG_PROBLEM_CONTEXT)
	private CauseCluster problemContext;

	/**
	 * Defines the minimum number of calls to one method. If one method is called more often it is
	 * considered to be a <code>N+1 Problem</code>.
	 */
	private static final int MIN_NUMBER_OF_CALLS_TO_SAME_METHOD = 10;

	/**
	 * In case the problemContext is one DataBaseCall we check if this is an N+1 Problem.
	 *
	 * @return In case the problemContext is one DataBaseCall
	 */
	@Condition(name = "isDatabaseCall", hint = "expensiveCall is a database call")
	public boolean isDatabaseCall() {
		return (problemContext.getCauseInvocations().size() == 1) && InvocationSequenceDataHelper.hasSQLData(problemContext.getCauseInvocations().get(0));
	}

	/**
	 * Rule execution.
	 *
	 * @return DIAGNOSIS_TAG_PROBLEM_CAUSE_NPLUSONE
	 */
	@Action(resultTag = RuleConstants.DIAGNOSIS_TAG_PROBLEM_CAUSE_NPLUSONE)
	public AggregatedDiagnosisInvocationData action() {

		/**
		 * <code>Root Cause</code> candidates are the cause invocations of the
		 * <code>Problem Context</code>.
		 */
		List<InvocationSequenceData> causeCandidates = getSqlStatementCalls();

		double sumExclusiveTime = 0.0;
		int i = 0;
		DiagnosisInvocationAggregator aggregator = new DiagnosisInvocationAggregator();
		AggregatedDiagnosisInvocationData rootCause = null;

		// Root Cause candidates are put into one Root Cause as long as the condition is true.
		while ((sumExclusiveTime < (PROPORTION * InvocationSequenceDataHelper.calculateDuration(problemContext.getCommonContext())))
				&& (i < causeCandidates.size())) {
			InvocationSequenceData invocation = causeCandidates.get(i);
			if (null == rootCause) {
				rootCause = (AggregatedDiagnosisInvocationData) aggregator.getClone(invocation);
			}
			aggregator.aggregate(rootCause, invocation);
			sumExclusiveTime += InvocationSequenceDataHelper.calculateExclusiveTime(invocation);
			i++;
		}

		// If there are Root Cause candidates left that were not considered for the Root Cause
		// before, the Three-Sigma Limit approach checks if these candidates can also be considered
		// for the Root Cause.
		if ((i > 1) && (i < causeCandidates.size())) {
			double mean = sumExclusiveTime / i;
			double[] durations = new double[rootCause.size()];
			int j = 0;
			for (InvocationSequenceData invocation : rootCause.getRawInvocationsSequenceElements()) {
				durations[j] = InvocationSequenceDataHelper.calculateExclusiveTime(invocation);
				j++;
			}

			StandardDeviation standardDeviation = new StandardDeviation(false);
			double sd = standardDeviation.evaluate(durations, mean);
			double lowerThreshold = mean - (3 * sd);

			for (int k = i; k < causeCandidates.size(); k++) {
				InvocationSequenceData invocation = causeCandidates.get(k);
				double duration = InvocationSequenceDataHelper.calculateExclusiveTime(invocation);
				if (duration > lowerThreshold) {
					aggregator.aggregate(rootCause, invocation);
				} else {
					break;
				}
			}
		}

		return rootCause;
	}

	/**
	 * Return all SqlStatement calls that are executed more often than
	 * MIN_NUMBER_OF_CALLS_TO_SAME_METHOD on the same level than the one long database call. When
	 * there are none of these methods just return the long database call.
	 *
	 * @return list of InvocationSequenceData that are analyzed to be problem causes.
	 */
	private List<InvocationSequenceData> getSqlStatementCalls() {
		List<InvocationSequenceData> sqlStatementCalls = new ArrayList<InvocationSequenceData>();

		AggregationPerformer<InvocationSequenceData> aggregationPerformer = new AggregationPerformer<InvocationSequenceData>(new DiagnosisInvocationAggregator());
		aggregationPerformer.processCollection(problemContext.getCommonContext().getNestedSequences());
		List<InvocationSequenceData> invocationSequenceDataList = aggregationPerformer.getResultList();

		for (InvocationSequenceData invocationSequenceData : invocationSequenceDataList) {
			AggregatedDiagnosisInvocationData aggInvocSeqData = (AggregatedDiagnosisInvocationData) invocationSequenceData;
			if (InvocationSequenceDataHelper.hasSQLData(aggInvocSeqData) && (aggInvocSeqData.getRawInvocationsSequenceElements().size() > MIN_NUMBER_OF_CALLS_TO_SAME_METHOD)) {
				sqlStatementCalls.addAll(aggInvocSeqData.getRawInvocationsSequenceElements());
			}
		}

		if (!sqlStatementCalls.isEmpty()) {
			sqlStatementCalls.add(problemContext.getCauseInvocations().get(0));
			return sqlStatementCalls;
		} else {
			return problemContext.getCauseInvocations();
		}
	}

}
