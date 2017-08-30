package clearcontrol.stack.sourcesink.server;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.sink.StackSinkInterface;
import clearcontrol.stack.sourcesink.source.StackSourceInterface;
import coremem.recycling.RecyclerInterface;

/**
 * Stack RAM server
 *
 * @author royer
 */
public class StackRAMServer extends StackServerBase implements
                            StackSinkInterface,
                            StackSourceInterface
{

  private ConcurrentHashMap<String, ArrayList<StackInterface>> mStackMap =
                                                                         new ConcurrentHashMap<>();

  private Long mStartTimeStampInNanoseconds = null;

  /**
   * Instantiates a stack RAM server
   */
  public StackRAMServer()
  {
    super();
  }

  @Override
  public boolean update()
  {
    return true;
  }

  @Override
  public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
  }

  @Override
  public StackInterface getStack(String pChannel,
                                 final long pStackIndex,
                                 long pTime,
                                 TimeUnit pTimeUnit)
  {
    return getStack(pStackIndex);
  }

  @Override
  public StackInterface getStack(long pStackIndex)
  {
    return getStack(cDefaultChannel, pStackIndex);
  }

  @Override
  public StackInterface getStack(String pChannel, long pStackIndex)
  {
    ArrayList<StackInterface> lChannelStackList =
                                                mStackMap.get(pChannel);

    if (lChannelStackList == null)
      return null;

    return lChannelStackList.get(toIntExact(pStackIndex));
  }

  @Override
  public boolean appendStack(final StackInterface pStack)
  {
    return appendStack(cDefaultChannel, pStack);
  }

  @Override
  public boolean appendStack(String pChannel,
                             final StackInterface pStack)
  {
    long lNowInNanoseconds = System.nanoTime();
    if (mStartTimeStampInNanoseconds == null)
      mStartTimeStampInNanoseconds = lNowInNanoseconds;

    ArrayList<StackInterface> lChannelStackList =
                                                mStackMap.get(pChannel);
    if (lChannelStackList == null)
    {
      lChannelStackList = new ArrayList<StackInterface>();
      mStackMap.put(pChannel, lChannelStackList);
    }

    lChannelStackList.add(pStack);

    int lIndexLastAddedStack = lChannelStackList.size() - 1;

    double lTimeStampInSeconds = 1e-9
                                 * (lNowInNanoseconds
                                    - mStartTimeStampInNanoseconds);

    setStackTimeStampInSeconds(pChannel,
                               lIndexLastAddedStack,
                               lTimeStampInSeconds);

    return true;
  }

  @Override
  public void close() throws Exception
  {
    // do nothing
  }

}
