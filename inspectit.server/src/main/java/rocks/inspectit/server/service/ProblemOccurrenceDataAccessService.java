package rocks.inspectit.server.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rocks.inspectit.server.dao.ProblemOccurrenceDataDao;
import rocks.inspectit.server.spring.aop.MethodLog;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.spring.logger.Log;
import rocks.inspectit.shared.cs.cmr.service.IProblemOccurrenceDataAccessService;

@Service
public class ProblemOccurrenceDataAccessService implements IProblemOccurrenceDataAccessService {

	/** The logger of this class. */
	@Log
	Logger log;

	/**
	 * the problemOccurrence DAO
	 */
	@Autowired
	ProblemOccurrenceDataDao problemOccurrenceDataDao;
	
	@MethodLog
	public List<ProblemOccurrence> getProblemOccurrenceOverview(long platformId, Date fromDate, Date toDate,
			long globalContextId, long problemContextId, long requestRootId, long rootCauseId) {
		return problemOccurrenceDataDao.getProblemOccurrenceOverview(platformId, fromDate, toDate, globalContextId,
				problemContextId, requestRootId, rootCauseId);
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
