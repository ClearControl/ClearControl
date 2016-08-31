//package clearcontrol.hardware.cameras.devices.andorzyla.demo;
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.atomic.AtomicLong;
//
//import org.junit.Test;
//
//import clearcontrol.core.variable.Variable;
//import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
//import clearcontrol.hardware.cameras.devices.andorzyla.AndorZylaStackCamera;
//import clearcontrol.hardware.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
//import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
//import clearcontrol.stack.StackInterface;
//import clearcontrol.stack.StackRequest;
//import coremem.recycling.BasicRecycler;
//import coremem.types.NativeTypeEnum;
//
//import andorsdkj.*;
//
//public class AndorZylaCameraDemo
//{
//
//
//		@Test
//		public void testAndorZylaStackCameraOpenAndClose() throws Exception
//		{
//			// initializing andor environment
//			AndorSdkJ lAndorEnv = new AndorSdkJ();
//			AndorZylaStackCamera lZylaOne = new AndorZylaStackCamera(0);
//			lZylaOne.stop();
//			lAndorEnv.close();
//
//		}
// }
