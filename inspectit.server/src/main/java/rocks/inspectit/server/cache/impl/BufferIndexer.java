package rocks.inspectit.server.cache.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rocks.inspectit.server.cache.IBuffer;

/**
 * Thread that invokes the {@link IBuffer#indexNext()} method constantly.
 * 
 * @author Ivan Senic
 * 
 */
@Component
public class BufferIndexer extends BufferWorker {

	/**
	 * Default constructor. Just calls super class constructor.
	 * 
	 * @param buffer
	 *            Buffer to work on.
	 */
	public BufferIndexer() {
		super("buffer-indexing-thread");
		setPriority(NORM_PRIORITY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void work() throws InterruptedException {
		// System.out.println(((AtomicBuffer) getBuffer()).name + "-> Indexer: "
		// + ((RootBranch) ((AtomicBuffer) getBuffer()).indexingTree).name);
		getBuffer().indexNext();
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
