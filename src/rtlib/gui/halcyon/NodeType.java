package rtlib.gui.halcyon;

import halcyon.model.node.HalcyonNodeType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	AdaptiveOptics,
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
		return getIcon(mProperties.getProperty(name().toLowerCase() + ".icon"));
	}
}
