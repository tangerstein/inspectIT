package rocks.inspectit.shared.cs.indexing.query.factory.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.AbstractQueryFactory;
import rocks.inspectit.shared.cs.indexing.query.provider.IIndexQueryProvider;
import rocks.inspectit.shared.cs.indexing.restriction.impl.IndexQueryRestrictionFactory;

/**
 * Factory for all queries for the {@link InvocationSequenceData}.
 * 
 * @author Ivan Senic
 * 
 * @param <E>
 */
@Component
public class InvocationSequenceDataQueryFactory<E extends IIndexQuery> extends AbstractQueryFactory<E> {
	@Autowired
	public InvocationSequenceDataQueryFactory(
			@Qualifier("indexQueryProvider") IIndexQueryProvider<E> indexQueryProvider) {
		super.setIndexQueryProvider(indexQueryProvider);
	}
	/**
	 * Returns query for invocation overview.
	 * 
	 * @param platformId
	 *            The ID of the platform.
	 * @param methodId
	 *            The ID of the method.
	 * @param limit
	 *            The limit/size of the list.
	 * @param fromDate
	 *            Date include invocation from.
	 * @param toDate
	 *            Date include invocation to.
	 * 
	 * @return Returns the query for invocation sequence overview.
	 */
	public E getInvocationSequenceOverview(long platformId, long methodId, int limit, Date fromDate, Date toDate) {
		E query = getIndexQueryProvider().getIndexQuery();
		query.setPlatformIdent(platformId);
		query.setMethodIdent(methodId);
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(InvocationSequenceData.class);
		query.setObjectClasses(searchedClasses);
		if (fromDate != null) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (toDate != null) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

	/**
	 * Returns query for invocation overview.
	 * 
	 * @param platformId
	 *            Platform ID where to look for the objects. If the zero value is passed, looking
	 *            for the object will be done in all platforms.
	 * @param invocationIdCollection
	 *            Collections of invocations IDs to search.
	 * @param limit
	 *            The limit/size of the list.
	 * 
	 * @return Returns the query for invocation sequence overview.
	 */
	public E getInvocationSequenceOverview(long platformId, Collection<Long> invocationIdCollection, int limit) {
		E query = getIndexQueryProvider().getIndexQuery();
		query.setPlatformIdent(platformId);
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(InvocationSequenceData.class);
		query.setObjectClasses(searchedClasses);
		query.addIndexingRestriction(IndexQueryRestrictionFactory.isInCollection("id", invocationIdCollection));
		return query;
	}
}
