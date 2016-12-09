package net.launcher.utils.serial;

import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
public interface SerializedWriter<T, S>
{
	S writeTo(T instance, S serialized, Map<Object, Object> context);

	default S writeTo(T instance, S serialized) {return writeTo(instance, serialized, Collections.emptyMap());}
}
