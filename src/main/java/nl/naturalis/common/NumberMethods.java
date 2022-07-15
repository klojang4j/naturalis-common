package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;
import static nl.naturalis.common.TypeConversionException.targetTypeNotSupported;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * Methods for parsing, inspecting and converting {@code Number} instances. The focus
 * is on lossless conversion from one {@code Number} type to another {@code Number}
 * type.
 *
 * <p>NB For mathematical operations, see {@link MathMethods}.
 *
 * @author Ayco Holleman
 */
public final class NumberMethods {

  static final String UNSUPPORTED_NUMBER_TYPE = "unsupported Number type: {0}";

  private static final String NAN_OR_INFINITY = "NaN or Infinity";

  static Predicate<Number> yes() {return n -> true;}

  private static final Map<Class<? extends Number>, Function<Number, BigDecimal>>
      toBigDecimal =
      Map.of(
          BigDecimal.class, BigDecimal.class::cast,
          BigInteger.class, x -> new BigDecimal((BigInteger) x),
          Double.class, x -> new BigDecimal(Double.toString((double) x)),
          Float.class, x -> new BigDecimal(Float.toString((float) x)),
          Long.class, x -> new BigDecimal((Long) x),
          AtomicLong.class, x -> new BigDecimal(((AtomicLong) x).get()),
          Integer.class, x -> new BigDecimal((Integer) x),
          AtomicInteger.class, x -> new BigDecimal(((AtomicInteger) x).get()),
          Short.class, x -> new BigDecimal((Short) x),
          Byte.class, x -> new BigDecimal((Byte) x)
      );

  private static final Map<Class<?>, Function<String, Number>> parsers = Map.of(
      BigDecimal.class, NumberMethods::parseBigDecimal,
      BigInteger.class, NumberMethods::parseBigInteger,
      Double.class, NumberMethods::parseDouble,
      Float.class, NumberMethods::parseFloat,
      Long.class, NumberMethods::parseLong,
      AtomicLong.class, s -> new AtomicLong(parseLong(s)),
      Integer.class, NumberMethods::parseInt,
      AtomicInteger.class, s -> new AtomicInteger(parseInt(s)),
      Short.class, NumberMethods::parseShort,
      Byte.class, NumberMethods::parseByte);

  private static final Map<Class<?>, Predicate<String>> stringFitsInto = Map.of(
      BigDecimal.class, NumberMethods::isBigDecimal,
      BigInteger.class, NumberMethods::isBigInteger,
      Double.class, NumberMethods::isDouble,
      Float.class, NumberMethods::isFloat,
      Long.class, NumberMethods::isLong,
      AtomicLong.class, NumberMethods::isLong,
      Integer.class, NumberMethods::isInt,
      AtomicInteger.class, NumberMethods::isInt,
      Short.class, NumberMethods::isShort,
      Byte.class, NumberMethods::isByte);

  private static final Map<Class<?>, Predicate<Number>> numberFitsInto = Map.of(
      BigDecimal.class, ToBigDecimalConversion::isLossless,
      BigInteger.class, ToBigIntegerConversion::isLossless,
      Double.class, ToDoubleConversion::isLossless,
      Float.class, ToFloatConversion::isLossless,
      Long.class, ToLongConversion::isLossless,
      AtomicLong.class, ToLongConversion::isLossless,
      Integer.class, ToIntConversion::isLossless,
      AtomicInteger.class, ToIntConversion::isLossless,
      Short.class, ToShortConversion::isLossless,
      Byte.class, ToByteConversion::isLossless);

  private static final Map<Class<?>, UnaryOperator<Number>> converters = Map.of(
      BigDecimal.class, ToBigDecimalConversion::exec,
      BigInteger.class, ToBigIntegerConversion::exec,
      Double.class, ToDoubleConversion::exec,
      Float.class, ToFloatConversion::exec,
      Long.class, ToLongConversion::exec,
      AtomicLong.class, ToLongConversion::exec,
      Integer.class, ToIntConversion::exec,
      AtomicInteger.class, ToIntConversion::exec,
      Short.class, ToShortConversion::exec,
      Byte.class, ToByteConversion::exec);

  private static final Set<Class<? extends Number>> wrappers = Set.of(Byte.class,
      Short.class,
      Integer.class,
      Long.class,
      Float.class,
      Double.class);

  private static final Set<Class<? extends Number>> integrals = Set.of(Byte.class,
      Short.class,
      Integer.class,
      AtomicInteger.class,
      Long.class,
      AtomicLong.class,
      BigInteger.class);

  private NumberMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns {@code true} if the specified class is one of the standard primitive
   * number wrappers: {@code Byte}, {@code Short}, {@code Integer}, {@code Long},
   * {@code Float}, {@code Double}.
   *
   * @param numberType the class to test
   * @return whether the class is a primitive number wrapper
   * @see ClassMethods#isPrimitiveNumber(Class)
   */
  public static boolean isWrapper(Class<?> numberType) {
    Check.notNull(numberType);
    return wrappers.contains(numberType);
  }

