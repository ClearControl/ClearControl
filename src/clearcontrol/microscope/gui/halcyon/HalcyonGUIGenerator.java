package clearcontrol.microscope.gui.halcyon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import clearcontrol.hardware.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.hardware.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.hardware.signalgen.SignalGeneratorInterface;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.gui.ScriptingWindow;
import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonNodeType;
import halcyon.model.node.HalcyonOtherNode;
import halcyon.view.TreePanel;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;

public class HalcyonGUIGenerator
{
	private MicroscopeInterface mMicroscopeInterface;
	private HalcyonFrame mHalcyonFrame;
	private MicroscopeGUI mMicroscopeGUI;
	private Properties mProperties = new Properties();

	public HalcyonGUIGenerator(	MicroscopeInterface pMicroscopeInterface,
															MicroscopeGUI pMicroscopeGUI,
															Collection<HalcyonNodeType> pNodeTypeNamesList)
	{
		mMicroscopeInterface = pMicroscopeInterface;
		mMicroscopeGUI = pMicroscopeGUI;
		initJavaFX();

		TreePanel lTreePanel = new TreePanel(	"component tree",
																					"Hardware",
																					this.getClass()
																							.getResourceAsStream("./icons/folder_16.png"),
																					pNodeTypeNamesList);

		mHalcyonFrame = new HalcyonFrame(	pMicroscopeInterface.getName(),
																			1280,
																			768);

		mHalcyonFrame.setTreePanel(lTreePanel);

	}

	public <T> void loadMappingFromRessourceFile(	Class<T> pClass,
																								String pRessourcesPath)
	{
		try
		{
			InputStream lResourceAsStream = pClass.getResourceAsStream(pRessourcesPath);
			mProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public <U,V> void addMappingEntry(Class<U> pDeviceClass, Class<V> pPanelClass)
	{
		String lDeviceSimpleName = pDeviceClass.getSimpleName();
		String lPanelFullName = pPanelClass.getName();

		mProperties.setProperty(lDeviceSimpleName, lPanelFullName);
	}

	public HalcyonFrame getHalcyonFrame()
	{
		return mHalcyonFrame;
	}

	public void setupDeviceGUIs()
	{

		// Setting up devices:
		setupDevicePanels(LaserDeviceInterface.class, MicroscopeNodeType.Laser);
		setupDevicePanels(OpticalSwitchDeviceInterface.class,
					MicroscopeNodeType.OpticalSwitch);
		setupDevicePanels(FilterWheelDeviceInterface.class,
					MicroscopeNodeType.FilterWheel);
		setupDevicePanels(StageDeviceInterface.class, MicroscopeNodeType.Stage);
		setupDevicePanels(StackCameraDeviceInterface.class, MicroscopeNodeType.Camera);
		setupDevicePanels(SignalGeneratorInterface.class,
					MicroscopeNodeType.SignalGenerator);
		setupDevicePanels(ScalingAmplifierDeviceInterface.class,
					MicroscopeNodeType.ScalingAmplifier);
		setupDevicePanels(StackRecyclerManager.class, MicroscopeNodeType.Other);
		setupDevicePanels(AcquisitionStateManager.class, MicroscopeNodeType.Acquisition);
		

		// setting up script engines:
		setupScriptEngines(mMicroscopeInterface);

		// setting up 2D and 3D displays:
		setup2DDisplays();
		setup3DDisplays();

		// Utility interfaces are added
		// lHalcyonFrame.addToolbar( new DemoToolbarWindow(
		// lHalcyonFrame.getViewManager() ) );

	}

	private void initJavaFX()
	{
		final CountDownLatch latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new JFXPanel(); // initializes JavaFX environment
				latch.countDown();
			}
		});
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void setup3DDisplays()
	{
		// 3D Views:

		for (Stack3DDisplay lStack3DDisplay : mMicroscopeGUI.get3DStackDisplayList())
		{
			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack3DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay3D,
																												() -> {
																													lStack3DDisplay.setVisible(true);
																													lStack3DDisplay.requestFocus();
																												},
																												() -> {
																													lStack3DDisplay.setVisible(false);
																												},
																												() -> {
																													lStack3DDisplay.close();
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setup2DDisplays()
	{
		// 2D Views:

		for (Stack2DDisplay lStack2DDisplay : mMicroscopeGUI.get2DStackDisplayList())
		{
			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack2DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay2D,
																												() -> {
																													lStack2DDisplay.setVisible(true);
																													lStack2DDisplay.requestFocus();
																												},
																												() -> {
																													lStack2DDisplay.setVisible(false);
																												},
																												() -> {
																													lStack2DDisplay.close();
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupScriptEngines(MicroscopeInterface pMicroscopeInterface)
	{
		// Script Engines:

		for (ScriptingEngine lScriptingEngine : mMicroscopeGUI.getScriptingEnginesList())
		{

			MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

			ScriptingWindow lScriptingWindow = new ScriptingWindow(	pMicroscopeInterface.getName() + " scripting window",
																															lScriptingEngine,
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
																																																							60),
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
																																																							80));

			lScriptingWindow.loadLastLoadedScriptFile();

			HalcyonNodeInterface node = new HalcyonNode(lScriptingEngine.getScriptingLanguageInterface()
																																	.getName(),
																									MicroscopeNodeType.Scripting,
																									lScriptingWindow);
			mHalcyonFrame.addNode(node);
		}/**/
	}

	
	private <T> void setupDevicePanels(Class<T> pClass, HalcyonNodeType pNodeType)
	{
		try
		{
			String lDeviceClassSimpleName = pClass.getSimpleName();
			String lPanelClassName = mProperties.getProperty(lDeviceClassSimpleName);

			if (lPanelClassName == null)
			{
				System.err.println("Could not find panel class name for: "+lDeviceClassSimpleName);
				return;
			}

			Class<?> lClass = Class.forName(lPanelClassName);

			for (Object lDevice : mMicroscopeInterface.getDevices(pClass))
			{

				Constructor<?> lConstructor = lClass.getConstructor(pClass);
				Object lPanelAsObject = lConstructor.newInstance(lDevice);
				Node lPanelAsNode = (Node) lPanelAsObject;

				HalcyonNode node;
				if (lDevice instanceof NameableInterface)
				{
					NameableInterface lNameableDevice = (NameableInterface) lDevice;
					node = new HalcyonNode(	lNameableDevice.getName(),
																	pNodeType,
																	lPanelAsNode);
				}
				else
				{
					node = new HalcyonNode(	pClass.getSimpleName(),
																	pNodeType,
																	lPanelAsNode);
				}
				mHalcyonFrame.addNode(node);
			}
		}
		catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			e.printStackTrace();
		}

	}

	public boolean isVisible()
	{
		return mHalcyonFrame.isVisible();
	}

}
