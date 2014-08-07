package rtlib.ao.dms;

import java.io.IOException;

import mirao52.udp.Mirao52UDPClient;
import rtlib.ao.DeformableMirrorDevice;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.log.Loggable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArray;

public class Mirao52eDevice extends DeformableMirrorDevice implements
																													Loggable
{
	private static final int cFullMatrixWidthHeight = 8;
	private static final int cActuatorResolution = 2 << 14;
	
	private Mirao52UDPClient mMirao52UDPClient;
	private String mHostname;
	private int mPort;

	public Mirao52eDevice(int pDeviceIndex)
	{
		super("MIRAO52e");
		mMatrixWidthVariable = new DoubleVariable("MatrixWidth",
																							(double) cFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
																								(double) cFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
																											(double) cActuatorResolution);
		mNumberOfActuatorsVariable = new DoubleVariable("NumberOfActuators",
																											(double) cActuatorResolution);
		
		mNumberOfReceivedShapesVariable = new DoubleVariable(	"NumberOfReceivedShapesVariable",
																													0);

		mMatrixVariable = new ObjectVariable<NDArray>("MatrixReference")
		{
			@Override
			public NDArray setEventHook(final NDArray pOldValue,
																	final NDArray pNewValue)
			{
				if (mMirao52UDPClient.isReady())
					mMirao52UDPClient.sendFullMatrixMirrorShapeVector(pNewValue.getRAM()
																																		.passNativePointerToByteBuffer()
																																		.asDoubleBuffer());
				long lNumberOfReceivedShapes = mMirao52UDPClient.getNumberOfReceivedShapes();
				mNumberOfReceivedShapesVariable.setValue(lNumberOfReceivedShapes);
				return super.setEventHook(pOldValue, pNewValue);
			}

		};
		

		String[] lNetworkDeviceHostnameAndPort = MachineConfiguration.getCurrentMachineConfiguration()
																																	.getNetworkDeviceHostnameAndPort(	"ao.dm.mirao52e",
													pDeviceIndex,
													"NULL");
		
		mHostname = lNetworkDeviceHostnameAndPort[0];
		mPort = Integer.parseInt(lNetworkDeviceHostnameAndPort[1]);
		
		
		mMirao52UDPClient = new Mirao52UDPClient();
	}

	@Override
	public boolean open()
	{
		try
		{
			mMirao52UDPClient.open(mHostname, mPort);
			mNumberOfReceivedShapesVariable.setValue(mMirao52UDPClient.getNumberOfReceivedShapes());
			return true;
		}
		catch (IOException e)
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
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		try
		{
			mMirao52UDPClient.close();
			return true;
		}
		catch (IOException e)
		{
			String lErrorString = "Could not close connection to Mirao52e DM - " + e.getLocalizedMessage();
			error("AO", lErrorString);
			return false;
		}
	}



	

}
