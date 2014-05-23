package rtlib.kam.memory;


public interface PointerAccessible extends MemoryTyped
{
	long getAddress();
	long getSizeInBytes();
}
