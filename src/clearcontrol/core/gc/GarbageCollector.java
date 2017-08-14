package clearcontrol.core.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import clearcontrol.core.log.LoggingFeature;

/**
 * GarbageCollector handles programatic triggering of GC and monitoring of GC
 * events
 * 
 * NOTE: some code from : http://www.fasterj.com/articles/gcnotifs.shtml
 * 
 * @author royer
 *
 */
public class GarbageCollector implements LoggingFeature
{

  static GarbageCollector sGarbageCollector;

  static volatile boolean sGCDebugOutputFlag = false;

  static
  {
    sGarbageCollector = new GarbageCollector();

    sGarbageCollector.installGCNotifier();
  }

  /**
   * Returns singleton garbage collector
   * 
   * @return singleton garbage collector
   */
  public static GarbageCollector getSingletonGarbageCollector()
  {
    return sGarbageCollector;
  }

  /**
   * Adds a garbage collection notification listener
   * 
   * @param pNotificationListener
   *          garbage collection notification listener
   */
  public void addGCNotificationListener(NotificationListener pNotificationListener)
  {
    info("Adding GC notification listener: %s",
         pNotificationListener);

    // get all the GarbageCollectorMXBeans - there's one for each heap
    // generation
    // so probably two - the old generation and young generation
    List<GarbageCollectorMXBean> gcbeans =
                                         java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();

    // Install a notifcation handler for each bean
    for (GarbageCollectorMXBean gcbean : gcbeans)
    {
      // System.out.println(gcbean);
      NotificationEmitter emitter = (NotificationEmitter) gcbean;
      // use an anonymously generated listener for this example
      // - proper code should really use a named class

      // Add the listener
      emitter.addNotificationListener(pNotificationListener,
                                      null,
                                      null);
    }
  }

  /**
   * Sets the debug output flag
   * 
   * @param pGCDebugOutputFlag
   *          new debug output flag value
   */
  public static void setDebugOutputFlag(boolean pGCDebugOutputFlag)
  {
    sGarbageCollector.info("Setting GC debug output notification flag to %s",
                           pGCDebugOutputFlag);
    sGCDebugOutputFlag = pGCDebugOutputFlag;
  }

  private void installGCNotifier()
  {
    info("Adding Debug GC notification listener");
    NotificationListener lDebugGCNotificationListener =
                                                      getDebugGCNotificationListener();
    addGCNotificationListener(lDebugGCNotificationListener);
  }

  private NotificationListener getDebugGCNotificationListener()
  {
    NotificationListener listener =
                                  new DebugGCNotificationListener(this);
    return listener;
  }

  /**
   * Triggers GC
   */
  public static void trigger()
  {
    // sGarbageCollector.info("Garbage collection started.");
    System.gc();
    // sGarbageCollector.info("Garbage collection finished.");
  }

}
