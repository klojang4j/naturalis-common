package nl.naturalis.common.x.invoke;

import nl.naturalis.common.ExceptionMethods;

import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/*
 * Provides MethodHandle instances for the xxxExact() methods in subclasses of
 * Number.
 */
public class ValueExactMethod {

  public static final ValueExactMethod INSTANCE = new ValueExactMethod();

  private final Map<Class<? extends Number>, MethodHandle> mhs;

  private ValueExactMethod() {
    this.mhs = getMethodHandles();
  }

  public MethodHandle getMethodHandle(Class<?> type) {
    return mhs.get(type);
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

}
