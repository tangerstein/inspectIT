package rocks.inspectit.server.cache.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rocks.inspectit.server.cache.IBuffer;

/**
 * Thread that invokes the {@link IBuffer#evict()} method constantly.
 * 
 * @author Ivan Senic
 * 
 */
@Component
public class BufferEvictor extends BufferWorker {

	/**
	 * Default constructor. Just calls super class constructor.
	 * 
	 * @param buffer
	 *            Buffer to work on.
	 */
	public BufferEvictor() {
		super("buffer-evicting-thread");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void work() throws InterruptedException {
		getBuffer().evict();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PostConstruct
	public synchronized void start() {
		super.start();
	}

}
