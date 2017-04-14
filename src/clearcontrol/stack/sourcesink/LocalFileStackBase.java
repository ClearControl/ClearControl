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
public abstract class LocalFileStackBase extends StackServerBase
                                         implements AutoCloseable
{
  protected final File mFolder;
  private final File mDataFolder;
  protected final File mBinaryFile;
  protected long mNextFreeStackIndex;
  protected long mNextFreeTypePosition;

  protected final File mIndexFile;
  protected final File mMetaDataFile;

  /**
   * Instanciates a local file stack source or sink.
   * 
   * @param pRootFolder
   *          root folder
   * @param pName
   *          dataset name
   * @param pReadOnly
   *          true -> read-only
   * @throws IOException
   *           thrown if there is an IO problem
   */
  public LocalFileStackBase(final File pRootFolder,
                            final String pName,
                            final boolean pReadOnly) throws IOException
  {
    super();
    mFolder = new File(pRootFolder, pName);
    mDataFolder = new File(mFolder, "/data/");
    mBinaryFile = new File(mDataFolder, "data.bin");

    if (!pReadOnly && mBinaryFile.exists())
    {
      throw new IOException(this.getClass().getSimpleName()
                            + ": Cannot overwrite an existing file!");
    }

    if (!pReadOnly)
    {
      final File lParentFile = mBinaryFile.getParentFile();
      lParentFile.mkdirs();
    }

    mNextFreeTypePosition = 0;

    mIndexFile = new File(mFolder, "/data/index.txt");
    if (!pReadOnly)
    {
      mIndexFile.getParentFile().mkdirs();
    }

    mMetaDataFile = new File(mFolder, "/metadata.txt");
    if (!pReadOnly)
    {
      mMetaDataFile.getParentFile().mkdirs();
    }

  }

  protected FileChannel getFileChannelForBinaryFile(final boolean pReadOnly,
                                                    final boolean pForMapping) throws IOException
  {
    FileChannel lFileChannel;
    if (pReadOnly)
    {
      if (pForMapping)
      {
        lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                                        StandardOpenOption.READ);
      }
      else
      {
        lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                                        StandardOpenOption.READ);
      }
    }
    else
    {
      if (pForMapping)
      {
        lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.READ,
                                        StandardOpenOption.WRITE);
      }
      else
      {
        lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                                        StandardOpenOption.APPEND,
                                        StandardOpenOption.WRITE,
                                        StandardOpenOption.CREATE);
      }
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
    return mDataFolder;
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
