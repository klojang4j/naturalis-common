package nl.naturalis.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.check.Check;
import static java.lang.invoke.MethodHandles.lookup;

class NumberConverter<T extends Number> {

  private static Map<Class<? extends Number>, MethodHandle> callables;
  private static final String ERR0 = "%s does not fit into %s";

  private final Class<T> targetType;

  NumberConverter(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
    if (callables == null) {
      callables = createCallables();
    }
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
        float f = (float) n.doubleValue();
        if (f == n.doubleValue()) {
          return (T) (Float) f;
        }
        return Check.fail(ERR0, n, targetType.getSimpleName());
      }
      return (T) (Float) n.floatValue();
    } else {
      BigDecimal bd = new BigDecimal(n.doubleValue());
      MethodHandle mh = callables.get(targetType);
      try {
        return (T) mh.invoke(bd);
      } catch (ArithmeticException e) {
        return Check.fail(ERR0, n, targetType.getSimpleName());
      } catch (Throwable e) {
        throw ExceptionMethods.uncheck(e);
      }
    }
  }

  private static Map<Class<? extends Number>, MethodHandle> createCallables() {
    HashMap<Class<? extends Number>, MethodHandle> tmp = new HashMap<>(8);
    try {

      MethodType mt = MethodType.methodType(int.class);
      MethodHandle mh = lookup().findVirtual(BigDecimal.class, "intValueExact", mt);
      tmp.put(Integer.class, mh);

      mt = MethodType.methodType(short.class);
      mh = lookup().findVirtual(BigDecimal.class, "shortValueExact", mt);
      tmp.put(Short.class, mh);

      mt = MethodType.methodType(byte.class);
      mh = lookup().findVirtual(BigDecimal.class, "byteValueExact", mt);
      tmp.put(Byte.class, mh);

    } catch (Exception e) {
      throw ExceptionMethods.uncheck(e);
    }
    return Map.copyOf(tmp);
  }
}
