package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclableFactory;

public class StackIdentityPipeline<T extends NativeType<T>, A extends ArrayDataAccess<A>>	implements
																							SameTypeStackProcessingPipeline<T, A>
{

	private ObjectVariable<StackInterface<T, A>> mStackVariable = new ObjectVariable<StackInterface<T, A>>("StackVariable");

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
	public ObjectVariable<StackInterface<T, A>> getInputVariable()
	{
		return mStackVariable;
	}

	@Override
	public ObjectVariable<StackInterface<T, A>> getOutputVariable()
	{
		return mStackVariable;
	}

	@Override
	public void addStackProcessor(	SameTypeStackProcessorInterface<T, A> pStackProcessor,
									RecyclableFactory<StackInterface<T, A>, StackRequest<T>> pStackFactory,
									int pMaximumNumberOfObjects)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStackProcessor(SameTypeStackProcessorInterface<T, A> pStackProcessor)
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
