package clearcontrol.microscope.lightsheet.adaptor.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.adaptor.Adaptator;

/**
 * Base class providing common fields and methods for all adaptation modules
 *
 * @author royer
 * @param <S>
 *          state type
 */
public abstract class AdaptationModuleBase<S extends LightSheetAcquisitionStateInterface<S>>
                                          extends NameableBase
                                          implements
                                          AdaptationModuleInterface<S>,
                                          LoggingInterface
{

  private Adaptator<S> mAdaptator;

  private int mPriority = 1;

  protected ArrayList<Future<?>> mListOfFuturTasks =
                                                   new ArrayList<>();

  private final Variable<Boolean> mIsActiveVariable =
                                                    new Variable<>("IsActive",
                                                                   true);

  private final Variable<String> mStatusStringVariable =
                                                       new Variable<>("Status",
                                                                      "");

  /**
   * Instanciate an adaptation module given a name
   * 
   * @param pName
   *          name
   */
  public AdaptationModuleBase(String pName)
  {
    super(pName);
  }

  @Override
  public void setAdaptator(Adaptator<S> pAdaptator)
  {
    mAdaptator = pAdaptator;
  }

  @Override
  public Adaptator<S> getAdaptator()
  {
    return mAdaptator;
  }

  @Override
  public void setPriority(int pPriority)
  {
    mPriority = pPriority;
  }

  @Override
  public int getPriority()
  {
    return mPriority;
  }

  @Override
  public abstract Boolean apply(Void pVoid);

  @Override
  public Variable<Boolean> getIsActiveVariable()
  {
    return mIsActiveVariable;
  }

  @Override
  public Variable<String> getStatusStringVariable()
  {
    return mStatusStringVariable;
  }

  @Override
  public boolean isReady()
  {
    boolean lAllDone = true;
    for (Future<?> lTask : mListOfFuturTasks)
      if (lTask != null)
      {
        boolean lDone = lTask.isDone();
        lAllDone &= lDone;
        // if (!lDone)
        // info("Task: %s not done yet", lTask);
      }

    return lAllDone;
  }

  @Override
  public void reset()
  {
    mListOfFuturTasks.clear();
  }

  @Override
  public String toString()
  {
    return String.format("AdaptationModuleBase [getName()=%s, getPriority()=%s, getIsActiveVariable()=%s, isReady()=%s]",
                         getName(),
                         getPriority(),
                         getIsActiveVariable(),
                         isReady());
  }

}
