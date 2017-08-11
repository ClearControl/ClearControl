package clearcontrol.microscope.stacks;

import java.util.concurrent.ConcurrentLinkedQueue;

import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

/**
 * Cleanup stack variable. This stack variable keeps a queue of 'received'
 * stacks and eventually releases the oldest received stacks.
 *
 * @author royer
 */
public class CleanupStackVariable extends Variable<StackInterface>
                                  implements LoggingFeature
{
  private ConcurrentLinkedQueue<StackInterface> mKeepStacksAliveQueue =
                                                                      new ConcurrentLinkedQueue<>();
  private int mNumberOfStacksToKeepAlive;

  /**
   * Instanciates a cleanup stack variable.
   * 
   * @param pVariableName
   *          variable name
   * @param pNumberOfStacksToKeepAlive
   *          numer of stacks to keep s
   */
  public CleanupStackVariable(String pVariableName,
                              int pNumberOfStacksToKeepAlive)
  {
    super(pVariableName, null);
    mNumberOfStacksToKeepAlive = pNumberOfStacksToKeepAlive;
  }

  @Override
  public StackInterface setEventHook(StackInterface pOldValue,
                                     StackInterface pNewValue)
  {
    if (pNewValue != null && !pNewValue.isReleased())
      mKeepStacksAliveQueue.add(pNewValue);

    while (mKeepStacksAliveQueue.size() > mNumberOfStacksToKeepAlive)
    {
      StackInterface lStackToRelease = mKeepStacksAliveQueue.remove();
      // info("Releasing stack: '%s'.",lStackToRelease);
      lStackToRelease.release();
    }

    return pNewValue;
  }
}