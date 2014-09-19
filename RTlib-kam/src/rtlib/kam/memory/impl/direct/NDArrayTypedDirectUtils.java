package rtlib.kam.memory.impl.direct;

import static java.lang.Math.toIntExact;

public class NDArrayTypedDirectUtils
{
	public static final double[] toDoubleArray(	NDArrayTypedDirect<Double> NDArrayDoubleDirect,
																							double[] pDoubleArray)
	{
		long lLengthInElements = NDArrayDoubleDirect.getLengthInElements();
		
		if(pDoubleArray==null || pDoubleArray.length!=lLengthInElements)
			pDoubleArray = new double[toIntExact(lLengthInElements)];
		
		for(int i=0; i<lLengthInElements; i++)
		{
			double lValue = NDArrayDoubleDirect.getDoubleAligned(i);
			pDoubleArray[i] = lValue;
		}

		return pDoubleArray;
	}
}
