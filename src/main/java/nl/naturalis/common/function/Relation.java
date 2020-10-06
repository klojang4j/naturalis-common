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
   * Returns the reverse of the specified relation, swapping subject and object in the relationship.
   * For example, the reverse of <i>X contains Y</i> is <i>Y contains X</i> (or <i>X element-of
   * Y</i>).
   *
   * @param <X> The type of the subject of the original relation (and the object of the returned
   *     {@code Relation})
   * @param <Y> The type of the object of the original relation (and the subject of the returned
   *     {@code Relation})
   * @param relation The {@code Relation} to return the reverse of
   * @return The reverse {@code Relation}
   */
  public static <X, Y> Relation<Y, X> reverse(Relation<X, Y> relation) {
    return (x, y) -> relation.exists(y, x);
  }

  /**
   * Returns the negation of the specified relation.
   *
   * @param <X> The type of the subject of the specified {@code Relation}
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
   * @param subject The subject of the relation (the entity from which the relationship extends)
   * @param object The object of the relation (the entity to which the relationship extends)
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);
}
