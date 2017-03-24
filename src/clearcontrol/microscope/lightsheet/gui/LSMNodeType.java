package clearcontrol.microscope.lightsheet.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javafx.scene.Node;

import halcyon.model.node.HalcyonNodeType;

/**
 * HalcyonNode Type enumeration
 */
public enum LSMNodeType implements HalcyonNodeType
{
 /**
  * Lightsheet
  */
 LightSheet,

 /**
  * Detection arm
  */
 DetectionArm;

  private static Properties mProperties;
  static
  {
    try
    {
      mProperties = new Properties();
      InputStream lResourceAsStream =
                                    LSMNodeType.class.getResourceAsStream("icons/IconMap.properties");
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
      System.err.println("Problem while obtaining icon for key: "
                         + lKey);
      e.printStackTrace();
      return null;
    }
  }
}
