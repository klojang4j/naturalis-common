package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.TypeMap;
import nl.naturalis.common.function.ThrowingFunction;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry;
import static java.util.stream.Collectors.*;
import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ArrayMethods.START_INDEX;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;

/** Methods extending the Java Collection framework. */
public class CollectionMethods {

  private CollectionMethods() {}

  private static final String ERR_MAP_VALUES_NON_UNIQUE = "Map values must be unique";
  private static final List<?> LIST_OF_NULL = Collections.singletonList(null);

  private static final TypeMap<Function<Object, List>> LISTIFIERS =
      TypeMap.build(Function.class)
          .autobox(false)
          .autoExpand(false)
          .add(List.class, List.class::cast)
          .add(Collections.class, o -> new ArrayList<>((Collection) o))
          .add(int[].class, o -> ArrayMethods.asList((int[]) o))
          .add(double[].class, o -> ArrayMethods.asList((double[]) o))
          .add(long[].class, o -> ArrayMethods.asList((long[]) o))
          .add(byte[].class, o -> ArrayMethods.asList((byte[]) o))
          .add(char[].class, o -> ArrayMethods.asList((char[]) o))
          .add(float[].class, o -> ArrayMethods.asList((float[]) o))
          .add(short[].class, o -> ArrayMethods.asList((short[]) o))
          .add(boolean[].class, o -> ArrayMethods.asList((boolean[]) o))
          .add(Object[].class, o -> Arrays.asList((Object[]) o))
          .add(Object.class, Arrays::asList)
          .freeze();

  /**
   * Converts the specified object to a {@code List}. If the value already is a {@code List}, it is
   * returned as-is. Other types of collections will yield a {@code List} containing the values in
   * the collection. Arrays will yield a {@code List} containing the values in the array. Single
   * values (including {@code null}) will yield a {@code List} containing just that value. In other
   * words, this method takes the shortest path to "listify" the value and there is no guarantee
   * about the type of {@code List} you get.
   *
   * @see ArrayMethods#asWrapperArray(int[])
   * @see ArrayMethods#asList(int[])
   * @param val The value to convert
   * @return The value converted to a {@code List}
   */
  @SuppressWarnings({"rawtypes"})
  public static List<?> asList(Object val) {
    return val == null ? LIST_OF_NULL : LISTIFIERS.get(val.getClass()).apply(val);
  }

  /**
   * Creates a fixed-size, but modifiable {@code List} of the specified size with all elements
   * initialized to specified value (must not be null).
   *
   * @param <E> The type of the elements
   * @param initVal The initial value of the elements (must not be null)
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size with all elements initialized to
   *     the specified value
   */
  @SuppressWarnings("unchecked")
  public static <E> List<E> initializeList(E initVal, int size) {
    Check.notNull(initVal, "initVal");
    Check.that(size, "size").is(gte(), 0);
    E[] array = InvokeUtils.newArray(initVal.getClass().arrayType(), size);
    Arrays.fill(array, 0, size, initVal);
    return (List<E>) Arrays.asList(array);
  }

  /**
   * Returns a modifiable {@link Map} initialized with the specified key-value pairs.
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values
   * @param capacity The initial capacity of the map. If you specify a number less than the number
   *     of key-value pairs (half the length of the varargs array), it will be taken as a
   *     multiplier. For example, 2 would mean that you expect the map to grow to about twice its
   *     initial size.
   * @param kvPairs An array alternating between keys and values
   * @return A {@code HashMap} initialized with the specified key-value pairs
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> initializeMap(int capacity, Object... kvPairs) {
    Check.that(capacity, "capacity").is(gt(), 0);
    Check.notNull(kvPairs, "kvPairs").has(length(), even());
    int sz = capacity < kvPairs.length / 2 ? capacity * kvPairs.length : capacity;
    HashMap<K, V> map = new HashMap<>(1 + sz * 4 / 3);
    for (int i = 0; i < kvPairs.length; i += 2) {
      Object key = kvPairs[i];
      Object val = kvPairs[i + 1];
      K k;
      V v;
      try {
        k = key == null ? null : (K) key;
      } catch (ClassCastException e) {
        return Check.fail("Invalid key type at position ${0}: ${1}", i, className(key));
      }
      try {
        v = val == null ? null : (V) val;
      } catch (ClassCastException e) {
        return Check.fail("Invalid value type at position ${0}: ${1}", i + 1, className(val));
      }
      map.put(k, v);
    }
    return map;
  }

  /**
   * Returns an {@link EnumMap} with all enum constants set to non-null values. The number of values
   * must exactly equal the number of enum constants, and they are assigned according to ordinal
   * number. This method throws an {@link IllegalArgumentException} if the number of values is not
   * exactly equal to the number of constants in the enum class, or if any of the values is null.
   *
   * @param <K> The key type
   * @param <V> The value type
   * @param enumClass The enum class
   * @param values The values to assign to the
   * @return A fully-occupied {@code EnumMap} with no null-values
   * @throws IllegalArgumentException if {@code enumClass} or {@code Values} is null, or if any of
   *     the provided values is null
   */
  @SuppressWarnings("unchecked")
  public static <K extends Enum<K>, V, M extends EnumMap<K, ? super V>> M saturatedEnumMap(
      Class<K> enumClass, V... values) throws IllegalArgumentException {
    K[] consts = Check.notNull(enumClass, "enumClass").ok().getEnumConstants();
    Check.that(values, "values").is(deepNotNull()).has(length(), eq(), consts.length);
    EnumMap<K, ? super V> map = new EnumMap<>(enumClass);
    for (int i = 0; i < consts.length; ++i) {
      map.put(consts[i], values[i]);
    }
    return (M) map;
  }

