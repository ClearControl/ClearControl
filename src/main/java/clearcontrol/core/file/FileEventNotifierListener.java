package clearcontrol.core.file;

import java.io.File;

import clearcontrol.core.file.FileEventNotifier.FileEventKind;

/**
 * File event notifier listener
 *
 * @author royer
 */
public interface FileEventNotifierListener
{

  /**
   * Notifies of a file event originating from a given file event notifier, a
   * given file, and event kind
   * 
   * @param pThis
   *          file event notifier from which the event originates
   * @param pFile
   *          concerned file
   * @param pEventKind
   *          event kind
   */
  void fileEvent(FileEventNotifier pThis,
                 File pFile,
                 FileEventKind pEventKind);

}
