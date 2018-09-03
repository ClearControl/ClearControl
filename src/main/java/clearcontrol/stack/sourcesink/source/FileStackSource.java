package clearcontrol.stack.sourcesink.source;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.FileStackBase;
import clearcontrol.stack.sourcesink.FileStackInterface;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * Raw file stack sink
 *
 * @author royer
 */
public class FileStackSource extends FileStackBase implements
                             FileStackInterface,
                             FileStackSourceInterface,
                             AutoCloseable
{

  FileStackSourceInterface mDelegatedFileStackSource;
  private BasicRecycler<StackInterface, StackRequest> mStackRecycler;

  /**
   * Instantiates a file stack sink given a root folder and dataset name. This
   * delegates to the appropriate file stack sink by determining the format
   * automatically.
   * 
   * @param pStackRecycler
   *          stack recycler
   * 
   */
  public FileStackSource(final BasicRecycler<StackInterface, StackRequest> pStackRecycler)
  {
    super(false);
    mStackRecycler = pStackRecycler;
  }

  @Override
  public void setLocation(File pRootFolder, String pName)
  {
    super.setLocation(pRootFolder, pName);
    discoverType(pRootFolder, pName);
    mDelegatedFileStackSource.setLocation(pRootFolder, pName);
  }

  private void discoverType(File pRootFolder, String pName)
  {
    String lExampleDataFileName = getExampleDataFile().getName();

    if (lExampleDataFileName.contains(".raw"))
    {
      mDelegatedFileStackSource =
                                new RawFileStackSource(mStackRecycler);
    }
    else if (lExampleDataFileName.contains(".sqy"))
    {
      /// would be sqeazy...
    }

  }

  @Override
  public boolean update()
  {
    return mDelegatedFileStackSource.update();
  }

  @Override
  public long getNumberOfStacks()
  {
    return mDelegatedFileStackSource.getNumberOfStacks();
  }

  @Override
  public long getNumberOfStacks(String pChannel)
  {
    return mDelegatedFileStackSource.getNumberOfStacks(pChannel);
  }

  @Override
  public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    mDelegatedFileStackSource.setStackRecycler(pStackRecycler);
  }

  @Override
  public StackInterface getStack(long pStackIndex)
  {
    return mDelegatedFileStackSource.getStack(pStackIndex);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex)
  {
    return mDelegatedFileStackSource.getStack(pChannel,
                                              pStackIndex,
                                              1,
                                              TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex,
                                 final long pTime,
                                 final TimeUnit pTimeUnit)
  {
    return mDelegatedFileStackSource.getStack(pChannel,
                                              pStackIndex,
                                              pTime,
                                              pTimeUnit);
  }

  @Override
  public void close() throws IOException
  {
    try
    {
      mDelegatedFileStackSource.close();
    }
    catch (Throwable e)
    {
      throw new IOException("Exception while closing", e);
    }
  }

}