  /**
   * Returns a sublist containing all but the last element of the provided list. The returned list
   * is backed by the original list, so changing its elements will affect the original list as well.
   *
   * @param <T> The type of the elements in the list
   * @param list The list to shrink
   * @return A sublist containing all but the last element of the provided list
   */
  public static <T> List<T> shrink(List<T> list) {
    return shrink(list, 1);
  }

  /**
   * Returns a sublist containing all but the last {@code by} elements of the provided list. The
   * returned list is backed the original list, so changing its elements will affect the original
   * list as well.
   *
   * @param <T> The type of the elements in the list
   * @param list The list to shrink
   * @param by The number of elements by which to shrink the list
   * @return A sublist containing all but the last {@code by} elements of the provided list
   */
  public static <T> List<T> shrink(List<T> list, int by) {
    Check.that(list, "list").isNot(empty());
    Check.that(by, "by").is(validToIndex(), list);
    int sz = list.size();
    return sz == by ? Collections.emptyList() : list.subList(0, sz - by);
  }

  /**
   * Left-shifts the provided list by one element. The returned list is backed by the original list,
   * so changing its elements will affect the original list as well.
   *
   * @param <T> The type of the elements in the list
   * @param list The {@code List} whose elements to shift
   * @return A sublist containing all but the first element of the provided list
   */
  public static <T> List<T> shift(List<T> list) {
    return shift(list, 1);
  }

  /**
   * Left-shifts the provided list by the specified number of elements. The returned list is backed
   * by the original list, so changing its elements will affect the original list as well.
   *
   * @param <T> The type of the elements in the list
   * @param list The {@code List} whose elements to shift
   * @param by The number of elements to removed from the list
   * @return A sublist containing all but the first {@code by} elements of the provided list
   */
  public static <T> List<T> shift(List<T> list, int by) {
    int sz = Check.that(list, "list").isNot(empty()).ok().size();
    Check.that(by, "by").is(validToIndex(), list);
    return sz == by ? Collections.emptyList() : list.subList(by, sz);
  }

  /**
   * Returns a sublist of the provided list starting with element {@code from} and containing at
   * most {@code length} elements. The returned list is backed by the original list, so changing its
   * elements will affect the original list as well.
   *
   * <p>
   *
   * <ol>
   *   <li>If {@code from} is negative, it is taken relative to the end of the list.
   *   <li>If {@code length} is negative, the sublist is taken in the opposite direction, with the
   *       element at {@code from} now becoming the <i>last</i> element of the sublist
   * </ol>
   *
   * @param list The {@code List} to extract a sublist from
   * @param from The start index (however, see above)
   * @param length The length of the sublist
   * @return A sublist of the provided list
   */
  public static <T> List<T> sublist(List<T> list, int from, int length) {
    Check.notNull(list, "list");
    int sz = list.size();
    int start;
    if (from < 0) {
      start = from + sz;
      Check.that(start, "start index").is(gte(), 0);
    } else {
      start = from;
      Check.that(start, "start index").is(lte(), sz);
    }
    int end;
    if (length >= 0) {
      end = start + length;
    } else {
      end = start + 1;
      start = end + length;
      Check.that(start, START_INDEX).is(gte(), 0);
    }
    Check.that(end, "end index").is(lte(), sz);
    return list.subList(start, end);
  }
  /**
   * Returns a new {@code Map} where keys and values are swapped. The specified {@code Map} must not
   * contain duplicate values. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @return A new {@code Map} where keys and values are swapped
   */
  public static <K, V> Map<V, K> swap(Map<K, V> map) {
    return swap(map, i -> new HashMap(1 + map.size() * 4 / 3));
  }

