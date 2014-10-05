package rtlib.core.memory;

public class SizeOf
{

	public static int sizeOf(final Class<?> pClass)
	{
		if (pClass == Character.class || pClass == char.class
				|| pClass == Character.TYPE)
			return sizeOfChar();
		else if (pClass == Byte.class || pClass == byte.class
							|| pClass == Byte.TYPE)
			return sizeOfByte();
		else if (pClass == Short.class || pClass == short.class
							|| pClass == Short.TYPE)
			return sizeOfShort();
		else if (pClass == Integer.class || pClass == int.class
							|| pClass == Integer.TYPE)
			return sizeOfInt();
		else if (pClass == Long.class || pClass == long.class
							|| pClass == Long.TYPE)
			return sizeOfLong();
		else if (pClass == Float.class || pClass == float.class
							|| pClass == Float.TYPE)
			return sizeOfFloat();
		else if (pClass == Double.class || pClass == double.class
							|| pClass == Double.TYPE)
			return sizeOfDouble();
		else
			throw new RuntimeException("Invalid Class!");

	}

	public static int sizeOfDouble()
	{
		return Double.BYTES;
	}

	public static int sizeOfLong()
	{
		return Long.BYTES;
	}

	public static int sizeOfFloat()
	{
		return Float.BYTES;
	}

	public static int sizeOfInt()
	{
		return Integer.BYTES;
	}

	public static int sizeOfShort()
	{
		return Short.BYTES;
	}

	public static int sizeOfByte()
	{
		return Byte.BYTES;
	}

	public static int sizeOfChar()
	{
		return Character.BYTES;
	}

	public static Class<?> integralTypeFromSize(final int pNumberOfBytes,
																							final boolean pSigned)
	{
		switch (pNumberOfBytes)
		{
		case Byte.BYTES:
			return byte.class;
		case Short.BYTES:
			if (pSigned)
				return short.class;
			else
				return char.class;
		case Integer.BYTES:
			return int.class;
		case Long.BYTES:
			return long.class;
		}
		throw new RuntimeException("Invalid number of bytes!");
	}

	public static Class<?> floatTypeFromSize(final int pNumberOfBytes)
	{
		switch (pNumberOfBytes)
		{
		case Float.BYTES:
			return float.class;
		case Double.BYTES:
			return double.class;
		}
		throw new RuntimeException("Invalid number of bytes!");
	}
}
