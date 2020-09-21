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
   * Returns whether or not the specified {@code Number} can be converted to the specified target
   * {@code Number} type without actual loss of precision.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param from The {@code Number} to convert
   * @param to The type of {@code Number} to convert to
   * @return Whether or not conversion will be lossless
   */
  public static <T extends Number> boolean isLossless(Number from, Class<T> to) {
    Check.notNull(from, "from");
    Check.notNull(to, "to");
    Class<?> fc = from.getClass();
    if (to == Double.class) {
      return true;
    } else if (to == Long.class) {
      if (fc == Double.class) {
        return (long) from.doubleValue() == from.doubleValue();
      } else if (fc == Float.class) {
        return (long) from.floatValue() == from.floatValue();
      }
      return true;
    } else if (to == Float.class) {
      if (fc == Double.class) {
        return (float) from.doubleValue() == from.doubleValue();
      } else if (fc == Long.class) {
        return (float) from.longValue() == from.longValue();
      } else if (fc == Integer.class) { // Is this necessary?
        return (float) from.intValue() == from.intValue();
      }
      return true;
    } else if (to == Integer.class) {
      if (fc == Double.class) {
        return (int) from.doubleValue() == from.doubleValue();
      } else if (fc == Long.class) {
        return (int) from.longValue() == from.longValue();
      } else if (fc == Float.class) {
        return (int) from.floatValue() == from.floatValue();
      }
      return true;
    } else if (to == Short.class) {
      if (fc == Double.class) {
        return (short) from.doubleValue() == from.doubleValue();
      } else if (fc == Long.class) {
        return (short) from.longValue() == from.longValue();
      } else if (fc == Float.class) {
        return (short) from.floatValue() == from.floatValue();
      } else if (fc == Integer.class) {
        return (short) from.intValue() == from.intValue();
      }
      return true;
    } else /* Byte.class */ {
      if (fc == Double.class) {
        return (byte) from.doubleValue() == from.doubleValue();
      } else if (fc == Long.class) {
        return (byte) from.longValue() == from.longValue();
      } else if (fc == Float.class) {
        return (byte) from.floatValue() == from.floatValue();
      } else if (fc == Integer.class) {
        return (byte) from.intValue() == from.intValue();
      } else if (fc == Short.class) {
        return (byte) from.shortValue() == from.shortValue();
      }
      return true;
    }
  }
}
