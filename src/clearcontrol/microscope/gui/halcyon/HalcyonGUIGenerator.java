package clearcontrol.microscope.gui.halcyon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingInterface;
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
import halcyon.model.node.Window;
import halcyon.view.TreePanel;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;

public class HalcyonGUIGenerator implements LoggingInterface
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
			info("Setting up Halcyon frame for device class: " + lClass.getSimpleName());
			setupDevicePanels(lClass);
		}

		// setting up script engines:
		setupScriptEngines(mMicroscopeInterface);

		// setting up 2D and 3D displays:
		setup2DDisplays();
		setup3DDisplays();


	}

	private void initJavaFX()
	{
		final CountDownLatch latch = new CountDownLatch(1);

		new JFXPanel(); // initializes JavaFX environment
		latch.countDown();

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
		info("Setting up 3D displays");

		for (Stack3DDisplay lStack3DDisplay : mMicroscopeGUI.get3DStackDisplayList())
		{
			info("Setting up %s", lStack3DDisplay);
			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack3DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay3D,
																												new Window()
																												{
																													@Override public int getWidth()
																													{
																														return lStack3DDisplay.getGLWindow().getWidth();
																													}

																													@Override public int getHeight()
																													{
																														return lStack3DDisplay.getGLWindow().getHeight();
																													}

																													@Override public void setSize( int width, int height )
																													{
																														lStack3DDisplay.getGLWindow().setSize( width, height );
																													}

																													@Override public int getX()
																													{
																														return lStack3DDisplay.getGLWindow().getWindowX();
																													}

																													@Override public int getY()
																													{
																														return lStack3DDisplay.getGLWindow().getWindowY();
																													}

																													@Override public void setPosition( int x, int y )
																													{
																														lStack3DDisplay.getGLWindow().setWindowPosition( x, y );
																													}

																													@Override public void show()
																													{
																														lStack3DDisplay.setVisible(true);
																														lStack3DDisplay.requestFocus();
																													}

																													@Override public void hide()
																													{
																														lStack3DDisplay.setVisible(false);
																													}

																													@Override public void close()
																													{
																														lStack3DDisplay.close();
																													}
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setup2DDisplays()
	{
		info("Setting up 2D displays");

		for (Stack2DDisplay lStack2DDisplay : mMicroscopeGUI.get2DStackDisplayList())
		{
			info("Setting up %s", lStack2DDisplay);

			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack2DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay2D,
																												new Window()
																												{
																													@Override public int getWidth()
																													{
																														return lStack2DDisplay.getGLWindow().getWidth();
																													}

																													@Override public int getHeight()
																													{
																														return lStack2DDisplay.getGLWindow().getHeight();
																													}

																													@Override public void setSize( int width, int height )
																													{
																														lStack2DDisplay.getGLWindow().setSize( width, height );
																													}

																													@Override public int getX()
																													{
																														return lStack2DDisplay.getGLWindow().getWindowX();
																													}

																													@Override public int getY()
																													{
																														return lStack2DDisplay.getGLWindow().getWindowY();
																													}

																													@Override public void setPosition( int x, int y )
																													{
																														lStack2DDisplay.getGLWindow().setWindowPosition( x, y );
																													}

																													@Override public void show()
																													{
																														lStack2DDisplay.setVisible(true);
																														lStack2DDisplay.requestFocus();
																													}

																													@Override public void hide()
																													{
																														lStack2DDisplay.setVisible(false);
																													}

																													@Override public void close()
																													{
																														lStack2DDisplay.close();
																													}
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupScriptEngines(MicroscopeInterface pMicroscopeInterface)
	{
		info("Setting up scripting engines");
		// Script Engines:

		for (ScriptingEngine lScriptingEngine : mMicroscopeGUI.getScriptingEnginesList())
		{
			info("Setting up %s", lScriptingEngine);
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
