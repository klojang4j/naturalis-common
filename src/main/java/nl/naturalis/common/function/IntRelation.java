package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between two integers. For example, if x equals 5 and y equals
 * 3, then the relation <b>X is-greater-than Y</b> exists between x and y.
 *
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface IntRelation {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship. For
   * example, the converse of <b>X &gt; Y</b> is <b>Y &gt; X</b> (or <b>X &lt;= Y</b>).
   *
   * @return The converse of this {@code IntRelation}
   */
  default IntRelation converse() {
    return (x, y) -> exists(x, y);
  }

  /**
   * Returns the negation of this {@code IntRelation}. For example, the negation of <b>X &gt; Y</b>
   * is <b>X &lt;= Y</b>.
   *
   * @return The negation of this {@code IntRelation}
   */
  default IntRelation negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Whether the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The value to test
   * @param object The value to test it against
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(int subject, int object);
}
