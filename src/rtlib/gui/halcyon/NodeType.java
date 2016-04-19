package rtlib.gui.halcyon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import halcyon.model.node.HalcyonNodeType;
import javafx.scene.Node;

/**
 * HalcyonNode Type enumeration
 */
public enum NodeType implements HalcyonNodeType
{
	Camera,
	Laser,
	LightSheet,
	Stage,
	FilterWheel,
	OpticalSwitch,
	SignalGenerator,
	ScalingAmplifier,
	AdaptiveOptics,
	Scripting,
	StackDisplay2D,
	StackDisplay3D,
	Other;

	private static Properties mProperties;
	static
	{
		try
		{
			mProperties = new Properties();
			InputStream lResourceAsStream = NodeType.class.getResourceAsStream("icons/IconMap.properties");
			mProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Node getIcon()
	{
		String lKey = name().toLowerCase() + ".icon";
		try
		{
			String lProperty = mProperties.getProperty(lKey);
			if (lProperty == null)
			{
				System.err.println("Cannot find property for key: " + lKey);
				return null;
			}

			Node lIcon = getIcon(lProperty);

			if (lIcon == null)
			{
				System.err.println("Cannot find icon for key: " + lProperty);
				return null;
			}

			return lIcon;
		}
		catch (Throwable e)
		{
			System.err.println("Problem while obtaining icon for key: " + lKey);
			e.printStackTrace();
			return null;
		}
	}
}
