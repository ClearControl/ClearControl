package rtlib.microscope.lightsheet.detection;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
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

	private int mStaveIndex;

	public DetectionPath(String pName)
	{
		this(	pName,
					MachineConfiguration.getCurrentMachineConfiguration()
															.getIntegerProperty("device.lsm.detection." + pName
		        																										+ ".index.z",
		        																										0));

	}

	public DetectionPath(String pName, int pStaveIndex)
	{
		super(pName + pStaveIndex);

		final VariableSetListener<Double> lDoubleVariableListener = (u, v) -> {
			update();
		};

		final VariableSetListener<UnivariateFunction> lObjectVariableListener = (	u,
																																							v) -> {
			update();
		};

		mDetectionFocusZ.addSetListener(lDoubleVariableListener);
		mDetectionZFunction.addSetListener(lObjectVariableListener);

		mStaveIndex = pStaveIndex;

	}

	public DoubleVariable getDetectionFocusZInMicronsVariable()
	{
		return mDetectionFocusZ;
	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	mStaveIndex,
																			mDetectionPathStaveZ);
	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		// Analog outputs at exposure:
		pExposureMovement.setStave(mStaveIndex,
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
