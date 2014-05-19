package rtlib.core.concurrent.asyncprocs;

import java.io.Closeable;

public interface AsynchronousProcessorInterface<I, O> extends
																											ProcessorInterface<I, O>,
																											Closeable
{

	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor);

	public boolean start();

	public boolean passOrWait(I pObject);

	public boolean passOrFail(I pObject);

	public boolean waitToFinish(final int pPollInterval);

	public boolean stop();

	public int getInputQueueLength();

	public int getRemainingCapacity();

}
