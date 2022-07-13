package nl.naturalis.common;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static nl.naturalis.common.BigDecimalConverter.doubleFitsInto;
import static nl.naturalis.common.BigDecimalConverter.floatFitsInto;
import static nl.naturalis.common.NumberMethods.*;
import static nl.naturalis.common.TypeConversionException.inputTypeNotSupported;

class FitsIntoLong {

  private static final Map<Class<?>, Predicate<Number>> fitsIntoLong = Map.of(
      Double.class, n -> doubleFitsInto(n, Long.class),
      Float.class, n -> floatFitsInto(n, Long.class),
      Long.class, yes(),
      AtomicLong.class, yes(),
      Integer.class, yes(),
      AtomicInteger.class, yes(),
      Short.class, yes(),
      Byte.class, yes()
  );

  static boolean test(Number number) {
    Predicate<Number> tester = fitsIntoLong.get(number.getClass());
    if (tester != null) {
      return tester.test(number);
    }
    throw inputTypeNotSupported(number, Long.class);
  }

}
