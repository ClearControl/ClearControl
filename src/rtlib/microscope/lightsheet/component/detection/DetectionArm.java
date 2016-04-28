package rtlib.microscope.lightsheet.component.detection;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.device.name.NamedVirtualDevice;
import rtlib.hardware.signalgen.movement.Movement;
import rtlib.hardware.signalgen.staves.ConstantStave;

public class DetectionArm extends NamedVirtualDevice implements
																										DetectionArmInterface
{

	private final BoundedVariable<Number> mDetectionFocusZ = new BoundedVariable<Number>(	"FocusZ",
																																												0.0);

	private final Variable<UnivariateAffineFunction> mZFunction = new Variable<>(	"DetectionZFunction",
																																								new UnivariateAffineFunction());

	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z",
																																				0);

	private final int mStaveIndex;

	@SuppressWarnings("unchecked")
	public DetectionArm(String pName)
	{
		super(pName);

		resetFunctions();
		resetBounds();

		@SuppressWarnings("rawtypes")
		final VariableSetListener lVariableListener = (o, n) -> {
			System.out.println(getName() + ": new Z value: " + n);
			update();
		};

		mDetectionFocusZ.addSetListener(lVariableListener);
		
		
		final VariableSetListener<UnivariateAffineFunction> lFunctionVariableListener = (o, n) -> {
			System.out.println(getName() + ": new Z function: " + n);
			resetBounds();
			update();
		};
		
		mZFunction.addSetListener(lFunctionVariableListener);

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
	public void resetBounds()
	{

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.detection." + getName()
																									+ ".z.bounds",
																							mDetectionFocusZ,
																							mZFunction.get());

	}

	@Override
	public BoundedVariable<Number> getZVariable()
	{
		return mDetectionFocusZ;
	}

	@Override
	public Variable<UnivariateAffineFunction> getZFunction()
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
																											.value(mDetectionFocusZ.get()
																																							.doubleValue()));
		}
	}
}
