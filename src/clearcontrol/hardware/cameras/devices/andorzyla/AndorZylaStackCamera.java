package clearcontrol.hardware.cameras.devices.andorzyla;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.ContiguousMemoryInterface;

import static andorsdkj.bindings.util.Buffer16ToArray.toArray;
import static andorsdkj.bindings.util.SavePNG.savePNG;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import andorsdkj.*;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;
import andorsdkj.sequence.ImageSequence;
import andorsdkj.sequence.SequenceAcquisition;

/**
 * AndorZylaStackCamera class provides the opportunity to operate AndorZyla as a
 * StackCameraDevice from ClearControl.
 */

public class AndorZylaStackCamera extends StackCameraDeviceBase
        implements StackCameraDeviceInterface, OpenCloseDeviceInterface, AsynchronousExecutorServiceAccess {
	private AndorSdkJ mAndorSDKJ;
	private AndorCamera mAndorCamera;
	private TriggerMode mTriggeringMode;
	private int mCameraIndex;
	private SequenceAcquisition mSequenceAcquisition;
	private Object mLock = new Object();

	/**
	 * AndorZylaStackCamera constructor.
	 * 
	 * @param pCameraIndex
	 *            - index of the opened camera for future reference
	 * @param pTriggeringMode
	 *            - triggering mode of the camera: hardware, software or
	 *            internal
	 * @throws AndorSdkJException
	 *             a generic exception type thrown when the return from
	 *             AtcoreLibrary of AndorSDK is not AT.SUCCESS
	 */

//	public AndorZylaStackCamera(int pCameraIndex, TriggerMode pTriggeringMode) throws AndorSdkJException {
//		super("AndorZyla " + pCameraIndex);
//		mAndorSDKJ = new AndorSdkJ();
//		mAndorSDKJ.open();
//		mAndorCamera = mAndorSDKJ.openCamera(pCameraIndex);
//		mCameraIndex = pCameraIndex;
//		mTriggeringMode = pTriggeringMode;
//		mStackDepthVariable = new Variable<Long>("depth", 10L);
//		mStackWidthVariable = new Variable<Long>("width", 2048L);
//		mStackHeightVariable = new Variable<Long>("height", 2048L);
//	}
	
	public AndorZylaStackCamera(AndorSdkJ pAndorEnv, int pCameraIndex, TriggerMode pTriggeringMode) throws AndorSdkJException {
		super("AndorZyla " + pCameraIndex);
		
		mAndorCamera = pAndorEnv.openCamera(pCameraIndex);
		mCameraIndex = pCameraIndex;
		mTriggeringMode = pTriggeringMode;
		mStackDepthVariable = new Variable<Long>("depth", 10L);
		mStackWidthVariable = new Variable<Long>("width", 2048L);
		mStackHeightVariable = new Variable<Long>("height", 2048L);
		mExposureInMicrosecondsVariable = new Variable<Double>("Exposure", 30000.0);
		mStackReference = new Variable<StackInterface>("abc", OffHeapPlanarStack.createStack(false, 2060, 2048, 10));
		mAndorCamera.set16PixelEncoding();
	}
	
	@Override
	public void setExposure(double pExposureInMicroseconds)
	{
		super.setExposure(pExposureInMicroseconds);
		try {
			mAndorCamera.setExposureTimeInSeconds(pExposureInMicroseconds * Math.pow(10, -6));
		} catch (AndorSdkJException e) {
			System.out.println("couldn't set the exposure, watch the stacktrace");
			e.printStackTrace();
		}
	};

	@Override
	public void trigger() {
		if (mTriggeringMode == TriggerMode.SOFTWARE) {
			try {
				mAndorCamera.SoftwareTrigger();
			} catch (AndorSdkJException e) {
				System.out.println("Cannot software trigger AndorZylaStackCamera " + mCameraIndex);
				e.printStackTrace();
			}
		} else {
			throw new java.lang.IllegalArgumentException("Please use the software trigger option. Other options are not implemented yet.");
		}
	}

	@Override
	public void reopen() {
		synchronized (mLock) {
			try {
				mAndorCamera.stopAcquisition();
				mAndorCamera.close();
				mAndorCamera = new AndorCamera(mCameraIndex);
			} catch (Exception e) {
				System.out.println("Cannot reopen AndorZylaStackCamera " + mCameraIndex);
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean open() {
		try {
			mAndorSDKJ.open();
			mAndorCamera = new AndorCamera(mCameraIndex);
			mAndorCamera.setTriggeringMode(mTriggeringMode);

			mSequenceAcquisition.addListener((a, s) -> {
				// this is just for debug,
				System.out.println(s);
			});

			return super.open();
		} catch (AndorSdkJException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean close() {
		try {
			mSequenceAcquisition.close();
			mAndorCamera.close();
			//mAndorSDKJ.close();
			return super.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean start() {
		synchronized (mLock) {
			/*
			 * try { lAndorSDKJ.open(); lAndorCamera.startAcquisition(); return
			 * true; } catch (Exception e) { System.out.println(
			 * "Cannot start the acquisition of AndorZylaStackCamera " +
			 * lCameraIndex); e.printStackTrace(); }/
			 **/
			return false;
		}
	}

	@Override
	public boolean stop() {
		synchronized (mLock) {
			// boolean exitFlag = true;
			
			  try 
			  	{ 
				  mAndorCamera.stopAcquisition(); // lAndorSDKJ.close();
				  return true; 
				} catch (Exception e) 
			  	{
					System.out.println("Cannot stop the acquisition of AndorZylaStackCamera " + mCameraIndex); 
					e.printStackTrace(); 
				}
			
			  return false;
		}
	}

	@Override
	public Future<Boolean> playQueue() {
		super.playQueue();

		Callable<Boolean> lAcquireSequenceCallable = () -> {

			// TODO: code that creates an image sequence based on a buffer that
		    // comes
		    // from a requested stack

			// NOTE: request should be done with the correct widtha and height
		    // as
		    // returned by driver...

			System.out.println("in callable");
//			mAndorCamera.collectMetadata(true);
//			mAndorCamera.collectTimestamp(true);

			
			
			long lHeight = mStackHeightVariable.get() > 0 ? mStackHeightVariable.get() : 1;
			long lWidth = mStackWidthVariable.get() > 0 ? mStackWidthVariable.get() : 1;
			long lDepth = mStackDepthVariable.get() > 0 ? mStackDepthVariable.get() : 1;

			
			
			// TODO: checl why it throws errors
			mAndorCamera.setFrameHeight((int) lHeight);
			mAndorCamera.setFrameWidth((int) lWidth);

			lWidth = mAndorCamera.getStrideInPixels(2);
			System.out.println("----------> width-stride is: " + lWidth);
			
//			final StackRequest lStackRequest = StackRequest.build(lHeight + 1, lWidth, lDepth);
//
//			final StackInterface lStack = mRecycler.getOrWait(1, TimeUnit.SECONDS, lStackRequest);
			
			
			final StackInterface lStack = OffHeapPlanarStack.createStack(false, lWidth, lHeight, lDepth);
			

			if (lStack != null) {

				System.out.println("stack is not null");
				final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();

				ImageSequence lImageSequence = createSequenceFromMemory(lContiguousMemory.getBridJPointer(Byte.class), lWidth, lHeight, lDepth);

				//mAndorCamera.setExposureTimeInSeconds(mExposureInMicrosecondsVariable.get() / 1000);
				//mAndorCamera.setReadoutRate(ReadOutRate._100_MHz);

				// TODO: compute an over-estimate (+20%) of the time required to
		        // acquire
		        // the stack.

				// for standard readout rate!!
				long lTimeOutInMilliseconds = (long) (mExposureInMicrosecondsVariable.get() / 1000L) * lDepth;
				lTimeOutInMilliseconds += (long) lTimeOutInMilliseconds * 0.2;

				mSequenceAcquisition = new SequenceAcquisition(mAndorCamera, lImageSequence);
				System.out.println("before acquireSequence");
				mSequenceAcquisition.acquireSequence(lTimeOutInMilliseconds, TimeUnit.MILLISECONDS);
				System.out.println("after acquireSequence");

				// TODO: fill up metadata
				lStack.setTimeStampInNanoseconds(0);// ...
				getStackVariable().set(lStack);

				return true;
			}

			return false;
		};

		Future<Boolean> lFuture = executeAsynchronously(lAcquireSequenceCallable);

		return lFuture;
	}

	private ImageSequence createSequenceFromMemory(Pointer<Byte> pBufferPointer, long pWidth, long pHeight, long pDepth) {
		ImageSequence lImSec;
		try {
			lImSec = new ImageSequence(mAndorCamera.getImageSizeInBytes(), pWidth, pHeight, pDepth, pBufferPointer);
			return lImSec;
		} catch (AndorSdkJException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void saveSequence(ImageSequence pImageSequence) {
		for (int j = 0; j < pImageSequence.getDepth(); j++) {
			ImageBuffer lImageBufferToProcess = new ImageBuffer(pImageSequence.getImageBufferArray()[j].getPointer(),
			        pImageSequence.getImageSizeInBytes());

			int lHeight = 0;
			int lWidth = 0;
			try {
				lHeight = mSequenceAcquisition.getCamera().getFrameHeight();
				lWidth = mSequenceAcquisition.getCamera().getStrideInPixels(2);
				System.out.println("listener: width is: " + lWidth + " height is: " + lHeight);
			} catch (Exception e) {

				e.printStackTrace();
			}

			int[][] BufferArray = toArray(lImageBufferToProcess, lWidth, lHeight);
			savePNG(BufferArray, "C:\\Users\\myersadmin\\images\\", "seq_" + j + ".png");
		}
	}
	
	public void setDebugMessagesOn(boolean pFlag) {
		mAndorCamera.setDebugMessagesOn(pFlag);
	}
}
