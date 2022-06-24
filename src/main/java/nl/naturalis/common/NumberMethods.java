package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for working with {@code Number} instances. Note that this class is about
 * casting, parsing and inspecting numbers. For mathematical operation on them, use
 * {@link MathMethods}.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public final class NumberMethods {

  private NumberMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Parses the specified string into an {@code int}. This method delegates to {@link
   * BigInteger#intValueExact()} and is therefore stricter than {@link
   * Integer#parseInt(String)}. The {@link NumberFormatException} and the {@link
   * ArithmeticException} thrown from {@code intValueExact()} are both converted to a
   * {@link TypeConversionException}. Note that neither {@code intValueExact()} nor
   * this method {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code int} value represented by the string
   */
  public static int parseInt(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, int.class);
    }
    try {
      return new BigInteger(s).intValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, int.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code int} without
   * causing integer overflow. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether the specified string can be parsed into an {@code int} without
   *     causing integer overflow
   */
  public static boolean isInt(String s) {
    if (!isEmpty(s)) {
      try {
        parseInt(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalInt} if the specified string cannot be parsed
   * into a 32-bit integer, else an {@code OptionalInt} containing the {@code int}
   * value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalInt} containing the {@code int} value parsed out of
   *     the string
   */
  public static OptionalInt toInt(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalInt.of(parseInt(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalInt.empty();
  }

  /**
   * Parses the specified string into a {@code long}. This method delegates to {@link
   * BigInteger#longValueExact()} and is therefore stricter than {@link
   * Long#parseLong(String)}. The {@link NumberFormatException} and the {@link
   * ArithmeticException} thrown from {@code longValueExact()} are both converted to
   * a {@link TypeConversionException}. Note that neither {@code intValueExact()} nor
   * this method {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code long} value represented by the string
   */
  public static long parseLong(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, long.class);
    }
    try {
      return new BigInteger(s).longValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, long.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code long} without
   * causing integer overflow. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether the specified string can be parsed into a {@code long} without
   *     causing integer overflow
   */
  public static boolean isLong(String s) {
    if (!isEmpty(s)) {
      try {
        parseLong(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalLong} if the specified string cannot be parsed
   * into a 64-bit integer, else an {@code OptionalLong} containing the {@code long}
   * value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalLong} containing the {@code long} value parsed out of
   *     the string
   */
  public static OptionalLong toLong(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalLong.of(parseLong(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalLong.empty();
  }

  /**
   * Parses the specified string into a {@code double}. This method delegates to
   * {@link BigInteger#doubleValue()}. The {@link NumberFormatException} thrown from
   * {@code doubleValue()} is converted to a {@link TypeConversionException}. This
   * method also throws a {@code TypeConversionException} if the string is parsed
   * into {@link Double#NaN}, {@link Double#POSITIVE_INFINITY} or {@link
   * Double#NEGATIVE_INFINITY}. Note that neither {@code doubleValue()} nor this
   * method {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code double} value represented by the string
   */
  public static double parseDouble(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, double.class);
    }
    try {
      double d;
      if (Double.isFinite(d = new BigDecimal(s).doubleValue())) {
        return d;
      }
    } catch (NumberFormatException e) {
      throw new TypeConversionException(s, double.class, e.toString());
    }
    throw new TypeConversionException(s, double.class, "NaN or Infinity");
  }

  /**
   * Returns whether the specified string can be parsed into a {@code double} without
   * producing {@link Double#NaN}, {@link Double#POSITIVE_INFINITY} or {@link
   * Double#NEGATIVE_INFINITY}. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether he specified string can be parsed into a regular, finite {@code
   *     double}
   */
  public static boolean isDouble(String s) {
    if (!isEmpty(s)) {
      try {
        parseDouble(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalDouble} if the specified string cannot be parsed
   * into a regular, finite {@code double} value, else an {@code OptionalDouble}
   * containing the {@code double} value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalDouble} containing the {@code double} value parsed out
   *     of the string
   */
  public static OptionalDouble toDouble(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalDouble.of(parseDouble(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalDouble.empty();
  }

  /**
   * Parses the specified string into a {@code float}. This method delegates to
   * {@link BigInteger#floatValue()}. The {@link NumberFormatException} thrown from
   * {@code floatValue()} is converted to a {@link TypeConversionException}. This
   * method also throws a {@code TypeConversionException} if the string is parsed
   * into {@link Float#NaN}, {@link Float#POSITIVE_INFINITY} or {@link
   * Float#NEGATIVE_INFINITY}. Note that neither {@code floatValue()} nor this method
   * {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code float} value represented by the string
   */
  public static float parseFloat(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, float.class);
    }
    try {
      float d;
      if (Float.isFinite(d = new BigDecimal(s).floatValue())) {
        return d;
      }
    } catch (NumberFormatException e) {
      throw new TypeConversionException(s, float.class, e.toString());
    }
    throw new TypeConversionException(s, float.class, "NaN or Infinity");
  }

  /**
   * Returns whether the specified string can be parsed into a {@code float} without
   * producing {@link Float#NaN}, {@link Float#POSITIVE_INFINITY} or {@link
   * Float#NEGATIVE_INFINITY}. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether he specified string can be parsed into a regular, finite {@code
   *     float}
   */
  public static boolean isFloat(String s) {
    if (!isEmpty(s)) {
      try {
        parseFloat(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalDouble} if the specified string cannot be parsed
   * into a regular, finite {@code float} value, else an {@code OptionalDouble}
   * containing the {@code float} value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalDouble} containing the {@code float} value parsed out
   *     of the string
   */
  public static OptionalDouble toFloat(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalDouble.of(parseFloat(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalDouble.empty();
  }

  /**
   * Parses the specified string into a {@code short}. This method delegates to
   * {@link BigInteger#shortValueExact()} and is therefore stricter than {@link
   * Short#parseShort(String)}. The {@link NumberFormatException} and the {@link
   * ArithmeticException} thrown from {@code shortValueExact()} are both converted to
   * a {@link TypeConversionException}. Note that neither {@code shortValueExact()}
   * nor this method {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code short} value represented by the string
   */
  public static short parseShort(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, short.class);
    }
    try {
      return new BigInteger(s).shortValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, short.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code short} without
   * causing integer overflow. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether he specified string can be parsed into a {@code short} without
   *     causing integer overflow
   */
  public static boolean isShort(String s) {
    if (!isEmpty(s)) {
      try {
        parseShort(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalInt} if the specified string cannot be parsed
   * into a 16-bit integer, else an {@code OptionalInt} containing the {@code short}
   * value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalInt} containing the {@code short} value parsed out of
   *     the string
   */
  public static OptionalInt toShort(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalInt.of(parseShort(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalInt.empty();
  }

  /**
   * Parses the specified string into a {@code byte}. This method delegates to {@link
   * BigInteger#byteValueExact()} and is therefore stricter than {@link
   * Byte#parseByte(String)}. The {@link NumberFormatException} and the {@link
   * ArithmeticException} thrown from {@code byteValueExact()} are both converted to
   * a {@link TypeConversionException}. Note that neither {@code byteValueExact()}
   * nor this method {@link String#strip() strips} the string before parsing it.
   *
   * @param s The string to be parsed
   * @return The {@code byte} value represented by the string
   */
  public static int parseByte(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, byte.class);
    }
    try {
      return new BigInteger(s).shortValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, byte.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code byte} without
   * causing integer overflow. The argument is allowed to be {@code null}, in which
   * case the return value will be {@code false}.
   *
   * @param s The string to be parsed
   * @return Whether he specified string can be parsed into a {@code byte} without
   *     causing an integer overflow
   */
  public static boolean isByte(String s) {
    if (!isEmpty(s)) {
      try {
        parseByte(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code OptionalInt} if the specified string cannot be parsed
   * into an 8-bit integer, else an {@code OptionalInt} containing the {@code byte}
   * value parsed out of the string.
   *
   * @param s The string to be parsed
   * @return An {@code OptionalInt} containing the {@code byte} value parsed out of
   *     the string
   */
  public static OptionalInt toByte(String s) {
    if (!isEmpty(s)) {
      try {
        return OptionalInt.of(parseByte(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return OptionalInt.empty();
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
   * Converts the specified number into a number of the specified type. Throws an
   * {@link TypeConversionException} if the number is too big to fit into the target
   * type.
   *
   * @param <T> The type of the number to be converted
   * @param <U> The target type
   * @param number The number to be converted
   * @param targetType The class of the target type
   * @return An instance of the target type
   */
  public static <T extends Number, U extends Number> U convert(T number,
      Class<U> targetType) {
    return new NumberConverter<>(targetType).convert(number);
  }

  /**
   * Parses the specified string into a number of the specified type. Throws an
   * {@link TypeConversionException} if the string is not a number or if the number
   * is too big to fit into the target type.
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
   * Returns whether the specified {@code Number} can be converted into an instance
   * of the specified {@code Number} class without loss of information.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param number The {@code Number} to convert
   * @param targetType The type of {@code Number} to convert to
   * @return Whether conversion will be lossless
   */
  public static <T extends Number> boolean fitsInto(Number number,
      Class<T> targetType) {
    Class<?> myType = Check.notNull(number, "number")
        .isNot(instanceOf(), BigDecimal.class)
        .isNot(instanceOf(), BigInteger.class)
        .ok(Object::getClass);
    Check.notNull(targetType, "targetType")
        .isNot(sameAs(), BigDecimal.class)
        .isNot(sameAs(), BigInteger.class);
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
        return number.longValue() <= Integer.MAX_VALUE
            && number.longValue() >= Integer.MIN_VALUE;
      }
      return true;
    } else if (targetType == Short.class) {
      if (myType == Double.class) {
        return (short) number.doubleValue() == number.doubleValue();
      } else if (myType == Float.class) {
        return (short) number.floatValue() == number.floatValue();
      } else if (myType == Long.class) {
        return number.longValue() <= Short.MAX_VALUE
            && number.longValue() >= Short.MIN_VALUE;
      } else if (myType == Integer.class) {
        return number.intValue() <= Short.MAX_VALUE
            && number.intValue() >= Short.MIN_VALUE;
      }
      return true;
    }
    if (myType == Double.class) {
      return (byte) number.doubleValue() == number.doubleValue();
    } else if (myType == Float.class) {
      return (byte) number.floatValue() == number.floatValue();
    } else if (myType == Long.class) {
      return number.longValue() <= Byte.MAX_VALUE
          && number.longValue() >= Byte.MIN_VALUE;
    } else if (myType == Integer.class) {
      return number.intValue() <= Byte.MAX_VALUE
          && number.intValue() >= Byte.MIN_VALUE;
    }
    return number.shortValue() <= Byte.MAX_VALUE
        && number.shortValue() >= Byte.MIN_VALUE;
  }

  private static final Map<Class, UnaryOperator<? extends Number>> absFunctions = Map.of(
      Integer.class,
      n -> n.intValue() >= 0 ? n : Integer.valueOf(-n.intValue()),
      Double.class,
      n -> n.doubleValue() >= 0 ? n : Double.valueOf(-n.doubleValue()),
      Long.class,
      n -> n.longValue() >= 0 ? n : Long.valueOf(-n.longValue()),
      Float.class,
      n -> n.floatValue() >= 0 ? n : Float.valueOf(-n.floatValue()),
      Short.class,
      n -> n.shortValue() >= 0 ? n : Short.valueOf((short) -n.shortValue()),
      Byte.class,
      n -> n.byteValue() >= 0 ? n : Byte.valueOf((byte) -n.byteValue()),
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
  public static boolean isBetween(int subject,
      int lowerBoundInclusive,
      int upperBoundExclusive) {
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
   * Returns the zero-based number (a.k.a. index) of the last page, given the
   * specified row count and page size (rows per page).
   *
   * @param rowCount The total number of rows (or elements) to divide up into
   *     pages (or slices)
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
   * Returns the total number of pages required for the specified row count, given
   * the specified page size (rows per page). A.k.a. the number you should use as the
   * {@code to} index in loops or list operations.
   *
   * @param rowCount The total number of rows (or elements) to divide up into
   *     pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The total number of pages you need for the specified row count
   */
  public static int getPageCount(int rowCount, int pageSize) {
    return getLastPage(rowCount, pageSize) + 1;
  }

  /**
   * Returns the number of rows in the last page, given the specified row count and
   * page size (rows per page). That's just {@code rowCount % pageSize}.
   *
   * @param rowCount The total number of rows (or elements) to divide up into
   *     pages (or slices)
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
   * @param rowCount The total number of rows (or elements) to divide up into
   *     pages (or slices)
   * @param pageSize The maximum number of rows per page (or elements per slice)
   * @return The number of unoccupied rows in the last page
   */
  public static int emptyRowsOnLastPage(int rowCount, int pageSize) {
    return pageSize - rowsOnLastPage(rowCount, pageSize);
  }

}
