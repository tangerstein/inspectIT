package rocks.inspectit.shared.all.communication.data.diagnosis.results;

import org.codehaus.jackson.annotate.JsonProperty;

import rocks.inspectit.shared.all.cmr.model.MethodIdent;
import rocks.inspectit.shared.all.communication.data.cmr.ApplicationData;
import rocks.inspectit.shared.all.communication.data.cmr.BusinessTransactionData;

/**
 * 
 * @author Tobias Angerstein
 *
 */
public class ProblemOccurrenceDetail extends ProblemOccurrence {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 1263486575117938416L;

	/**
	 * full name of Method, which was executed in the request root.
	 */
	@JsonProperty(value = "requestRootMethodIdentifier")
	String requestRootMethodIdentifier;

	/**
	 * full name of Method, which was executed in the root cause.
	 */
	@JsonProperty(value = "rootCauseMethodIdentifier")
	String rootCauseMethodIdentifier;

	/**
	 * full name of Method, which was executed in the problem context.
	 */
	@JsonProperty(value = "problemContextMethodIdentifier")
	String problemContextMethodIdentifier;

	/**
	 * full name of Method, which was executed in the global context.
	 */
	@JsonProperty(value = "globalContextMethodIdentfier")
	String globalContextMethodIdentifier;

	/**
	 * application name.
	 */
	@JsonProperty(value = "applicationName")
	String applicationName;

	/**
	 * business transaction name.
	 */
	@JsonProperty(value = "businessTransactionName")
	String businessTransactionName;

	/**
	 * Constructor using ProblemOccurrance.
	 * 
	 * @param problemOccurrence
	 *            problem occurrence
	 * @param requestRootMethodIdent
	 *            request root method Id
	 * @param rootCauseMethodIdent
	 *            root cause method Id
	 * @param problemContextMethodIdent
	 *            global context method Id
	 * @param globalContextMethodIdent
	 *            global context method Id
	 * @param applicationData
	 *            application data
	 * @param businessTransactionData
	 *            business transaction data
	 */
	public ProblemOccurrenceDetail(ProblemOccurrence problemOccurrence, MethodIdent requestRootMethodIdent, MethodIdent rootCauseMethodIdent, MethodIdent problemContextMethodIdent,
			MethodIdent globalContextMethodIdent, ApplicationData applicationData, BusinessTransactionData businessTransactionData) {

		super(problemOccurrence.getRequestRoot(), problemOccurrence.getGlobalContext(), problemOccurrence.getProblemContext(), problemOccurrence.getRootCause(), problemOccurrence.getCauseStructure(),
				problemOccurrence.getBusinessTransactionNameIdent(), problemOccurrence.getApplicationNameIdent());
		this.setId(problemOccurrence.getId());
		this.setPlatformIdent(problemOccurrence.getPlatformIdent());
		this.setTimeStamp(problemOccurrence.getTimeStamp());
		this.setSensorTypeIdent(problemOccurrence.getSensorTypeIdent());
		this.requestRootMethodIdentifier = requestRootMethodIdent.toString();
		this.rootCauseMethodIdentifier = rootCauseMethodIdent.toString();
		this.problemContextMethodIdentifier = problemContextMethodIdent.toString();
		this.globalContextMethodIdentifier = globalContextMethodIdent.toString();
		this.applicationName = applicationData.getName();
		this.businessTransactionName = businessTransactionData.getName();
	}

	public String getRequestRootMethodIdentifier() {
		return requestRootMethodIdentifier;
	}

	public void setRequestRootMethodIdentifier(String requestRootMethodIdentifier) {
		this.requestRootMethodIdentifier = requestRootMethodIdentifier;
	}

	public String getRootCauseMethodIdentifier() {
		return rootCauseMethodIdentifier;
	}

	public void setRootCauseMethodIdentifier(String rootCauseMethodIdentifier) {
		this.rootCauseMethodIdentifier = rootCauseMethodIdentifier;
	}

	public String getProblemContextMethodIdentifier() {
		return problemContextMethodIdentifier;
	}

	public void setProblemContextMethodIdentifier(String problemContextMethodIdentifier) {
		this.problemContextMethodIdentifier = problemContextMethodIdentifier;
	}

	public String getGlobalContextMethodIdentfier() {
		return globalContextMethodIdentifier;
	}

	public void setGlobalContextMethodIdentfier(String globalContextMethodIdentfier) {
		this.globalContextMethodIdentifier = globalContextMethodIdentfier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.requestRootMethodIdentifier == null) ? 0 : this.requestRootMethodIdentifier.hashCode());
		result = (prime * result) + ((this.globalContextMethodIdentifier == null) ? 0 : this.globalContextMethodIdentifier.hashCode());
		result = (prime * result) + ((this.problemContextMethodIdentifier == null) ? 0 : this.problemContextMethodIdentifier.hashCode());
		result = (prime * result) + ((this.rootCauseMethodIdentifier == null) ? 0 : this.rootCauseMethodIdentifier.hashCode());
		result = (prime * result) + ((this.applicationName == null) ? 0 : this.applicationName.hashCode());
		result = (prime * result) + ((this.businessTransactionName == null) ? 0 : this.businessTransactionName.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProblemOccurrenceDetail other = (ProblemOccurrenceDetail) obj;

		if (!this.requestRootMethodIdentifier.equals(other.requestRootMethodIdentifier)) {
			return false;
		}
		if (!this.globalContextMethodIdentifier.equals(other.globalContextMethodIdentifier)) {
			return false;
		}
		if (!this.problemContextMethodIdentifier.equals(other.problemContextMethodIdentifier)) {
			return false;
		}
		if (!this.rootCauseMethodIdentifier.equals(other.rootCauseMethodIdentifier)) {
			return false;
		}
		if (!this.applicationName.equals(other.applicationName)) {
			return false;
		}
		if (!this.businessTransactionName.equals(other.businessTransactionName)) {
			return false;
		}
		return true;
	}

}
