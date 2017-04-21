package rocks.inspectit.server.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import rocks.inspectit.server.diagnosis.results.IDiagnosisResults;
import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.communication.data.AggregatedInvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.TimerData;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.all.testbase.TestBase;
import rocks.inspectit.shared.cs.cmr.service.cache.CachedDataService;
import rocks.inspectit.shared.cs.indexing.impl.IndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.ProblemOccurrenceQueryFactory;

/**
 * Tests the {@link ProblemOccurrenceDataAccessService}.
 * 
 * @author Tobias Angerstein
 *
 */
@SuppressWarnings("PMD")
public class ProblemOccurrenceDataAccessServiceTest extends TestBase {
	/**
	 * Class under test.
	 */
	@InjectMocks
	ProblemOccurrenceDataAccessService problemOccurrenceDataAccessService;

	/**
	 * Mocked Index query provider.
	 */
	@Mock
	ProblemOccurrenceQueryFactory<IIndexQuery> problemOccurrenceDataQueryFactory;

	/**
	 * Mocked DiagnosisResults.
	 */
	@Mock
	IDiagnosisResults<ProblemOccurrence> diagnosisResultsRepository;

	/**
	 * Dummy Problem Occurrences
	 */
	private static Collection<ProblemOccurrence> problemOccurrences;

	/**
	 * Initializes dummy problem occurrences
	 */
	public void initProblemOccurrences() {
		String methodName = "methodName";
		// Invocation Sequence data dummy
		InvocationSequenceData invocationSequenceData = new InvocationSequenceData();

		// Method ident
		MethodIdent methodIdent = new MethodIdent();
		methodIdent.setId(1L);
		methodIdent.setMethodName(methodName);

		// Timer data
		TimerData timerData = new TimerData();
		timerData.setDuration(1.0);
		timerData.setCpuDuration(1.0);

		// Configure invocation sequence data
		invocationSequenceData.setTimerData(timerData);
		invocationSequenceData.setMethodIdent(1L);
		invocationSequenceData.setApplicationId(2);
		invocationSequenceData.setBusinessTransactionId(3);

		AggregatedInvocationSequenceData aggregatedInvocationSequenceData = new AggregatedInvocationSequenceData();
		aggregatedInvocationSequenceData.setTimerData(timerData);
		aggregatedInvocationSequenceData.setMethodIdent(1L);
		aggregatedInvocationSequenceData.aggregate(invocationSequenceData);

		problemOccurrences = new ArrayList<ProblemOccurrence>();
		ProblemOccurrence problemOccurrence = new ProblemOccurrence(invocationSequenceData, invocationSequenceData, invocationSequenceData, aggregatedInvocationSequenceData,
				new CauseStructure(CauseType.SINGLE, 3));
		problemOccurrences.add(problemOccurrence);
	}

	/**
	 * Tests the
	 * {@link ProblemOccurrenceDataAccessService#getProblemOccurrencesBasedOnInvocationIds(long, java.util.Date, java.util.Date, long, long, long, long, rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType, int, int)}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public static class GetProblemOccurrencesBasedOnInvocationIds extends ProblemOccurrenceDataAccessServiceTest {
		@Test
		public void testGetProblemOccurrencesBasedOnInvocationIds() {
			initProblemOccurrences();

			// Query
			IIndexQuery query = new IndexQuery();

			// Behavior of query factory
			when(problemOccurrenceDataQueryFactory.getProblemOccurrencesBasedOnInvocationIds(0L, null, null, 1, 1, 1, 1, CauseType.SINGLE, 2, 3)).thenReturn(query);

			// Behavior of diagnosis results repository
			when(diagnosisResultsRepository.getDiagnosisResults(query)).thenReturn(problemOccurrences);

			// Method call
			Collection<ProblemOccurrence> result = problemOccurrenceDataAccessService.getProblemOccurrencesBasedOnInvocationIds(0L, null, null, 1, 1, 1, 1, CauseType.SINGLE, 2, 3);

			assertThat(result, is(problemOccurrences));
			verify(problemOccurrenceDataQueryFactory).getProblemOccurrencesBasedOnInvocationIds(0L, null, null, 1, 1, 1, 1, CauseType.SINGLE, 2, 3);
		}
	}

	/**
	 * Tests the
	 * {@link ProblemOccurrenceDataAccessService#getProblemOccurrencesBasedOnMethodNames(Long, java.util.Date, java.util.Date, String, String, String, String, rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType, String, String)}}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */

	public static class GetProblemOccurrencesBasedOnMethodNames extends ProblemOccurrenceDataAccessServiceTest {
		/**
		 * Mocked Cached data service.
		 */
		@Mock
		CachedDataService cachedDataService;

		@Test
		public void testGetProblemOccurrencesBasedOnMethodNames() {
			initProblemOccurrences();
			// names
			String applicationName = "applicationName";
			String businessTransactionName = "businessTransactionName";
			String methodName = "methodName";

			// Query
			IIndexQuery query = new IndexQuery();

			// Behavior of cachedDataService
			when(cachedDataService.getIdForMethodName(methodName)).thenReturn(1L);
			when(cachedDataService.getIdForApplicationName(applicationName)).thenReturn(2);
			when(cachedDataService.getIdForBusinessTransactionName(businessTransactionName)).thenReturn(3);

			// Behavior of query factory
			when(problemOccurrenceDataQueryFactory.getProblemOccurrencesBasedOnMethodIds(0L, null, null, 1, 1, 1, 1, CauseType.SINGLE, 2, 3)).thenReturn(query);

			// Behavior of diagnosis results repository
			when(diagnosisResultsRepository.getDiagnosisResults(query)).thenReturn(problemOccurrences);

			// Method call
			Collection<ProblemOccurrence> result = problemOccurrenceDataAccessService.getProblemOccurrencesBasedOnMethodNames(0L, null, null, methodName, methodName, methodName, methodName,
					CauseType.SINGLE, applicationName, businessTransactionName);

			assertThat(result, is(problemOccurrences));
			verify(problemOccurrenceDataQueryFactory).getProblemOccurrencesBasedOnMethodIds(0L, null, null, 1, 1, 1, 1, CauseType.SINGLE, 2, 3);
		}

	}

}
