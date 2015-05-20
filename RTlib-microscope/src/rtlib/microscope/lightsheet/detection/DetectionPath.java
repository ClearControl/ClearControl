package rtlib.microscope.lightsheet.detection;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.core.variable.VariableListenerAdapter;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;

public class DetectionPath extends NamedVirtualDevice	implements
																											DetectionPathInterface
{

	private final DoubleVariable mDetectionFocusZ = new DoubleVariable(	"FocusZ",
																																			0);
	
	private final ObjectVariable<UnivariateFunction> mDetectionZFunction = new ObjectVariable<UnivariateFunction>("DetectionZFunction",
																																																									new UnivariateAffineFunction());

	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z.be",
																																				0);

	public DetectionPath(String pName)
	{
		super(pName);

		final VariableListenerAdapter<Double> lVariableListener = new VariableListenerAdapter<Double>()
		{

			@Override
			public void setEvent(Double pCurrentValue, Double pNewValue)
			{
				update();

			}

		};

		final VariableListenerAdapter<UnivariateFunction> lFunctionListener = new VariableListenerAdapter<UnivariateFunction>()
		{

			@Override
			public void setEvent(	UnivariateFunction pCurrentValue,
														UnivariateFunction pNewValue)
			{
				update();

			}
		};

		mDetectionFocusZ.addListener(lVariableListener);
		mDetectionZFunction.addListener(lFunctionListener);


	}

	public DoubleVariable getDetectionFocusZ()
	{
		return mDetectionFocusZ;
	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.detection." + getName().toLowerCase()
																																													+ ".index.z",
																																											1),
																			mDetectionPathStaveZ);

	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.detection." + getName().toLowerCase()
																																										+ ".index.z",
																																								1),
																mDetectionPathStaveZ);

	}

	public void update()
	{
		synchronized (this)
		{
			mDetectionPathStaveZ.setValue((float) mDetectionZFunction.get()
																																.value(mDetectionFocusZ.getValue()));
		}
	}
}
