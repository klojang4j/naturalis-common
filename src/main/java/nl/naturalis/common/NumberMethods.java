package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.math.BigDecimal;
import java.math.BigInteger;

import static nl.naturalis.common.StringMethods.isEmpty;
import static nl.naturalis.common.StringMethods.isNotEmpty;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for working with {@code Number} instances.
 *
 * @author Ayco Holleman
 */
public class NumberMethods {

  /**
   * Returns whether the specified string represents a valid integer. This method delegates to
   * {@link BigDecimal#intValueExact()} and is therefore stricter than {@link
   * Integer#parseInt(String)}.
   *
   * @param str The string
   * @return Whether the specified string represents a valid integer
   */
  public static boolean isInteger(String str) {
    if (isNotEmpty(str)) {
      try {
        new BigInteger(str).intValueExact();
        return true;
      } catch (NumberFormatException | ArithmeticException e) {
      }
    }
    return false;
  }

  /**
   * Returns whether the specified string represents a plain, non-negative integer, consisting of digits
   * only, without plus or minus sign, without leading zeros, and fitting into a 32-bit integer.
   *
   * @param str The string
   * @return Whether the specified string is a valid, digit-only integer
   */
  public static boolean isPlainInt(String str) {
    if (isEmpty(str)) {
      return false;
    } else if (str.charAt(0) == '0') {
      return str.length() == 1;
    } else if (str.codePoints().allMatch(Character::isDigit)) {
      try {
        new BigInteger(str).intValueExact();
        return true;
      } catch (ArithmeticException e) {
      }
    }
    return false;
  }

  /**
   * Returns whether the specified string represents a valid {@code short}.
   *
   * @param str The string
   * @return Whether the specified string represents a valid {@code short}.
   */
  public static boolean isShort(String str) {
    if (!isEmpty(str) && str.codePoints().allMatch(Character::isDigit)) {
      try {
        new BigInteger(str).shortValueExact();
        return true;
      } catch (NumberFormatException | ArithmeticException e) {
      }
    }
    return false;
  }

  /**
   * Returns whether the specified string consists of digits only, without plus or minus sign,
   * without leading zeros, and fitting into a 16-bit integer.
   *
   * @param str The string
   * @return Whether the specified string is a valid, digit-only integer
   */
  public static boolean isPlainShort(String str) {
    if (isEmpty(str)) {
      return false;
    } else if (str.charAt(0) == '+' || str.charAt(0) == '-') {
      return false;
    } else if (str.charAt(0) == '0') {
      return str.length() == 1;
    } else if (str.codePoints().allMatch(Character::isDigit)) {
      try {
        new BigInteger(str).shortValueExact();
        return true;
      } catch (ArithmeticException e) {
      }
    }
    return false;
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
                ? new BigDecimal((Double) n)
                : t == Long.class
                    ? new BigDecimal((Long) n)
                    : t == Float.class ? new BigDecimal((Float) n) : new BigDecimal(n.intValue());
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
   * Parses the specified string into an {@code Integer}. Throws an {@link TypeConversionException}
   * if the string is not a number or if the number is too big to fit into an {@code Integer}. This
   * method delegates to {@link BigDecimal#intValueExact()} and is therefore stricter than {@link
   * Integer#parseInt(String)}.
   *
   * @param s The string to be parsed
   * @return The {@code Integer} representation of the string
   */
  public static Integer parseInt(String s) throws TypeConversionException {
    return parse(s, Integer.class);
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
    Class<?> myType = Check.notNull(number, "number").ok(Object::getClass);
    Check.notNull(targetType, "targetType");
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
      } else if (myType == Long.class) {
        return number.longValue() <= Integer.MAX_VALUE && number.longValue() >= Integer.MIN_VALUE;
      } else if (myType == Float.class) {
        return (int) number.floatValue() == number.floatValue();
      }
      return true;
    } else if (targetType == Short.class) {
      if (myType == Double.class) {
        return (short) number.doubleValue() == number.doubleValue();
      } else if (myType == Long.class) {
        return number.longValue() <= Short.MAX_VALUE && number.longValue() >= Short.MIN_VALUE;
      } else if (myType == Float.class) {
        return (short) number.floatValue() == number.floatValue();
      } else if (myType == Integer.class) {
        return number.intValue() <= Short.MAX_VALUE && number.intValue() >= Short.MIN_VALUE;
      }
      return true;
    } else /* Byte.class */ {
      if (myType == Double.class) {
        return (byte) number.doubleValue() == number.doubleValue();
      } else if (myType == Long.class) {
        return number.longValue() <= Byte.MAX_VALUE && number.longValue() >= Byte.MIN_VALUE;
      } else if (myType == Float.class) {
        return (byte) number.floatValue() == number.floatValue();
      } else if (myType == Integer.class) {
        return number.intValue() <= Byte.MAX_VALUE && number.intValue() >= Byte.MIN_VALUE;
      } else if (myType == Short.class) {
        return number.shortValue() <= Byte.MAX_VALUE && number.shortValue() >= Byte.MIN_VALUE;
      }
      return true;
    }
  }

  /**
   * Returns the absolute value of the specified number.
   *
   * @param <T> The type of the number
   * @param number The number
   * @return Its absolute value
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T abs(T number) {
    Check.notNull(number);
    if (number.getClass() == Integer.class) {
      return number.intValue() >= 0 ? number : (T) Integer.valueOf(-number.intValue());
    } else if (number.getClass() == Long.class) {
      return number.longValue() >= 0 ? number : (T) Long.valueOf(-number.longValue());
    } else if (number.getClass() == Double.class) {
      return number.doubleValue() >= 0 ? number : (T) Double.valueOf(-number.doubleValue());
    } else if (number.getClass() == Float.class) {
      return number.floatValue() >= 0 ? number : (T) Float.valueOf(-number.floatValue());
    } else if (number.getClass() == Short.class) {
      return number.shortValue() >= 0 ? number : (T) Short.valueOf((short) -number.shortValue());
    }
    return number.byteValue() >= 0 ? number : (T) Byte.valueOf((byte) -number.byteValue());
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
  public static boolean inRangeClosed(
      int subject, int lowerBoundInclusive, int upperBoundInclusive) {
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

  private NumberMethods() {}
}
