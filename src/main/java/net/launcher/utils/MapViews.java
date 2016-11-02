package net.launcher.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class MapViews
{
	public static <K, V> MapView<K, V> wrap(Map<K, V> map)
	{
		return new MapView<K, V>()
		{
			@Override
			public int size()
			{
				return map.size();
			}

			@Override
			public boolean isEmpty()
			{
				return map.isEmpty();
			}

			@Override
			public boolean containsKey(Object key)
			{
				return map.containsKey(key);
			}

			@Override
			public boolean containsValue(Object value)
			{
				return map.containsKey(value);
			}

			@Override
			public V get(Object key)
			{
				return map.get(key);
			}

			@Override
			public Set<K> keySet()
			{
				return map.keySet();
			}

			@Override
			public Collection<V> values()
			{
				return map.values();
			}

			@Override
			public Set<Map.Entry<K, V>> entrySet()
			{
				return map.entrySet();
			}
		};
	}

	public static <K, V, O> MapView<K, V> wrap(Map<K, O> map, Function<O, V> transform)
	{
		Objects.requireNonNull(map);
		Objects.requireNonNull(transform);
		return new MapViewImpl<>(map, transform);
	}

	public static <K, V, O> Map<K, V> wrapToMap(Map<K, O> map, Function<O, V> transform)
	{
		Objects.requireNonNull(map);
		Objects.requireNonNull(transform);
		return new MapViewMapImpl<>(map, transform);
	}

	private static class MapViewMapImpl<K, V, O> extends MapViewImpl<K, V, O> implements Map<K, V>
	{
		public MapViewMapImpl(Map<K, O> map, Function<O, V> transform)
		{
			super(map, transform);
		}

		@Override
		public V put(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object key)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V putIfAbsent(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V replace(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V getOrDefault(Object key, V defaultValue)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action)
		{
			throw new UnsupportedOperationException();
		}
	}

	private static class MapViewImpl<K, V, O> implements MapView<K, V>
	{
		private Map<K, O> map;
		private Function<O, V> transform;

		public MapViewImpl(Map<K, O> map, Function<O, V> transform)
		{
			this.map = map;
			this.transform = transform;
		}

		@Override
		public int size() {return map.size();}

		@Override
		public boolean isEmpty() {return map.isEmpty();}

		@Override
		public boolean containsKey(Object key) {return map.containsKey(key);}

		@Override
		public boolean containsValue(Object value) {return map.containsValue(value);}

		@Override
		public V get(Object key)
		{
			return transform.apply(map.get(key));
		}

		@Override
		public Set<K> keySet() {return map.keySet();}

		@Override
		public Collection<V> values()
		{
			return map.values().stream().map(transform).collect(Collectors.toList());
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet()
		{
			return map.entrySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), transform.apply(e.getValue()))).collect(Collectors.toSet());
		}

		@Override
		public boolean equals(Object o) {return map.equals(o);}

		@Override
		public int hashCode() {return map.hashCode();}
	}
}
