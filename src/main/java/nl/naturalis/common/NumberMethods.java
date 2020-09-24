package nl.naturalis.common;

import nl.naturalis.common.check.Check;

/**
 * Methods for working with {@code Number} instances.
 *
 * @author Ayco Holleman
 */
public class NumberMethods {

  private NumberMethods() {}

  /**
   * Returns whether or not the specified {@code Number} can be converted an instance of the {@code
   * Number} type without losing of information.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param n The {@code Number} to convert
   * @param to The type of {@code Number} to convert to
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
      } else if (fc == Integer.class) { // Is this necessary?
        return (float) n.intValue() == n.intValue();
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
}
