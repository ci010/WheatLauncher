package net.wheatlauncher.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Objects;

/**
 * @author ci010
 */
public class WrappedStrictProperty<T> implements StrictProperty<T>
{
	private Property<T> delegate;
	private T cache;
	private ObjectProperty<StrictProperty.Validator<T>> validate = new SimpleObjectProperty<>(this, "validator");
	private ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(this, "state"),
			stateCache = new SimpleObjectProperty<>();

	public WrappedStrictProperty(Property<T> delegate)
	{
		this.delegate = delegate;
		this.stateCache.addListener(observable ->
		{
			if (stateCache.get() == null)
				return;
			stateProperty.set(stateCache.get());
			if (state().getValue() != null && state().getValue().getState() == State.Values.PASS)
				this.delegate.setValue(cache);
			cache = null;
			stateCache.set(null);
		});
		this.setValue(delegate.getValue());//refresh state
	}

	@Override
	public void bind(ObservableValue<? extends T> observable) {delegate.bind(observable);}

	@Override
	public void unbind() {delegate.unbind();}

	@Override
	public boolean isBound() {return delegate.isBound();}

	@Override
	public void bindBidirectional(Property<T> other) {delegate.bindBidirectional(other);}

	@Override
	public void unbindBidirectional(Property<T> other) {delegate.unbindBidirectional(other);}

	@Override
	public Object getBean() {return delegate.getBean();}

	@Override
	public String getName() {return delegate.getName();}

	@Override
	public void addListener(ChangeListener<? super T> listener) {delegate.addListener(listener);}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {delegate.removeListener(listener);}

	@Override
	public T getValue() {return delegate.getValue();}

	@Override
	public void addListener(InvalidationListener listener) {delegate.addListener(listener);}

	@Override
	public void removeListener(InvalidationListener listener) {delegate.removeListener(listener);}

	@Override
	public void setValue(T value)
	{
		Objects.requireNonNull(value);
		cache = value;
		Validator<T> tValidator = validate.get();
		if (tValidator != null)
			tValidator.validate(stateCache, value);
		else NON_NULL.validate(stateCache, value);
	}

	@Override
	public ObservableValue<State> state()
	{
		return stateProperty;
	}

	@Override
	public Property<Validator<T>> validator()
	{
		return validate;
	}

	public WrappedStrictProperty<T> withValidator(Validator<T> validator)
	{
		this.validate.set(validator);
		return this;
	}
}
