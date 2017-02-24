package net.wheatlauncher.control.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Mickey
 * @modifier ci010
 */
public enum FinalFieldSetter
{
	INSTANCE;

	private Object unsafeObj;
	private Method
			putObjectMethod,
			objectFieldOffsetMethod,
			staticFieldOffsetMethod,
			staticFieldBaseMethod;

	FinalFieldSetter()
	{
		try
		{
			final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");

			final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafeObj = unsafeField.get(null);

			putObjectMethod = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);

			objectFieldOffsetMethod = unsafeClass.getMethod("objectFieldOffset", Field.class);

			staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", Field.class);

			staticFieldBaseMethod = unsafeClass.getMethod("staticFieldBase", Field.class);
		}
		catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}

	}

	public void set(final Object o, final Field field, final Object value) throws Exception
	{
		final Object fieldBase = o;
		final long fieldOffset = (Long) objectFieldOffsetMethod.invoke(unsafeObj, field);

		putObjectMethod.invoke(unsafeObj, fieldBase, fieldOffset, value);
	}

	public void setStatic(final Field field, final Object value) throws Exception
	{
		final Object fieldBase = staticFieldBaseMethod.invoke(this.unsafeObj, field);
		final long fieldOffset = (Long) staticFieldOffsetMethod.invoke(this.unsafeObj, field);

		putObjectMethod.invoke(this.unsafeObj, fieldBase, fieldOffset, value);
	}


}
