package rtlib.kam.memory;

public interface MapAndReadWrite extends MappableMemory
{

	void mapAndReadFrom(PointerAccessible pPointerAccessible);

	void mapAndWriteTo(PointerAccessible pPointerAccessible);

}
