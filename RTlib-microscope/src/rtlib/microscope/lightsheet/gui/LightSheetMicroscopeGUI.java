package rtlib.microscope.lightsheet.gui;

import java.util.ArrayList;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.scripting.engine.ScriptingEngine;
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

	public LightSheetMicroscopeGUI(LightSheetMicroscope pLightSheetMicroscope)
	{
		super(pLightSheetMicroscope.getName() + "GUI");
		mLightSheetMicroscope = pLightSheetMicroscope;

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

			final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = new Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess>("Video 3D - " + lStackCameraDevice.getName(),
																																																																							new UnsignedShortType(),
																																																																							cDefaultWindowWidth,
																																																																							cDefaultWindowHeight,
																																																																							1,
																																																																							10);
			mStack3DVideoDeviceList.add(lStack3DDisplay);

			final GroovyScripting lGroovyScripting = new GroovyScripting();

			final ScriptingEngine lScriptingEngine = new ScriptingEngine(	lGroovyScripting,
																																		null);

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

			final Stack3DDisplay<UnsignedShortType, ShortOffHeapAccess> lStack3DDisplay = mStack3DVideoDeviceList.get(i);

			lStack2DDisplay.setOutputStackVariable(lStack3DDisplay.getStackInputVariable());
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

			lStack2DDisplay.setOutputStackVariable(null);
		}
	}

}
