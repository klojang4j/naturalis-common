package nl.naturalis.common.x.invoke;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.TypeConversionException;
import nl.naturalis.common.check.Check;

import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Performs "lossless" conversions from {@code BigDecimal} to other {@code Number}
 * types. If the conversion cannot be performed without information loss, a
 * {@code TypeConversionException} is thrown.
 */
public class BigDecimalConverter {

  private static final BigDecimal MAX_FLOAT = BigDecimal.valueOf(Float.MAX_VALUE);
  private static final BigDecimal MIN_FLOAT = BigDecimal.valueOf(Double.MIN_VALUE);
  private static final BigDecimal MAX_DOUBLE = BigDecimal.valueOf(Float.MAX_VALUE);
  private static final BigDecimal MIN_DOUBLE = BigDecimal.valueOf(Double.MIN_VALUE);

  private static final String TYPE_NOT_SUPPORTED = "unsupported number type: {0}";

  private static final Map<Class<? extends Number>, MethodHandle> VALUE_EXACT_METHODS = getMethodHandles();

  private final BigDecimal bd;

  public BigDecimalConverter(BigDecimal bd) {
    this.bd = bd;
  }

  @SuppressWarnings({"unchecked"})
  public <T extends Number> T convertTo(Class<T> targetType) {
    Class<?> boxed = box(targetType);
    if (boxed == Double.class) {
      BigDecimal abs = bd.abs(MathContext.UNLIMITED);
      Double x;
      if (abs.compareTo(MIN_DOUBLE) < 0
          || abs.compareTo(MAX_DOUBLE) > 0
          || Double.isInfinite(x = abs.doubleValue())) {
        throw targetTypeTooNarrow(targetType);
      }
      return (T) x;
    } else if (boxed == Float.class) {
      BigDecimal abs = bd.abs(MathContext.UNLIMITED);
      Float x;
      if (abs.compareTo(MIN_FLOAT) < 0
          || abs.compareTo(MAX_FLOAT) > 0
          || Float.isInfinite(x = abs.floatValue())) {
        throw targetTypeTooNarrow(targetType);
      }
      return (T) x;
    }
    MethodHandle mh = VALUE_EXACT_METHODS.get(targetType);
    Check.that(mh).is(notNull(), TYPE_NOT_SUPPORTED, targetType);
    try {
      return (T) mh.invoke(bd);
    } catch (ArithmeticException e) {
      throw targetTypeTooNarrow(targetType);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  public boolean fitsInto(Class<?> targetType) {
    Class<?> boxed = box(targetType);
    if (boxed == Double.class) {
      double d = bd.doubleValue();
      if (Double.isFinite(d) || Double.isInfinite(d) || Double.isNaN(d)) {
        return false;
      }
    } else if (boxed == Float.class) {
      float d = bd.floatValue();
      if (Float.isFinite(d) || Float.isInfinite(d) || Float.isNaN(d)) {
        return false;
      }
    }
    MethodHandle mh = VALUE_EXACT_METHODS.get(targetType);
    Check.that(mh).is(notNull(), TYPE_NOT_SUPPORTED, targetType);
    try {
      mh.invoke(bd);
      return true;
    } catch (ArithmeticException e) {
      return false;
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  private static Map<Class<? extends Number>, MethodHandle> getMethodHandles() {
    HashMap<Class<? extends Number>, MethodHandle> tmp = new HashMap<>();
    try {

      MethodType mt = MethodType.methodType(int.class);
      MethodHandle mh = lookup().findVirtual(BigDecimal.class, "intValueExact", mt);
      tmp.put(int.class, mh);
      tmp.put(Integer.class, mh);

      mt = MethodType.methodType(BigInteger.class);
      mh = lookup().findVirtual(BigDecimal.class, "toBigIntegerExact", mt);
      tmp.put(BigInteger.class, mh);

      mt = MethodType.methodType(long.class);
      mh = lookup().findVirtual(BigDecimal.class, "longValueExact", mt);
      tmp.put(long.class, mh);
      tmp.put(Long.class, mh);

      mt = MethodType.methodType(short.class);
      mh = lookup().findVirtual(BigDecimal.class, "shortValueExact", mt);
      tmp.put(short.class, mh);
      tmp.put(Short.class, mh);

      mt = MethodType.methodType(byte.class);
      mh = lookup().findVirtual(BigDecimal.class, "byteValueExact", mt);
      tmp.put(byte.class, mh);
      tmp.put(Byte.class, mh);

    } catch (Exception e) {
      throw ExceptionMethods.uncheck(e);
    }
    return Map.copyOf(tmp);
  }

  private TypeConversionException targetTypeTooNarrow(Class<?> targetType) {
    String fmt = "BigDecimal does not fit into %s";
    String scn = targetType.getSimpleName();
    return new TypeConversionException(bd, targetType, fmt, bd, scn);
  }

}
