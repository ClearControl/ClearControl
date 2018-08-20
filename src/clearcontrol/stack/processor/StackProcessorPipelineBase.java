package clearcontrol.stack.processor;

import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Base class for stack processing pipelines
 *
 * @author royer
 */
public class StackProcessorPipelineBase extends NameableBase
                                        implements
                                        StackProcessingPipelineInterface
{
  private StackRecyclerManager mStackRecyclerManager;

  protected final CopyOnWriteArrayList<StackProcessorInterface> mProcessorList =
                                                                               new CopyOnWriteArrayList<>();
  protected final CopyOnWriteArrayList<RecyclerInterface<StackInterface, StackRequest>> mRecyclerList =
                                                                                                      new CopyOnWriteArrayList<>();

  private Variable<StackInterface> mInputVariable;
  private Variable<StackInterface> mOutputVariable;

  /**
   * Instantiates a stack processor given a stack recycler manager
   * 
   * @param pName
   *          name
   * 
   * @param pStackRecyclerManager
   *          stack recycler manager
   */
  public StackProcessorPipelineBase(String pName,
                                    StackRecyclerManager pStackRecyclerManager)
  {
    super(pName);
    mStackRecyclerManager = pStackRecyclerManager;
    mInputVariable = new Variable<StackInterface>("inputVariable");
    mOutputVariable = new Variable<StackInterface>("OutputVariable");
  }

  @Override
  public void addStackProcessor(StackProcessorInterface pStackProcessor,
                                String pRecyclerName,
                                int pMaximumNumberOfLiveObjects,
                                int pMaximumNumberOfAvailableObjects)
  {
    RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                              mStackRecyclerManager.getRecycler(pRecyclerName,
                                                                                                pMaximumNumberOfLiveObjects,
                                                                                                pMaximumNumberOfAvailableObjects);
    mRecyclerList.add(lRecycler);
    mProcessorList.add(pStackProcessor);
  }

  @Override
  public StackProcessorInterface getStackProcessor(int pProcessorIndex)
  {
    return mProcessorList.get(pProcessorIndex);
  }

  @Override
  public void removeStackProcessor(StackProcessorInterface pStackProcessor)
  {
    final int lIndex = mProcessorList.indexOf(pStackProcessor);
    mProcessorList.remove(pStackProcessor);
    mRecyclerList.remove(lIndex);
  }

  @Override
  public Variable<StackInterface> getInputVariable()
  {
    return mInputVariable;
  }

  @Override
  public Variable<StackInterface> getOutputVariable()
  {
    return mOutputVariable;
  }

  protected StackInterface doProcess(StackInterface pInput)
  {
    StackInterface lStack = pInput;
    for (int i = 0; i < mProcessorList.size(); i++)
    {
      final StackProcessorInterface lProcessor =
                                               mProcessorList.get(i);
      if (lProcessor.isActive())
      {

        final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                        mRecyclerList.get(i);

        if (lStack == null)
          return null;

        lStack = lProcessor.process(lStack, lRecycler);

      }
    }
    return lStack;
  }

  @Deprecated
  public RecyclerInterface<StackInterface, StackRequest> getRecyclerOfProcessor(StackProcessorInterface pProcessor)
  {
    for (int i = 0; i < mProcessorList.size()
                    && i < mRecyclerList.size(); i++)
    {
      if (mProcessorList.get(i) == pProcessor)
      {
        return mRecyclerList.get(i);
      }
    }
    return null;
  }

}
