package rtlib.stack.server;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.BasicRecycler;

public class StackRAMServer<T extends NativeType<T>, A extends ArrayDataAccess<A>>	implements
																																										StackSinkInterface<T, A>,
																																										StackSourceInterface<T, A>
{

	ArrayList<StackInterface<T, A>> mStackList = new ArrayList<StackInterface<T, A>>();
	final TLongArrayList mStackTimePointList = new TLongArrayList();

	protected final VariableBundle mMetaDataVariableBundle = new VariableBundle("MetaData");

	public StackRAMServer()
	{
		super();
	}

	@Override
	public boolean update()
	{
		return true;
	}

	@Override
	public long getNumberOfStacks()
	{
		return mStackList.size();
	}

	@Override
	public void setStackRecycler(final BasicRecycler<StackInterface<T, A>, StackRequest<T>> pStackRecycler)
	{
	}

	@Override
	public StackInterface<T, A> getStack(	final long pStackIndex,
																				long pTime,
																				TimeUnit pTimeUnit)
	{
		return getStack(pStackIndex);
	}

	@Override
	public StackInterface<T, A> getStack(long pStackIndex)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public double getStackTimeStampInSeconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(final StackInterface<T, A> pStack)
	{
		mStackTimePointList.add(pStack.getTimeStampInNanoseconds());
		return mStackList.add(pStack);
	}
	
	@Override
	public void addMetaData(final String pPrefix, final double pValue)
	{
		mMetaDataVariableBundle.addVariable(new DoubleVariable(	pPrefix,
																																	pValue));
	}

	@Override
	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.addVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundle.removeAllVariables();
	}

	@Override
	public void removeMetaDataVariable(final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.removeVariable(pVariable);

	}


}
