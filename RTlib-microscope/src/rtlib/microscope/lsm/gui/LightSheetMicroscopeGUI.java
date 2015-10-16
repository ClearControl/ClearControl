package rtlib.microscope.lsm.gui;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.engine.ScriptingEngineListener;
import rtlib.scripting.gui.ScriptingWindow;
import rtlib.scripting.lang.groovy.GroovyScripting;
import rtlib.stack.StackInterface;

public class LightSheetMicroscopeGUI extends NamedVirtualDevice
{

	private static final int cDefaultWindowWidth = 512;
	private static final int cDefaultWindowHeight = 512;

	private final LightSheetMicroscope mLightSheetMicroscope;

	private ArrayList<Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess>> mStack2DVideoDeviceList = new ArrayList<>();
	private Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> mStack3DVideoDevice;
	private ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>[] mCleanupStackVariable;
	private ScriptingWindow mScriptingWindow;
	private final boolean m3dView;

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mLightSheetMicroscope = pLightSheetMicroscope;
		m3dView = p3DView;

		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		setup2D3DDisplay();

		setupScripting(	pLightSheetMicroscope,
										lCurrentMachineConfiguration);

	}

	@SuppressWarnings("unchecked")
	public void setup2D3DDisplay()
	{
		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		mCleanupStackVariable = new ObjectVariable[lNumberOfCameras];

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			mCleanupStackVariable[i] = new ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>(	"CleanupStackVariable",
																																																						null)
			{
				ConcurrentLinkedQueue<StackInterface<UnsignedShortType, ShortOffHeapAccess>> mKeepStacksAliveQueue = new ConcurrentLinkedQueue<>();

				public StackInterface<UnsignedShortType, ShortOffHeapAccess> setEventHook(StackInterface<UnsignedShortType, ShortOffHeapAccess> pOldValue,
																																									StackInterface<UnsignedShortType, ShortOffHeapAccess> pNewValue)
				{
					if (pOldValue != null && !pOldValue.isReleased())
						mKeepStacksAliveQueue.add(pOldValue);

					if (mKeepStacksAliveQueue.size() > lNumberOfCameras)
					{
						StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackToRelease = mKeepStacksAliveQueue.remove();
						lStackToRelease.release();
					}
					return pNewValue;
				}
			};

			final StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> lStackCameraDevice = mLightSheetMicroscope.getDeviceLists()
																																																												.getStackCameraDevice(i);

			final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2DDisplay = new Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 2D - " + lStackCameraDevice.getName(),
																																																																							new UnsignedShortType(),
																																																																							cDefaultWindowWidth,
																																																																							cDefaultWindowHeight);
			mStack2DVideoDeviceList.add(lStack2DDisplay);

		}

		if (m3dView)
		{
			final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = new Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 3D",
																																																																							new UnsignedShortType(),
																																																																							cDefaultWindowWidth,
																																																																							cDefaultWindowHeight,
																																																																							1, // FIX
																																																																							10);
			mStack3DVideoDevice = lStack3DDisplay;
		}
		else
			mStack3DVideoDevice = null;

	}

	public void setupScripting(	LightSheetMicroscope pLightSheetMicroscope,
															final MachineConfiguration lCurrentMachineConfiguration)
	{
		final GroovyScripting lGroovyScripting = new GroovyScripting();

		final ScriptingEngine lScriptingEngine = new ScriptingEngine(	lGroovyScripting,
																																	null);

		lScriptingEngine.addListener(new ScriptingEngineListener()
		{

			@Override
			public void updatedScript(ScriptingEngine pScriptingEngine,
																String pScript)
			{

			}

			@Override
			public void beforeScriptExecution(ScriptingEngine pScriptingEngine,
																				String pScriptString)
			{

			}

			@Override
			public void asynchronousResult(	ScriptingEngine pScriptingEngine,
																			String pScriptString,
																			Map<String, Object> pBinding,
																			Throwable pThrowable,
																			String pErrorMessage)
			{
				if (pThrowable != null)
					pThrowable.printStackTrace();
			}

			@Override
			public void afterScriptExecution(	ScriptingEngine pScriptingEngine,
																				String pScriptString)
			{

			}

			@Override
			public void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine)
			{

			}
		});

		lScriptingEngine.set("lsm", pLightSheetMicroscope);

		mScriptingWindow = new ScriptingWindow(	pLightSheetMicroscope.getName() + " scripting window",
																						lScriptingEngine,
																						lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
																																														60),
																						lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
																																														80));

		mScriptingWindow.loadLastLoadedScriptFile();
	}

	@Override
	public boolean open()
	{
		for (final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2dDisplay : mStack2DVideoDeviceList)
		{
			lStack2dDisplay.open();
		}

		if (m3dView)
			mStack3DVideoDevice.open();

		mScriptingWindow.setVisible(true);

		return super.open();
	}

	@Override
	public boolean close()
	{
		if (m3dView)
			mStack3DVideoDevice.close();

		for (final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2dDisplay : mStack2DVideoDeviceList)
		{
			lStack2dDisplay.close();
		}

		mScriptingWindow.setVisible(false);

		return super.close();
	}

	public void connectGUI()
	{

		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		for (int i = 0; i < lNumberOfCameras; i++)
		{
			final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2DDisplay = mStack2DVideoDeviceList.get(i);

			mLightSheetMicroscope.getDeviceLists()
														.getStackVariable(i)
														.sendUpdatesTo(lStack2DDisplay.getInputStackVariable());

			if (m3dView)
			{
				lStack2DDisplay.setOutputStackVariable(mStack3DVideoDevice.getStackInputVariable());
				mStack3DVideoDevice.setOutputStackVariable(mCleanupStackVariable[i]);
			}
			else
				lStack2DDisplay.setOutputStackVariable(mCleanupStackVariable[i]);
		}

	}

	public void disconnectGUI()
	{
		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		for (int i = 0; i < lNumberOfCameras; i++)
		{

			final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2DDisplay = mStack2DVideoDeviceList.get(i);

			mLightSheetMicroscope.getDeviceLists()
														.getStackVariable(i)
														.doNotSendUpdatesTo(lStack2DDisplay.getInputStackVariable());
			if (m3dView)
			{
				lStack2DDisplay.setOutputStackVariable(null);
				mStack3DVideoDevice.setOutputStackVariable(null);
			}
			else
				lStack2DDisplay.setOutputStackVariable(null);

		}
	}

	public boolean isVisible()
	{
		return mScriptingWindow != null && mScriptingWindow.isVisible();
	}

}
