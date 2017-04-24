package clearcontrol.microscope.lightsheet.processor.fusion.tasks;

import java.io.IOException;
import java.util.Arrays;

import clearcl.ClearCLImage;
import clearcl.ClearCLKernel;
import clearcl.viewer.ClearCLImageViewer;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngineInterface;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionException;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Fuses two stacks using the average method.
 *
 * @author royer
 */
public class AverageTask extends FusionTaskBase
                         implements FusionTaskInterface
{
  private final String[] mInputImagesSlotKeys;
  private final String mDestImageSlotKey;
  private ClearCLImageViewer mViewA, mViewB, mViewFused;
  private volatile boolean mDebugDisplay = false;

  /**
   * Instantiates an average fusion task given the keys for two input images and
   * destination image
   * 
   * @param pImageASlotKey
   *          image A slot key
   * @param pImageBSlotKey
   *          image B slot key
   * @param pDestImageKey
   *          destination image key
   */
  public AverageTask(String pImageASlotKey,
                     String pImageBSlotKey,
                     String pDestImageKey)
  {
    super(pImageASlotKey, pImageBSlotKey);
    setupProgramAndKernel(AverageTask.class,
                          "./kernels/fuseavg.cl",
                          "fuseavg2");
    mInputImagesSlotKeys = new String[]
    { pImageASlotKey, pImageBSlotKey };
    mDestImageSlotKey = pDestImageKey;
  }

  /**
   * Instantiates an average fusion task given the keys for the two input images
   * and destination image.
   * 
   * @param pImageASlotKey
   *          image A key
   * @param pImageBSlotKey
   *          image B key
   * @param pImageCSlotKey
   *          image C key
   * @param pImageDSlotKey
   *          image D key
   * @param pDestImageSlotKey
   *          destination image key
   */
  public AverageTask(String pImageASlotKey,
                     String pImageBSlotKey,
                     String pImageCSlotKey,
                     String pImageDSlotKey,
                     String pDestImageSlotKey)
  {
    super(pImageASlotKey,
          pImageBSlotKey,
          pImageCSlotKey,
          pImageDSlotKey);
    setupProgramAndKernel(AverageTask.class,
                          "./kernels/fuseavg.cl",
                          "fuseavg4");
    mInputImagesSlotKeys = new String[]
    { pImageASlotKey,
      pImageBSlotKey,
      pImageCSlotKey,
      pImageDSlotKey };
    mDestImageSlotKey = pDestImageSlotKey;
  }

  @Override
  public boolean enqueue(FastFusionEngineInterface pStackFusionEngine,
                         boolean pWaitToFinish)
  {
    try
    {
      ClearCLImage lImageA, lImageB, lImageC = null, lImageD = null;

      lImageA = pStackFusionEngine.getImage(mInputImagesSlotKeys[0]);
      lImageB = pStackFusionEngine.getImage(mInputImagesSlotKeys[1]);

      if (lImageA == null || lImageB == null)
        throw new FastFusionException("Fusion task %s received a null image",
                                      this);

      if (!Arrays.equals(lImageA.getDimensions(),
                         lImageB.getDimensions()))
        throw new FastFusionException("Fusion task %s received two images of incompatible dimensions: %s and %s",
                                      this,
                                      Arrays.toString(lImageA.getDimensions()),
                                      Arrays.toString(lImageB.getDimensions()));

      if (mInputImagesSlotKeys.length == 4)
      {
        lImageC =
                pStackFusionEngine.getImage(mInputImagesSlotKeys[2]);
        lImageD =
                pStackFusionEngine.getImage(mInputImagesSlotKeys[3]);

        if (lImageC == null || lImageD == null)
          throw new FastFusionException("Fusion task %s received a null image",
                                        this);

        if (!Arrays.equals(lImageC.getDimensions(),
                           lImageD.getDimensions()))
          throw new FastFusionException("Fusion task %s received two images of incompatible dimensions: %s and %s",
                                        this,
                                        Arrays.toString(lImageC.getDimensions()),
                                        Arrays.toString(lImageD.getDimensions()));

        if (!Arrays.equals(lImageA.getDimensions(),
                           lImageC.getDimensions()))
          throw new FastFusionException("Fusion task %s received two images of incompatible dimensions: %s and %s",
                                        this,
                                        Arrays.toString(lImageA.getDimensions()),
                                        Arrays.toString(lImageC.getDimensions()));
      }

      MutablePair<Boolean, ClearCLImage> lPair =
                                               pStackFusionEngine.ensureImageAllocated(mDestImageSlotKey,
                                                                                       lImageA.getDimensions());

      ClearCLImage lImageFused = lPair.getRight();

      ClearCLKernel lKernel = getKernel(lImageFused.getContext());

      lKernel.setArgument("imagea", lImageA);
      lKernel.setArgument("imageb", lImageB);
      if (mInputImagesSlotKeys.length == 4)
      {
        lKernel.setArgument("imagec", lImageC);
        lKernel.setArgument("imaged", lImageD);
      }
      lKernel.setArgument("imagedest", lImageFused);

      lKernel.setGlobalSizes(lImageFused);

      // System.out.println("running kernel");
      lKernel.run(pWaitToFinish);
      lPair.setLeft(true);

      if (mDebugDisplay)
      {
        String lWindowTitlePrefix = this.getClass().getSimpleName()
                                    + ":";
        if (mViewA == null)
        {

          mViewA = ClearCLImageViewer.view(lImageA,
                                           lWindowTitlePrefix
                                                    + mInputImagesSlotKeys[0],
                                           512,
                                           512);
        }
        if (mViewB == null)
          mViewB = ClearCLImageViewer.view(lImageB,
                                           lWindowTitlePrefix
                                                    + mInputImagesSlotKeys[1],
                                           512,
                                           512);
        if (mViewFused == null)
          mViewFused =
                     ClearCLImageViewer.view(lImageFused,
                                             lWindowTitlePrefix + ":"
                                                          + mDestImageSlotKey,
                                             512,
                                             512);

        mViewA.setImage(lImageA);
        mViewB.setImage(lImageB);
        mViewFused.setImage(lImageFused);

        lImageA.notifyListenersOfChange(lImageA.getContext()
                                               .getDefaultQueue());
        lImageB.notifyListenersOfChange(lImageB.getContext()
                                               .getDefaultQueue());
        lImageFused.notifyListenersOfChange(lImageFused.getContext()
                                                       .getDefaultQueue());
      }

      return true;
    }
    catch (IOException e)
    {
      throw new FastFusionException(e,
                                    "Error while reading kernel source code");
    }

  }

}
