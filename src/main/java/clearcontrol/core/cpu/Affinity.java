package clearcontrol.core.cpu;

import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;

/**
 * CPU affinity
 *
 * @author royer
 */
public class Affinity
{
  /**
   * Affinity thread factory that creates threads on same core
   */
  public static final AffinityThreadFactory cSameCoreAfinityThreadFactory =
                                                                          new AffinityThreadFactory("SameCore",
                                                                                                    AffinityStrategies.SAME_CORE);
  /**
   * Affinity thread factory that creates threads on a different core
   */
  public static final AffinityThreadFactory cDifferentCoreAfinityThreadFactory =
                                                                               new AffinityThreadFactory("DifferentCore",
                                                                                                         AffinityStrategies.DIFFERENT_CORE);

  /**
   * Affinity thread factory that creates threads on the same socket
   */
  public static final AffinityThreadFactory cSameSocketAfinityThreadFactory =
                                                                            new AffinityThreadFactory("SameSocket",
                                                                                                      AffinityStrategies.SAME_SOCKET);
  /**
   * Affinity thread factory that creates threads on a different socket
   */
  public static final AffinityThreadFactory cDifferentSocketAfinityThreadFactory =
                                                                                 new AffinityThreadFactory("DifferentSocket",
                                                                                                           AffinityStrategies.DIFFERENT_SOCKET);

  /**
   * Creates a pinned thread on same core
   * 
   * @param pName
   *          thread name
   * @param pRunnable
   *          runnable
   * @return thread
   */
  public static final Thread createPinnedThreadOnSameCore(String pName,
                                                          Runnable pRunnable)
  {

    return createPinnedThread(cSameCoreAfinityThreadFactory,
                              pName,
                              pRunnable);
  }

  /**
   * Creates a pinned thread using a given affinity thread factory
   * 
   * @param pAffinityThreadFactory
   *          affinity thread factory
   * @param pName
   *          thread name
   * @param pRunnable
   *          runnable
   * @return thread
   */
  public static final Thread createPinnedThread(AffinityThreadFactory pAffinityThreadFactory,
                                                String pName,
                                                Runnable pRunnable)
  {
    Thread lNewThread = pAffinityThreadFactory.newThread(pRunnable);
    lNewThread.setName(pName);
    return lNewThread;
  }
}
