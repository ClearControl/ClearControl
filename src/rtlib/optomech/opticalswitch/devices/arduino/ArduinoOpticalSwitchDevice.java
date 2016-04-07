package rtlib.optomech.opticalswitch.devices.arduino;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.SwitchingDeviceInterface;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.longv.LongVariable;
import rtlib.optomech.OptoMechDeviceInterface;
import rtlib.optomech.opticalswitch.devices.arduino.adapters.ArduinoOpticalSwitchPositionAdapter;
import rtlib.serial.SerialDevice;

public class ArduinoOpticalSwitchDevice extends SerialDevice implements
																														SwitchingDeviceInterface,
																														OptoMechDeviceInterface
{

	private final LongVariable mCommandVariable;

	private final BooleanVariable[] mLightSheetOnOff;

	private static final int cAllClosed = 0;
	private static final int cAllOpened = 100;

	public ArduinoOpticalSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"fiberswitch.optojena",
																										pDeviceIndex,
																										"NULL"));
	}

	public ArduinoOpticalSwitchDevice(final String pPortName)
	{
		super("ArduinoOpticalSwitch", pPortName, 250000);

		final ArduinoOpticalSwitchPositionAdapter lFiberSwitchPosition = new ArduinoOpticalSwitchPositionAdapter(this);

		mCommandVariable = (LongVariable) addSerialVariable("OpticalSwitchPosition",
																								lFiberSwitchPosition);

		mLightSheetOnOff = new BooleanVariable[4];

		final VariableSetListener<Boolean> lBooleanVariableListener = (	u,
																																		v) -> {

			int lCount = 0;
			for (int i = 0; i < mLightSheetOnOff.length; i++)
				if (mLightSheetOnOff[i].getBooleanValue())
					lCount++;

			if (lCount == 1)
			{
				for (int i = 0; i < mLightSheetOnOff.length; i++)
					if (mLightSheetOnOff[i].getBooleanValue())
						mCommandVariable.setValue(101 + i);
			}
			else
				for (int i = 0; i < mLightSheetOnOff.length; i++)
				{
					boolean lOn = mLightSheetOnOff[i].getBooleanValue();
					mCommandVariable.setValue((i + 1) * (lOn ? 1 : -1));
				}
		};

		for (int i = 0; i < mLightSheetOnOff.length; i++)
		{

			mLightSheetOnOff[i] = new BooleanVariable(String.format("LightSheet%dOnOff",
																															i),
																								false);
			mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);

		}

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		mCommandVariable.setValue(cAllClosed);

		return lIsOpened;
	}

	@Override
	public boolean close()
	{
		final boolean lIsClosed = super.close();
		mCommandVariable.setValue(cAllClosed);

		return lIsClosed;
	}

	@Override
	public int getNumberOfSwitches()
	{
		return 4;
	}

	@Override
	public BooleanVariable getSwitchingVariable(int pSwitchIndex)
	{
		return mLightSheetOnOff[pSwitchIndex];
	}

}
