package clearcontrol.microscope.state;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.name.ReadOnlyNameableInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
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
                                    LoggingFeature
{
  private final MicroscopeInterface<?> mMicroscopeInterface;

  private CopyOnWriteArrayList<S> mAcquisitionStateList =
                                                        new CopyOnWriteArrayList<>();

  private final Variable<S> mCurrentStateVariable =
                                                  new Variable<>("CurrentState",
                                                                 null);

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

    getCurrentStateVariable().addSetListener((o, n) -> {
      if (n != null)
      {
        if (!mAcquisitionStateList.contains(n))
          mAcquisitionStateList.add(n);
        info("setCurrent: " + n.getName());
        notifyListeners(this);
      }
    });

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
    return mCurrentStateVariable.get();
  }

  /**
   * ConvenienceSets current state.
   * 
   * @param pCurrentState
   *          new current state
   */
  public void setCurrentState(S pCurrentState)
  {
    mCurrentStateVariable.set(pCurrentState);
  }

  /**
   * Returns the current state variable
   * 
   * @return current state variable
   */
  public Variable<S> getCurrentStateVariable()
  {
    return mCurrentStateVariable;
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
