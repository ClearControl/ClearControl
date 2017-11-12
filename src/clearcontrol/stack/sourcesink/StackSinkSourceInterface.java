package clearcontrol.stack.sourcesink;

import java.util.ArrayList;

import com.google.common.collect.Lists;

/**
 * Interface for stack sources and sinks.
 *
 * @author royer
 */
public interface StackSinkSourceInterface
{
  /**
   * Stacks sinks ans sources default channel
   */
  public static final String cDefaultChannel = "default";

  /**
   * Basename string for stacks written to files
   */
  public static final String cBasename = "%06d";

  /**
   * Filename suffix for stacks written to files
   */
  public static final String cDefaultFileExtension = ".raw";

  /**
   * Filename suffix for stacks written to files using sqeazy
   */
  public static final String cSqeazyFileExtension = ".sqy";

  /**
   * Filename suffix for stacks written to files using sqeazy in HDF5 format
   */
  public static final String cSqeazyHDF5FileExtension = ".h5";

  /**
   * Format used to write the stacks filenames
   */
  public static final String cFormat = cBasename
                                       + cDefaultFileExtension;

  /**
   * Returns the list of available channels for this source or sink
   * 
   * @return list of channels
   */
  default ArrayList<String> getChannelList()
  {
    return Lists.newArrayList(cDefaultChannel);
  }

}
