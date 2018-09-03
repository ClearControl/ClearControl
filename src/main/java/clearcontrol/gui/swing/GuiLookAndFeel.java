package clearcontrol.gui.swing;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ch.randelshofer.quaqua.QuaquaManager;
import clearcontrol.core.configuration.MachineConfiguration;

public class GuiLookAndFeel
{
  public static final void setSystemLookAndFeel()
  {
    String lSystemLookAndFeelClassName =
                                       UIManager.getSystemLookAndFeelClassName();

    setLookAndFeel(lSystemLookAndFeelClassName);
  }

  public static void setLookAndFeel(String pLookAndFeel)
  {
    try
    {
      UIManager.setLookAndFeel(pLookAndFeel);
    }
    catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
  }

  public static void setLookAndFeelFromMachineConfiguration()
  {
    final MachineConfiguration lCurrentMachineConfiguration =
                                                            MachineConfiguration.get();

    String lLookAndFeelName =
                            lCurrentMachineConfiguration.getStringProperty("lookandfeel",
                                                                           "system");

    if (lLookAndFeelName.equalsIgnoreCase("system"))
      setSystemLookAndFeel();
    else if (lLookAndFeelName.equalsIgnoreCase("metal"))
      setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    else if (lLookAndFeelName.equalsIgnoreCase("nimbus"))
      setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    else if (lLookAndFeelName.equalsIgnoreCase("seaglass"))
      setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
    else if (lLookAndFeelName.equalsIgnoreCase("jgplastic"))
      setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
    else if (lLookAndFeelName.equalsIgnoreCase("jgwindows"))
      setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
    else if (lLookAndFeelName.equalsIgnoreCase("quaqua"))
      setLookAndFeel("" + QuaquaManager.getLookAndFeelClassName());
    else
      setLookAndFeel(lLookAndFeelName);

  }
}
