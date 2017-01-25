package rocks.inspectit.server.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import rocks.inspectit.server.dao.TimerDataDao;
import rocks.inspectit.shared.all.communication.data.TimerData;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.cs.indexing.AbstractBranch;
import rocks.inspectit.shared.cs.indexing.aggregation.Aggregators;
import rocks.inspectit.shared.cs.indexing.buffer.IBufferTreeComponent;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.TimerDataQueryFactory;

/**
 * Implementation of {@link TimerData} that searches for timer data in buffer.
 * <br>The query-Method of {@link AbstractBranch} without fork&join is executed, because much timer-data is expected and 
 * querying with fork&join will be faster.<br>
 * 
 * @author Ivan Senic
 * 
 */
@Repository
public class BufferTimerDataDaoImpl extends DefaultBufferDataDao<TimerData> implements TimerDataDao {
	@Autowired
	public BufferTimerDataDaoImpl(@Qualifier("indexingTree") IBufferTreeComponent<TimerData> indexingTree,
			@Qualifier("indexingTreeForkJoinPool") ForkJoinPool forkJoinPool) {
		super(indexingTree, forkJoinPool);
	}
	/**
	 * Index query factory.
	 */
	@Autowired
	private TimerDataQueryFactory<IIndexQuery> timerDataQueryFactory;

	/**
	 * {@inheritDoc}
	 */
	public List<TimerData> getAggregatedTimerData(TimerData timerData) {
		return this.getAggregatedTimerData(timerData, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<TimerData> getAggregatedTimerData(TimerData timerData, Date fromDate, Date toDate) {
		IIndexQuery query = timerDataQueryFactory.getAggregatedTimerDataQuery(timerData, fromDate, toDate);
		return super.executeQuery(query, Aggregators.TIMER_DATA_AGGREGATOR, true);
	}
}
