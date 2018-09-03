package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.processor.StackProcessingPipelineInterface;
import clearcontrol.stack.processor.StackProcessorInterface;
import coremem.recycling.RecyclerInterface;

/**
 * MicroscopeInterface is a generic interface for all microscopes types.
 * 
 * @author royer
 * @param <Q>
 *          queue type
 */
public interface MicroscopeInterface<Q extends QueueInterface> extends
                                    NameableInterface,
                                    QueueDeviceInterface<Q>,
                                    HasChangeListenerInterface<VirtualDevice>

{

  /**
   * Returns the microscopes name.
   * 
   * @return microscope's name.
   */
  @Override
  public String getName();

  /**
   * Returns the master lock, which can be used to make sure that opening,
   * closing, playing queues don't overlapp.
   * 
   * @return master lock
   */
  ReentrantLock getMasterLock();

  /**
   * Current task. This is used to prevent multiple tasks executions don't
   * overlap, for example, starting an acquisition while some other task is
   * already running...
   * 
   * @return current task atomic reference
   */
  AtomicReference<Object> getCurrentTask();

  /**
   * Sets the simulation flag.
   * 
   * @param pSimulation
   *          true if simulation, false otherwise.
   */
  void setSimulation(boolean pSimulation);

  /**
   * Returns whether the microscope is in simulation mode.
   * 
   * @return true if simulation mode, or false otherwise
   */
  default boolean isSimulation()
  {
    return false;
  };

  /**
   * Adds a device of a given type. Devices are uniquely identified by their
   * class and index: (class,index) -> device
   * 
   * @param pDeviceIndex
   *          device index
   * @param pDevice
   *          device
   */
  public <T> void addDevice(int pDeviceIndex, T pDevice);

  /**
   * Returns the number of devices of a given class. Devices are uniquely
   * identified by their class and index: (class,index) -> device
   * 
   * @param pClass
   *          class
   * @return number of devices of a given type
   */
  public <T> int getNumberOfDevices(Class<T> pClass);

  /**
   * Returns a device for a given type (class) and index. Devices are uniquely
   * identified by their class and index: (class,index) -> device
   * 
   * @param pClass
   *          class
   * @param pIndex
   *          index
   * @return device for given pair; (class,index)
   */
  public <T> T getDevice(Class<T> pClass, int pIndex);

  /**
   * Returns all devices for a given type (class). Devices are uniquely
   * identified by their class and index: (class,index) -> device
   * 
   * @param pClass
   *          class
   * @return device for given pair: (class,index)
   */
  public <T> ArrayList<T> getDevices(Class<T> pClass);

  /**
   * Returns the device list object from which all devices can be queried.
   * 
   * @return device list object
   */
  public MicroscopeDeviceLists getDeviceLists();

  /**
   * Adds acquisition state manager
   * 
   * @return acquisition manager
   */
  public AcquisitionStateManager<?> addAcquisitionStateManager();

  /**
   * Returns the acquisition state manager for this microscope
   * 
   * @return acquisition state manager
   */
  public AcquisitionStateManager<?> getAcquisitionStateManager();

  /**
   * Sets the recycler that should be used by the stack camera device of given
   * id.
   * 
   * @param pStackCameraDeviceIndex
   *          stack camera device index
   * @param pRecycler
   *          recycler
   */
  public void setRecycler(int pStackCameraDeviceIndex,
                          RecyclerInterface<StackInterface, StackRequest> pRecycler);

  /**
   * Sets the recycler that should be used by _all_ stack camera devices.
   * 
   * @param pRecycler
   *          recyler to be used by all camera devices
   */
  public void setRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

  /**
   * Returns the recycler currently b the stack camera device of given id.
   * 
   * @param pStackCameraDeviceIndex
   *          stack camera index id.
   * @return recycler.
   */
  public RecyclerInterface<StackInterface, StackRequest> getRecycler(int pStackCameraDeviceIndex);

  /**
   * Uses a recycler with given parameters. This recycler will be used for all
   * subsequent plays. if teh recycler does not exist yet, it is created.
   * 
   * @param pName
   *          recycler name
   * @param pMinimumNumberOfAvailableStacks
   *          minimum number of available stacks
   * @param pMaximumNumberOfAvailableStacks
   *          maximum number of available stacks
   * @param pMaximumNumberOfLiveStacks
   *          maximum number of live stacks
   */
  public void useRecycler(String pName,
                          int pMinimumNumberOfAvailableStacks,
                          int pMaximumNumberOfAvailableStacks,
                          int pMaximumNumberOfLiveStacks);

  /**
   * Clears a given recycler.
   * 
   * @param pName
   *          recycler name
   */
  public void clearRecycler(String pName);

  /**
   * Clears all recyclers.
   */
  public void clearAllRecyclers();

  /**
   * Plays queue for all devices, and waits for playback to finish.
   * 
   * @param pQueue
   *          queue to play
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          time unit for timeout
   * @return true if successful
   * @throws InterruptedException
   *           if interupted
   * @throws ExecutionException
   *           if execution occured during async execution
   * @throws TimeoutException
   *           if timeout
   */
  public Boolean playQueueAndWait(Q pQueue,
                                  long pTimeOut,
                                  TimeUnit pTimeUnit) throws InterruptedException,
                                                      ExecutionException,
                                                      TimeoutException;

  /**
   * Plays queue for all devices, waits for playback to finish as well as waits
   * for stacks to be delivered.
   * 
   * @param pQueue
   *          queue to play
   * @param pTimeOut
   *          timeout time
   * @param pTimeUnit
   *          timeout unit
   * @return true if successfull
   * @throws InterruptedException
   *           if interupted
   * @throws ExecutionException
   *           if execution occured during async execution
   * @throws TimeoutException
   *           if timeout
   */
  public Boolean playQueueAndWaitForStacks(Q pQueue,
                                           long pTimeOut,
                                           TimeUnit pTimeUnit) throws InterruptedException,
                                                               ExecutionException,
                                                               TimeoutException;

  /**
   * Returns the variable hlding the last played queue
   * 
   * @return last played queue variable
   */
  Variable<Q> getPlayedQueueVariable();

  /**
   * Returns the average timestamp for all stacks acquired during for last
   * played queue.
   * 
   * @return timestamp in nanoseconds
   */
  public long lastAcquiredStacksTimeStampInNS();

  /**
   * Returns the stack processing pipeline
   * 
   * @return stack processing pipeline
   */
  StackProcessingPipelineInterface getStackProcesssingPipeline();

  /**
   * Adds a given stack processor;
   * 
   * 
   * @param pStackProcessor
   *          stack processor
   * @param pRecyclerName
   *          recycler name (from stack recycler manager)
   * @param pMaximumNumberOfLiveObjects
   *          max num of live objects
   * @param pMaximumNumberOfAvailableObjects
   *          max num of available objects
   */
  public void addStackProcessor(StackProcessorInterface pStackProcessor,
                                String pRecyclerName,
                                int pMaximumNumberOfLiveObjects,
                                int pMaximumNumberOfAvailableObjects);

  /**
   * Removes a given stack processor;
   * 
   * @param pStackProcessor
   *          stack processor
   */
  void removeStackProcessor(StackProcessorInterface pStackProcessor);

  /**
   * Returns pieline index of given index.
   * 
   * @param pProcessorIndex
   *          processor index
   * @return processing pipeline
   */
  StackProcessorInterface getStackProcessor(int pProcessorIndex);

  /**
   * Returns Stack Variable for given stack camera index
   * 
   * @return Stack Variable
   */
  public Variable<StackInterface> getPipelineStackVariable();

  /**
   * Returns Stack Variable for given stack camera index
   * 
   * @param pIndex
   *          stack camera index
   * @return Stack Variable
   */
  public Variable<StackInterface> getCameraStackVariable(int pIndex);

  /**
   * Returns the size in nanometer (anisotropic XY) of a pixel. this is the
   * actual physical size in the sample - thus taking into account overall
   * magnification. The size is returned wrapped into a Variable.
   * 
   * @param pCameraIndex
   *          camera index
   * @return size in nanometer as a Variable
   */
  public Variable<Double> getCameraPixelSizeInNanometerVariable(int pCameraIndex);

  /**
   * Sets the main sample stage X position.
   * 
   * @param pXValue
   *          x position value
   */
  public void setStageX(double pXValue);

  /**
   * Sets the main sample stage Y position.
   * 
   * @param pYValue
   *          y position value
   */
  public void setStageY(double pYValue);

  /**
   * Sets the sample's main stage Z position.
   * 
   * @param pZValue
   *          z position value
   */
  public void setStageZ(double pZValue);

  /**
   * Sets the sample's main stage R position.
   * 
   * @param pRValue
   *          r position value
   */
  public void setStageR(double pRValue);

  /**
   * Returns the main sample stage X position.
   * 
   * @return x position value
   */
  public double getStageX();

  /**
   * Returns the main sample stage Y position.
   * 
   * @return y position value
   */
  public double getStageY();

  /**
   * Returns the sample's main stage Z position.
   * 
   * @return z position value
   */
  public double getStageZ();

  /**
   * Returns the sample's main stage R position.
   * 
   * @return r position value
   */
  public double getStageR();

  /**
   * Returns the main XYZR stage of this microscope.
   * 
   * @return main XYZR stage
   */
  public StageDeviceInterface getMainStage();

}
