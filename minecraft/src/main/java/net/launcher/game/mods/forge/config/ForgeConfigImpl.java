package net.launcher.game.mods.forge.config;

import java.nio.file.Path;
import java.util.*;

/**
 * @author ci010
 */
class ForgeConfigImpl implements ForgeConfig
{
	Map<String, Category> categories;

	Path path;

	ForgeConfigImpl(Path path, Map<String, Category> categoryMap)
	{
		this.categories = categoryMap;
		this.path = path;
	}

	@Override
	public Optional<Property> findProperty(String category, String key)
	{
		return findCategory(category).map(c -> c.get(key));
	}

	@Override
	public Optional<Category> findCategory(String category)
	{
		return Optional.ofNullable(categories.get(category));
	}

	@Override
	public Set<String> getCategoryNames()
	{
		return categories.keySet();
	}

	static class CateImpl implements ForgeConfig.Category
	{
		private String name, comment;

		Map<String, Property> propertyMap;
		List<Category> children;
		CateImpl parent;

		CateImpl(String name, String comment, CateImpl parent)
		{
			this.name = name;
			this.comment = comment;
			this.parent = parent;
			if (this.parent.children == null)
				this.parent.children = new ArrayList<>();
			this.parent.children.add(this);
		}

		@Override
		public int size() {return propertyMap.size();}

		@Override
		public boolean isEmpty() {return propertyMap.isEmpty();}

		@Override
		public boolean containsKey(Object key) {return propertyMap.containsKey(key);}

		@Override
		public boolean containsValue(Object value) {return propertyMap.containsValue(value);}

		@Override
		public Property get(Object key) {return propertyMap.get(key);}

		@Override
		public Set<String> keySet()
		{
			return propertyMap.keySet();
		}

		@Override
		public Collection<Property> values()
		{
			return propertyMap.values();
		}

		@Override
		public Set<Map.Entry<String, Property>> entrySet()
		{
			return propertyMap.entrySet();
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public String getComment()
		{
			return comment;
		}
	}

	public abstract static class PropImpl implements ForgeConfig.Property
	{
		private String name, comment;
		private Type type;

		public PropImpl(String name, String comment, Type type)
		{
			this.name = name;
			this.comment = comment;
			this.type = type;
		}

		@Override
		public String getName() {return name;}

		@Override
		public Type getType() {return type;}

		@Override
		public String getComment() {return comment;}

		@Override
		public boolean isList() {return false;}

		@Override
		public Property setValue(String value) {throw new UnsupportedOperationException();}

		@Override
		public Property setValue(Number val)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Property setValue(boolean value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Property setValues(String[] values)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Property setValues(boolean[] values)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Property setValues(int[] values)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Property setValues(double[] values)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Number getNumber()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getBoolean()
		{
			throw new UnsupportedOperationException();

		}

		@Override
		public String getString()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String[] getStringList()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int[] getIntList()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean[] getBooleanList()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public double[] getDoubleList()
		{
			throw new UnsupportedOperationException();
		}
	}

	static class SingleProp extends PropImpl
	{
		private String value;

		public SingleProp(String name, String comment, Type type)
		{
			super(name, comment, type);
		}

		@Override
		public Property setValue(String value)
		{
			Objects.requireNonNull(value);
			this.value = value;
			return this;
		}

		@Override
		public Property setValue(Number val)
		{
			Objects.requireNonNull(val);
			this.value = val.toString();
			return this;
		}

		@Override
		public Property setValue(boolean value)
		{
			Objects.requireNonNull(value);
			this.value = String.valueOf(value);
			return this;
		}

		@Override
		public Number getNumber()
		{
			return Double.valueOf(value);
		}

		@Override
		public boolean getBoolean()
		{
			return Boolean.valueOf(value);
		}

		@Override
		public String getString()
		{
			return value;
		}

	}

	static class ListProp extends PropImpl
	{
		private String[] values;

		public ListProp(String name, String comment, Type type)
		{
			super(name, comment, type);
		}

		@Override
		public boolean isList() {return true;}

		@Override
		public Property setValues(String[] values)
		{
			this.values = values;
			return this;
		}

		@Override
		public Property setValues(boolean[] values)
		{
			String[] arr = new String[values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = String.valueOf(values[i]);
			this.values = arr;
			return this;
		}

		@Override
		public Property setValues(int[] values)
		{
			String[] arr = new String[values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = String.valueOf(values[i]);
			this.values = arr;
			return this;
		}

		@Override
		public Property setValues(double[] values)
		{
			String[] arr = new String[values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = String.valueOf(values[i]);
			this.values = arr;
			return this;
		}

		@Override
		public String[] getStringList()
		{
			return values;
		}

		@Override
		public int[] getIntList()
		{
			int[] arr = new int[this.values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = Integer.valueOf(values[i]);
			return arr;
		}

		@Override
		public boolean[] getBooleanList()
		{
			boolean[] arr = new boolean[this.values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = Boolean.valueOf(values[i]);
			return arr;
		}

		@Override
		public double[] getDoubleList()
		{
			double[] arr = new double[this.values.length];
			for (int i = 0; i < values.length; i++)
				arr[i] = Double.valueOf(values[i]);
			return arr;
		}
	}
}
