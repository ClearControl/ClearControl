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

public class AndorZylaCameraDemo
{

	
		@Test
		public void testAndorZylaStackCameraOpenAndClose() throws Exception
		{
			// initializing andor environment
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorZylaStackCamera lZylaOne = new AndorZylaStackCamera(0, TriggerMode.SOFTWARE);
			lZylaOne.stop();
			lAndorEnv.close();
			
		}
		
		@Test
		public void testAndorZyla3DDisplay() throws Exception
		{
			// initializing andor environment
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorZylaStackCamera lZylaOne = new AndorZylaStackCamera(0, TriggerMode.SOFTWARE);
			
			final Stack3DDisplay lVideoFrame3DDisplay = new Stack3DDisplay("Test");
			final Variable<StackInterface> lInputVariable = lVideoFrame3DDisplay.getStackInputVariable();
			lZylaOne.getStackVariable().sendUpdatesTo(lInputVariable);
			lVideoFrame3DDisplay.open();
			
			//TODO: start stack acquisition  otherwise you see nothing
			
			lZylaOne.stop();
			lAndorEnv.close();
			
		}
}
