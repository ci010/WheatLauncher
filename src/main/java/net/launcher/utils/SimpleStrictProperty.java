package net.launcher.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;


/**
 * @author ci010
 */
public class SimpleStrictProperty<T> extends SimpleObjectProperty<T> implements StrictProperty<T>
{
	private T cache;
	private ObjectProperty<Validator<T>> validate = new SimpleObjectProperty<>(this, "validator");
	private ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(this, "state"),
			stateCache = new SimpleObjectProperty<>();

	public SimpleStrictProperty()
	{
		this.stateCache.addListener(observable -> refreshState());
		setValue(null);
	}

	public SimpleStrictProperty(T initialValue)
	{
		super(initialValue);
		this.stateCache.addListener(observable -> refreshState());
		setValue(initialValue);
	}

	public SimpleStrictProperty(Object bean, String name)
	{
		super(bean, name);
		this.stateCache.addListener(observable -> refreshState());
		setValue(null);
	}

	public SimpleStrictProperty(Object bean, String name, T initialValue)
	{
		super(bean, name, initialValue);
		this.stateCache.addListener(observable -> refreshState());
		setValue(initialValue);
	}

	public SimpleStrictProperty<T> setValidator(Validator<T> validator)
	{
		validator().setValue(validator);
		return this;
	}

	@Override
	public ObjectProperty<State> state()
	{
		return stateProperty;
	}

	@Override
	public Property<Validator<T>> validator()
	{
		return validate;
	}

	private void refreshState()
	{
		if (stateCache.get() == null)
			return;
		stateProperty.set(stateCache.get());
		if (state().getValue() != null && state().get().getState() == State.Values.PASS)
			set0(cache);
		cache = null;
		stateCache.set(null);
	}

	@Override
	public void setValue(T value)
	{
//		Logger.trace("try set value " + value + " with validator " + validator());
		cache = value;
		Validator<T> tValidator = validate.get();
		if (tValidator != null)
			tValidator.validate(stateCache, value);
		else StrictProperty.NON_NULL.validate(stateCache, value);
	}

	public static class Hierarchy<T> extends SimpleStrictProperty<T>
	{
		private StrictProperty<T> parent;

		public Hierarchy(StrictProperty<T> parent)
		{
			this.parent = parent;
		}

		public Hierarchy(T initialValue, StrictProperty<T> parent)
		{
			super(initialValue);
			this.parent = parent;
		}

		public Hierarchy(Object bean, String name, StrictProperty<T> parent)
		{
			super(bean, name);
			this.parent = parent;
		}

		public Hierarchy(Object bean, String name, T initialValue, StrictProperty<T> parent)
		{
			super(bean, name, initialValue);
			this.parent = parent;
		}

		public Hierarchy()
		{
		}

		public Hierarchy(T initialValue)
		{
			super(initialValue);
		}

		public Hierarchy(Object bean, String name)
		{
			super(bean, name);
		}

		public Hierarchy(Object bean, String name, T initialValue)
		{
			super(bean, name, initialValue);
		}

		@Override
		public T get()
		{
			T v = super.get();
			if (v == null)
				return parent.getValue();
			return v;
		}
	}

	private void set0(T value)
	{
		super.set(value);
	}
}
