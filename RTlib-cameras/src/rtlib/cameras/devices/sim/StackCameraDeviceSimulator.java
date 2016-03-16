package rtlib.cameras.devices.sim;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.cameras.StackCameraDeviceBase;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.WaitingScheduledFuture;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.device.SimulatorDeviceInterface;
import rtlib.core.log.Loggable;
import rtlib.core.variable.types.booleanv.BooleanEventListenerInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
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
																															SimulatorDeviceInterface<StackCameraDeviceSimulatorHint>,
																															AsynchronousSchedulerServiceAccess
{
	private StackCameraDeviceSimulatorHint mHint;
	private StackSourceInterface<T, A> mStackSource;
	private BooleanVariable mTriggerVariable;
	protected volatile long mCurrentStackIndex = 0;
	private RecyclerInterface<StackInterface<T, A>, StackRequest<T>> mRecycler;
	private final T mType;

	private volatile CountDownLatch mLeftInQueue;
	private WaitingScheduledFuture<?> mTriggerScheduledAtFixedRate;

	public StackCameraDeviceSimulator(	StackSourceInterface<T, A> pStackSource,
										T pType,
										BooleanVariable pTriggerVariable)
	{
		super("StackCameraSimulator");
		mStackSource = pStackSource;
		mType = pType;
		mTriggerVariable = pTriggerVariable;

		mLineReadOutTimeInMicrosecondsVariable = new DoubleVariable("LineReadOutTimeInMicroseconds",
																	1);
		mStackBytesPerPixelVariable = new DoubleVariable(	"FrameBytesPerPixel",
															2);
		mStackWidthVariable = new DoubleVariable("FrameWidth", 320);
		mStackHeightVariable = new DoubleVariable("FrameHeight", 320);
		mStackDepthVariable = new DoubleVariable("FrameDepth", 100);
		mExposureInMicrosecondsVariable = new DoubleVariable(	"ExposureInMicroseconds",
																1000);
		mPixelSizeinNanometersVariable = new DoubleVariable("PixelSizeinNanometers",
															160);

		mStackReference = new ObjectVariable<>("StackReference");

		if (mTriggerVariable == null)
		{
			severe(	"cameras",
					"Cannot instantiate " + StackCameraDeviceSimulator.class.getSimpleName()
							+ " because trigger variable is null!");
			return;
		}

		mTriggerVariable.addEdgeListener(new BooleanEventListenerInterface()
		{

			@Override
			public void fire(boolean pCurrentBooleanValue)
			{
				final long lExposuretimeInMicroSeconds = (long) mExposureInMicrosecondsVariable.getValue();
				final long lDepth = (long) mStackDepthVariable.getValue();
				final long lWaitTimeMicroseconds = lExposuretimeInMicroSeconds * lDepth;
				ThreadUtils.sleep(	lWaitTimeMicroseconds,
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

					lStack.setNumberOfImagesPerPlane((long) getNumberOfImagesPerPlaneVariable().getValue());
					mStackReference.set(lStack);

					if (mLeftInQueue != null)
						mLeftInQueue.countDown();

				}
			}
		});

		final ContiguousOffHeapPlanarStackFactory<T, A> lContiguousOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<T, A>();

		mRecycler = new BasicRecycler<StackInterface<T, A>, StackRequest<T>>(	lContiguousOffHeapPlanarStackFactory,
																				20);

	}

	protected StackInterface<T, A> generateSyntheticStack() throws IncompatibleTypeException
	{
		final int lWidth = (int) max(	16,
										mStackWidthVariable.getValue());
		final int lHeight = (int) max(	16,
										mStackHeightVariable.getValue());
		int lDepth = (int) max(16, mStackDepthVariable.getValue());
		if (mHint != null && mHint.type.startsWith("autofocus.angle"))
			lDepth = mHint.nbangles;

		final int lNumberOfImagesPerPlane = (int) getNumberOfImagesPerPlaneVariable().getValue();

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
		// System.out.println(lStackRequest.toString());

		final byte time = (byte) mCurrentStackIndex;
		if (mHint == null || mHint.type.startsWith("normal"))
		{
			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
			final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

			for (int z = 0; z < lDepth; z++)
				for (int y = 0; y < lHeight; y++)
					for (int x = 0; x < lWidth; x++)
					{
						int lValueValue = (((byte) (x + time) ^ (byte) (cos(lNumberOfImagesPerPlane * x
																			+ (z % lNumberOfImagesPerPlane)))
											^ (byte) z ^ (time)));/**/
						if (lValueValue < 32)
							lValueValue = 0;
						lContiguousBuffer.writeShort((short) lValueValue);
					}
		}
		else if (mHint != null && mHint.type.startsWith("autofocus"))
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
		final Runnable lRunnable = () -> {
			trigger();
		};
		mTriggerScheduledAtFixedRate = scheduleAtFixedRate(	lRunnable,
															(long) getExposureInMicrosecondsVariable().getValue(),
															TimeUnit.MICROSECONDS);
		return true;
	}

	@Override
	public boolean stop()
	{
		if (mTriggerScheduledAtFixedRate != null)
			mTriggerScheduledAtFixedRate.cancel(false);
		return true;
	}

	@Override
	public Future<Boolean> playQueue()
	{
		mLeftInQueue = new CountDownLatch(getQueueLength());

		mStackDepthVariable.setValue(mStackDepthVariable.getValue() + 1);

		final Future<Boolean> lFuture = new Future<Boolean>()
		{

			@Override
			public boolean cancel(boolean pMayInterruptIfRunning)
			{
				return false;
			}

			@Override
			public boolean isCancelled()
			{
				return false;
			}

			@Override
			public boolean isDone()
			{
				return false;
			}

			@Override
			public Boolean get() throws InterruptedException,
								ExecutionException
			{
				// mLeftInQueue.await();
				return true;
			}

			@Override
			public Boolean get(long pTimeout, TimeUnit pUnit)	throws InterruptedException,
																ExecutionException,
																TimeoutException
			{
				// mLeftInQueue.await(pTimeout, pUnit);
				return true;
			}
		};

		return lFuture;
	}

	@Override
	public void trigger()
	{
		mTriggerVariable.setEdge(true);
	}

	public BooleanVariable getTriggerVariable()
	{
		return mTriggerVariable;
	}

	@Override
	public void giveHint(StackCameraDeviceSimulatorHint pHint)
	{
		mHint = pHint;
	}



}
