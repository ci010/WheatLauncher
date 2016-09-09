package net.wheatlauncher.utils;

import java.util.Optional;

/**
 * @author ci010
 */
public enum PrimitiveType
{
	BOOL(Boolean.class)
			{
				@Override
				public Object parse(String s)
				{
					return Boolean.parseBoolean(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Boolean.parseBoolean(s);
				}

				@Override
				public Object defaultValue()
				{
					return false;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Boolean) a).compareTo((Boolean) b);
				}
			},
	BYTE(Byte.class)
			{
				@Override
				public Object parse(String s)
				{
					return Byte.parseByte(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Byte.parseByte(s, radix);
				}

				@Override
				public Object defaultValue()
				{
					return (byte) 0;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Byte) a).compareTo((Byte) b);
				}
			},
	SHORT(Short.class)
			{
				@Override
				public Object parse(String s)
				{
					return Short.parseShort(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Short.parseShort(s, radix);
				}

				@Override
				public Object defaultValue()
				{
					return (short) 0;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Short) a).compareTo((Short) b);
				}
			},
	INT(Integer.class)
			{
				@Override
				public Object parse(String s)
				{
					return Integer.parseInt(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Integer.parseInt(s, radix);
				}

				@Override
				public Object defaultValue()
				{
					return 0;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Integer) a).compareTo((Integer) b);
				}
			},
	LONG(Long.class)
			{
				@Override
				public Object parse(String s)
				{
					return Long.parseLong(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Long.parseLong(s, radix);
				}

				@Override
				public Object defaultValue()
				{
					return 0L;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Long) a).compareTo((Long) b);
				}
			},
	FLOAT(Float.class)
			{
				@Override
				public Object parse(String s)
				{
					return Float.parseFloat(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Float.parseFloat(s);
				}

				@Override
				public Object defaultValue()
				{
					return 0.0F;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Float) a).compareTo((Float) b);
				}
			},
	DOUBLE(Double.class)
			{
				@Override
				public Object parse(String s)
				{
					return Double.parseDouble(s);
				}

				@Override
				public Object parse(String s, int radix)
				{
					return Double.parseDouble(s);
				}

				@Override
				public Object defaultValue()
				{
					return 0.0D;
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return ((Double) a).compareTo((Double) b);
				}
			},
	CHARACTER(Character.class)
			{
				@Override
				public Object parse(String s)
				{
					return s.toCharArray()[0];
				}

				@Override
				public Object parse(String s, int radix)
				{
					return s.toCharArray()[0];
				}

				@Override
				public Object defaultValue()
				{
					return ' ';
				}

				@Override
				protected int compare(Object a, Object b)
				{
					return Character.class.cast(a) - Character.class.cast(b);
				}
			};

	public static Optional<PrimitiveType> of(Class<?> type)
	{
		return Optional.ofNullable(ofUnsafe(type));
	}

	public static PrimitiveType ofUnsafe(Class<?> type)
	{
		for (PrimitiveType t : values())
			if (t.type == type)
				return t;
		return null;
	}

	public static PrimitiveType inspect(Number number)
	{
		if (number instanceof Integer)
			return INT;
		if (number instanceof Float)
			return FLOAT;
		if (number instanceof Double)
			return DOUBLE;
		if (number instanceof Long)
			return LONG;
		if (number instanceof Byte)
			return BYTE;
		if (number instanceof Short)
			return SHORT;
		return null;
	}

	public static PrimitiveType of(String name)
	{
		name = name.toUpperCase();
		if (name.equals("BOOLEAN"))
			name = "BOOL";
		else if (name.equals("INTEGER"))
			name = "INT";
		return PrimitiveType.valueOf(name);
	}

	private Class<?> type;

	PrimitiveType(Class<?> type)
	{
		this.type = type;
	}

	public abstract Object parse(String s);

	public abstract Object parse(String s, int radix);

	public boolean greater(Object a, Object b)
	{
		return this.compareBetween(a, b) > 0;
	}

	public boolean less(Object a, Object b)
	{
		int i = compareBetween(a, b);
		if (i == Integer.MIN_VALUE)
			return false;
		return i < 0;
	}

	public boolean equal(Object a, Object b)
	{
		return compareBetween(a, b) == 0;
	}

	public boolean isType(Object o) { return o.getClass().equals(type); }

	public abstract Object defaultValue();

	public int compareBetween(Object a, Object b)
	{
		if (a.getClass() == type && type == b.getClass())
			return compare(a, b);
		return Integer.MIN_VALUE;
	}

	protected abstract int compare(Object a, Object b);

	public Class<?> getType()
	{
		return type;
	}
}