  /**
   * Returns whether the specified number belongs to one of the primitive number
   * wrappers.
   *
   * @param number the number to test
   * @return whether the specified number belongs to one of the primitive number
   *     wrappers
   * @see #isWrapper(Class)
   */
  public static boolean isWrapper(Number number) {
    Check.notNull(number);
    return wrappers.contains(number.getClass());
  }

  /**
   * Returns {@code true} if the specified class is one of {@code Byte},
   * {@code Short}, {@code Integer}, {@code Long}, {@code BigInteger}.
   *
   * @param type the class to test
   * @return whether the class is an integral number type
   */
  public static boolean isIntegral(Class<?> type) {
    Check.notNull(type);
    return integrals.contains(type);
  }

  /**
   * Returns whether the specified number belongs is an integral number.
   *
   * @param number the number to test
   * @return whether the specified number belongs is an integral number
   * @see #isIntegral(Class)
   */
  public static boolean isIntegral(Number number) {
    Check.notNull(number);
    return integrals.contains(number.getClass());
  }

  /**
   * Parses the specified string into an {@code int}. This method delegates to
   * {@link BigDecimal#intValueExact()} and is therefore stricter than
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
    if (!isEmpty(s)) {
      try {
        return new BigDecimal(s).intValueExact();
      } catch (NumberFormatException | ArithmeticException e) {
        throw new TypeConversionException(s, int.class, e.toString());
      }
    }
    throw new TypeConversionException(s, int.class);
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
   * {@link BigDecimal#longValueExact()} and is therefore stricter than
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
    if (!isEmpty(s)) {
      try {
        return new BigDecimal(s).longValueExact();
      } catch (NumberFormatException | ArithmeticException e) {
        throw new TypeConversionException(s, long.class, e.toString());
      }
    }
    throw new TypeConversionException(s, long.class);
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
    if (!isEmpty(s)) {
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
    throw new TypeConversionException(s, double.class);
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
    if (!isEmpty(s)) {
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
    throw new TypeConversionException(s, float.class);
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
   * {@link BigDecimal#shortValueExact()} and is therefore stricter than
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
    if (!isEmpty(s)) {
      try {
        return new BigDecimal(s).shortValueExact();
      } catch (NumberFormatException | ArithmeticException e) {
        throw new TypeConversionException(s, short.class, e.toString());
      }
    }
    throw new TypeConversionException(s, short.class);
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
   * {@link BigDecimal#byteValueExact()} and is therefore stricter than
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
    if (!isEmpty(s)) {
      try {
        return new BigDecimal(s).byteValueExact();
      } catch (NumberFormatException | ArithmeticException e) {
        throw new TypeConversionException(s, byte.class, e.toString());
      }
    }
    throw new TypeConversionException(s, byte.class);
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
   * Parses the specified string into an {@code BigInteger}. This method delegates to
   * {@link BigDecimal#toBigIntegerExact()}. The {@link NumberFormatException} and
   * the {@link ArithmeticException} thrown from {@code intValueExact()} are both
   * converted to a {@link TypeConversionException}.
   *
   * @param s the string to be parsed
   * @return the {@code BigInteger} value represented by the string
   */
  public static BigInteger parseBigInteger(String s) {
    try {
      return new BigDecimal(s).toBigIntegerExact();
    } catch (NumberFormatException | ArithmeticException e) {
      throw new TypeConversionException(s, BigInteger.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code BigInteger}.
   * The argument is allowed to be {@code null}, in which case the return value will
   * be {@code false}.
   *
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a {@code BigInteger}
   */
  public static boolean isBigInteger(String s) {
    if (!isEmpty(s)) {
      try {
        parseBigInteger(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code Optional} if the specified string cannot be parsed into
   * BigInteger, else an {@code Optional} containing the {@code BigInteger} value
   * parsed out of the string.
   *
   * @param s the string to be parsed
   * @return an {@code Optional} containing the {@code BigInteger} value parsed out
   *     of the string
   */
  public static Optional<BigInteger> toBigInteger(String s) {
    if (!isEmpty(s)) {
      try {
        return Optional.of(parseBigInteger(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return Optional.empty();
  }

  /**
   * Parses the specified string into an {@code BigDecimal}. The
   * {@link NumberFormatException} potentially being thrown while the string is
   * parsed is converted to a {@link TypeConversionException}.
   *
   * @param s the string to be parsed
   * @return the {@code BigDecimal} value represented by the string
   */
  public static BigDecimal parseBigDecimal(String s) {
    try {
      return new BigDecimal(s);
    } catch (NumberFormatException e) {
      throw new TypeConversionException(s, BigDecimal.class, e.toString());
    }
  }

  /**
   * Returns whether the specified string can be parsed into an {@code BigDecimal}.
   * The argument is allowed to be {@code null}, in which case the return value will
   * be {@code false}.
   *
   * @param s the string to be parsed
   * @return whether he specified string can be parsed into a {@code BigDecimal}
   */
  public static boolean isBigDecimal(String s) {
    if (!isEmpty(s)) {
      try {
        parseBigDecimal(s);
        return true;
      } catch (TypeConversionException ignored) {
      }
    }
    return false;
  }

  /**
   * Returns an empty {@code Optional} if the specified string cannot be parsed into
   * BigDecimal, else an {@code Optional} containing the {@code BigDecimal} value
   * parsed out of the string.
   *
   * @param s the string to be parsed
   * @return an {@code Optional} containing the {@code BigDecimal} value parsed out
   *     of the string
   */
  public static Optional<BigDecimal> toBigDecimal(String s) {
    if (!isEmpty(s)) {
      try {
        return Optional.of(parseBigDecimal(s));
      } catch (TypeConversionException ignored) {
      }
    }
    return Optional.empty();
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
  @SuppressWarnings("unchecked")
  public static <T extends Number> T parse(String s, Class<T> targetType)
      throws TypeConversionException {
    Check.notNull(targetType, "targetType");
    Function<String, Number> parser = parsers.get(targetType);
    Check.that(parser).is(notNull(), () -> targetTypeNotSupported(s, targetType));
    return (T) parser.apply(s);
  }

  /**
   * Tests whether the specified string can be parsed into a {@code Number} of the
   * specified type.
   *
   * @param <T> the type of {@code Number} to convert the string to
   * @param s the string to be parsed
   * @param targetType the class of the {@code Number} type
   * @return whether the specified string can be parsed into a {@code Number} of the
   *     specified type
   */
  public static <T extends Number> boolean fitsInto(String s, Class<T> targetType) {
    if (!isEmpty(s)) {
      Predicate<String> tester = stringFitsInto.get(targetType);
      if (tester != null) {
        return tester.test(s);
      }
      throw targetTypeNotSupported(s, targetType);
    }
    return false;
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
    Function<Number, BigDecimal> fnc = toBigDecimal.get(n.getClass());
    if (fnc != null) {
      return fnc.apply(n);
    }
    throw inputTypeNotSupported(n, BigDecimal.class);
  }

  /**
   * Safely converts a number of an unspecified type to a number of a definite type.
   * Throws a {@link TypeConversionException} if the number cannot be converted to
   * the target type without loss of information, or if the number is a {@code Float}
   * or {@code Double} with value {@code NaN}, {@code POSITIVE_INFINITY} or
   * {@code NEGATIVE_INFINITY}. In other words, these values will <i>never</i> be
   * returned, even if the conversion happens to be a float-to-float or
   * double-to-double conversion. The number is allowed to be {@code null}, since
   * that value can be assigned to any {@code Number} type.
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
    Check.notNull(targetType, "targetType");
    if (number == null || number.getClass() == targetType) {
      return (R) number;
    } else if (number instanceof Double d && !Double.isFinite(d)) {
      throw nanOrInfinity(number, targetType);
    } else if (number instanceof Float f && !Float.isFinite(f)) {
      throw nanOrInfinity(number, targetType);
    }
    UnaryOperator<Number> converter = converters.get(targetType);
    if (converter != null) {
      return (R) converter.apply(number);
    }
    throw targetTypeNotSupported(number, targetType);
  }

  /**
   * Returns {@code true} if the specified number can be converted to the specified
   * target type without loss of information, and it is not equal to {@code NaN},
   * {@code POSITIVE_INFINITY} or {@code NEGATIVE_INFINITY}. The number is allowed to
   * be {@code null}, in which case {@code true} will be returned.
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
    Predicate<Number> tester = numberFitsInto.get(targetType);
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, targetType);
  }

  /**
   * Determines whether the specified float's fractional part is 0 or absent.
   *
   * @param f the {@code float} to inspect
   * @return whether the specified float's fractional part is 0 or absent
   */
  public static boolean isRound(float f) {
    return isRound(new BigDecimal(Float.toString(f)));
  }

  /**
   * Determines whether the specified double's fractional part is 0 or absent.
   *
   * @param d the {@code double} to inspect
   * @return whether the specified double's fractional part is 0 or absent
   */
  public static boolean isRound(double d) {
    return isRound(new BigDecimal(Double.toString(d)));
  }

  /**
   * Determines whether the specified BigDecimal's fractional part is 0 or absent.
   *
   * @param bd the {@code BigDecimal} to inspect
   * @return whether the specified BigDecimal's fractional part is 0 or absent
   */
  public static boolean isRound(BigDecimal bd) {
    if (bd.signum() == 0 || bd.scale() == 0) {
      return true;
    }
    try {
      bd.setScale(0, RoundingMode.UNNECESSARY);
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  static TypeConversionException nanOrInfinity(Number n, Class<?> targetType) {
    return new TypeConversionException(n, targetType, "NaN or Infinity");
  }

}
