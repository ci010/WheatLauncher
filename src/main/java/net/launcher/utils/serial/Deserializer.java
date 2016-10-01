package net.launcher.utils.serial;

import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
public interface Deserializer<T, S>
{
	T deserialize(S serialized, Map<Object, Object> context);

	default T deserialize(S serialized) {return deserialize(serialized, Collections.emptyMap());}
}
