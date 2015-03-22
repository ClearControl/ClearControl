package rtlib.cameras.devices.sim;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;

import java.util.concurrent.TimeUnit;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.cameras.StackCameraDeviceBase;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.device.SimulatorDeviceInterface;
import rtlib.core.log.Loggable;
import rtlib.core.variable.booleanv.BooleanEventListenerInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.server.StackSourceInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class StackCameraDeviceSimulator<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																																StackCameraDeviceBase<T, A>	implements
																																																														Loggable,
																																																														SimulatorDeviceInterface<StackCameraDeviceSimulatorHint>
{
	private StackCameraDeviceSimulatorHint mHint;
	private StackSourceInterface<T, A> mStackSource;
	private BooleanVariable mTriggerVariable;
	protected volatile long mCurrentStackIndex = 0;
	private RecyclerInterface<StackInterface<T, A>, StackRequest<T>> mRecycler;
	private final T mType;

	public StackCameraDeviceSimulator(StackSourceInterface<T, A> pStackSource,
																		T pType,
																		BooleanVariable pTriggerVariable)
	{
		super("StackCameraSimulator");
		mStackSource = pStackSource;
		mType = pType;
		mTriggerVariable = pTriggerVariable;

		mLineReadOutTimeInMicrosecondsVariable = new DoubleVariable("LineReadOutTimeInMicroseconds",
																																1);
		mFrameBytesPerPixelVariable = new DoubleVariable(	"FrameBytesPerPixel",
																											2);
		mFrameWidthVariable = new DoubleVariable("FrameWidth", 320);
		mFrameHeightVariable = new DoubleVariable("FrameHeight", 320);
		mFrameDepthVariable = new DoubleVariable("FrameDepth", 100);
		mExposureInMicrosecondsVariable = new DoubleVariable(	"ExposureInMicroseconds",
																													1000);
		mPixelSizeinNanometersVariable = new DoubleVariable("PixelSizeinNanometers",
																												160);

		mStackReference = new ObjectVariable<>("StackReference");

		if (mTriggerVariable == null)
		{
			error("cameras",
						"Cannot instantiate " + StackCameraDeviceSimulator.class.getSimpleName()
								+ " because trigger variable is null!");
			return;
		}

		mTriggerVariable.addEdgeListener(new BooleanEventListenerInterface()
		{

			@Override
			public void fire(boolean pCurrentBooleanValue)
			{
				final long lWaitTimeMicroseconds = (long) mExposureInMicrosecondsVariable.getValue();
				ThreadUtils.sleep(lWaitTimeMicroseconds,
													TimeUnit.MICROSECONDS);

				if (pCurrentBooleanValue)
				{
					StackInterface<T, A> lStack;
					if (mStackSource != null)
					{
						lStack = mStackSource.getStack(mCurrentStackIndex);
						mCurrentStackIndex = (mCurrentStackIndex + 1) % mStackSource.getNumberOfStacks();
					}
					else
					{
						try
						{
							lStack = generateSyntheticStack();
						}
						catch (final Throwable e)
						{
							e.printStackTrace();
							return;
						}
						mCurrentStackIndex++;
					}
					if (lStack == null)
					{
						System.err.println("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
						return;
					}
					mStackReference.set(lStack);

				}
			}
		});

		final ContiguousOffHeapPlanarStackFactory<T, A> lContiguousOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<T, A>();

		mRecycler = new BasicRecycler<StackInterface<T, A>, StackRequest<T>>(	lContiguousOffHeapPlanarStackFactory,
																																					20);

	}

	protected StackInterface<T, A> generateSyntheticStack() throws IncompatibleTypeException
	{
		final int lWidth = (int) max(16, mFrameWidthVariable.getValue());
		final int lHeight = (int) max(16, mFrameHeightVariable.getValue());
		final int lDepth = (int) max(16, mFrameDepthVariable.getValue());

		if (lWidth * lHeight * lDepth <= 0)
			return null;

		final StackRequest<T> lStackRequest = StackRequest.build(	mType,
																															lWidth,
																															lHeight,
																															lDepth);

		final StackInterface<T, A> lStack = mRecycler.getOrWait(1,
																														TimeUnit.SECONDS,
																														lStackRequest);

		// mRecycler.printDebugInfo();
		System.out.println(lStackRequest.toString());

		if (mHint == null || mHint.type.equals("normal"))
		{
			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
			final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

			for (int z = 0; z < lDepth; z++)
				for (int y = 0; y < lHeight; y++)
					for (int x = 0; x < lWidth; x++)
					{
						int lValueValue = (((byte) x ^ (byte) y ^ (byte) z ^ ((byte) mCurrentStackIndex)));
						if (lValueValue < 12)
							lValueValue = 0;
						lContiguousBuffer.writeShort((short) lValueValue);
					}
		}
		else if (mHint.type.equals("autofocus"))
		{
			final double lInFocusZ = mHint.focusz;

			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
			final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);
			for (int z = 0; z < lDepth; z++)
			{
				final double lNormalizedZ = (1.0 * z) / lDepth;
				final double lFocalDistance = abs(lNormalizedZ - lInFocusZ);
				final double lIntensity = 1 / (1 + 10 * lFocalDistance);
				final double lFrequency = 0.1 * (1 - lFocalDistance);
				for (int y = 0; y < lHeight; y++)
					for (int x = 0; x < lWidth; x++)
					{
						final int lValueValue = (int) (128 * lIntensity * (1 + cos(x * lFrequency)));
						// System.out.println(lValueValue);
						lContiguousBuffer.writeShort((short) lValueValue);
					}
			}

			/*final RandomAccessibleInterval<T> lImage = lStack.getImage();

			for (int z = 0; z < lDepth; z++)
			{
				@SuppressWarnings("rawtypes")
				final IntervalView lHyperSlice = Views.hyperSlice(lImage,
																													2,
																													z);

				@SuppressWarnings(
				{ "rawtypes", "unchecked" })
				final RandomAccessible lInfiniteImg = Views.extendValue(lHyperSlice,
																																mType);

				final double lNormalizedZ = 1.0 * z / lDepth;
				final double lFocalDistance = abs(lNormalizedZ - lInFocusZ);
				Gauss3.gauss(20 * lFocalDistance, lInfiniteImg, lHyperSlice);
			}/**/

		}

		return lStack;
	}

	@Override
	public void reopen()
	{
		return;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public void trigger()
	{

	}

	@Override
	public void giveHint(StackCameraDeviceSimulatorHint pHint)
	{
		mHint = pHint;
	}

}
