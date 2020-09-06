package nl.naturalis.common.function;

import java.util.Collection;
import java.util.Objects;

/**
 * Verifies that two objects have a certain relationship to each other. For example, if object A is
 * a {@code Collection} and object B is an element of it, then the relationship <i>X contains Y</i>
 * exists between these objects.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 * @param <U> The type of the object of the relation
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the reverse of the specified relation. For example, the reverse of <i>X contains Y</i>
   * is <i>X element-of Y</i> (or <i>Y contains X</i>).
   *
   * @param <X> The type of the subject of the original relation
   * @param <Y> The type of the object of the original relation
   * @param relation The {@code Relation} to return the reverse of
   * @return The reverse {@code Relation}
   */
  public static <X, Y> Relation<Y, X> reverse(Relation<X, Y> relation) {
    return (x, y) -> relation.exists(y, x);
  }

  /**
   * Returns the negation of the specified relation.
   *
   * @param <X> The type of the subject of the original relation
   * @param <Y> The type of the object of the original relation
   * @param relation The {@code Relation} to return the negation of
   * @return The negated {@code Relation}
   */
  public static <X, Y> Relation<X, Y> not(Relation<X, Y> relation) {
    return (x, y) -> !relation.exists(x, y);
  }

  /**
   * Returns the <i>contains</i> relationship for {@code Collection} objects. Same as {@link
   * Collection#contains(Object) Collection::contains}, but more concise as a static import.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the {@code Collection}
   * @return The <i>contains</i> relationship
   */
  public static <E, C extends Collection<E>> Relation<C, E> contains() {
    return Collection::contains;
  }

  /**
   * Returns the <i>element-of</i> relationship for {@code Collection} objects.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the {@code Collection}
   * @return The <i>element-of</i> relationship
   */
  public static <E, C extends Collection<E>> Relation<E, C> elementOf() {
    return reverse(contains());
  }

  /**
   * Returns the <i>equal-to</i> relationship. Same as {@link Objects#equals(Object, Object)
   * Objects::equals}, but more concise as a static import.
   *
   * @param <X> The type of the subject and the object of the relation
   * @return The <i>equal-to</i> relationship
   */
  public static <X> Relation<X, X> isEqualTo() {
    return Objects::equals;
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The object from which the relationship extends
   * @param object The target of the relationship
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);
}
