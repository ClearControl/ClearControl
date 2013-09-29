package asyncprocs;

import java.io.Closeable;

public interface ProcessorInterface<I, O> extends Closeable
{

	public O process(I pInput);

}
