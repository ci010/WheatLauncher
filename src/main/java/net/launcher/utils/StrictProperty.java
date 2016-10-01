package net.launcher.utils;

import javafx.beans.property.Property;
import javafx.beans.value.WritableValue;

/**
 * @author ci010
 */
public interface StrictProperty<T> extends StatedProperty<T>
{
	Property<Validator<T>> validator();

	Validator<Object> NON_NULL = (Validator<Object>) (writableValue, v) ->
	{
		Logger.trace("try validate " + v);
		if (v != null)
			writableValue.setValue(State.of(State.Values.PASS));
		else writableValue.setValue(State.of(State.Values.FAIL, "null"));
	};

	State PASS = State.of(State.Values.PASS);

	interface Validator<T>
	{
		void validate(WritableValue<State> stateHandler, T v);
	}
}
