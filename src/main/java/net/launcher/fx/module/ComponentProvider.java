package net.launcher.fx.module;

import net.launcher.fx.View;

import java.util.List;

/**
 * @author ci010
 */
public interface ComponentProvider
{
	List<Class<?>> getAllComponentTypes();

	<T> View<T> getComponent(Class<T> type);

	boolean saveComponent(View<?> o) throws Exception;
}
