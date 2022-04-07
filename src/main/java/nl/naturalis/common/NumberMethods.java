package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.ObjectMethods.isNotEmpty;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for working with {@code Number} instances.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public final class NumberMethods {

  private static final int STRLEN_MAX_INT = String.valueOf(Integer.MAX_VALUE).length();
  private static final int STRLEN_MAX_SHORT = String.valueOf(Short.MAX_VALUE).length();

  private NumberMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns whether the specified string represents a valid integer. This method delegates to
   * {@link BigDecimal#intValueExact()} and is therefore stricter than {@link
   * Integer#parseInt(String)}.
   *
   * @param str The string
   * @return Whether it represents a valid integer
   */
  @SuppressWarnings({"ResultOfMethodCallIgnored"})
  public static boolean isInteger(String str) {
    if (!isEmpty(str)) {
      try {
        new BigInteger(str).intValueExact();
        return true;
      } catch (NumberFormatException | ArithmeticException ignored) {
      }
    }
    return false;
  }

  /**
   * Parses the specified string into an {@code Integer}. Throws an {@link TypeConversionException}
   * if the string is not a number or if the number is too big to fit into an {@code Integer}. This
   * method delegates to {@link BigDecimal#intValueExact()} and is therefore stricter than {@link
   * Integer#parseInt(String)}.
   *
   * @param s The string to be parsed
   * @return The {@code Integer} representation of the string
   */
  public static int parseInt(String s) throws TypeConversionException {
    try {
      return new BigInteger(s).intValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, int.class, e.getMessage());
    }
  }

  /**
   * Returns whether the specified string represents a plain, non-negative integer, consisting of
   * digits only, without plus or minus sign, without leading zeros, and fitting into a 32-bit
   * integer.
   *
   * @param s The string
   * @return Whether the specified string is a valid, digit-only integer
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static boolean isPlainInt(String s) {
    if (isEmpty(s) || s.length() > STRLEN_MAX_INT) {
      return false;
    } else if (s.charAt(0) == '0') {
      return s.length() == 1;
    }
    return s.codePoints().allMatch(Character::isDigit) && isInteger(s);
  }

  /**
   * Returns an empty {@code OptionalInt} if the specified string does not represent a {@link
   * #isPlainInt(String) plain integer}, else the integer parsed from the string.
   *
   * @param s The string
   * @return An {@code OptionalInt} plain, non-negative integer, consisting of digits only, without
   *     plus or minus sign, without leading zeros, and fitting into a 32-bit integer, or an empty
   *     {@code OptionalInt}
   */
  public static OptionalInt toPlainInt(String s) {
    if (isEmpty(s) || s.length() > STRLEN_MAX_INT) {
      return OptionalInt.empty();
    } else if (s.charAt(0) == '0') {
      return s.length() == 1
          ? OptionalInt.of(0)
          : OptionalInt.empty();
    }
    return s.codePoints().allMatch(Character::isDigit)
        ? OptionalInt.of(parseInt(s))
        : OptionalInt.empty();
  }

  /**
   * Returns whether the specified string represents a valid {@code short}.
   *
   * @param str The string
   * @return Whether the specified string represents a valid {@code short}.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static boolean isShort(String str) {
    if (!isEmpty(str)) {
      try {
        new BigInteger(str).shortValueExact();
        return true;
      } catch (NumberFormatException | ArithmeticException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns whether the specified string consists of digits only, without plus or minus sign,
   * without leading zeros, and fitting into a 16-bit integer.
   *
   * @param str The string
   * @return Whether the specified string is a valid, digit-only 16-but integer
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static boolean isPlainShort(String str) {
    if (isEmpty(str) || str.length() > STRLEN_MAX_SHORT) {
      return false;
    } else if (str.charAt(0) != '0') {
      return str.length() == 1;
    }
    return str.codePoints().allMatch(Character::isDigit) && isShort(str);
  }

  /**
   * Converts a {@code Number} of unspecified type to a {@code BigDecimal}.
   *
   * @param n The number
   * @return The {@code BigDecimal} representing the number
   */
  public static BigDecimal toBigDecimal(Number n) {
    Class<? extends Number> t = n.getClass();
    return t == BigDecimal.class
        ? (BigDecimal) n
        : t == BigInteger.class
            ? new BigDecimal((BigInteger) n)
            : t == Double.class
                ? BigDecimal.valueOf((Double) n)
                : t == Long.class
                    ? new BigDecimal((Long) n)
                    : t == Float.class
                        ? BigDecimal.valueOf((Float) n)
                        : new BigDecimal(n.intValue());
  }

  /**
   * Converts the specified number into a number of the specified type. Throws an {@link
   * TypeConversionException} if the number is too big to fit into the target type.
   *
   * @param <T> The type of the number to be converted
   * @param <U> The target type
   * @param number The number to be converted
   * @param targetType The class of the target type
   * @return An instance of the target type
   */
  public static <T extends Number, U extends Number> U convert(T number, Class<U> targetType) {
    return new NumberConverter<>(targetType).convert(number);
  }

  /**
   * Parses the specified string into a number of the specified type. Throws an {@link
   * TypeConversionException} if the string is not a number or if the number is too big to fit into
   * the target type.
   *
   * @param <T> The type of {@code Number} to convert the string to
   * @param s The string to be parsed
   * @param targetType The class of the {@code Number} type
   * @return A {@code Number} of the specified type
   */
  public static <T extends Number> T parse(String s, Class<T> targetType)
      throws TypeConversionException {
    return new NumberParser<>(targetType).parse(s);
  }

  /**
   * Returns whether the specified {@code Number} can be converted into an instance of the specified
   * {@code Number} class without loss of information.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param number The {@code Number} to convert
   * @param targetType The type of {@code Number} to convert to
   * @return Whether conversion will be lossless
   */
  public static <T extends Number> boolean fitsInto(Number number, Class<T> targetType) {
    Class<?> myType = Check.notNull(number, "number").isNot(instanceOf(), BigDecimal.class).isNot(
        instanceOf(),
        BigInteger.class).ok(Object::getClass);
    Check.notNull(targetType, "targetType").isNot(sameAs(), BigDecimal.class).isNot(sameAs(),
        BigInteger.class);
    if (myType == targetType || targetType == Double.class) {
      return true;
    } else if (targetType == Float.class) {
      if (myType == Double.class) {
        return (float) number.doubleValue() == number.doubleValue();
      }
      return true;
    } else if (targetType == Long.class) {
      if (myType == Double.class) {
        return (long) number.doubleValue() == number.doubleValue();
      } else if (myType == Float.class) {
        return (long) number.floatValue() == number.floatValue();
      }
      return true;
    } else if (targetType == Integer.class) {
      if (myType == Double.class) {
        return (int) number.doubleValue() == number.doubleValue();
      } else if (myType == Float.class) {
        return (int) number.floatValue() == number.floatValue();
      } else if (myType == Long.class) {
        return number.longValue() <= Integer.MAX_VALUE && number.longValue() >= Integer.MIN_VALUE;
      }
      return true;
    } else if (targetType == Short.class) {
      if (myType == Double.class) {
        return (short) number.doubleValue() == number.doubleValue();
      } else if (myType == Float.class) {
        return (short) number.floatValue() == number.floatValue();
      } else if (myType == Long.class) {
        return number.longValue() <= Short.MAX_VALUE && number.longValue() >= Short.MIN_VALUE;
      } else if (myType == Integer.class) {
        return number.intValue() <= Short.MAX_VALUE && number.intValue() >= Short.MIN_VALUE;
      }
      return true;
    }
    if (myType == Double.class) {
      return (byte) number.doubleValue() == number.doubleValue();
    } else if (myType == Float.class) {
      return (byte) number.floatValue() == number.floatValue();
    } else if (myType == Long.class) {
      return number.longValue() <= Byte.MAX_VALUE && number.longValue() >= Byte.MIN_VALUE;
    } else if (myType == Integer.class) {
      return number.intValue() <= Byte.MAX_VALUE && number.intValue() >= Byte.MIN_VALUE;
    }
    return number.shortValue() <= Byte.MAX_VALUE && number.shortValue() >= Byte.MIN_VALUE;
  }

  private static final Map<Class, UnaryOperator<? extends Number>> absFunctions =
      Map.of(Integer.class,
          n -> n.intValue() >= 0
              ? n
              : Integer.valueOf(-n.intValue()),
          Double.class,
          n -> n.doubleValue() >= 0
              ? n
              : Double.valueOf(-n.doubleValue()),
          Long.class,
          n -> n.longValue() >= 0
              ? n
              : Long.valueOf(-n.longValue()),
          Float.class,
          n -> n.floatValue() >= 0
              ? n
              : Float.valueOf(-n.floatValue()),
          Short.class,
          n -> n.shortValue() >= 0
              ? n
              : Short.valueOf((short) -n.shortValue()),
          Byte.class,
          n -> n.byteValue() >= 0
              ? n
              : Byte.valueOf((byte) -n.byteValue()),
          BigInteger.class,
          n -> ((BigInteger) n).abs(),
          BigDecimal.class,
          n -> ((BigDecimal) n).abs());

  /**
   * Returns the absolute value of an arbitrary type of number.
   *
   * @param <T> The type of the number
   * @param number The number
   * @return Its absolute value
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T abs(T number) {
    UnaryOperator op = Check.notNull(number).ok(n -> absFunctions.get(n.getClass()));
    return (T) op.apply(number);
  }

  /**
   * Returns whether {@code subject} lies within the specified range.
   *
   * @param subject The integer to test
   * @param lowerBoundInclusive The lower bound of the range (inclusive)
   * @param upperBoundExclusive The upper bound of the range (exclusive)
   * @return Whether {@code subject} lies within the specified range
   */
  public static boolean isBetween(int subject, int lowerBoundInclusive, int upperBoundExclusive) {
    return subject >= lowerBoundInclusive && subject < upperBoundExclusive;
  }

  /**
   * Returns whether {@code subject} lies within the specified range.
   *
   * @param subject The integer to test
   * @param lowerBoundInclusive The lower bound of the range (inclusive)
   * @param upperBoundInclusive The upper bound of the range (inclusive)
   * @return Whether {@code subject} lies within the specified range
   */
  public static boolean inRangeClosed(int subject,
      int lowerBoundInclusive,
      int upperBoundInclusive) {
    return subject >= lowerBoundInclusive && subject <= upperBoundInclusive;
  }

  /**
   * Returns the zero-based number (a.k.a. index) of the last page, given the specified row count
   * and page size (rows per page).
   *
   * @param rowCount The total number of rows (or elements) to divide up into pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The zero-based (a.k.a. index) number of the last page
   */
  public static int getLastPage(int rowCount, int pageSize) {
    Check.that(rowCount, "rowCount").isNot(negative());
    Check.that(pageSize, "pageSize").is(gt(), 0);
    if (rowCount == 0) {
      return 0;
    }
    return ((rowCount - 1) / pageSize);
  }

  /**
   * Returns the total number of pages required for the specified row count, given the specified
   * page size (rows per page). A.k.a. the number you should use as the {@code to} index in loops or
   * list operations.
   *
   * @param rowCount The total number of rows (or elements) to divide up into pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The total number of pages you need for the specified row count
   */
  public static int getPageCount(int rowCount, int pageSize) {
    return getLastPage(rowCount, pageSize) + 1;
  }

  /**
   * Returns the number of rows in the last page, given the specified row count and page size (rows
   * per page). That's just {@code rowCount % pageSize}.
   *
   * @param rowCount The total number of rows (or elements) to divide up into pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The number of rows in the last page
   */
  public static int rowsOnLastPage(int rowCount, int pageSize) {
    Check.that(rowCount, "rowCount").is(gte(), 0);
    Check.that(pageSize, "pageSize").is(gt(), 0);
    return rowCount % pageSize;
  }

  /**
   * Returns the number of empty rows in the last page.
   *
   * @param rowCount The total number of rows (or elements) to divide up into pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The number of unoccupied rows in the last page
   */
  public static int emptyRowsOnLastPage(int rowCount, int pageSize) {
    return pageSize - rowsOnLastPage(rowCount, pageSize);
  }

}
