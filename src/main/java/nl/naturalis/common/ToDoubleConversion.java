package nl.naturalis.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.NumberMethods.yes;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;

final class ToDoubleConversion {

  static final BigDecimal BIG_MIN_DOUBLE = new BigDecimal(Double.toString(Double.MIN_VALUE));
  static final BigDecimal BIG_MAX_DOUBLE = new BigDecimal(Double.toString(Double.MAX_VALUE));

  private ToDoubleConversion() {
    throw new UnsupportedOperationException();
  }

  private static final Map<Class<?>, Predicate<Number>> fitsIntoDouble = Map.of(
      BigDecimal.class, ToDoubleConversion::testBigDecimal,
      BigInteger.class, ToDoubleConversion::testBigInteger,
      Double.class, yes(),
      Float.class, yes(),
      Long.class, yes(),
      AtomicLong.class, yes(),
      Integer.class, yes(),
      AtomicInteger.class, yes(),
      Short.class, yes(),
      Byte.class, yes()
  );

  static boolean isLossless(Number n) {
    Predicate<Number> tester = fitsIntoDouble.get(n.getClass());
    if (tester != null) {
      return tester.test(n);
    }
    throw inputTypeNotSupported(n, Float.class);
  }

  static double exec(Number n) {
    if (isLossless(n)) {
      return n.doubleValue();
    }
    throw new TypeConversionException(n, Double.class);
  }

  private static boolean testBigInteger(Number n) {
    return new BigDecimal(((BigInteger) n).abs()).compareTo(BIG_MAX_DOUBLE) <= 0;
  }

  private static boolean testBigDecimal(Number n) {
    return ((BigDecimal) n).abs().compareTo(BIG_MAX_DOUBLE) <= 0;
  }

}
