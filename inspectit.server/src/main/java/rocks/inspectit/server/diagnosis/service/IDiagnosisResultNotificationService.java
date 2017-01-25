/**
 *
 */
package rocks.inspectit.server.diagnosis.service;

import java.util.Collection;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;

/**
 * @author Alexander Wert
 *
 */
public interface IDiagnosisResultNotificationService {
	void onNewDiagnosisResult(ProblemOccurrence problemOccurrence);

	void onNewDiagnosisResult(Collection<ProblemOccurrence> problemOccurrences);
}
