package rtlib.ao;

import rtlib.core.variable.doublev.DoubleVariable;


public abstract class DeformableMirrorDevice extends
																						SpatialPhaseModulatorDevice
{

	protected DoubleVariable mNumberOfReceivedShapesVariable;

	public DeformableMirrorDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

	public DoubleVariable getNumberOfReceivedShapesVariable()
	{
		return mNumberOfReceivedShapesVariable;
	}

}
