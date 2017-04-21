package rocks.inspectit.shared.cs.indexing.query.factory.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

import rocks.inspectit.shared.all.communication.data.diagnosis.results.CauseStructure.CauseType;
import rocks.inspectit.shared.all.communication.data.diagnosis.results.ProblemOccurrence;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.cs.indexing.query.factory.AbstractQueryFactory;
import rocks.inspectit.shared.cs.indexing.restriction.impl.IndexQueryRestrictionFactory;

/**
 * Factory for all queries for the {@link ProblemOccurrence}.
 *
 * @author Tobias Angerstein
 *
 * @param <E>
 */
@Component
public class ProblemOccurrenceQueryFactory<E extends IIndexQuery> extends AbstractQueryFactory<E> {

	/**
	 * Returns a query object, which filters problem occurrences based on invocation sequence data
	 * ids.
	 * 
	 * @param platformId
	 *            platform id
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            to date
	 * @param globalContextInvocationId
	 *            global context invocation id
	 * @param problemContextInvocationId
	 *            problem context invocation id
	 * @param requestRootInvocationId
	 *            request root invocation id
	 * @param rootCauseInvocationId
	 *            root cause invocation id
	 * @param causeType
	 *            {@link CauseType}
	 * @param applicationNameIdent
	 *            id of application name
	 * @param businessTransactionNameIdent
	 *            id of business transaction name
	 * @return {@link IIndexQuery}
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public E getProblemOccurrencesBasedOnInvocationIds(long platformId, Date fromDate, Date toDate, long globalContextInvocationId, long problemContextInvocationId, long requestRootInvocationId,
			long rootCauseInvocationId, CauseType causeType, int applicationNameIdent, int businessTransactionNameIdent) {
		E query = getIndexQueryProvider().getIndexQuery();
		// Set additional restrictions
		query.setPlatformIdent(platformId);

		if (requestRootInvocationId != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("requestRoot.invocationId", requestRootInvocationId));
		}
		if (rootCauseInvocationId != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.collectionContainsObject("rootCause.invocationIds", rootCauseInvocationId));
		}
		if (problemContextInvocationId != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("problemContext.invocationId", problemContextInvocationId));
		}
		if (globalContextInvocationId != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("globalContext.invocationId", globalContextInvocationId));
		}
		if (causeType != null) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("causeStructure.causeType", causeType));
		}

		if (applicationNameIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("applicationNameIdent", applicationNameIdent));
		}

		if (businessTransactionNameIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("businessTransactionNameIdent", businessTransactionNameIdent));
		}

		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ProblemOccurrence.class);
		query.setObjectClasses(searchedClasses);
		if (fromDate != null) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (toDate != null) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

	/**
	 * Returns a query object, which filters problem occurrences based on method ident ids.
	 * 
	 * @param platformId
	 *            platform id
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            to date
	 * @param globalContextMethodIdent
	 *            global context method ident
	 * @param problemContextMethodIdent
	 *            problem context method ident
	 * @param requestRootMethodIdent
	 *            request root method ident
	 * @param rootCauseMethodIdent
	 *            root cause method ident
	 * @param causeType
	 *            {@link CauseType}
	 * @param applicationNameIdent
	 *            id of application name
	 * @param businessTransactionNameIdent
	 *            id of business transaction name
	 * @return {@link IIndexQuery}
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public E getProblemOccurrencesBasedOnMethodIds(long platformId, Date fromDate, Date toDate, long globalContextMethodIdent, long problemContextMethodIdent, long requestRootMethodIdent,
			long rootCauseMethodIdent, CauseType causeType, int applicationNameIdent, int businessTransactionNameIdent) {
		E query = getIndexQueryProvider().getIndexQuery();
		// Set additional restrictions
		query.setPlatformIdent(platformId);

		if (requestRootMethodIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("requestRoot.methodIdent", requestRootMethodIdent));
		}
		if (rootCauseMethodIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("rootCause.methodIdent", rootCauseMethodIdent));
		}
		if (problemContextMethodIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("problemContext.methodIdent", problemContextMethodIdent));
		}
		if (globalContextMethodIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("globalContext.methodIdent", globalContextMethodIdent));
		}
		if (causeType != null) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("causeStructure.causeType", causeType));
		}

		if (applicationNameIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("applicationNameIdent", applicationNameIdent));
		}

		if (businessTransactionNameIdent != 0) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("businessTransactionNameIdent", businessTransactionNameIdent));
		}

		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ProblemOccurrence.class);
		query.setObjectClasses(searchedClasses);
		if (fromDate != null) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (toDate != null) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

}
