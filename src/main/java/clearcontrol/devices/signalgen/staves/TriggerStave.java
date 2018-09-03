package clearcontrol.devices.signalgen.staves;

public class TriggerStave extends IntervalStave
                          implements StaveInterface
{

  public TriggerStave(String pName)
  {
    super(pName);
  }

  public TriggerStave(String pName,
                      float pSyncStart,
                      float pSyncStop,
                      float pInsideValue,
                      float pOutsideValue)
  {
    super(pName, pSyncStart, pSyncStop, pInsideValue, pOutsideValue);
  }

}
