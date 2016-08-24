package clearcontrol.microscope.lightsheet.component.detection;

import static java.lang.Math.round;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.staves.ConstantStave;

public class DetectionArm extends VirtualDevice	implements
																								DetectionArmInterface
{

	private StackCameraDeviceInterface mStackCameraDevice;

	private final BoundedVariable<Number> mDetectionFocusZ = new BoundedVariable<Number>(	"FocusZ",
																																												0.0);


	private final Variable<UnivariateAffineFunction> mZFunction = new Variable<>(	"DetectionZFunction",
																																								new UnivariateAffineFunction());


	private final ConstantStave mDetectionPathStaveZ = new ConstantStave(	"detection.z",
																																				0);

	private final int mStaveIndex;

	@SuppressWarnings("unchecked")
	public DetectionArm(String pName,
											StackCameraDeviceInterface StackCameraDevice)
	{
		super(pName);
		mStackCameraDevice = StackCameraDevice;

		resetFunctions();
		resetBounds();

	


		@SuppressWarnings("rawtypes")
		final VariableSetListener lVariableListener = (o, n) -> {
			// System.out.println(getName() + ": new Z value: " + n);
			update();
			notifyListeners(this);
		};


		mDetectionFocusZ.addSetListener(lVariableListener);

		final VariableSetListener<UnivariateAffineFunction> lFunctionVariableListener = (	o,
																																											n) -> {
			System.out.println(getName() + ": new Z function: " + n);
			resetBounds();
			update();
			notifyListeners(this);
		};

		mZFunction.addSetListener(lFunctionVariableListener);

		int lStaveIndex = MachineConfiguration.getCurrentMachineConfiguration()
																					.getIntegerProperty("device.lsm.detection." + getName()
																																	+ ".z.index",
																															0);

		mStaveIndex = lStaveIndex;

		update();
		notifyListeners(this);
	}

	@Override
	public void resetFunctions()
	{
	
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

	/**
	 * Updates underlying signal generation staves and stack camera configuration.
	 */
	private void update()
	{
		synchronized (this)
		{
			double lZFocus = mDetectionFocusZ.get().doubleValue();
			float lZFocusTransformed = (float) mZFunction.get()
																										.value(lZFocus);
			mDetectionPathStaveZ.setValue(lZFocusTransformed);

		}
	}
}
