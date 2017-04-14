package clearcontrol.microscope.lightsheet.processor.fusion.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.ClearCLBackends;
import clearcl.viewer.ClearCLImageViewer;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngine;
import clearcontrol.microscope.lightsheet.processor.fusion.tasks.AverageTask;
import coremem.offheap.OffHeapMemory;

import org.junit.Test;

public class FastFusionTests
{

  @Test
  public void test() throws Exception
  {

    int width = 100;
    int height = 101;
    int depth = 102;

    ClearCLBackendInterface lBestBackend =
                                         ClearCLBackends.getBestBackend();

    try (ClearCL lClearCL = new ClearCL(lBestBackend);
        ClearCLDevice lFastestGPUDevice =
                                        lClearCL.getFastestGPUDeviceForImages();
        ClearCLContext lContext = lFastestGPUDevice.createContext())
    {

      FastFusionEngine lStackFusion = new FastFusionEngine(lContext);

      lStackFusion.addTask(new AverageTask("a", "b", "c"));

      OffHeapMemory lStackDataA =
                                OffHeapMemory.allocateShorts(width
                                                             * height
                                                             * depth);
      OffHeapMemory lStackDataB =
                                OffHeapMemory.allocateShorts(width
                                                             * height
                                                             * depth);

      for (int z = 0; z < depth; z++)
        for (int y = 0; y < height; y++)
          for (int x = 0; x < width; x++)
          {
            int i = x + width * y + width * height * z;

            lStackDataA.setShortAligned(i, (short) (x ^ y));

            lStackDataB.setShortAligned(i, (short) (y ^ z));

          }

      assertFalse(lStackFusion.isImageAvailable("a"));
      assertFalse(lStackFusion.isImageAvailable("b"));
      assertFalse(lStackFusion.isImageAvailable("c"));

      lStackFusion.passImage("a", lStackDataA, width, height, depth);

      assertTrue(lStackFusion.isImageAvailable("a"));
      assertFalse(lStackFusion.isImageAvailable("b"));
      assertFalse(lStackFusion.isImageAvailable("c"));

      assertTrue(lStackFusion.executeAllTasks() == 0);

      lStackFusion.passImage("b", lStackDataB, width, height, depth);

      assertTrue(lStackFusion.isImageAvailable("a"));
      assertTrue(lStackFusion.isImageAvailable("b"));

      assertTrue(lStackFusion.executeOneTask() > 0);

      assertTrue(lStackFusion.isImageAvailable("c"));

      /*
      ClearCLImageViewer lViewA =
                                ClearCLImageViewer.view(lStackFusion.getImage("a"));
      ClearCLImageViewer lViewB =
                                ClearCLImageViewer.view(lStackFusion.getImage("b"));/**/

      ClearCLImageViewer lView =
                               ClearCLImageViewer.view(lStackFusion.getImage("c"));

      assertTrue(lStackFusion.executeAllTasks() == 0);

      lStackFusion.reset(false);
      assertFalse(lStackFusion.isImageAvailable("c"));

      while (lView.isShowing())
        ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);

    }

  }

}
