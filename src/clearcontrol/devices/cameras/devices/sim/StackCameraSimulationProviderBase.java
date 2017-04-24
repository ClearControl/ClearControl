package clearcontrol.devices.cameras.devices.sim;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 *
 *
 * @author royer
 */
public abstract class StackCameraSimulationProviderBase implements
                                                        StackCameraSimulationProvider
{
  private final long mGetStackTimeOutInSeconds;

  /**
   * Instantiates a stack camera simulation provider
   */
  public StackCameraSimulationProviderBase()
  {
    this(Long.MAX_VALUE);
  }

  /**
   * Instantiates a stack camera simulation provider
   * 
   * @param pGetStackTimeOut
   *          timeout in seconds
   */
  public StackCameraSimulationProviderBase(long pGetStackTimeOut)
  {
    super();
    mGetStackTimeOutInSeconds = pGetStackTimeOut;
  }

  @Override
  public StackInterface getStack(RecyclerInterface<StackInterface, StackRequest> pRecycler,
                                 StackCameraSimulationQueue pQueue)
  {
    VariableQueueBase lVariableStateQueues = pQueue;

    ArrayList<Boolean> lKeepPlaneList =
                                      lVariableStateQueues.getVariableQueue(pQueue.getKeepPlaneVariable());

    long lNumberOfKeptImages = sum(lKeepPlaneList);

    final long lWidth = max(1,
                            pQueue.getStackWidthVariable()
                                  .get()
                                  .longValue());
    final long lHeight = max(1,
                             pQueue.getStackHeightVariable()
                                   .get()
                                   .longValue());

    final long lDepth = max(1, lNumberOfKeptImages);

    final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                          lHeight,
                                                          lDepth);

    final StackInterface lStack =
                                pRecycler.getOrWait(mGetStackTimeOutInSeconds,
                                                    TimeUnit.SECONDS,
                                                    lStackRequest);

    if (lStack != null)
    {
      fillStackData(pQueue,
                    lKeepPlaneList,
                    lWidth,
                    lHeight,
                    lDepth,
                    lStack);
    }

    return lStack;
  }

  protected abstract void fillStackData(StackCameraSimulationQueue pQueue,
                                        ArrayList<Boolean> pKeepPlaneList,
                                        long pWidth,
                                        long pHeight,
                                        long pDepth,
                                        StackInterface pStack);

  /**
   * @param pKeepPlaneList
   * @return
   */
  private long sum(ArrayList<Boolean> pKeepPlaneList)
  {
    int lLength = pKeepPlaneList.size();
    long sum = 0;
    for (int i = 0; i < lLength; i++)
      sum += pKeepPlaneList.get(i) ? 1 : 0;
    return sum;
  }

  protected double fract(double x)
  {
    return x - Math.floor(x);
  }

  protected double clamp(double x, double pMin, double pMax)
  {
    return Math.max(Math.min(x, pMax), pMin);
  }

}
