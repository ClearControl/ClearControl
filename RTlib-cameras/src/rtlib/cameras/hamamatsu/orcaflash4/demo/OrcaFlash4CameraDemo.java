package rtlib.cameras.hamamatsu.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rtlib.cameras.hamamatsu.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;
import dcamj.DcamAcquisition.TriggerType;

public class OrcaFlash4CameraDemo
{
	AtomicLong mCounter = new AtomicLong(0);

	@Test
	public void test() throws InterruptedException
	{
		OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																							TriggerType.Internal);

		StackProcessorInterface lCounterProcessor = new StackProcessorBase("Counter")
		{

			@Override
			public Stack process(	Stack pStack,
														Recycler<Stack, Long> pStackRecycler)
			{
				long lCounter = mCounter.incrementAndGet();

				System.out.println(pStack);
				// System.out.println(pStack.hashCode());
				// assertTrue(pStack.getStackIndex() == lCounter);
				return pStack;
			}
		};

		lCounterProcessor.setActive(true);

		lOrcaFlash4StackCamera.addStackProcessor(lCounterProcessor);

		assertTrue(lOrcaFlash4StackCamera.open());

		lOrcaFlash4StackCamera.ensureEnough2DFramesAreAvailable(100);

		Thread.sleep(1000);

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(1000);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();

		System.out.println(mCounter.get());

		assertTrue(mCounter.get() == 99);
	}

}
