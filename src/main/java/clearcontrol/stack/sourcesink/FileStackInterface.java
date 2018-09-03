package clearcontrol.stack.sourcesink;

import java.io.File;

/**
 * interface implemented by all file stack sources and sinks
 *
 * @author royer
 */
public interface FileStackInterface extends AutoCloseable
{

  /**
   * Sets the location to read/write from
   * 
   * @param pRootFolder
   *          root folder
   * @param pDataSetName
   *          dataset name (which will correspond to a sub-folder of the root
   *          folder)
   */
  void setLocation(File pRootFolder, String pDataSetName);

  /**
   * A File object representing the directory, where meta header and log files
   * are written to
   *
   * @return File
   */
  File getLocation();

}
