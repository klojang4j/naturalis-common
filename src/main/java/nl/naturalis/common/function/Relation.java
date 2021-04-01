package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between two objects. For example, if object x is a {@code
 * Collection} and object y is an element of it, then the relation <i>X contains Y</i> exists
 * between these two objects.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 * @param <U> The type of the object of the relation
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship. For
   * example, the converse of <i>X contains Y</i> is <i>Y contains X</i> (or <i>X element-of Y</i>).
   *
   * @return The converse of this {@code Relation}
   */
  default Relation<U, T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of the this relation.
   *
   * @return The negation of the this relation
   */
  default Relation<T, U> negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The subject of the relation (the entity from which the relationship extends)
   * @param object The object of the relation (the entity to which the relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);
}
