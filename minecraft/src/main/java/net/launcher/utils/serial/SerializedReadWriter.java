package net.launcher.utils.serial;

import java.util.Map;

/**
 * @author ci010
 */
public interface SerializedReadWriter<T, S> extends SerializedReader<T, S>, SerializedWriter<T, S>
{
	static <T, S> SerializedReadWriter<T, S> combine(final SerializedReader<T, S> reader, final SerializedWriter<T, S> writer)
	{
		return new SerializedReadWriter<T, S>()
		{
			@Override
			public T readFrom(T instance, S serialized, Map<Object, Object> context)
			{
				return reader.readFrom(instance, serialized, context);
			}

			@Override
			public S writeTo(T instance, S serialized, Map<Object, Object> context)
			{
				return writer.writeTo(instance, serialized, context);
			}
		};
	}
}
