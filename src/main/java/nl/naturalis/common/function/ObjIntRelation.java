package nl.naturalis.common.function;

/**
 * Verifies that an object has a certain relationship to an integer. For example, if object A is a
 * {@code Collection} with 6 elements, and y is the number 3, then the relation <i>x.size() &gt;
 * y</i> exists between these two values.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 */
@FunctionalInterface
public interface ObjIntRelation<T> {

  /**
   * Returns the negation of the specified relation.
   *
   * @param <X> The type of the subject of the original relation
   * @param relation The {@code Relation} to return the negation of
   * @return The negated {@code Relation}
   */
  public static <X> ObjIntRelation<X> not(ObjIntRelation<X> relation) {
    return (x, y) -> !relation.exists(x, y);
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The object from which the relationship extends
   * @param object The target of the relationship
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, int object);
}
