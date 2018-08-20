package clearcontrol.microscope.gui;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.gui.CameraDevicePanel;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.gui.LaserDevicePanel;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.gui.jfx.FilterWheelDevicePanel;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.devices.optomech.opticalswitch.gui.OpticalSwitchDevicePanel;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.devices.signalamp.gui.ScalingAmplifierPanel;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.gui.StageDevicePanel;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.adaptive.AdaptiveEngine;
import clearcontrol.microscope.adaptive.gui.AdaptiveEnginePanel;
import clearcontrol.microscope.adaptive.gui.AdaptiveEngineToolbar;
import clearcontrol.microscope.gui.halcyon.HalcyonGUIGenerator;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.simulation.SimulationManager;
import clearcontrol.microscope.simulation.gui.SimulationManagerPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.StackRecyclerManagerPanel;
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
                           AsynchronousExecutorFeature,
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
  private Stage mPrimaryStage;
  private HalcyonFrame mHalcyonFrame;

  /**
   * Instanciates a microscope GUI given a microscope, an array of halcyon node
   * types, and two flags that decide whether to set up 2D and 3D displays.
   * 
   * @param pMicroscope
   *          microscope
   * @param pHalcyonNodeTypeArray
   *          halcyon node type array
   * @param pPrimaryStage
   *          JFX primary stage
   * @param p2DDisplay
   *          2D display
   * @param p3DDisplay
   *          3D display
   */
  public MicroscopeGUI(MicroscopeInterface<?> pMicroscope,
                       HalcyonNodeType[] pHalcyonNodeTypeArray,
                       Stage pPrimaryStage,
                       boolean p2DDisplay,
                       boolean p3DDisplay)
  {
    super(pMicroscope.getName() + "GUI");
    mMicroscope = pMicroscope;
    mPrimaryStage = pPrimaryStage;
    m2DDisplay = p2DDisplay;
    m3DDisplay = p3DDisplay;

    ArrayList<HalcyonNodeType> lNodeTypeList = new ArrayList<>();
    for (HalcyonNodeType lNode : MicroscopeNodeType.values())
      lNodeTypeList.add(lNode);
    for (HalcyonNodeType lNode : pHalcyonNodeTypeArray)
      lNodeTypeList.add(lNode);

    mHalcyonGUIGenerator = new HalcyonGUIGenerator(pMicroscope,
                                                   this,
                                                   lNodeTypeList,
                                                   pPrimaryStage);

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
                         StageDevicePanel.class,
                         MicroscopeNodeType.Stage);

    addPanelMappingEntry(StackRecyclerManager.class,
                         StackRecyclerManagerPanel.class,
                         MicroscopeNodeType.Other);

    addPanelMappingEntry(SimulationManager.class,
                         SimulationManagerPanel.class,
                         MicroscopeNodeType.Other);

    addToolbarMappingEntry(AdaptiveEngine.class,
                           AdaptiveEngineToolbar.class);

    addPanelMappingEntry(AdaptiveEngine.class,
                         AdaptiveEnginePanel.class,
                         MicroscopeNodeType.Acquisition);/**/

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
    setup2Dand3DDisplays(2, 1);
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
   * 
   * @param pNumberOf2DDisplays
   *          number of 2D displays
   * @param pNumberOf3DDisplays
   *          number of 3D displays
   */
  public void setup2Dand3DDisplays(int pNumberOf2DDisplays,
                                   int pNumberOf3DDisplays)
  {

    if (m2DDisplay)
    {

      int lNumberOfCameras =
                           getMicroscope().getNumberOfDevices(StackCameraDeviceInterface.class);

      for (int c = 0; c < lNumberOfCameras; c++)
      {
        final Stack2DDisplay lStack2DDisplay =
                                             new Stack2DDisplay(String.format("Video 2D for camera %d",
                                                                              c),
                                                                cDefaultWindowWidth,
                                                                cDefaultWindowHeight,
                                                                c % 2 == 1);
        lStack2DDisplay.setVisible(false);
        mStack2DDisplayList.add(lStack2DDisplay);
        getMicroscope().addDevice(0, lStack2DDisplay);
      }
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
      getMicroscope().addDevice(0, lStack3DDisplay);
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
      mHalcyonFrame.start(mPrimaryStage);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return super.open();
  }

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
   * Connects Stack cameras to 2D display.
   * 
   * @param p2DDisplayIndex
   *          2D display index
   */
  public void connectPipelineTo2D(int p2DDisplayIndex)
  {
    Stack2DDisplay lStack2dDisplay =
                                   mStack2DDisplayList.get(p2DDisplayIndex);

    mMicroscope.getPipelineStackVariable()
               .sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

    lStack2dDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));
  }

  /**
   * Connects Stack cameras to 3D display.
   *
   * Deprecated: Stacks to view should be sent to the viewer explicitly via
   * schedulers see ViewFusedStackScheduler in clearcontrol-lightsheet
   * repository
   * 
   * @param p3DDisplayIndex
   *          3D display index
   */
  @Deprecated
  public void connectPipelineTo3D(int p3DDisplayIndex)
  {
    Stack3DDisplay lStack3DDisplay =
                                   mStack3DDisplayList.get(p3DDisplayIndex);

    // mMicroscope.getPipelineStackVariable()
    // .sendUpdatesTo(lStack3DDisplay.getInputStackVariable());

    lStack3DDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));
  }

  /**
   * Connects Stack cameras of given index to 2D display of given idex.
   * 
   */
  public void connectCamerasTo2D()
  {

    int lNumberOfCameras =
                         getMicroscope().getNumberOfDevices(StackCameraDeviceInterface.class);

    for (int c = 0; c < lNumberOfCameras; c++)
    {
      Stack2DDisplay lStack2dDisplay = mStack2DDisplayList.get(c);

      mMicroscope.getCameraStackVariable(c)
                 .sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

      lStack2dDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));
    }
  }

  /**
   * Disconnects variable of given index.
   * 
   * @param pCameraIndex
   *          camera index
   * @param p2DDisplayIndex
   *          2D display index
   * 
   */
  public void disconnectCamera(int pCameraIndex, int p2DDisplayIndex)
  {
    Stack2DDisplay lStack2dDisplay =
                                   mStack2DDisplayList.get(p2DDisplayIndex);
    mMicroscope.getCameraStackVariable(pCameraIndex)
               .doNotSendUpdatesTo(lStack2dDisplay.getInputStackVariable());

  }

  /**
   * Disconnects all cameras from all displays
   * 
   */
  public void disconnectAllCameras()
  {
    int lNumberOfCameras =
                         getMicroscope().getNumberOfDevices(StackCameraDeviceInterface.class);

    for (int c = 0; c < lNumberOfCameras; c++)
    {
      for (Stack2DDisplay lStack2DDisplay : mStack2DDisplayList)
        mMicroscope.getPipelineStackVariable()
                   .doNotSendUpdatesTo(lStack2DDisplay.getInputStackVariable());

      for (Stack3DDisplay lStack3DDisplay : mStack3DDisplayList)
        mMicroscope.getPipelineStackVariable()
                   .doNotSendUpdatesTo(lStack3DDisplay.getInputStackVariable());
    }

  }

  /**
   * Connects 2D and 3D display variables.
   * 
   * @param p2DDisplayIndex
   *          2D display index
   * @param p3DDisplayIndex
   *          3D display index
   * 
   */
  public void connect2DTo3D(int p2DDisplayIndex, int p3DDisplayIndex)
  {
    Stack2DDisplay lStack2dDisplay =
                                   mStack2DDisplayList.get(p2DDisplayIndex);
    Stack3DDisplay lStack3dDisplay =
                                   mStack3DDisplayList.get(p3DDisplayIndex);

    lStack2dDisplay.setOutputStackVariable(lStack3dDisplay.getInputStackVariable());

    lStack3dDisplay.setOutputStackVariable(new Variable<StackInterface>("Null"));
  }

  /**
   * Disconnects 2D output variables
   */
  public void disconnectAll2DOutputs()
  {
    for (Stack2DDisplay lStack2DDisplay : mStack2DDisplayList)
    {
      lStack2DDisplay.setOutputStackVariable(null);
    }
  }

  /**
   * Disconnects 3D output variables
   */
  public void disconnectAll3DOutputs()
  {
    for (Stack3DDisplay lStack3DDisplay : mStack3DDisplayList)
    {
      lStack3DDisplay.setOutputStackVariable(null);
    }
  }

  /**
   * Connects GUI to microscope variables
   */
  public void connectGUI()
  {

    if (m2DDisplay)
    {
      connectCamerasTo2D();

      if (m3DDisplay)
        connectPipelineTo3D(0);
    }

  }

  /**
   * Disconnects GUI from microscope variables
   */
  public void disconnectGUI()
  {
    disconnectAllCameras();
    disconnectAll2DOutputs();
    disconnectAll3DOutputs();
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
      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
      return lMicroscopeGUI.isVisible() == pVisible;
    });
  }
}
