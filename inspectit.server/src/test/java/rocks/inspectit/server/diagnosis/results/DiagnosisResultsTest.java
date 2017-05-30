package rocks.inspectit.server.diagnosis.results;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.mockito.InjectMocks;
import org.testng.annotations.Test;

import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.TimerData;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.all.testbase.TestBase;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType;
import rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrence;
import rocks.inspectit.shared.cs.communication.data.diagnosis.RootCause;
import rocks.inspectit.shared.cs.indexing.impl.IndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.impl.ProblemOccurrenceQueryFactory;
import rocks.inspectit.shared.cs.indexing.query.provider.IIndexQueryProvider;
import rocks.inspectit.shared.cs.indexing.restriction.impl.CachingIndexQueryRestrictionProcessor;

/**
 * Tests {@link DiagnosisResults}.
 * 
 * @author Tobias Angerstein
 *
 */
@SuppressWarnings("PMD")
public class DiagnosisResultsTest extends TestBase {
	@InjectMocks
	DiagnosisResults diagnosisResults;

	/**
	 * Dummy ProblemOccurrence
	 */
	ProblemOccurrence problemOccurrence;

	/**
	 * Initializes ProblemOccurrence object
	 */

	public void initProblemOccurrences() {
		// names
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
		invocationSequenceData.setPlatformIdent(0L);
		invocationSequenceData.setTimerData(timerData);
		invocationSequenceData.setMethodIdent(1L);
		invocationSequenceData.setApplicationId(2);
		invocationSequenceData.setBusinessTransactionId(3);
		
		RootCause rootCause = new RootCause(1L, invocationSequenceData, new ArrayList<InvocationSequenceData>(Arrays.asList(new InvocationSequenceData[]{invocationSequenceData})));

		CauseStructure causeStructure = new CauseStructure(CauseType.SINGLE, 3);

		problemOccurrence = new ProblemOccurrence(invocationSequenceData, invocationSequenceData, invocationSequenceData, rootCause, causeStructure);
	}

	/**
	 * Test {@link DiagnosisResults#getDiagnosisResults()}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public class getDiagnosisResults extends DiagnosisResultsTest {
		@Test
		public void getAllProblemOccurrences() {
			initProblemOccurrences();

			diagnosisResults.resultingSet.add(problemOccurrence);

			// Expected resultList
			Collection<ProblemOccurrence> expectedResults = new HashSet<ProblemOccurrence>();
			expectedResults.add(problemOccurrence);

			// Method call
			Collection<ProblemOccurrence> results = diagnosisResults.getDiagnosisResults();

			assertThat(results, is(expectedResults));
		}
	}

	/**
	 * Test
	 * {@link DiagnosisResults#getDiagnosisResults(rocks.inspectit.shared.all.indexing.IIndexQuery)}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public class getDiagnosisResultsFilteredWithQuery extends DiagnosisResultsTest {
		@Test
		public void getProblemOccurrenceWithFittingQuery() {
			initProblemOccurrences();

			diagnosisResults.resultingSet.add(problemOccurrence);

			// Expected resultList
			Collection<ProblemOccurrence> expectedResults = new HashSet<ProblemOccurrence>();
			expectedResults.add(problemOccurrence);

			// Create query which fits with created problemOccurrence
			ProblemOccurrenceQueryFactory<IIndexQuery> queryFactory = new ProblemOccurrenceQueryFactory<IIndexQuery>();
			queryFactory.setIndexQueryProvider(new IIndexQueryProvider() {

				@Override
				public IIndexQuery getIndexQuery() {
					return new IndexQuery();
				}
			});
			IndexQuery query = (IndexQuery) queryFactory.getProblemOccurrencesBasedOnMethodIds(0L, null, null, 1L, 1L, 1L, 1L, CauseType.SINGLE, 2, 3);
			query.setRestrictionProcessor(new CachingIndexQueryRestrictionProcessor());

			// Method call
			Collection<ProblemOccurrence> results = diagnosisResults.getDiagnosisResults(query);

			assertThat(results, is(expectedResults));
		}

		@Test
		public void getProblemOccurrenceWithNotFittingQuery() {
			initProblemOccurrences();

			diagnosisResults.resultingSet.add(problemOccurrence);

			// Expected resultList
			Collection<ProblemOccurrence> expectedResults = new HashSet<ProblemOccurrence>();

			// Create query which does not fit with created problemOccurrence
			ProblemOccurrenceQueryFactory<IIndexQuery> queryFactory = new ProblemOccurrenceQueryFactory<IIndexQuery>();
			queryFactory.setIndexQueryProvider(new IIndexQueryProvider() {

				@Override
				public IIndexQuery getIndexQuery() {
					return new IndexQuery();
				}
			});
			CauseStructure causeStructure = new CauseStructure(CauseType.SINGLE, 3);

			causeStructure.setCauseType(CauseType.SINGLE);
			IndexQuery query = (IndexQuery) queryFactory.getProblemOccurrencesBasedOnMethodIds(1L, null, null, 1L, 1L, 1L, 1L, CauseType.SINGLE, 2, 3);
			query.setRestrictionProcessor(new CachingIndexQueryRestrictionProcessor());

			// Method call
			Collection<ProblemOccurrence> results = diagnosisResults.getDiagnosisResults(query);

			assertThat(results, is(expectedResults));
		}
	}

}
