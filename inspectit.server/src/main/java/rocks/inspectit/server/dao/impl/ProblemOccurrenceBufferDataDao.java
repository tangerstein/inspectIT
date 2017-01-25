package rocks.inspectit.server.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import rocks.inspectit.server.dao.ProblemOccurrenceDataDao;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.cs.indexing.buffer.IBufferTreeComponent;
import rocks.inspectit.shared.cs.indexing.impl.ProblemOccurrenceIndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.ProblemOccurrenceQueryFactory;

/**
 * Abstract class for all buffer data DAO service.
 * 
 * @param <E>
 *            Type of the data to be queried.
 * 
 * 
 */
@Repository
public class ProblemOccurrenceBufferDataDao extends DefaultBufferDataDao<ProblemOccurrence>
		implements ProblemOccurrenceDataDao {
	/**
	 * Index query provider.
	 */
	@Autowired
	private ProblemOccurrenceQueryFactory<ProblemOccurrenceIndexQuery> problemOccurrenceDataQueryFactory;

	@Autowired
	public ProblemOccurrenceBufferDataDao(
			@Qualifier("problemOccurrenceIndexingTree") IBufferTreeComponent<ProblemOccurrence> indexingTree,
			@Qualifier("indexingTreeForkJoinPool") ForkJoinPool forkJoinPool) {
		super(indexingTree, forkJoinPool);
	}

	@Override
	public List<ProblemOccurrence> getProblemOccurrenceOverview(long platformId, Date fromDate,
			Date toDate,
			long globalContextId, long problemContextId, long requestRootId, long rootCauseId) {
		IIndexQuery query = problemOccurrenceDataQueryFactory.getProblemOccurrenceOverview(platformId,
				fromDate, toDate, globalContextId, problemContextId, requestRootId, rootCauseId);
		List<ProblemOccurrence> resultWithChildren;

		resultWithChildren = super.executeQuery(query, false);

		return resultWithChildren;
	}
	
}
