package rtlib.microscope.lightsheetmicroscope.detection;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.UpdatableDevice;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;

public class DetectionPath extends UpdatableDevice implements
																											DetectionPathInterface
{

	private final DoubleVariable mDetectionFocusZ = new DoubleVariable(	"FocusZ",
																																			0);
	
	
	private final UnivariateFunction mDetectionZConversion = new UnivariateAffineFunction();

	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z.be",
																																				0);

	public DetectionPath(String pName)
	{
		super(pName);

	}

	public DoubleVariable getDetectionFocusZ()
	{
		return mDetectionFocusZ;
	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.detection." + getDeviceName().toLowerCase()
																																													+ ".index.z",
																																											1),
																			mDetectionPathStaveZ);

	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.detection." + getDeviceName().toLowerCase()
																																										+ ".index.z",
																																								1),
																mDetectionPathStaveZ);

	}

	@Override
	public void ensureIsUpToDate()
	{
		if (!isUpToDate())
		{
			synchronized (this)
			{
				mDetectionPathStaveZ.mValue = mDetectionZConversion.value(mDetectionFocusZ.getValue());
				requestUpdateAllStaves();
			  setUpToDate(false);
			}
		}
	}

	private void requestUpdateAllStaves()
	{
		mDetectionPathStaveZ.requestUpdate();
	}

}
