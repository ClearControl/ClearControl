package rtlib.stack.processor;

public interface StackProcessing<I, O>
{

	public void addStackProcessor(final StackProcessorInterface<I, O> pStackProcessor);

	public void removeStackProcessor(final StackProcessorInterface<I, O> pStackProcessor);

}
