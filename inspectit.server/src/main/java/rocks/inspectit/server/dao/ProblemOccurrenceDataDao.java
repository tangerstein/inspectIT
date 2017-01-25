package rocks.inspectit.server.dao;

import java.util.Date;
import java.util.List;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;

public interface ProblemOccurrenceDataDao {
	List<ProblemOccurrence> getProblemOccurrenceOverview(long platformId, Date fromDate, Date toDate,
			long globalContextId, long problemContextId, long requestRootId, long rootCauseId);

}
