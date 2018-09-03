package clearcontrol.core.file;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.log.LoggingFeature;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * File event notifier.
 * 
 * 
 *
 * @author royer
 */
public class FileEventNotifier implements
                               AutoCloseable,
                               LoggingFeature
{

  private static final long cDefaultMonitoringPeriodInMilliseconds =
                                                                   300;
  private final File mFileToMonitor;
  private final File mParentFolder;
  private final FileAlterationObserver mFileAlterationObserver;

  private final CopyOnWriteArrayList<FileEventNotifierListener> mListenerList =
                                                                              new CopyOnWriteArrayList<FileEventNotifierListener>();
  private final FileAlterationMonitor mFileAlterationMonitor;
  private volatile boolean mIgnore = false;

  /**
   * File event kind.
   *
   * @author royer
   */
  public static enum FileEventKind
  {
   /**
    * Event triggered when a file is created
    */
   Created,
   /**
    * Event triggered when a file is modified
    */
   Modified,
   /**
    * Event triggered when a file is deleted
    */
   Deleted
  }

  /**
   * Instantiates a file event notifier for a given file
   * 
   * @param pFileToMonitor
   *          file to monitor
   */
  public FileEventNotifier(final File pFileToMonitor)
  {
    this(pFileToMonitor,
         cDefaultMonitoringPeriodInMilliseconds,
         TimeUnit.MILLISECONDS);
  }

  /**
   * Instantiates a file event notifier for a given file
   * 
   * @param pFileToMonitor
   *          file to monitor
   * @param pMonitoringPeriod
   *          monitoring period
   * @param pTimeUnit
   *          monitoring period time unit
   */
  public FileEventNotifier(final File pFileToMonitor,
                           final long pMonitoringPeriod,
                           final TimeUnit pTimeUnit)
  {
    super();
    mFileToMonitor = pFileToMonitor;
    mParentFolder = mFileToMonitor.getParentFile();

    final FileEventNotifier lThis = this;

    mFileAlterationObserver =
                            new FileAlterationObserver(mParentFolder);
    mFileAlterationObserver.addListener(new FileAlterationListener()
    {

      @Override
      public void onStop(final FileAlterationObserver pObserver)
      {
      }

      @Override
      public void onStart(final FileAlterationObserver pObserver)
      {
      }

      @Override
      public void onFileDelete(final File pFile)
      {
        notifyFileEvent(lThis, pFile, FileEventKind.Deleted);
      }

      @Override
      public void onFileCreate(final File pFile)
      {
        notifyFileEvent(lThis, pFile, FileEventKind.Created);
      }

      @Override
      public void onFileChange(final File pFile)
      {
        notifyFileEvent(lThis, pFile, FileEventKind.Modified);
      }

      @Override
      public void onDirectoryDelete(final File pDirectory)
      {
        notifyFileEvent(lThis, pDirectory, FileEventKind.Deleted);
      }

      @Override
      public void onDirectoryCreate(final File pDirectory)
      {
      }

      @Override
      public void onDirectoryChange(final File pDirectory)
      {
      }
    });

    mFileAlterationMonitor =
                           new FileAlterationMonitor(TimeUnit.MILLISECONDS.convert(pMonitoringPeriod,
                                                                                   pTimeUnit));
    mFileAlterationMonitor.addObserver(mFileAlterationObserver);
  }

  /**
   * Adds a given file change event listener.
   * 
   * @param pFileChangeNotifierListener
   *          file change event listener
   */
  public void addFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
  {
    mListenerList.add(pFileChangeNotifierListener);
  }

  /**
   * Removes a given file change event listener
   * 
   * @param pFileChangeNotifierListener
   *          file change event listener
   */
  public void removeFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
  {
    mListenerList.remove(pFileChangeNotifierListener);
  }

  /**
   * Removes all file change event listener
   * 
   * @param pFileChangeNotifierListener
   *          file change event listener
   */
  public void removeAllFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
  {
    mListenerList.clear();
  }

  protected void notifyFileEvent(final FileEventNotifier pThis,
                                 final File pFile,
                                 final FileEventKind pEventKind)
  {
    if (mIgnore)
      return;
    info("Event: %s \t\t %s", pFile, pEventKind);
    if (pFile.getName().equals(mFileToMonitor.getName()))
    {
      for (final FileEventNotifierListener lFileEventNotifierListener : mListenerList)
      {
        lFileEventNotifierListener.fileEvent(pThis,
                                             pFile,
                                             pEventKind);
      }
    }
  }

  /**
   * Starts monitoring for file changes.
   * 
   * @return true if started successfully.
   * 
   */
  public boolean startMonitoring()
  {
    try
    {
      mFileAlterationMonitor.start();
      return true;
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Starts monitoring for file changes.
   * 
   * @return true if stopped successfully.
   * 
   */
  public boolean stopMonitoring()
  {
    try
    {
      mFileAlterationMonitor.stop();
      return true;
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void close() throws IOException
  {
    try
    {
      mFileAlterationObserver.destroy();
      mFileAlterationMonitor.stop();
    }
    catch (final Throwable e)
    {
    }
  }

  /**
   * Sets ignore flag.
   * 
   * @param pIgnore
   *          new flag value
   */
  public void setIgnore(boolean pIgnore)
  {
    mIgnore = pIgnore;
  }

}
