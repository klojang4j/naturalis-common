package nl.naturalis.common.function;

/**
 * The {@code Relation} interface and its sister interfaces let you implement
 * well-defined relationships between two objects. The objects may or may not have
 * the same type. The object that you want to assert something about is called the
 * <i>subject</i> of the relationship, and the object that it is compared against is
 * called the <i>object</i> of the relationship. If the subject does indeed have the
 * specified relation to the object, the relation is said to <i>exist</i>. For
 * example, if object x is a {@code Collection} and object y is an element of it,
 * then the <i>contains</i> relation ({@code Collection::contains}) exists between x
 * and y. The {@link nl.naturalis.common.check.CommonChecks} class defines a lot of
 * small {@code Relation} implementations (like {@code Collection::contains}) that
 * you can use for argument validation.
 *
 * @param <T> The type of the subject of the relation
 * @param <U> The type of the object of the relation
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the converse of this relation, swapping subject and object in the
   * relationship. For example, the converse of <b>X contains Y</b> is <b>Y contains
   * X</b> (or <b>X is-element-of Y</b>).
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

  default Relation<T, U> or(U alternative) {
    return (x, y) -> Relation.this.exists(x, y)
        || Relation.this.exists(x, alternative);
  }

  /**
   * Whether the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The value to test
   * @param object The value to test it against
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);

}
