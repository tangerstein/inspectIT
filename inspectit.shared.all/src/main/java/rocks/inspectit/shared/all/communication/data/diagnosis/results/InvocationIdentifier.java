/**
 *
 */
package rocks.inspectit.shared.all.communication.data.diagnosis.results;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;

/**
 * Abstract representation of an InvocationSequenceData, including the id to the methodName, the Id
 * to the InvocationSequenceData and the relevant {@link #TimerDataProblemOccurence}.
 *
 * @author Alexander Wert, Christian Voegele
 *
 */
public class InvocationIdentifier implements Serializable {

	/**
	 * Default serialVersionUID.
	 */
	private static final long serialVersionUID = -8585945205489244223L;

	/**
	 * The id to the methodName.
	 */
	@JsonProperty(value = "methodIdent")
	private long methodIdent;

	/**
	 * The id to the invocationId.
	 */
	@JsonProperty(value = "invocationId")
	private long invocationId;

	/**
	 * Representation of relevant timerData.
	 */
	@JsonProperty(value = "timerDataProblemOccurence")
	private TimerDataProblemOccurrence timerDataProblemOccurence;

	/**
	 * Create new InvocationIdentifier based on InvocationSequenceData.
	 *
	 * @param invocationSequenceData
	 *            the InvocationIdentifier the InvocationIdentifier is created
	 */
	public InvocationIdentifier(final InvocationSequenceData invocationSequenceData) {
		this.methodIdent = invocationSequenceData.getMethodIdent();
		this.invocationId = invocationSequenceData.getId();
		this.timerDataProblemOccurence = new TimerDataProblemOccurrence(invocationSequenceData);
	}

	/**
	 * Gets {@link #methodIdent}.
	 *
	 * @return {@link #methodIdent}
	 */
	public final long getMethodIdent() {
		return this.methodIdent;
	}

	/**
	 * Sets {@link #methodIdent}.
	 *
	 * @param methodIdent
	 *            New value for {@link #methodIdent}
	 */
	public final void setMethodIdent(long methodIdent) {
		this.methodIdent = methodIdent;
	}

	/**
	 * Gets {@link #invocationId}.
	 *
	 * @return {@link #invocationId}
	 */
	public final long getInvocationId() {
		return this.invocationId;
	}

	/**
	 * Sets {@link #invocationId}.
	 *
	 * @param invocationId
	 *            New value for {@link #invocationId}
	 */
	public final void setInvocationId(long invocationId) {
		this.invocationId = invocationId;
	}

	/**
	 * Gets {@link #timerDataProblemOccurence}.
	 *
	 * @return {@link #timerDataProblemOccurence}
	 */
	public final TimerDataProblemOccurrence getTimerDataProblemOccurence() {
		return this.timerDataProblemOccurence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (this.invocationId ^ (this.invocationId >>> 32));
		result = (prime * result) + (int) (this.methodIdent ^ (this.methodIdent >>> 32));
		result = (prime * result) + ((this.timerDataProblemOccurence == null) ? 0 : this.timerDataProblemOccurence.hashCode());
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InvocationIdentifier other = (InvocationIdentifier) obj;
		if (this.invocationId != other.invocationId) {
			return false;
		}
		if (this.methodIdent != other.methodIdent) {
			return false;
		}
		if (this.timerDataProblemOccurence == null) {
			if (other.timerDataProblemOccurence != null) {
				return false;
			}
		} else if (!this.timerDataProblemOccurence.equals(other.timerDataProblemOccurence)) {
			return false;
		}
		return true;
	}

}
