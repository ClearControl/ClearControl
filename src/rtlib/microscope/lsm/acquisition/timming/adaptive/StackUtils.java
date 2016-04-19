package rtlib.microscope.lsm.acquisition.timming.adaptive;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;

import coremem.buffers.ContiguousBuffer;
import rtlib.microscope.lsm.LightSheetMicroscopeInterface;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.stack.StackInterface;

public class StackUtils
{

	public static StackInterface fuse(LightSheetMicroscopeInterface pLSM)
	{
		StackInterface lFirstStack = pLSM.getStackVariable(0).get();

		StackInterface lFusedStack = lFirstStack.duplicate();

		int lNumberOfStacksToFuse = pLSM.getDeviceLists()
																		.getNumberOfDevices(DetectionArmInterface.class);

		if (lNumberOfStacksToFuse > 1)
		{
			for (int d = 1; d < lNumberOfStacksToFuse; d++)
			{
				StackInterface lStack = pLSM.getStackVariable(0).get();

				maxStack(lFusedStack, lStack);
			}
		}

		return lFusedStack;
	}

	private static void maxStack(	StackInterface pMaxStack,
																StackInterface pOtherStack)
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

	public static double computeAverageDifference(StackInterface pStack1,
																								StackInterface pStack2,
																								int pPower)
	{
		ContiguousBuffer lMaxBuffer = ContiguousBuffer.wrap(pStack1.getContiguousMemory());
		ContiguousBuffer lOtherBuffer = ContiguousBuffer.wrap(pStack2.getContiguousMemory());

		double lDifference = 0;
		while (lMaxBuffer.hasRemaining() && lOtherBuffer.hasRemaining())
		{
			char u = lMaxBuffer.readChar();
			char v = lOtherBuffer.readChar();
			lDifference += pow(1.0 * abs(u - v), pPower);
		}

		double lAverageDifference = (lDifference) / (lMaxBuffer.getSizeInBytes() / 2);

		return lAverageDifference;
	}

}
