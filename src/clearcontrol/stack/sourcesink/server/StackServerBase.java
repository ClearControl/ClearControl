package clearcontrol.stack.sourcesink.server;

import java.util.HashMap;

import clearcontrol.stack.StackRequest;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;

/**
 * Stack server base
 *
 * @author royer
 */
public abstract class StackServerBase implements AutoCloseable
{
  protected final TLongDoubleHashMap mStackIndexToTimeStampInSecondsMap =
                                                                        new TLongDoubleHashMap();
  protected final TLongLongHashMap mStackIndexToBinaryFilePositionMap =
                                                                      new TLongLongHashMap();
  protected final HashMap<Long, StackRequest> mStackIndexToStackRequestMap =
                                                                           new HashMap<Long, StackRequest>();

  /**
   * Instanciates a stack server base
   */
  public StackServerBase()
  {
    super();
  }

  /**
   * Returns the number of stacks
   * 
   * @return number of stacks
   */
  public long getNumberOfStacks()
  {
    return mStackIndexToTimeStampInSecondsMap.size();
  }

  /**
   * Returns - for a given stack index - the stack's time stamp in seconds
   * 
   * @param pStackIndex
   *          stack idnex
   * @return stack's time stamp in seconds
   */
  public double getStackTimeStampInSeconds(final long pStackIndex)
  {
    return mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
  }

}
