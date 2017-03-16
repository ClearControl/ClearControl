package clearcontrol.simulation;

import static clearcontrol.simulation.loaders.SampleSpaceSaveAndLoad.loadUnsignedShortSampleSpaceFromDisk;
import static net.imglib2.img.utils.Copy.copy;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackProvider;
import coremem.memmap.FileMappedMemoryRegion;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;

/**
 * Created by dibrov on 10/03/17.
 */
public class BasicSampleSimulator implements SampleSimulatorInterface
{

  private long[] mCurrentPosition =
  { 0, 0, 0 };
  private long[] mCurrentDimensions =
  { 10, 10, 10 };
  private long mCurrentTimeStep = 0;

  private long mNumberOfTimeSteps;
  private long[] mDimensions =
  { 0, 0, 0 };

  private LinkedList<OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>> mTimeLapse;

  public BasicSampleSimulator(LinkedList<OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>> pTimeLapse)
  {
    if (pTimeLapse == null & pTimeLapse.isEmpty())
    {
      throw new IllegalArgumentException("Can't create a Simulator with an empty timelapse!");
    }
    mTimeLapse = pTimeLapse;
    mNumberOfTimeSteps = pTimeLapse.size();
    mDimensions[0] = mTimeLapse.get(0).dimension(0);
    mDimensions[1] = mTimeLapse.get(0).dimension(1);
    mDimensions[2] = mTimeLapse.get(0).dimension(2);
  }

  public BasicSampleSimulator(String pPathToFile, long[] pDimensions)
  {
    mDimensions = Arrays.copyOf(pDimensions, pDimensions.length);
    LinkedList<OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>> lImgs =
                                                                              new LinkedList<>();
    short[] arr =
                loadUnsignedShortSampleSpaceFromDisk(pPathToFile,
                                                     (int) pDimensions[0],
                                                     (int) pDimensions[1],
                                                     (int) pDimensions[2]);
    OffHeapMemory cm = new OffHeapMemory("mem",
                                         this,
                                         OffHeapMemoryAccess.allocateMemory((2
                                                                             * pDimensions[0]
                                                                             * pDimensions[1]
                                                                             * pDimensions[2])),
                                         2 * pDimensions[0]
                                                                                                 * pDimensions[1]
                                                                                                 * pDimensions[2]);
    cm.copyFrom(arr);

    OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> imgOH =
                                                                  (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new OffHeapPlanarImgFactory().createShortInstance(cm,
                                                                                                                                                                              pDimensions,
                                                                                                                                                                              new UnsignedShortType());

    lImgs.add(imgOH);
    this.mTimeLapse = lImgs;
  }

  public void setCurrentPosition(long[] pCurrentPosition)
  {
    this.mCurrentPosition = pCurrentPosition;
  }

  @Override
  public long[] getDimensions()
  {
    return Arrays.copyOf(mDimensions, mDimensions.length);
  }

  public void setCurrentDimensions(long[] pCurrentDimensions)
  {
    this.mCurrentDimensions =
                            Arrays.copyOf(pCurrentDimensions,
                                          pCurrentDimensions.length);
  }

  private StackProvider mStackProvider = new StackProvider()
  {
    @Override
    public StackInterface getStack()
    {
      return getCurrentStack();
    }
  };

  private StackInterface getCurrentStack()
  {
    return getSubstack(mCurrentPosition,
                       mCurrentDimensions,
                       mCurrentTimeStep);
  }

  private StackInterface getSubstack(long[] pPosition,
                                     long[] pDimensions,
                                     long pTimeStep)
  {
    if (pTimeStep > mNumberOfTimeSteps)
    {
      throw new ArrayIndexOutOfBoundsException("The simulated timelapse is "
                                               + mNumberOfTimeSteps
                                               + " "
                                               + "stacks long. You requested a stack on time step "
                                               + pTimeStep);
    }
    if (pPosition.length != 3)
    {
      throw new IllegalArgumentException("Position vector is supposed to be 3D. You provided: "
                                         + pPosition.length);
    }
    if (pPosition[0] < 0 || pPosition[0] >= mDimensions[0])
    {
      throw new IllegalArgumentException("Wrong X coordinate for substack. Should be within the range of: ["
                                         + 0
                                         + ", "
                                         + mDimensions[0]
                                         + "].");
    }
    if (pPosition[1] < 0 || pPosition[1] >= mDimensions[1])
    {
      throw new IllegalArgumentException("Wrong Y coordinate for substack. Should be within the range of: ["
                                         + 0
                                         + ", "
                                         + mDimensions[1]
                                         + "].");
    }
    if (pPosition[2] < 0 || pPosition[2] >= mDimensions[2])
    {
      throw new IllegalArgumentException("Wrong Z coordinate for substack. Should be within the range of: ["
                                         + 0
                                         + ", "
                                         + mDimensions[2]
                                         + "].");
    }
    if (pDimensions.length != 3)
    {
      throw new IllegalArgumentException("Dimensions vector is supposed to be 3D. You provided: "
                                         + pDimensions.length);
    }

    OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> imgOut =
                                                                   copy(mTimeLapse.get(0),
                                                                        pPosition,
                                                                        pDimensions);

    OffHeapPlanarStack ops = new OffHeapPlanarStack(0, 0, imgOut);
    return ops;

  }

  public static BasicSampleSimulator getBasicSampleSimulatorWithASingleStackFromARawFile(String pPathToFile,
                                                                                         long[] pDimensions)
  {
    try
    {

      // // assuming short
      LinkedList<OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>> lImgs =
                                                                                new LinkedList<>();
      BasicSampleSimulator bss = new BasicSampleSimulator(lImgs);
      short[] arr =
                  loadUnsignedShortSampleSpaceFromDisk(pPathToFile,
                                                       (int) pDimensions[0],
                                                       (int) pDimensions[1],
                                                       (int) pDimensions[2]);
      OffHeapMemory cm =
                       new OffHeapMemory("mem",
                                         bss,
                                         OffHeapMemoryAccess.allocateMemory((2
                                                                             * pDimensions[0]
                                                                             * pDimensions[1]
                                                                             * pDimensions[2])),
                                         pDimensions[0] * pDimensions[1]
                                                                                                 * pDimensions[2]);

      OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> imgOH =
                                                                    (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new OffHeapPlanarImgFactory().createShortInstance(cm,
                                                                                                                                                                                pDimensions,
                                                                                                                                                                                new UnsignedShortType());

      lImgs.add(imgOH);
      return new BasicSampleSimulator(lImgs);

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public StackProvider getStackProvider(long pIndex)
  {
    return mStackProvider;
  }

  public static void main(String[] args)
  {
    String lPath = "./config_files/testWD.raw";
    long[] dims =
    { 1313, 992, 65 };
    long l = 2 * dims[0] * dims[1] * dims[2];
    OffHeapMemory oh = OffHeapMemory.allocateBytes(l);

    try
    {
      File f = new File(lPath);
      FileMappedMemoryRegion fmmr = new FileMappedMemoryRegion(f,
                                                               l,
                                                               StandardOpenOption.READ);
      fmmr.map();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

  }
}
