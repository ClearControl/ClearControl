package rtlib.core.memory;

public class TypeId
{


	public static int classToId(Class<?> pClass)
	{
		if (pClass == Character.class || pClass == char.class
				|| pClass == Character.TYPE)
			return 0;
		else if (pClass == Byte.class || pClass == byte.class
							|| pClass == Byte.TYPE)
			return 1;
		else if (pClass == Short.class || pClass == short.class
							|| pClass == Short.TYPE)
			return 2;
		else if (pClass == Integer.class || pClass == int.class
							|| pClass == Integer.TYPE)
			return 3;
		else if (pClass == Long.class || pClass == long.class
							|| pClass == Long.TYPE)
			return 4;
		else if (pClass == Float.class || pClass == float.class
							|| pClass == Float.TYPE)
			return 5;
		else if (pClass == Double.class || pClass == double.class
							|| pClass == Double.TYPE)
			return 6;
		else
			throw new RuntimeException("Invalid Class!");

	}

	public static Class<?> idToClass(final int pId)
	{
		switch (pId)
		{
		case 0:
			return char.class;
		case 1:
			return byte.class;
		case 2:
			return short.class;
		case 3:
			return int.class;
		case 4:
			return long.class;
		case 5:
			return float.class;
		case 6:
			return double.class;
		}
			throw new RuntimeException("Invalid Class!");

	}

	public static boolean isFloatingPointType(final Class<?> pClass)
	{
		if (pClass == Float.class || pClass == float.class)
			return true;
		else if (pClass == Double.class || pClass == double.class
							|| pClass == Double.TYPE)
			return true;
		else
			return false;
	}

	public static boolean isByte(Class<?> pType)
	{
		return byte.class == pType || Byte.class == pType
						|| Byte.TYPE == pType;
	}

	public static boolean isChar(Class<?> pType)
	{
		return char.class == pType || Character.class == pType
						|| Character.TYPE == pType;
	}

	public static boolean isShort(Class<?> pType)
	{
		return short.class == pType || Short.class == pType
						|| Short.TYPE == pType;
	}

	public static boolean isInt(Class<?> pType)
	{
		return int.class == pType || Integer.class == pType
						|| Integer.TYPE == pType;
	}

	public static boolean isLong(Class<?> pType)
	{
		return long.class == pType || Long.class == pType
						|| Long.TYPE == pType;
	}

	public static boolean isFloat(Class<?> pType)
	{
		return float.class == pType || Float.class == pType
						|| Float.TYPE == pType;
	}

	public static boolean isDouble(Class<?> pType)
	{
		return double.class == pType || Double.class == pType
						|| Double.TYPE == pType;
	}

}
