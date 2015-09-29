package rtlib.microscope.lsm;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public interface LightSheetMicroscopeInterface	extends
												StateQueueDeviceInterface
{

	/**
	 * Returns the device list object from which all devices can be queried.
	 * 
	 * @return device list object
	 */
	LightSheetMicroscopeDeviceLists getDeviceLists();

	/**
	 * Sets the recycler that should be used by the stack camera device of given
	 * id.
	 * 
	 * @param pStackCameraDeviceIndex
	 */
	void setRecycler(	int pStackCameraDeviceIndex,
						RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> pRecycler);

	/**
	 * Sets the recycler that should be used by _all_ stack camera devices.
	 * 
	 * @param pRecycler
	 */
	void setRecycler(RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> pRecycler);

	/**
	 * Returns the recycler currently b the stack camera device of given id.
	 * 
	 * @param pStackCameraDeviceIndex
	 *            stack camera index id.
	 * @return recycler.
	 */
	RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> getRecycler(int pStackCameraDeviceIndex);

	/**
	 * Uses a recycler with given parameters. This recycler will be used for all
	 * subsequent plays. if teh recycler does not exist yet, it is created.
	 * 
	 * @param pName
	 *            recycler name
	 * @param pMaximumNumberOfAvailableStacks
	 *            maximum number of available stacks
	 * @param pMaximumNumberOfLiveStacks
	 *            maximum number of live stacks
	 */
	void useRecycler(	String pName,
						int pMinimumNumberOfAvailableStacks,
						int pMaximumNumberOfAvailableStacks,
						int pMaximumNumberOfLiveStacks);

	/**
	 * Clears a given recycler.
	 * 
	 * @param pName
	 */
	void clearRecycler(String pName);

	/**
	 * Clears all recyclers.
	 */
	void clearAllRecycler();

	/**
	 * Plays queue for all devices, and waits for playback to finish.
	 * 
	 * @param pTimeOut
	 *            timeout
	 * @param pTimeUnit
	 *            time unit for timeout
	 * @return true if successful
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	Boolean playQueueAndWait(long pTimeOut, TimeUnit pTimeUnit)	throws InterruptedException,
																ExecutionException,
																TimeoutException;

	/**
	 * Plays queue for all devices, waits for playback to finish as well as
	 * waits for stacks to be delivered.
	 * 
	 * @param pTimeOut
	 * @param pTimeUnit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	Boolean playQueueAndWaitForStacks(	long pTimeOut,
										TimeUnit pTimeUnit)	throws InterruptedException,
															ExecutionException,
															TimeoutException;

	/**
	 * Returns the average timestamp for all stacks acquired during for last
	 * played queue.
	 * 
	 * @return timestamp in nanoseconds
	 */
	long lastAcquiredStacksTimeStampInNS();

	ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> getStackVariable(int pIndex);

	/**
	 * Sets with and height of camera image
	 * 
	 * @param pWidth
	 *            width
	 * @param pHeight
	 *            height
	 */
	public void setCameraWidthHeight(int pWidth, int pHeight);

	/**
	 * Returns the camera image width.
	 * 
	 * @param pCameraDeviceIndex
	 *            camera device index
	 * @return width in pixels
	 */
	int getCameraWidth(int pCameraDeviceIndex);

	/**
	 * Returns the camera image height.
	 * 
	 * @param pCameraDeviceIndex
	 *            camera device index
	 * @return height in pixels
	 */
	int getCameraHeight(int pCameraDeviceIndex);

	/**
	 * Sets image acquisition exposure in
	 * 
	 * @param pValue
	 *            time
	 * @param pTimeUnit
	 *            time unit
	 * 
	 */
	public void setExposure(long pValue, TimeUnit pTimeUnit);

	/**
	 * Returns the camera exposure time.
	 * 
	 * @param pCameraDeviceIndex
	 *            camera device index
	 * @param pTimeUnit
	 *            time unit in which to return the exposure
	 * @return camera exposure time in the given unit
	 */
	long getExposure(int pCameraDeviceIndex, TimeUnit pTimeUnit);

	/**
	 * Selects _one_ light sheet to direct light to:
	 * 
	 * @param pLightSheetIndex
	 *            light sheet index
	 * 
	 */
	public void setI(int pLightSheetIndex);

	/**
	 * Returns true if a light sheet is 'on'.
	 * 
	 * @param pLightSheetIndex
	 *            light sheet device index
	 * @return true if on, false if off.
	 */
	boolean getI(int pLightSheetIndex);

	/**
	 * Directs light to one or several light sheets:
	 * 
	 * @param pLightSheetIndex
	 *            light sheet index
	 * @param pOnOff
	 *            true is on, false is off
	 */
	public void setI(int pLightSheetIndex, boolean pOnOff);

	/**
	 * Switches on/off a given laser.
	 * 
	 * @param pLaserIndex
	 *            index of the laser device
	 * @param pLaserOnOff
	 *            true for on, false otherwise
	 */
	public void setLO(int pLaserIndex, boolean pLaserOnOff);

	/**
	 * Returns whether a given laser is on or off.
	 * 
	 * @param pLaserIndex
	 *            laser device index
	 * @return true if on, false if off
	 */
	boolean getLO(int pLaserIndex);

	/**
	 * Sets a the laser power (mW) for a given laser device.
	 * 
	 * @param pLaserIndex
	 *            index of the laser device
	 * @param pLaserPowerInmW
	 *            laser power in mW
	 */
	public void setLP(int pLaserIndex, double pLaserPowerInmW);

	/**
	 * Returns the laser power in mW for a given laser device
	 * 
	 * @param pLaserIndex
	 *            laser device index
	 * @return laser power in mW
	 */
	double getLP(int pLaserIndex);

	/**
	 * Sets a flag that determines whether all cameras should acquire (or keep)
	 * an image.
	 * 
	 * @param pKeepImage
	 *            true if image should be acquired (or kept), false otherwise
	 */
	public void setC(boolean pKeepImage);

	/**
	 * Sets a flag that determines whether the camera should acquire (or keep)
	 * an image.
	 * 
	 * @param pCameraIndex
	 *            index of the stack camera device
	 * @param pKeepImage
	 *            true if image should be acquired (or kept), false otherwise
	 */
	public void setC(int pCameraIndex, boolean pKeepImage);

	/**
	 * Returns whether the given camera is set to acquire/keep an image.
	 * 
	 * @param pCameraIndex
	 *            camera device index
	 * @return true if acquiring
	 */
	boolean getC(int pCameraIndex);

	/**
	 * Sets a detection objective to a given position.
	 * 
	 * @param pDetectionIndex
	 *            index of detection objective
	 * @param pPositionZ
	 *            position to set objective
	 */
	public void setDZ(int pDetectionIndex, double pPositionZ);

	/**
	 * Returns the detection objective position
	 * 
	 * @param pDetectionArmIndex
	 *            detection arm index
	 * @return position
	 */
	double getDZ(int pDetectionArmIndex);

	/**
	 * Sets the lightsheet's X position (illumination objective).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pPositionX
	 *            lightsheet's X position
	 */
	public void setIX(int pLightSheetIndex, double pPositionX);

	/**
	 * Returns the lighsheet's X position (illumination objective).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's X position
	 */
	double getIX(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's Y position (vertical lightsheet scanning
	 * direction).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet index
	 * @param pPositionY
	 *            lightsheet's Y position
	 */
	public void setIY(int pLightSheetIndex, double pPositionY);

	/**
	 * Returns the lighsheet's Y position (vertical lightsheet scanning
	 * direction).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's Y position
	 */
	double getIY(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's Z position (stack scanning direction).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pPositionZ
	 *            Z position of lightsheet
	 */
	public void setIZ(int pLightSheetIndex, double pPositionZ);

	/**
	 * Returns the lightsheet's Z position (stack scanning direction).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return Z position of lightsheet
	 */
	double getIZ(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's angle alpha.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pAngleAlpha
	 *            lightsheet's alpha angle
	 */
	public void setIA(int pLightSheetIndex, double pAngleAlpha);

	/**
	 * Returns the lightsheet's angle alpha.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's alpha angle
	 */
	double getIA(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's angle beta.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pAngleBeta
	 *            lightsheet's beta angle
	 */
	public void setIB(int pLightSheetIndex, double pAngleBeta);

	/**
	 * Returns the lightsheet's angle beta.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's beta angle
	 */
	double getIB(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's width - i.e. its dimension along the propagation
	 * axis.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pRange
	 *            lightsheet's width
	 */
	public void setIW(int pLightSheetIndex, double pRange);

	/**
	 * Returns the lightsheet's width - i.e. its dimension along the propagation
	 * axis.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's width
	 */
	double getIW(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's height - i.e. its dimension along the scanning
	 * direction.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pHeight
	 *            lightsheet's height
	 */
	public void setIH(int pLightSheetIndex, double pLength);

	/**
	 * Returns the lighsheet's height - i.e. its dimension along the scanning
	 * direction.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return lightsheet's height
	 */
	double getIH(int pLightSheetIndex);

	/**
	 * Sets the lightsheet's analog laser modulation level (common to all
	 * lasers).
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet index
	 * @param pValue
	 *            lightsheet's analog modulation level
	 */
	public void setIP(int pLightSheetIndex, double pValue);

	/**
	 * Returns the lightsheet's analog laser modulation level (common to all
	 * lasers)
	 * 
	 * @param pLightSheetIndex
	 * @return
	 */
	double getIP(int pLightSheetIndex);

	/**
	 * Sets a flag that determines whether the laser power should be adapted to
	 * the height and with of the lightsheets.
	 * 
	 * @param pAdapt
	 *            true if power should be adapted
	 */
	public void setIPA(boolean pAdapt);

	/**
	 * Sets a flag that determines whether the laser power should be adapted to
	 * the height and with of a given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * 
	 * @param pAdapt
	 *            true if power should be adapted, false if not
	 */
	void setIPA(int pLightSheetIndex, boolean pAdapt);

	/**
	 * Returns the state of the lightsheet's flag that determines whether the
	 * intensity of the laser should be modulated to compensate for changes in
	 * the lightsheet's height and width.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @return true if power should be adapted, false if not
	 */
	boolean getIPA(int pLightSheetIndex);

	/**
	 * Sets the state (on/off) of all laser lines of all lightsheets.
	 * 
	 * @param pOn
	 *            state (true= on, false= off)
	 */
	void setILO(boolean pOn);

	/**
	 * Sets the state (on/off) of all laser lines of a given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet index
	 * @param pOn
	 *            state (true= on, false= off)
	 */
	void setILO(int pLightSheetIndex, boolean pOn);

	/**
	 * Sets the state (on/off) of a specific digital trigger of laser line for a
	 * given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @param pOn
	 *            state (true= on, false= off)
	 */
	void setILO(int pLightSheetIndex, int pLaserIndex, boolean pOn);

	/**
	 * Returns the state (on/off) of a specific digital trigger for a given
	 * laser line of a given lightsheet.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @return state (true= on, false= off)
	 */
	boolean getILO(int pLightSheetIndex, int pLaserIndex);

	/**
	 * Sets the structured illumination pattern for a given lightsheet and laser
	 * line.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @param pPattern
	 *            SI Pattern
	 */
	public void setIPattern(int pLightSheetIndex,
							int pLaserIndex,
							StructuredIlluminationPatternInterface pPattern);

	/**
	 * Returns the SI Pattern in use for a given lighsheet and laser line.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @return SI Pattern
	 */
	StructuredIlluminationPatternInterface getIPattern(	int pLightSheetIndex,
														int pLaserIndex);

	/**
	 * Sets whether the structured illumination pattern for a given lightsheet
	 * and laser line should be active or not.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @param pOnOff
	 *            true for on, false for off.
	 */
	public void setIPatternOnOff(	int pLightSheetIndex,
									int pLaserIndex,
									boolean pOnOff);

	/**
	 * Returns whether the currently set SI pattern should be used or not.
	 * 
	 * @param pLightSheetIndex
	 *            lightsheet device index
	 * @param pLaserIndex
	 *            laser device index
	 * @return
	 */
	public boolean getIPatternOnOff(int pLightSheetIndex,
									int pLaserIndex);

}
