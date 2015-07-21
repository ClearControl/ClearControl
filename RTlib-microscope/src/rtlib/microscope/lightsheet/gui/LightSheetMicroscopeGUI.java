package rtlib.microscope.lightsheet.gui;

import java.util.ArrayList;
import java.util.Map;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.engine.ScriptingEngineListener;
import rtlib.scripting.gui.ScriptingWindow;
import rtlib.scripting.lang.groovy.GroovyScripting;

public class LightSheetMicroscopeGUI extends NamedVirtualDevice
{

	private static final int cDefaultWindowWidth = 512;
	private static final int cDefaultWindowHeight = 512;

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final ArrayList<Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess>> mStack2DVideoDeviceList = new ArrayList<>();
	private final ArrayList<Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess>> mStack3DVideoDeviceList = new ArrayList<>();
	private ScriptingWindow mScriptingWindow;
	private final boolean m3dView;

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mLightSheetMicroscope = pLightSheetMicroscope;
		m3dView = p3DView;

		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		final int lNumberOfCameras = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfStackCameraDevices();

		for (int i = 0; i < lNumberOfCameras; i++)
		{
			final StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> lStackCameraDevice = mLightSheetMicroscope.getDeviceLists()
																																																												.getStackCameraDevice(i);
			final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2DDisplay = new Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 2D - " + lStackCameraDevice.getName(),
																																																																							new UnsignedShortType(),
																																																																							cDefaultWindowWidth,
																																																																							cDefaultWindowHeight);
			mStack2DVideoDeviceList.add(lStack2DDisplay);

			if (m3dView)
			{
				final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = new Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 3D - " + lStackCameraDevice.getName(),
																																																																								new UnsignedShortType(),
																																																																								cDefaultWindowWidth,
																																																																								cDefaultWindowHeight,
																																																																								1,
																																																																								10);
				mStack3DVideoDeviceList.add(lStack3DDisplay);
			}

			final GroovyScripting lGroovyScripting = new GroovyScripting();

			final ScriptingEngine lScriptingEngine = new ScriptingEngine(	lGroovyScripting,
																																		null);
			lScriptingEngine.loadLastExecutedScript();

			lScriptingEngine.addListener(new ScriptingEngineListener()
			{

				@Override
				public void updatedScript(ScriptingEngine pScriptingEngine,
																	String pScript)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeScriptExecution(ScriptingEngine pScriptingEngine,
																					String pScriptString)
				{
					// TODO Auto-generated method stub

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
					// TODO Auto-generated method stub

				}
			});

			lScriptingEngine.set("lsm", pLightSheetMicroscope);

			mScriptingWindow = new ScriptingWindow(	pLightSheetMicroscope.getName() + " scripting window",
																							lScriptingEngine,
																							lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
																																															60),
																							lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
																																															80));
		}

	}

	@Override
	public boolean open()
	{
		for (final Stack2DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack2dDisplay : mStack2DVideoDeviceList)
		{
			lStack2dDisplay.open();
		}

		if (m3dView)
			for (final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3dDisplay : mStack3DVideoDeviceList)
			{
				lStack3dDisplay.open();
			}

		mScriptingWindow.setVisible(true);

		return super.open();
	}

	@Override
	public boolean close()
	{
		if (m3dView)
			for (final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3dDisplay : mStack3DVideoDeviceList)
			{
				lStack3dDisplay.close();
			}

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
				final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = mStack3DVideoDeviceList.get(i);

				lStack2DDisplay.setOutputStackVariable(lStack3DDisplay.getStackInputVariable());
			}
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
				lStack2DDisplay.setOutputStackVariable(null);
		}
	}

}
