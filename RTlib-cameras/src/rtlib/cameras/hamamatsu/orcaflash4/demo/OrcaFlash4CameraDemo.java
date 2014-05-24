package rtlib.cameras.hamamatsu.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rtlib.cameras.hamamatsu.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;
import dcamj.DcamAcquisition.TriggerType;

public class OrcaFlash4CameraDemo
{
	AtomicLong mCounter = new AtomicLong(0);

	@Test
	public void test() throws InterruptedException
	{
		OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																							TriggerType.Internal);

		lOrcaFlash4StackCamera.getStackReferenceVariable()
													.sendUpdatesTo(new ObjectVariable<Stack>("Receiver")
													{

														@Override
														public Stack setEventHook(Stack pNewStack)
														{
															/*System.out.println("testbody: hashcode=" + pNewStack.hashCode()
																									+ " index="
																									+ pNewStack.getIndex());/**/
															System.out.println(pNewStack);
															return super.setEventHook(pNewStack);
														}

													});

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.getFrameDepthVariable().setValue(100);
		lOrcaFlash4StackCamera.getStackModeVariable().setValue(true);
		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

		Thread.sleep(1000);

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(2000);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();

		System.out.println(mCounter.get());

		assertTrue(mCounter.get() == 1);
	}

}
