package rocks.inspectit.shared.all.communication.data.diagnosis.results;

import org.codehaus.jackson.annotate.JsonProperty;

import rocks.inspectit.shared.all.cmr.model.MethodIdent;

/**
 * 
 * @author Tobias Angerstein
 *
 */
public class ProblemOccurranceRestAware extends ProblemOccurrence {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1263486575117938416L;
	/**
	 * full name of Method, which was executed in the request root
	 */
	@JsonProperty(value = "requestRootMethodIdentifier")
	String requestRootMethodIdentifier;
	/**
	 * full name of Method, which was executed in the root cause
	 */
	@JsonProperty(value = "rootCauseMethodIdentifier")
	String rootCauseMethodIdentifier;
	/**
	 * full name of Method, which was executed in the problem context
	 */
	@JsonProperty(value = "problemContextMethodIdentifier")
	String problemContextMethodIdentifier;
	/**
	 * full name of Method, which was executed in the global context
	 */
	@JsonProperty(value = "globalContextMethodIdentfier")
	String globalContextMethodIdentfier;

	/**
	 * Constructor.
	 * 
	 * @param requestRoot
	 * @param globalContext
	 * @param problemContext
	 * @param rootCause
	 * @param causeStructure
	 * @param requestRootMethodIdentifier
	 * @param rootCauseMethodIdentifier
	 * @param problemContextMethodIdentifier
	 * @param globalContextMethodIdentfier
	 */
	public ProblemOccurranceRestAware(InvocationIdentifier requestRoot, InvocationIdentifier globalContext,
			InvocationIdentifier problemContext, RootCause rootCause, CauseStructure causeStructure,
			String requestRootMethodIdentifier, String rootCauseMethodIdentifier, String problemContextMethodIdentifier,
			String globalContextMethodIdentfier) {
		super(requestRoot, globalContext, problemContext, rootCause, causeStructure);
		this.requestRootMethodIdentifier = requestRootMethodIdentifier;
		this.rootCauseMethodIdentifier = rootCauseMethodIdentifier;
		this.problemContextMethodIdentifier = problemContextMethodIdentifier;
		this.globalContextMethodIdentfier = globalContextMethodIdentfier;
	}

	/**
	 * Constructor using ProblemOccurrance
	 * 
	 * @param po
	 * @param requestRootMethodIdentifier.
	 * @param rootCauseMethodIdentifier
	 * @param problemContextMethodIdentifier
	 * @param globalContextMethodIdentfier
	 */
	public ProblemOccurranceRestAware(ProblemOccurrence po,
			MethodIdent requestRootMethodIdent, MethodIdent rootCauseMethodIdent, MethodIdent problemContextMethodIdent,
			MethodIdent globalContextMethodIdent) {
		super(po.getRequestRoot(), po.getGlobalContext(), po.getProblemContext(), po.getRootCause(),
				po.getCauseStructure());
		this.setId(po.getId());
		this.setPlatformIdent(po.getPlatformIdent());
		this.setTimeStamp(po.getTimeStamp());
		this.setSensorTypeIdent(po.getSensorTypeIdent());
		this.requestRootMethodIdentifier = requestRootMethodIdent.toString();
		this.rootCauseMethodIdentifier = rootCauseMethodIdent.toString();
		this.problemContextMethodIdentifier = problemContextMethodIdent.toString();
		this.globalContextMethodIdentfier = globalContextMethodIdent.toString();
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
		return globalContextMethodIdentfier;
	}

	public void setGlobalContextMethodIdentfier(String globalContextMethodIdentfier) {
		this.globalContextMethodIdentfier = globalContextMethodIdentfier;
	}

}
