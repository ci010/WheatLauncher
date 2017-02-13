package net.launcher.utils.serial;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ci010
 */
@FunctionalInterface
public interface Deserializer<T, S>
{
	T deserialize(S serialized, Map<Object, Object> context);

	default T deserializeWithException(S serialized, Consumer<Throwable> throwableConsumer)
	{
		Map<Object, Object> map = new HashMap<>();
		if (serialized instanceof File)
			SerializeMetadata.decroateWithFileInfo(map, (File) serialized);
		if (serialized instanceof Path)
			try
			{
				SerializeMetadata.decorateWithFileInfo(map, (Path) serialized);
			}
			catch (IOException e)
			{
				if (throwableConsumer != null)
					throwableConsumer.accept(e);
			}
		if (throwableConsumer != null)
			map.put("exceptionHandler", throwableConsumer);
		try
		{
			return deserialize(serialized, map);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	default T deserialize(S serialized)
	{
		return deserializeWithException(serialized, null);
	}
}
