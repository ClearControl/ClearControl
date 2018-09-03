package clearcontrol.stack.sourcesink;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.stack.sourcesink.server.StackServerBase;

/**
 * Base class providing common fields and methods for a local file stack sinks
 * and sources
 *
 * @author royer
 */
public abstract class FileStackBase extends StackServerBase
                                    implements FileStackInterface
{
  private boolean mReadOnly;

  protected File mFolder;
  protected File mStacksFolder;
  protected ConcurrentHashMap<String, File> mChannelToFolderMap =
                                                                new ConcurrentHashMap<>();

  protected ConcurrentHashMap<String, File> mChannelToIndexFileMap =
                                                                   new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, File> mChannelToMetadataFileMap =
                                                                      new ConcurrentHashMap<>();

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
  }

  @Override
  public File getLocation()
  {
    return mFolder;
  }

  @Override
  public ArrayList<String> getChannelList()
  {
    ArrayList<String> lChannelList = new ArrayList<String>();

    File[] lListOfFiles = mFolder.listFiles();

    for (File lFile : lListOfFiles)
    {
      String lFileName = lFile.getName();
      if (lFileName.endsWith(".index.txt"))
      {
        String lChannel = lFileName.replaceAll(".index.txt", "");
        lChannelList.add(lChannel);
      }
    }

    return lChannelList;
  }

  protected File getExampleDataFile()
  {
    ArrayList<String> lChannelList = getChannelList();

    if (lChannelList.isEmpty())
      return null;

    String lFirstChannel = lChannelList.get(0);

    File lChannelFolder = getChannelFolder(lFirstChannel);

    File[] lListFiles = lChannelFolder.listFiles();

    if (lListFiles.length == 0)
      return null;

    return lListFiles[0];
  }

  protected File getIndexFile(String pChannel)
  {
    File lIndexFile = mChannelToIndexFileMap.get(pChannel);
    if (lIndexFile == null)
    {
      lIndexFile = new File(mFolder, pChannel + ".index.txt");
      lIndexFile.getParentFile().mkdirs();
      mChannelToIndexFileMap.put(pChannel, lIndexFile);
    }
    return lIndexFile;
  }

  protected File getMetadataFile(String pChannel)
  {
    File lMetadataFile = mChannelToMetadataFileMap.get(pChannel);
    if (lMetadataFile == null)
    {
      lMetadataFile = new File(mFolder, pChannel + ".metadata.txt");
      lMetadataFile.getParentFile().mkdirs();
      mChannelToMetadataFileMap.put(pChannel, lMetadataFile);
    }
    return lMetadataFile;
  }

  protected File getChannelFolder(String pChannel)
  {
    File lChannelFolder = mChannelToFolderMap.get(pChannel);
    if (lChannelFolder == null)
    {
      lChannelFolder = new File(mStacksFolder, pChannel);
      if (!lChannelFolder.exists())
        lChannelFolder.mkdirs();
      mChannelToFolderMap.put(pChannel, lChannelFolder);
    }
    return lChannelFolder;
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
