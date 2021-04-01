package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between an integer and an object. For example, if x equals 6,
 * and y is an {@code int} array containing 6, then the relation <i>X element-of Y</i> exists.
 *
 * @author Ayco Holleman
 * @param <T> The type of the object of the relation
 */
@FunctionalInterface
public interface IntObjRelation<T> {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship.
   *
   * @return An {@code ObjIntRelation} that is the converse of this {@code IntObjRelation}.
   */
  default ObjIntRelation<T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of this {@code IntObjRelation}.
   *
   * @return The negation of this {@code IntObjRelation}
   */
  default IntObjRelation<T> negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Returns whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(int subject, T object);
}
