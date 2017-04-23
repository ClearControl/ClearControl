package clearcontrol.stack.sourcesink;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import clearcontrol.stack.sourcesink.server.StackServerBase;

/**
 * Base class providing common fields and methods for a local file stack sinks
 * and sources
 *
 * @author royer
 */
public abstract class FileStackBase extends StackServerBase implements
                                    FileStackInterface,
                                    AutoCloseable
{
  private boolean mReadOnly;

  protected File mFolder;
  protected File mStacksFolder;

  protected File mIndexFile;
  protected File mMetaDataFile;

  /**
   * Instantiates a local file stack source or sink. The method setLocation must
   * be called to set a folder location
   * 
   * @param pReadOnly
   *          read only
   */
  public FileStackBase(final boolean pReadOnly)
  {
    super();
    mReadOnly = pReadOnly;
  }

  @Override
  public void setLocation(final File pRootFolder, final String pName)
  {
    mFolder = new File(pRootFolder, pName);

    mStacksFolder = new File(mFolder, "/stacks/");

    if (!mReadOnly)
    {
      mStacksFolder.mkdirs();
    }

    mIndexFile = new File(mFolder, "/index.txt");
    if (!mReadOnly)
    {
      mIndexFile.getParentFile().mkdirs();
    }

    mMetaDataFile = new File(mFolder, "/metadata.txt");
    if (!mReadOnly)
    {
      mMetaDataFile.getParentFile().mkdirs();
    }
  }

  protected FileChannel getFileChannel(File pFile,
                                       final boolean pReadOnly) throws IOException
  {
    FileChannel lFileChannel;
    if (pReadOnly)
    {
      lFileChannel = FileChannel.open(pFile.toPath(),
                                      StandardOpenOption.READ);

    }
    else
    {

      lFileChannel = FileChannel.open(pFile.toPath(),
                                      StandardOpenOption.APPEND,
                                      StandardOpenOption.WRITE,
                                      StandardOpenOption.CREATE);

    }
    return lFileChannel;
  }

  /**
   * Returns data folder
   * 
   * @return data folder
   */
  public File getDataFolder()
  {
    return mStacksFolder;
  }

  /**
   * Returns folder
   * 
   * @return folder
   */
  public File getFolder()
  {
    return mFolder;
  }

  @Override
  public void close() throws IOException
  {
  }

}
