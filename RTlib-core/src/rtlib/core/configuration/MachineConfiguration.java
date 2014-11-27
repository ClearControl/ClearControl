package rtlib.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

public class MachineConfiguration
{
	private static final String cComments = "RTlib machine configuration file";
	private static final MachineConfiguration sConfiguration = new MachineConfiguration();

	public static MachineConfiguration getCurrentMachineConfiguration()
	{
		return sConfiguration;
	}

	private Properties mProperties;

	int fummy = 0;

	public MachineConfiguration()
	{
		super();

		try
		{
			String lUserHome = System.getProperty("user.home");
			File lUserHomeFolder = new File(lUserHome);
			File lRTLibFolder = new File(lUserHomeFolder, "RTlib/");
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
			mProperties = null;
		}
	}

	public Properties getProperties()
	{
		return mProperties;
	}

	public boolean containsKey(String pKey)
	{
		if (mProperties == null)
			return false;
		return mProperties.containsKey(pKey);
	}

	public String getStringProperty(String pKey, String pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		return mProperties.getProperty(pKey, pDefaultValue);
	}

	public int getIntegerProperty(String pKey, int pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Integer.parseInt(lProperty);
	}

	public long getLongProperty(String pKey, long pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Long.parseLong(lProperty);
	}

	public double getDoubleProperty(String pKey, double pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Double.parseDouble(lProperty);
	}

	public boolean getBooleanProperty(String pKey, boolean pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Boolean.parseBoolean(lProperty.toLowerCase()) || lProperty.trim()
																																			.equals("1")
						|| lProperty.trim().toLowerCase().equals("on")
						|| lProperty.trim().toLowerCase().equals("present")
						|| lProperty.trim().toLowerCase().equals("true");
	}

	public File getFileProperty(String pKey, File pDefaultFile)
	{
		return new File(getStringProperty(pKey,
																			pDefaultFile == null ? null
																													: pDefaultFile.getPath()));
	}

	public String getSerialDevicePort(String pDeviceName,
																		int pDeviceIndex,
																		String pDefaultPort)
	{
		String lKey = "device.serial." + pDeviceName.toLowerCase()
									+ "."
									+ pDeviceIndex;
		String lPort = getStringProperty(lKey, pDefaultPort);
		return lPort;
	}

	public String[] getNetworkDeviceHostnameAndPort(String pDeviceName,
																									int pDeviceIndex,
																									String pDefaultHostNameAndPort)
	{
		String lKey = "device.network." + pDeviceName.toLowerCase()
									+ "."
									+ pDeviceIndex;
		String lHostnameAndPort = getStringProperty(lKey,
																								pDefaultHostNameAndPort);
		return lHostnameAndPort.split(":");
	}

	public Integer getIODevicePort(	String pDeviceName,
																	Integer pDefaultPort)
	{
		String lKey = "device." + pDeviceName.toLowerCase();
		Integer lPort = getIntegerProperty(lKey, pDefaultPort);
		return lPort;
	}

	public boolean getIsDevicePresent(String pDeviceName,
																		int pDeviceIndex)
	{
		String lKey = "device." + pDeviceName.toLowerCase()
									+ "."
									+ pDeviceIndex;
		return getBooleanProperty(lKey, false);
	}

	public ArrayList<String> getList(String pPrefix)
	{
		ArrayList<String> lList = new ArrayList<String>();
		for (int i = 0; i < Integer.MAX_VALUE; i++)
		{
			String lKey = pPrefix + "." + i;
			String lProperty = mProperties.getProperty(lKey, null);
			if (lProperty == null)
				break;
			lList.add(lProperty);
		}
		return lList;
	}

}
