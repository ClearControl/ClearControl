package clearcontrol.microscope.gui;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NamedVirtualDevice;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.microscope.MicroscopeBase;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.halcyon.HalcyonGUIGenerator;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.gui.LSMNodeType;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.lang.ScriptingLanguageInterface;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.jython.JythonScripting;
import clearcontrol.stack.StackInterface;
import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNodeType;
import javafx.application.Platform;

public class MicroscopeGUI extends NamedVirtualDevice	implements
																											AsynchronousExecutorServiceAccess
{

	private static final int cDefaultWindowWidth = 512;
	private static final int cDefaultWindowHeight = 512;

	private final MicroscopeInterface mMicroscope;

	private final ArrayList<ScriptingEngine> mScriptingEngineList = new ArrayList<>();

	private ArrayList<Stack2DDisplay> mStack2DVideoDeviceList = new ArrayList<>();
	private ArrayList<Stack3DDisplay> mStack3DVideoDeviceList = new ArrayList<>();

	private Variable<StackInterface>[] mCleanupStackVariable;
	private final boolean m3dView;
	private HalcyonFrame mHalcyonFrame;

	public MicroscopeGUI(	MicroscopeInterface pLightSheetMicroscope,
												boolean p3DView)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mMicroscope = pLightSheetMicroscope;
		m3dView = p3DView;
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
		setupHalcyonWindow( mMicroscope );
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

		mCleanupStackVariable = new Variable[lNumberOfCameras];

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			mCleanupStackVariable[i] = new Variable<StackInterface>("CleanupStackVariable",
																															null)
			{
				ConcurrentLinkedQueue<StackInterface> mKeepStacksAliveQueue = new ConcurrentLinkedQueue<>();

				@Override
				public StackInterface setEventHook(	StackInterface pOldValue,
																						StackInterface pNewValue)
				{
					if (pOldValue != null && !pOldValue.isReleased())
						mKeepStacksAliveQueue.add(pOldValue);

					if (mKeepStacksAliveQueue.size() > lNumberOfCameras)
					{
						StackInterface lStackToRelease = mKeepStacksAliveQueue.remove();
						lStackToRelease.release();
					}
					return pNewValue;
				}
			};

			final StackCameraDeviceInterface lStackCameraDevice = mMicroscope.getDeviceLists()
																																				.getDevice(	StackCameraDeviceInterface.class,
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

	private void setupHalcyonWindow(MicroscopeInterface pMicroscopeInterface)
	{
		ArrayList<HalcyonNodeType> lNodeTypeList = new ArrayList<>();
		for(HalcyonNodeType lNode : MicroscopeNodeType.values())
		lNodeTypeList.add(lNode);
		for(HalcyonNodeType lNode : LSMNodeType.values())
			lNodeTypeList.add(lNode);
		
		
		HalcyonGUIGenerator lHalcyonGUIGenerator = new HalcyonGUIGenerator(	pMicroscopeInterface,
																										this,
																										lNodeTypeList);
		lHalcyonGUIGenerator.setupDeviceGUIs();

		mHalcyonFrame = lHalcyonGUIGenerator.getHalcyonFrame();
	}

	@Override
	public boolean open()
	{
		executeAsynchronously(() -> {
			try
			{
				mHalcyonFrame.externalStart();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});

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

		return super.open();
	}

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

	public void connectCameraTo2D(int pStackCameraIndex,
																int p2DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);

		mMicroscope.getStackVariable(pStackCameraIndex)
								.sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

		lStack2dDisplay.setOutputStackVariable(mCleanupStackVariable[pStackCameraIndex]);

	}

	public void disconnectCamera(int pStackCameraIndex)
	{
		Variable<StackInterface> lStackVariable = mMicroscope.getStackVariable(pStackCameraIndex);

		for (Stack2DDisplay lStack2DDisplay : mStack2DVideoDeviceList)
			lStackVariable.doNotSendUpdatesTo(lStack2DDisplay.getInputStackVariable());

	}

	public void connect2DTo3D(int p2DStackDisplayIndex,
														int p3DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);

		lStack2dDisplay.setOutputStackVariable(null);
	}

	public void disconnect2DTo3D(	int p2DStackDisplayIndex,
																int p3DStackDisplayIndex)
	{
		Stack2DDisplay lStack2dDisplay = mStack2DVideoDeviceList.get(p2DStackDisplayIndex);
		Stack3DDisplay lStack3dDisplay = mStack3DVideoDeviceList.get(p3DStackDisplayIndex);

		lStack2dDisplay.setOutputStackVariable(null);
		lStack3dDisplay.setOutputStackVariable(null);
	}

	public void connectGUI()
	{

		final int lNumberOfCameras = mMicroscope.getDeviceLists()
																						.getNumberOfDevices(StackCameraDeviceInterface.class);

		for (int lCameraIndex = 0; lCameraIndex < lNumberOfCameras; lCameraIndex++)
		{
			connectCameraTo2D(lCameraIndex, lCameraIndex);

			if (m3dView)
			{
				connect2DTo3D(lCameraIndex, lCameraIndex);
				mStack3DVideoDeviceList.get(lCameraIndex)
																.setOutputStackVariable(mCleanupStackVariable[lCameraIndex]);
			}
			else
				mStack2DVideoDeviceList.get(lCameraIndex)
																.setOutputStackVariable(mCleanupStackVariable[lCameraIndex]);
		}
	}

	public void disconnectGUI()
	{
		final int lNumberOfCameras = mMicroscope.getDeviceLists()
																						.getNumberOfDevices(StackCameraDeviceInterface.class);

		for (int lCameraIndex = 0; lCameraIndex < lNumberOfCameras; lCameraIndex++)
		{

			disconnectCamera(lCameraIndex);

			if (m3dView)
			{
				disconnect2DTo3D(lCameraIndex, lCameraIndex);
			}
			else
				mStack2DVideoDeviceList.get(lCameraIndex)
																.setOutputStackVariable(null);

		}
	}

	public boolean isVisible()
	{
		return mHalcyonFrame.isVisible();
	}

}
