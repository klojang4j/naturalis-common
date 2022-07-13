package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import static java.lang.invoke.MethodHandles.lookup;
import static nl.naturalis.common.NumberMethods.toBigDecimal;
import static nl.naturalis.common.TypeConversionException.targetTypeNotSupported;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performs "lossless" conversions from {@code BigDecimal} to other {@code Number}
 * types. If the conversion cannot be performed without information loss, a
 * {@code TypeConversionException} is thrown.
 */
final class BigDecimalConverter {

  private static final BigDecimal MAX_FLOAT = BigDecimal.valueOf(Float.MAX_VALUE);
  private static final BigDecimal MIN_FLOAT = BigDecimal.valueOf(Float.MIN_VALUE);

  private static final BigDecimal MAX_DOUBLE = BigDecimal.valueOf(Double.MAX_VALUE);
  private static final BigDecimal MIN_DOUBLE = BigDecimal.valueOf(Double.MIN_VALUE);

  static final Map<Class<? extends Number>, MethodHandle> VALUE_EXACT_METHODS = getMethodHandles();

  static boolean doubleFitsInto(Number n, Class<?> targetType) {
    MethodHandle method = VALUE_EXACT_METHODS.get(targetType);
    if (targetType != null) {
      try {
        method.invoke(BigDecimal.valueOf(n.doubleValue()));
        return true;
      } catch (ArithmeticException ignored) {
        return false;
      } catch (Throwable t) {
        throw new TypeConversionException(n, targetType, t.toString());
      }
    } else if (targetType == AtomicInteger.class) {
      return doubleFitsInto(n, Integer.class);
    } else if (targetType == AtomicLong.class) {
      return doubleFitsInto(n, Long.class);
    }
    throw targetTypeNotSupported(n, targetType);
  }

  static boolean floatFitsInto(Number n, Class<?> targetType) {
    MethodHandle method = VALUE_EXACT_METHODS.get(targetType);
    if (targetType != null) {
      try {
        method.invoke(BigDecimal.valueOf(n.floatValue()));
        return true;
      } catch (ArithmeticException ignored) {
        return false;
      } catch (Throwable t) {
        throw new TypeConversionException(n, targetType, t.toString());
      }
    } else if (targetType == AtomicInteger.class) {
      return floatFitsInto(n, Integer.class);
    } else if (targetType == AtomicLong.class) {
      return floatFitsInto(n, Long.class);
    }
    throw targetTypeNotSupported(n, targetType);
  }

  @SuppressWarnings({"unchecked"})
  static <T extends Number> T convertTo(Number number, Class<T> targetType) {
    BigDecimal bd = toBigDecimal(number);
    if (targetType == BigDecimal.class) {
      return (T) bd;
    } else if (targetType == Double.class) {
      BigDecimal abs = bd.abs(MathContext.DECIMAL64);
      if (abs.compareTo(MIN_DOUBLE) < 0 || abs.compareTo(MAX_DOUBLE) > 0) {
        throw targetTypeTooNarrow(number, targetType);
      }
      return (T) Double.valueOf(bd.doubleValue());
    } else if (targetType == Float.class) {
      BigDecimal abs = bd.abs(MathContext.DECIMAL64);
      if (abs.compareTo(MIN_FLOAT) < 0 || abs.compareTo(MAX_FLOAT) > 0) {
        throw targetTypeTooNarrow(number, targetType);
      }
      return (T) Float.valueOf(bd.floatValue());
    } else if (targetType == AtomicInteger.class) {
      return (T) new AtomicInteger(convertTo(number, Integer.class));
    } else if (targetType == AtomicLong.class) {
      return (T) new AtomicLong(convertTo(number, Long.class));
    }
    MethodHandle method = VALUE_EXACT_METHODS.get(targetType);
    if (method != null) {
      try {
        return (T) method.invoke(bd);
      } catch (ArithmeticException e) {
        throw targetTypeTooNarrow(number, targetType);
      } catch (Throwable t) {
        throw new TypeConversionException(number, targetType, t.toString());
      }
    }
    throw targetTypeNotSupported(bd, targetType);
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

  private static TypeConversionException targetTypeTooNarrow(Number n, Class<?> c) {
    return new TypeConversionException(n, c, "target type too narrow");
  }

}
