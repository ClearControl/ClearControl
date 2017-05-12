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
 * @param <S>
 *          state
 */
public class AcquisitionStateManager<S extends AcquisitionStateInterface<?, ?>>
                                    extends VirtualDevice implements
                                    ReadOnlyNameableInterface,
                                    LoggingInterface
{
  private final MicroscopeInterface<?> mMicroscopeInterface;

  private CopyOnWriteArrayList<S> mAcquisitionStateList =
                                                        new CopyOnWriteArrayList<>();

  private volatile S mCurrentState;

  /**
   * Constructs an LoggingManager.
   * 
   * @param pMicroscopeInterface
   *          microscope interface
   */
  public AcquisitionStateManager(MicroscopeInterface<?> pMicroscopeInterface)
  {
    super("Acquisition State Manager");
    mMicroscopeInterface = pMicroscopeInterface;
  }

  /**
   * Returns microscope
   * 
   * @return microscope
   */
  public MicroscopeInterface<?> getMicroscope()
  {
    return mMicroscopeInterface;
  }

  /**
   * Returns current state
   * 
   * @return current state
   */
  public S getCurrentState()
  {
    return mCurrentState;
  }

  /**
   * Sets current state.
   * 
   * @param pCurrentState
   *          new current state
   */
  public void setCurrentState(S pCurrentState)
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
  public void addState(S pState)
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
  public void removeState(S pState)
  {
    mAcquisitionStateList.remove(pState);
    notifyListeners(this);
  }

  /**
   * Removes all states except the one given
   * 
   * @param pState
   *          state to keep
   */
  public void removeOtherStates(S pState)
  {
    mAcquisitionStateList.clear();
    mAcquisitionStateList.add(pState);
    notifyListeners(this);
  }

  /**
   * Clears all states
   * 
   * @param pState
   *          state to clear
   */
  public void clearStates(S pState)
  {
    mAcquisitionStateList.clear();
    notifyListeners(this);
  }

  /**
   * Returns the state list (unmodifiable).
   * 
   * @return unmodifiable state list
   */
  public List<S> getStateList()
  {
    return Collections.unmodifiableList(mAcquisitionStateList);
  }

}
