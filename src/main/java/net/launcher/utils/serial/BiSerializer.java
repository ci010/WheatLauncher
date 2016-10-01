package net.launcher.utils.serial;

import java.util.Map;

/**
 * @author ci010
 */
public interface BiSerializer<T, S> extends Serializer<T, S>, Deserializer<T, S>
{
	static <T, S> BiSerializer<T, S> combine(final Serializer<T, S> serializer, final Deserializer<T, S> deserializer)
	{
		return new BiSerializer<T, S>()
		{
			@Override
			public T deserialize(S serialized, Map<Object, Object> context)
			{
				return deserializer.deserialize(serialized, context);
			}

			@Override
			public S serialize(T data, Map<Object, Object> context)
			{
				return serializer.serialize(data, context);
			}
		};
	}
}
