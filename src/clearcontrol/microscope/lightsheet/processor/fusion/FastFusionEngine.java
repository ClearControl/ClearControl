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
   * Instantiates a StackFusion object given a CLearCL context
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
   * Instantiates a fast fusion engine given an existing engine - all tasks are
   * copied over
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
  public void passImage(String pSlotKey,
                        ContiguousMemoryInterface pImageData,
                        long... pDimensions)
  {
    MutablePair<Boolean, ClearCLImage> lPair =
                                             ensureImageAllocated(pSlotKey,
                                                                  pDimensions);

    lPair.getRight().readFrom(pImageData, true);
    lPair.setLeft(true);
  }

  @Override
  public MutablePair<Boolean, ClearCLImage> ensureImageAllocated(final String pSlotKey,
                                                                 final long... pDimensions)
  {

    MutablePair<Boolean, ClearCLImage> lPair =
                                             getImageSlotsMap().get(pSlotKey);

    if (lPair == null)
    {
      lPair = MutablePair.of(true, (ClearCLImage) null);

      getImageSlotsMap().put(pSlotKey, lPair);
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
  public void assignImageToAnotherSlotKey(final String pSrcSlotKey,
                                          final String pDstSlotKey)
  {
    MutablePair<Boolean, ClearCLImage> lDstPair =
                                                getImageSlotsMap().get(pDstSlotKey);

    if (lDstPair == null)
    {
      lDstPair = MutablePair.of(true, (ClearCLImage) null);
      getImageSlotsMap().put(pDstSlotKey, lDstPair);
    }

    MutablePair<Boolean, ClearCLImage> lSrcPair =
                                                getImageSlotsMap().get(pSrcSlotKey);

    lDstPair.setRight(lSrcPair.getRight());
    lDstPair.setLeft(true);

  }

  @Override
  public ClearCLImage getImage(String pSlotKey)
  {
    return getImageSlotsMap().get(pSlotKey).getRight();
  }

  @Override
  public boolean isImageAvailable(String pSlotKey)
  {
    MutablePair<Boolean, ClearCLImage> lMutablePair =
                                                    getImageSlotsMap().get(pSlotKey);
    if (lMutablePair == null)
      return false;
    return lMutablePair.getLeft();
  }

  @Override
  public Set<String> getAvailableImagesSlotKeys()
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

    Set<String> lAvailableImageKeys = getAvailableImagesSlotKeys();

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
