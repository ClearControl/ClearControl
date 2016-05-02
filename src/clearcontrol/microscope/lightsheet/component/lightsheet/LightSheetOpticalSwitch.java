package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.device.name.NamedVirtualDevice;
import clearcontrol.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.staves.ConstantStave;

public class LightSheetOpticalSwitch extends NamedVirtualDevice	implements
																																OpticalSwitchDeviceInterface
{

	private final Variable<Boolean>[] mLightSheetOnOff;
	private final ConstantStave[] mBitStave;
	private int[] mStaveIndex;

	public LightSheetOpticalSwitch(	String pName,
																	int pNumberOfLightSheets)
	{
		super(pName);

		reset();

		final VariableSetListener<Boolean> lBooleanVariableListener = (	u,
																																		v) -> {

			if (u != v)
				update();
		};

		mBitStave = new ConstantStave[pNumberOfLightSheets];
		mStaveIndex = new int[pNumberOfLightSheets];
		mLightSheetOnOff = new Variable[pNumberOfLightSheets];

		for (int i = 0; i < mBitStave.length; i++)
		{
			mStaveIndex[i] = MachineConfiguration.getCurrentMachineConfiguration()
																						.getIntegerProperty("device.lsm.switch." + getName()
																																		+ i
																																		+ ".index",
																																-1);
			mBitStave[i] = new ConstantStave("lightsheet.s." + i, 0);

			mLightSheetOnOff[i] = new Variable<Boolean>(String.format("LightSheet%dOnOff",
																																i),
																									false);
			mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);

		}

	}

	@Override
	public int getNumberOfSwitches()
	{
		return mLightSheetOnOff.length;
	}

	public void reset()
	{

	}

	@Override
	public Variable<Boolean> getSwitchVariable(int pLightSheetIndex)
	{
		return mLightSheetOnOff[pLightSheetIndex];
	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		// Analog outputs before exposure:
		for (int i = 0; i < mBitStave.length; i++)
		{
			pBeforeExposureMovement.setStave(mStaveIndex[i], mBitStave[i]);
		}
	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		// Analog outputs at exposure:
		for (int i = 0; i < mBitStave.length; i++)
		{
			pExposureMovement.setStave(mStaveIndex[i], mBitStave[i]);
		}
	}

	public void update()
	{
		synchronized (this)
		{
			for (int i = 0; i < mBitStave.length; i++)
			{
				mBitStave[i].setValue(mLightSheetOnOff[i].get() ? 1 : 0);
			}
		}
	}

	@Override
	public String getSwitchName(int pSwitchIndex)
	{
		return "light sheet " + pSwitchIndex;
	}

}
