package rtlib.hardware.slm.slms;

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
