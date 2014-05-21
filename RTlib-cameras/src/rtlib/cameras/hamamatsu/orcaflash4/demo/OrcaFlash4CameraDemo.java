package rtlib.cameras.hamamatsu.orcaflash4.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.cameras.hamamatsu.orcaflash4.OrcaFlash4StackCamera;

public class OrcaFlash4CameraDemo
{

	@Test
	public void test() throws InterruptedException
	{
		OrcaFlash4StackCamera lOrcaFlash4StackCamera = new OrcaFlash4StackCamera(0);

		assertTrue(lOrcaFlash4StackCamera.open());

		assertTrue(lOrcaFlash4StackCamera.start());

		Thread.sleep(100);

		lOrcaFlash4StackCamera.stop();

		lOrcaFlash4StackCamera.close();
	}

}
