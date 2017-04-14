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

  private final HashSet<String> mRequiredImagesKeysSet =
                                                       new HashSet<>();

  private Class<AverageTask> mClass;
  private String mSourceFile;
  private ClearCLProgram mProgram;
  private String mKernelName;
  private ClearCLKernel mKernel;

  /**
   * Instanciates a fusion task given the keys of required images
   * 
   * @param pKeys
   *          list of keys
   */
  public FusionTaskBase(String... pKeys)
  {
    super();
    for (String lKey : pKeys)
      mRequiredImagesKeysSet.add(lKey);
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
  public boolean checkIfRequiredImagesAvailable(Set<String> pAvailableImagesKeys)
  {
    boolean lAllRequiredImagesAvailable =
                                        pAvailableImagesKeys.containsAll(mRequiredImagesKeysSet);

    return lAllRequiredImagesAvailable;
  }

  @Override
  public abstract boolean enqueue(FastFusionEngineInterface pStackFuser,
                                  boolean pWaitToFinish);

  @Override
  public String toString()
  {
    return String.format("FusionTaskBase [mKernelName=%s, mRequiredImagesKeysSet=%s]",
                         mKernelName,
                         mRequiredImagesKeysSet);
  }

}
