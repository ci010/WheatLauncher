package net.launcher.setting;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author ci010
 */
public abstract class OptionStringList extends GameSettingType.Option<ObservableList<String>>
{
	public OptionStringList(GameSettingType parent, String name)
	{
		super(parent, name);
	}

	@Override
	public GameSettingProperty.List<String> getDefaultValue(GameSetting gameSetting)
	{
		return new GameSettingPropertyList<>(gameSetting, getName(), FXCollections.observableArrayList(), this);
	}

	@Override
	public ObservableList<String> deserialize(GameSetting gameSetting, String s)
	{
		return FXCollections.observableArrayList();
	}

	protected abstract List<String> doDeserialize(String s);

	public static class GameSettingPropertyList<T> extends GameSettingProperty.List<T> implements GameSettingProperty<ObservableList<T>>
	{
		private GameSettingType.Option<ObservableList<T>> option;
		private SimpleListProperty<T> delegate;

		public GameSettingPropertyList(GameSetting instance, String name, ObservableList<T> es, GameSettingType.Option<ObservableList<T>> option)
		{
			this.delegate = new SimpleListProperty<T>(instance, name, es);
			this.option = option;
		}

		@Override
		public GameSetting getBean() {return (GameSetting) delegate.getBean();}

		@Override
		public String getName() {return delegate.getName();}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {return delegate.sizeProperty();}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {return delegate.emptyProperty();}

		@Override
		public void addListener(InvalidationListener listener) {delegate.addListener(listener);}

		@Override
		public void removeListener(InvalidationListener listener) {delegate.removeListener(listener);}

		@Override
		public void addListener(ChangeListener<? super ObservableList<T>> listener) {delegate.addListener(listener);}

		@Override
		public void removeListener(ChangeListener<? super ObservableList<T>> listener) {delegate.removeListener(listener);}

		@Override
		public void addListener(ListChangeListener<? super T> listener) {delegate.addListener(listener);}

		@Override
		public void removeListener(ListChangeListener<? super T> listener) {delegate.removeListener(listener);}

		@Override
		public ObservableList<T> get() {return delegate.get();}

		public void set(ObservableList<T> newValue) {delegate.set(newValue);}

		@Override
		public boolean isBound() {return delegate.isBound();}

		@Override
		public void bind(ObservableValue<? extends ObservableList<T>> newObservable) {delegate.bind(newObservable);}

		@Override
		public void unbind() {delegate.unbind();}

		@Override
		public String toString() {return delegate.toString();}

		@Override
		public void setValue(ObservableList<T> v) {delegate.setValue(v);}

		@Override
		public void bindBidirectional(Property<ObservableList<T>> other) {delegate.bindBidirectional(other);}

		@Override
		public void unbindBidirectional(Property<ObservableList<T>> other) {delegate.unbindBidirectional(other);}

		public void bindContentBidirectional(ObservableList<T> list) {delegate.bindContentBidirectional(list);}

		public void unbindContentBidirectional(Object object) {delegate.unbindContentBidirectional(object);}

		public void bindContent(ObservableList<T> list) {delegate.bindContent(list);}

		public void unbindContent(Object object) {delegate.unbindContent(object);}

		@Override
		public boolean equals(Object obj) {return delegate.equals(obj);}

		@Override
		public int hashCode() {return delegate.hashCode();}

		@Override
		public ObservableList<T> getValue() {return delegate.getValue();}

		public static <E> ListExpression<E> listExpression(ObservableListValue<E> value) {return ListExpression.listExpression(value);}

		@Override
		public int getSize() {return delegate.getSize();}

		@Override
		public ObjectBinding<T> valueAt(int index) {return delegate.valueAt(index);}

		@Override
		public ObjectBinding<T> valueAt(ObservableIntegerValue index) {return delegate.valueAt(index);}

		@Override
		public BooleanBinding isEqualTo(ObservableList<?> other) {return delegate.isEqualTo(other);}

