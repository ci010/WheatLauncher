package net.wheatlauncher.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class Condition extends ObservableValueBase<StrictProperty.EnumState>
{
	private List<ObservableValue<StrictProperty.EnumState>> conditions;
	private StrictProperty.EnumState state;
	private InvalidationListener listener = (observable) -> {
		Logger.traceLevel("invalid", 4);
		StrictProperty.EnumState state = StrictProperty.EnumState.PASS;
		for (ObservableValue<StrictProperty.EnumState> subCondition : conditions)
			state = state.and(subCondition.getValue());
		System.out.println("[Condition]new state is " + state);
		if (this.state != state)
		{
			this.state = state;
			this.fireValueChangedEvent();
		}
	};

	public final Condition add(StrictProperty<?>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		ObservableValue<StrictProperty.EnumState>[] arr = new ObservableValue[conditions.length];
		for (int i = 0; i < arr.length; i++)
			arr[i] = new Wrap(conditions[i].state());
		this.add(arr);
		return this;
	}

	private class Wrap extends ObservableValueBase<StrictProperty.EnumState>
	{
		ObservableValue<StrictProperty.State> state;

		public Wrap(ObservableValue<StrictProperty.State> state)
		{
			this.state = state;
		}

		@Override
		public StrictProperty.EnumState getValue()
		{
			return state.getValue().getState();
		}
	}

	@SafeVarargs
	public final Condition add(ObservableValue<StrictProperty.EnumState>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		if (this.conditions == null)
			this.conditions = new ArrayList<>();
		for (ObservableValue<StrictProperty.EnumState> condition : conditions)
			condition.addListener(listener);
		Collections.addAll(this.conditions, conditions);
		listener.invalidated(this);
		return this;
	}

	@Override
	public StrictProperty.EnumState getValue()
	{
		return state;
	}
}
