package net.launcher.model.fx;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
public class ViewBase<T> implements View<T>, ObservableView<T>
{
	private ObservableList<T> list;
	private Function<T, String> tranlation;
	private Map<String, T> map;

	public ViewBase(Function<T, String> tranlation)
	{
		list = FXCollections.observableArrayList();
		map = new TreeMap<>();
		this.tranlation = tranlation;
		list.addListener((ListChangeListener<T>) c ->
		{
			while (c.next())
			{
				map.putAll(c.getAddedSubList().stream().collect(Collectors.toMap(this.tranlation::apply, Function
						.identity())));
				for (T t : c.getRemoved())
				{
					String key = this.tranlation.apply(t);
					map.remove(key);
				}
			}
		});
	}

	@Override
	public void addListener(ListChangeListener<? super T> listener) {list.addListener(listener);}

	@Override
	public void removeListener(ListChangeListener<? super T> listener) {list.removeListener(listener);}

	@Override
	public boolean addAll(T[] elements) {return list.addAll(elements);}

	@Override
	public boolean setAll(T[] elements) {return list.setAll(elements);}

	@Override
	public boolean setAll(Collection<? extends T> col) {return list.setAll(col);}

	@Override
	public boolean removeAll(T[] elements) {return list.removeAll(elements);}

	@Override
	public boolean retainAll(T[] elements) {return list.retainAll(elements);}

	@Override
	public void remove(int from, int to) {list.remove(from, to);}

	@Override
	public FilteredList<T> filtered(Predicate<T> predicate) {return list.filtered(predicate);}

	@Override
	public SortedList<T> sorted(Comparator<T> comparator) {return list.sorted(comparator);}

	@Override
	public SortedList<T> sorted() {return list.sorted();}

	@Override
	public T get(String id)
	{
		return map.get(id);
	}

	@Override
	public Set<String> keySet()
	{
		return map.keySet();
	}

	@Override
	public boolean remove(String id)
	{
		T remove = map.remove(id);
		return list.remove(remove);
	}

	@Override
	public boolean containsKey(String id)
	{
		return map.containsKey(id);
	}

	@Override
	public Function<T, String> toKeyTranslation()
	{
		return tranlation;
	}

	@Override
	public int size() {return list.size();}

	@Override
	public boolean isEmpty() {return list.isEmpty();}

	@Override
	public boolean contains(Object o) {return list.contains(o);}

	@Override
	public Iterator<T> iterator() {return list.iterator();}

	@Override
	public Object[] toArray() {return list.toArray();}

	@Override
	public <T1> T1[] toArray(T1[] a) {return list.toArray(a);}

	@Override
	public boolean add(T t) {return list.add(t);}

	@Override
	public boolean remove(Object o)
	{
		String apply = tranlation.apply((T) o);
		map.remove(apply);
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {return list.containsAll(c);}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		map.putAll(c.stream().collect(Collectors.toMap(tranlation::apply, Function.identity())));
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		map.putAll(c.stream().collect(Collectors.toMap(tranlation::apply, Function.identity())));
		return list.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		for (Object o : c)
		{
			String apply = tranlation.apply((T) o);
			map.remove(apply);
		}
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {return list.retainAll(c);}

	@Override
	public void replaceAll(UnaryOperator<T> operator) {list.replaceAll(operator);}

	@Override
	public void sort(Comparator<? super T> c) {list.sort(c);}

	@Override
	public void clear() {list.clear();}

	@Override
	public boolean equals(Object o) {return list.equals(o);}

	@Override
	public int hashCode() {return list.hashCode();}

	@Override
	public T get(int index) {return list.get(index);}

	@Override
	public T set(int index, T element) {return list.set(index, element);}

	@Override
	public void add(int index, T element) {list.add(index, element);}

	@Override
	public T remove(int index) {return list.remove(index);}

	@Override
	public int indexOf(Object o) {return list.indexOf(o);}

	@Override
	public int lastIndexOf(Object o) {return list.lastIndexOf(o);}

	@Override
	public ListIterator<T> listIterator() {return list.listIterator();}

	@Override
	public ListIterator<T> listIterator(int index) {return list.listIterator(index);}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {return list.subList(fromIndex, toIndex);}

	@Override
	public Spliterator<T> spliterator() {return list.spliterator();}

	@Override
	public boolean removeIf(Predicate<? super T> filter)
	{
		return list.removeIf(filter);
	}

	@Override
	public Stream<T> stream() {return list.stream();}

	@Override
	public Stream<T> parallelStream() {return list.parallelStream();}

	@Override
	public void forEach(Consumer<? super T> action) {list.forEach(action);}

	@Override
	public void addListener(InvalidationListener listener) {list.addListener(listener);}

	@Override
	public void removeListener(InvalidationListener listener) {list.removeListener(listener);}

}
