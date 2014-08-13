package rtlib.ao.optimization.demo;

import java.io.IOException;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import rtlib.ao.dms.Mirao52eDevice;
import rtlib.ao.optimization.PSFOptimizer;
import rtlib.cameras.devices.orcaflash4.OrcaFlash4StackCamera;

public class PSFOptimizerDemo
{

	@Test
	public void test() throws InterruptedException, IOException
	{
		final OrcaFlash4StackCamera lOrcaFlash4StackCamera = OrcaFlash4StackCamera.buildWithSoftwareTriggering(0);
		Mirao52eDevice lMirao52eDevice = new Mirao52eDevice(1);

		lOrcaFlash4StackCamera.getExposureInMicrosecondsVariable()
													.setValue(10000);
		lOrcaFlash4StackCamera.getFrameWidthVariable().setValue(64);
		lOrcaFlash4StackCamera.getFrameHeightVariable().setValue(64);

		PSFOptimizer lPSFOptimizer = new PSFOptimizer(lOrcaFlash4StackCamera,
																									lMirao52eDevice);

		lPSFOptimizer.open();
		lPSFOptimizer.start();

		// while (true)
		// lOrcaFlash4StackCamera.trigger();

				DenseMatrix64F lVector = null;
				double lRadius = 0.5;
				for (int i = 0; i < 3; i++)
				{
					System.out.println("lRadius=" + lRadius);
					lVector = lPSFOptimizer.optimize(lVector, 8, 16, lRadius);
					lRadius *= 0.5;
				}

				// lVector = lPSFOptimizer.optimize(lVector, 1000000, 0, 3, 0.001);
				while (true)
					lOrcaFlash4StackCamera.trigger();

				// lPSFOptimizer.stop();
				// lPSFOptimizer.close();/**/
	}
}
