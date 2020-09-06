package nl.naturalis.common.function;

/**
 * Verifies that two integers have a certain relationship to each other. For example, if A equals 5
 * B equals 3, then the relationship <i>X greater than Y</i> exists between these integers. (An
 * equally appropriate name would be {@code IntBiPredicate}, an interface not present in the {@code
 * java.util.function} package}.)
 *
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface IntRelation {

  /** The <i>equals</i> relationship */
  public static final IntRelation EQUALS = (arg0, arg1) -> arg0 == arg1;
  /** The <i>not-equals</i> relationship */
  public static final IntRelation NOT_EQUALS = (arg0, arg1) -> arg0 != arg1;
  /** The <i>greater-than</i> relationship */
  public static final IntRelation GT = (arg0, arg1) -> arg0 > arg1;
  /** The <i>greater-than-or-equal-to</i> relationship */
  public static final IntRelation GTE = (arg0, arg1) -> arg0 >= arg1;
  /** The <i>less-than</i> relationship */
  public static final IntRelation LT = (arg0, arg1) -> arg0 < arg1;
  /** The <i>less-than-or-equal-to</i> relationship */
  public static final IntRelation LTE = (arg0, arg1) -> arg0 <= arg1;
  /** The <i>divisible-by</i> relationship */
  public static final IntRelation MULTIPLE_OF = (arg0, arg1) -> arg0 % arg1 == 0;
  /** The <i>divisor-of</i> relationship */
  public static final IntRelation DIVISOR_OF = (arg0, arg1) -> arg1 % arg0 == 0;

  /**
   * Whether or not the relationship between {@code subject} and {@code object} exists.
   *
   * @param subject The integer from which the relationship extends
   * @param object The target of the relationship
   * @return
   */
  boolean existsAsInt(int subject, int object);
}
