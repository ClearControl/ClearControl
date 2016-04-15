package rtlib.stack.sourcesink;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<StackInterface> mStackList = new ArrayList<StackInterface>();
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
	public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
	{
	}

	@Override
	public StackInterface getStack(	final long pStackIndex,
																	long pTime,
																	TimeUnit pTimeUnit)
	{
		return getStack(pStackIndex);
	}

	@Override
	public StackInterface getStack(long pStackIndex)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public double getStackTimeStampInSeconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(final StackInterface pStack)
	{
		mStackTimePointList.add(pStack.getTimeStampInNanoseconds());
		return mStackList.add(pStack);
	}

	@Override
	public void addMetaData(final String pPrefix, final double pValue)
	{
		mMetaDataVariableBundle.addVariable(new Variable<Double>(	pPrefix,
																															pValue));
	}

	@Override
	public void addMetaDataVariable(final String pPrefix,
																	final Variable<?> pVariable)
	{
		mMetaDataVariableBundle.addVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundle.removeAllVariables();
	}

	@Override
	public void removeMetaDataVariable(final Variable<?> pVariable)
	{
		mMetaDataVariableBundle.removeVariable(pVariable);

	}

}
