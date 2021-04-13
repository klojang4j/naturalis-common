package nl.naturalis.common;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.UnsafeList;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.common.ArrayMethods.END_INDEX;
import static nl.naturalis.common.ArrayMethods.START_INDEX;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;

/** Methods extending the Java Collection framework. */
public class CollectionMethods {

  private CollectionMethods() {}

  /**
   * Returns the specified value as a {@code List}. This method behaves as follows:
   *
   * <p>
   *
   * <ul>
   *   <li>If the value is {@code null} it is converted using {@code
   *       Collections.singletonList(val)}.
   *   <li>If the value already is a {@code List} it is returned as-is.
   *   <li>If the value is a {@code Collection} it is converted to a fixed-size, but mutable {@code
   *       List}
   *   <li>If the value is an array it is converted to a fixed-size, but mutable {@code List}
   *   <li>In any other case the value is converted using {@code Collections.singletonList(val)}.
   * </ul>
   *
   * @param val The value to convert
   * @return The value converted to a {@code List}
   */
  @SuppressWarnings({"rawtypes"})
  public static List<?> asList(Object val) {
    List objs;
    if (val == null) {
      objs = Collections.singletonList(val);
    } else if (val instanceof List) {
      objs = (List) val;
    } else if (val instanceof Collection) {
      objs = Arrays.asList(((Collection) val).toArray());
    } else if (val instanceof Object[]) {
      objs = Arrays.asList((Object[]) val);
    } else if (val.getClass() == int[].class) {
      objs = ArrayMethods.asList((int[]) val);
    } else if (val.getClass() == double[].class) {
      objs = ArrayMethods.asList((double[]) val);
    } else if (val.getClass() == byte[].class) {
      objs = ArrayMethods.asList((byte[]) val);
    } else if (val.getClass() == short[].class) {
      objs = ArrayMethods.asList((short[]) val);
    } else if (val.getClass() == float[].class) {
      objs = ArrayMethods.asList((float[]) val);
    } else if (val.getClass() == char[].class) {
      objs = ArrayMethods.asList((char[]) val);
    } else if (val.getClass() == boolean[].class) {
      objs = ArrayMethods.asList((boolean[]) val);
    } else {
      objs = Collections.singletonList(val);
    }
    return objs;
  }

  /**
   * Returns the specified value as a {@code List}. This method behaves as follows:
   *
   * <p>
   *
   * <ul>
   *   <li>If the value is {@code null} it is converted using {@code
   *       Collections.singletonList(val)}.
   *   <li>If the value already is a {@code List} it is returned as-is (it is <i>not</i> converted
   *       to an {@link UnsafeList}).
   *   <li>If the value is a {@code Collection} it is converted to an {@code UnsafeList}.
   *   <li>If the value is an instance of {@code Object[]} to an {@code UnsafeList}.
   *   <li>If the value is an array of a primitive type to an {@code UnsafeList}.
   *   <li>In any other case the value is converted using {@code Collections.singletonList(val)}.
   * </ul>
   *
   * @param val The value to convert
   * @return The value converted to a {@code List}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List<?> asUnsafeList(Object val) {
    List objs;
    if (val == null) {
      objs = Collections.singletonList(val);
    } else if (val instanceof List) {
      objs = (List) val;
    } else if (val instanceof Collection) {
      objs = new UnsafeList((Collection) val);
    } else if (val instanceof Object[]) {
      objs = new UnsafeList((Object[]) val);
    } else if (val.getClass() == int[].class) {
      objs = ArrayMethods.asUnsafeList((int[]) val);
    } else if (val.getClass() == double[].class) {
      objs = ArrayMethods.asUnsafeList((double[]) val);
    } else if (val.getClass() == byte[].class) {
      objs = ArrayMethods.asUnsafeList((byte[]) val);
    } else if (val.getClass() == short[].class) {
      objs = ArrayMethods.asUnsafeList((short[]) val);
    } else if (val.getClass() == float[].class) {
      objs = ArrayMethods.asUnsafeList((float[]) val);
    } else if (val.getClass() == char[].class) {
      objs = ArrayMethods.asUnsafeList((char[]) val);
    } else if (val.getClass() == boolean[].class) {
      objs = ArrayMethods.asUnsafeList((boolean[]) val);
    } else {
      objs = Collections.singletonList(val);
    }
    return objs;
  }

  /**
   * Returns the specified list if it is not empty else an immutable list containing only the
   * specified element.
   *
   * @param <T>
   * @param list
   * @param e0
   * @return
   */
  public static <T> List<T> ifEmpty(List<T> list, T e0) {
    return ObjectMethods.isEmpty(list) ? Collections.singletonList(e0) : list;
  }

  /**
   * Returns the specified list if it is not empty else an immutable list containing the specified
   * elements.
   *
   * @param <T>
   * @param list
   * @param e0
   * @param e1
   * @param moreElems
   * @return
   */
  @SafeVarargs
  public static <T> List<T> ifEmpty(List<T> list, T e0, T e1, T... moreElems) {
    return ObjectMethods.isEmpty(list) ? List.of(ArrayMethods.prefix(moreElems, e0, e1)) : list;
  }

