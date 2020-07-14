package nl.naturalis.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
  public static boolean notEmpty(Collection<?> c) {
    return !isEmpty(c);
  }

  public static <T> List<T> ifEmpty(List<T> list, T e0) {
    return isEmpty(list) ? List.of(e0) : list;
  }

  public static <T> List<T> ifEmpty(List<T> list, T e0, T e1) {
    return isEmpty(list) ? List.of(e0, e1) : list;
  }

  @SafeVarargs
  public static <T> List<T> ifEmpty(List<T> list, T e0, T e1, T... moreElems) {
    return isEmpty(list) ? List.of(ArrayMethods.prefix(moreElems, e0, e1)) : list;
  }

  public static <T> List<T> ifEmpty(List<T> list, Collection<T> use) {
    if (isEmpty(list)) {
      return use instanceof List ? (List<T>) use : List.copyOf(use);
    }
    return list;
  }

  public static <T> Set<T> ifEmpty(Set<T> set, T e0) {
    return isEmpty(set) ? Set.of(e0) : set;
  }

  public static <T> Set<T> ifEmpty(Set<T> set, T e0, T e1) {
    return isEmpty(set) ? Set.of(e0, e1) : set;
  }

  @SafeVarargs
  public static <T> Set<T> ifEmpty(Set<T> set, T e0, T e1, T... moreElems) {
    return isEmpty(set) ? Set.of(ArrayMethods.prefix(moreElems, e0, e1)) : set;
  }

  public static <T> Set<T> ifEmpty(Set<T> set, Collection<T> use) {
    if (isEmpty(set)) {
      return use instanceof Set ? (Set<T>) use : Set.copyOf(use);
    }
    return set;
  }

  /**
   * Returns a slice of the provided list starting with starting with element {@code from} and
   * containing at most {@code length} elements.
   * <ol>
   * <li>If {@code from} is less than zero, it is taken relative to the end of the list.
   * <li>If {@code length} is zero or {@code from} is past the end of the list, an empty list is
   * returned.
   * </ol>
   * 
   * @param list
   * @param from
   * @param length
   * @return
   */
  public static <T> List<T> sublist(List<T> list, int from, int length) {
    Check.notNull(list, "list");
    Check.argument(length >= 0, "length must not be negative");
    if (length == 0 || from >= list.size()) {
      return Collections.emptyList();
    }
    if (from < 0) {
      from = list.size() + from;
    }
    from = Math.max(0, from);
    int to = Math.min(list.size(), from + length);
    return list.subList(from, to);
  }

  /**
   * Creates and returns a mutable {@link HashSet} containing the provided elements.
   * 
   * @param <T>
   * @param e0
   * @param e1
   * @param moreElements
   * @return
   */
  @SafeVarargs
  public static <T> HashSet<T> newHashSet(T e0, T e1, T... moreElements) {
    HashSet<T> set = new HashSet<>();
    set.add(e0);
    set.add(e1);
    Arrays.stream(moreElements).forEach(set::add);
    return set;
  }

  /**
   * Returns a mutable {@link HashMap} containing the provided key-value pairs.
   * 
   * @param <K>
   * @param <V>
   * @param k0
   * @param v0
   * @param k1
   * @param v1
   * @param moreKVPairs
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <K, V> HashMap<K, V> newHashMap(K k0, V v0, K k1, V v1, Object... moreKVPairs) {
    Check.argument(moreKVPairs.length % 2 == 0, "moreKVPairs array must contain an even number of elements");
    HashMap<K, V> map = new HashMap<>();
    map.put(k0, v0);
    map.put(k1, v1);
    for (int i = 0; i < moreKVPairs.length; i += 2) {
      map.put((K) moreKVPairs[i], (V) moreKVPairs[i + 1]);
    }
    return map;
  }

  /**
   * Creates and returns a mutable {@link LinkedHashMap} containing the provided key-value pairs.
   * 
   * @param <K>
   * @param <V>
   * @param k0
   * @param v0
   * @param k1
   * @param v1
   * @param moreKVPairs
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(K k0, V v0, K k1, V v1, Object... moreKVPairs) {
    Check.argument(moreKVPairs.length % 2 == 0, "moreKVPairs array must contain even number of elements");
    LinkedHashMap<K, V> map = new LinkedHashMap<>();
    map.put(k0, v0);
    map.put(k1, v1);
    for (int i = 0; i < moreKVPairs.length; i += 2) {
      map.put((K) moreKVPairs[i], (V) moreKVPairs[i + 1]);
    }
    return map;
  }

}
