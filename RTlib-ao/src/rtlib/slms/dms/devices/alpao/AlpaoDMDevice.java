package rtlib.slms.dms.devices.alpao;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.log.Loggable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.slms.dms.DeformableMirrorDevice;
import asdk.AlpaoDeformableMirror;

public class AlpaoDMDevice extends DeformableMirrorDevice	implements
																													Loggable
{
	private static final int cFullMatrixWidthHeight = 11;
	private static final int cActuatorResolution = 2 << 14;

	private AlpaoDeformableMirror mAlpaoDeformableMirror;

	public AlpaoDMDevice(int pAlpaoDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getStringProperty(	"device.ao.dm.alpao." + pAlpaoDeviceIndex,
																									"NULL"));

	}

	public AlpaoDMDevice(String pAlpaoSerialName)
	{
		super("ALPAO_" + pAlpaoSerialName);

		mAlpaoDeformableMirror = new AlpaoDeformableMirror(pAlpaoSerialName);

		mMatrixWidthVariable = new DoubleVariable("MatrixWidth",
																							(double) cFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
																								(double) cFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
																											(double) cActuatorResolution);

		mMatrixVariable = new ObjectVariable<NDArrayTyped<Double>>("MatrixReference")
		{
			@Override
			public NDArrayTyped<Double> setEventHook(	final NDArrayTyped<Double> pOldValue,
																								final NDArrayTyped<Double> pNewValue)
			{

				mAlpaoDeformableMirror.sendFullMatrixMirrorShapeVector(pNewValue.getBridJPointer(Double.class));

				return super.setEventHook(pOldValue, pNewValue);
			}

		};

	}

	@Override
	public boolean open()
	{
		try
		{
			boolean lOpen = mAlpaoDeformableMirror.open();
			mNumberOfActuatorsVariable = new DoubleVariable("NumberOfActuators",
																											(double) mAlpaoDeformableMirror.getNumberOfActuators());
			return lOpen;
		}
		catch (Throwable e)
		{
			String lErrorString = "Could not open connection to ALPAO DM - " + e.getLocalizedMessage();
			error("AO", lErrorString);
			return false;
		}

	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		try
		{
			mAlpaoDeformableMirror.close();
			return true;
		}
		catch (Throwable e)
		{
			String lErrorString = "Could not close connection to ALPAO DM - " + e.getLocalizedMessage();
			error("AO", lErrorString);
			return false;
		}
	}

}
