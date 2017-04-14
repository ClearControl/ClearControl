package clearcontrol.microscope.gui;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.gui.jfx.CameraDevicePanel;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.gui.jfx.LaserDevicePanel;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.gui.jfx.FilterWheelDevicePanel;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.devices.optomech.opticalswitch.gui.jfx.OpticalSwitchDevicePanel;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.devices.signalamp.gui.jfx.ScalingAmplifierPanel;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.gui.jfx.XYZRStageDevicePanel;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.halcyon.HalcyonGUIGenerator;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.sim.SimulationManager;
import clearcontrol.microscope.sim.gui.SimulationManagerPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.jfx.StackRecyclerManagerPanel;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.lang.ScriptingLanguageInterface;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.jython.JythonScripting;
import clearcontrol.stack.StackInterface;
import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNodeType;

import org.dockfx.DockNode;

/**
 * Microscope GUI.
 *
 * @author royer
 */
public class MicroscopeGUI extends VirtualDevice implements
                           AsynchronousExecutorServiceAccess,
                           WaitingInterface
{

  private static final int cDefaultWindowWidth = 512;
  private static final int cDefaultWindowHeight = 512;

  private final MicroscopeInterface<?> mMicroscope;

  private final ArrayList<ScriptingEngine> mScriptingEngineList =
                                                                new ArrayList<>();

  private ArrayList<Stack2DDisplay> mStack2DDisplayList =
                                                        new ArrayList<>();
  private ArrayList<Stack3DDisplay> mStack3DDisplayList =
                                                        new ArrayList<>();

  private final boolean m2DDisplay, m3DDisplay;
  private HalcyonGUIGenerator mHalcyonGUIGenerator;
  private HalcyonFrame mHalcyonFrame;

  /**
   * Instanciates a microscope GUI given a microscope, an array of halcyon node
   * types, and two flags that decide whether to set up 2D and 3D displays.
   * 
   * @param pMicroscope
   *          microscope
   * @param pHalcyonNodeTypeArray
   *          halcyon node type array
   * @param p2DDisplay
   *          2D display
   * @param p3DDisplay
   *          3D display
   */
  public MicroscopeGUI(MicroscopeInterface<?> pMicroscope,
                       HalcyonNodeType[] pHalcyonNodeTypeArray,
                       boolean p2DDisplay,
                       boolean p3DDisplay)
  {
    super(pMicroscope.getName() + "GUI");
    mMicroscope = pMicroscope;
    m2DDisplay = p2DDisplay;
    m3DDisplay = p3DDisplay;

    ArrayList<HalcyonNodeType> lNodeTypeList = new ArrayList<>();
    for (HalcyonNodeType lNode : MicroscopeNodeType.values())
      lNodeTypeList.add(lNode);
    for (HalcyonNodeType lNode : pHalcyonNodeTypeArray)
      lNodeTypeList.add(lNode);

    mHalcyonGUIGenerator =
                         new HalcyonGUIGenerator(pMicroscope,
                                                 this,
                                                 lNodeTypeList);

    addPanelMappingEntry(LaserDeviceInterface.class,
                         LaserDevicePanel.class,
                         MicroscopeNodeType.Laser);

    addPanelMappingEntry(StackCameraDeviceInterface.class,
                         CameraDevicePanel.class,
                         MicroscopeNodeType.Camera);/**/

    addPanelMappingEntry(FilterWheelDeviceInterface.class,
                         FilterWheelDevicePanel.class,
                         MicroscopeNodeType.FilterWheel);

    addPanelMappingEntry(OpticalSwitchDeviceInterface.class,
                         OpticalSwitchDevicePanel.class,
                         MicroscopeNodeType.OpticalSwitch);

    addPanelMappingEntry(ScalingAmplifierDeviceInterface.class,
                         ScalingAmplifierPanel.class,
                         MicroscopeNodeType.ScalingAmplifier);

    addPanelMappingEntry(StageDeviceInterface.class,
                         XYZRStageDevicePanel.class,
                         MicroscopeNodeType.Stage);

    addPanelMappingEntry(StackRecyclerManager.class,
                         StackRecyclerManagerPanel.class,
                         MicroscopeNodeType.Other);

    addPanelMappingEntry(SimulationManager.class,
                         SimulationManagerPanel.class,
                         MicroscopeNodeType.Other);

    SimulationManager lSimulationManager =
                                         new SimulationManager(pMicroscope);
    mMicroscope.addDevice(0, lSimulationManager);

    initializeConcurentExecutor();
  }

  /**
   * Adds a toolbar to this GUI
   * 
   * @param pDockNode
   *          toolbar's dockable node.
   */
  public void addToolbar(DockNode pDockNode)
  {
    getHalcyonFrame().addToolbar(pDockNode);
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
    mHalcyonGUIGenerator.addPanelMappingEntry(pDeviceClass,
                                              pPanelClass,
                                              pNodeType);
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
    mHalcyonGUIGenerator.addToolbarMappingEntry(pDeviceClass,
                                                pToolbarClass);
  }

  /**
   * Adds a scripting engine.
   * 
   * @param pMicroscopeObjectName
   *          name of the microscope object within the scripting environment
   * @param pScriptingLanguageInterface
   *          scripting language interface
   */
  public void addScripting(String pMicroscopeObjectName,
                           ScriptingLanguageInterface pScriptingLanguageInterface)
  {
    final ScriptingEngine lScriptingEngine =
                                           new ScriptingEngine(pScriptingLanguageInterface,
                                                               null);
    lScriptingEngine.set(pMicroscopeObjectName, mMicroscope);
    mScriptingEngineList.add(lScriptingEngine);
  }

  /**
   * Adds Groovy scripting.
   * 
   * @param pMicroscopeObjectName
   *          name of the microscope object within the scripting environment
   */
  public void addGroovyScripting(String pMicroscopeObjectName)
  {
    GroovyScripting lGroovyScripting = new GroovyScripting();
    addScripting(pMicroscopeObjectName, lGroovyScripting);
  }

  /**
   * Adds Jython scripting.
   * 
   * @param pMicroscopeObjectName
   *          name of the microscope object within the scripting environment
   */
  public void addJythonScripting(String pMicroscopeObjectName)
  {
    JythonScripting lJythonScripting = new JythonScripting();
    addScripting(pMicroscopeObjectName, lJythonScripting);
  }

  /**
   * Sets up the GUI - i.e. main Halcyon window, toolbars, panels for devices,
   * and the 2D and 3D displays.
   */
  public void setup()
  {
    setup2Dand3DDisplays();
    setupHalcyonWindow();
  }

  /**
   * Returns the microscope for this GUI
   * 
   * @return microscope that this GUI serves
   */
  public MicroscopeInterface<?> getMicroscope()
  {
    return mMicroscope;
  }

  /**
   * Returns the list of scripting Engines
   * 
   * @return scripting engines list
   */
  public ArrayList<ScriptingEngine> getScriptingEnginesList()
  {
    return mScriptingEngineList;
  }

  /**
   * Returns the list of 2D displays.
   * 
   * @return list of 2D displays
   */
  public ArrayList<Stack2DDisplay> get2DDisplayDeviceList()
  {
    return mStack2DDisplayList;
  }

  /**
   * Returns the list of 3D displays
   * 
   * @return list of 3D displays
   */
  public ArrayList<Stack3DDisplay> get3DDisplayDeviceList()
  {
    return mStack3DDisplayList;
  }

  /**
   * Returns Halcyon frame
   * 
   * @return Halcyon frame
   */
  public HalcyonFrame getHalcyonFrame()
  {
    return mHalcyonFrame;
  }

  /**
   * Sets up 2D and 3D displays.
   */
  public void setup2Dand3DDisplays()
  {

    if (m2DDisplay)
    {

      final Stack2DDisplay lStack2DDisplay =
                                           new Stack2DDisplay("Video 2D",
                                                              cDefaultWindowWidth,
                                                              cDefaultWindowHeight);
      lStack2DDisplay.setVisible(false);
      mStack2DDisplayList.add(lStack2DDisplay);
    }

    if (m3DDisplay)
    {
      final Stack3DDisplay lStack3DDisplay =
                                           new Stack3DDisplay("Video 3D",
                                                              cDefaultWindowWidth,
                                                              cDefaultWindowHeight,
                                                              1,
                                                              10);
      lStack3DDisplay.getVisibleVariable().set(false);
      mStack3DDisplayList.add(lStack3DDisplay);
    }

  }

  private void setupHalcyonWindow()
  {
    mHalcyonGUIGenerator.setupDeviceGUIs();

    mHalcyonFrame = mHalcyonGUIGenerator.getHalcyonFrame();
  }

  /* (non-Javadoc)
   * @see clearcontrol.device.openclose.OpenCloseDeviceAdapter#open()
   */
  @Override
  public boolean open()
  {

    if (m2DDisplay)
      executeAsynchronously(() -> {
        for (final Stack2DDisplay lStack2DDisplay : mStack2DDisplayList)
        {
          lStack2DDisplay.open();
        }
      });

    if (m3DDisplay)
      executeAsynchronously(() -> {
        for (final Stack3DDisplay lStack3dDisplay : mStack3DDisplayList)
        {
          lStack3dDisplay.open();
        }
      });

    try
    {
      mHalcyonFrame.externalStart();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return super.open();
  }

  /* (non-Javadoc)
   * @see clearcontrol.device.openclose.OpenCloseDeviceAdapter#close()
   */
  @Override
  public boolean close()
  {
    if (m2DDisplay)
      executeAsynchronously(() -> {
        for (final Stack2DDisplay lStack2DDisplayDevice : mStack2DDisplayList)
        {
          lStack2DDisplayDevice.close();
        }
      });

    if (m3DDisplay)
      executeAsynchronously(() -> {
        for (final Stack3DDisplay mStack3DDisplayDevice : mStack3DDisplayList)
        {
          mStack3DDisplayDevice.close();
        }
      });

    executeAsynchronously(() -> {
      try
      {
        mHalcyonFrame.externalStop();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    });

    return super.close();
  }

  /**
   * Connects Stack camera of given index to 2D display of given idex.
   * 
   */
  public void connectPipelineTo2D()
  {
    Stack2DDisplay lStack2dDisplay = mStack2DDisplayList.get(0);

    mMicroscope.getPipelineStackVariable()
               .sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

    lStack2dDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));

  }

  /**
   * Disconnects variable of given index.
   * 
   */
  public void disconnectCamera()
  {
    Stack2DDisplay lStack2dDisplay = mStack2DDisplayList.get(0);
    mMicroscope.getPipelineStackVariable()
               .doNotSendUpdatesTo(lStack2dDisplay.getInputStackVariable());

  }

  /**
   * Connects 2D and 3D display variables.
   * 
   */
  public void connect2DTo3D()
  {
    Stack2DDisplay lStack2dDisplay = mStack2DDisplayList.get(0);
    Stack3DDisplay lStack3dDisplay = mStack3DDisplayList.get(0);

    lStack2dDisplay.setOutputStackVariable(lStack3dDisplay.getInputStackVariable());

    lStack3dDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));
  }

  /**
   * Disconnects 2D to 3D display variables.
   * 
   * 
   */
  public void disconnect2DTo3D()
  {
    Stack2DDisplay lStack2DDisplay = mStack2DDisplayList.get(0);

    lStack2DDisplay.setOutputStackVariable(null);
  }

  /**
   * Connects GUI to microscope variables
   */
  public void connectGUI()
  {

    if (m2DDisplay)
    {
      connectPipelineTo2D();

      if (m3DDisplay)
        connect2DTo3D();
    }

  }

  /**
   * Disconnects GUI from microscope variables
   */
  public void disconnectGUI()
  {
    if (m2DDisplay)
    {
      disconnectCamera();

      if (m3DDisplay)
      {
        disconnect2DTo3D();
      }
      else
        mStack2DDisplayList.get(0).setOutputStackVariable(null);

    }
  }

  /**
   * Retruns whether the GUI elements are visible.
   * 
   * @return true if GUI elements (windows) are visible
   */
  public boolean isVisible()
  {
    return mHalcyonFrame.isVisible();
  }

  /**
   * Waits until the GUI main window is either visible or not visible.
   * 
   * @param pVisible
   *          main window state to wait for
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time out unit
   * @return whether the main window is visible or not.
   */
  public boolean waitForVisible(boolean pVisible,
                                Long pTimeOut,
                                TimeUnit pTimeUnit)
  {
    MicroscopeGUI lMicroscopeGUI = this;
    return waitFor(pTimeOut, pTimeUnit, () -> {
      ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
      return lMicroscopeGUI.isVisible() == pVisible;
    });
  }
}
