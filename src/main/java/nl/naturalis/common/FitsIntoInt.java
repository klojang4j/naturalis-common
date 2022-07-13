package nl.naturalis.common;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.BigDecimalConverter.doubleFitsInto;
import static nl.naturalis.common.BigDecimalConverter.floatFitsInto;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;
import static nl.naturalis.common.NumberMethods.*;

class FitsIntoInt {

  private static final Integer I_MIN = Integer.MIN_VALUE;
  private static final Integer I_MAX = Integer.MAX_VALUE;

  private static final Map<Class<?>, Predicate<Number>> fitsIntoInt = Map.of(
      Double.class, n -> doubleFitsInto(n, Integer.class),
      Float.class, n -> floatFitsInto(n, Integer.class),
      Long.class, n -> n.longValue() >= I_MIN && n.longValue() <= I_MAX,
      AtomicLong.class, n -> n.longValue() >= I_MIN && n.longValue() <= I_MAX,
      Integer.class, yes(),
      AtomicInteger.class, yes(),
      Short.class, yes(),
      Byte.class, yes()
  );

  static boolean test(Number number) {
    Predicate<Number> tester = fitsIntoInt.get(number.getClass());
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, Integer.class);
  }

}
