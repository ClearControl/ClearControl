package clearcontrol.hardware.cameras.devices.andorzyla.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.hardware.cameras.devices.andorzyla.AndorZylaStackCamera;
import clearcontrol.hardware.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.types.NativeTypeEnum;

import andorsdkj.*;
import andorsdkj.enums.TriggerMode;

public class AndorZylaCameraDemo {

	@Test
	public void testAndorZylaStackCameraOpenAndClose() throws Exception {
		// initializing andor environment
		AndorSdkJ lAndorEnv = new AndorSdkJ();
		lAndorEnv.open();
		AndorZylaStackCamera lZylaOne = new AndorZylaStackCamera(lAndorEnv, 0, TriggerMode.SOFTWARE);
		lZylaOne.stop();
		lAndorEnv.close();

	}

	@Test
	public void testAndorZyla3DDisplay() throws Exception {
		// initializing andor environment
		try

		{
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorZylaStackCamera lZylaOne = new AndorZylaStackCamera(lAndorEnv, 0, TriggerMode.SOFTWARE);
			lZylaOne.setExposure(30000);
			lZylaOne.setExposure(30000);
			lZylaOne.setExposure(30000);
			lZylaOne.setExposure(30000);
			lZylaOne.setExposure(30000);
			lZylaOne.getStackDepthVariable().set(10L);
			lZylaOne.getStackWidthVariable().set(2000L);
			lZylaOne.getStackHeightVariable().set(2000L);
			lZylaOne.setDebugMessagesOn(false);

			final Stack3DDisplay lVideoFrame3DDisplay = new Stack3DDisplay("Test");
			StackInterface lStackHolder = lVideoFrame3DDisplay.getStackInputVariable().get();
		
			final Variable<StackInterface> lInputVariable = lVideoFrame3DDisplay.getStackInputVariable();
		
			lVideoFrame3DDisplay.open();
			lVideoFrame3DDisplay.setVisible(true);
			while (!lVideoFrame3DDisplay.isVisible()) {
				Thread.sleep(10);
			}
			

			for (int i = 0; i < 10; i++) {
				lZylaOne.addCurrentStateToQueue();
			}

			int i = 0;

			while (lVideoFrame3DDisplay.isVisible()) {
				i++;
				Future<Boolean> lPlayQueue = lZylaOne.playQueue();
				lPlayQueue.get();
				Thread.sleep(5000);
				lZylaOne.getStackVariable().sendUpdatesTo(lInputVariable);
			}

			Thread.sleep(3000);
			// TODO: start stack acquisition otherwise you see nothing

			// lZylaOne.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
