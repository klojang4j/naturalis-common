package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.invoke.ValueExactMethod;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;

import static nl.naturalis.common.NumberMethods.UNSUPPORTED_NUMBER_TYPE;
import static nl.naturalis.common.check.CommonChecks.notNull;

class NumberConverter<T extends Number> {

  private final Class<T> targetType;

  NumberConverter(Class<T> targetType) {
    this.targetType = targetType;
  }

  @SuppressWarnings("unchecked")
  <U extends Number> T convert(U number) {
    Class<U> from;
    Class<T> to;
    if (number == null || (to = targetType) == Number.class) {
      return (T) number;
    }
    from = (Class<U>) number.getClass();
    if (from == to) {
      return (T) number;
    }
    BigDecimal bd = NumberMethods.toBigDecimal(number);
    if (to == Double.class) {
      double d = bd.doubleValue();
      if (d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
        throw targetTypeTooNarrow(number, to);
      }
      return (T) (Double) d;
    } else if (to == Float.class) {
      float f = bd.floatValue();
      if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
        throw targetTypeTooNarrow(number, to);
      }
      return (T) (Float) f;
    } else {
      MethodHandle mh = ValueExactMethod.INSTANCE.getMethodHandle(to);
      Check.that(mh).is(notNull(), UNSUPPORTED_NUMBER_TYPE, to);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        throw targetTypeTooNarrow(number, to);
      } catch (Throwable e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }

  private static <T extends Number, U extends Number> TypeConversionException targetTypeTooNarrow(
      U number,
      Class<T> toType) {
    return new TypeConversionException(number,
        toType,
        "%s does not fit into %s",
        number,
        toType.getSimpleName());
  }

}
