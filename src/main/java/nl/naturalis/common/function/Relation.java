package nl.naturalis.common.function;

import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.x.Generated;

/**
 * The {@code Relation} interface and its sister interfaces in this package assess
 * the presence of a relationship between two objects. The objects may or may not
 * have the same type. The object being assessed is called the <b>subject</b> of the
 * relationship, and the object that it is compared against is called the
 * <b>object</b> of the relationship. If the subject does indeed have the specified
 * relation to the object, the relation is said to <b>exist</b>. For example, if
 * object x is a {@code Collection} and object y is an element of it, then the
 * <b>contains</b> relation ({@code Collection::contains}) exists between x and y.
 * The {@link CommonChecks} class defines many small {@code Relation} implementations
 * (like {@code Collection::contains}) that you can use for argument validation.
 *
 * @param <T> the type of the subject of the relation
 * @param <U> the type of the object of the relation
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface Relation<T, U> {

  /**
   * Returns the converse of this relation, swapping subject and object in the
   * relationship. For example, the converse of <b>X contains Y</b> is <b>Y contains
   * X</b> (or <b>X is-element-of Y</b>).
   *
   * @return the converse of this {@code Relation}
   */
  @Generated
  default Relation<U, T> converse() {
    return (x, y) -> exists(y, x);
  }

  /**
   * Returns the negation of this {@code Relation}.
   *
   * @return the negation of this {@code Relation}
   */
  @Generated
  default Relation<T, U> negate() {
    return (x, y) -> !exists(x, y);
  }

  @Generated
  default Relation<T, U> or(U alternative) {
    return (x, y) -> Relation.this.exists(x, y)
        || Relation.this.exists(x, alternative);
  }

  /**
   * Whether this {@code Relation} is found to exist between the provided {@code
   * subject} and {@code object}.
   *
   * @param subject The value to test
   * @param object The value to test it against
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(T subject, U object);

}
