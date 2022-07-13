package nl.naturalis.common;

import nl.naturalis.common.check.Check;

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
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;
import static nl.naturalis.common.TypeConversionException.targetTypeNotSupported;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.check.CommonChecks.subtypeOf;

/**
 * Methods for working with {@code Number} instances. Note that this class is about
 * casting, parsing and inspecting numbers. For mathematical operation on them, use
 * {@link MathMethods}.
 *
 * @author Ayco Holleman
 */
public final class NumberMethods {

  static final String UNSUPPORTED_NUMBER_TYPE = "unsupported Number type: {0}";

  private static final String NAN_OR_INFINITY = "NaN or Infinity";

  static Predicate<Number> yes() {return n -> true;}

  private static final Map<Class<? extends Number>, Function<Number, BigDecimal>> toBigDecimalConversions =
      Map.of(
          BigDecimal.class, BigDecimal.class::cast,
          BigInteger.class, x -> new BigDecimal((BigInteger) x),
          Double.class, x -> BigDecimal.valueOf((double) x),
          Float.class, x -> BigDecimal.valueOf((float) x),
          Long.class, x -> new BigDecimal((Long) x),
          AtomicLong.class, x -> new BigDecimal(((AtomicLong) x).get()),
          Integer.class, x -> new BigDecimal((Integer) x),
          AtomicInteger.class, x -> new BigDecimal(((AtomicInteger) x).get()),
          Short.class, x -> new BigDecimal((Short) x),
          Byte.class, x -> new BigDecimal((Byte) x)
      );

  private static final Map<Class<?>, Function<String, Number>> parsers = Map.of(
      Double.class, NumberMethods::parseDouble,
      Float.class, NumberMethods::parseFloat,
      Long.class, NumberMethods::parseLong,
      AtomicLong.class, s -> new AtomicLong(parseLong(s)),
      Integer.class, NumberMethods::parseInt,
      AtomicInteger.class, s -> new AtomicInteger(parseInt(s)),
      Short.class, NumberMethods::parseShort,
      Byte.class, NumberMethods::parseByte);

  private static final Map<Class<?>, Predicate<Number>> fitsIntoTests = Map.of(
      Double.class, FitsIntoDouble::test,
      Float.class, FitsIntoFloat::test,
      Long.class, FitsIntoLong::test,
      AtomicLong.class, FitsIntoLong::test,
      Integer.class, FitsIntoInt::test,
      AtomicInteger.class, FitsIntoInt::test,
      Short.class, FitsIntoShort::test,
      Byte.class, FitsIntoByte::test);

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
    throw new TypeConversionException(s, double.class, NAN_OR_INFINITY);
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
    throw new TypeConversionException(s, float.class, NAN_OR_INFINITY);
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
  public static byte parseByte(String s) throws TypeConversionException {
    if (isEmpty(s)) {
      throw new TypeConversionException(s, byte.class);
    }
    try {
      return new BigInteger(s).byteValueExact();
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
    Check.notNull(targetType, "targetType");
    Function<String, Number> parser = parsers.get(targetType);
    Check.that(parser).is(notNull(), () -> targetTypeNotSupported(s, targetType));
    return (T) parser.apply(s);
  }

  ////////////////////////////////////////////////////////////////////////////////
  //                            END OF PARSE METHOD                             //
  ////////////////////////////////////////////////////////////////////////////////

  /**
   * Converts a {@code Number} of an unspecified type to a {@code BigDecimal}.
   *
   * @param n the number
   * @return the {@code BigDecimal} representing the number
   */
  public static BigDecimal toBigDecimal(Number n) {
    Check.notNull(n);
    Function<Number, BigDecimal> fnc = toBigDecimalConversions.get(n.getClass());
    if (fnc != null) {
      return fnc.apply(n);
    }
    throw inputTypeNotSupported(n, BigDecimal.class);
  }

  /**
   * Converts a number of an unspecified type to a number of a definite type. Throws
   * an {@link TypeConversionException} if the number cannot be converted to the
   * target type without loss of information. In addition, a
   * {@code TypeConversionException} is thrown if the number is a {@code float} or
   * {@code Double} with value {@code NaN}, {@code POSITIVE_INFINITY} or
   * {@code NEGATIVE_INFINITY}. In other words, these values <i>always</i> result in
   * a {@code TypeConversionException}, even if the conversion turns out to be a
   * float-to-float or double-to-double conversion. The number is allowed to be
   * {@code null}, in which case {@code null} will be returned.
   *
   * @param <T> the input type
   * @param <R> the output type
   * @param number the number to be converted
   * @param targetType the class of the target type
   * @return an instance of the target type
   */
  @SuppressWarnings({"unchecked"})
  public static <T extends Number, R extends Number> R convert(T number,
      Class<R> targetType) throws TypeConversionException {
    Check.notNull(targetType, "targetType").is(subtypeOf(), Number.class);
    if (number == null || targetType.isInstance(number)) {
      return (R) number;
    } else if (number instanceof Double d && !Double.isFinite(d)) {
      throw nanOrInfinity(number, targetType);
    } else if (number instanceof Float f && !Float.isFinite(f)) {
      throw nanOrInfinity(number, targetType);
    }
    return BigDecimalConverter.convertTo(number, targetType);
  }

  /**
   * Returns whether the specified {@code Number} can be converted into an instance
   * of the specified {@code Number} class without loss of information. The number is
   * allowed to be {@code null}, in which case {@code true} will be returned.
   *
   * @param <T> the type of {@code Number} to convert to
   * @param number the {@code Number} to convert
   * @param targetType the type of {@code Number} to convert to
   * @return whether conversion will be lossless
   */
  public static <T extends Number> boolean fitsInto(Number number,
      Class<T> targetType) {
    Check.notNull(targetType, "targetType");
    if (number == null) {
      return true;
    }
    Predicate<Number> tester = fitsIntoTests.get(targetType);
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, targetType);
  }

  static TypeConversionException nanOrInfinity(Number n, Class<?> targetType) {
    return new TypeConversionException(n, targetType, NAN_OR_INFINITY);
  }

}
