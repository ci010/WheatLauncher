package net.wheatlauncher.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import net.launcher.utils.State;
import net.launcher.utils.StrictProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class Condition extends ObservableValueBase<State.Values>
{
	private List<ObservableValue<State.Values>> conditions;
	private State.Values state;
	public String name;
	private InvalidationListener listener = (observable) -> {
//		System.out.println();
//		Logger.trace("[" + nameProperty + "] invalid");
		State.Values state = State.Values.PASS;
		for (ObservableValue<State.Values> subCondition : conditions)
			state = state.and(subCondition.getValue());
//		System.out.println("[Condition]new state is " + state);
//		if (state == StrictProperty.Values.FAIL)
//		{
//			StringBuilder builder = new StringBuilder();
//			for (ObservableValue<StrictProperty.Values> condition : conditions)
//				builder.append(condition.getValue()).append(" ");
//			System.out.println(builder);
//
//		}
		if (this.state != state)
		{
			this.state = state;
			this.fireValueChangedEvent();
		}
	};

	public Condition() {}

	public Condition(String name)
	{
		this.name = name;
	}

	public final Condition add(StrictProperty<?>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		ObservableValue<State.Values>[] arr = new ObservableValue[conditions.length];
		for (int i = 0; i < arr.length; i++)
			arr[i] = new Wrap(conditions[i].state());
		this.add(arr);
		return this;
	}

	public final Condition add(ObservableValue<State> state)
	{
		if (state == null) return this;
		this.add(new Wrap(state));
		return this;
	}

	private class Wrap extends ObservableValueBase<State.Values>
	{
		ObservableValue<State> state;

		public Wrap(ObservableValue<State> state)
		{
			this.state = state;
			state.addListener(observable -> fireValueChangedEvent());
		}

		@Override
		public State.Values getValue()
		{
			return state.getValue().getState();
		}
	}

	@SafeVarargs
	public final Condition add(ObservableValue<State.Values>... conditions)
	{
		if (conditions == null || conditions.length == 0)
			return this;
		if (this.conditions == null)
			this.conditions = new ArrayList<>();
		for (ObservableValue<State.Values> condition : conditions)
			condition.addListener(listener);
		Collections.addAll(this.conditions, conditions);
		listener.invalidated(this);
		return this;
	}

	@Override
	public State.Values getValue()
	{
		return state;
	}
}
