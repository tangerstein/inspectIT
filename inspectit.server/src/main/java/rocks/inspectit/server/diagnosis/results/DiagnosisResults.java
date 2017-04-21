package rocks.inspectit.server.diagnosis.results;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.indexing.IIndexQuery;

/**
 *
 * Stores diagnosis results (ProblemOccurrence).
 *
 * @author Christian Voegele
 *
 */
@Repository
public class DiagnosisResults implements IDiagnosisResults<ProblemOccurrence> {

	/**
	 * Stores the resulting ProblemOccurrences.
	 */
	Set<ProblemOccurrence> resultingSet = new HashSet<ProblemOccurrence>(1000);

	/**
	 * Gets {@link #resultingSet}.
	 *
	 * @return {@link #resultingSet}
	 */
	@Override
	public final Set<ProblemOccurrence> getDiagnosisResults() {
		return this.resultingSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<ProblemOccurrence> getDiagnosisResults(IIndexQuery query) {
		Set<ProblemOccurrence> filteredProblemOccurrences = new HashSet<ProblemOccurrence>();
		for (ProblemOccurrence problemOccurrence : resultingSet) {
			if (problemOccurrence.isQueryComplied(query)) {
				filteredProblemOccurrences.add(problemOccurrence);
			}
		}
		return filteredProblemOccurrences;

	}
}
