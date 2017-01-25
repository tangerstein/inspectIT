package rocks.inspectit.server.service.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import rocks.inspectit.server.dao.ProblemOccurrenceDataDao;
import rocks.inspectit.server.service.rest.error.JsonError;
import rocks.inspectit.shared.all.cmr.service.ICachedDataService;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurranceRestAware;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;

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
	private IProblemOccurrenceDataAccessService problemOccurrenceDataAccessService;
	/**
	 * TODO Reference to the existing {@link ProblemOccurrenceDataDao}.
	 */
	@Autowired
	private ICachedDataService cachedDataService;

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
		 * Provides overview of several invocation data.
		 *
		 * *
		 * <p>
		 * <i> Example URL: /data/invocations</i>
		 * </p>
		 *
		 * @param agentId
		 *            Agent ID.
		 * @param fromDate
		 *            Begin of time period.
		 * @param toDate
		 *            End of time period.
		 * @param latestReadId
		 *            Latest read ID of the invocations, only invocations with higher id are submitted.
		 * @param businessTrxId
		 *            Business transaction ID.
		 * @param applicationId
		 *            Application ID.
		 * @param limit
		 *            The limit/size of the results.
		 * @param minDuration
		 *            Minimum duration in milliseconds of the invocation to be returned.
		 * @return a list of {@link InvocationSequenceData}.
		 */
		@RequestMapping(method = GET, value = "")
		@ResponseBody
	public List<ProblemOccurranceRestAware> getInvocationSequenceOverview(
			@RequestParam(value = "agentId", required = false, defaultValue = "0") Long agentId,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date toDate,
			@RequestParam(value = "problemContextId", required = false, defaultValue = "0") Long problemContextId,
			@RequestParam(value = "globalContextId", required = false, defaultValue = "0") Long globalContextId,
			@RequestParam(value = "rootCauseId", required = false, defaultValue = "0") Long rootCauseId,
			@RequestParam(value = "requestRootId", required = false, defaultValue = "0") Long requestRootId
						){

		List<ProblemOccurrence> result = problemOccurrenceDataAccessService.getProblemOccurrenceOverview(agentId,
				fromDate, toDate, globalContextId, problemContextId, requestRootId, rootCauseId);
		List<ProblemOccurranceRestAware> resultRestAware = new ArrayList<ProblemOccurranceRestAware>();
		// Add method id
		for(ProblemOccurrence po : result){
			resultRestAware.add(new ProblemOccurranceRestAware(po,
					cachedDataService.getMethodIdentForId(po.getRequestRoot().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getRootCause().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getProblemContext().getMethodIdent()),
					cachedDataService.getMethodIdentForId(po.getGlobalContext().getMethodIdent())));
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
