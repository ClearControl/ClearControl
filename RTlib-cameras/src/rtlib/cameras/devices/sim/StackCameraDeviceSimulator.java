package rtlib.cameras.devices.sim;

import rtlib.cameras.StackCameraDeviceBase;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.stack.server.StackSourceInterface;

public class StackCameraDeviceSimulator<I, O> extends
																							StackCameraDeviceBase<I, O>
{



	private StackSourceInterface<I> mStackSource;
	private DoubleVariable mTriggervariable;

	public StackCameraDeviceSimulator(StackSourceInterface<I> pStackSource, DoubleVariable pTriggervariable)
	{
		super("StackCameraSimulator");
		mStackSource = pStackSource;
		mTriggervariable = pTriggervariable;

		mFrameBytesPerPixelVariable = new DoubleVariable(	"FrameBytesPerPixel",
																											2);
		mFrameWidthVariable = new DoubleVariable("FrameWidth", 320);
		mFrameHeightVariable = new DoubleVariable("FrameHeight", 320);
		mFrameDepthVariable = new DoubleVariable("FrameDepth", 100);
		mExposureInMicrosecondsVariable = new DoubleVariable(	"ExposureInMicroseconds",
																									1000);
		mPixelSizeinNanometersVariable = new DoubleVariable("PixelSizeinNanometers",
																								160);
		
		// TODO: implement simulator
	}

	@Override
	public void reopen()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean start()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
