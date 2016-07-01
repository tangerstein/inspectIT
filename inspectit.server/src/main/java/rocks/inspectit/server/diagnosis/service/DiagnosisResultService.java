package rocks.inspectit.server.diagnosis.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rocks.inspectit.server.diagnosis.service.results.ProblemOccurrence;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.cs.cmr.service.IInvocationDataAccessService;

/**
 * @author Alexander Wert
 *
 */
@Component
public class DiagnosisResultService implements IDiagnosisResultNotificationService {

	@Autowired
	IInvocationDataAccessService accessService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewDiagnosisResult(ProblemOccurrence problemOccurrence) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewDiagnosisResult(Collection<ProblemOccurrence> problemOccurrences) {
		for (ProblemOccurrence po : problemOccurrences) {
			long invocId = po.getProblemContext().getInvocationId();
			List<InvocationSequenceData> templates = accessService.getInvocationSequenceOverview(0, Collections.singletonList(invocId), 1, null);
			System.out.println("test");
		}

	}

}
