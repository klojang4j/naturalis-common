package nl.naturalis.common;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.BigDecimalConverter.doubleFitsInto;
import static nl.naturalis.common.BigDecimalConverter.floatFitsInto;
import static nl.naturalis.common.NumberMethods.*;

import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;

class FitsIntoByte {

  private static final Byte B_MIN = Byte.MIN_VALUE;
  private static final Byte B_MAX = Byte.MAX_VALUE;

  private static final Map<Class<?>, Predicate<Number>> fitsIntoByte = Map.of(
      Double.class, n -> doubleFitsInto(n, Byte.class),
      Float.class, n -> floatFitsInto(n, Byte.class),
      Long.class, n -> n.longValue() >= B_MIN && n.longValue() <= B_MAX,
      AtomicLong.class, n -> n.longValue() >= B_MIN && n.longValue() <= B_MAX,
      Integer.class, n -> n.intValue() >= B_MIN && n.intValue() <= B_MAX,
      AtomicInteger.class, n -> n.intValue() >= B_MIN && n.intValue() <= B_MAX,
      Short.class, n -> n.shortValue() >= B_MIN && n.shortValue() <= B_MAX,
      Byte.class, yes()
  );

  static boolean test(Number number) {
    Predicate<Number> tester = fitsIntoByte.get(number.getClass());
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, Byte.class);
  }

}
