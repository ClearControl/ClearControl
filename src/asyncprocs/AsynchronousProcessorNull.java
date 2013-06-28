package asyncprocs;

public class AsynchronousProcessorNull<I,O> extends AsynchronousProcessorBase<I,O> implements
																																		AsynchronousProcessorInterface<I,O>
{

	

	public AsynchronousProcessorNull(String pName, int pMaxQueueSize)
	{
		super(pName, pMaxQueueSize);
	}
	
	
	@Override
	public O process(I pInput)
	{
		//Example: here is where the logic happens, here nothing happens and it returns null
		return null;
	}


	@Override
	public void waitToFinish(final int pPollInterval)
	{
		
	}




}
