package clearcontrol.microscope.gui.halcyon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.device.name.NameableInterface;
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
import halcyon.view.TreePanel;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;

public class HalcyonGUIGenerator
{
	private MicroscopeInterface mMicroscopeInterface;
	private HalcyonFrame mHalcyonFrame;
	private MicroscopeGUI mMicroscopeGUI;
	private HashMap<Class<?>, Class<?>> mDeviceClassToPanelMap = new HashMap<>();
	private HashMap<Class<?>, HalcyonNodeType> mDeviceClassToHalcyonTypeMap = new HashMap<>();

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

		mHalcyonFrame = new HalcyonFrame(pMicroscopeInterface.getName());

		mHalcyonFrame.setTreePanel(lTreePanel);

	}

	public <U, V> void addMappingEntry(	Class<U> pDeviceClass,
																			Class<V> pPanelClass,
																			HalcyonNodeType pNodeType)
	{
		mDeviceClassToPanelMap.put(pDeviceClass, pPanelClass);
		mDeviceClassToHalcyonTypeMap.put(pDeviceClass, pNodeType);
	}

	public HalcyonFrame getHalcyonFrame()
	{
		return mHalcyonFrame;
	}

	public void setupDeviceGUIs()
	{

		// Setting up devices:

		for (Class<?> lClass : mDeviceClassToPanelMap.keySet())
		{
			System.out.println("setting up Halcyon frame for device class: " + lClass.getSimpleName());
			setupDevicePanels(lClass);
		}

		/*setupDevicePanels(LaserDeviceInterface.class);
		setupDevicePanels(OpticalSwitchDeviceInterface.class);
		setupDevicePanels(FilterWheelDeviceInterface.class);
		setupDevicePanels(StageDeviceInterface.class);
		setupDevicePanels(StackCameraDeviceInterface.class);
		setupDevicePanels(SignalGeneratorInterface.class);
		setupDevicePanels(ScalingAmplifierDeviceInterface.class);
		setupDevicePanels(StackRecyclerManager.class);
		setupDevicePanels(LoggingManager.class);/**/

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
																													lStack3DDisplay.getVisibleVariable().set(true);
																													lStack3DDisplay.requestFocus();
																												},
																												() -> {
																													lStack3DDisplay.getVisibleVariable().set(false);
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

	private <T> void setupDevicePanels(Class<T> pClass)
	{

		try
		{

			Class<?> lPanelClass = mDeviceClassToPanelMap.get(pClass);

			if (lPanelClass == null)
			{
				String lDeviceClassSimpleName = pClass.getSimpleName();
				System.err.println("Could not find panel class for: " + lDeviceClassSimpleName);
				return;
			}

			HalcyonNodeType lNodeType = mDeviceClassToHalcyonTypeMap.get(pClass);

			for (Object lDevice : mMicroscopeInterface.getDevices(pClass))
			{

				try
				{
					Constructor<?> lConstructor = lPanelClass.getConstructor(pClass);

					Object lPanelAsObject = lConstructor.newInstance(lDevice);
					Node lPanelAsNode = (Node) lPanelAsObject;

					HalcyonNode node;
					if (lDevice instanceof NameableInterface)
					{
						NameableInterface lNameableDevice = (NameableInterface) lDevice;
						node = new HalcyonNode(	lNameableDevice.getName(),
																		lNodeType,
																		lPanelAsNode);
					}
					else
					{
						node = new HalcyonNode(	pClass.getSimpleName(),
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

	public boolean isVisible()
	{
		return mHalcyonFrame.isVisible();
	}

}
