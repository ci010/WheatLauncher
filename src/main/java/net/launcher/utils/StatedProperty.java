package net.launcher.utils;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * @author ci010
 */
public interface StatedProperty<T> extends Property<T>
{
	ObservableValue<State> state();
}
