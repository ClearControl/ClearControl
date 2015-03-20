package rtlib.ao.slms.dms;

import rtlib.ao.slms.SpatialPhaseModulatorDeviceBase;

public abstract class DeformableMirrorDevice extends
																						SpatialPhaseModulatorDeviceBase
{

	public DeformableMirrorDevice(final String pDeviceName,
																int pFullMatrixWidthHeight,
																int pActuatorResolution)
	{
		super(pDeviceName, pFullMatrixWidthHeight, pActuatorResolution);
	}

}
