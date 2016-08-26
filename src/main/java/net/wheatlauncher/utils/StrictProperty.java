package net.wheatlauncher.utils;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

import java.util.StringJoiner;

/**
 * @author ci010
 */
public interface StrictProperty<T> extends Property<T>
{
	ObservableValue<State> state();

	Property<Validator<T>> validator();

	Validator<Object> NON_NULL = (Validator<Object>) (writableValue, v) -> {
		Logger.trace("try validate " + v);
		if (v != null)
			writableValue.setValue(State.of(EnumState.PASS));
		else writableValue.setValue(State.of(EnumState.FAIL, "null"));
	};

	class State
	{
		private EnumState state;
		private String cause;

		public static State of(EnumState state, String... cause)
		{
			if (cause != null && cause.length > 0)
			{
				StringJoiner stringJoiner = new StringJoiner(".");
				for (String s : cause)
					stringJoiner.add(s);
				return new State(state, stringJoiner.toString());
			}
			return new State(state, "");
		}

		private State(EnumState state, String cause)
		{
			this.state = state;
			this.cause = cause;
		}

		public EnumState getState() {return state;}

		public String getCause() {return cause;}
	}

	enum EnumState
	{
		FAIL, PENDING, PASS;

		public EnumState and(EnumState state)
		{
			return this.ordinal() < state.ordinal() ? this : state;
		}
	}

	interface Validator<T>
	{
		void validate(WritableValue<State> stateHandler, T v);
	}
}
