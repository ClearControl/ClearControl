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
  private final String[] mInputImagesKeys;
  private final String mDestImageKey;
  private ClearCLImageViewer mViewA, mViewB, mViewFused;
  private volatile boolean mDebugDisplay = false;

  /**
   * Instanciates an average fusion task given the keys for two input images and
   * destination image
   * 
   * @param pImageAKey
   *          image A key
   * @param pImageBKey
   *          image B key
   * @param pDestImageKey
   *          destination image key
   */
  public AverageTask(String pImageAKey,
                     String pImageBKey,
                     String pDestImageKey)
  {
    super(pImageAKey, pImageBKey);
    setupProgramAndKernel(AverageTask.class,
                          "./kernels/fuseavg.cl",
                          "fuseavg2");
    mInputImagesKeys = new String[]
    { pImageAKey, pImageBKey };
    mDestImageKey = pDestImageKey;
  }

  /**
   * Instanciates an avreage fusion task given the keys for the two input images
   * and destination image
   * 
   * @param pImageAKey
   *          image A key
   * @param pImageBKey
   *          image B key
   * @param pImageCKey
   *          image C key
   * @param pImageDKey
   *          image D key
   * @param pDestImageKey
   *          destination image key
   */
  public AverageTask(String pImageAKey,
                     String pImageBKey,
                     String pImageCKey,
                     String pImageDKey,
                     String pDestImageKey)
  {
    super(pImageAKey, pImageBKey, pImageCKey, pImageDKey);
    setupProgramAndKernel(AverageTask.class,
                          "./kernels/fuseavg.cl",
                          "fuseavg4");
    mInputImagesKeys = new String[]
    { pImageAKey, pImageBKey, pImageCKey, pImageDKey };
    mDestImageKey = pDestImageKey;
  }

  @Override
  public boolean enqueue(FastFusionEngineInterface pStackFuser,
                         boolean pWaitToFinish)
  {
    try
    {
      ClearCLImage lImageA, lImageB, lImageC = null, lImageD = null;

      lImageA = pStackFuser.getImage(mInputImagesKeys[0]);
      lImageB = pStackFuser.getImage(mInputImagesKeys[1]);

      if (lImageA == null || lImageB == null)
        throw new FastFusionException("Fusion task %s received a null image",
                                      this);

      if (!Arrays.equals(lImageA.getDimensions(),
                         lImageB.getDimensions()))
        throw new FastFusionException("Fusion task %s received two images of incompatible dimensions: %s and %s",
                                      this,
                                      Arrays.toString(lImageA.getDimensions()),
                                      Arrays.toString(lImageB.getDimensions()));

      if (mInputImagesKeys.length == 4)
      {
        lImageC = pStackFuser.getImage(mInputImagesKeys[2]);
        lImageD = pStackFuser.getImage(mInputImagesKeys[3]);

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
                                               pStackFuser.ensureImageAllocated(mDestImageKey,
                                                                                lImageA.getDimensions());

      ClearCLImage lImageFused = lPair.getRight();

      ClearCLKernel lKernel = getKernel(lImageFused.getContext());

      lKernel.setArgument("imagea", lImageA);
      lKernel.setArgument("imageb", lImageB);
      if (mInputImagesKeys.length == 4)
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
                                                    + mInputImagesKeys[0],
                                           512,
                                           512);
        }
        if (mViewB == null)
          mViewB = ClearCLImageViewer.view(lImageB,
                                           lWindowTitlePrefix
                                                    + mInputImagesKeys[1],
                                           512,
                                           512);
        if (mViewFused == null)
          mViewFused =
                     ClearCLImageViewer.view(lImageFused,
                                             lWindowTitlePrefix + ":"
                                                          + mDestImageKey,
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
