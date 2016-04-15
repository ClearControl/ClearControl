package rtlib.hardware.cameras.devices.sim;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.exception.IncompatibleTypeException;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.WaitingScheduledFuture;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.device.SimulatorDeviceInterface;
import rtlib.core.log.Loggable;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableEdgeListener;
import rtlib.hardware.cameras.StackCameraDeviceBase;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.sourcesink.StackSourceInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class StackCameraDeviceSimulator extends StackCameraDeviceBase	implements
																																			Loggable,
																																			SimulatorDeviceInterface<StackCameraDeviceSimulatorHint>,
																																			AsynchronousSchedulerServiceAccess
{
	private StackCameraDeviceSimulatorHint mHint;
	private StackSourceInterface mStackSource;
	private Variable<Boolean> mTriggerVariable;
	protected volatile long mCurrentStackIndex = 0;
	private RecyclerInterface<StackInterface, StackRequest> mRecycler;

	private volatile CountDownLatch mLeftInQueue;
	private WaitingScheduledFuture<?> mTriggerScheduledAtFixedRate;

	public StackCameraDeviceSimulator(StackSourceInterface pStackSource,
																		Variable<Boolean> pTriggerVariable)
	{
		super("StackCameraSimulator");
		mStackSource = pStackSource;
		mTriggerVariable = pTriggerVariable;

		mLineReadOutTimeInMicrosecondsVariable = new Variable<Double>("LineReadOutTimeInMicroseconds",
																																	1.0);
		mStackBytesPerPixelVariable = new Variable<Long>(	"FrameBytesPerPixel",
																											2L);
		mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);
		mStackWidthVariable.addSetListener((o,n) ->{System.out.println(getName()+": New camera width: "+n);});
		
		mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);
		mStackHeightVariable.addSetListener((o,n) ->{System.out.println(getName()+": New camera height: "+n);});
		

		mStackMaxWidthVariable = new Variable<Long>("FrameMaxWidth",
																								2048L);
		mStackMaxHeightVariable = new Variable<Long>(	"FrameMaxHeight",
																									2048L);

		mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);
		mStackDepthVariable.addSetListener((o,n) ->{System.out.println(getName()+": New camera stack depth: "+n);});
		
		
		mExposureInMicrosecondsVariable = new Variable<Double>(	"ExposureInMicroseconds",
																														1000.0);
		mExposureInMicrosecondsVariable.addSetListener((o,n) ->{System.out.println(getName()+": New camera exposure: "+n);});
		
		mPixelSizeinNanometersVariable = new Variable<Double>("PixelSizeinNanometers",
																													160.0);

		mStackReference = new Variable<>("StackReference");

		if (mTriggerVariable == null)
		{
			severe(	"cameras",
							"Cannot instantiate " + StackCameraDeviceSimulator.class.getSimpleName()
									+ " because trigger variable is null!");
			return;
		}

		mTriggerVariable.addEdgeListener(new VariableEdgeListener<Boolean>()
		{

			@Override
			public void fire(Boolean pAfterEdge)
			{
				if (pAfterEdge)
				{

					final long lExposuretimeInMicroSeconds = mExposureInMicrosecondsVariable.get()
																																									.longValue();
					final long lDepth = mStackDepthVariable.get();
					final long lWaitTimeMicroseconds = lExposuretimeInMicroSeconds * lDepth;
					ThreadUtils.sleep(lWaitTimeMicroseconds,
														TimeUnit.MICROSECONDS);

					StackInterface lStack;
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

					lStack.setNumberOfImagesPerPlane(getNumberOfImagesPerPlaneVariable().get());
					mStackReference.set(lStack);

					if (mLeftInQueue != null)
						mLeftInQueue.countDown();

				}
			}
		});

		final ContiguousOffHeapPlanarStackFactory lContiguousOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		mRecycler = new BasicRecycler<StackInterface, StackRequest>(lContiguousOffHeapPlanarStackFactory,
																																20);

	}

	protected StackInterface generateSyntheticStack() throws IncompatibleTypeException
	{
		final int lWidth = (int) max(16, mStackWidthVariable.get());
		final int lHeight = (int) max(16, mStackHeightVariable.get());
		int lDepth = (int) max(16, mStackDepthVariable.get());
		if (mHint != null && mHint.type.startsWith("autofocus.angle"))
			lDepth = mHint.nbangles;

		final int lNumberOfImagesPerPlane = getNumberOfImagesPerPlaneVariable().get()
																																						.intValue();

		if (lWidth * lHeight * lDepth <= 0)
			return null;

		final StackRequest lStackRequest = StackRequest.build(lWidth,
																													lHeight,
																													lDepth);

		final StackInterface lStack = mRecycler.getOrWait(1,
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
																												getExposureInMicrosecondsVariable().get()
																																														.longValue(),
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

		mStackDepthVariable.set(mStackDepthVariable.get() + 1);

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
		mTriggerVariable.setEdge(false, true);
	}

	public Variable<Boolean> getTriggerVariable()
	{
		return mTriggerVariable;
	}

	@Override
	public void giveHint(StackCameraDeviceSimulatorHint pHint)
	{
		mHint = pHint;
	}

}
