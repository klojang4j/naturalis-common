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
   * Returns the reverse of the specified relation, swapping subject and object in the relationship.
   * For example, the reverse of <i>X &gt; Y</i> is <i>Y &gt; X</i> (or <i>X &lt;= Y</i>).
   *
   * @param relation The {@code Relation} to return the reverse of
   * @return The reverse {@code Relation}
   */
  public static IntRelation reverse(IntRelation relation) {
    return (x, y) -> relation.exists(y, x);
  }

  /**
   * Returns the negation of the specified relation. For example, the negation of <i>X &gt; Y</i> is
   * <i>X &lt;= Y</i>.
   *
   * @param relation The {@code Relation} to return the negation of
   * @return The negated {@code Relation}
   */
  public static IntRelation not(IntRelation relation) {
    return (x, y) -> !relation.exists(x, y);
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
