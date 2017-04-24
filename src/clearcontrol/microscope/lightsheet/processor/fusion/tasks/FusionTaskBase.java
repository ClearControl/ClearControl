package clearcontrol.microscope.lightsheet.processor.fusion.tasks;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import clearcl.ClearCLContext;
import clearcl.ClearCLKernel;
import clearcl.ClearCLProgram;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngineInterface;

/**
 * Base class providing common fields and methods for all task implementations
 *
 * @author royer
 */
public abstract class FusionTaskBase implements FusionTaskInterface
{

  private final HashSet<String> mRequiredImagesSlotKeysSet =
                                                           new HashSet<>();

  private Class<AverageTask> mClass;
  private String mSourceFile;
  private ClearCLProgram mProgram;
  private String mKernelName;
  private ClearCLKernel mKernel;

  /**
   * Instantiates a fusion task given the keys of required images
   * 
   * @param pSlotKeys
   *          list of slot keys
   */
  public FusionTaskBase(String... pSlotKeys)
  {
    super();
    for (String lSlotKey : pSlotKeys)
      mRequiredImagesSlotKeysSet.add(lSlotKey);
  }

  protected void setupProgramAndKernel(Class<AverageTask> pClass,
                                       String pSourceFile,
                                       String pKernelName)
  {
    mClass = pClass;
    mSourceFile = pSourceFile;
    mKernelName = pKernelName;
  }

  protected ClearCLKernel getKernel(ClearCLContext pContext) throws IOException
  {
    if (mKernel != null)
      return mKernel;
    mProgram = pContext.createProgram(mClass, mSourceFile);
    mProgram.addBuildOptionAllMathOpt();
    mProgram.buildAndLog();
    mKernel = mProgram.createKernel(mKernelName);
    return mKernel;
  }

  @Override
  public boolean checkIfRequiredImagesAvailable(Set<String> pAvailableImagesSlotKeys)
  {
    boolean lAllRequiredImagesAvailable =
                                        pAvailableImagesSlotKeys.containsAll(mRequiredImagesSlotKeysSet);

    return lAllRequiredImagesAvailable;
  }

  @Override
  public abstract boolean enqueue(FastFusionEngineInterface pStackFuser,
                                  boolean pWaitToFinish);

  @Override
  public String toString()
  {
    return String.format("FusionTaskBase [mKernelName=%s, mRequiredImagesSlotKeysSet=%s]",
                         mKernelName,
                         mRequiredImagesSlotKeysSet);
  }

}
