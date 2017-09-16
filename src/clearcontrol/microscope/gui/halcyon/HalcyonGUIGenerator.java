package clearcontrol.microscope.gui.halcyon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.stage.Stage;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.gui.ScriptingWindow;
import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonNodeType;
import halcyon.model.node.HalcyonOtherNode;
import halcyon.model.node.Window;
import halcyon.view.TreePanel;

import org.dockfx.DockNode;

/**
 * Halcyon GUI generator. Uses the Halcyon library (based on DockFX) to build a
 * GUI.
 *
 * @author royer
 */
public class HalcyonGUIGenerator implements LoggingFeature
{
  private MicroscopeInterface<?> mMicroscopeInterface;
  private HalcyonFrame mHalcyonFrame;
  private MicroscopeGUI mMicroscopeGUI;
  private HashMap<Class<?>, Class<?>> mDeviceClassToPanelMap =
                                                             new HashMap<>();
  private HashMap<Class<?>, HalcyonNodeType> mDeviceClassToHalcyonTypeMap =
                                                                          new HashMap<>();
  private HashMap<Class<?>, Class<?>> mDeviceClassToToolbarMap =
                                                               new HashMap<>();

  /**
   * Instanciates a Halcyon GUI generator from a given microscope, microscope
   * parent GUI, and a collection of halcyon node types.
   * 
   * @param pMicroscopeInterface
   *          micrscope
   * @param pMicroscopeGUI
   *          microscope GUI (parent of this generator)
   * @param pNodeTypeCollection
   *          node type list
   * @param pPrimaryStage
   *          JFX primary stage
   */
  public HalcyonGUIGenerator(MicroscopeInterface<?> pMicroscopeInterface,
                             MicroscopeGUI pMicroscopeGUI,
                             Collection<HalcyonNodeType> pNodeTypeCollection,
                             Stage pPrimaryStage)
  {
    mMicroscopeInterface = pMicroscopeInterface;
    mMicroscopeGUI = pMicroscopeGUI;
    initJavaFX(pPrimaryStage);

    TreePanel lTreePanel = new TreePanel("Device tree",
                                         "Devices",
                                         this.getClass()
                                             .getResourceAsStream("icons/folder_16.png"),
                                         pNodeTypeCollection);

    mHalcyonFrame = new HalcyonFrame(pMicroscopeInterface.getName());

    mHalcyonFrame.setTreePanel(lTreePanel);

  }

  /**
   * Adds a mapping between a device class, panel class and node type.
   * 
   * @param pDeviceClass
   *          device class
   * @param pPanelClass
   *          panel class
   * @param pNodeType
   *          node type
   */
  public <U, V> void addPanelMappingEntry(Class<U> pDeviceClass,
                                          Class<V> pPanelClass,
                                          HalcyonNodeType pNodeType)
  {
    mDeviceClassToPanelMap.put(pDeviceClass, pPanelClass);
    mDeviceClassToHalcyonTypeMap.put(pDeviceClass, pNodeType);
  }

  /**
   * Adds a toolbar mapping entry
   * 
   * @param pDeviceClass
   *          device class
   * @param pToolbarClass
   *          toolbar class
   */
  public <U, V> void addToolbarMappingEntry(Class<U> pDeviceClass,
                                            Class<V> pToolbarClass)
  {
    mDeviceClassToToolbarMap.put(pDeviceClass, pToolbarClass);
  }

  /**
   * Returns the halcyon frame used internally
   * 
   * @return halcyon frame
   */
  public HalcyonFrame getHalcyonFrame()
  {
    return mHalcyonFrame;
  }

  /**
   * Returns true if the halcyon frame (the window that this object handles) is
   * visible.
   * 
   * @return true if visible
   */
  public boolean isVisible()
  {
    return mHalcyonFrame.isVisible();
  }

  /**
   * Setup device GUIs (panels and toolbars) based on the defined mappings.
   */
  public void setupDeviceGUIs()
  {

    for (Entry<Class<?>, Class<?>> lEntry : mDeviceClassToPanelMap.entrySet())
    {
      Class<?> lDeviceClass = lEntry.getKey();
      Class<?> lGUIElementClass = lEntry.getValue();

      info("Setting up device GUI element class %s for device class: %s \n",
           lGUIElementClass.getSimpleName(),
           lDeviceClass.getSimpleName());
      setupDevicePanels(lDeviceClass, lGUIElementClass);
    }

    for (Entry<Class<?>, Class<?>> lEntry : mDeviceClassToToolbarMap.entrySet())
    {
      Class<?> lDeviceClass = lEntry.getKey();
      Class<?> lGUIElementClass = lEntry.getValue();

      info("Setting up device GUI element class %s for device class: %s \n",
           lGUIElementClass.getSimpleName(),
           lDeviceClass.getSimpleName());
      setupDeviceToolbars(lDeviceClass, lGUIElementClass);
    }

    // setting up script engines:
    setupScriptEngines(mMicroscopeInterface);

    // setting up 2D and 3D displays:
    setup2DDisplays();
    setup3DDisplays();

  }

