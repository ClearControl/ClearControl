package rtlib.cameras.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rtlib.cameras.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.stack.Stack;
import dcamj.DcamAcquisition.TriggerType;

public class OrcaFlash4CameraDemo
{
	AtomicLong mFrameIndex = new AtomicLong(0);

	@Test
	public void testAcquireSingleFrames() throws InterruptedException
	{
		mFrameIndex.set(0);
		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																										TriggerType.Internal);

		lOrcaFlash4StackCamera.getStackReferenceVariable()
													.sendUpdatesTo(new ObjectVariable<Stack<Short>>("Receiver")
													{

														@Override
														public Stack<Short> setEventHook(	final Stack<Short> pOldStack,
																															final Stack<Short> pNewStack)
														{
															/*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
																									+ " index="
																									+ pNewStack.getIndex());/**/
															System.out.println(pNewStack);
															mFrameIndex.incrementAndGet();
															return super.setEventHook(pOldStack,
																												pNewStack);
														}

													});

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.getFrameDepthVariable().setValue(1);
		lOrcaFlash4StackCamera.getStackModeVariable().setValue(false);
		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

		Thread.sleep(1000);

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(2000);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();

		System.out.println(mFrameIndex.get());

		assertTrue(mFrameIndex.get() >= 199);
	}

	@Test
	public void testAcquireStack() throws InterruptedException
	{
		mFrameIndex.set(0);
		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																										TriggerType.Internal);

		lOrcaFlash4StackCamera.getStackReferenceVariable()
													.sendUpdatesTo(new ObjectVariable<Stack<Short>>("Receiver")
													{

														@Override
														public Stack<Short> setEventHook(	final Stack<Short> pOldStack,
																															final Stack<Short> pNewStack)
														{
															/*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
																									+ " index="
																									+ pNewStack.getIndex());/**/
															System.out.println(pNewStack);
															mFrameIndex.incrementAndGet();
															return super.setEventHook(pOldStack,
																												pNewStack);
														}

													});

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
													.setValue(500);
		lOrcaFlash4StackCamera.getFrameWidthVariable().setValue(128);
		lOrcaFlash4StackCamera.getFrameHeightVariable().setValue(128);
		lOrcaFlash4StackCamera.getFrameDepthVariable().setValue(128);
		lOrcaFlash4StackCamera.getStackModeVariable().setValue(true);
		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(128);

		Thread.sleep(1000);

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(2000);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();

		System.out.println(mFrameIndex.get());

		assertTrue(mFrameIndex.get() == 6);
	}

	@Test
	public void testDisplayVideo() throws InterruptedException,
																IOException
	{
		final NDArrayDirect<Short> lNDArrayDirect = NDArrayDirect.allocateTXYZ(	Short.class,
																																						256,
																																						256,
																																						1);

		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											lNDArrayDirect.getSizeAlongDimension(1),
																											lNDArrayDirect.getSizeAlongDimension(2));
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setSourceBuffer(lNDArrayDirect);
		lVideoWindow.setVisible(true);

		mFrameIndex.set(0);
		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																										TriggerType.Internal);

		lOrcaFlash4StackCamera.getStackReferenceVariable()
													.sendUpdatesTo(new ObjectVariable<Stack<Short>>("Receiver")
													{

														@Override
														public Stack<Short> setEventHook(	final Stack<Short> pOldStack,
																															final Stack<Short> pNewStack)
														{
															/*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
																									+ " index="
																									+ pNewStack.getIndex());/**/
															System.out.println("mCounter=" + mFrameIndex.get());
															System.out.println(pNewStack);

															assertTrue(mFrameIndex.get() == pNewStack.getIndex());

															lVideoWindow.setSourceBuffer(pNewStack.getNDArray());
															lVideoWindow.notifyNewFrame();
															lVideoWindow.display();/**/

															mFrameIndex.incrementAndGet();
															return super.setEventHook(pOldStack,
																												pNewStack);
														}

													});

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
													.setValue(500);
		lOrcaFlash4StackCamera.getFrameWidthVariable()
													.setValue(lNDArrayDirect.getSizeAlongDimension(1));
		lOrcaFlash4StackCamera.getFrameHeightVariable()
													.setValue(lNDArrayDirect.getSizeAlongDimension(2));
		lOrcaFlash4StackCamera.getFrameDepthVariable().setValue(1);
		lOrcaFlash4StackCamera.getStackModeVariable().setValue(false);
		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

		Thread.sleep(1000);

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(2000);

		lOrcaFlash4StackCamera.stop();
		// Thread.sleep(1000);

		lOrcaFlash4StackCamera.close();

		System.out.println(mFrameIndex.get());

		assertTrue(mFrameIndex.get() >= 1000);

		lVideoWindow.close();
	}

	/**/

}
