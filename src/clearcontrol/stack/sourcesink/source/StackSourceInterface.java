package clearcontrol.stack.sourcesink.source;

import java.util.concurrent.TimeUnit;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Stack source interface
 *
 * @author royer
 */
public interface StackSourceInterface
{

  /**
   * Updates the information available to this source about the number of stacks
   * available, etc..
   * 
   * @return true -> success
   */
  public boolean update();

  /**
   * Returns the number of stacks in source
   * 
   * @return number of stacks
   */
  public long getNumberOfStacks();

  /**
   * Sets the stack recycler
   * 
   * @param pStackRecycler
   *          stack recycler
   */
  public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

  /**
   * Returns stack for given index
   * 
   * @param pStackIndex
   *          stack index
   * @return stack
   */
  public StackInterface getStack(long pStackIndex);

  /**
   * Returns stack for given index.
   * 
   * @param pStackIndex
   *          stack index
   * @param pTime
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return stack
   */
  public StackInterface getStack(final long pStackIndex,
                                 long pTime,
                                 TimeUnit pTimeUnit);

  /**
   * Returns satck time stamp in seconds
   * 
   * @param pStackIndex
   *          stack index
   * @return time stamp in seconds
   */
  public double getStackTimeStampInSeconds(final long pStackIndex);

}
