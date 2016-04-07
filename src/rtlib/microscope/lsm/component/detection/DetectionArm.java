package rtlib.microscope.lsm.component.detection;

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

	private int mDeviceIndex;

	private final DoubleVariable mDetectionFocusZ = new DoubleVariable(	"FocusZ",
																																			0);

	private final ObjectVariable<UnivariateAffineComposableFunction> mZFunction = new ObjectVariable<>(	"DetectionZFunction",
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
																															-1); 

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
	public DoubleVariable getZVariable()
	{
		return mDetectionFocusZ;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getZFunction()
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
																											.value(mDetectionFocusZ.getValue()));
		}
	}
}
