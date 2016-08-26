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
	private List<ObservableValue<StrictProperty.State>> conditions;
	private StrictProperty.EnumState state;
	private InvalidationListener listener = (observable) -> {
		System.out.println("[Condition]invalid");
		StrictProperty.EnumState state = StrictProperty.EnumState.PASS;
		for (ObservableValue<StrictProperty.State> subCondition : conditions)
			state = state.and(subCondition.getValue().getState());
		System.out.println("[Condition]new state is " + state);
		if (this.state != state)
		{
			this.state = state;
			this.fireValueChangedEvent();
		}
	};

	protected final Condition add(StrictProperty<?>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		ObservableValue<StrictProperty.State>[] arr = new ObservableValue[conditions.length];
		for (int i = 0; i < arr.length; i++)
			arr[i] = conditions[i].state();
		this.add(arr);
		return this;
	}

	@SafeVarargs
	protected final Condition add(ObservableValue<StrictProperty.State>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		if (this.conditions == null)
			this.conditions = new ArrayList<>();
		for (ObservableValue<StrictProperty.State> condition : conditions)
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