  private void initJavaFX(Stage pPrimaryStage)
  {
    if (pPrimaryStage == null)
    {
      new JFXPanel(); // initializes JavaFX environment
    }
  }

  /**
   * Sets up required 3D displays in halcyon.
   */
  private void setup3DDisplays()
  {
    info("Setting up 3D displays");

    for (Stack3DDisplay lStack3DDisplay : mMicroscopeGUI.get3DDisplayDeviceList())
    {
      info("Setting up %s", lStack3DDisplay);
      HalcyonNodeInterface node =
                                new HalcyonOtherNode(lStack3DDisplay.getName(),
                                                     MicroscopeNodeType.StackDisplay3D,
                                                     new Window()
                                                     {
                                                       @Override
                                                       public int getWidth()
                                                       {
                                                         return lStack3DDisplay.getGLWindow()
                                                                               .getWindowWidth();
                                                       }

                                                       @Override
                                                       public int getHeight()
                                                       {
                                                         return lStack3DDisplay.getGLWindow()
                                                                               .getWindowHeight();
                                                       }

                                                       @Override
                                                       public void setSize(int width,
                                                                           int height)
                                                       {
                                                         lStack3DDisplay.getGLWindow()
                                                                        .setSize(width,
                                                                                 height);
                                                       }

                                                       @Override
                                                       public int getX()
                                                       {
                                                         return lStack3DDisplay.getGLWindow()
                                                                               .getWindowX();
                                                       }

                                                       @Override
                                                       public int getY()
                                                       {
                                                         return lStack3DDisplay.getGLWindow()
                                                                               .getWindowY();
                                                       }

                                                       @Override
                                                       public void setPosition(int x,
                                                                               int y)
                                                       {
                                                         lStack3DDisplay.getGLWindow()
                                                                        .setWindowPosition(x,
                                                                                           y);
                                                       }

                                                       @Override
                                                       public void show()
                                                       {
                                                         lStack3DDisplay.setVisible(true);
                                                         lStack3DDisplay.requestFocus();
                                                       }

                                                       @Override
                                                       public void hide()
                                                       {
                                                         lStack3DDisplay.setVisible(false);
                                                       }

                                                       @Override
                                                       public void close()
                                                       {
                                                         lStack3DDisplay.close();
                                                       }
                                                     });
      mHalcyonFrame.addNode(node);
    }
  }

  /**
   * Sets up 2D displays in halcyon.
   */
  private void setup2DDisplays()
  {
    info("Setting up 2D displays");

    for (Stack2DDisplay lStack2DDisplay : mMicroscopeGUI.get2DDisplayDeviceList())
    {
      info("Setting up %s", lStack2DDisplay);

      HalcyonNodeInterface node =
                                new HalcyonOtherNode(lStack2DDisplay.getName(),
                                                     MicroscopeNodeType.StackDisplay2D,
                                                     new Window()
                                                     {
                                                       @Override
                                                       public int getWidth()
                                                       {
                                                         return lStack2DDisplay.getGLWindow()
                                                                               .getWindowWidth();
                                                       }

                                                       @Override
                                                       public int getHeight()
                                                       {
                                                         return lStack2DDisplay.getGLWindow()
                                                                               .getWindowHeight();
                                                       }

                                                       @Override
                                                       public void setSize(int width,
                                                                           int height)
                                                       {
                                                         lStack2DDisplay.getGLWindow()
                                                                        .setSize(width,
                                                                                 height);
                                                       }

                                                       @Override
                                                       public int getX()
                                                       {
                                                         return lStack2DDisplay.getGLWindow()
                                                                               .getWindowX();
                                                       }

                                                       @Override
                                                       public int getY()
                                                       {
                                                         return lStack2DDisplay.getGLWindow()
                                                                               .getWindowY();
                                                       }

                                                       @Override
                                                       public void setPosition(int x,
                                                                               int y)
                                                       {
                                                         lStack2DDisplay.getGLWindow()
                                                                        .setWindowPosition(x,
                                                                                           y);
                                                       }

                                                       @Override
                                                       public void show()
                                                       {
                                                         lStack2DDisplay.setVisible(true);
                                                         lStack2DDisplay.requestFocus();
                                                       }

                                                       @Override
                                                       public void hide()
                                                       {
                                                         lStack2DDisplay.setVisible(false);
                                                       }

                                                       @Override
                                                       public void close()
                                                       {
                                                         lStack2DDisplay.close();
                                                       }
                                                     });
      mHalcyonFrame.addNode(node);
    }
  }

