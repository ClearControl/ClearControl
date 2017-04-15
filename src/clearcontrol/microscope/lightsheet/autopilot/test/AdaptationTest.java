package clearcontrol.microscope.lightsheet.autopilot.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.lightsheet.autopilot.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.autopilot.modules.NDIteratorAdaptationModule;

/**
 * Adaptation module used for testing
 *
 * @author royer
 */
public class AdaptationTest extends NDIteratorAdaptationModule
                            implements AdaptationModuleInterface
{

  /**
   * Instanciates a test adaptation module given a number od smaple san
   * dprobabiliy threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public AdaptationTest(int pNumberOfSamples,
                        double pProbabilityThreshold)
  {
    super(pNumberOfSamples, pProbabilityThreshold);

  }

  @Override
  public Future<?> atomicStep(int pControlPlaneIndex,
                              int pLightSheetIndex,
                              int pNumberOfSamples)
  {
    System.out.format("cpi=%d, lsi=%d, ns=%d \n",
                      pControlPlaneIndex,
                      pLightSheetIndex,
                      pNumberOfSamples);

    Runnable lRunnable =
                       () -> ThreadUtils.sleep(1,
                                               TimeUnit.MILLISECONDS);

    Future<?> lFuture =
                      getAdaptator().executeAsynchronously(lRunnable);

    return lFuture;
  }

  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {
    System.out.format("cpi=%d, lsi=%d, agmaxlist=%s \n",
                      pControlPlaneIndex,
                      pLightSheetIndex,
                      pArgMaxList.toString());
  }

}
