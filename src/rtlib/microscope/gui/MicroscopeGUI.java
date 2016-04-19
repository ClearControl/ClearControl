package rtlib.microscope.gui;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.variable.Variable;
import rtlib.device.name.NamedVirtualDevice;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.microscope.MicroscopeInterface;
import rtlib.microscope.gui.halcyon.HalcyonGUIGenerator;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.lang.ScriptingLanguageInterface;
import rtlib.scripting.lang.groovy.GroovyScripting;
import rtlib.scripting.lang.jython.JythonScripting;
import rtlib.stack.StackInterface;

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
	private HalcyonGUIGenerator mHalcyonMicroscopeGUI;

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
		setupHalcyonWindow(mMicroscope);
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

	@SuppressWarnings("unchecked")
	public void setup2Dand3DDisplays()
	{
		final int lNumberOfCameras = mMicroscope.getDeviceLists()
																						.getNumberOfStackCameraDevices();

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
																																				.getStackCameraDevice(i);

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
		mHalcyonMicroscopeGUI = new HalcyonGUIGenerator(pMicroscopeInterface,
																										this);
	}

	@Override
	public boolean open()
	{
		try
		{
			mHalcyonMicroscopeGUI.externalStart();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

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
				mHalcyonMicroscopeGUI.externalStop();
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

		mMicroscope.getDeviceLists()
								.getStackVariable(pStackCameraIndex)
								.sendUpdatesTo(lStack2dDisplay.getInputStackVariable());

		lStack2dDisplay.setOutputStackVariable(mCleanupStackVariable[pStackCameraIndex]);

	}

	public void disconnectCamera(int pStackCameraIndex)
	{
		Variable<StackInterface> lStackVariable = mMicroscope.getDeviceLists()
																													.getStackVariable(pStackCameraIndex);

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
																						.getNumberOfStackCameraDevices();

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
																						.getNumberOfStackCameraDevices();

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
		return mHalcyonMicroscopeGUI.isVisible();
	}

}
