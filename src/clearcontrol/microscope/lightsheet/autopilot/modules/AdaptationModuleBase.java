package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.autopilot.Adaptator;

/**
 * Base class providing common fields and methods for all adaptation modules
 *
 * @author royer
 */
public abstract class AdaptationModuleBase implements
                                           AdaptationModuleInterface
{
  private int mPriority = 1;

  protected ArrayList<Future<?>> mListOfFuturTasks =
                                                   new ArrayList<>();

  private Adaptator mAdaptator;

  @Override
  public void setAdaptator(Adaptator pAdaptator)
  {
    mAdaptator = pAdaptator;
  }

  @Override
  public Adaptator getAdaptator()
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
  public boolean isReady()
  {
    boolean lAllDone = true;
    for (Future<?> lTask : mListOfFuturTasks)
      if (lTask != null)
        lAllDone &= lTask.isDone();

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
    StringBuilder lBuilder = new StringBuilder();
    lBuilder.append("AdaptationModuleBase [mPriority=");
    lBuilder.append(mPriority);
    lBuilder.append(", mListOfFuturTasks=");
    lBuilder.append(mListOfFuturTasks);
    lBuilder.append(", mLSMAdaptator=");
    lBuilder.append(mAdaptator);
    lBuilder.append(", isReady()=");
    lBuilder.append(isReady());
    lBuilder.append("]");
    return lBuilder.toString();
  };

}
