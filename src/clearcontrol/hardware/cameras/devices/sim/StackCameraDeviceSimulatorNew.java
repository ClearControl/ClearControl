package clearcontrol.hardware.cameras.devices.sim;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.hardware.cameras.devices.sim.SynteticStackTypeEnum;
import clearcontrol.simulation.BasicSampleSimulator;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackProvider;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.StackSourceInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;
import gnu.trove.list.array.TByteArrayList;
import net.imglib2.exception.IncompatibleTypeException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.max;

/**
 * Created by dibrov on 12/03/17.
 */
public class StackCameraDeviceSimulatorNew extends StackCameraDeviceBase implements LoggingInterface,
        AsynchronousSchedulerServiceAccess,
        AsynchronousExecutorServiceAccess,
        SimulationDeviceInterface {

    private StackProvider mStackProvider;
    protected AtomicLong mCurrentStackIndex = new AtomicLong(0);
    private volatile CountDownLatch mStackSent;
    private final AtomicLong mTriggeCounter = new AtomicLong();

    /**
     * Crates a StackCameraDeviceSimulator of a given name. Synthetic Stacks are
     * sent to the output variable when a positive edge is sent to the trigger
     * variable (false -> true).
     *
     * @param pDeviceName      camera name
     * @param pTriggerVariable trigger
     */
    public StackCameraDeviceSimulatorNew(String pDeviceName,
                                      Variable<Boolean> pTriggerVariable) {
        this(pDeviceName, null, pTriggerVariable);
    }

    /**
     * Crates a StackCameraDeviceSimulator of a given name. Stacks from the given
     * StackSourceInterface are sent to the output variable when a positive edge
     * is sent to the trigger variable (false -> true).
     *
     * @param pDeviceName
     * @param pStackProvider
     * @param pTriggerVariable
     */
    public StackCameraDeviceSimulatorNew(String pDeviceName,
                                      StackProvider pStackProvider,
                                      Variable<Boolean> pTriggerVariable) {
        super(pDeviceName);
        mStackProvider = pStackProvider;
        mTriggerVariable = pTriggerVariable;

        mChannelVariable = new Variable<Integer>("Channel", 0);

        mLineReadOutTimeInMicrosecondsVariable = new Variable<Double>("LineReadOutTimeInMicroseconds",
                1.0);
        mStackBytesPerPixelVariable = new Variable<Long>("FrameBytesPerPixel",
                2L);
        mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);
        mStackWidthVariable.addSetListener((o, n) -> {
            if (isSimLogging())
                info(getName() + ": New camera width: " + n);
        });

        mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);
        mStackHeightVariable.addSetListener((o, n) -> {
            if (isSimLogging())
                info(getName() + ": New camera height: " + n);
        });

        mStackMaxWidthVariable = new Variable<Long>("FrameMaxWidth",
                2048L);
        mStackMaxHeightVariable = new Variable<Long>("FrameMaxHeight",
                2048L);

        mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);
        mStackDepthVariable.addSetListener((o, n) -> {
            if (isSimLogging())
                info(getName() + ": New camera stack depth: " + n);
        });

        mExposureInMicrosecondsVariable = new Variable<Double>("ExposureInMicroseconds",
                1000.0);
        mExposureInMicrosecondsVariable.addSetListener((o, n) -> {
            if (isSimLogging())
                info(getName() + ": New camera exposure: " + n);
        });

        mPixelSizeinNanometersVariable = new Variable<Double>("PixelSizeinNanometers",
                160.0);

        mStackReference = new Variable<>("StackReference");

        if (mTriggerVariable == null) {
            severe("cameras",
                    "Cannot instantiate " + StackCameraDeviceSimulator.class.getSimpleName()
                            + " because trigger variable is null!");
            return;
        }

        mTriggerVariable.addEdgeListener(new VariableEdgeListener<Boolean>() {
            @Override
            public void fire(Boolean pAfterEdge) {
                if (pAfterEdge)
                    receivedTrigger();
            }
        });

        final ContiguousOffHeapPlanarStackFactory lContiguousOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

        mRecycler = new BasicRecycler<StackInterface, StackRequest>(lContiguousOffHeapPlanarStackFactory,
                40);

    }

    protected void receivedTrigger() {
        if (isSimLogging())
            info("Received Trigger");
        final long lExposuretimeInMicroSeconds = mExposureInMicrosecondsVariable.get()
                .longValue();
        final long lDepth = mStackDepthVariable.get();

        if (mTriggeCounter.incrementAndGet() >= lDepth) {
            mTriggeCounter.set(0);

            executeAsynchronously(() -> {

                StackInterface lStack = null;
                if (mStackProvider != null) {
                    try {
                        System.out.println("Stack provider not null");
                        final long Index = mCurrentStackIndex.get();
                        lStack = mStackProvider.getStack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (lStack == null)
                    severe("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
                else {

                    lStack.setTimeStampInNanoseconds(System.nanoTime());
                    lStack.setIndex(mCurrentStackIndex.get());
                    lStack.setNumberOfImagesPerPlane(getNumberOfImagesPerPlaneVariable().get());
                    lStack.setChannel(getChannelVariable().get());
                    mStackReference.set(lStack);
                }

                if (mStackSent != null)
                    try {
                        System.out.println("waiting for exposure");
                        Thread.sleep(((long) getExposure() / 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                mStackSent.countDown();
            });
        }

    }



    private long sum(TByteArrayList pArrayList) {
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


    @Override
    public void reopen() {
        return;
    }

    @Override
    public boolean start() {
        /*final Runnable lRunnable = () -> {
            trigger();
		};
		mTriggerScheduledAtFixedRate = scheduleAtFixedRate(	lRunnable,
																												getExposureInMicrosecondsVariable().get()
																																														.longValue(),
																												TimeUnit.MICROSECONDS);

																												/**/
        return true;
    }

    @Override
    public boolean stop() {
		/*
		if (mTriggerScheduledAtFixedRate != null)
			mTriggerScheduledAtFixedRate.cancel(false);
			/**/

        return true;
    }

    @Override
    public Future<Boolean> playQueue() {
        if (isSimLogging())
            info("Playing queue...");

        mStackSent = new CountDownLatch(1);
        super.playQueue();

        final Future<Boolean> lFuture = new Future<Boolean>() {

            @Override
            public boolean cancel(boolean pMayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Boolean get() throws InterruptedException,
                    ExecutionException {
                mStackSent.await();
                return true;
            }

            @Override
            public Boolean get(long pTimeout, TimeUnit pUnit) throws InterruptedException,
                    ExecutionException,
                    TimeoutException {
                mStackSent.await(pTimeout, pUnit);
                return true;
            }
        };

        return lFuture;
    }

    @Override
    public void trigger() {
        mTriggerVariable.setEdge(false, true);
    }


}
