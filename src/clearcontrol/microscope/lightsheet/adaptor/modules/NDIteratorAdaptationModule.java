package clearcontrol.microscope.lightsheet.adaptor.modules;

import java.util.Arrays;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.adaptor.utils.NDIterator;

/**
 * ND iterator adaptation module
 *
 * @author royer
 * @param <S>
 *          state type
 */
public abstract class NDIteratorAdaptationModule<S extends LightSheetAcquisitionStateInterface<S>>
                                                extends
                                                AdaptationModuleBase<S>
{

  private NDIterator mNDIterator;

  /**
   * Instanciates a ND iterator adaptation module
   * 
   * @param pModuleName
   *          module name
   */
  public NDIteratorAdaptationModule(String pModuleName)
  {
    super(pModuleName);
  }

  /**
   * Returns ND iterator
   * 
   * @return ND iterator
   */
  public NDIterator getNDIterator()
  {
    return mNDIterator;
  }

  /**
   * Sets the ND iterator
   * 
   * @param pNDIterator
   *          ND iterator
   */
  public void setNDIterator(NDIterator pNDIterator)
  {
    mNDIterator = pNDIterator;
  }

  @Override
  public int getNumberOfSteps()
  {
    return mNDIterator.getNumberOfIterations();
  };

  @Override
  public int getRemainingNumberOfSteps()
  {
    return mNDIterator.getRemainingNumberOfIterations();
  }

  @Override
  public void reset()
  {
    super.reset();

    getStatusStringVariable().set("");
    /*
     * derived classes must set the iterator:
     * setNDIterator(new NDIterator(...));
     */
  }

  @Override
  public Boolean apply(Void pVoid)
  {
    info("NDIteratorAdaptationModule step \n");

    boolean lHasNext = getNDIterator().hasNext();

    info("Has next step: %s \n", lHasNext);

    if (lHasNext)
    {
      int[] lNextStepCoordinates = getNDIterator().next();

      info("Next step: [%s] ->%s \n",
           getName(),
           Arrays.toString(lNextStepCoordinates));

      Future<?> lFuture = atomicStep(lNextStepCoordinates);

      getStatusStringVariable().set(Arrays.toString(lNextStepCoordinates));

      mListOfFuturTasks.add(lFuture);
    }

    return getNDIterator().hasNext();
  }

  /**
   * Performs an atomic step
   * 
   * @param pStepCoordinates
   *          step coordinates
   * 
   * @return future
   */
  public abstract Future<?> atomicStep(int... pStepCoordinates);

  @Override
  public boolean areAllTasksCompleted()
  {
    return super.isReady();
  }

  @Override
  public boolean areAllStepsCompleted()
  {
    return !getNDIterator().hasNext();
  }

  @Override
  public boolean isReady()
  {
    boolean lAllTasksCompleted = areAllTasksCompleted();
    boolean lAllStepsCompleted = areAllStepsCompleted();

    info("module: %s, all tasks completed: %s, all steps completed: %s \n",
         getName(),
         lAllTasksCompleted ? "yes" : "no",
         lAllStepsCompleted ? "yes" : "no");

    return lAllTasksCompleted && lAllStepsCompleted;
  }

}
