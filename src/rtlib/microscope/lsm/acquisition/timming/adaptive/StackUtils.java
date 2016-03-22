package rtlib.microscope.lsm.acquisition.timming.adaptive;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.microscope.lsm.LightSheetMicroscopeInterface;
import rtlib.stack.StackInterface;
import coremem.buffers.ContiguousBuffer;

public class StackUtils
{

	public static StackInterface<UnsignedShortType, ShortOffHeapAccess> fuse(LightSheetMicroscopeInterface pLSM)
	{
		StackInterface<UnsignedShortType, ShortOffHeapAccess> lFirstStack = pLSM.getStackVariable(0)
																				.get();

		StackInterface<UnsignedShortType, ShortOffHeapAccess> lFusedStack = lFirstStack.duplicate();

		int lNumberOfStacksToFuse = pLSM.getDeviceLists()
										.getNumberOfDetectionArmDevices();
		
		if (lNumberOfStacksToFuse > 1)
		{
			for (int d = 1; d < lNumberOfStacksToFuse; d++)
			{
				StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack = pLSM.getStackVariable(0)
																					.get();

				maxStack(lFusedStack, lStack);
			}
		}

		return lFusedStack;
	}

	private static void maxStack(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pMaxStack,
									StackInterface<UnsignedShortType, ShortOffHeapAccess> pOtherStack)
	{
		ContiguousBuffer lMaxBuffer = ContiguousBuffer.wrap(pMaxStack.getContiguousMemory());
		ContiguousBuffer lOtherBuffer = ContiguousBuffer.wrap(pMaxStack.getContiguousMemory());

		while (lMaxBuffer.hasRemaining() && lOtherBuffer.hasRemaining())
		{
			char u = lMaxBuffer.readChar();
			char v = lOtherBuffer.readChar();
			char m = (char) max(u, v);

			lMaxBuffer.skipChars(-1);
			lMaxBuffer.writeChar(m);
		}
	}

	public static double computeAverageDifference(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack1,
											StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack2, int pPower)
	{
		ContiguousBuffer lMaxBuffer = ContiguousBuffer.wrap(pStack1.getContiguousMemory());
		ContiguousBuffer lOtherBuffer = ContiguousBuffer.wrap(pStack2.getContiguousMemory());

		double lDifference = 0;
		while (lMaxBuffer.hasRemaining() && lOtherBuffer.hasRemaining())
		{
			char u = lMaxBuffer.readChar();
			char v = lOtherBuffer.readChar();
			lDifference += pow(1.0*abs(u-v),pPower);
		}
		
		double lAverageDifference = ((double)lDifference)/(lMaxBuffer.getSizeInBytes()/2);
		
		return lAverageDifference;
	}

}
