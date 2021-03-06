package nl.naturalis.common.function;

import nl.naturalis.common.x.Generated;

/**
 * Defines some kind of relationship between an object and an integer. For example,
 * if x is an {@code int[]} array containing 6, and y equals 6, then the
 * <b>contain-elements</b> relation exists between x and y.
 *
 * @param <T> The type of the subject of the relation
 * @author Ayco Holleman
 * @see Relation
 */
@FunctionalInterface
public interface ObjIntRelation<T> {

  /**
   * Returns the converse of this relation, swapping subject and object in the
   * relationship. Thus, the converse of an {@code IntObjRelation} is an {@link
   * ObjIntRelation}.
   *
   * @return an {@code IntObjRelation} that is the converse of this {@code
   *     ObjIntRelation}.
   */
  @Generated
  default IntObjRelation<T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of the this {@code ObjIntRelation}.
   *
   * @return the negation of the this {@code ObjIntRelation}
   */
  @Generated
  default ObjIntRelation<T> negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Whether this {@code ObjIntRelation} is found to exist between the provided
   * {@code subject} and {@code object}.
   *
   * @param subject The subject of the relation (the entity from which the
   *     relationship extends)
   * @param object The object of the relation (the entity to which the
   *     relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, int object);

}
