package nl.naturalis.common;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notNull;

class NumberParser<T extends Number> {

  private static final String ERR0 = "%s not parsable into %s";

  private final Class<T> targetType;

  NumberParser(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  @SuppressWarnings("unchecked")
  T parse(String s) {
    Check.that(s).is(notNull(), ERR0, s, targetType.getSimpleName());
    BigDecimal bd;
    try {
      bd = new BigDecimal(s);
    } catch (NumberFormatException e) {
      return Check.fail(ERR0, s, targetType.getSimpleName());
    }
    if (targetType == Double.class) {
      return (T) (Double) bd.doubleValue();
    } else if (targetType == Float.class) {
      double d = bd.doubleValue();
      if (d >= Float.MIN_NORMAL && d <= Float.MAX_VALUE) {
        return (T) (Float) bd.floatValue();
      }
      return Check.fail(ERR0, s, targetType.getSimpleName());
    } else {
      MethodHandle mh = ValueExact.INSTANCE.forType(targetType);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        return Check.fail(ERR0, s, targetType.getSimpleName());
      } catch (Throwable e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }
}
