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

	private final BoundedVariable<Number> mWidth = new BoundedVariable<Number>(	"Width",
																																							0.0);

	private final BoundedVariable<Number> mHeight = new BoundedVariable<Number>("Height",
																																							0.0);

	private final Variable<UnivariateAffineFunction> mZFunction = new Variable<>(	"DetectionZFunction",
																																								new UnivariateAffineFunction());

	private final Variable<UnivariateAffineFunction> mWidthFunction = new Variable<>(	"DetectionWidthFunction",
																																										new UnivariateAffineFunction());

	private final Variable<UnivariateAffineFunction> mHeightFunction = new Variable<>("DetectionHeightFunction",
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

		if (mWidthFunction.get() != null && mWidthFunction.get()
																											.hasInverse())
		{
			final long lMaxStackWidthInPixels = StackCameraDevice.getStackMaxWidthVariable()
																														.get()
																														.longValue();
			final long lMaxStackHeightInPixels = StackCameraDevice.getStackMaxHeightVariable()
																														.get()
																														.longValue();

			UnivariateAffineFunction lWidthInverse = mWidthFunction.get()
																															.inverse();
			UnivariateAffineFunction lHeightInverse = mHeightFunction.get()
																																.inverse();

			final long lMaxStackWidth = (long) round(lWidthInverse.value(lMaxStackWidthInPixels));
			final long lMaxStackHeight = (long) round(lHeightInverse.value(lMaxStackHeightInPixels));

			mWidth.setMinMax(lMaxStackWidth, lMaxStackHeight);
		}

		// Changes in Stack camera width and height are propagated to this detection
		// arm device.
		final VariableSetListener<Long> lCameraWidthHeightVariableListener = (o,
																																					n) -> {
			if (!o.equals(n))
			{
				if (mWidthFunction.get() != null && mWidthFunction.get()
																													.hasInverse()
						&& mHeightFunction.get() != null
						&& mHeightFunction.get().hasInverse())
				{
					UnivariateAffineFunction lWidthInverse = mWidthFunction.get()
																																	.inverse();
					UnivariateAffineFunction lHeightInverse = mHeightFunction.get()
																																		.inverse();

					final long lWidthInPixels = mStackCameraDevice.getStackWidthVariable()
																												.get();
					final long lHeightInPixels = mStackCameraDevice.getStackHeightVariable()
																													.get();

					final long lWidthInMicrons = (long) Math.round(lWidthInverse.value(lWidthInPixels));
					final long lHeightInMicrons = (long) Math.round(lHeightInverse.value(lHeightInPixels));

					mWidth.set(lWidthInMicrons);
					mHeight.set(lHeightInMicrons);
				}

			}
		};

		mStackCameraDevice.getStackWidthVariable()
											.addSetListener(lCameraWidthHeightVariableListener);
		mStackCameraDevice.getStackHeightVariable()
											.addSetListener(lCameraWidthHeightVariableListener);

		@SuppressWarnings("rawtypes")
		final VariableSetListener lVariableListener = (o, n) -> {
			// System.out.println(getName() + ": new Z value: " + n);
			update();
			notifyChange();
		};

		mWidth.addSetListener(lVariableListener);
		mHeight.addSetListener(lVariableListener);
		mDetectionFocusZ.addSetListener(lVariableListener);

		final VariableSetListener<UnivariateAffineFunction> lFunctionVariableListener = (	o,
																																											n) -> {
			System.out.println(getName() + ": new Z function: " + n);
			resetBounds();
			update();
			notifyChange();
		};

		mZFunction.addSetListener(lFunctionVariableListener);

		int lStaveIndex = MachineConfiguration.getCurrentMachineConfiguration()
																					.getIntegerProperty("device.lsm.detection." + getName()
																																	+ ".z.index",
																															0);

		mStaveIndex = lStaveIndex;

		update();
		notifyChange();
	}

	@Override
	public void resetFunctions()
	{
		mWidthFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.detection." + getName()
																																					+ ".width.f"));

		mHeightFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.detection." + getName()
																																					+ ".height.f"));

		mZFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.detection." + getName()
																																			+ ".z.f"));

	}

	@Override
	public void resetBounds()
	{

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.detection." + getName()
																									+ ".width.bounds",
																							mWidth,
																							mWidthFunction.get());

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.detection." + getName()
																									+ ".height.bounds",
																							mHeight,
																							mHeightFunction.get());

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

	@Override
	public BoundedVariable<Number> getWidthVariable()
	{
		return mWidth;
	}

	@Override
	public BoundedVariable<Number> getHeightVariable()
	{
		return mHeight;
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

			final long lWidthInMicrons = mWidth.get().longValue();
			final long lHeightInMicrons = mHeight.get().longValue();

			final long lWidth = (long) Math.round(mWidthFunction.get()
																													.value(lWidthInMicrons));
			final long lHeight = (long) Math.round(mHeightFunction.get()
																														.value(lHeightInMicrons));

			mStackCameraDevice.getStackWidthVariable().set(lWidth);
			mStackCameraDevice.getStackHeightVariable().set(lHeight);
		}
	}
}
