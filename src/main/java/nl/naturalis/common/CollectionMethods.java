package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ArrayMethods.END_INDEX;
import static nl.naturalis.common.ArrayMethods.START_INDEX;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;

/** Methods extending the Java Collection framework. */
public class CollectionMethods {

  private CollectionMethods() {}

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
   * Returns {@code collection} if not empty, else the {@code Collection} provided by {@codeimport
   * com.sun.org.apache.xpath.internal.functions.Function;
   *
   * <p><p><p><p><p><p><p>alternative}.
   *
   * @param <T>
   * @param <U>
   * @param collection
   * @param alternative
   * @return
   */
  public static <T, U extends Collection<T>> U ifEmpty(U collection, U alternative) {
    return ObjectMethods.isEmpty(collection) ? alternative : collection;
  }

  public static String implode(Collection<?> collection) {
    return implode(collection, ", ");
  }

  public static String implode(Collection<?> collection, String separator) {
    return collection.stream().map(Objects::toString).collect(Collectors.joining(separator));
  }

  public static <K, V> Map<K, V> toMap(
      List<V> list, Function<? super V, ? extends K> keyExtractor) {
    return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
  }

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
    Check.that(values, "values").is(noneNull()).has(length(), eq(), consts.length);
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
    Check.that(list, "list").is(notEmpty());
    Check.that(by, "by").is(toIndexOf(), list);
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
    int sz = Check.that(list, "list").is(notEmpty()).ok().size();
    Check.that(by, "by").is(toIndexOf(), list);
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
   * {@code null}.
   *
   * @param <E> The type of the elements
   * @param <L> The type of the {@code List}
   * @param elementType The class of the elements
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size with all elements initialized to
   *     {@code null}
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends List<? super E>> L initializedList(Class<E> elementType, int size) {
    E[] array = (E[]) Array.newInstance(elementType, size);
    return (L) new ArrayList<E>(List.of(array));
  }

  /**
   * Creates a new, modifiable {@code List} of the specified size with all elements initialized to
   * the specified value.
   *
   * @param <E> The type of the elements
   * @param <L> The type of the {@code List}
   * @param elementType The class of the elements
   * @param initValue the value to initialze the list elements to
   * @param size The desired size of the {@code List}
   * @return A new, modifiable {@code List} of the specified size with all elements initialized to
   *     the specified value.
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends List<? super E>> L initializedList(
      Class<E> elementType, int size, E initVal) {
    E[] array = (E[]) Array.newInstance(elementType, size);
    Arrays.fill(array, initVal);
    return (L) new ArrayList<E>(List.of(array));
  }
}
