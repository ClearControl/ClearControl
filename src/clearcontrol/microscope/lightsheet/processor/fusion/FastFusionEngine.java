package clearcontrol.microscope.lightsheet.processor.fusion;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.enums.HostAccessType;
import clearcl.enums.ImageChannelDataType;
import clearcl.enums.KernelAccessType;
import clearcontrol.microscope.lightsheet.processor.fusion.tasks.FusionTaskInterface;
import coremem.ContiguousMemoryInterface;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * fast fusion engine.
 *
 * @author royer
 */
public class FastFusionEngine implements FastFusionEngineInterface
{
  private final ClearCLContext mContext;

  private final ConcurrentHashMap<String, MutablePair<Boolean, ClearCLImage>> mImageSlotsMap =
                                                                                             new ConcurrentHashMap<>();

  private final ArrayList<FusionTaskInterface> mFusionTasks =
                                                            new ArrayList<>();

  private final HashSet<FusionTaskInterface> mExecutedFusionTasks =
                                                                  new HashSet<>();

  /**
   * Instanciates a StackFusion object given a CLearCL context
   * 
   * @param pContext
   *          ClearCL context
   */
  public FastFusionEngine(ClearCLContext pContext)
  {
    super();
    mContext = pContext;
  }

  /**
   * Instanciates a fast fusion engine given a
   * 
   * @param pFastFusionEngine
   *          fast fusion engine
   */
  public FastFusionEngine(FastFusionEngine pFastFusionEngine)
  {
    this(pFastFusionEngine.getContext());

    mFusionTasks.addAll(pFastFusionEngine.getTasks());
  }

  @Override
  public void reset(boolean pCloseImages)
  {
    mContext.getDefaultQueue().waitToFinish();

    for (Entry<String, MutablePair<Boolean, ClearCLImage>> lEntry : mImageSlotsMap.entrySet())
    {

      lEntry.getValue().left = false;
      if (pCloseImages)
        lEntry.getValue().getRight().close();
    }
    mExecutedFusionTasks.clear();
  }

  @Override
  public void addTask(FusionTaskInterface pTask)
  {
    mFusionTasks.add(pTask);
  }

  @Override
  public ArrayList<FusionTaskInterface> getTasks()
  {
    return mFusionTasks;
  }

  @Override
  public void passImage(String pImageKey,
                        ContiguousMemoryInterface pImageData,
                        long... pDimensions)
  {
    MutablePair<Boolean, ClearCLImage> lPair =
                                             ensureImageAllocated(pImageKey,
                                                                  pDimensions);

    lPair.getRight().readFrom(pImageData, true);
    lPair.setLeft(true);
  }

  @Override
  public MutablePair<Boolean, ClearCLImage> ensureImageAllocated(final String pImageKey,
                                                                 final long... pDimensions)
  {

    MutablePair<Boolean, ClearCLImage> lPair =
                                             getImageSlotsMap().get(pImageKey);

    if (lPair == null)
    {
      lPair = MutablePair.of(true, (ClearCLImage) null);

      getImageSlotsMap().put(pImageKey, lPair);
    }

    ClearCLImage lImage = lPair.getRight();

    if (lImage == null
        || !Arrays.equals(lImage.getDimensions(), pDimensions))
    {
      if (lImage != null)
        lImage.close();

      lImage =
             mContext.createSingleChannelImage(HostAccessType.ReadWrite,
                                               KernelAccessType.ReadWrite,
                                               ImageChannelDataType.UnsignedInt16,
                                               pDimensions);

      lPair.setLeft(false);
      lPair.setRight(lImage);
    }

    return lPair;
  }

  @Override
  public ClearCLImage getImage(String pImageKey)
  {
    return getImageSlotsMap().get(pImageKey).getRight();
  }

  @Override
  public boolean isImageAvailable(String pImageKey)
  {
    MutablePair<Boolean, ClearCLImage> lMutablePair =
                                                    getImageSlotsMap().get(pImageKey);
    if (lMutablePair == null)
      return false;
    return lMutablePair.getLeft();
  }

  @Override
  public Set<String> getAvailableImagesKeys()
  {
    HashSet<String> lAvailableImagesKeys = new HashSet<String>();
    for (Entry<String, MutablePair<Boolean, ClearCLImage>> lEntry : mImageSlotsMap.entrySet())
    {
      if (lEntry.getValue().getKey())
      {
        lAvailableImagesKeys.add(lEntry.getKey());
      }
    }
    return lAvailableImagesKeys;
  }

  @Override
  public int executeOneTask()
  {
    return executeSeveralTasks(1);
  }

  @Override
  public int executeSeveralTasks(int pMaxNumberOfTasks)
  {
    ArrayList<FusionTaskInterface> lReadyTasks = new ArrayList<>();

    Set<String> lAvailableImageKeys = getAvailableImagesKeys();

    for (FusionTaskInterface lFusionTask : mFusionTasks)
      if (!mExecutedFusionTasks.contains(lFusionTask))
      {
        boolean lImagesAvailable =
                                 lFusionTask.checkIfRequiredImagesAvailable(lAvailableImageKeys);

        if (lImagesAvailable)
          lReadyTasks.add(lFusionTask);

      }

    int lNumberOfTasksReady = lReadyTasks.size();

    if (lNumberOfTasksReady == 0)
      return 0;

    lNumberOfTasksReady = min(lNumberOfTasksReady, pMaxNumberOfTasks);

    for (int i = 0; i < lNumberOfTasksReady; i++)
    {
      FusionTaskInterface lTask = lReadyTasks.get(i);
      lTask.enqueue(this, true);
      mExecutedFusionTasks.add(lTask);
    }

    return lNumberOfTasksReady;
  }

  /**
   * Waits for the currently
   */
  public void waitFusionTasksToComplete()
  {
    getContext().getDefaultQueue().waitToFinish();
  }

  /**
   * Returns ClearCL context
   * 
   * @return context
   */
  public ClearCLContext getContext()
  {
    return mContext;
  }

  private Map<String, MutablePair<Boolean, ClearCLImage>> getImageSlotsMap()
  {
    return mImageSlotsMap;
  }

}
