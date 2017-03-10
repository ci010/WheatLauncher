package net.launcher.utils.serial;

import java.io.File;
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
	T deserialize(S serialized, Map<Object, Object> context) throws Exception;

	default T deserializeWithException(S serialized, Consumer<Throwable> throwableConsumer)
	{
		try
		{
			return directDeserialize(serialized);
		}
		catch (Exception e)
		{
			throwableConsumer.accept(e);
		}
		return null;
	}

	default T directDeserialize(S serialized) throws Exception
	{
		Map<Object, Object> map = new HashMap<>();
		if (serialized instanceof File)
			SerializeMetadata.decroateWithFileInfo(map, (File) serialized);
		if (serialized instanceof Path)
			SerializeMetadata.decorateWithFileInfo(map, (Path) serialized);
		return deserialize(serialized, map);
	}

	default T deserialize(S serialized)
	{
		return deserializeWithException(serialized, Throwable::printStackTrace);
	}
}
