package clearcontrol.core.file.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.file.FileEventNotifier;
import clearcontrol.core.file.FileEventNotifier.FileEventKind;
import clearcontrol.core.file.FileEventNotifierListener;

import org.junit.Test;

/**
 * File event notifier tests
 *
 * @author royer
 */
public class FileEventNotifierTests
{

  protected volatile int lEventCounter;

  /**
   * test
   * 
   * @throws Exception
   *           N/A
   */
  @Test
  public void test() throws Exception
  {
    final File lTestFile =
                         File.createTempFile("FileEventNotifierTests",
                                             "test");
    final File lOtherFile =
                          File.createTempFile("FileEventNotifierTests",
                                              "other");

    lTestFile.delete();
    lOtherFile.delete();

    final FileEventNotifier lFileEventNotifier =
                                               new FileEventNotifier(lTestFile);

    lFileEventNotifier.addFileEventListener(new FileEventNotifierListener()
    {

      @Override
      public void fileEvent(final FileEventNotifier pThis,
                            final File pFile,
                            final FileEventKind pEventKind)
      {
        lEventCounter++;
        System.out.format("Received Event: %s %s %s \n",
                          pThis.toString(),
                          pFile.toString(),
                          pEventKind.toString());
      }
    });

    lEventCounter = 0;
    lFileEventNotifier.startMonitoring();

    assertEquals(0, lEventCounter);

    final Formatter lTestFileFormatter = new Formatter(lTestFile);

    lTestFileFormatter.format("test1\n");
    lTestFileFormatter.flush();
    ThreadSleep.sleep(3, TimeUnit.SECONDS);
    assertEquals(1, lEventCounter);

    lTestFileFormatter.format("test2\n");
    lTestFileFormatter.flush();
    lTestFileFormatter.close();
    ThreadSleep.sleep(2, TimeUnit.SECONDS);
    assertEquals(2, lEventCounter);

    final Formatter lOtherFileFormatter = new Formatter(lOtherFile);
    lOtherFileFormatter.format("test3\n");
    lOtherFileFormatter.flush();
    lOtherFileFormatter.close();
    ThreadSleep.sleep(2, TimeUnit.SECONDS);
    assertEquals(2, lEventCounter);

    lFileEventNotifier.stopMonitoring();

    lFileEventNotifier.close();
  }
}
