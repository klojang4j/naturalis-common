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
   * Returns the reverse of the specified relation, swapping subject and object in the relationship.
   *
   * @param <X> The type of the object of the original relation (and the subject of the returned
   *     {@code ObjIntRelation})
   * @param relation The {@code IntObjRelation} to return the reverse of
   * @return An {@code ObjIntRelation} that is the reverse of the specified {@code IntObjRelation}.
   */
  public static <X> ObjIntRelation<X> reverse(IntObjRelation<X> relation) {
    return (y, x) -> relation.exists(x, y);
  }

  /**
   * Returns the negation of the specified relation.
   *
   * @param <X> The type of the object of the original relation
   * @param relation The {@code IntObjRelation} to return the negation of
   * @return The negated {@code IntObjRelation}
   */
  public static <X> IntObjRelation<X> not(IntObjRelation<X> relation) {
    return (y, x) -> !relation.exists(y, x);
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The subject of the relation (the entity from which the relationship extends)
   * @param object The object of the relation (the entity to which the relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(int subject, T object);
}
