package rocks.inspectit.server.service;

import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rocks.inspectit.server.diagnosis.results.IDiagnosisResults;
import rocks.inspectit.server.spring.aop.MethodLog;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.all.spring.logger.Log;
import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;
import rocks.inspectit.shared.cs.cmr.service.cache.CachedDataService;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.ProblemOccurrenceQueryFactory;

/**
 * @author Tobias Angerstein
 *
 */
@Service
public class ProblemOccurrenceDataAccessService implements IProblemOccurrenceDataAccessService {

	/** The logger of this class. */
	@Log
	Logger log;

	/**
	 * Index query provider.
	 */
	@Autowired
	private ProblemOccurrenceQueryFactory<IIndexQuery> problemOccurrenceDataQueryFactory;

	/**
	 * Cached data service.
	 */
	@Autowired
	private CachedDataService cachedDataService;

	/**
	 * DiagnosisResults.
	 */
	@Autowired
	private IDiagnosisResults<ProblemOccurrence> diagnosisResultsRepository;

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public Collection<ProblemOccurrence> getProblemOccurrencesBasedOnInvocationIds(long platformId, Date fromDate,
			Date toDate, long globalContextId, long problemContextId, long requestRootId, long rootCauseId,
			CauseType causeType, int applicationNameIdent, int businessTransactionNameIdent) {
		IIndexQuery query = problemOccurrenceDataQueryFactory.getProblemOccurrencesBasedOnInvocationIds(platformId,
				fromDate, toDate, globalContextId, problemContextId, requestRootId, rootCauseId, causeType,
				applicationNameIdent, businessTransactionNameIdent);
		return diagnosisResultsRepository.getDiagnosisResults(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public Collection<ProblemOccurrence> getProblemOccurrencesBasedOnMethodNames(Long agentId, Date fromDate,
			Date toDate, String globalContextMethodName, String problemContextMethodName, String requestRootMethodName,
			String rootCauseMethodName, CauseType causeType, String applicationName, String businessTransactionName) {
		IIndexQuery query = problemOccurrenceDataQueryFactory.getProblemOccurrencesBasedOnMethodIds(agentId, fromDate,
				toDate, cachedDataService.getIdForMethodName(globalContextMethodName),
				cachedDataService.getIdForMethodName(problemContextMethodName),
				cachedDataService.getIdForMethodName(requestRootMethodName),
				cachedDataService.getIdForMethodName(rootCauseMethodName), causeType,
				cachedDataService.getIdForApplicationName(applicationName),
				cachedDataService.getIdForBusinessTransactionName(businessTransactionName));
		return diagnosisResultsRepository.getDiagnosisResults(query);
	}

	/**
	 * Is executed after dependency injection is done to perform any
	 * initialization.
	 * 
	 * @throws Exception
	 *             if an error occurs during {@link PostConstruct}
	 */
	@PostConstruct
	public void postConstruct() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("|-ProblemOccurrence Data Access Service active...");
		}
	}

}
