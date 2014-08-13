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
		return 8;
	}

	public static int sizeOfLong()
	{
		return 8;
	}

	public static int sizeOfFloat()
	{
		return 4;
	}

	public static int sizeOfInt()
	{
		return 4;
	}

	public static int sizeOfShort()
	{
		return 2;
	}

	public static int sizeOfByte()
	{
		return 1;
	}

	public static int sizeOfChar()
	{
		return 2;
	}

	public static Class<?> integralTypeFromSize(final int pNumberOfBytes)
	{
		switch (pNumberOfBytes)
		{
		case 1:
			return byte.class;
		case 2:
			return short.class;
		case 4:
			return int.class;
		case 8:
			return long.class;
		}
		throw new RuntimeException("Invalid number of bytes!");
	}

	public static Class<?> floatTypeFromSize(final int pNumberOfBytes)
	{
		switch (pNumberOfBytes)
		{
		case 4:
			return float.class;
		case 8:
			return double.class;
		}
		throw new RuntimeException("Invalid number of bytes!");
	}
}
