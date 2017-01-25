/**
 *
 */
package rocks.inspectit.shared.all.communication.data.diagnosis.results;




import org.codehaus.jackson.annotate.JsonProperty;

import rocks.inspectit.shared.all.communication.DefaultData;
import rocks.inspectit.shared.all.communication.data.AggregatedInvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceDataHelper;
import rocks.inspectit.shared.all.communication.data.TimerData;
import rocks.inspectit.shared.all.indexing.IIndexQuery;
import rocks.inspectit.shared.all.indexing.IProblemOccurrenceIndexQuery;

/**
 * @author Alexander Wert
 *
 */
public class ProblemOccurrence extends DefaultData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3470892613217620473L;

	@JsonProperty(value = "requestRoot")
	private InvocationIdentifier requestRoot;

	@JsonProperty(value = "globalContext")
	private InvocationIdentifier globalContext;

	@JsonProperty(value = "problemContext")
	private InvocationIdentifier problemContext;

	@JsonProperty(value = "rootCause")
	private RootCause rootCause;

	@JsonProperty(value = "causeStructure")
	private CauseStructure causeStructure;

	/**
	 * Default constructor.
	 */
	public ProblemOccurrence() {
	}

	/**
	 * @param requestRoot
	 * @param globalContext
	 * @param problemContext
	 * @param rootCause
	 * @param causeStructure
	 */
	public ProblemOccurrence(InvocationIdentifier requestRoot, InvocationIdentifier globalContext, InvocationIdentifier problemContext, RootCause rootCause, CauseStructure causeStructure) {
		super();
		this.requestRoot = requestRoot;
		this.globalContext = globalContext;
		this.problemContext = problemContext;
		this.rootCause = rootCause;
		this.causeStructure = causeStructure;
	}

	public ProblemOccurrence(InvocationSequenceData requestRoot, InvocationSequenceData globalContext, InvocationSequenceData problemContext, AggregatedInvocationSequenceData rootCause,
			CauseStructure causeStructure) {
		this.requestRoot = new InvocationIdentifier(requestRoot.getMethodIdent(), requestRoot.getId());
		this.globalContext = new InvocationIdentifier(globalContext.getMethodIdent(), globalContext.getId());
		this.problemContext = new InvocationIdentifier(problemContext.getMethodIdent(), problemContext.getId());
		TimerData timerData = null;
		if (InvocationSequenceDataHelper.hasTimerData(rootCause)) {
			timerData = rootCause.getTimerData();
		} else if (InvocationSequenceDataHelper.hasSQLData(rootCause)) {
			timerData = rootCause.getSqlStatementData();
		}

		this.rootCause = new RootCause(rootCause.getMethodIdent(), timerData);
		for (InvocationSequenceData invocation : rootCause.getRawInvocationsSequenceElements()) {
			this.rootCause.getInvocationIds().add(invocation.getId());
		}
		this.causeStructure = causeStructure;
	}

	/**
	 * Gets {@link #requestRoot}.
	 *
	 * @return {@link #requestRoot}
	 */
	public InvocationIdentifier getRequestRoot() {
		return requestRoot;
	}

	/**
	 * Sets {@link #requestRoot}.
	 *
	 * @param requestRoot
	 *            New value for {@link #requestRoot}
	 */
	public void setRequestRoot(InvocationIdentifier requestRoot) {
		this.requestRoot = requestRoot;
	}

	/**
	 * Gets {@link #globalContext}.
	 *
	 * @return {@link #globalContext}
	 */
	public InvocationIdentifier getGlobalContext() {
		return globalContext;
	}

	/**
	 * Sets {@link #globalContext}.
	 *
	 * @param globalContext
	 *            New value for {@link #globalContext}
	 */
	public void setGlobalContext(InvocationIdentifier globalContext) {
		this.globalContext = globalContext;
	}

	/**
	 * Gets {@link #problemContext}.
	 *
	 * @return {@link #problemContext}
	 */
	public InvocationIdentifier getProblemContext() {
		return problemContext;
	}

	/**
	 * Sets {@link #problemContext}.
	 *
	 * @param problemContext
	 *            New value for {@link #problemContext}
	 */
	public void setProblemContext(InvocationIdentifier problemContext) {
		this.problemContext = problemContext;
	}

	/**
	 * Gets {@link #rootCause}.
	 *
	 * @return {@link #rootCause}
	 */
	public RootCause getRootCause() {
		return rootCause;
	}

	/**
	 * Sets {@link #rootCause}.
	 *
	 * @param rootCause
	 *            New value for {@link #rootCause}
	 */
	public void setRootCause(RootCause rootCause) {
		this.rootCause = rootCause;
	}

	/**
	 * Gets {@link #causeStructure}.
	 *
	 * @return {@link #causeStructure}
	 */
	public CauseStructure getCauseStructure() {
		return causeStructure;
	}

	/**
	 * Sets {@link #causeStructure}.
	 *
	 * @param causeStructure
	 *            New value for {@link #causeStructure}
	 */
	public void setCauseStructure(CauseStructure causeStructure) {
		this.causeStructure = causeStructure;
	}

	public static class CauseStructure {
		@JsonProperty(value = "causeType")
		private CauseType causeType;
		@JsonProperty(value = "depth")
		private int depth;

		/**
		 * @param causeType
		 * @param depth
		 */
		public CauseStructure(CauseType causeType, int depth) {
			super();
			this.causeType = causeType;
			this.depth = depth;
		}

		/**
		 * Gets {@link #causeType}.
		 *
		 * @return {@link #causeType}
		 */
		public CauseType getCauseType() {
			return causeType;
		}

		/**
		 * Sets {@link #causeType}.
		 *
		 * @param causeType
		 *            New value for {@link #causeType}
		 */
		public void setCauseType(CauseType causeType) {
			this.causeType = causeType;
		}

		/**
		 * Gets {@link #depth}.
		 *
		 * @return {@link #depth}
		 */
		public int getDepth() {
			return depth;
		}

		/**
		 * Sets {@link #depth}.
		 *
		 * @param depth
		 *            New value for {@link #depth}
		 */
		public void setDepth(int depth) {
			this.depth = depth;
		}



	}

	/**
	 * {@inheritDoc} TODO: CauseStructure is missing due to visibility reasons
	 */
	public boolean isQueryComplied(IIndexQuery query) {
		if (!super.isQueryComplied(query)) {
			return false;
		}
		if (query instanceof IProblemOccurrenceIndexQuery) {
			IProblemOccurrenceIndexQuery problemOccurrenceQuery = (IProblemOccurrenceIndexQuery) query;
			if (problemOccurrenceQuery.getGlobalContextId() != 0
					& problemOccurrenceQuery.getGlobalContextId() != this.getGlobalContext().getInvocationId()) {
				return false;
			}
			if (problemOccurrenceQuery.getProblemContextId() != 0
					& problemOccurrenceQuery.getProblemContextId() != this.getProblemContext().getInvocationId()) {
				return false;
			}
			if (problemOccurrenceQuery.getRequestRootID() != 0
					& problemOccurrenceQuery.getRequestRootID() != this.getRequestRoot().getInvocationId()) {
				return false;
			}
			if (problemOccurrenceQuery.getRootCauseId() != 0
					& this.getRootCause().getInvocationIds().contains(problemOccurrenceQuery.getRootCauseId())) {
				return false;
			}
		}
		return true;

	}



	public static enum CauseType {
		SINGLE, ITERATIVE, RECURSIVE
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProblemOccurrence other = (ProblemOccurrence) obj;
		if (getId() != other.getId()) {
			return false;
		}
		if (getPlatformIdent() != other.getPlatformIdent()) {
			return false;
		}
		if (getSensorTypeIdent() != other.getSensorTypeIdent()) {
			return false;
		}
		if (getTimeStamp() == null) {
			if (other.getTimeStamp() != null) {
				return false;
			}
		} else if (!getTimeStamp().equals(other.getTimeStamp())) {
			return false;
		}
		if (!(globalContext.equals(other.getGlobalContext()))) {
			return false;
		}
		if (!(problemContext.equals(other.getProblemContext()))) {
			return false;
		}
		if (!(requestRoot.equals(other.getRequestRoot()))) {
			return false;
		}
		if (!(rootCause.equals(other.getRootCause()))) {
			return false;
		}
		if (!(causeStructure.equals(other.getCauseStructure()))) {
			return false;
		}
		return true;
	}


	public int hashcode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((globalContext == null) ? 0 : globalContext.hashCode());
		result = prime * result + ((problemContext == null) ? 0 : problemContext.hashCode());
		result = prime * result + ((requestRoot == null) ? 0 : requestRoot.hashCode());
		result = prime * result + ((rootCause == null) ? 0 : rootCause.hashCode());
		result = prime * result + ((causeStructure == null) ? 0 : causeStructure.hashCode());
		return result;
	}

}
