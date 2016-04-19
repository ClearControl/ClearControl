package rtlib.microscope.lsm.component.detection;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;
import rtlib.device.name.NamedVirtualDevice;
import rtlib.hardware.signalgen.movement.Movement;
import rtlib.hardware.signalgen.staves.ConstantStave;

public class DetectionArm extends NamedVirtualDevice implements
																										DetectionArmInterface
{

	private int mDeviceIndex;

	private final Variable<Double> mDetectionFocusZ = new Variable<Double>(	"FocusZ",
																																											0.0);

	private final Variable<UnivariateAffineComposableFunction> mZFunction = new Variable<>(	"DetectionZFunction",
																																																			new UnivariateAffineFunction());

	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z",
																																				0);

	private final int mStaveIndex;

	public DetectionArm(String pName)
	{
		super(pName);

		resetFunctions();

		final VariableSetListener<Double> lDoubleVariableListener = (u, v) -> {
			update();
		};

		final VariableSetListener<UnivariateAffineComposableFunction> lObjectVariableListener = (	u,
																																															v) -> {
			update();
		};

		mDetectionFocusZ.addSetListener(lDoubleVariableListener);
		mZFunction.addSetListener(lObjectVariableListener);

		int lStaveIndex = MachineConfiguration.getCurrentMachineConfiguration()
																					.getIntegerProperty("device.lsm.detection." + getName()
																																	+ ".z.index",
																															0);

		mStaveIndex = lStaveIndex;

	}

	@Override
	public void resetFunctions()
	{
		mZFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.detection." + getName()
																																			+ ".z.f"));
	}

	@Override
	public Variable<Double> getZVariable()
	{
		return mDetectionFocusZ;
	}

	@Override
	public Variable<UnivariateAffineComposableFunction> getZFunction()
	{
		return mZFunction;
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

	@Override
	public void update()
	{
		synchronized (this)
		{
			mDetectionPathStaveZ.setValue((float) mZFunction.get()
																											.value(mDetectionFocusZ.get()));
		}
	}
}
