package nl.naturalis.common;

import nl.naturalis.common.x.invoke.ValueExactMethod;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;

class NumberConverter<T extends Number> {

  private final Class<T> targetType;

  NumberConverter(Class<T> targetType) {
    this.targetType = targetType;
  }

  @SuppressWarnings("unchecked")
  <U extends Number> T convert(U number) {
    Class<T> toType;
    if (number == null || (toType = targetType) == Number.class) {
      return (T) number;
    }
    Class<U> myType = (Class<U>) number.getClass();
    if (myType == toType) {
      return (T) number;
    }
    BigDecimal bd = NumberMethods.toBigDecimal(number);
    if (toType == Double.class) {
      double d = bd.doubleValue();
      if (d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
        throw targetTypeTooNarrow(number, toType);
      }
      return (T) (Double) d;
    } else if (toType == Float.class) {
      float f = bd.floatValue();
      if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
        throw targetTypeTooNarrow(number, toType);
      }
      return (T) (Float) f;
    } else {
      MethodHandle mh = ValueExactMethod.INSTANCE.getMethodHandle(toType);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        throw targetTypeTooNarrow(number, toType);
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
