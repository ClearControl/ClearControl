package clearcontrol.microscope.state;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.name.ReadOnlyNameableInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * The acquisition state manager handles a set of saved acquisition states.
 * These states are used for acquisition purposes.
 * 
 * @author royer
 *
 */
public class AcquisitionStateManager extends VirtualDevice implements
                                     ReadOnlyNameableInterface,
                                     LoggingInterface
{
  private final MicroscopeInterface mMicroscopeInterface;

  private CopyOnWriteArrayList<AcquisitionStateInterface<?>> mAcquisitionStateList =
                                                                                   new CopyOnWriteArrayList<>();

  private volatile AcquisitionStateInterface<?> mCurrentState;

  /**
   * Constructs an LoggingManager.
   * 
   * @param pMicroscopeInterface
   *          microscope interface
   */
  public AcquisitionStateManager(MicroscopeInterface pMicroscopeInterface)
  {
    super("AcquisitionStateManager");
    mMicroscopeInterface = pMicroscopeInterface;
  }

  /**
   * Returns microscope
   * 
   * @return microscope
   */
  public MicroscopeInterface getMicroscope()
  {
    return mMicroscopeInterface;
  }

  @Override
  public String getName()
  {
    return "AcquisitionStateManager";
  }

  /**
   * Returns current state
   * 
   * @return current state
   */
  public AcquisitionStateInterface<?> getCurrentState()
  {
    return mCurrentState;
  }

  /**
   * Sets current state.
   * 
   * @param pCurrentState
   *          new current state
   */
  public void setCurrentState(AcquisitionStateInterface<?> pCurrentState)
  {
    if (pCurrentState != null)
    {
      if (!mAcquisitionStateList.contains(pCurrentState))
        mAcquisitionStateList.add(pCurrentState);
      info("setCurrent: " + pCurrentState.getName());
      mCurrentState = pCurrentState;
      notifyListeners(this);
    }
  }

  /**
   * Adds a state.
   * 
   * @param pState
   *          stet to add
   */
  public void addState(AcquisitionStateInterface<?> pState)
  {
    mAcquisitionStateList.add(pState);
    notifyListeners(this);
  }

  /**
   * Removes a state
   * 
   * @param pState
   *          state to remove
   */
  public void removeState(AcquisitionStateInterface<?> pState)
  {
    mAcquisitionStateList.remove(pState);
    notifyListeners(this);
  }

  /**
   * Clears all states
   * 
   * @param pState
   *          state to clear
   */
  public void clearStates(AcquisitionStateInterface<?> pState)
  {
    mAcquisitionStateList.clear();
    notifyListeners(this);
  }

  /**
   * Returns the state list (unmodifiable).
   * 
   * @return unmodifiable state list
   */
  public List<AcquisitionStateInterface<?>> getStateList()
  {
    return Collections.unmodifiableList(mAcquisitionStateList);
  }

}
