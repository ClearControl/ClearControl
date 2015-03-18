package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.Recycler;

public interface StackProcessorInterface<TI extends NativeType<TI>, AI extends ArrayDataAccess<AI>, TO extends NativeType<TO>, AO extends ArrayDataAccess<AO>>
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public StackInterface<TO, AO> process(StackInterface<TI, AI> pStack,
																				Recycler<StackInterface<TO, AO>, StackRequest<TO>> pStackRecycler);

}
