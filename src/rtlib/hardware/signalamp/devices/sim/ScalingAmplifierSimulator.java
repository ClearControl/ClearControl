package rtlib.hardware.signalamp.devices.sim;

import rtlib.core.variable.Variable;
import rtlib.hardware.signalamp.ScalingAmplifierBaseDevice;
import rtlib.hardware.signalamp.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierSimulator extends
																			ScalingAmplifierBaseDevice implements
																																ScalingAmplifierDeviceInterface
{

	public ScalingAmplifierSimulator(String pDeviceName)
	{
		super(pDeviceName);

		mGainVariable = new Variable<Number>("Gain", 1.0);
		mGainVariable.addSetListener((o,n)->{System.out.println(getName()+": new gain: "+n);});
		
		mOffsetVariable = new Variable<Number>("Offset", 0.0);
		mOffsetVariable.addSetListener((o,n)->{System.out.println(getName()+": new offset: "+n);});
		
		mMinGain= -19.9;
		mMaxGain= 19.9;
		
		mMinOffset= 0;
		mMaxOffset= 10;
		
	}

}
