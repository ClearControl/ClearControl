package rtlib.slms.dms.devices.mirao52e;

import java.io.IOException;

import mirao52e.Mirao52eDeformableMirror;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.log.Loggable;
import rtlib.core.variable.objectv.ObjectVariable;
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
		super("MIRAO52e_" + pDeviceIndex,
					cFullMatrixWidthHeight,
					cActuatorResolution);

		mMirao52eDeformableMirror = new Mirao52eDeformableMirror();

		mMatrixVariable = new ObjectVariable<DenseMatrix64F>("MatrixReference")
		{
			@Override
			public DenseMatrix64F setEventHook(	final DenseMatrix64F pOldValue,
																					final DenseMatrix64F pNewValue)
			{
				if (mMirao52eDeformableMirror.isOpen())
					mMirao52eDeformableMirror.sendFullMatrixMirrorShapeVector(pNewValue.data);

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
		catch (final Throwable e)
		{
			final String lErrorString = "Could not open connection to Mirao52e DM - " + e.getLocalizedMessage();
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
		catch (final IOException e)
		{
			final String lErrorString = "Could not close connection to Mirao52e DM - " + e.getLocalizedMessage();
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
