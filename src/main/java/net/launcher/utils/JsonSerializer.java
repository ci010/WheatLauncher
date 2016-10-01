package net.launcher.utils;

import org.to2mbn.jmccc.internal.org.json.JSONObject;

/**
 * @author ci010
 */
public interface JsonSerializer<T>
{
	T deserialize(JSONObject jsonObject);

	JSONObject serialize(T data);

	interface IO<T>
	{
		void readFrom(JSONObject jsonObject, T data);

		void writeTo(JSONObject jsonObject, T data);
	}
}
