package rocks.inspectit.server.diagnosis.results;

import java.util.Collection;

Qimport rocks.inspectit.shared.all.indexing.IIndexQuery;

/**
 * Interface of DiagnosisResults storage.
 *
 * @author Christian Voegele
 *
 * @param <R>
 */
public interface IDiagnosisResults<R> {

	/**
	 * @return Collection of diagnosis results.
	 */
	Collection<R> getDiagnosisResults();

	/**
	 * 
	 * @param query
	 *            Query defines the properties, which the returning results
	 *            should have
	 * @return Collection of diagnosis results which meet the restrictions of
	 *         the query.
	 */
	Collection<R> getDiagnosisResults(IIndexQuery query);
}