  /**
   * Returns the specified set if it is not empty else an immutable set containing only the
   * specified element.
   *
   * @param <T>
   * @param set
   * @param e0
   * @return
   */
  public static <T> Set<T> ifEmpty(Set<T> set, T e0) {
    return ObjectMethods.isEmpty(set) ? Collections.singleton(e0) : set;
  }

  /**
   * Returns the specified set if it is not empty else an immutable set containing the specified
   * elements.
   *
   * @param <T>
   * @param set
   * @param e0
   * @param e1
   * @param moreElems
   * @return
   */
  @SafeVarargs
  public static <T> Set<T> ifEmpty(Set<T> set, T e0, T e1, T... moreElems) {
    return ObjectMethods.isEmpty(set) ? Set.of(ArrayMethods.prefix(moreElems, e0, e1)) : set;
  }

  /**
   * PHP-style implode method, concatenating the collection elements with &#34;, &#34; as separator
   * string.
   *
   * @param collection The collection to implode
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection) {
    return implode(collection, ", ");
  }

  /**
   * PHP-style implode method, concatenating the collection elements using the specified separator
   * string.
   *
   * @param collection The collection to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection, String separator) {
    return implode(collection, separator, -1);
  }

  /**
   * PHP-style implode method, concatenating the collection elements using the specified separator
   * string.
   *
   * @param collection The collection to implode
   * @param separator The separator string
   * @param limit The maximum number of elements to collect. Specify -1 for no maximum. Any other
   *     negative integer results in an {@link IllegalArgumentException}.
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection, String separator, int limit) {
    Check.notNull(collection, "collection");
    Check.notNull(separator, "separator");
    Check.that(limit, "limit").is(gte(), -1);
    Stream<?> stream = collection.stream();
    if (limit != -1 || limit < collection.size()) {
      stream = stream.limit(limit);
    }
    return stream.map(Objects::toString).collect(Collectors.joining(separator));
  }

  /**
   * Shortcut for the ubiquitous <code>
   * list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()))</code>.
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values and the list elements
   * @param list The {@code List} to convert.
   * @param keyExtractor The key-extraction function
   * @return A modifiable {@code Map}
   */
  public static <K, V> Map<K, V> toMap(
      List<V> list, Function<? super V, ? extends K> keyExtractor) {
    return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
  }

