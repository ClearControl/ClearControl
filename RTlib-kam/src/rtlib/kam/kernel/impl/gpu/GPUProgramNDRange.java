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
											long[] pRange,
											Object... pArgs)
	{
		execute(pFunctionName, null, pRange, null, pArgs);
	}

	@Override
	public void execute(String pFunctionName,
											long[] pRangeOffset,
											long[] pRange,
											long[] pLocalRange,
											Object... pArgs)
	{
		ArrayList<Object> lArgList = new ArrayList<>();

		long lNDRangeVolume = NDRangeUtils.volume(pRange);

		long[] lMaxThreadNDRange = mCLContext.getMaxThreadNDRange();

		long[] lEffectiveNDRange = new long[pRange.length];

		for (int i = 0; i < pRange.length; i++)
			lEffectiveNDRange[i] = Math.min(lMaxThreadNDRange[i], pRange[i]);

		long lEffectiveNDRangeVolume = NDRangeUtils.volume(lEffectiveNDRange);

		double lRatio = lNDRangeVolume / lEffectiveNDRangeVolume;

		if (lRatio != Math.round(lRatio))
			throw new KernelException("Work volume is not divisible by the max thread volume");

		if (pRangeOffset == null)
			lArgList.add(NDRangeUtils.zero(pRange.length));
		else
			lArgList.add(pRangeOffset);
		lArgList.add(pRange);

		for (Object lArg : pArgs)
			lArgList.add(lArg);

		super.execute(pFunctionName,
									pRangeOffset,
									lEffectiveNDRange,
									pLocalRange,
									lArgList.toArray());
	}
}
