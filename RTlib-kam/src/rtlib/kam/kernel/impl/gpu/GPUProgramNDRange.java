package rtlib.kam.kernel.impl.gpu;

import java.util.ArrayList;

import rtlib.kam.context.Context;
import rtlib.kam.context.HasContext;
import rtlib.kam.kernel.KernelException;
import rtlib.kam.kernel.NDRangeUtils;
import rtlib.kam.kernel.Program;
import rtlib.kam.queues.QueueableOperations;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLQueue;

public class GPUProgramNDRange extends GPUProgram	implements
																									Program<CLKernel>,
																									HasContext<CLContext>,
																									QueueableOperations<CLQueue>
{

	public GPUProgramNDRange(Context<CLContext> pCLContext)
	{
		super(pCLContext);
	}

	@Override
	public void execute(String pFunctionName,
											int[] pRange,
											Object... pArgs)
	{
		execute(pFunctionName, pRange, null, pArgs);
	}

	@Override
	public void execute(String pFunctionName,
											int[] pRange,
											int[] pLocalRange,
											Object... pArgs)
	{
		ArrayList<Object> lArgList = new ArrayList<>();

		int lNDRangeVolume = NDRangeUtils.volume(pRange);

		int[] lMaxThreadNDRange = mCLContext.getMaxThreadNDRange();

		int[] lEffectiveNDRange = new int[pRange.length];

		for (int i = 0; i < pRange.length; i++)
			lEffectiveNDRange[i] = Math.min(lMaxThreadNDRange[i], pRange[i]);

		long lEffectiveNDRangeVolume = NDRangeUtils.volume(lEffectiveNDRange);

		double lRatio = lNDRangeVolume / lEffectiveNDRangeVolume;

		if (lRatio != Math.round(lRatio))
			throw new KernelException("Work volume is not divisible by the max thread volume");

		lArgList.add(pRange);

		for (Object lArg : pArgs)
			lArgList.add(lArg);

		super.execute(pFunctionName,
									lEffectiveNDRange,
									pLocalRange,
									lArgList.toArray());
	}
}
