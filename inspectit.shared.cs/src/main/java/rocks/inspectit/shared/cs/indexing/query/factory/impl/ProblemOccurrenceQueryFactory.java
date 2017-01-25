package rocks.inspectit.shared.cs.indexing.query.factory.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.cs.indexing.impl.ProblemOccurrenceIndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.AbstractQueryFactory;
import rocks.inspectit.shared.cs.indexing.query.provider.impl.ProblemOccurrenceIndexQueryProvider;

@Component
public class ProblemOccurrenceQueryFactory<E extends ProblemOccurrenceIndexQuery>
		extends AbstractQueryFactory<ProblemOccurrenceIndexQuery> {

	@Autowired
	public ProblemOccurrenceQueryFactory(
			@Qualifier("problemOccurrenceIndexQueryProvider") ProblemOccurrenceIndexQueryProvider indexQueryProvider) {
		super.setIndexQueryProvider(indexQueryProvider);
	}
	/**
	 * Returns a Query
	 * 
	 * @param platformId
	 * @param methodId
	 * @param limit
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public ProblemOccurrenceIndexQuery getProblemOccurrenceOverview(long platformId, Date fromDate, Date toDate,
			long globalContextId,
			long problemContextId, long requestRootId, long rootCauseId) {
		ProblemOccurrenceIndexQuery query = getIndexQueryProvider().getIndexQuery();
		query.setPlatformIdent(platformId);
		query.setRequestRootID(requestRootId);
		query.setGlobalContextId(globalContextId);
		query.setProblemContextId(problemContextId);
		query.setRootCauseId(rootCauseId);

		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ProblemOccurrence.class);
		query.setObjectClasses(searchedClasses);
		if (fromDate != null) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (toDate != null) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

}
