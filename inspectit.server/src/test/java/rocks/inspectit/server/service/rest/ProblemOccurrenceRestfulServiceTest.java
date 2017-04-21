package rocks.inspectit.server.service.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.cmr.service.ICachedDataService;
import rocks.inspectit.shared.all.communication.data.AggregatedInvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.TimerData;
import rocks.inspectit.shared.all.communication.data.cmr.ApplicationData;
import rocks.inspectit.shared.all.communication.data.cmr.BusinessTransactionData;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrenceDetail;
import rocks.inspectit.shared.all.testbase.TestBase;
import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;

/**
 * Tests the {@link ProblemOccurrenceRestfulService}
 * 
 * @author Tobias Angerstein
 *
 */
@SuppressWarnings("PMD")
public class ProblemOccurrenceRestfulServiceTest extends TestBase {
	/**
	 * Class under test.
	 */
	@InjectMocks
	ProblemOccurrenceRestfulService problemOccurrenceRestfulService;

	/**
	 * Tests the
	 * {@link ProblemOccurrenceRestfulService#getProblemOccurrences(Long, java.util.Date, java.util.Date, String, String, String, String, rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType, String, String)}
	 * method.
	 *
	 * @author Tobias Angerstein
	 *
	 */
	public static class GetProblemOccurrences extends ProblemOccurrenceRestfulServiceTest {

		/**
		 * Mocked {@link IProblemOccurrenceDataAccessService}.
		 */
		@Mock
		private IProblemOccurrenceDataAccessService problemOccurrenceDataAccessService;

		/**
		 * Mocked {@link ICachedDataService}.
		 */
		@Mock
		private ICachedDataService cachedDataService;

		@Test
		public void getSpecificProblemOccurrence() {
			// names
			String applicationName = "applicationName";
			String businessTransactionName = "businessTransactionName";
			String methodName = "methodName";

			// Invocation Sequence data dummy
			InvocationSequenceData invocationSequenceData = new InvocationSequenceData();

			// Method ident
			MethodIdent methodIdent = new MethodIdent();
			methodIdent.setId(1L);
			methodIdent.setMethodName(methodName);

			// Application data
			ApplicationData applicationData = new ApplicationData(10, 2, applicationName);

			// Business transaction data
			BusinessTransactionData businessTransactionData = new BusinessTransactionData(11, 3, applicationData, businessTransactionName);

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

			// internal result list
			Collection<ProblemOccurrence> problemOccurrences = new ArrayList<ProblemOccurrence>();
			ProblemOccurrence problemOccurrence = new ProblemOccurrence(invocationSequenceData, invocationSequenceData, invocationSequenceData, aggregatedInvocationSequenceData,
					new CauseStructure(CauseType.SINGLE, 3));
			problemOccurrences.add(problemOccurrence);

			// external expected resultList
			Collection<ProblemOccurrenceDetail> problemOccurrenceDetails = new ArrayList<ProblemOccurrenceDetail>();
			ProblemOccurrenceDetail problemOccurrenceDetail = new ProblemOccurrenceDetail(problemOccurrence, methodIdent, methodIdent, methodIdent, methodIdent, applicationData,
					businessTransactionData);
			problemOccurrenceDetails.add(problemOccurrenceDetail);

			// Behaviour of cachedDataService
			when(cachedDataService.getMethodIdentForId(1)).thenReturn(methodIdent);
			when(cachedDataService.getApplicationForId(2)).thenReturn(applicationData);
			when(cachedDataService.getBusinessTransactionForId(2, 3)).thenReturn(businessTransactionData);

			when(problemOccurrenceDataAccessService.getProblemOccurrencesBasedOnMethodNames(0L, null, null, methodName, methodName, methodName, methodName, CauseType.SINGLE, applicationName,
					businessTransactionName)).thenReturn(problemOccurrences);

			Collection<ProblemOccurrenceDetail> result = problemOccurrenceRestfulService.getProblemOccurrences(0L, null, null, methodName, methodName, methodName, methodName, CauseType.SINGLE,
					applicationName, businessTransactionName);

			assertThat(result, is(problemOccurrenceDetails));
			verify(problemOccurrenceDataAccessService).getProblemOccurrencesBasedOnMethodNames(0L, null, null, methodName, methodName, methodName, methodName, CauseType.SINGLE, applicationName,
					businessTransactionName);
		}
	}

	/**
	 * Tests the
	 * {@link ProblemOccurrenceRestfulService#setVaryResponseHeader(javax.servlet.http.HttpServletResponse)}}
	 * 
	 * @author Tobias Angerstein
	 *
	 */
	public static class SetVaryResponseHeader extends ProblemOccurrenceRestfulServiceTest {

		@Test
		public void testVaryResponseHeader() {
			HttpServletResponse response = new MockHttpServletResponse();

			// Method call
			problemOccurrenceRestfulService.setVaryResponseHeader(response);

			assertThat(response.getHeader("Access-Control-Allow-Origin"), is("*"));
			assertThat(response.getHeader("Access-Control-Allow-Headers"), is("Origin, X-Requested-With, Content-Type, Accept"));

		}
	}
}