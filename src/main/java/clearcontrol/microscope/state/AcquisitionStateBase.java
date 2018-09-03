package clearcontrol.microscope.state;

import clearcontrol.core.device.NameableWithChangeListener;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * Base class providing common fields and methods for all acquisition state
 * implementations
 *
 * @param <M>
 *          microscope type
 * @param <Q>
 *          queue type
 * @author royer
 */
public abstract class AcquisitionStateBase<M extends MicroscopeInterface<Q>, Q extends QueueInterface>
                                          extends
                                          NameableWithChangeListener<AcquisitionStateInterface<M, Q>>
                                          implements
                                          AcquisitionStateInterface<M, Q>
{

  private final M mMicroscope;

  protected Q mQueue;

  private final BoundedVariable<Number> mExposureInSeconds =
                                                           new BoundedVariable<Number>("ExposureInSeconds",
                                                                                       0.020,
                                                                                       0.0,
                                                                                       100.0);

  private final BoundedVariable<Number> mImageWidthVariable =
                                                            new BoundedVariable<Number>("ImageWidth",
                                                                                        0.0);

  private final BoundedVariable<Number> mImageHeightVariable =
                                                             new BoundedVariable<Number>("mImageHeight",
                                                                                         0.0);

  private final BoundedVariable<Number> mStageXVariable =
                                                        new BoundedVariable<Number>("StageX",
                                                                                    0.0);

  private final BoundedVariable<Number> mStageYVariable =
                                                        new BoundedVariable<Number>("StageY",
                                                                                    0.0);

  private final BoundedVariable<Number> mStageZVariable =
                                                        new BoundedVariable<Number>("StageZ",
                                                                                    0.0);

  /**
   * Instantiates an acquisition state given a name and parent microscope
   * 
   * @param pName
   *          name
   * @param pMicroscope
   *          parent microscope
   */
  public AcquisitionStateBase(String pName, M pMicroscope)
  {
    super(pName);
    mMicroscope = pMicroscope;
  }

  /**
   * Sets the settings of this acquisition state (exp, width, height, stage
   * x,y,z, ...) to be identical to the given state.
   * 
   * @param pAcquisitionState
   *          state to copy settings from.
   */
  public void set(AcquisitionStateBase<M, Q> pAcquisitionState)
  {
    getExposureInSecondsVariable().set(pAcquisitionState.getExposureInSecondsVariable());
    getImageWidthVariable().set(pAcquisitionState.getImageWidthVariable());
    getImageHeightVariable().set(pAcquisitionState.getImageHeightVariable());

    getStageXVariable().set(pAcquisitionState.getStageXVariable());
    getStageYVariable().set(pAcquisitionState.getStageYVariable());
    getStageZVariable().set(pAcquisitionState.getStageZVariable());
  }

  /**
   * Returns the parent microscope
   * 
   * @return parent microscope
   */
  public M getMicroscope()
  {
    return mMicroscope;
  }

  @Override
  public BoundedVariable<Number> getExposureInSecondsVariable()
  {
    return mExposureInSeconds;
  }

  /**
   * Returns the centered ROI image height variable
   * 
   * @return image height variable
   */
  public BoundedVariable<Number> getImageWidthVariable()
  {
    return mImageWidthVariable;
  }

  /**
   * Returns the centered ROI image width variable
   * 
   * @return image width variable
   */
  public BoundedVariable<Number> getImageHeightVariable()
  {
    return mImageHeightVariable;
  }

  /**
   * Returns state variable x
   * 
   * @return stage variable x
   */
  public BoundedVariable<Number> getStageXVariable()
  {
    return mStageXVariable;
  }

  /**
   * Returns state variable y
   * 
   * @return stage variable y
   */
  public BoundedVariable<Number> getStageYVariable()
  {
    return mStageYVariable;
  }

  /**
   * Returns state variable z
   * 
   * @return stage variable z
   */
  public BoundedVariable<Number> getStageZVariable()
  {
    return mStageZVariable;
  }

}
