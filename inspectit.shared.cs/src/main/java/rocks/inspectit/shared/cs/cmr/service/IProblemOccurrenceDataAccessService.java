package rocks.inspectit.shared.cs.cmr.service;

import java.util.Collection;
import java.util.Date;

import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.cmr.service.ServiceExporterType;
import rocks.inspectit.shared.all.cmr.service.ServiceInterface;
import rocks.inspectit.shared.all.communication.data.cmr.ApplicationData;
import rocks.inspectit.shared.all.communication.data.cmr.BusinessTransactionData;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;

/**
 * Service interface which defines the methods to retrieve data objects based on
 * the problem occurrence recordings.
 * 
 * @author Tobias Angerstein
 *
 */
@ServiceInterface(exporter = ServiceExporterType.HTTP)
public interface IProblemOccurrenceDataAccessService {
	/**
	 * Returns a list of {@link ProblemOccurrence} which meet the given
	 * attributes.
	 * 
	 * @param agentId
	 *            agent id
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            end date
	 * @param globalContextInvocationId
	 *            global context invocation id
	 * @param problemContextInvocationId
	 *            problem context invocation id
	 * @param requestRootInvocationId
	 *            request root invocation id
	 * @param rootCauseInvocationId
	 *            root cause invocation id
	 * @param causeType
	 *            cause tyoe
	 * @param applicationNameIdent
	 *            id of {@link ApplicationData}
	 * @param businessTransactionNameIdent
	 *            id of {@link BusinessTransactionData}
	 * @return collection of problem occurrences
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	Collection<ProblemOccurrence> getProblemOccurrencesBasedOnInvocationIds(long agentId, Date fromDate, Date toDate,
			long globalContextInvocationId, long problemContextInvocationId, long requestRootInvocationId,
			long rootCauseInvocationId, CauseType causeType, int applicationNameIdent,
			int businessTransactionNameIdent);

	/**
	 * Returns a list of {@link ProblemOccurrence} which meet the given
	 * attributes.
	 * 
	 * @param agentId
	 *            agent id
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            end date
	 * @param globalContextMethodName
	 *            method name of global context (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param problemContextMethodName
	 *            method name of problem context (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param requestRootMethodName
	 *            method name of request root (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param rootCauseMethodName
	 *            method name of root cause (uses {@link MethodIdent#toString()
	 *            formatting})
	 * @param causeType
	 *            cause type
	 * @param applicationName
	 *            name of {@link ApplicationData}
	 * @param businessTransactionName
	 *            name of {@link BusinessTransactionData}
	 * @return collection of problem occurrences
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	Collection<ProblemOccurrence> getProblemOccurrencesBasedOnMethodNames(Long agentId, Date fromDate, Date toDate,
			String globalContextMethodName, String problemContextMethodName, String requestRootMethodName,
			String rootCauseMethodName, CauseType causeType, String applicationName, String businessTransactionName);
}