  /**
   * Returns a new {@code Map} where keys and values are swapped. The specified {@code Map} must not
   * contain duplicate values. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @param mapFactory A function that produces an instance of the {@code Map} that will be returned
   * @return A new {@code Map} where keys and values are swapped
   */
  public static <K, V> Map<V, K> swap(Map<K, V> map, IntFunction<? extends Map<V, K>> mapFactory) {
    Check.notNull(map, "map");
    Check.notNull(mapFactory, "mapFactory");
    Map<V, K> out = mapFactory.apply(map.size());
    map.forEach((k, v) -> out.put(v, k));
    return Check.that(out).has(mapSize(), eq(), map.size(), ERR_MAP_VALUES_NON_UNIQUE).ok();
  }

  /**
   * Returns a new {@code Map} where keys and values are swapped. The specified {@code Map} must not
   * contain duplicate values. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @return A new {@code Map} where keys and values are swapped
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<V, K> swapAndFreeze(Map<K, V> map) {
    Check.notNull(map);
    Check.that(map.values()).has(size(), eq(), map.size(), ERR_MAP_VALUES_NON_UNIQUE);
    return Map.ofEntries(
        map.entrySet().stream()
            .map(e -> Map.entry(e.getValue(), e.getKey()))
            .toArray(Map.Entry[]::new));
  }

  /**
   * Returns an unmodifiable {@code Map} where the values of the input {@code Map} have been
   * converted using the specified {@code Function}.
   *
   * @param <K> The type of the keys of the input and output {@code Map}
   * @param <V> The type of the values of the input {@code Map}
   * @param <W> The type of the values of the output {@code Map}
   * @param src The input {@code Map}
   * @param converter A {@code Function} that converts the values of the input {@code Map}
   * @return An unmodifiable {@code Map} where the values of the input {@code Map} have been
   *     converted using the specified {@code Function}
   */
  @SuppressWarnings("unchecked")
  public static <K, V, W> Map<K, W> freeze(
      Map<K, V> src, Function<? super V, ? extends W> converter) {
    return src.entrySet().stream()
        .map(e -> changeMapValue(e, converter))
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  private static <K, V, W> Map.Entry<K, W> changeMapValue(
      Map.Entry<K, V> e, Function<? super V, ? extends W> f) {
    return Map.entry(e.getKey(), f.apply(e.getValue()));
  }

  /**
   * Returns an unmodifiable {@code Map} where the values of the input {@code Map} have been
   * converted using the specified {@code BiFunction}. This method passes both the key and the value
   * to the converter function so you can make the conversion key-dependent, or so you can mention
   * the key if the conversion fails.
   *
   * @param <K> The type of the keys of the input and output {@code Map}
   * @param <V> The type of the values of the input {@code Map}
   * @param <W> The type of the values of the output {@code Map}
   * @param src The input {@code Map}
   * @param converter A {@code Function} that converts the values of the input {@code Map}
   * @return An unmodifiable {@code Map} where the values of the input {@code Map} have been
   *     converted using the specified {@code Function}
   */
  @SuppressWarnings("unchecked")
  public static <K, V, W> Map<K, W> freeze(
      Map<K, V> src, BiFunction<? super K, ? super V, ? extends W> converter) {
    return src.entrySet().stream()
        .map(e -> changeMapValue(e, converter))
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  private static <K, V, W> Map.Entry<K, W> changeMapValue(
      Map.Entry<K, V> e, BiFunction<? super K, ? super V, ? extends W> f) {
    return Map.entry(e.getKey(), f.apply(e.getKey(), e.getValue()));
  }

  /**
   * Returns an unmodifiable {@code List} containing the values that result from applying the
   * specified function to the source list's elements.
   *
   * @param <T> The type of the elements in the source list
   * @param <U> The type of the elements in the returned list
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code List} containing the values that result from applying the
   *     specified function to the source list's elements
   */
  @SuppressWarnings("unchecked")
  public static <T, U, E extends Throwable> List<U> freeze(
      List<? extends T> src, ThrowingFunction<? super T, ? extends U, E> converter) throws E {
    Object[] objs = new Object[src.size()];
    for (int i = 0; i < src.size(); ++i) {
      objs[i] = converter.apply(src.get(i));
    }
    return (List<U>) List.of(objs);
  }

  /**
   * Returns an unmodifiable {@code Set} containing the values that result from applying the
   * specified function to the source set's elements.
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned set
   * @param src The source set
   * @param converter The conversion function
   * @return An unmodifiable {@code Set} containing the values that result from applying the
   */
  public static <T, U, E extends Throwable> Set<U> freeze(
      Set<? extends T> src, ThrowingFunction<? super T, ? extends U, E> converter) throws E {
    Object[] objs = new Object[src.size()];
    Iterator<? extends T> iterator = src.iterator();
    for (int i = 0; i < src.size(); ++i) {
      objs[i] = converter.apply(iterator.next());
    }
    return (Set<U>) Set.of(objs);
  }

  /**
   * Shortcut method. Returns:
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned list
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code List} containing the values that result from applying the
   *     specified function to the source collection's elements
   */
  public static <T, U> List<U> freezeIntoList(
      Collection<? extends T> src, Function<? super T, ? extends U> converter) {
    return src.stream().map(converter).collect(toUnmodifiableList());
  }

  /**
   * Returns an unmodifiable {@code Set} containing the values that result from applying the
   * specified function to the source collection's elements.
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned list
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code Set} containing the values that result from applying the
   *     specified function to the source collection's elements
   */
  public static <T, U> Set<U> freezeIntoSet(
      Collection<? extends T> src, Function<? super T, ? extends U> converter) {
    return src.stream().map(converter).collect(toUnmodifiableSet());
  }

  /**
   * Shortcut method. Returns:
   *
   * <blockquote>
   *
   * <pre>{@code
   * list.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()))
   * }</pre>
   *
   * </blockquote>
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values and the list elements
   * @param src The {@code List} to convert.
   * @param keyExtractor The key-extraction function
   * @return An unmodifiable {@code Map}
   */
  public static <K, V> Map<K, V> freezeIntoMap(
      Collection<V> src, Function<? super V, ? extends K> keyExtractor) {
    return src.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()));
  }

