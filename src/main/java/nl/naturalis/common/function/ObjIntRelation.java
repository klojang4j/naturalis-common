package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between an object and an integer. For example, if x is a {@code
 * Collection} with 6 elements, and y equals 3, then the relation <b>X has-size-greater-than Y</b>
 * exists between x and y.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 */
@FunctionalInterface
public interface ObjIntRelation<T> {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship.
   *
   * @return An {@code IntObjRelation} that is the converse of this {@code ObjIntRelation}.
   */
  default IntObjRelation<T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of the this {@code ObjIntRelation}.
   *
   * @return The negation of the this {@code ObjIntRelation}
   */
  default ObjIntRelation<T> negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The subject of the relation (the entity from which the relationship extends)
   * @param object The object of the relation (the entity to which the relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, int object);
}
