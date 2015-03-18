package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public interface StackProcessing<TI extends NativeType<TI>, AI extends ArrayDataAccess<AI>, TO extends NativeType<TO>, AO extends ArrayDataAccess<AO>>
{

	public void addStackProcessor(final StackProcessorInterface<TI, AI, TO, AO> pStackProcessor);

	public void removeStackProcessor(final StackProcessorInterface<TI, AI, TO, AO> pStackProcessor);

}
