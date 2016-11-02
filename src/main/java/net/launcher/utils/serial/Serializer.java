package net.launcher.utils.serial;

import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
@FunctionalInterface
public interface Serializer<T, S>
{
	S serialize(T data, Map<Object, Object> context);

	default S serialize(T data) {return serialize(data, Collections.emptyMap());}
}
