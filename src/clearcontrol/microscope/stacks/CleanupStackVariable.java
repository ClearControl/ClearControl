package clearcontrol.microscope.stacks;

import java.util.concurrent.ConcurrentLinkedQueue;

import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

public class CleanupStackVariable extends Variable<StackInterface>
{
  private ConcurrentLinkedQueue<StackInterface> mKeepStacksAliveQueue =
                                                                      new ConcurrentLinkedQueue<>();
  private int mNumberOfStacksToKeepAlive;

  public CleanupStackVariable(String pVariableName,
                              StackInterface pReference,
                              int pNumberOfStacksToKeepAlive)
  {
    super(pVariableName, pReference);
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
      // System.out.println("RELEASING:" + lStackToRelease);
      lStackToRelease.release();
    }

    return pNewValue;
  }
}