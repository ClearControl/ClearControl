package clearcontrol.microscope.state;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.device.change.ChangeListeningBase;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * AcquisitionStateManager handles a set of saved acquisition states. These
 * states are used for acquisition purposes.
 * 
 * @author royer
 *
 */
public class AcquisitionStateManager extends
																		ChangeListeningBase<AcquisitionStateManager>
{
	private final MicroscopeInterface mMicroscopeInterface;

	private CopyOnWriteArrayList<AcquisitionStateInterface<?>> mAcquisitionStateList = new CopyOnWriteArrayList<>();

	private volatile AcquisitionStateInterface<?> mCurrentState;

	/**
	 * Constructs an AcquisitionStateManager.
	 */
	public AcquisitionStateManager(MicroscopeInterface pMicroscopeInterface)
	{
		super();
		mMicroscopeInterface = pMicroscopeInterface;
	}

	public void setCurrent(AcquisitionStateInterface<?> pCurrentState)
	{
		if (pCurrentState != null)
		{
			System.out.println("setCurrent: " + pCurrentState.getName());
			mCurrentState = pCurrentState;
		}
	}

	/**
	 * Adds a state.
	 * 
	 * @param pState
	 */
	public void addState(AcquisitionStateInterface<?> pState)
	{
		mAcquisitionStateList.add(pState);
		notifyListeners();
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
		notifyListeners();
	}

	/**
	 * Clears all states
	 * 
	 * @param pState
	 */
	public void clearStates(AcquisitionStateInterface<?> pState)
	{
		mAcquisitionStateList.clear();
		notifyListeners();
	}

	/**
	 * Returns the state list (unmodifiable).
	 * 
	 * @return
	 * 
	 * @return
	 */
	public List<AcquisitionStateInterface<?>> getStateList()
	{
		return Collections.unmodifiableList(mAcquisitionStateList);
	}

	public MicroscopeInterface getMicroscope()
	{
		return mMicroscopeInterface;
	}

}
