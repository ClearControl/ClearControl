package asyncprocs;

import java.io.IOException;

public class AsynchronousProcessorAdapter<I,O>	implements
																					AsynchronousProcessorInterface<I, O>
{

	@Override
	public O process(I pInput)
	{
		return null;
	}

	@Override
	public void close() throws IOException
	{
	}

	@Override
	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean passOrWait(I pObject)
	{
		return true;
	}

	@Override
	public boolean passOrFail(I pObject)
	{
		return true;
	}

}
