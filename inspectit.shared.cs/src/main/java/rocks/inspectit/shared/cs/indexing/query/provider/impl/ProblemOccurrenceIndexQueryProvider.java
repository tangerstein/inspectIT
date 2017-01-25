package rocks.inspectit.shared.cs.indexing.query.provider.impl;

import rocks.inspectit.shared.cs.indexing.impl.ProblemOccurrenceIndexQuery;
import rocks.inspectit.shared.cs.indexing.query.provider.IIndexQueryProvider;
import rocks.inspectit.shared.cs.indexing.storage.impl.StorageIndexQuery;

public abstract class ProblemOccurrenceIndexQueryProvider implements IIndexQueryProvider<ProblemOccurrenceIndexQuery> {

	/**
	 * Creates properly initialized {@link StorageIndexQuery}.
	 * 
	 * @return Returns properly initialized {@link StorageIndexQuery}.
	 */
	public abstract ProblemOccurrenceIndexQuery createNewProblemOccurrenceQuery();

	/**
	 * {@inheritDoc}
	 */
	public ProblemOccurrenceIndexQuery getIndexQuery() {
		return createNewProblemOccurrenceQuery();
	}

}
