package rocks.inspectit.server.service.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import rocks.inspectit.server.service.rest.error.JsonError;
import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.cmr.service.ICachedDataService;
import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;
import rocks.inspectit.shared.cs.communication.data.diagnosis.CauseStructure.CauseType;
import rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrence;
import rocks.inspectit.shared.cs.communication.data.diagnosis.ProblemOccurrenceDetail;

/**
 * Restful service provider for detail {@link ProblemOccurrence} information.
 *
 * @author Tobias Angerstein
 *
 */
@Controller
@RequestMapping(value = "/data/problemOccurrences")
public class ProblemOccurrenceRestfulService {

	/**
	 * TODO Reference to the existing {@link ProblemOccurrenceDataDao}.
	 */
	@Autowired
	IProblemOccurrenceDataAccessService problemOccurrenceDataAccessService;

	/**
	 * TODO Reference to the existing {@link ProblemOccurrenceDataDao}.
	 */
	@Autowired
	ICachedDataService cachedDataService;

	/**
	 * Handling of all the exceptions happening in this controller.
	 *
	 * @param exception
	 *            Exception being thrown
	 * @return {@link ModelAndView}
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(Exception exception) {
		return new JsonError(exception).asModelAndView();
	}

	/**
	 * Returns collection of problem occurrences which meet the given arguments.
	 * 
	 * @param agentId
	 *            agent id
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            end date
	 * @param problemContextMethodName
	 *            method name of problem context (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param globalContextMethodName
	 *            method name of global context (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param rootCauseMethodName
	 *            method name of root cause (uses {@link MethodIdent#toString()
	 *            formatting})
	 * @param requestRootMethodName
	 *            method name of request root (uses
	 *            {@link MethodIdent#toString() formatting})
	 * @param causeType
	 *            cause type
	 * @param applicationName
	 *            name of application
	 * @param businessTransactionName
	 *            name of business transaction
	 * @return collection of problem occurrences
	 */
	@RequestMapping(method = GET, value = "")
	@ResponseBody
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public Collection<ProblemOccurrenceDetail> getProblemOccurrences(
			@RequestParam(value = "agentId", required = false, defaultValue = "0") Long agentId,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date toDate,
			@RequestParam(value = "problemContext", required = false) String problemContextMethodName,
			@RequestParam(value = "globalContext", required = false) String globalContextMethodName,
			@RequestParam(value = "rootCause", required = false) String rootCauseMethodName,
			@RequestParam(value = "requestRoot", required = false) String requestRootMethodName,
			@RequestParam(value = "causeType", required = false) CauseType causeType,
			@RequestParam(value = "applicationName", required = false) String applicationName,
			@RequestParam(value = "businessTransactionName", required = false) String businessTransactionName) {

		Collection<ProblemOccurrence> result = problemOccurrenceDataAccessService
				.getProblemOccurrencesBasedOnMethodNames(agentId, fromDate, toDate, globalContextMethodName,
						problemContextMethodName, requestRootMethodName, rootCauseMethodName, causeType,
						applicationName, businessTransactionName);

		return convertToProblemOccurrenceDetail(result);
	}

	/**
	 * Converts given ProblemOccurrences to ProblemOccurrenceDetail.
	 * 
	 * @param problemOccurrences
	 *            the given problemOccurrences
	 * 
	 * @return converted ProblemOccurrenceDetails
	 */
	private Collection<ProblemOccurrenceDetail> convertToProblemOccurrenceDetail(
			Collection<ProblemOccurrence> problemOccurrences) {
		Collection<ProblemOccurrenceDetail> resultRestAware = new ArrayList<ProblemOccurrenceDetail>();
		// Add method id
		for (ProblemOccurrence po : problemOccurrences) {
			resultRestAware.add(new ProblemOccurrenceDetail(po,
					cachedDataService.getMethodIdentForId(po.getRequestRoot().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getRootCause().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getProblemContext().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getGlobalContext().getMethodIdent()),
					cachedDataService.getApplicationForId(po.getApplicationNameIdent()),
					cachedDataService.getBusinessTransactionForId(po.getApplicationNameIdent(),
							po.getBusinessTransactionNameIdent())));
		}
		return resultRestAware;
	}

	/**
	 * Header information for swagger requests.
	 *
	 * @param response
	 *            Response information
	 */
	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	}
}
