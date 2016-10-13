package net.launcher.utils.serial;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
public interface Deserializer<T, S>
{
	T deserialize(S serialized, Map<Object, Object> context);

	default T deserialize(S serialized)
	{
		Map<Object, Object> map = Collections.emptyMap();
		if (serialized instanceof File)
			SerializeMetadata.decroateWithFileInfo(map, (File) serialized);
		return deserialize(serialized, map);
	}
}
