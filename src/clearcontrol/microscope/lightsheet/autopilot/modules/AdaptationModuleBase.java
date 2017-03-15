package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.autopilot.AutoPilot;

public abstract class AdaptationModuleBase implements
                                           AdaptationModuleInterface
{
  private int mPriority = 1;

  protected ArrayList<Future<?>> mListOfFuturTasks =
                                                   new ArrayList<>();

  private AutoPilot mLSMAdaptator;

  @Override
  public void setAdaptator(AutoPilot pLSMAdaptator)
  {
    mLSMAdaptator = pLSMAdaptator;
  }

  @Override
  public AutoPilot getAdaptator()
  {
    return mLSMAdaptator;
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

  /**
   * Interface method implementation
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder lBuilder = new StringBuilder();
    lBuilder.append("AdaptationModuleBase [mPriority=");
    lBuilder.append(mPriority);
    lBuilder.append(", mListOfFuturTasks=");
    lBuilder.append(mListOfFuturTasks);
    lBuilder.append(", mLSMAdaptator=");
    lBuilder.append(mLSMAdaptator);
    lBuilder.append(", isReady()=");
    lBuilder.append(isReady());
    lBuilder.append("]");
    return lBuilder.toString();
  };

}
