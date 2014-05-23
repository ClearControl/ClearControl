package rtlib.core.memory;

public class SizeOf
{

	public static boolean isFloat(Class<?> pClass)
	{
		if (pClass == Float.class || pClass == float.class)
			return true;
		else if (pClass == Double.class || pClass == double.class)
			return true;
		else
			return false;
	}

	public static int sizeOf(Class<?> pClass)
	{
		if (pClass == Character.class || pClass == char.class)
			return sizeOfChar();
		else if (pClass == Byte.class || pClass == byte.class)
			return sizeOfByte();
		else if (pClass == Short.class || pClass == short.class)
			return sizeOfShort();
		else if (pClass == Integer.class || pClass == int.class)
			return sizeOfInt();
		else if (pClass == Long.class || pClass == long.class)
			return sizeOfLong();
		else if (pClass == Float.class || pClass == float.class)
			return sizeOfFloat();
		else if (pClass == Double.class || pClass == double.class)
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
		return 1;
	}

}
