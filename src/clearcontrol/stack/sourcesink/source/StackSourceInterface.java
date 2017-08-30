package clearcontrol.stack.sourcesink.source;

import java.util.concurrent.TimeUnit;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import coremem.recycling.RecyclerInterface;

/**
 * Stack source interface
 *
 * @author royer
 */
public interface StackSourceInterface extends StackSinkSourceInterface
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
   * Returns the number of stacks in source for a given channel
   * 
   * @param pChannel
   *          channel
   * 
   * @return number of stacks
   */
  public long getNumberOfStacks(String pChannel);

  /**
   * Sets the stack recycler
   * 
   * @param pStackRecycler
   *          stack recycler
   */
  public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

  /**
   * Returns stack for default channel and given index
   * 
   * @param pStackIndex
   *          stack index
   * @return stack
   */
  public StackInterface getStack(long pStackIndex);

  /**
   * Returns stack for given channel and index
   * 
   * @param pChannel
   *          channel
   * 
   * @param pStackIndex
   *          stack index
   * @return stack
   */
  public StackInterface getStack(String pChannel, long pStackIndex);

  /**
   * Returns stack for given index.
   * 
   * @param pChannel
   *          channel
   * 
   * @param pStackIndex
   *          stack index
   * @param pTime
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return stack
   */
  public StackInterface getStack(String pChannel,
                                 final long pStackIndex,
                                 long pTime,
                                 TimeUnit pTimeUnit);

  /**
   * Returns stack time stamp in seconds for default channel and given index.
   * 
   * @param pStackIndex
   *          stack index
   * @return time stamp in seconds
   */
  public Double getStackTimeStampInSeconds(final long pStackIndex);

  /**
   * Returns stack time stamp in seconds for given channel and index.
   * 
   * @param pChannel
   *          channel
   * 
   * @param pStackIndex
   *          stack index
   * @return time stamp in seconds
   */
  public Double getStackTimeStampInSeconds(String pChannel,
                                           final long pStackIndex);

}
