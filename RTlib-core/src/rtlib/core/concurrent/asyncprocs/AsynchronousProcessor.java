package rtlib.core.concurrent.asyncprocs;


public class AsynchronousProcessor<I, O>	extends
																									AsynchronousProcessorBase<I, O>
{

	private ProcessorInterface<I, O> mProcessor;

	public AsynchronousProcessor(	String pName,
																int pMaxQueueSize,
																final ProcessorInterface<I, O> pProcessor)
	{
		super(pName, pMaxQueueSize);
		mProcessor = pProcessor;
	}

	@Override
	public O process(I pInput)
	{
		return mProcessor.process(pInput);
	}

}
