package rtlib.kam.memory.impl.gpu.demo;

import java.util.Arrays;

import org.junit.Test;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLMem.Flags;
import com.nativelibs4java.opencl.CLMem.ObjectType;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.JavaCL;

public class AvailableDevicesDemo
{

	@Test
	public void testListDevices()
	{

		CLPlatform[] lListPlatforms = JavaCL.listPlatforms();

		for (CLPlatform lCLPlatform : lListPlatforms)
		{
			System.out.println("_____________________________________________");
			System.out.println(lCLPlatform.getName());
			System.out.println(lCLPlatform.getProfile());
			System.out.println(lCLPlatform.getVendor());
			System.out.println(lCLPlatform.getVersion());
			System.out.println(lCLPlatform.getBestDevice());

			System.out.println("BESTDEVICE:");
			printInfo(lCLPlatform.getBestDevice());

			for (CLDevice lDevice : lCLPlatform.listAllDevices(false))
			{
				printInfo(lDevice);

				CLContext lCreateContext = lCLPlatform.createContext(	null,
																															lDevice);
				CLImageFormat[] lSupported3DImageFormats = lCreateContext.getSupportedImageFormats(	Flags.ReadWrite,
																																														ObjectType.Image3D);
				for (CLImageFormat lCLImageFormat : lSupported3DImageFormats)
				{
					System.out.println(lCLImageFormat.toString());
				}
			}
		}

	}

	private void printInfo(CLDevice lDevice)
	{
		System.out.println("_____________________________________________");
		System.out.printf("Device name:    \t %s \n", lDevice);
		System.out.printf("Global Mem size: \t %s \n",
											lDevice.getGlobalMemSize());
		System.out.printf("Local Mem size: \t %s \n",
											lDevice.getLocalMemSize());
		System.out.printf("Max compute units: \t %s \n",
											lDevice.getMaxComputeUnits());

		System.out.printf("Max Workgroup size: \t %s \n",
											lDevice.getMaxWorkGroupSize());
		System.out.printf("Max Workitem sizes: \t %s \n",
											Arrays.toString(lDevice.getMaxWorkItemSizes()));
		System.out.printf("Maximal Image size: \t %s x %s x %s\n",
											lDevice.getImage3DMaxWidth(),
											lDevice.getImage3DMaxHeight(),
											lDevice.getImage3DMaxDepth());

	}

}
