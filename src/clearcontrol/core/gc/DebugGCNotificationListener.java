package clearcontrol.core.gc;

import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

/**
 * Debug GC notification listener
 *
 * @author royer
 */
public class DebugGCNotificationListener implements
                                         NotificationListener
{

  private GarbageCollector mGarbageCollector;

  // keep a count of the total time spent in GCs
  long totalGcDuration = 0;

  /**
   * Insatnciates a debug GC notification listener
   * 
   * @param pGarbageCollector
   *          garbage collector parent
   */
  public DebugGCNotificationListener(GarbageCollector pGarbageCollector)
  {
    mGarbageCollector = pGarbageCollector;
    // TODO Auto-generated constructor stub
  }

  // implement the notifier callback handler
  @Override
  public void handleNotification(Notification notification,
                                 Object handback)
  {
    if (!GarbageCollector.sGCDebugOutputFlag)
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

      mGarbageCollector.info("Garbage Collection event: " + gctype
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
        // long memInit = memdetail.getInit();
        long memCommitted = memdetail.getCommitted();
        long memMax = memdetail.getMax();
        long memUsed = memdetail.getUsed();
        MemoryUsage before = membefore.get(name);
        long beforepercent = ((before.getUsed() * 1000L)
                              / before.getCommitted());
        long percent = ((memUsed * 1000L) / before.getCommitted()); // >100%
                                                                    // when
                                                                    // it
                                                                    // gets
                                                                    // expanded

        mGarbageCollector.info("Memory usage: " + name
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

      mGarbageCollector.info("GC cumulated overhead " + (percent / 10)
                             + "."
                             + (percent % 10)
                             + "%");
    }
  }
}
