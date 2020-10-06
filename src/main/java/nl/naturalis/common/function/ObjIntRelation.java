package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between an object and an integer. For example, if x is a {@code
 * Collection} with 6 elements, and y equals 3, then the relation <i>X.size() &gt; Y</i> exists
 * between these two values.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 */
@FunctionalInterface
public interface ObjIntRelation<T> {

  /**
   * Returns the reverse of the specified relation, swapping subject and object in the relationship.
   *
   * @param <X> The type of the object of the specified {@code ObjIntRelation} (and the subject of
   *     the returned {@code IntObjRelation})
   * @param relation The {@code IntObjRelation} to return the reverse of
   * @return An {@code ObjIntRelation} that is the reverse of the specified {@code IntObjRelation}.
   */
  public static <X> IntObjRelation<X> reverse(ObjIntRelation<X> relation) {
    return (y, x) -> relation.exists(x, y);
  }

  /**
   * Returns the negation of the specified relation.
   *
   * @param <X> The type of the subject of the original relation
   * @param relation The {@code ObjIntRelation} to return the negation of
   * @return The negated {@code ObjIntRelation}
   */
  public static <X> ObjIntRelation<X> not(ObjIntRelation<X> relation) {
    return (x, y) -> !relation.exists(x, y);
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
