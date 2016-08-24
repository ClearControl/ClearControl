package clearcontrol.hardware.cameras.devices.sim;

import static java.lang.Math.max;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.StackSourceInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;
import gnu.trove.list.array.TByteArrayList;
import net.imglib2.exception.IncompatibleTypeException;

public class StackCameraDeviceSimulator extends StackCameraDeviceBase	implements
																																			Loggable,
																																			AsynchronousSchedulerServiceAccess,
																																			AsynchronousExecutorServiceAccess
{
	private StackSourceInterface mStackSource;

	protected AtomicLong mCurrentStackIndex = new AtomicLong(0);

	private final Variable<SynteticStackTypeEnum> mSyntheticStackTypeVariable = new Variable<SynteticStackTypeEnum>("SyntheticStackType",
																																																					SynteticStackTypeEnum.Fractal);

<<<<<<< HEAD
	private AtomicLong mTriggerCounter = new AtomicLong(0);
	private volatile CountDownLatch mLeftInQueue;
=======
	private volatile CountDownLatch mStackSent;
	private final AtomicLong mTriggeCounter = new AtomicLong();
>>>>>>> 128b8fb8c801159fbe542164dd46f5c1c196454e

	/**
	 * Crates a StackCameraDeviceSimulator of a given name. Synthetic Stacks are
	 * sent to the output variable when a positive edge is sent to the trigger
	 * variable (false -> true).
	 * 
	 * @param pDeviceName
	 *          camera name
	 * @param pTriggerVariable
	 *          trigger
	 */
	public StackCameraDeviceSimulator(String pDeviceName,
																		Variable<Boolean> pTriggerVariable)
	{
		this(pDeviceName, null, pTriggerVariable);
	}

	/**
	 * Crates a StackCameraDeviceSimulator of a given name. Stacks from the given
	 * StackSourceInterface are sent to the output variable when a positive edge
	 * is sent to the trigger variable (false -> true).
	 * 
	 * @param pDeviceName
	 * @param pStackSource
	 * @param pTriggerVariable
	 */
	public StackCameraDeviceSimulator(String pDeviceName,
																		StackSourceInterface pStackSource,
																		Variable<Boolean> pTriggerVariable)
	{
		super(pDeviceName);
		mStackSource = pStackSource;
		mTriggerVariable = pTriggerVariable;

		mChannelVariable = new Variable<Integer>("Channel", 0);

		mLineReadOutTimeInMicrosecondsVariable = new Variable<Double>("LineReadOutTimeInMicroseconds",
																																	1.0);
		mStackBytesPerPixelVariable = new Variable<Long>(	"FrameBytesPerPixel",
																											2L);
		mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);
		mStackWidthVariable.addSetListener((o, n) -> {
			System.out.println(getName() + ": New camera width: " + n);
		});

		mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);
		mStackHeightVariable.addSetListener((o, n) -> {
			System.out.println(getName() + ": New camera height: " + n);
		});

		mStackMaxWidthVariable = new Variable<Long>("FrameMaxWidth",
																								2048L);
		mStackMaxHeightVariable = new Variable<Long>(	"FrameMaxHeight",
																									2048L);

		mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);
		mStackDepthVariable.addSetListener((o, n) -> {
			System.out.println(getName() + ": New camera stack depth: " + n);
		});

		mExposureInMicrosecondsVariable = new Variable<Double>(	"ExposureInMicroseconds",
																														1000.0);
		mExposureInMicrosecondsVariable.addSetListener((o, n) -> {
			System.out.println(getName() + ": New camera exposure: " + n);
		});

		mPixelSizeinNanometersVariable = new Variable<Double>("PixelSizeinNanometers",
																													160.0);

		mStackVariable = new Variable<>("StackReference");

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
<<<<<<< HEAD
				{
					final long lEdgeCounterValue = mTriggerCounter.incrementAndGet();
					if (lEdgeCounterValue >= getStackDepthVariable().get())

//					final long lExposuretimeInMicroSeconds = mExposureInMicrosecondsVariable.get()
//																																									.longValue();
//					final long lDepth = mStackDepthVariable.get();
//					final long lWaitTimeMicroseconds = lExposuretimeInMicroSeconds
//																							* lDepth;
//					ThreadUtils.sleep(lWaitTimeMicroseconds,
//														TimeUnit.MICROSECONDS);
//
//					StackInterface lStack;
//					if (mStackSource != null)
//					{
//						final long Index = mCurrentStackIndex.get();
//						lStack = mStackSource.getStack(Index);
//						mCurrentStackIndex.set((Index + 1)
//																		% mStackSource.getNumberOfStacks());
//					}
//					else
//					{
//						try
//						{
//							switch (mSyntheticStackType.get())
//							{
//							default:
//							case Fractal:
//								lStack = generateFractalStack();
//								break;
//							case Sinus:
//								lStack = generateSinusStack();
//								break;
//							}
//
//						}
//						catch (final Throwable e)
//						{
//							e.printStackTrace();
//							return;
//						}
//						mCurrentStackIndex.incrementAndGet();
//					}
//					if (lStack == null)
//						System.err.println("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
//					else

					{
						sendStack();
						mTriggerCounter.set(0);
					}
=======
					receivedTrigger();
			}
		});

		final ContiguousOffHeapPlanarStackFactory lContiguousOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		mRecycler = new BasicRecycler<StackInterface, StackRequest>(lContiguousOffHeapPlanarStackFactory,
																																40);

	}

	protected void receivedTrigger()
	{
		final long lExposuretimeInMicroSeconds = mExposureInMicrosecondsVariable.get()
																																						.longValue();
		final long lDepth = mStackDepthVariable.get();

		if (mTriggeCounter.incrementAndGet() >= lDepth)
		{
			mTriggeCounter.set(0);

			executeAsynchronously(() -> {

				StackInterface lStack;
				if (mStackSource != null)
				{
					final long Index = mCurrentStackIndex.get();
					lStack = mStackSource.getStack(Index);
					mCurrentStackIndex.set((Index + 1) % mStackSource.getNumberOfStacks());
				}
				else
				{
					try
					{
						switch (getSyntheticStackTypeVariable().get())
						{
						default:
						case Fractal:
							lStack = generateFractalStack();
							break;
						case Sinus:
							lStack = generateSinusStack();
							break;
						}

					}
					catch (final Throwable e)
					{
						e.printStackTrace();
						return;
					}
					mCurrentStackIndex.incrementAndGet();
>>>>>>> 128b8fb8c801159fbe542164dd46f5c1c196454e
				}
				if (lStack == null)
					System.err.println("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
				else
				{

					lStack.setTimeStampInNanoseconds(System.nanoTime());
					lStack.setIndex(mCurrentStackIndex.get());
					lStack.setNumberOfImagesPerPlane(getNumberOfImagesPerPlaneVariable().get());
					lStack.setChannel(getChannelVariable().get());
					mStackReference.set(lStack);
				}

<<<<<<< HEAD
		mRecycler =
							new BasicRecycler<StackInterface, StackRequest>(lContiguousOffHeapPlanarStackFactory,
																															40);
<<<<<<< HEAD
=======
				if (mStackSent != null)
					mStackSent.countDown();
			});
		}
>>>>>>> 128b8fb8c801159fbe542164dd46f5c1c196454e

	}

	private void sendStack()
	{
		final long lExposuretimeInMicroSeconds = mExposureInMicrosecondsVariable.get()
																																						.longValue();
		final long lDepth = mStackDepthVariable.get();
		final long lWaitTimeMicroseconds = lExposuretimeInMicroSeconds
																				* lDepth;
		ThreadUtils.sleep(lWaitTimeMicroseconds, TimeUnit.MICROSECONDS);

		StackInterface lStack;
		if (mStackSource != null)
		{
			final long Index = mCurrentStackIndex.get();
			lStack = mStackSource.getStack(Index);
			mCurrentStackIndex.set((Index + 1)
															% mStackSource.getNumberOfStacks());
		}
		else
		{
			try
			{
				switch (mSyntheticStackType.get())
				{
				default:
				case Fractal:
					lStack = generateFractalStack();
					break;
				case Sinus:
					lStack = generateSinusStack();
					break;
				}

			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return;
			}
			mCurrentStackIndex.incrementAndGet();
		}
		if (lStack == null)
			System.err.println("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
		else
		{
			lStack.setTimeStampInNanoseconds(System.nanoTime());
			lStack.setIndex(mCurrentStackIndex.get());
			lStack.setNumberOfImagesPerPlane(getNumberOfImagesPerPlaneVariable().get());
			lStack.setChannel(getChannelVariable().get());
			mStackVariable.set(lStack);
		}
=======
>>>>>>> 4a7f38538133b52c1ed547db9dd476a3063296fd

		if (mLeftInQueue != null)
			mLeftInQueue.countDown();
	}

	protected StackInterface generateFractalStack() throws IncompatibleTypeException
	{
		final long lWidth = max(1, mStackWidthVariable.get());
		final long lHeight = max(1, mStackHeightVariable.get());

		long lNumberOfKeptImages = sum(mStagingKeepAcquiredImageArray);

		final long lDepth = max(1, lNumberOfKeptImages);
		final int lChannel = mChannelVariable.get();

		//System.out.println("lChannel=" + lChannel);

		final int lNumberOfImagesPerPlane = getNumberOfImagesPerPlaneVariable().get()
																																						.intValue();

		final StackRequest lStackRequest = StackRequest.build(lWidth,
																													lHeight,
																													lDepth);

		final StackInterface lStack = mRecycler.getOrWait(1,
																											TimeUnit.SECONDS,
																											lStackRequest);

		if (lStack != null)
		{
			final byte time = (byte) mCurrentStackIndex.get();

			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
			final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

<<<<<<< HEAD
			for (int z = 0; z < lDepth; z++)
				for (int y = 0; y < lHeight; y++)
					for (int x = 0; x < lWidth; x++)
					{
						short lValue = (short) (((byte) (x + time)
																			^ (byte) (y
																								+ (lHeight * lChannel)
																									/ 3)
																			^ (byte) z
																			^ (byte) (time)));/**/
						if (lValue < 32)
							lValue = 0;
						lContiguousBuffer.writeShort(lValue);
					}
=======
			for (int z = 0; z < mStackDepthVariable.get(); z++)
				if (mStagingKeepAcquiredImageArray.get(z) > 0)
					for (int y = 0; y < lHeight; y++)
						for (int x = 0; x < lWidth; x++)
						{
							short lValue = (short) (((byte) (x + time) ^ (byte) (y + (lHeight * lChannel) / 3)
																				^ (byte) z ^ (byte) (time)));/**/
							if (lValue < 32)
								lValue = 0;
							lContiguousBuffer.writeShort(lValue);
						}
>>>>>>> 4a7f38538133b52c1ed547db9dd476a3063296fd

		}

		return lStack;
	}

	private long sum(TByteArrayList pArrayList)
	{
		int lLength = pArrayList.size();
		long sum = 0;
		for (int i = 0; i < lLength; i++)
			sum += pArrayList.getQuick(i);
		return sum;
	}

	/**
	 * @return
	 * @throws IncompatibleTypeException
	 */
	protected StackInterface generateSinusStack() throws IncompatibleTypeException
	{
		final long lWidth = max(1, mStackWidthVariable.get());
		final long lHeight = max(1, mStackHeightVariable.get());
		final long lDepth = max(1, mStackDepthVariable.get());
		final int lChannel = mChannelVariable.get();

		final int lNumberOfImagesPerPlane = getNumberOfImagesPerPlaneVariable().get()
																																						.intValue();

		final StackRequest lStackRequest = StackRequest.build(lWidth,
																													lHeight,
																													lDepth);

		final StackInterface lStack = mRecycler.getOrWait(1,
																											TimeUnit.SECONDS,
																											lStackRequest);

		if (lStack != null)
		{
			final byte time = (byte) mCurrentStackIndex.get();

			final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
			final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

			for (int z = 0; z < lDepth; z++)
				for (int y = 0; y < lHeight; y++)
					for (int x = 0; x < lWidth; x++)
					{
<<<<<<< HEAD
<<<<<<< HEAD
						short lValue = (short) (128
																		+ 128 * Math.sin(
																											((x + (lWidth
																															* lChannel)
																														/ 3)
																												% lWidth)
																											/ 64.0));/**/
=======
						short lValue = (short) (128 + 128 * Math.sin(((x+time + (lWidth * lChannel) / 3) % lWidth) / 64.0));/**/
=======
						short lValue = (short) (128 + 128 * Math.sin(((x + time + (lWidth * lChannel) / 3) % lWidth) / 64.0));/**/
>>>>>>> 128b8fb8c801159fbe542164dd46f5c1c196454e

>>>>>>> 4a7f38538133b52c1ed547db9dd476a3063296fd
						lContiguousBuffer.writeShort(lValue);
					}

		}

		return lStack;
	}

<<<<<<< HEAD
	protected StackInterface generateOtherStack() throws IncompatibleTypeException
	{
		final long lWidth = max(1, mStackWidthVariable.get());
		final long lHeight = max(1, mStackHeightVariable.get());
		final long lDepth = max(1, mStackDepthVariable.get());

		final int lNumberOfImagesPerPlane =
																			getNumberOfImagesPerPlaneVariable()	.get()
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
		/*
				if (lStack != null)
				{
					final byte time = (byte) mCurrentStackIndex;
					if (mHint == null || mHint.type.startsWith("normal"))
					{
						final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
						final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);
		
						for (int z = 0; z < lDepth; z++)
							for (int y = 0; y < lHeight; y++)
								for (int x = 0; x < lWidth; x++)
								{
									int lValueValue = (((byte) (x+time) ^ (byte) (y)
																			^ (byte) z ^ (byte)(time)));
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
						}
		
					}
				}/**/

		return lStack;
	}

=======
>>>>>>> 4a7f38538133b52c1ed547db9dd476a3063296fd
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
	public Future<Boolean> playQueue()
	{
<<<<<<< HEAD
		mTriggerCounter.set(0);
		mLeftInQueue = new CountDownLatch(getQueueLength());

		super.playQueue();

		// mStackDepthVariable.set(mStackDepthVariable.get() + 1);
=======
		mStackSent = new CountDownLatch(1);
		super.playQueue();
>>>>>>> 128b8fb8c801159fbe542164dd46f5c1c196454e

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
				mStackSent.await();
				return true;
			}

			@Override
			public Boolean get(long pTimeout, TimeUnit pUnit)	throws InterruptedException,
																												ExecutionException,
																												TimeoutException
			{
				mStackSent.await(pTimeout, pUnit);
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

	public Variable<SynteticStackTypeEnum> getSyntheticStackTypeVariable()
	{
		return mSyntheticStackTypeVariable;
	}

}
