package clearcontrol.stack.sourcesink.source.viewer.demo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.source.RawFileStackSource;
import clearcontrol.stack.sourcesink.source.viewer.StackSourceViewer;
import clearcontrol.stack.sourcesink.synthetic.FractalStackSource;
import coremem.recycling.BasicRecycler;

import org.junit.Test;

/**
 * Stack Source viewer tests
 *
 * @author royer
 */
public class StackSourceViewerDemo
{

  /**
   * Demo with saved data
   * 
   * @throws IOException
   *           NA
   */
  @Test
  public void demoWithSavedData() throws IOException
  {
    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final BasicRecycler<StackInterface, StackRequest> lStackRecycler =
                                                                     new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                     10);

    RawFileStackSource lLocalFileStackSource =
                                             new RawFileStackSource(lStackRecycler);

    lLocalFileStackSource.setLocation(new File("/Volumes/myersspimdata/XScope"),
                                      "2017-08-09-17-18-17-27-ZFishRun4On09.08.17");

    lLocalFileStackSource.update();

    StackSourceViewer lStackViewer = new StackSourceViewer();

    lStackViewer.getStackSourceVariable().set(lLocalFileStackSource);
    lStackViewer.getStackChannelVariable().set("default");

    for (int i = 300; i < 1000; i += 5)
    {
      System.out.println("TimePoint: " + i);
      lStackViewer.getStackIndexVariable().set((long) i);
      // ThreadSleep.sleep(1, TimeUnit.SECONDS);
    }

    lStackViewer.close();

  }

  /**
   * Demo with fractal data
   * 
   * @throws IOException
   *           NA
   */
  @Test
  public void demoWithFractalSource() throws IOException
  {

    FractalStackSource lFractalStackSource = new FractalStackSource();

    StackSourceViewer lStackViewer = new StackSourceViewer();

    lStackViewer.getStackSourceVariable().set(lFractalStackSource);
    lStackViewer.getStackChannelVariable().set("default");

    for (int i = 300; i < 1000; i += 5)
    {
      System.out.println("TimePoint: " + i);
      lStackViewer.getStackIndexVariable().set((long) i);

      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
    }

    lStackViewer.close();

  }

}
