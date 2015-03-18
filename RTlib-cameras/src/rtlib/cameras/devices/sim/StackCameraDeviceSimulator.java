package rtlib.cameras.devices.sim;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.cameras.StackCameraDeviceBase;
import rtlib.core.log.Loggable;
import rtlib.core.variable.booleanv.BooleanEventListenerInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.server.StackSourceInterface;

public class StackCameraDeviceSimulator<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																																StackCameraDeviceBase<T, A>	implements
																																																														Loggable
{

	private StackSourceInterface<T, A> mStackSource;
	private BooleanVariable mTriggerVariable;
	protected long mCurrentStackIndex = 0;

	public StackCameraDeviceSimulator(StackSourceInterface<T, A> pStackSource,
																		BooleanVariable pTriggerVariable)
	{
		super("StackCameraSimulator");
		mStackSource = pStackSource;
		mTriggerVariable = pTriggerVariable;

		mLineReadOutTimeInMicrosecondsVariable = new DoubleVariable("LineReadOutTimeInMicroseconds",
																																1);
		mFrameBytesPerPixelVariable = new DoubleVariable(	"FrameBytesPerPixel",
																											2);
		mFrameWidthVariable = new DoubleVariable("FrameWidth", 320);
		mFrameHeightVariable = new DoubleVariable("FrameHeight", 320);
		mFrameDepthVariable = new DoubleVariable("FrameDepth", 100);
		mExposureInMicrosecondsVariable = new DoubleVariable(	"ExposureInMicroseconds",
																													1000);
		mPixelSizeinNanometersVariable = new DoubleVariable("PixelSizeinNanometers",
																												160);

		mStackReference = new ObjectVariable<>("StackReference");

		if (mTriggerVariable == null)
		{
			error("cameras",
						"Cannot instantiate " + StackCameraDeviceSimulator.class.getSimpleName()
								+ " because trigger variable is null!");
			return;
		}

		mTriggerVariable.addEdgeListener(new BooleanEventListenerInterface()
		{

			@Override
			public void fire(boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					final StackInterface<T, A> lStack = mStackSource.getStack(mCurrentStackIndex);
					mStackReference.set(lStack);
					mCurrentStackIndex = (mCurrentStackIndex + 1) % mStackSource.getNumberOfStacks();
				}
			}
		});

	}

	@Override
	public void reopen()
	{
		return;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

}
