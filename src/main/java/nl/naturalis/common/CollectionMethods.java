package nl.naturalis.common;

import java.util.*;

/**
 * Methods extending the Java Collection framework.
 */
public class CollectionMethods {

  private CollectionMethods() {}

  /**
   * Whether or not the provided <code>Collection</code> is null or empty.
   *
   * @param c
   * @return
   */
  public static boolean isEmpty(Collection<?> c) {
    return c == null || c.isEmpty();
  }

  /**
   * Whether or not the provided <code>Collection</code> is neither null nor empty.
   *
   * @param c
   * @return
   */
  public static boolean isNotEmpty(Collection<?> c) {
    return !isEmpty(c);
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
    return isEmpty(list) ? Collections.singletonList(e0) : list;
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
    return isEmpty(list) ? List.of(ArrayMethods.prefix(moreElems, e0, e1)) : list;
  }

  /**
   * Returns the specified set if it is not empty else an immutable set containing only the specified
   * element.
   *
   * @param <T>
   * @param set
   * @param e0
   * @return
   */
  public static <T> Set<T> ifEmpty(Set<T> set, T e0) {
    return isEmpty(set) ? Collections.singleton(e0) : set;
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
    return isEmpty(set) ? Set.of(ArrayMethods.prefix(moreElems, e0, e1)) : set;
  }

  /**
   * Returns {@code collection} if not empty, else the {@code Collection} provided by
   * {@code alternative}.
   *
   * @param <T>
   * @param <U>
   * @param collection
   * @param alternative
   * @return
   */
  public static <T, U extends Collection<T>> U ifEmpty(U collection, U alternative) {
    return isEmpty(collection) ? alternative : collection;
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
    Check.notNull(kvPairs, "kvPairs");
    Check.integer(kvPairs.length, x -> x % 2 == 0, "kvPairs array must contain even number of elements");
    HashMap<K, V> map = new HashMap<>(kvPairs.length);
    for (int i = 0; i < kvPairs.length; i += 2) {
      map.put((K) kvPairs[i], (V) kvPairs[i + 1]);
    }
    return map;
  }

  /**
   * Returns a mutable {@link HashSet} containing the provided elements.
   *
   * @param <T>
   * @param elems
   * @return
   */
  @SafeVarargs
  public static <T> HashSet<T> newHashSet(T... elems) {
    HashSet<T> set = new HashSet<>(elems.length);
    Arrays.stream(elems).forEach(set::add);
    return set;
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
    Check.notNull(kvPairs, "kvPairs");
    Check.integer(kvPairs.length, x -> x % 2 == 0, "kvPairs array must contain even number of elements");
    LinkedHashMap<K, V> map = new LinkedHashMap<>(kvPairs.length);
    for (int i = 0; i < kvPairs.length; i += 2) {
      map.put((K) kvPairs[i], (V) kvPairs[i + 1]);
    }
    return map;
  }

  /**
   * Returns a mutable {@link LinkedHashSet} containing the provided elements.
   *
   * @param <T>
   * @param elems
   * @return
   */
  @SafeVarargs
  public static <T> LinkedHashSet<T> newLinkedHashSet(T... elems) {
    LinkedHashSet<T> set = new LinkedHashSet<>(elems.length);
    Arrays.stream(elems).forEach(set::add);
    return set;
  }

  /**
   * Shrinks the provided list by one element.
   *
   * @param <T> The type of the elements in the list
   * @param list The list to shrink
   * @return A sublist containing all but the last element of the provided list
   */
  public static <T> List<T> shrink(List<T> list) {
    return shrink(list, 1);
  }

  /**
   * Shrinks the provided list by the specified number of elements.
   *
   * @param <T> The type of the elements in the list
   * @param list The list to shrink
   * @param by The number of elements by which to shrink the list
   * @return A sublist containing all but the last {@code by} elements of the provided list
   */
  public static <T> List<T> shrink(List<T> list, int by) {
    Check.notEmpty(list, "list");
    Check.inRange(by, 0, list.size(), "by");
    int sz = list.size();
    return sz == by ? Collections.emptyList() : list.subList(0, sz - by);
  }

  /**
   * Left-shifts the provided list by one element.
   *
   * @param <T>
   * @param list
   * @return
   */
  public static <T> List<T> shift(List<T> list) {
    return shift(list, 1);
  }

  /**
   * Left-shifts the provided list by the specified number of elements.
   *
   * @param <T>
   * @param list
   * @param by
   * @return
   */
  public static <T> List<T> shift(List<T> list, int by) {
    Check.notEmpty(list, "list");
    Check.inRange(by, 0, list.size(), "by");
    int sz = list.size();
    return sz == by ? Collections.emptyList() : list.subList(by, sz);
  }

  /**
   * Returns a slice of the provided list starting with starting with element {@code from} and
   * containing at most {@code length} elements.
   * <ol>
   * <li>If {@code from} is negative, it is relative to the end of the list.
   * <li>If {@code length} is negative, the sublist is taken to the left of {@code from}. Note that
   * <code>list.get(from)</code> is still included in the sublist (as the last element); {@code from}
   * does not morph into the {@code to} (exclusive) parameter.
   * <li>Both {@code from} and {@code length} are clamped to their minimum and maximum values. In
   * other words you will never get an {@link ArrayIndexOutOfBoundsException}.
   * </ol>
   *
   * @param list
   * @param from
   * @param length
   * @return
   */
  public static <T> List<T> sublist(List<T> list, int from, int length) {
    Check.notNull(list, "list");
    if (length == 0) {
      return Collections.emptyList();
    }
    if (from < 0) {
      from = Math.max(0, list.size() + from);
    }
    if (length < 0) {
      /*
       * e.g. if from == 4 and length == -2, then element 4 (the 5th) element is the *last* of the sublist
       * and element 4 the first.
       */
      length = Math.min(from + 1, Math.abs(length));
      from = from - length + 1;
    } else {
      from = Math.min(list.size() - 1, from);
    }
    int to = Math.min(list.size(), from + length);
    return list.subList(from, to);
  }

}
