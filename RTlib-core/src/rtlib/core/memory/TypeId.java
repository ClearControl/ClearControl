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


}
