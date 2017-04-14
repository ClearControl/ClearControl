package clearcontrol.core.device.task;

import clearcontrol.core.variable.Variable;

/**
 * Running task interface
 *
 * @author royer
 */
public interface IsRunningTaskInterface
{

  /**
   * Returns the variable that indicates whether this task is currently running
   * 
   * @return is-running variable
   */
  Variable<Boolean> getIsRunningVariable();

}
