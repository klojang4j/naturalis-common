package nl.naturalis.common.function;

/**
 * Defines some kind of relationship between two integers. For example, if x equals 5 and y equals
 * 3, then the relation <i>X greater than Y</i> exists between these integers.
 *
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface IntRelation {

  /**
   * Returns the converse of this relation, swapping subject and object in the relationship. For
   * example, the converse of <i>X &gt; Y</i> is <i>Y &gt; X</i> (or <i>X &lt;= Y</i>).
   *
   * @return The converse of this {@code IntRelation}
   */
  default IntRelation converse() {
    return (x, y) -> exists(x, y);
  }

  /**
   * Returns the negation of this {@code IntRelation}. For example, the negation of <i>X &gt; Y</i>
   * is <i>X &lt;= Y</i>.
   *
   * @return The negation of this {@code IntRelation}
   */
  default IntRelation negate() {
    return (x, y) -> !exists(x, y);
  }

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The integer from which the relationship extends
   * @param object The target of the relationship
   * @return {@code true} if the relation exists, {@code false} otherwise.
   */
  boolean exists(int subject, int object);
}
