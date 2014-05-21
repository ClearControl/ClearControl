package rtlib.stack.processor;


public interface StackProcessing
{

	public void addStackProcessor(final StackProcessorInterface pStackProcessor);

	public void removeStackProcessor(final StackProcessorInterface pStackProcessor);

}
