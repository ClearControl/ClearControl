package clearcontrol.core.variable;

public interface VariableSyncInterface<O>
{

  public void sendUpdatesTo(Variable<O> pVariable);

  public void doNotSendUpdatesTo(Variable<O> pVariable);

  public void doNotSendAnyUpdates();

  public void syncWith(Variable<O> pVariable);

  public void doNotSyncWith(Variable<O> pVariable);
  /**/
}
