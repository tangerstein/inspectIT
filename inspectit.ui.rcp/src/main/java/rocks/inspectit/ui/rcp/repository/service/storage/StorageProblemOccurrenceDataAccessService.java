package rocks.inspectit.ui.rcp.repository.service.storage;

import java.util.Collection;
import java.util.Date;

import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType;
import rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrence;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.ProblemOccurrenceQueryFactory;
import rocks.inspectit.shared.cs.indexing.storage.IStorageTreeComponent;
import rocks.inspectit.shared.cs.indexing.storage.impl.StorageIndexQuery;

/**
 * @author Tobias Angerstein
 *
 */
public class StorageProblemOccurrenceDataAccessService extends AbstractStorageService<ProblemOccurrence> implements IProblemOccurrenceDataAccessService {
	/**
	 * Indexing tree.
	 */
	private IStorageTreeComponent<ProblemOccurrence> indexingTree;

	/**
	 * Index query provider.
	 */
	private ProblemOccurrenceQueryFactory<StorageIndexQuery> problemOccurrenceQueryFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStorageTreeComponent<ProblemOccurrence> getIndexingTree() {
		return indexingTree;
	}

	/**
	 * @param indexingTree
	 *            indexing tree
	 */
	public void setIndexingTree(IStorageTreeComponent<ProblemOccurrence> indexingTree) {
		this.indexingTree = indexingTree;
	}

	/**
	 * @param problemOccurrenceQueryFactory
	 *            the {@link ProblemOccurrenceQueryFactory} to set
	 */
	public void setProblemOccurrenceQueryFactory(ProblemOccurrenceQueryFactory<StorageIndexQuery> problemOccurrenceQueryFactory) {
		this.problemOccurrenceQueryFactory = problemOccurrenceQueryFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public Collection<ProblemOccurrence> getProblemOccurrencesBasedOnInvocationIds(long agentId, Date fromDate, Date toDate, long globalContextInvocationId, long problemContextInvocationId,
			long requestRootInvocationId, long rootCauseInvocationId, CauseType causeType, int applicationNameIdent, int businessTransactionNameIdent) {
		StorageIndexQuery query = problemOccurrenceQueryFactory.getProblemOccurrencesBasedOnInvocationIds(agentId, fromDate, toDate, globalContextInvocationId, problemContextInvocationId,
				requestRootInvocationId, rootCauseInvocationId, causeType, applicationNameIdent, businessTransactionNameIdent);
		return executeQuery(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public Collection<ProblemOccurrence> getProblemOccurrencesBasedOnMethodNames(Long agentId, Date fromDate, Date toDate, String globalContextMethodName, String problemContextMethodName,
			String requestRootMethodName, String rootCauseMethodName, CauseType causeType, String applicationName, String businessTransactionName) {
		// TODO Auto-generated method stub
		return null;
	}

}
