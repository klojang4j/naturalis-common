package nl.naturalis.common.function;

/**
 * Verifies that two objects have a certain relationship to each other. For example, if object A is
 * a {@code Collection} and object B is an element of it, then the relationship <i>x contains y</i>
 * exists between these objects.
 *
 * @author Ayco Holleman
 * @param <T> The type of the subject of the relation
 * @param <U> The type of the object of the relation
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the reverse of the specified relation, swapping subject ans object in the relation. For
   * example, the reverse of <i>X contains Y</i> is <i>y contains y</i> (or <i>X element-of Y</i> )
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
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The object from which the relationship extends
   * @param object The target of the relationship
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);
}
