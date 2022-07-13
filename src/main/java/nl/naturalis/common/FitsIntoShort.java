package nl.naturalis.common;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.BigDecimalConverter.doubleFitsInto;
import static nl.naturalis.common.BigDecimalConverter.floatFitsInto;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;
import static nl.naturalis.common.NumberMethods.*;

class FitsIntoShort {

  private static final Short S_MIN = Short.MIN_VALUE;
  private static final Short S_MAX = Short.MAX_VALUE;

  private static final Map<Class<?>, Predicate<Number>> fitsIntoShort = Map.of(
      Double.class, n -> doubleFitsInto(n, Integer.class),
      Float.class, n -> floatFitsInto(n, Integer.class),
      Long.class, n -> n.longValue() >= S_MIN && n.longValue() <= S_MAX,
      AtomicLong.class, n -> n.longValue() >= S_MIN && n.longValue() <= S_MAX,
      Integer.class, n -> n.intValue() >= S_MIN && n.intValue() <= S_MAX,
      AtomicInteger.class, n -> n.intValue() >= S_MIN && n.intValue() <= S_MAX,
      Short.class, yes(),
      Byte.class, yes()
  );

  static boolean test(Number number) {
    Predicate<Number> tester = fitsIntoShort.get(number.getClass());
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, Short.class);
  }

}
