package nl.naturalis.common.function;

import java.util.function.Function;

/**
 * Defines a relationship between two objects. For example, if object x is a {@code Collection} and
 * object y is an element of it, then the relation <i>X contains Y</i> exists between these two
 * objects.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 * @param <U> The type of the object of the relation
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship. For
   * example, the converse of {@code X contains Y} is {@code Y contains X} (or {@code X element-of
   * Y}).
   *
   * @return The converse of this {@code Relation}
   */
  default Relation<U, T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of this {@code Relation}.
   *
   * @return The negation of this {@code Relation}
   */
  default Relation<T, U> negate() {
    return (x, y) -> !exists(x, y);
  }

  default <V> Relation<T,V> or(Function<V,Relation<T,V>> other) {
    return null;
  }

  /**
   * Whether the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The subject of the relation (the entity from which the relationship extends)
   * @param object The object of the relation (the entity to which the relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);
}
