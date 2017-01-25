package rocks.inspectit.shared.cs.indexing.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rocks.inspectit.shared.all.indexing.IProblemOccurrenceIndexQuery;

/**
 * TODO: create senseful criterias
 * 
 * @author Tobias Angerstein
 *
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ProblemOccurrenceIndexQuery extends IndexQuery implements IProblemOccurrenceIndexQuery {
	/**
	 * 
	 */
	private long requestRootID;
	/**
	 * 
	 */
	private long globalContextId;
	/**
	 * 
	 */
	private long problemContextId;

	/**
	 * 
	 */
	private long rootCauseId;

	public long getRequestRootID() {
		return requestRootID;
	}

	public void setRequestRootID(long requestRootID) {
		this.requestRootID = requestRootID;
	}

	public long getGlobalContextId() {
		return globalContextId;
	}

	public void setGlobalContextId(long globalContext) {
		this.globalContextId = globalContext;
	}

	public long getProblemContextId() {
		return problemContextId;
	}

	public void setProblemContextId(long problemContext) {
		this.problemContextId = problemContext;
	}

	public long getRootCauseId() {
		return rootCauseId;
	}

	public void setRootCauseId(long rootCause) {
		this.rootCauseId = rootCause;
	}
}
