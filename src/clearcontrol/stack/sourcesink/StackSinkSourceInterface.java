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
   * Format used to write the stacks filenames
   */
  public static final String cFormat = "%06d.raw";

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
