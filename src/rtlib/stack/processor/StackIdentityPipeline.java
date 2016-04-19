package rtlib.stack.processor;

import coremem.recycling.RecyclableFactory;
import rtlib.core.variable.Variable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public class StackIdentityPipeline implements StackProcessingPipeline
{

	private Variable<StackInterface> mStackVariable = new Variable<StackInterface>("StackVariable");

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public Variable<StackInterface> getInputVariable()
	{
		return mStackVariable;
	}

	@Override
	public Variable<StackInterface> getOutputVariable()
	{
		return mStackVariable;
	}

	@Override
	public void addStackProcessor(StackProcessorInterface pStackProcessor,
																RecyclableFactory<StackInterface, StackRequest> pStackFactory,
																int pMaximumNumberOfObjects)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStackProcessor(StackProcessorInterface pStackProcessor)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Interface method implementation
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder lBuilder = new StringBuilder();
		lBuilder.append("StackIdentityPipeline [mStackVariable=");
		lBuilder.append(mStackVariable);
		lBuilder.append("]");
		return lBuilder.toString();
	}

}
