package clearcontrol.core.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import clearcontrol.core.log.LoggingInterface;

import com.sun.management.GarbageCollectionNotificationInfo;

/**
 * GarbageCollector handles programatic triggering of GC and monitoring of GC
 * events
 * 
 * NOTE: some code from : http://www.fasterj.com/articles/gcnotifs.shtml
 * 
 * @author royer
 *
 */
public class GarbageCollector implements LoggingInterface
{

  static GarbageCollector sGarbageCollector;

  static volatile boolean sGCDebugOutputFlag = false;

  static
  {
    sGarbageCollector = new GarbageCollector();

    sGarbageCollector.installGCNotifier();
  }

  public static GarbageCollector getSingletonGarbageCollector()
  {
    return sGarbageCollector;
  }

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
      System.out.println(gcbean);
      NotificationEmitter emitter = (NotificationEmitter) gcbean;
      // use an anonymously generated listener for this example
      // - proper code should really use a named class

      // Add the listener
      emitter.addNotificationListener(pNotificationListener,
                                      null,
                                      null);
    }
  }

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
    NotificationListener listener = new NotificationListener()
    {
      // keep a count of the total time spent in GCs
      long totalGcDuration = 0;

      // implement the notifier callback handler
      @Override
      public void handleNotification(Notification notification,
                                     Object handback)
      {
        if (!sGCDebugOutputFlag)
          return;

        // we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
        if (notification.getType()
                        .equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION))
        {
          // get the information associated with this notification
          GarbageCollectionNotificationInfo info =
                                                 GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
          // get all the info and pretty print it
          long duration = info.getGcInfo().getDuration();
          String gctype = info.getGcAction();
          if ("end of minor GC".equals(gctype))
          {
            gctype = "Young Gen GC";
          }
          else if ("end of major GC".equals(gctype))
          {
            gctype = "Old Gen GC";
          }

          info("Garbage Collection event: " + gctype
               + ": - "
               + info.getGcInfo().getId()
               + " "
               + info.getGcName()
               + " (from "
               + info.getGcCause()
               + ") "
               + duration
               + " microseconds; start-end times "
               + info.getGcInfo().getStartTime()
               + "-"
               + info.getGcInfo().getEndTime());
          // System.out.println("GcInfo CompositeType: " +
          // info.getGcInfo().getCompositeType());
          // System.out.println("GcInfo MemoryUsageAfterGc: " +
          // info.getGcInfo().getMemoryUsageAfterGc());
          // System.out.println("GcInfo MemoryUsageBeforeGc: " +
          // info.getGcInfo().getMemoryUsageBeforeGc());

          // Get the information about each memory space, and pretty print it
          Map<String, MemoryUsage> membefore =
                                             info.getGcInfo()
                                                 .getMemoryUsageBeforeGc();
          Map<String, MemoryUsage> mem = info.getGcInfo()
                                             .getMemoryUsageAfterGc();
          for (Entry<String, MemoryUsage> entry : mem.entrySet())
          {
            String name = entry.getKey();
            MemoryUsage memdetail = entry.getValue();
            long memInit = memdetail.getInit();
            long memCommitted = memdetail.getCommitted();
            long memMax = memdetail.getMax();
            long memUsed = memdetail.getUsed();
            MemoryUsage before = membefore.get(name);
            long beforepercent = ((before.getUsed() * 1000L)
                                  / before.getCommitted());
            long percent =
                         ((memUsed * 1000L) / before.getCommitted()); // >100%
                                                                      // when
                                                                      // it
                                                                      // gets
                                                                      // expanded

            info("Memory usage: " + name
                 + (memCommitted == memMax ? "(fully expanded)"
                                           : "(still expandable)")
                 + "used: "
                 + (beforepercent / 10)
                 + "."
                 + (beforepercent % 10)
                 + "%->"
                 + (percent / 10)
                 + "."
                 + (percent % 10)
                 + "%("
                 + ((memUsed / 1048576) + 1)
                 + "MB) / ");
          }
          totalGcDuration += info.getGcInfo().getDuration();
          long percent = totalGcDuration * 1000L
                         / info.getGcInfo().getEndTime();

          info("GC cumulated overhead " + (percent / 10)
               + "."
               + (percent % 10)
               + "%");
        }
      }
    };

    return listener;
  }

  public static void trigger()
  {
    // sGarbageCollector.info("Garbage collection started.");
    System.gc();
    // sGarbageCollector.info("Garbage collection finished.");
  }

}
