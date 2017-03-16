package net.launcher.fx.module;

import java.util.List;

/**
 * @author ci010
 */
public interface InstanceProvider
{
	List<Class<?>> getAllInstanceBuilderType();

	Object getInstance(Class<?> type);

	boolean saveInstance(Object o) throws Exception;
}
