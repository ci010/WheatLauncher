package net.launcher.model.fx;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author ci010
 */
public interface View<T> extends List<T>
{
	T get(String id);

	Set<String> keySet();

	boolean remove(String id);

	boolean containsKey(String id);

	Function<T, String> toKeyTranslation();
}