		@Override
		public BooleanBinding isNotEqualTo(ObservableList<?> other) {return delegate.isNotEqualTo(other);}

		@Override
		public BooleanBinding isNull() {return delegate.isNull();}

		@Override
		public BooleanBinding isNotNull() {return delegate.isNotNull();}

		@Override
		public StringBinding asString() {return delegate.asString();}

		@Override
		public int size() {return delegate.size();}

		@Override
		public boolean isEmpty() {return delegate.isEmpty();}

		@Override
		public boolean contains(Object obj) {return delegate.contains(obj);}

		@Override
		public Iterator<T> iterator() {return delegate.iterator();}

		@Override
		public Object[] toArray() {return delegate.toArray();}

		@Override
		public <T1> T1[] toArray(T1[] array) {return delegate.toArray(array);}

		@Override
		public boolean add(T element) {return delegate.add(element);}

		@Override
		public boolean remove(Object obj) {return delegate.remove(obj);}

		@Override
		public boolean containsAll(Collection<?> objects) {return delegate.containsAll(objects);}

		@Override
		public boolean addAll(Collection<? extends T> elements) {return delegate.addAll(elements);}

		@Override
		public boolean addAll(int i, Collection<? extends T> elements) {return delegate.addAll(i, elements);}

		@Override
		public boolean removeAll(Collection<?> objects) {return delegate.removeAll(objects);}

		@Override
		public boolean retainAll(Collection<?> objects) {return delegate.retainAll(objects);}

		@Override
		public void clear() {delegate.clear();}

		@Override
		public T get(int i) {return delegate.get(i);}

		@Override
		public T set(int i, T element) {return delegate.set(i, element);}

		@Override
		public void add(int i, T element) {delegate.add(i, element);}

		@Override
		public T remove(int i) {return delegate.remove(i);}

		@Override
		public int indexOf(Object obj) {return delegate.indexOf(obj);}

		@Override
		public int lastIndexOf(Object obj) {return delegate.lastIndexOf(obj);}

		@Override
		public ListIterator<T> listIterator() {return delegate.listIterator();}

		@Override
		public ListIterator<T> listIterator(int i) {return delegate.listIterator(i);}

		@Override
		public java.util.List<T> subList(int from, int to) {return delegate.subList(from, to);}

		@Override
		public boolean addAll(T[] elements) {return delegate.addAll(elements);}

		@Override
		public boolean setAll(T[] elements) {return delegate.setAll(elements);}

		@Override
		public boolean setAll(Collection<? extends T> elements) {return delegate.setAll(elements);}

		@Override
		public boolean removeAll(T[] elements) {return delegate.removeAll(elements);}

		@Override
		public boolean retainAll(T[] elements) {return delegate.retainAll(elements);}

		@Override
		public void remove(int from, int to) {delegate.remove(from, to);}

		@Override
		public FilteredList<T> filtered(Predicate<T> predicate) {return delegate.filtered(predicate);}

		@Override
		public SortedList<T> sorted(Comparator<T> comparator) {return delegate.sorted(comparator);}

		@Override
		public SortedList<T> sorted() {return delegate.sorted();}

		@Override
		public void replaceAll(UnaryOperator<T> operator) {delegate.replaceAll(operator);}

		@Override
		public void sort(Comparator<? super T> c) {delegate.sort(c);}

		@Override
		public Spliterator<T> spliterator() {return delegate.spliterator();}

		@Override
		public boolean removeIf(Predicate<? super T> filter) {return delegate.removeIf(filter);}

		@Override
		public Stream<T> stream() {return delegate.stream();}

		@Override
		public Stream<T> parallelStream() {return delegate.parallelStream();}

		@Override
		public void forEach(Consumer<? super T> action) {delegate.forEach(action);}

		@Override
		public GameSettingType.Option<ObservableList<T>> getOption() {return option;}
	}
}
