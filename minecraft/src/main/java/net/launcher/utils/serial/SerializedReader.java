package net.launcher.utils.serial;

import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
public interface SerializedReader<T, S>
{
	T readFrom(T instance, S serialized, Map<Object, Object> context);

	default T readFrom(T instance, S serialized) {return readFrom(instance, serialized, Collections.emptyMap());}
}
