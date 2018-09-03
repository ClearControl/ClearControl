package clearcontrol.devices.signalamp.devices.srs.adapters;

import static java.lang.Math.max;
import static java.lang.Math.min;

import clearcontrol.devices.signalamp.devices.srs.SIM900MainframeDevice;
import clearcontrol.devices.signalamp.devices.srs.adapters.protocol.ProtocolSIM;

public class OffsetAdapter extends SIMAdapter
{

  public OffsetAdapter(SIM900MainframeDevice pSim900MainframeDevice,
                       int pPort)
  {
    super(pSim900MainframeDevice, pPort, ProtocolSIM.cOffset);
  }

  @Override
  public Number clampSetValue(Number pValue)
  {
    return min(max(pValue.doubleValue(), -10.00), 10.00);
  }

}
