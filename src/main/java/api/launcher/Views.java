package api.launcher;

import javafx.beans.InvalidationListener;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ci010
 */
public class Views
{
	public static <T> View<T> create(Function<T, String> translation)
	{
		return new ViewImpl<>(translation);
	}

	public static <T> View<T> create(Function<T, String> translation, Collection<T> list)
	{
		return new ViewImpl<>(translation, list);
	}

	public static <T> View<T> create(Function<T, String> translation, ObservableList<T> list)
	{
		return new ViewImpl<>(translation, list);
	}

	public static <T> View<T> create(Function<T, String> translation, ObservableMap<String, T> map)
	{
		ObservableList<T> list = FXCollections.observableArrayList();
		map.addListener(new WeakMapChangeListener<>(change ->
		{
			if (change.wasAdded())
				list.add(change.getValueAdded());
			else if (change.wasRemoved())
				list.remove(change.getValueRemoved());
		}));
		return new ViewImpl<>(translation, list, map);
	}

	public static <T, R> View<T> create(Function<T, String> keyMapper, Function<R, T> valueMapper,
										ObservableMap<String, R> map)
	{
		ObservableList<T> list = FXCollections.observableArrayList();
		map.addListener((MapChangeListener<String, R>) change ->
		{
			if (change.wasAdded())
				list.add(valueMapper.apply(change.getValueAdded()));
			else if (change.wasRemoved())
				list.remove(valueMapper.apply(change.getValueRemoved()));
		});
		return new ViewImpl<>(keyMapper, list);
	}

	public static class ViewImpl<T> implements View<T>
	{
		private ObservableList<T> list;
		private Function<T, String> translation;
		private Map<String, T> map;

		public ViewImpl(Function<T, String> translation) {this(translation, FXCollections.observableArrayList());}

		public ViewImpl(Function<T, String> translation, ObservableList<T> content)
		{
			list = content;
			map = new TreeMap<>();
			this.translation = translation;
			map.putAll(list.stream().collect(Collectors.toMap(this.translation, Function.identity())));
			list.addListener((ListChangeListener<T>) c ->
			{
				while (c.next())
				{
					map.putAll(c.getAddedSubList().stream().collect(Collectors.toMap(this.translation::apply, Function.identity())));
					for (T t : c.getRemoved())
					{
						String key = this.translation.apply(t);
						map.remove(key);
					}
				}
			});
		}

		public ViewImpl(Function<T, String> translation, ObservableList<T> content, Map<String, T> map)
		{
			this.list = content;
			this.map = map;
			this.translation = translation;
		}

		public ViewImpl(Function<T, String> translation, Collection<T> content)
		{
			this(translation, FXCollections.observableArrayList());
			list.addAll(content);
		}

		//@formatter:off
		public T getByKey(String id) {return map.get(id);}
		public Set<String> keySet() {return map.keySet();}
		public boolean containsKey(String id) {return map.containsKey(id);}
		public Function<T, String> toKeyTranslation() {return translation;}
		public int size() {return list.size();}
		public boolean isEmpty() {return list.isEmpty();}
		public boolean contains(Object o) {return list.contains(o);}
		public Iterator<T> iterator() {return list.iterator();}
		public Object[] toArray() {return list.toArray();}
		public <T1> T1[] toArray(T1[] a) {return list.toArray(a);}
		public boolean add(T t){throw new UnsupportedOperationException();}
		public boolean remove(Object o){throw new UnsupportedOperationException();}
		public boolean containsAll(Collection<?> c) {return list.containsAll(c);}
		public boolean addAll(Collection<? extends T> c){throw new UnsupportedOperationException();}
		public boolean addAll(int index, Collection<? extends T> c){throw new UnsupportedOperationException();}
		public boolean removeAll(Collection<?> c){throw new UnsupportedOperationException();}
		public boolean retainAll(Collection<?> c){throw new UnsupportedOperationException();}
		public void replaceAll(UnaryOperator<T> operator) {throw new UnsupportedOperationException();}
		public void sort(Comparator<? super T> c) {throw new UnsupportedOperationException();}
		public void clear(){throw new UnsupportedOperationException();}
		public boolean equals(Object o) {return list.equals(o);}
		public int hashCode() {return list.hashCode();}
		public T get(int index) {return list.get(index);}
		public T set(int index, T element) {throw new UnsupportedOperationException();}
		public void add(int index, T element){throw new UnsupportedOperationException();}
		public T remove(int index) {throw new UnsupportedOperationException();}
		public int indexOf(Object o) {return list.indexOf(o);}
		public int lastIndexOf(Object o) {return list.lastIndexOf(o);}
		public ListIterator<T> listIterator() {return list.listIterator();}
		public ListIterator<T> listIterator(int index) {return list.listIterator(index);}
		public List<T> subList(int fromIndex, int toIndex) {return list.subList(fromIndex, toIndex);}
		public Spliterator<T> spliterator() {return list.spliterator();}
		public boolean removeIf(Predicate<? super T> filter){throw new UnsupportedOperationException();}
		public Stream<T> stream() {return list.stream();}
		public Stream<T> parallelStream() {return list.parallelStream();}
		public void forEach(Consumer<? super T> action) {list.forEach(action);}
		public void addListener(InvalidationListener listener) {list.addListener(listener);}
		public void removeListener(InvalidationListener listener) {list.removeListener(listener);}
		public void addListener(ListChangeListener<? super T> listener) {list.addListener(listener);}
		public void removeListener(ListChangeListener<? super T> listener) {list.removeListener(listener);}
		public boolean addAll(T[] elements) {throw new UnsupportedOperationException();}
		public boolean setAll(T[] elements) {throw new UnsupportedOperationException();}
		public boolean setAll(Collection<? extends T> col){throw new UnsupportedOperationException();}
		public boolean removeAll(T[] elements) {throw new UnsupportedOperationException();}
		public boolean retainAll(T[] elements){throw new UnsupportedOperationException();}
		public void remove(int from, int to) {throw new UnsupportedOperationException();}
		public FilteredList<T> filtered(Predicate<T> predicate) {return list.filtered(predicate);}
		public SortedList<T> sorted(Comparator<T> comparator) {return list.sorted(comparator);}
		public SortedList<T> sorted() {return list.sorted();}
	}
}
