package net.launcher.model;

import org.to2mbn.jmccc.internal.org.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ci010
 */
public interface Setting extends Serializable, Map<String, String>
{
	String getId();

	default void putInt(String s, int i) {put(s, String.valueOf(i));}

	default int getInt(String s) {return Integer.valueOf(get(s));}

	default void putFloat(String s, float f) {put(s, String.valueOf(f));}

	default float getFloat(String s) {return Float.valueOf(get(s));}

	default void putArray(String s, JSONArray jsonArray) {put(s, jsonArray.toString());}

	default JSONArray getArray(String s) {return new JSONArray(get(s));}
}
