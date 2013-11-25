package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class MachineConfiguration
{

	private static final String cComments = "RTlib machine configuration file";
	private static final MachineConfiguration sConfiguration = new MachineConfiguration();

	private Properties mProperties;
	
	int fummy =0;

	public MachineConfiguration()
	{
		super();

		try
		{
			String lUserHome = System.getProperty("user.home");
			File lUserHomeFolder = new File(lUserHome);
			File lRTLibFolder = new File(lUserHomeFolder,"RTlib/");
			lRTLibFolder.mkdirs();
			File lConfigurationFile = new File(	lRTLibFolder,
																					"configuration.txt");

			if (!lConfigurationFile.exists())
			{
				Writer lWriter = new FileWriter(lConfigurationFile);
				mProperties.store(lWriter, cComments);
			}
			FileInputStream lFileInputStream = new FileInputStream(lConfigurationFile);
			mProperties = new Properties();
			mProperties.load(lFileInputStream);
		}

		catch (IOException e2)
		{
			e2.printStackTrace();
			mProperties=null;
		}
	}

	public Properties getProperties()
	{
		return mProperties;
	}

	public boolean containsKey(String pKey)
	{
		if(mProperties==null) return false;
		return mProperties.containsKey(pKey);
	}
	
	public String getStringProperty(String pKey, String pDefaultValue)
	{
		if(mProperties==null) return pDefaultValue;
		return mProperties.getProperty(pKey, pDefaultValue);
	}

	public long getIntegerProperty(String pKey, long pDefaultValue)
	{
		if(mProperties==null) return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Long.parseLong(lProperty);
		
	}

	public double getDoubleProperty(String pKey, double pDefaultValue)
	{
		if(mProperties==null) return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Double.parseDouble(lProperty);
	}
	
	public String getSerialDevicePort(String pDeviceName, int pDeviceIndex, String pDefaultPort)
	{
		String lKey = "device.serial."+pDeviceName.toLowerCase()+"."+pDeviceIndex;
		return getStringProperty(lKey, pDefaultPort);
	}

	public static MachineConfiguration getCurrentMachineConfiguration()
	{
		return sConfiguration;
	}



}
