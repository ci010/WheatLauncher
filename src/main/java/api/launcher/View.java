package api.launcher;

import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 * @author ci010
 */
public interface View<T> extends Iterable<T>, ObservableList<T>
{
	int size();

	boolean isEmpty();

	boolean contains(Object o);

	Iterator<T> iterator();

	Object[] toArray();

	<T1> T1[] toArray(T1[] a);

	boolean containsAll(Collection<?> c);

	boolean equals(Object o);

	int hashCode();

	T get(int index);

	int indexOf(Object o);

	int lastIndexOf(Object o);

	T getByKey(String id);

	Set<String> keySet();

	boolean containsKey(String id);

	Function<T, String> toKeyTranslation();
}
