package clearcontrol.core.variable;

/**
 * Variable sync inerface
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableSyncInterface<O>
{

  /**
   * Sends updates to the given variable
   * 
   * @param pVariable
   *          variable to send updates to
   */
  public void sendUpdatesTo(Variable<O> pVariable);

  /**
   * Do not send updates (anymore) to the given variable
   * 
   * @param pVariable
   *          variable not to send updates (anymore) to
   */
  public void doNotSendUpdatesTo(Variable<O> pVariable);

  /**
   * This variable will not send updates to any other variable (but might well
   * receive updates from other variables!)
   */
  public void doNotSendAnyUpdates();

  /**
   * Synced this variable with teh given variable. This means that this and the
   * given variable will send each other updates
   * 
   * @param pVariable
   *          variable to cync to
   */
  public void syncWith(Variable<O> pVariable);

  /**
   * Stops syncing this and the given variable together.
   * 
   * @param pVariable
   *          variable not to sync with
   */
  public void doNotSyncWith(Variable<O> pVariable);
  /**/
}
