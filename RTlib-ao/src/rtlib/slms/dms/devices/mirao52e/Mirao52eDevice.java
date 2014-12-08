package rtlib.slms.dms.devices.mirao52e;

import java.io.IOException;

import mirao52e.Mirao52eDeformableMirror;
import rtlib.core.log.Loggable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.slms.dms.DeformableMirrorDevice;

public class Mirao52eDevice extends DeformableMirrorDevice implements
																													Loggable
{
	private static final int cFullMatrixWidthHeight = 8;
	private static final int cActuatorResolution = 2 << 14;

	private Mirao52eDeformableMirror mMirao52eDeformableMirror;
	private String mHostname;
	private int mPort;

	public Mirao52eDevice(int pDeviceIndex)
	{
		super("MIRAO52e_" + pDeviceIndex);
		mMatrixWidthVariable = new DoubleVariable("MatrixWidth",
																							cFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
																								cFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
																											cActuatorResolution);
		mNumberOfActuatorsVariable = new DoubleVariable("NumberOfActuators",
																										cActuatorResolution);

		mMirao52eDeformableMirror = new Mirao52eDeformableMirror();

		mMatrixVariable = new ObjectVariable<NDArrayTyped<Double>>("MatrixReference")
		{
			@Override
			public NDArrayTyped<Double> setEventHook(	final NDArrayTyped<Double> pOldValue,
																								final NDArrayTyped<Double> pNewValue)
			{
				if (mMirao52eDeformableMirror.isOpen())
					mMirao52eDeformableMirror.sendFullMatrixMirrorShapeVector(pNewValue.getBridJPointer(Double.class));

				return super.setEventHook(pOldValue, pNewValue);
			}

		};

	}

	@Override
	public boolean open()
	{
		try
		{
			mMirao52eDeformableMirror.open();
			return true;
		}
		catch (Throwable e)
		{
			String lErrorString = "Could not open connection to Mirao52e DM - " + e.getLocalizedMessage();
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
	public void zero()
	{
		mMirao52eDeformableMirror.sendFlatMirrorShapeVector();
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
			mMirao52eDeformableMirror.close();
			return true;
		}
		catch (IOException e)
		{
			String lErrorString = "Could not close connection to Mirao52e DM - " + e.getLocalizedMessage();
			error("AO", lErrorString);
			return false;
		}
	}

	@Override
	public long getRelaxationTimeInMilliseconds()
	{
		return 5;
	}

}
