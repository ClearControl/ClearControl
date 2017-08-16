package clearcontrol.stack.sourcesink.sink;

import java.io.File;
import java.io.IOException;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.sourcesink.FileStackBase;

/**
 * Raw file stack sink
 *
 * @author royer
 */
public class FileStackSink extends FileStackBase implements
                           FileStackSinkInterface,
                           AutoCloseable
{

  FileStackSinkInterface mDelegatedFileStackSink;

  /**
   * Instantiates a file stack sink given a root folder and dataset name. This
   * delegates to the appropriate file stack sink by determining the format
   * automatically.
   * 
   */
  public FileStackSink()
  {
    super(false);
  }

  @Override
  public void setLocation(File pRootFolder, String pName)
  {
    super.setLocation(pRootFolder, pName);

    discoverType(pRootFolder, pName);
  }

  private void discoverType(File pRootFolder, String pName)
  {
    String lExampleDataFileName = getExampleDataFile().getName();

    if (lExampleDataFileName.contains(".raw"))
    {
      mDelegatedFileStackSink = new RawFileStackSink();
    }
    else if (lExampleDataFileName.contains(".sqy"))
    {
      /// would be sqeazy
    }

  }

  @Override
  public boolean appendStack(StackInterface pStack)
  {
    return mDelegatedFileStackSink.appendStack(cDefaultChannel,
                                               pStack);
  }

  @Override
  public boolean appendStack(String pChannel,
                             final StackInterface pStack)
  {
    return mDelegatedFileStackSink.appendStack(pChannel, pStack);
  }

  @Override
  public void close() throws IOException
  {
    try
    {
      mDelegatedFileStackSink.close();
    }
    catch (Throwable e)
    {
      throw new IOException("Exception while closing", e);
    }
  }

}