  /**
   * PHP-style implode method, concatenating the collection elements using ", " (comma-space) as
   * separator.
   *
   * @see ArrayMethods#implode(Object[])
   * @param collection The collection to implode
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(Collection<T> collection) {
    return implode(collection, DEFAULT_IMPLODE_SEPARATOR);
  }

  /**
   * PHP-style implode method, concatenating the collection elements with the specified separator.
   *
   * @see ArrayMethods#implode(Object[], String)
   * @param collection The collection to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(Collection<T> collection, String separator) {
    Check.notNull(collection);
    return implode(collection, Objects::toString, separator, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} collection elements using ", "
   * (comma-space) as separator.
   *
   * @see ArrayMethods#implode(Object[], int)
   * @param collection The collection to implode
   * @param limit The maximum number of elements to collect. Specify -1 for no maximum. Specifying a
   *     number greater than the length of the collection is OK. It will be clamped to the
   *     collection length.
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(Collection<T> collection, int limit) {
    return implode(collection, DEFAULT_IMPLODE_SEPARATOR, limit);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} collection elements using ", "
   * (comma-space) as separator.
   *
   * @see ArrayMethods#implode(Object[], Function)
   * @param collection The collection to implode
   * @param stringifier A {@code Function} that converts the collection elements to strings
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(Collection<T> collection, Function<T, String> stringifier) {
    return implode(collection, stringifier, DEFAULT_IMPLODE_SEPARATOR, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} collection elements using the
   * specified separator.
   *
   * @see ArrayMethods#implode(Object[], String, int)
   * @param collection The collection to implode
   * @param limit The maximum number of elements to collect. Specify -1 for no maximum. Specifying a
   *     number greater than the length of the collection is OK. It will be clamped to the
   *     collection length.
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(Collection<T> collection, String separator, int limit) {
    return implode(collection, Objects::toString, separator, 0, -1);
  }

  /**
   * PHP-style implode method.
   *
   * @see ArrayMethods#implode(Object[], Function, String, int, int)
   * @param collection The collection to implode
   * @param stringifier A {@code Function} that converts the collection elements to strings
   * @param separator The separator string
   * @param from The index of the element to begin the concatenation with (inclusive)
   * @param to The index of the element to end the concatenation with (exclusive). The specified
   *     number will be clamped to {@code collection.size()} (i.e. it's OK to specify a number
   *     greater than {@code collection.size()}). You can specify -1 as a shorthand for {@code
   *     collection.size()}.
   * @return A concatenation of the elements in the collection.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T> String implode(
      Collection<T> collection,
      Function<T, String> stringifier,
      String separator,
      int from,
      int to) {
    int sz = Check.notNull(collection, "collection").ok(Collection::size);
    Check.notNull(stringifier, "stringifier");
    Check.notNull(separator, "separator");
    Check.that(from, "from").is(gte(), 0).is(lte(), sz);
    int x = to == -1 ? sz : Math.min(to, sz);
    Check.that(x, "to").is(gte(), from);
    if (from == 0) {
      Stream<T> stream = x == sz ? collection.stream() : collection.stream().limit(x);
      return stream.map(stringifier).collect(joining(separator));
    } else if (collection instanceof List) {
      List<T> sublist = ((List<T>) collection).subList(from, x);
      return sublist.stream().map(stringifier).collect(joining(separator));
    }
    Stream stream = Arrays.stream(collection.toArray(), from, x);
    return (String) stream.map((Function) stringifier).collect(joining(separator));
  }
}
