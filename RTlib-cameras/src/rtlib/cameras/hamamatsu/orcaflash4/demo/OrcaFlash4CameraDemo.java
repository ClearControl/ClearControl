package rtlib.cameras.hamamatsu.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rtlib.cameras.hamamatsu.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.processor.StackProcessorBase;
import dcamj.DcamAcquisition.TriggerType;

public class OrcaFlash4CameraDemo
{
	AtomicLong mCounter = new AtomicLong(0);
	@Test
	public void test() throws InterruptedException
	{
		OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(	0,
																																							TriggerType.Internal);

		lOrcaFlash4StackCamera.addStackProcessor(new StackProcessorBase("Counter")
		{

			@Override
			public Stack process(	Stack pStack,
														Recycler<Stack, Long> pStackRecycler)
			{
				setActive(true);
				System.out.println(pStack);
				mCounter.incrementAndGet();
				return pStack;
			}

		});

		assertTrue(lOrcaFlash4StackCamera.open());

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(1000);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();

		System.out.println(mCounter.get());
		
		assertTrue(mCounter.get() > 0);
	}

}
