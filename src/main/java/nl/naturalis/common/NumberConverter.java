package nl.naturalis.common;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import nl.naturalis.common.check.Check;

class NumberConverter<T extends Number> {

  private static final String ERR0 = "%s does not fit into %s";

  private final Class<T> targetType;

  NumberConverter(Class<T> targetType) {
    this.targetType = targetType;
  }

  @SuppressWarnings("unchecked")
  <U extends Number> T convert(U n) {
    if (n == null) {
      return (T) n;
    }
    Class<U> myType = (Class<U>) Check.notNull(n).ok(Object::getClass);
    Class<T> tt = targetType;
    if (myType == tt) {
      return (T) n;
    }
    BigDecimal bd = NumberMethods.toBigDecimal(n);
    if (tt == Double.class) {
      double d = bd.doubleValue();
      if (d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
        // myType is BigDecimal or BigInteger
        throw new TypeConversionException(n, tt, ERR0, n, tt.getSimpleName());
      }
      return (T) (Double) d;
    } else if (tt == Float.class) {
      float f = bd.floatValue();
      if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
        throw new TypeConversionException(n, tt, ERR0, n, tt.getSimpleName());
      }
      return (T) (Float) f;
    } else {
      MethodHandle mh = ValueExact.INSTANCE.forType(tt);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        throw new TypeConversionException(n, tt, ERR0, n, tt.getSimpleName());
      } catch (Throwable e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }
}