  /**
   * Shortcut for <code>
   * list.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()))</code>.
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values and the list elements
   * @param list The {@code List} to convert.
   * @param keyExtractor The key-extraction function
   * @return An unmodfiable {@code Map}
   */
  public static <K, V> Map<K, V> toUnmodifiableMap(
      List<V> list, Function<? super V, ? extends K> keyExtractor) {
    return list.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()));
  }

  /**
   * Returns a mutable {@link HashMap} containing the provided key-value pairs.
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values
   * @param kvPairs An array alternating between keys and values
   * @return a new {@code HashMap}
   */
  @SuppressWarnings("unchecked")
  public static <K, V> HashMap<K, V> newHashMap(Object... kvPairs) {
    Check.notNull(kvPairs, "kvPairs").has(length(), even());
    HashMap<K, V> map = new HashMap<>(kvPairs.length);
    for (int i = 0; i < kvPairs.length; i += 2) {
      map.put((K) kvPairs[i], (V) kvPairs[i + 1]);
    }
    return map;
  }

  /**
   * Returns a mutable {@link LinkedHashMap} containing the provided key-value pairs.
   *
   * @param <K>
   * @param <V>
   * @param kvPairs
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Object... kvPairs) {
    Check.notNull(kvPairs, "kvPairs").has(length(), even());
    LinkedHashMap<K, V> map = new LinkedHashMap<>(kvPairs.length);
    for (int i = 0; i < kvPairs.length; i += 2) {
      map.put((K) kvPairs[i], (V) kvPairs[i + 1]);
    }
    return map;
  }

  /**
   * Returns a mutable {@link EnumMap} with all enum constants set to non-null values. The number if
   * values must exactly equal the number of enum constants and they are assigned according to
   * ordinal number. This method throws an {@link IllegalArgumentException} if the number of values
   * is not exactly equal to the number of constants in the enum class, or if any of the values is
   * null.
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
    Check.that(values, "values").is(neverNull()).has(length(), eq(), consts.length);
    EnumMap<K, ? super V> map = new EnumMap<>(enumClass);
    for (int i = 0; i < consts.length; ++i) {
      map.put(consts[i], values[i]);
    }
    return (M) map;
  }

  /**
   * Returns a tightly-sized copy of the specified map.
   *
   * @param <K> The type of the keys
   * @param <V> The type of the values
   * @param map The {@code Map} to copy
   * @return A tightly-sized copy of the specified map
   */
  public static <K, V> Map<K, V> tightHashMap(Map<K, V> map) {
    HashMap<K, V> copy = new HashMap<>(map.size() + 1, 1F);
    copy.putAll(map);
    return copy;
  }

  /**
   * Prints the specified {@code Map} to the specified {@code OutputStream}. Each key-value pair is
   * on a new line.
   *
   * @param map The map to print
   * @param out The {@code OutputStream} to print to
   */
  public static void printMap(Map<?, ?> map, OutputStream out) {
    PrintStream ps = out.getClass() == PrintStream.class ? (PrintStream) out : new PrintStream(out);
    map.forEach((k, v) -> ps.printf("%20s : %s%n", k, v));
  }

  /**
   * Returns a mutable {@link HashSet} containing the provided elements.
   *
   * @param <T> The type of the elements
   * @param elems The elements
   * @return A {@code HashSet} containing the elements
   */
  @SafeVarargs
  public static <T> HashSet<T> newHashSet(T... elems) {
    Check.notNull(elems, "elems");
    HashSet<T> set = new HashSet<>(elems.length);
    Arrays.stream(elems).forEach(set::add);
    return set;
  }

  /**
   * Returns a mutable {@link LinkedHashSet} containing the provided elements.
   *
   * @param <T> The type of the elements
   * @param elems The elements
   * @return A {@code HashSet} containing the elements
   */
  @SafeVarargs
  public static <T> LinkedHashSet<T> newLinkedHashSet(T... elems) {
    Check.notNull(elems, "elems");
    LinkedHashSet<T> set = new LinkedHashSet<>(elems.length);
    Arrays.stream(elems).forEach(set::add);
    return set;
  }

  /**
   * Returns a sublist containing all but the last element of the provided list. The returned list
   * is backed the original list, so changing its elements will affect the original list as well.
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
   * Left-shifts the provided list by one element. The returned list is backed the original list, so
   * changing its elements will affect the original list as well.
   *
   * @param <T>
   * @param list
   * @return
   */
  public static <T> List<T> shift(List<T> list) {
    return shift(list, 1);
  }

  /**
   * Left-shifts the provided list by the specified number of elements. The returned list is backed
   * the original list, so changing its elements will affect the original list as well.
   *
   * @param <T>
   * @param list
   * @param by
   * @return
   */
  public static <T> List<T> shift(List<T> list, int by) {
    int sz = Check.that(list, "list").isNot(empty()).ok().size();
    Check.that(by, "by").is(validToIndex(), list);
    return sz == by ? Collections.emptyList() : list.subList(by, sz);
  }

  /**
   * Returns a sublist of the provided list starting with starting with element {@code from} and
   * containing at most {@code length} elements. The returned list is backed the original list, so
   * changing its elements will affect the original list as well.
   *
   * <p>
   *
   * <ol>
   *   <li>If {@code from} is negative, it is taken relative to the end of the list.
   *   <li>If {@code length} is negative, the sublist is taken in the opposite direction, with
   *       {@code from} now becoming the <i>last</i> element of the sublist
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
    if (from < 0) {
      from = Check.that(sz + from, START_INDEX).is(gte(), 0).intValue();
    } else {
      Check.that(from, START_INDEX).is(lte(), sz);
    }
    int to;
    if (length >= 0) {
      to = Check.that(from + length, END_INDEX).is(lte(), sz).intValue();
    } else {
      to = Check.that(from + 1, END_INDEX).is(lte(), sz).intValue();
      from = Check.that(to + length, START_INDEX).is(gte(), 0).intValue();
    }
    return list.subList(from, to);
  }

  /**
   * Creates a new, modifiable {@code List} of the specified size with all elements initialized to
   * specified value (must not be null).
   *
   * @param <E> The type of the elements
   * @param <L> The type of the {@code List}
   * @param initVal The initial value of the elements (must not be null)
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size with all elements initialized to
   *     the specified value
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends List<E>> L initializedList(E initVal, int size) {
    Check.notNull(initVal, "initVal");
    Check.that(size, "size").is(gte(), 0);
    E[] array = (E[]) Array.newInstance(initVal.getClass(), size);
    Arrays.fill(array, 0, size, initVal);
    return (L) asList(array);
  }

  /**
   * Creates a new, modifiable {@code List} of the specified size whose elements are initialized
   * using the specified value generator.
   *
   * @param <E> The type of the elements
   * @param <L> The type of the {@code List}
   * @param valueGenerator A function that generates a value based on the list index
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size whose elements are initialized
   *     using the specified value generator.
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends List<E>> L initializedList(IntFunction<E> valueGenerator, int size) {
    Check.notNull(valueGenerator, "valueGenerator");
    Check.that(size, "size").is(gte(), 0);
    return (L) IntStream.range(0, size).mapToObj(valueGenerator::apply).collect(toList());
  }

  /**
   * Creates a new, modifiable {@code List} of the specified size with all elements initialized to
   * null.
   *
   * @param <E> The type of the elements
   * @param <L> The type of the {@code List}
   * @param clazz The class of the elements
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size with all elements initialized to
   *     null.
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends List<E>> L initializedList(Class<E> clazz, int size) {
    E[] array = (E[]) Array.newInstance(clazz, size);
    return (L) asList(array);
  }
}
