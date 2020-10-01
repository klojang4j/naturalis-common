package nl.naturalis.common;

import nl.naturalis.common.check.Check;

/**
 * Methods for working with {@code Number} instances.
 *
 * @author Ayco Holleman
 */
public class NumberMethods {

  /** Zero as Integer */
  public static final Integer ZERO = 0;
  /** Zero as Double */
  public static final Double ZERO_D = 0D;
  /** Zero as Long */
  public static final Long ZERO_L = 0L;
  /** Zero as Float */
  public static final Float ZERO_F = 0F;
  /** Zero as Short */
  public static final Short ZERO_S = 0;
  /** Zero as Byte */
  public static final Byte ZERO_B = 0;

  /**
   * Returns whether or not the specified {@code Number} can be converted an instance of the {@code
   * Number} type without losing of information.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param n The {@code Number} to convert
   * @param targetType The type of {@code Number} to convert to
   * @return Whether or not conversion will be lossless
   */
  public static <T extends Number> boolean fitsInto(Number n, Class<T> targetType) {
    Class<T> to;
    Check.notNull(n, "from");
    Check.notNull(to = targetType, "targetType");
    Class<?> fc = n.getClass();
    if (n.getClass() == to || to == Double.class) {
      return true;
    } else if (to == Long.class) {
      if (fc == Double.class) {
        return (long) n.doubleValue() == n.doubleValue();
      } else if (fc == Float.class) {
        return (long) n.floatValue() == n.floatValue();
      }
      return true;
    } else if (to == Float.class) {
      if (fc == Double.class) {
        return (float) n.doubleValue() == n.doubleValue();
      } else if (fc == Long.class) {
        return (float) n.longValue() == n.longValue();
      }
      return true;
    } else if (to == Integer.class) {
      if (fc == Double.class) {
        return (int) n.doubleValue() == n.doubleValue();
      } else if (fc == Long.class) {
        return n.longValue() <= Integer.MAX_VALUE && n.longValue() >= Integer.MIN_VALUE;
      } else if (fc == Float.class) {
        return (int) n.floatValue() == n.floatValue();
      }
      return true;
    } else if (to == Short.class) {
      if (fc == Double.class) {
        return (short) n.doubleValue() == n.doubleValue();
      } else if (fc == Long.class) {
        return n.longValue() <= Short.MAX_VALUE && n.longValue() >= Short.MIN_VALUE;
      } else if (fc == Float.class) {
        return (short) n.floatValue() == n.floatValue();
      } else if (fc == Integer.class) {
        return n.intValue() <= Short.MAX_VALUE && n.intValue() >= Short.MIN_VALUE;
      }
      return true;
    } else /* Byte.class */ {
      if (fc == Double.class) {
        return (byte) n.doubleValue() == n.doubleValue();
      } else if (fc == Long.class) {
        return n.longValue() <= Byte.MAX_VALUE && n.longValue() >= Byte.MIN_VALUE;
      } else if (fc == Float.class) {
        return (byte) n.floatValue() == n.floatValue();
      } else if (fc == Integer.class) {
        return n.intValue() <= Byte.MAX_VALUE && n.intValue() >= Byte.MIN_VALUE;
      } else if (fc == Short.class) {
        return n.shortValue() <= Byte.MAX_VALUE && n.shortValue() >= Byte.MIN_VALUE;
      }
      return true;
    }
  }

  /**
   * Returns the default {@code int} value (0) if the argument is null, else the argument itself.
   *
   * @param i The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Integer nvl(Integer i) {
    return ObjectMethods.ifNull(i, ZERO);
  }

  /**
   * Returns the default {@code double} value (0) if the argument is null, else the argument itself.
   *
   * @param d The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Double nvl(Double d) {
    return ObjectMethods.ifNull(d, ZERO_D);
  }

  /**
   * Returns the default {@code long} value (0) if the argument is null, else the argument itself.
   *
   * @param l The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Long nvl(Long l) {
    return ObjectMethods.ifNull(l, ZERO_L);
  }

  /**
   * Returns the default {@code float} value (0) if the argument is null, else the argument itself.
   *
   * @param f The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Float nvl(Float f) {
    return ObjectMethods.ifNull(f, ZERO_F);
  }

  /**
   * Returns the default {@code short} value (0) if the argument is null, else the argument itself.
   *
   * @param s The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Short nvl(Short s) {
    return ObjectMethods.ifNull(s, ZERO_S);
  }

  /**
   * Returns the default {@code byte} value (0) if the argument is null, else the argument itself.
   *
   * @param b The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Byte nvl(Byte b) {
    return ObjectMethods.ifNull(b, ZERO_B);
  }

  private NumberMethods() {}
}
