package rtlib.kam.kernel.impl.gpu;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.kam.HasPeer;
import rtlib.kam.context.Context;
import rtlib.kam.context.HasContext;
import rtlib.kam.kernel.KernelException;
import rtlib.kam.kernel.Program;
import rtlib.kam.queues.Queue;
import rtlib.kam.queues.QueueableOperations;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.IOUtils;

public class GPUProgram	implements
												Program<CLKernel>,
												HasContext<CLContext>,
												QueueableOperations<CLQueue>

{
	protected Context<CLContext> mCLContext;
	protected CLProgram mCLProgram;
	protected Queue<CLQueue> mCLQueue;
	protected Map<String, CLKernel> mNameToKernelMap = new HashMap<>();

	protected ByteOrder mCLContextByteOrder;
	protected String mKernelString;
	protected AtomicBoolean mUpToDate = new AtomicBoolean();
	protected Map<String, Object> mPropertyMap = new HashMap<>();

	public GPUProgram(Context<CLContext> pCLContext)
	{
		super();
		mCLContext = pCLContext;
		mCLQueue = mCLContext.getDefaultQueue();
		mCLContextByteOrder = mCLContext.getByteOrder();
	}

	@Override
	public Context<CLContext> getContext()
	{
		return mCLContext;
	}

	@Override
	public Queue<CLQueue> getCurrentQueue()
	{
		return mCLQueue;
	}

	@Override
	public void setCurrentQueue(Queue<CLQueue> pQueue)
	{
		mCLQueue = pQueue;
	}

	@Override
	public void setProgramString(String pKernelString)
	{
		mKernelString = pKernelString;
		if (mKernelString == null || mKernelString.equals(pKernelString))
			mUpToDate.set(false);
	}

	@Override
	public void setProgramStringFromURL(URL pKernelURL) throws IOException
	{
		setProgramString(IOUtils.readText(pKernelURL));
	}

	@Override
	public void setProgramStringFromRessource(Class<?> pRootClass,
																						String pRessourceName) throws IOException
	{
		URL lResourceURL = pRootClass.getResource(pRessourceName);
		setProgramStringFromURL(lResourceURL);
	}

	@Override
	public boolean isUpToDate()
	{
		return mUpToDate.get();
	}

	public boolean ensureKernelsAreCompiled()
	{
		if (mCLProgram != null && mUpToDate.get())
			return false;

		if (mKernelString == null || mKernelString.isEmpty())
			throw new KernelException("Kernel string is null or empty!");

		mCLProgram = mCLContext.getPeer().createProgram(mKernelString);

		mCLProgram.defineMacros(mPropertyMap);

		CLKernel[] lKernels = mCLProgram.createKernels();

		for (CLKernel lCLKernel : lKernels)
		{
			mNameToKernelMap.put(lCLKernel.getFunctionName(), lCLKernel);
		}

		mUpToDate.set(true);
		return true;
	}

	protected CLKernel getKernelFunction(String pFunctionName)
	{
		complainIfFreed();
		return mNameToKernelMap.get(pFunctionName);
	}

	public void setStringProperty(String pKey, String pValue)
	{
		mPropertyMap.put(pKey, pValue);
	}

	public void setType(String pType)
	{
		// String lTypeFirstLetter = getTypeFirstLetter(pType);

		String lTypeFamily = getTypeFamily(pType);
		setStringProperty("read_imageT",
											"read_image" + getTypeFirstLetter(lTypeFamily));
		setStringProperty("write_imageT",
											"write_image" + getTypeFirstLetter(lTypeFamily));
		setStringProperty("type", pType);
		setStringProperty("type1", pType + "1");
		setStringProperty("type2", pType + "2");
		setStringProperty("type3", pType + "3");
		setStringProperty("type4", pType + "4");

		setStringProperty("typefam", lTypeFamily);
		setStringProperty("typefam2", lTypeFamily + "2");
		setStringProperty("typefam3", lTypeFamily + "3");
		setStringProperty("typefam4", lTypeFamily + "4");
		setStringProperty("typefam8", lTypeFamily + "8");
		setStringProperty("typefam12", lTypeFamily + "16");

		setStringProperty("convert_type", "convert_" + pType);
		setStringProperty("convert_type2", "convert_" + pType + "2");
		setStringProperty("convert_type3", "convert_" + pType + "3");
		setStringProperty("convert_type4", "convert_" + pType + "4");
		setStringProperty("convert_type8", "convert_" + pType + "8");
		setStringProperty("convert_type16", "convert_" + pType + "16");
	}

	String getTypeFirstLetter(String pType)
	{
		String lTypeFirstLetter;
		lTypeFirstLetter = pType.substring(0, 1);
		if (lTypeFirstLetter.equals("u"))
			lTypeFirstLetter = pType.substring(0, 2);
		return lTypeFirstLetter;
	}

	private String getTypeFamily(String pTypeFirstLetter)
	{
		switch (pTypeFirstLetter)
		{
		case "char":
			return "int";
		case "uchar":
			return "uint";
		case "short":
			return "int";
		case "ushortnt":
			return "uint";
		case "int":
			return "int";
		case "uint":
			return "uint";
		case "long":
			return "int";
		case "ulong":
			return "uint";
		case "float":
			return "float";
		case "double":
			return "float";
		}
		return null;
	}

	@Override
	public void execute(String pFunctionName,
											int[] pRange,
											int[] pLocalRange,
											Object... pArgs)
	{
		ensureKernelsAreCompiled();
		complainIfFreed();
		CLQueue lQueuePeer = mCLQueue.getPeer();
		CLKernel lClKernel = getKernelFunction(pFunctionName);
		if (lClKernel == null)
			throw new KernelException("Cannot find function " + pFunctionName);

		ArrayList<Object> lArgList = new ArrayList<>();

		for (Object lArg : pArgs)
			if (lArg instanceof HasPeer<?>)
				lArgList.add(((HasPeer<?>) lArg).getPeer());
			else
				lArgList.add(lArg);

		lClKernel.setArgs(lArgList.toArray());
		lClKernel.enqueueNDRange(	lQueuePeer,
															pRange,
															pLocalRange);
	}

	@Override
	public void execute(String pFunctionName,
											int[] pRange,
											Object... pArgs)
	{
		execute(pFunctionName, pRange, null, pArgs);
	}

	@Override
	public void free()
	{
		mCLProgram.release();
		mCLProgram = null;
		mNameToKernelMap.clear();
		mUpToDate.set(false);
	}

	@Override
	public boolean isFree()
	{
		return mCLProgram == null;
	}

}
