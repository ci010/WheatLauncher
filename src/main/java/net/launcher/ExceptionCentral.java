package net.launcher;

import javafx.collections.ObservableList;

/**
 * @author ci010
 */
public interface ExceptionCentral
{
	void report(Throwable throwable);

	void culminate(Throwable throwable);

	ObservableList<Throwable> getCulmilatedList();
}
