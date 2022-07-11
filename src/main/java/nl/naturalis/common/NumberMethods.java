package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.invoke.BigDecimalConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.check.Check.fail;
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

  static final String UNSUPPORTED_NUMBER_TYPE = "unsupported Number type: {0}";

  private NumberMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Parses the specified string into an {@code int}. This method delegates to
   * {@link BigInteger#intValueExact()} and is therefore stricter than
   * {@link Integer#parseInt(String)}. The {@link NumberFormatException} and the
   * {@link ArithmeticException} thrown from {@code intValueExact()} are both
   * converted to a {@link TypeConversionException}. Note that neither
   * {@code intValueExact()} nor this method {@link String#strip() strips} the string
   * before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code int} value represented by the string
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
   * @param s the string to be parsed
   * @return whether the specified string can be parsed into an {@code int} without
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
   * @param s the string to be parsed
   * @return an {@code OptionalInt} containing the {@code int} value parsed out of
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
   * Parses the specified string into a {@code long}. This method delegates to
   * {@link BigInteger#longValueExact()} and is therefore stricter than
   * {@link Long#parseLong(String)}. The {@link NumberFormatException} and the
   * {@link ArithmeticException} thrown from {@code longValueExact()} are both
   * converted to a {@link TypeConversionException}. Note that neither
   * {@code intValueExact()} nor this method {@link String#strip() strips} the string
   * before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code long} value represented by the string
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
   * @param s the string to be parsed
   * @return whether the specified string can be parsed into a {@code long} without
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
   * @param s the string to be parsed
   * @return an {@code OptionalLong} containing the {@code long} value parsed out of
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
   * into {@link Double#NaN}, {@link Double#POSITIVE_INFINITY} or
   * {@link Double#NEGATIVE_INFINITY}. Note that neither {@code doubleValue()} nor
   * this method {@link String#strip() strips} the string before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code double} value represented by the string
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
   * producing {@link Double#NaN}, {@link Double#POSITIVE_INFINITY} or
   * {@link Double#NEGATIVE_INFINITY}. The argument is allowed to be {@code null}, in
   * which case the return value will be {@code false}.
   *
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a regular, finite
   *     {@code double}
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
   * @param s the string to be parsed
   * @return an {@code OptionalDouble} containing the {@code double} value parsed out
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
   * into {@link Float#NaN}, {@link Float#POSITIVE_INFINITY} or
   * {@link Float#NEGATIVE_INFINITY}. Note that neither {@code floatValue()} nor this
   * method {@link String#strip() strips} the string before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code float} value represented by the string
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
   * producing {@link Float#NaN}, {@link Float#POSITIVE_INFINITY} or
   * {@link Float#NEGATIVE_INFINITY}. The argument is allowed to be {@code null}, in
   * which case the return value will be {@code false}.
   *
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a regular, finite
   *     {@code float}
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
   * @param s the string to be parsed
   * @return an {@code OptionalDouble} containing the {@code float} value parsed out
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
   * {@link BigInteger#shortValueExact()} and is therefore stricter than
   * {@link Short#parseShort(String)}. The {@link NumberFormatException} and the
   * {@link ArithmeticException} thrown from {@code shortValueExact()} are both
   * converted to a {@link TypeConversionException}. Note that neither
   * {@code shortValueExact()} nor this method {@link String#strip() strips} the
   * string before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code short} value represented by the string
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
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a {@code short} without
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
   * @param s the string to be parsed
   * @return an {@code OptionalInt} containing the {@code short} value parsed out of
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
   * Parses the specified string into a {@code byte}. This method delegates to
   * {@link BigInteger#byteValueExact()} and is therefore stricter than
   * {@link Byte#parseByte(String)}. The {@link NumberFormatException} and the
   * {@link ArithmeticException} thrown from {@code byteValueExact()} are both
   * converted to a {@link TypeConversionException}. Note that neither
   * {@code byteValueExact()} nor this method {@link String#strip() strips} the
   * string before parsing it.
   *
   * @param s the string to be parsed
   * @return the {@code byte} value represented by the string
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
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a {@code byte} without
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
   * @param s the string to be parsed
   * @return an {@code OptionalInt} containing the {@code byte} value parsed out of
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

  private static final Map<Class<?>, Function<Number, BigDecimal>> TO_BIG_DECIMAL =
      Map.of(
          BigDecimal.class, BigDecimal.class::cast,
          BigInteger.class, n -> new BigDecimal((BigInteger) n),
          Double.class, n -> BigDecimal.valueOf((double) n),
          Float.class, n -> BigDecimal.valueOf((float) n),
          Long.class, n -> new BigDecimal((Long) n),
          Integer.class, n -> new BigDecimal((Integer) n),
          Short.class, n -> new BigDecimal((Short) n),
          Byte.class, n -> new BigDecimal((Byte) n)
      );

  /**
   * Converts a {@code Number} of unspecified type to a {@code BigDecimal}. If the
   * number is a {@code Double} or a {@code Float}, it is converted by passing it
   * {@code BigDecimal.valueOf}, thus keeping its precision intact. Otherwise the
   * number is passed itself to the constructor of {@code BigDecimal}. This method
   * does not support the more "exotic" {@code Number} types, like
   * {@link AtomicLong}.
   *
   * @param n the number
   * @return the {@code BigDecimal} representing the number
   */
  public static BigDecimal toBigDecimal(Number n) {
    Check.notNull(n);
    Function<Number, BigDecimal> fnc = TO_BIG_DECIMAL.get(n.getClass());
    if (fnc != null) {
      return fnc.apply(n);
    }
    return fail(UNSUPPORTED_NUMBER_TYPE, n.getClass());
  }

  /**
   * Converts a number of an unspecified type to a number of a definite type. Throws
   * an {@link TypeConversionException} if the number is too big to fit into the
   * target type. This method does not support more "exotic" {@code Number} types,
   * like {@link AtomicLong}.
   *
   * @param <T> the type of the number to be converted
   * @param <U> The target type
   * @param number the number to be converted
   * @param targetType the class of the target type
   * @return an instance of the target type
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
   * @param <T> the type of {@code Number} to convert the string to
   * @param s the string to be parsed
   * @param targetType the class of the {@code Number} type
   * @return a {@code Number} of the specified type
   */
  public static <T extends Number> T parse(String s, Class<T> targetType)
      throws TypeConversionException {
    return new NumberParser<>(targetType).parse(s);
  }

  /**
   * Returns whether the specified {@code Number} can be converted into an instance
   * of the specified {@code Number} class without loss of information.
   *
   * @param <T> the type of {@code Number} to convert to
   * @param number the {@code Number} to convert
   * @param targetType the type of {@code Number} to convert to
   * @return whether conversion will be lossless
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
      return fitsIntoFloat(number);
    } else if (targetType == Long.class) {
      return fitsIntoLong(number);
    } else if (targetType == Integer.class) {
      return fitsIntoInt(number);
    } else if (targetType == Short.class) {
      return fitsIntoShort(number);
    }
    return fitsIntoByte(number);
  }

  private static final Map<Class<?>, Predicate<Number>> fitsIntoPredicates = Map.of(
      Double.class, x -> true,
      Float.class, NumberMethods::fitsIntoFloat,
      Long.class, NumberMethods::fitsIntoLong,
      AtomicLong.class, NumberMethods::fitsIntoLong,
      Integer.class, NumberMethods::fitsIntoInt,
      AtomicInteger.class, NumberMethods::fitsIntoInt,
      Short.class, NumberMethods::fitsIntoShort,
      Byte.class, NumberMethods::fitsIntoByte);

  private static boolean fitsIntoFloat(Number number) {
    Class<?> type = number.getClass();
    if (type == Double.class) {
      return (float) number.doubleValue() == number.doubleValue();
    }
    return true;
  }

  private static boolean fitsIntoLong(Number number) {
    Class<?> type = number.getClass();
    if (type == Double.class) {
      return (long) number.doubleValue() == number.doubleValue();
    } else if (type == Float.class) {
      return (long) number.floatValue() == number.floatValue();
    } else if (number instanceof BigDecimal bd) {
      return new BigDecimalConverter(bd).fitsInto(long.class);
    }
    return true;
  }

  private static boolean fitsIntoInt(Number number) {
    Class<?> type = number.getClass();
    if (type == Double.class) {
      return (int) number.doubleValue() == number.doubleValue();
    } else if (type == Float.class) {
      return (int) number.floatValue() == number.floatValue();
    } else if (type == Long.class) {
      return number.longValue() <= Integer.MAX_VALUE
          && number.longValue() >= Integer.MIN_VALUE;
    } else if (number instanceof BigDecimal bd) {
      return new BigDecimalConverter(bd).fitsInto(int.class);
    }
    return true;
  }

  private static boolean fitsIntoShort(Number number) {
    Class<?> type = number.getClass();
    if (type == Double.class) {
      return (short) number.doubleValue() == number.doubleValue();
    } else if (type == Float.class) {
      return (short) number.floatValue() == number.floatValue();
    } else if (type == Long.class) {
      return number.longValue() <= Short.MAX_VALUE
          && number.longValue() >= Short.MIN_VALUE;
    } else if (type == Integer.class) {
      return number.intValue() <= Short.MAX_VALUE
          && number.intValue() >= Short.MIN_VALUE;
    } else if (number instanceof BigDecimal bd) {
      return new BigDecimalConverter(bd).fitsInto(short.class);
    }
    return true;
  }

  private static boolean fitsIntoByte(Number number) {
    Class<?> type = number.getClass();
    if (type == Double.class) {
      return (byte) number.doubleValue() == number.doubleValue();
    } else if (type == Float.class) {
      return (byte) number.floatValue() == number.floatValue();
    } else if (type == Long.class) {
      return number.longValue() <= Byte.MAX_VALUE
          && number.longValue() >= Byte.MIN_VALUE;
    } else if (type == Integer.class) {
      return number.intValue() <= Byte.MAX_VALUE
          && number.intValue() >= Byte.MIN_VALUE;
    } else if (number instanceof BigDecimal bd) {

    }
    return number.shortValue() <= Byte.MAX_VALUE
        && number.shortValue() >= Byte.MIN_VALUE;
  }

}
