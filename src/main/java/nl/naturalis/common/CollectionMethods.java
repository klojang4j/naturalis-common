package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.TypeMap;
import nl.naturalis.common.function.ThrowingFunction;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Map.Entry;
import static java.util.stream.Collectors.*;
import static nl.naturalis.common.ArrayMethods.*;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;

/** Methods extending the Java Collection framework. */
public class CollectionMethods {

  private CollectionMethods() {}

  private static final String ERR_NON_UNIQUE = "Map values must be unique";
  private static final List<?> LIST_OF_NULL = Collections.singletonList(null);

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static final TypeMap<Function<Object, List>> LISTIFIERS =
      TypeMap.build(Function.class)
          .autobox(false)
          .autoExpand(false)
          .add(List.class, List.class::cast)
          .add(Collection.class, o -> new ArrayList<>((Collection) o))
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
  public static <E> List<E> initializeList(E initVal, int size) {
    Check.notNull(initVal, "initVal");
    Check.that(size, "size").is(gte(), 0);
    E[] array = InvokeUtils.newArray(initVal.getClass().arrayType(), size);
    Arrays.fill(array, 0, size, initVal);
    return Arrays.asList(array);
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
    for (int i = 0; i < kvPairs.length - 1; i += 2) {
      map.put((K) kvPairs[i], (V) kvPairs[i + 1]);
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
   * @param values The values to associate with the enum constants
   * @return A fully-occupied {@code EnumMap} with no null-values
   * @throws IllegalArgumentException if {@code enumClass} or {@code Values} is null, or if any of
   *     the provided values is null, or is the number of values is not exactly equals to the number
   *     of enum constants
   */
  @SuppressWarnings("unchecked")
  public static <K extends Enum<K>, V> EnumMap<K, V> saturatedEnumMap(
      Class<K> enumClass, V... values) throws IllegalArgumentException {
    K[] consts = Check.notNull(enumClass, "enumClass").ok().getEnumConstants();
    Check.notNull(values, "values").has(length(), eq(), consts.length);
    EnumMap<K, V> map = new EnumMap<>(enumClass);
    for (int i = 0; i < consts.length; ++i) {
      Check.that(values[i]).is(notNull(), "Illegal null value for key ${0}", consts[i]);
      map.put(consts[i], values[i]);
    }
    return map;
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
      Check.that(start, START_INDEX).is(gte(), 0);
    } else {
      start = from;
      Check.that(start, START_INDEX).is(lte(), sz);
    }
    int end;
    if (length >= 0) {
      end = start + length;
    } else {
      end = start + 1;
      start = end + length;
      Check.that(start, START_INDEX).is(gte(), 0);
    }
    Check.that(end, END_INDEX).is(lte(), sz);
    return list.subList(start, end);
  }
  /**
   * Returns a new {@code Map} where keys and values of the input map have traded places. The
   * specified {@code Map} must not contain duplicate values. An {@link IllegalArgumentException} is
   * thrown if it does.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @return A new {@code Map} where keys and values are swapped
   */
  public static <K, V> Map<V, K> swap(Map<K, V> map) {
    return swap(map, () -> new HashMap<>(1 + map.size() * 4 / 3));
  }

  /**
   * Returns a new {@code Map} where keys and values of the input map have traded places. The
   * specified {@code Map} must not contain duplicate values. An {@link IllegalArgumentException} is
   * thrown if it does. {@code null} keys and values are allowed, however.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @param mapFactory A supplier of a {@code Map} instance
   * @return A new {@code Map} where keys and values are swapped
   */
  public static <K, V> Map<V, K> swap(Map<K, V> map, Supplier<? extends Map<V, K>> mapFactory) {
    Check.notNull(map, "map");
    Check.notNull(mapFactory, "mapFactory");
    Map<V, K> out = mapFactory.get();
    map.forEach((k, v) -> out.put(v, k));
    return Check.that(out).has(mapSize(), eq(), map.size(), ERR_NON_UNIQUE).ok();
  }

  /**
   * Returns an unmodifiable {@code Map} where keys and values of the input map have traded places.
   * The specified {@code Map} must not contain {@code null} keys, {@code null} values or duplicate
   * values. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param <K> The type of the keys in the original map, and of the values in the returned map
   * @param <V> The type of the values in the original map, and of the keys in the returned map
   * @param map The source map
   * @return A new {@code Map} where keys and values are swapped
   */
  public static <K, V> Map<V, K> swapAndFreeze(Map<K, V> map) {
    Map<V, K> out = deepFreeze(map, e -> Map.entry(e.getValue(), e.getKey()));
    return Check.that(out).has(mapSize(), eq(), map.size(), ERR_NON_UNIQUE).ok();
  }

  /**
   * Returns an unmodifiable {@code Map} where the values of the input {@code Map} have been
   * converted using the specified {@code Function}. The specified {@code Map} must not contain
   * {@code null} keys, {@code null} values or duplicate values. An {@link IllegalArgumentException}
   * is thrown if it does.
   *
   * @param <K> The type of the keys of the input and output {@code Map}
   * @param <V0> The type of the values of the input {@code Map}
   * @param <V1> The type of the values of the output {@code Map}
   * @param src The input {@code Map}
   * @param valueConverter A {@code Function} that converts the values of the input {@code Map}
   * @return An unmodifiable {@code Map} where the values of the input {@code Map} have been
   *     converted using the specified {@code Function}
   */
  public static <K, V0, V1> Map<K, V1> freeze(
      Map<K, V0> src, Function<? super V0, ? extends V1> valueConverter) {
    Check.notNull(src, "src");
    Check.notNull(valueConverter, "valueConverter");
    return src.entrySet().stream()
        .peek(checkEntry())
        .map(toEntryConverter(valueConverter))
        .collect(toUnmodifiableMap(key(), value()));
  }

  /**
   * Returns an unmodifiable {@code Map} where the values of the input {@code Map} have been
   * converted using the specified {@code BiFunction}. This method passes both the key and the value
   * to the converter function so you can make the conversion key-dependent, or so you can mention
   * the key if the conversion fails.
   *
   * @param <K> The type of the keys of the input and output {@code Map}
   * @param <V0> The type of the values of the input {@code Map}
   * @param <V1> The type of the values of the output {@code Map}
   * @param src The input {@code Map}
   * @param valueConverter A {@code Function} that converts the values of the input {@code Map}
   * @return An unmodifiable {@code Map} where the values of the input {@code Map} have been
   *     converted using the specified {@code Function}
   */
  public static <K, V0, V1> Map<K, V1> freeze(
      Map<K, V0> src, BiFunction<? super K, ? super V0, ? extends V1> valueConverter) {
    Check.notNull(src, "src");
    Check.notNull(valueConverter, "valueConverter");
    return src.entrySet().stream()
        .peek(checkEntry())
        .map(toEntryConverter(valueConverter))
        .collect(toUnmodifiableMap(key(), value()));
  }

  /**
   * Returns an unmodifiable {@code Map} where the entries of the input {@code Map} have been
   * converted using the specified {@code Function}. The output map may be smaller than the input
   * map if the conversion function does not generate unique keys.
   *
   * @param src The input {@code Map}
   * @param entryConverter A {@code Function} that produces a new entry from the original entry
   * @param <K0> The type of the keys in the input map
   * @param <V0> The type of the values in the input map
   * @param <K1> The type of the keys in the output map
   * @param <V1> The type of the values in the output map
   * @return An unmodifiable {@code Map} where the values of the input {@code Map} have been
   *     converted using the specified {@code Function}
   */
  public static <K0, V0, K1, V1> Map<K1, V1> deepFreeze(
      Map<K0, V0> src, Function<Entry<K0, V0>, Entry<K1, V1>> entryConverter) {
    Check.notNull(src, "src");
    Check.notNull(entryConverter, "entryConverter");
    Map<K1, V1> out = new HashMap<>(1 + src.size() * 4 / 3);
    src.entrySet().stream()
        .peek(checkEntry())
        .map(entryConverter)
        .forEach(e -> out.put(e.getKey(), e.getValue()));
    return Map.copyOf(out);
  }

  private static <K, V0, V1> Function<Entry<K, V0>, Entry<K, V1>> toEntryConverter(
      BiFunction<? super K, ? super V0, ? extends V1> f) {
    return e -> Map.entry(e.getKey(), f.apply(e.getKey(), e.getValue()));
  }

  private static <K, V0, V1> Function<Entry<K, V0>, Entry<K, V1>> toEntryConverter(
      Function<? super V0, ? extends V1> f) {
    return e -> Map.entry(e.getKey(), f.apply(e.getValue()));
  }

  private static <K, V> Consumer<Entry<K, V>> checkEntry() {
    return e ->
        Check.that(e)
            .has(key(), notNull(), "Illegal null key in source map")
            .has(value(), notNull(), "Illegal null value in source map");
  }

  /**
   * Returns an unmodifiable {@code List} containing the values that result from applying the
   * specified function to the source list's elements. The conversion function is allowed to throw a
   * checked exception.
   *
   * @param <T> The type of the elements in the source list
   * @param <U> The type of the elements in the returned list
   * @param <E> The type of exception thrown if the conversion fails
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code List} containing the values that result from applying the
   *     specified function to the source list's elements
   */
  @SuppressWarnings("unchecked")
  public static <T, U, E extends Throwable> List<U> freeze(
      List<? extends T> src, ThrowingFunction<? super T, ? extends U, E> converter) throws E {
    Check.notNull(src, "src");
    Check.notNull(converter, "converter");
    Object[] objs = new Object[src.size()];
    for (int i = 0; i < src.size(); ++i) {
      objs[i] = converter.apply(src.get(i));
    }
    return (List<U>) List.of(objs);
  }

  /**
   * Returns an unmodifiable {@code Set} containing the values that result from applying the
   * specified function to the source set's elements. The conversion function is allowed to throw a
   * checked exception.
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned set
   * @param <E> The type of exception thrown if the conversion fails
   * @param src The source set
   * @param converter The conversion function
   * @return An unmodifiable {@code Set} containing the values that result from applying the
   */
  @SuppressWarnings({"unchecked"})
  public static <T, U, E extends Throwable> Set<U> freeze(
      Set<? extends T> src, ThrowingFunction<? super T, ? extends U, E> converter) throws E {
    Check.notNull(src, "src");
    Check.notNull(converter, "converter");
    Object[] objs = new Object[src.size()];
    Iterator<? extends T> iterator = src.iterator();
    for (int i = 0; i < src.size(); ++i) {
      objs[i] = converter.apply(iterator.next());
    }
    return (Set<U>) Set.of(objs);
  }

  /**
   * Shortcut method. Returns an unmodifiable list using:
   *
   * <blockquote>
   *
   * <pre>{@code
   * src.stream().map(converter).collect(toUnmodifiableList());
   * }</pre>
   *
   * </blockquote>
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned list
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code List} containing the values that result from applying the
   *     specified function to the source collection's elements
   */
  public static <T, U> List<U> collectionToList(
      Collection<? extends T> src, Function<? super T, ? extends U> converter) {
    Check.notNull(src, "src");
    Check.notNull(converter, "converter");
    return src.stream().map(converter).collect(toUnmodifiableList());
  }

  /**
   * Shortcut method. Returns an unmodifiable set using:
   *
   * <blockquote>
   *
   * <pre>{@code
   * src.stream().map(converter).collect(toUnmodifiableSet());
   * }</pre>
   *
   * </blockquote>
   *
   * @param <T> The type of the elements in the source set
   * @param <U> The type of the elements in the returned list
   * @param src The source list
   * @param converter The conversion function
   * @return An unmodifiable {@code Set} containing the values that result from applying the
   *     specified function to the source collection's elements
   */
  public static <T, U> Set<U> collectionToSet(
      Collection<? extends T> src, Function<? super T, ? extends U> converter) {
    Check.notNull(src);
    Check.notNull(converter, "converter");
    return src.stream().map(converter).collect(toUnmodifiableSet());
  }

  /**
   * Shortcut method. Returns an unmodifiable map using:
   *
   * <blockquote>
   *
   * <pre>{@code
   * src.stream().collect(toUnmodifiableMap(keyExtractor, Function.identity()))
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
  public static <K, V> Map<K, V> collectionToMap(
      Collection<V> src, Function<? super V, ? extends K> keyExtractor) {
    Check.notNull(src);
    Check.notNull(keyExtractor, "keyExtractor");
    return src.stream().collect(toUnmodifiableMap(keyExtractor, Function.identity()));
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
    return implode(collection, Objects::toString, separator, 0, limit);
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
    return (String) stream.map(stringifier).collect(joining(separator));
  }
}
