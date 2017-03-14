package net.launcher.model.fx.module;

import javafx.concurrent.Task;
import org.to2mbn.jmccc.util.Builder;

import java.util.List;

/**
 * @author ci010
 */
public interface InstanceProvider
{
	List<Class<?>> getAllInstanceBuilderType();

	Builder<?> getInstanceBuilder(Class<?> type);

	Task<Void> saveInstanceTask(Object o);
}
