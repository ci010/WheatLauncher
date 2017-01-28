package net.launcher.game.forge.config;


import net.launcher.utils.MapView;

import java.util.Optional;
import java.util.Set;

/**
 * @author ci010
 */
public interface ForgeConfig
{
	Optional<Property> findProperty(String category, String key);

	Optional<Category> findCategory(String category);

	Set<String> getCategoryNames();

	interface Category extends MapView<String, Property>
	{
		String getName();

		String getComment();
	}

	interface Property
	{
		enum Type
		{
			STRING, INTEGER, BOOLEAN, DOUBLE, COLOR, MOD_ID
		}

		String getName();

		Property.Type getType();

		String getComment();

		boolean isList();

		//@formatter:off
		Property setValue(String value);
		Property setValue(Number val);
		Property setValue(boolean value);
		Property setValues(String[] values);
		Property setValues(boolean[] values);
		Property setValues(int[] values);
		Property setValues(double[] values);
		//@formatter:on

		//@formatter:off
		Number getNumber();
		boolean getBoolean();
		String getString();
		String[] getStringList();
		int[] getIntList();
		boolean[] getBooleanList();
		double[] getDoubleList();
		default Number getNumber(Number _default) {try {return getNumber();} catch (Exception e) {return _default;}}
		default boolean getBoolean(boolean _default) {try {return getBoolean();} catch (Exception e) {return _default;}}
		default String getString(String _default) {try {return getString();}catch (Exception e) {return _default;}}
		default String[] getStringList(String[] _default) {try {return getStringList();}catch (Exception e) {return _default;}}
		default int[] getIntList(int[] _default) {try {return getIntList();} catch (Exception e) {return _default;}}
		default boolean[] getBooleanList(boolean[] _default) {try {return getBooleanList();} catch (Exception e) {return _default;}}
		default double[] getDoubleList(double[] _default) {try {return getDoubleList();} catch (Exception e) {return _default;}}
		//@formatter:on
	}
}
