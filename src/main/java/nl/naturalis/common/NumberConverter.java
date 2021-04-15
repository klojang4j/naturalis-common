package nl.naturalis.common;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import nl.naturalis.common.check.Check;

class NumberConverter<T extends Number> {

  private static final String ERR0 = "%s does not fit into %s";

  private final Class<T> targetType;

  NumberConverter(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  @SuppressWarnings("unchecked")
  <U extends Number> T convert(U n) {
    Class<U> myType = (Class<U>) Check.notNull(n).ok(Object::getClass);
    if (myType == targetType) {
      return (T) n;
    } else if (targetType == Double.class) {
      return (T) (Double) n.doubleValue();
    } else if (targetType == Float.class) {
      if (myType == Double.class) {
        double d = n.doubleValue();
        if (d >= Float.MIN_NORMAL && d <= Float.MAX_VALUE) {
          return (T) (Float) (float) d;
        }
        return Check.fail(ERR0, n, targetType.getSimpleName());
      }
      return (T) (Float) n.floatValue();
    } else {
      BigDecimal bd = new BigDecimal(n.doubleValue());
      MethodHandle mh = ValueExact.INSTANCE.forType(targetType);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        return Check.fail(ERR0, n, targetType.getSimpleName());
      } catch (Throwable e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }
}
