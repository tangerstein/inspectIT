/**
 *
 */
package rocks.inspectit.server.diagnosis.service.rules.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import rocks.inspectit.server.diagnosis.engine.rule.annotation.Action;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.Rule;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.TagValue;
import rocks.inspectit.server.diagnosis.service.rules.RuleConstants;
import rocks.inspectit.shared.all.communication.data.AggregatedInvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceDataHelper;
import rocks.inspectit.shared.cs.indexing.aggregation.impl.InvocationSequenceDataAggregator;

/**
 * @author Alexander Wert
 *
 */
@Rule(name = "ProblemCauseRule")
public class ProblemCauseRule {
	private static final Double RT_RALATION_THRESHOLD = 0.8;

	@TagValue(type = RuleConstants.TAG_TIME_WASTING_OPERATIONS)
	private AggregatedInvocationSequenceData timeWastingOperation;

	@TagValue(type = RuleConstants.TAG_PROBLEM_CONTEXT)
	private InvocationSequenceData problemContext;

	@Action(resultTag = RuleConstants.TAG_PROBLEM_CAUSE)
	public AggregatedInvocationSequenceData action() {
		List<InvocationSequenceData> causeCandidates = asInvocationSequenceDataList(Collections.singletonList(problemContext), new ArrayList<InvocationSequenceData>());
		Collections.sort(causeCandidates, new Comparator<InvocationSequenceData>() {
			@Override
			public int compare(InvocationSequenceData o1, InvocationSequenceData o2) {
				return Double.compare(InvocationSequenceDataHelper.calculateExclusiveTime(o2), InvocationSequenceDataHelper.calculateExclusiveTime(o1));
			}
		});

		double sumExclusiveTime = 0.0;
		int i = 0;
		InvocationSequenceDataAggregator aggregator = new InvocationSequenceDataAggregator();
		AggregatedInvocationSequenceData rootCause = null;
		while (sumExclusiveTime < RT_RALATION_THRESHOLD * InvocationSequenceDataHelper.calculateDuration(problemContext) && i < causeCandidates.size()) {

			InvocationSequenceData invocation = causeCandidates.get(i);
			if (null == rootCause) {
				rootCause = (AggregatedInvocationSequenceData) aggregator.getClone(invocation);
			}
			aggregator.aggregate(rootCause, invocation);
			sumExclusiveTime += InvocationSequenceDataHelper.calculateDuration(invocation);
			i++;
		}
		if (i > 1) {
			double mean = sumExclusiveTime / i;
			double[] durations = new double[rootCause.size()];
			int j = 0;
			for (InvocationSequenceData invocation : rootCause.getRawInvocationsSequenceElements()) {
				durations[j] = InvocationSequenceDataHelper.calculateDuration(invocation);
				j++;
			}

			StandardDeviation standardDeviation = new StandardDeviation(false);
			double sd = standardDeviation.evaluate(durations, mean);
			double lowerThreshold = mean - 3 * sd;

			for (int k = i; k < causeCandidates.size(); k++) {
				InvocationSequenceData invocation = causeCandidates.get(k);
				double duration = InvocationSequenceDataHelper.calculateDuration(invocation);
				if (duration > lowerThreshold) {
					aggregator.aggregate(rootCause, invocation);
				} else {
					break;
				}
			}
		}

		return rootCause;
	}

	private List<InvocationSequenceData> asInvocationSequenceDataList(List<InvocationSequenceData> invocationSequences, final List<InvocationSequenceData> resultList) {
		for (InvocationSequenceData invocationSequence : invocationSequences) {
			if (invocationSequence.getMethodIdent() == timeWastingOperation.getMethodIdent() && InvocationSequenceDataHelper.calculateExclusiveTime(invocationSequence) > 0.0) {
				resultList.add(invocationSequence);
			}
			asInvocationSequenceDataList(invocationSequence.getNestedSequences(), resultList);
		}

		return resultList;
	}

}
