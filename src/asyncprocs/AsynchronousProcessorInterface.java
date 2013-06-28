package asyncprocs;

import java.io.Closeable;

public interface AsynchronousProcessorInterface<I, O> extends
																											ProcessorInterface<I, O>,
																											Closeable
{

	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor);

	public boolean start();
	
	public boolean passOrWait(I pObject);

	public boolean passOrFail(I pObject);
	
	public int getInputQueueLength();
	
	public void waitToFinish(final int pPollInterval);

	public boolean stop();







}
