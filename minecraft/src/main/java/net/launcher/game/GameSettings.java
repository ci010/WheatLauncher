package net.launcher.game;

import org.to2mbn.jmccc.internal.org.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author ci010
 */
public interface GameSettings
{
	List<Option> getAllActiveOptions();

	Map<String, Option> getOptionMap();

	boolean addOption(Option option);

	interface OptionType
	{
		String toString(Object o);

		Object fromString(String s);
	}

	enum DefaultOptionType implements OptionType
	{
		BOOLEAN(Boolean::parseBoolean), INT(Integer::parseInt), DOUBLE(Double::parseDouble),
		LIST(s ->
		{
			JSONArray objects = new JSONArray(s);
			String[] strings = new String[objects.length()];
			for (int i = 0; i < objects.length(); i++)
				strings[i] = objects.getString(i);
			return strings;
		}, v -> new JSONArray(v).toString());

		private Function<String, Object> toObject;
		private Function<Object, String> toString;

		DefaultOptionType(Function<String, Object> toObject, Function<Object, String> toString)
		{
			this.toObject = toObject;
			this.toString = toString;
		}

		DefaultOptionType(Function<String, Object> toObject)
		{
			this.toObject = toObject;
		}

		@Override
		public String toString(Object o)
		{
			if (toString != null)
				return toString.apply(o);
			return o.toString();
		}

		@Override
		public Object fromString(String s)
		{
			return toObject.apply(s);
		}
	}

	interface Option
	{
		String getId();

		OptionType getType();

		boolean setValue(Object o);

		Object getValue();

		String getStringValue();
	}
}
