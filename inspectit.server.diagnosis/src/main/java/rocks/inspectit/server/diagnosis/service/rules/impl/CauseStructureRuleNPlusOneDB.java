package rocks.inspectit.server.diagnosis.service.rules.impl;

import rocks.inspectit.server.diagnosis.engine.rule.annotation.Action;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.Rule;
import rocks.inspectit.server.diagnosis.engine.rule.annotation.TagValue;
import rocks.inspectit.server.diagnosis.service.aggregation.AggregatedDiagnosisInvocationData;
import rocks.inspectit.server.diagnosis.service.data.CauseCluster;
import rocks.inspectit.server.diagnosis.service.rules.RuleConstants;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType;

/**
 * This rule investigates if the <code>Root Cause</code> identified as potential N+1 problem is a
 * N+1 problem or only one database call.
 *
 * // * @author Alper Hidiroglu, Christian Voegele
 *
 */
@Rule(name = "CauseStructureRuleNPlusONeDB")
public class CauseStructureRuleNPlusOneDB {

	/**
	 * Injection of the <code>Problem Context</code>.
	 */
	@TagValue(type = RuleConstants.DIAGNOSIS_TAG_PROBLEM_CONTEXT)
	private CauseCluster problemContext;

	/**
	 * Injection of the <code>Root Causes</code>.
	 */
	@TagValue(type = RuleConstants.DIAGNOSIS_TAG_PROBLEM_CAUSE_NPLUSONE)
	private AggregatedDiagnosisInvocationData cause;

	/**
	 * Rule execution.
	 *
	 * @return DIAGNOSIS_TAG_CAUSE_STRUCTURE
	 */
	@Action(resultTag = RuleConstants.DIAGNOSIS_TAG_CAUSE_STRUCTURE)
	public CauseStructure action() {
		if (cause.getRawInvocationsSequenceElements().size() > 1) {
			return new CauseStructure(CauseType.NPLUSONE_DATABASE, 0);
		} else {
			return new CauseStructure(CauseType.SINGLE_DATABASE, 0);
		}
	}

}