  private void setupScriptEngines(MicroscopeInterface<?> pMicroscopeInterface)
  {
    info("Setting up scripting engines");
    // Script Engines:

    for (ScriptingEngine lScriptingEngine : mMicroscopeGUI.getScriptingEnginesList())
    {
      info("Setting up %s", lScriptingEngine);
      MachineConfiguration lCurrentMachineConfiguration =
                                                        MachineConfiguration.get();

      ScriptingWindow lScriptingWindow =
                                       new ScriptingWindow(pMicroscopeInterface.getName()
                                                           + " scripting window",
                                                           lScriptingEngine,
                                                           lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
                                                                                                           60),
                                                           lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
                                                                                                           80));

      lScriptingWindow.loadLastLoadedScriptFile();

      HalcyonNodeInterface node = new HalcyonNode(
                                                  lScriptingEngine.getScriptingLanguageInterface()
                                                                  .getName(),
                                                  MicroscopeNodeType.Scripting,
                                                  lScriptingWindow);
      mHalcyonFrame.addNode(node);
    } /**/
  }

  private <T> void setupDevicePanels(Class<T> pDeviceClass,
                                     Class<?> pGUIElementClass)
  {

    try
    {

      if (pGUIElementClass == null)
      {
        String lDeviceClassSimpleName = pDeviceClass.getSimpleName();
        System.err.println("Could not find panel class for: "
                           + lDeviceClassSimpleName);
        return;
      }

      HalcyonNodeType lNodeType =
                                mDeviceClassToHalcyonTypeMap.get(pDeviceClass);

      for (Object lDevice : mMicroscopeInterface.getDevices(pDeviceClass))
      {

        try
        {
          Constructor<?> lConstructor =
                                      pGUIElementClass.getConstructor(pDeviceClass);

          Object lPanelAsObject = lConstructor.newInstance(lDevice);
          Node lPanelAsNode = (Node) lPanelAsObject;

          HalcyonNode node;
          if (lDevice instanceof NameableInterface)
          {
            NameableInterface lNameableDevice =
                                              (NameableInterface) lDevice;
            node =
                 new HalcyonNode(lNameableDevice.getName() + " Panel",
                                 lNodeType,
                                 lPanelAsNode);
          }
          else
          {
            node = new HalcyonNode(pDeviceClass.getSimpleName(),
                                   lNodeType,
                                   lPanelAsNode);
          }
          mHalcyonFrame.addNode(node);
        }
        catch (NoSuchMethodException | SecurityException
            | InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException e)
        {
          e.printStackTrace();
        }

      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

  private <T> void setupDeviceToolbars(Class<T> pClass,
                                       Class<?> pGUIElementClass)
  {

    try
    {

      if (pGUIElementClass == null)
      {
        String lDeviceClassSimpleName = pClass.getSimpleName();
        System.err.println("Could not find panel class for: "
                           + lDeviceClassSimpleName);
        return;
      }

      for (Object lDevice : mMicroscopeInterface.getDevices(pClass))
      {

        try
        {
          Constructor<?> lConstructor =
                                      pGUIElementClass.getConstructor(pClass);

          Object lToolbarAsObject = lConstructor.newInstance(lDevice);
          Node lToolbarAsNode = (Node) lToolbarAsObject;
          DockNode lDockNode = new DockNode(lToolbarAsNode);

          if (lDevice instanceof NameableInterface)
          {
            NameableInterface lNameableInterface =
                                                 (NameableInterface) lDevice;
            lDockNode.setTitle(lNameableInterface.getName());
          }

          mHalcyonFrame.addToolbar(lDockNode);
        }
        catch (NoSuchMethodException | SecurityException
            | InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException e)
        {
          e.printStackTrace();
        }

      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

}
