package clearcontrol.microscope.adaptive.test;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.adaptive.modules.NDIteratorAdaptationModule;
import clearcontrol.microscope.adaptive.utils.NDIterator;

/**
 * Adaptation module used for testing purposes
 *
 * @author royer
 */
public class AdaptationTestModule extends
                                  NDIteratorAdaptationModule<TestState>
                                  implements
                                  AdaptationModuleInterface<TestState>,
                                  LoggingFeature
{
  private int[] mDimensions;

  /**
   * Instanciates a test module given a name.
   * 
   * @param pModuleName
   *          module name
   * @param pDimensions
   *          iterator dimensions
   * 
   */
  public AdaptationTestModule(String pModuleName, int... pDimensions)
  {
    super(pModuleName);
    mDimensions = pDimensions;
    reset();
  }

  @Override
  public void reset()
  {
    super.reset();
    setNDIterator(new NDIterator(mDimensions));

  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    info("!!!TESTMODULE %s - ACQUIRING DATA... !!: step: %s \n",
         getName(),
         Arrays.toString(pStepCoordinates));
    // here we would 'acquire' the data, takes a bit of time... tada...
    ThreadSleep.sleep(100 * pStepCoordinates.length,
                      TimeUnit.MILLISECONDS);

    // this runnable does the analysis on thw data... takes some time too, but
    // can be done asynchronously
    Runnable lRunnable = () -> {
      info("!!!TESTMODULE %s - PROCESSING DATA... !!: step:  %s \n",
           getName(),
           Arrays.toString(pStepCoordinates));
      System.out.print("Tic");
      ThreadSleep.sleep(1, TimeUnit.SECONDS);
      System.out.println("Tac");

    };

    return executeAsynchronously(lRunnable);
  }

  @Override
  public void updateState(TestState pStateToUpdate)
  {
    info("!!!TESTMODULE %s - UPDATING NEW STATE... !!", getName());
  }

}
