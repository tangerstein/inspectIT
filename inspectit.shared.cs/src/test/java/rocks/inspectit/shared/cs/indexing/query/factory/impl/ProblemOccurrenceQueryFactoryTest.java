package rocks.inspectit.shared.cs.indexing.query.factory.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.all.testbase.TestBase;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType;
import rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrence;
import rocks.inspectit.shared.cs.indexing.impl.IndexQuery;
import rocks.inspectit.shared.cs.indexing.query.provider.impl.IndexQueryProvider;
import rocks.inspectit.shared.cs.indexing.restriction.impl.IndexQueryRestrictionFactory;

/**
 * Tests the {@link ProblemOccurrenceQueryFactory}
 * 
 * @author Tobias Angerstein
 *
 */
public class ProblemOccurrenceQueryFactoryTest extends TestBase {

	/**
	 * Class under test.
	 */
	@InjectMocks
	ProblemOccurrenceQueryFactory<IIndexQuery> problemOccurrenceQueryFactory;

	/**
	 * Mocked IndexQueryProvider
	 *
	 */
	@Mock
	IndexQueryProvider indexQueryProvider;

	/**
	 * Tests
	 * {@link ProblemOccurrenceQueryFactory#getProblemOccurrencesBasedOnInvocationIds(long, java.util.Date, java.util.Date, long, long, long, long, rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType, int, int)}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public static class GetProblemOccurrencesBasedOnInvocationIds extends ProblemOccurrenceQueryFactoryTest {

		@Test
		public void getWildCardQueryBasedOnInvocationIds() {
			// empty query
			IndexQuery emptyQuery = new IndexQuery();

			// IndexQueryProvider Behavior
			when(indexQueryProvider.getIndexQuery()).thenReturn(emptyQuery);

			// Method call
			IIndexQuery query = problemOccurrenceQueryFactory.getProblemOccurrencesBasedOnInvocationIds(0, null, null,
					0, 0, 0, 0, null, 0, 0);

			assertThat(query, is((IIndexQuery) emptyQuery));
		}

		@Test
		public void getCustomQueryBasedOnInvocationIds() {
			// empty query
			IndexQuery emptyQuery = new IndexQuery();

			// IndexQueryProvider Behavior
			when(indexQueryProvider.getIndexQuery()).thenReturn(emptyQuery);

			// Method call
			IIndexQuery query = problemOccurrenceQueryFactory.getProblemOccurrencesBasedOnInvocationIds(0L,
					new Date(1000), new Date(1010), 1L, 1L, 1L, 1L, rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType.RECURSIVE, 2, 3);

			// Configure expected query
			IIndexQuery expectedQuery = new IndexQuery();
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("requestRoot.invocationId", 1L));
			expectedQuery.addIndexingRestriction(
					IndexQueryRestrictionFactory.collectionContainsObject("rootCause.invocationIds", 1L));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("problemContext.invocationId", 1L));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("globalContext.invocationId", 1L));
			expectedQuery.addIndexingRestriction(
					IndexQueryRestrictionFactory.equal("causeStructure.causeType", rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType.RECURSIVE));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("applicationNameIdent", 2));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("businessTransactionNameIdent", 3));
			expectedQuery.setFromDate(new Timestamp(1000));
			expectedQuery.setToDate(new Timestamp(1010));
			ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
			searchedClasses.add(rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrence.class);
			expectedQuery.setObjectClasses(searchedClasses);

			assertThat(query, is(expectedQuery));

		}
	}

	/**
	 * Tests
	 * {@link ProblemOccurrenceQueryFactory#getProblemOccurrencesBasedOnMethodIds(long, Date, Date, long, long, long, long, CauseType, int, int)}.
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public static class GetProblemOccurrencesBasedOnMethodIdents extends ProblemOccurrenceQueryFactoryTest {

		@Test
		public void getWildCardQueryBasedOnMethodIdents() {
			// empty query
			IndexQuery emptyQuery = new IndexQuery();

			// IndexQueryProvider Behavior
			when(indexQueryProvider.getIndexQuery()).thenReturn(emptyQuery);

			// Method call
			IIndexQuery query = problemOccurrenceQueryFactory.getProblemOccurrencesBasedOnMethodIds(0, null, null, 0, 0,
					0, 0, null, 0, 0);

			assertThat(query, is((IIndexQuery) emptyQuery));
		}

		@Test
		public void getCustomQueryBasedOnMethodIdents() {
			// empty query
			IndexQuery emptyQuery = new IndexQuery();

			// IndexQueryProvider Behavior
			when(indexQueryProvider.getIndexQuery()).thenReturn(emptyQuery);

			// Method call
			IIndexQuery query = problemOccurrenceQueryFactory.getProblemOccurrencesBasedOnMethodIds(0L, new Date(1000),
					new Date(1010), 1L, 1L, 1L, 1L, CauseType.RECURSIVE, 2, 3);

			// Configure expected query
			IIndexQuery expectedQuery = new IndexQuery();
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("requestRoot.methodIdent", 1L));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("rootCause.methodIdent", 1L));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("problemContext.methodIdent", 1L));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("globalContext.methodIdent", 1L));
			expectedQuery.addIndexingRestriction(
					IndexQueryRestrictionFactory.equal("causeStructure.causeType", CauseType.RECURSIVE));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("applicationNameIdent", 2));
			expectedQuery.addIndexingRestriction(IndexQueryRestrictionFactory.equal("businessTransactionNameIdent", 3));
			expectedQuery.setFromDate(new Timestamp(1000));
			expectedQuery.setToDate(new Timestamp(1010));
			ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
			searchedClasses.add(ProblemOccurrence.class);
			expectedQuery.setObjectClasses(searchedClasses);

			assertThat(query, is(expectedQuery));

		}
	}

}
