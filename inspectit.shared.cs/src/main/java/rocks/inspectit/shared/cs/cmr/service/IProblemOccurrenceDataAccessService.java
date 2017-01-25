package rocks.inspectit.shared.cs.cmr.service;

import java.util.Date;
import java.util.List;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;

public interface IProblemOccurrenceDataAccessService {
	/**
	 * This class provides the fitting ProblemOccurrences based on the given
	 * arguments.
	 * 
	 * @param platformId
	 * @param fromDate
	 * @param toDate
	 * @param globalContextId
	 * @param problemContextId
	 * @param requestRootId
	 * @param rootCauseId
	 * @return
	 */
	List<ProblemOccurrence> getProblemOccurrenceOverview(long platformId, Date fromDate, Date toDate,
			long globalContextId, long problemContextId, long requestRootId, long rootCauseId);
}
