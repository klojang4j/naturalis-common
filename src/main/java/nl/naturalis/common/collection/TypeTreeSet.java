package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;
import static nl.naturalis.common.collection.TypeTreeMap.EMPTY_TYPE_ARRAY;

/**
 * A specialized {@link Set} implementation for {@code Class} objects. It returns {@code true} from
 * its {@link Set#contains(Object) contains} method if the set contains the specified type <i>or any
 * of its super types</i>. Its elements are ordered in ascending order of abstraction. That is, for
 * any two elements in the set, the one that comes first will never be a super type of the one that
 * comes second and, conversely, the one that comes second will never be a subtype of the one that
 * comes first. As with {@link TypeHashMap} and {@link TypeTreeMap} you can optionally enable
 * "autoboxing" and auto-expansion. See {@link TypeHashMap} for a description of these features. A
 * {@code TypeTreeSet} is unmodifiable and does not allow {@code null} values. It is backed by a
 * {@link TypeTreeMap}.
 *
 * @author Ayco Holleman
 * @see TypeHashMap
 * @see TypeSet
 * @see TypeTreeMap
 */
public class TypeTreeSet extends AbstractTypeSet {

  /**
   * Returns a new {@code Collection} with a near-total ordering of the types in the specified
   * {@code Collection}, roughly according to the Java type hierarchy. A regular {@code TypeTreeSet}
   * imposes just enough order to efficiently climb the type hierarchy of the types it contains. The
   * {@link java.util.Comparator} used to produce this {@code Collection} is much more rigorous (and
   * that much more inefficient), although the exact order of the types is still somewhat dependent
   * on the encounter order of the types in the source collection. This is how the types in the new
   * {@code Collection} are sorted:
   *
   * <p>
   *
   * <ol>
   *   <li>Primitive types
   *   <li>{@code enum} types
   *   <li>Normal classes except {@code Object.class} - the longer the distance to {@code
   *       Object.class} the earlier in the {@code Collection}
   *   <li>Interfaces - sub-interfaces before super-interfaces
   *   <li>Array types - internally sorted by the type of their elements
   *   <li>{@code Object.class}
   *   <li>Alphabetically by simple class name
   * </ol>
   *
   * @param c The {@code Collection} to sort
   * @return A new {@code Collection} in which the types are sorted from less abstract to more
   *     abstract
   */
  public static Collection<Class<?>> prettySort(Collection<Class<?>> c) {
    return new TypeTreeMap(toMap(c)).keySet();
  }

  /**
   * Sorts the types in the specified collection from less abstract to more abstract and then
   * returns their class names in a {@code List}.
   *
   * @param c The {@code Collection} to sort
   * @return A {@code List} of class names, sorted from less abstract to more abstract
   */
  public static List<String> sortAndGetNames(Collection<Class<?>> c) {
    return new TypeTreeMap(toMap(c)).typeNames();
  }

  /**
   * Sorts the types in the specified collection from less abstract to more abstract and then
   * returns their class names in a {@code List}.
   *
   * @param c The {@code Collection} to sort
   * @return A {@code List} of class names, sorted from less abstract to more abstract
   */
  public static List<String> sortAndGetSimpleNames(Collection<Class<?>> c) {
    return new TypeTreeMap(toMap(c)).simpleTypeNames();
  }

  /**
   * Returns a non-auto-expanding, autoboxing {@code TypeTreeSet} for the specified types.
   *
   * @param types The types to add to the {@code Set}.
   * @return A non-auto-expanding, autoboxing {@code TypeTreeSet} for the specified types
   */
  public static TypeTreeSet of(Class<?>... types) {
    return of(false, true, types);
  }

  /**
   * Returns an autoboxing {@code TypeTreeSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion (see {@link TypeHashMap})
   * @param types The types to add to the {@code Set}.
   * @return An autoboxing {@code TypeTreeSet} for the specified types
   */
  public static TypeTreeSet of(boolean autoExpand, Class<?>... types) {
    return of(autoExpand, true, types);
  }

  /**
   * Returns a {@code TypeTreeSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMap.Builder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @param autobox Whether to enable "autoboxing"
   * @param types The types to add to the {@code Set}.
   * @return A {@code TypeTreeSet} for the specified types
   */
  public static TypeTreeSet of(boolean autoExpand, boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return new TypeTreeSet(asList(types), autoExpand, autobox);
  }

  /**
   * Converts the specified {@code Collection} to a non-auto-expanding, autoboxing {@code
   * TypeTreeSet}.
   *
   * @param c The {@code Collection} to convert
   * @return A non-auto-expanding, autoboxing {@code TypeTreeSet}
   */
  public static TypeTreeSet copyOf(Collection<Class<?>> c) {
    return copyOf(c, false, true);
  }

  /**
   * Converts the specified {@code Collection} to an autoboxing {@code TypeTreeSet}.
   *
   * @param c The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion (see {@link TypeHashMap})
   * @return An autoboxing {@code TypeTreeSet}
   */
  public static TypeTreeSet copyOf(Collection<Class<?>> c, boolean autoExpand) {
    return copyOf(c, autoExpand, true);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeTreeSet}.
   *
   * @param c The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion (see {@link TypeHashMap})
   * @param autobox Whether to enable "autoboxing"
   * @return A {@code TypeTreeSet}
   */
  public static TypeTreeSet copyOf(Collection<Class<?>> c, boolean autoExpand, boolean autobox) {
    Check.that(c).is(deepNotNull());
    if (c.getClass() == TypeTreeSet.class) {
      TypeTreeSet tts = (TypeTreeSet) c;
      if (tts.map.autoExpand == autoExpand && tts.map.autobox == autobox) {
        return tts;
      }
    }
    return new TypeTreeSet(c, false, true);
  }

  private TypeTreeSet(Collection<? extends Class<?>> s, boolean autoExpand, boolean autobox) {
    super(new TypeTreeMap<>(toMap(s), autoExpand, autobox, EMPTY_TYPE_ARRAY));
  }

}
