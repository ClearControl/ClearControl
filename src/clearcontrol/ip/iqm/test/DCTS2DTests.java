package clearcontrol.ip.iqm.test;

/**
 * DCTS 2D tests
 *
 * @author royer
 */
public class DCTS2DTests
{

  /**
   * Test
   * 
   * @throws IOException
   *           NA
   * @throws FormatException
   *           NA
   * @throws InterruptedException
   *           NA
   */
  /*@Test
  public void test() throws IOException,
                     FormatException,
                     InterruptedException
  {
    final File lTempFile =
                         File.createTempFile(DCTS2DTests.class.getSimpleName(),
                                             "test.tif");
  
    java.nio.file.Files.copy(DCTS2DTests.class.getResourceAsStream("./stacks/example.tif"),
                             lTempFile.toPath(),
                             StandardCopyOption.REPLACE_EXISTING);
  
  
    SCIFIO lSCIFIO = null;
    try
    {
      lSCIFIO = new SCIFIO();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    final Reader lReader =
                         lSCIFIO.initializer()
                                .initializeReader(lTempFile.getAbsolutePath());
  
    final int lWidth = (int) lReader.openPlane(0, 0).getLengths()[0];
    final int lHeight = (int) lReader.openPlane(0, 0).getLengths()[1];
    final int lDepth = (int) lReader.getPlaneCount(0);
  
    final DCTS2D lDCTS2D = new DCTS2D();
  
    final int repeats = 30;
  
    OffHeapPlanarStack lStack =
                              OffHeapPlanarStack.createStack(lWidth,
                                                             lHeight,
                                                             lDepth);
  
    for (int z = 0; z < lDepth; z++)
    {
      final ContiguousMemoryInterface lPlaneContiguousMemory =
                                                             lStack.getContiguousMemory(z);
  
      final Plane lPlane = lReader.openPlane(0, z);
      final byte[] lBytes = lPlane.getBytes();
  
      lPlaneContiguousMemory.copyFrom(lBytes);
    }
  
    // new ImageJ();
    // final ImagePlus lShow = ImageJFunctions.show(lImage);
  
    double[] lComputeDCTS = new double[lDepth];
  
    final long lStartTimeInNs = System.nanoTime();
    for (int r = 0; r < repeats; r++)
      lComputeDCTS = lDCTS2D.computeImageQualityMetric(lStack);
    final long lStopTimeInNs = System.nanoTime();
  
    final double lElapsedTimeInMs =
                                  OrderOfMagnitude.nano2milli((lStopTimeInNs
                                                               - lStartTimeInNs)
                                                              / repeats);
    System.out.println("time per slicewise-dcts computation on a stack: "
                       + lElapsedTimeInMs + " ms");
  
    System.out.println(Arrays.toString(lComputeDCTS));
  
    for (final double lValue : lComputeDCTS)
    {
      assertFalse(Double.isNaN(lValue));
      assertFalse(Double.isInfinite(lValue));
      assertFalse(lValue == 0);
  
    }
  
  }*/

}
