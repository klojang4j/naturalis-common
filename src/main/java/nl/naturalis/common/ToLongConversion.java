package nl.naturalis.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.NumberMethods.*;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;

class ToLongConversion {

  static final BigDecimal BIG_MIN_LONG = new BigDecimal(Long.MIN_VALUE);
  static final BigDecimal BIG_MAX_LONG = new BigDecimal(Long.MAX_VALUE);

  private static final Map<Class<?>, Predicate<Number>> fitsIntoLong = Map.of(
      BigDecimal.class, ToLongConversion::testBigDecimal,
      BigInteger.class, ToLongConversion::testBigInteger,
      Double.class, n -> n.doubleValue() == n.longValue(),
      //Double.class, FitsIntoLong::testDouble,
      Float.class, n -> n.floatValue() == n.longValue(),
      //Float.class, FitsIntoLong::testFloat,
      Long.class, yes(),
      AtomicLong.class, yes(),
      Integer.class, yes(),
      AtomicInteger.class, yes(),
      Short.class, yes(),
      Byte.class, yes()
  );

  static boolean isLossless(Number n) {
    Predicate<Number> tester = fitsIntoLong.get(n.getClass());
    if (tester != null) {
      return tester.test(n);
    }
    throw inputTypeNotSupported(n, Long.class);
  }

  static Long exec(Number n) {
    if (isLossless(n)) {
      return n.longValue();
    }
    throw new TypeConversionException(n, Long.class);
  }

  private static boolean testBigDecimal(Number n) {
    try {
      ((BigDecimal) n).longValueExact();
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  private static boolean testBigInteger(Number n) {
    try {
      ((BigInteger) n).longValueExact();
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  // Keep these methods around. They produce different result, and
  // seem more formally correct.
  @SuppressWarnings("unused")
  private static boolean testDouble(Number n) {
    try {
      new BigDecimal(Double.toString((Double) n)).longValueExact();
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  @SuppressWarnings("unused")
  private static boolean testFloat(Number n) {
    try {
      new BigDecimal(Float.toString((Float) n)).longValueExact();
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  static Long doubleToLong(Number n) {
    try {
      return new BigDecimal(Double.toString((Double) n)).longValueExact();
    } catch (ArithmeticException e) {
      throw new TypeConversionException(n, Long.class);
    }
  }

}
