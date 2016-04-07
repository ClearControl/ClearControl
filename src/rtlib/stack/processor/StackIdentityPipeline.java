package rtlib.stack.processor;

import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclableFactory;

public class StackIdentityPipeline implements StackProcessingPipeline
{

	private ObjectVariable<StackInterface> mStackVariable = new ObjectVariable<StackInterface>("StackVariable");

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
	public ObjectVariable<StackInterface> getInputVariable()
	{
		return mStackVariable;
	}

	@Override
	public ObjectVariable<StackInterface> getOutputVariable()
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
