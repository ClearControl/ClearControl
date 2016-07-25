package clearcontrol.microscope.gui;

import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNodeType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.microscope.CleanupStackVariable;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.halcyon.HalcyonGUIGenerator;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.gui.LSMNodeType;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.lang.ScriptingLanguageInterface;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.jython.JythonScripting;
import clearcontrol.stack.StackInterface;

public class MicroscopeGUI extends VirtualDevice implements
																								AsynchronousExecutorServiceAccess
{

	private static final int cDefaultWindowWidth = 512;
	private static final int cDefaultWindowHeight = 512;

	private final MicroscopeInterface mMicroscope;

	private final ArrayList<ScriptingEngine> mScriptingEngineList = new ArrayList<>();

	private ArrayList<Stack2DDisplay> mStack2DVideoDeviceList = new ArrayList<>();
	private ArrayList<Stack3DDisplay> mStack3DVideoDeviceList = new ArrayList<>();

	private ArrayList<Variable<StackInterface>> mCleanupStackVariableList = new ArrayList<>();
	private final boolean m3dView;
	private HalcyonFrame mHalcyonFrame;

	public MicroscopeGUI(	MicroscopeInterface pLightSheetMicroscope,
												boolean p3DView)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mMicroscope = pLightSheetMicroscope;
		m3dView = p3DView;

		initializeConcurentExecutor();
	}

	public void addScripting(	String pMicroscopeObjectName,
														ScriptingLanguageInterface pScriptingLanguageInterface)
	{
		final ScriptingEngine lScriptingEngine = new ScriptingEngine(	pScriptingLanguageInterface,
																																	null);
		lScriptingEngine.set(pMicroscopeObjectName, mMicroscope);
		mScriptingEngineList.add(lScriptingEngine);
	}

	public void addGroovyScripting(String pMicroscopeObjectName)
	{
		GroovyScripting lGroovyScripting = new GroovyScripting();
		addScripting(pMicroscopeObjectName, lGroovyScripting);
	}

	public void addJythonScripting(String pMicroscopeObjectName)
	{
		JythonScripting lJythonScripting = new JythonScripting();
		addScripting(pMicroscopeObjectName, lJythonScripting);
	}

	public void generate()
	{
		setup2Dand3DDisplays();
		setupHalcyonWindow(mMicroscope);
	}

	public MicroscopeInterface getMicroscope()
	{
		return mMicroscope;
	}

	public ArrayList<ScriptingEngine> getScriptingEnginesList()
	{
		return mScriptingEngineList;
	}

	public ArrayList<Stack2DDisplay> get2DStackDisplayList()
	{
		return mStack2DVideoDeviceList;
	}

	public ArrayList<Stack3DDisplay> get3DStackDisplayList()
	{
		return mStack3DVideoDeviceList;
	}

	public HalcyonFrame getHalcyonFrame()
	{
		return mHalcyonFrame;
	}

	@SuppressWarnings("unchecked")
	public void setup2Dand3DDisplays()
	{
		final int lNumberOfCameras = mMicroscope.getDeviceLists()
																						.getNumberOfDevices(StackCameraDeviceInterface.class);

		ArrayList<StackCameraDeviceInterface> lDevices = mMicroscope.getDevices(StackCameraDeviceInterface.class);
		System.out.println(lDevices);

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			CleanupStackVariable lCleanupStackVariable = new CleanupStackVariable("CleanupStackVariable",
																																						null,
																																						2);

			mCleanupStackVariableList.add(lCleanupStackVariable);

			final StackCameraDeviceInterface lStackCameraDevice = mMicroscope.getDevice(StackCameraDeviceInterface.class,
																																									i);

			final Stack2DDisplay lStack2DDisplay = new Stack2DDisplay("Video 2D - " + lStackCameraDevice.getName(),
																																cDefaultWindowWidth,
																																cDefaultWindowHeight);
			lStack2DDisplay.setVisible(false);
			mStack2DVideoDeviceList.add(lStack2DDisplay);

		}

		if (m3dView)
		{
			final Stack3DDisplay lStack3DDisplay = new Stack3DDisplay("Video 3D",
																																cDefaultWindowWidth,
																																cDefaultWindowHeight,
																																lNumberOfCameras,
																																10);
			lStack3DDisplay.setVisible(false);
			mStack3DVideoDeviceList.add(lStack3DDisplay);
		}

	}

	/**
	 * Setup Halcyon window (automatically) for a given Microscope.
	 * @param pMicroscopeInterface
	 */
	private void setupHalcyonWindow(MicroscopeInterface pMicroscopeInterface)
	{
		ArrayList<HalcyonNodeType> lNodeTypeList = new ArrayList<>();
		for (HalcyonNodeType lNode : MicroscopeNodeType.values())
			lNodeTypeList.add(lNode);
		for (HalcyonNodeType lNode : LSMNodeType.values())
			lNodeTypeList.add(lNode);

		HalcyonGUIGenerator lHalcyonGUIGenerator = new HalcyonGUIGenerator(	pMicroscopeInterface,
																																				this,
																																				lNodeTypeList);
		lHalcyonGUIGenerator.setupDeviceGUIs();

		mHalcyonFrame = lHalcyonGUIGenerator.getHalcyonFrame();
	}

	/* (non-Javadoc)
	 * @see clearcontrol.device.openclose.OpenCloseDeviceAdapter#open()
	 */
	@Override
	public boolean open()
	{

		executeAsynchronously(() -> {
			for (final Stack2DDisplay lStack2dDisplay : mStack2DVideoDeviceList)
			{
				lStack2dDisplay.open();
			}
		});

		executeAsynchronously(() -> {
			if (m3dView)
				for (final Stack3DDisplay lStack3dDisplay : mStack3DVideoDeviceList)
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

		executeAsynchronously(() -> {
			if (m3dView)
				for (final Stack3DDisplay mStack3DVideoDevice : mStack3DVideoDeviceList)
				{
					mStack3DVideoDevice.close();
				}
		});

		executeAsynchronously(() -> {
			for (final Stack2DDisplay lStack2dDisplay : mStack2DVideoDeviceList)
			{
				lStack2dDisplay.close();
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
	 * @param pStackCameraIndex
	 * @param p2DStackDisplayIndex
	 */
	public void connectCameraTo2D(int pStackCameraIndex,
																int p2DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);

		mMicroscope.getStackVariable(pStackCameraIndex)
								.sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

		lStack2dDisplay.setOutputStackVariable(mCleanupStackVariableList.get(pStackCameraIndex));

	}

	/**
	 * Disconnects variable of given index.
	 * @param pStackCameraIndex camera index.
	 */
	public void disconnectCamera(int pStackCameraIndex)
	{
		Variable<StackInterface> lStackVariable = mMicroscope.getStackVariable(pStackCameraIndex);

		for (Stack2DDisplay lStack2DDisplay : mStack2DVideoDeviceList)
			lStackVariable.doNotSendUpdatesTo(lStack2DDisplay.getInputStackVariable());

	}

	/**
	 * Connects 2D and 3D display variables.
	 * @param p2DStackDisplayIndex
	 * @param p3DStackDisplayIndex
	 */
	public void connect2DTo3D(int p2DStackDisplayIndex,
														int p3DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);
		Stack3DDisplay lStack3dDisplay = mStack3DVideoDeviceList.get(p3DStackDisplayIndex);

		lStack2dDisplay.setOutputStackVariable(lStack3dDisplay.getStackInputVariable());
	}

	/**
	 * Disconnects 2D to 3D display variables.
	 * 
	 * @param p2DStackDisplayIndex index of 2D display 
	 * @param p3DStackDisplayIndex index of 3D display
	 */
	public void disconnect2DTo3D(	int p2DStackDisplayIndex,
																int p3DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);
		Stack3DDisplay lStack3dDisplay = mStack3DVideoDeviceList.get(p3DStackDisplayIndex);

		lStack2dDisplay.setOutputStackVariable(null);
	}

	/**
	 * Connects GUI to microscope variables
	 */
	public void connectGUI()
	{

		final int lNumberOfCameras = mMicroscope.getNumberOfDevices(StackCameraDeviceInterface.class);

		for (int lCameraIndex = 0; lCameraIndex < lNumberOfCameras; lCameraIndex++)
		{
			connectCameraTo2D(lCameraIndex, lCameraIndex);

			/*if (m3dView)
			{
				connect2DTo3D(lCameraIndex, 0);
				mStack3DVideoDeviceList.get(0)
																.setOutputStackVariable(mCleanupStackVariable);
			}
			else
				mStack2DVideoDeviceList.get(lCameraIndex)
																.setOutputStackVariable(mCleanupStackVariable);/**/
		}
	}

	/**
	 * Disconnects GUI from microscope variables
	 */
	public void disconnectGUI()
	{
		final int lNumberOfCameras = mMicroscope.getNumberOfDevices(StackCameraDeviceInterface.class);

		for (int lCameraIndex = 0; lCameraIndex < lNumberOfCameras; lCameraIndex++)
		{

			disconnectCamera(lCameraIndex);

			if (m3dView)
			{
				disconnect2DTo3D(lCameraIndex, 0);
			}
			else
				mStack2DVideoDeviceList.get(lCameraIndex)
																.setOutputStackVariable(null);

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
	 * Waits until the GUI window(s) are closed.
	 */
	public void waitForClose()
	{
		while (isVisible())
		{
			ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
		}
	}

}
