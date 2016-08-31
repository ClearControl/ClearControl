package clearcontrol.hardware.slm.slms.devices.alpao;

import org.ejml.data.DenseMatrix64F;

import asdk.AlpaoDeformableMirror;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.slm.slms.DeformableMirrorDevice;

public class AlpaoDMDevice extends DeformableMirrorDevice	implements
																													LoggingInterface
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
		super("ALPAO_" + pAlpaoSerialName,
					cFullMatrixWidthHeight,
					cActuatorResolution);

		mAlpaoDeformableMirror = new AlpaoDeformableMirror(pAlpaoSerialName);

		mMatrixVariable = new Variable<DenseMatrix64F>("MatrixReference")
		{
			@Override
			public DenseMatrix64F setEventHook(	final DenseMatrix64F pOldValue,
																					final DenseMatrix64F pNewValue)
			{

				mAlpaoDeformableMirror.sendFullMatrixMirrorShapeVector(pNewValue.data);

				return super.setEventHook(pOldValue, pNewValue);
			}

		};

	}

	@Override
	public boolean open()
	{
		try
		{
			final boolean lOpen = mAlpaoDeformableMirror.open();
			mNumberOfActuatorsVariable = new Variable<Double>("NumberOfActuators",
																												(double) mAlpaoDeformableMirror.getNumberOfActuators());
			return lOpen;
		}
		catch (final Throwable e)
		{
			final String lErrorString = "Could not open connection to ALPAO DM - " + e.getLocalizedMessage();
			severe("AO", lErrorString);
			return false;
		}

	}

	@Override
	public boolean start()
	{
		zero();
		return true;
	}

	@Override
	public void zero()
	{
		mAlpaoDeformableMirror.sendFlatMirrorShapeVector();
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
		catch (final Throwable e)
		{
			final String lErrorString = "Could not close connection to ALPAO DM - " + e.getLocalizedMessage();
			severe("AO", lErrorString);
			return false;
		}
	}

	@Override
	public long getRelaxationTimeInMilliseconds()
	{
		return 1;
	}

}
