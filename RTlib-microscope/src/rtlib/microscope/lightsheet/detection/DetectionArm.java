package rtlib.microscope.lightsheet.detection;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;

public class DetectionArm extends NamedVirtualDevice implements
																										DetectionArmInterface
{

	private String mName;

	private final DoubleVariable mDetectionFocusZ = new DoubleVariable(	"FocusZ",
																																			0);

	private final ObjectVariable<UnivariateAffineComposableFunction> mDetectionZFunction = new ObjectVariable<>("DetectionZFunction",
																																																							new UnivariateAffineFunction());

	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z",
																																				0);

	private final int mStaveIndex;



	public DetectionArm(String pName, int pStaveIndex)
	{
		super(pName + pStaveIndex);
		mName = pName;

		reset();

		final VariableSetListener<Double> lDoubleVariableListener = (u, v) -> {
			update();
		};

		final VariableSetListener<UnivariateAffineComposableFunction> lObjectVariableListener = (	u,
																																															v) -> {
			update();
		};

		mDetectionFocusZ.addSetListener(lDoubleVariableListener);
		mDetectionZFunction.addSetListener(lObjectVariableListener);

		mStaveIndex = pStaveIndex;

	}

	public void reset()
	{
		final double lA = MachineConfiguration.getCurrentMachineConfiguration()
																					.getDoubleProperty(	"device.lsm.detection." + mName
																																	+ ".sa",
																															0.5);

		final double lB = MachineConfiguration.getCurrentMachineConfiguration()
																					.getDoubleProperty(	"device.lsm.detection." + mName
																																	+ ".sb",
																															0.5);

		mDetectionZFunction.set(new UnivariateAffineFunction(lA, lB));
	}

	public DetectionArm(String pName)
	{
		this(	pName,
					MachineConfiguration.getCurrentMachineConfiguration()
															.getIntegerProperty("device.lsm.detection." + pName
																											+ ".index.z",
																									0));

	}

	@Override
	public DoubleVariable getDetectionFocusZInMicronsVariable()
	{
		return mDetectionFocusZ;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getDetectionFocusZFunction()
	{
		return mDetectionZFunction;
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
		pExposureMovement.setStave(mStaveIndex, mDetectionPathStaveZ);
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
