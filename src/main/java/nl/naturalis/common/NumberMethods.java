package nl.naturalis.common;

import java.math.BigInteger;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ArrayMethods.isOneOf;

/**
 * Methods for working with {@code Number} instances.
 *
 * @author Ayco Holleman
 */
public class NumberMethods {

  /** Zero as Integer */
  public static final Integer ZERO_INT = 0;
  /** Zero as Double */
  public static final Double ZERO_DOUBLE = 0D;
  /** Zero as Long */
  public static final Long ZERO_LONG = 0L;
  /** Zero as Float */
  public static final Float ZERO_FLOAT = 0F;
  /** Zero as Short */
  public static final Short ZERO_SHORT = 0;
  /** Zero as Byte */
  public static final Byte ZERO_BYTE = 0;

  /**
   * Returns whether or not the specified string is a valid integer.
   *
   * @param str The string
   * @return Whether or not the specified string is a valid integer
   */
  public static boolean isInteger(String str) {
    try {
      BigInteger bi = new BigInteger(str);
      bi.intValueExact();
      return true;
    } catch (NumberFormatException | ArithmeticException e) {
      return false;
    }
  }

  /**
   * Returns whether or not the specified string is a valid integer.
   *
   * @param str The string
   * @return Whether or not the specified string is a valid integer
   */
  public static boolean isShort(String str) {
    try {
      BigInteger bi = new BigInteger(str);
      bi.shortValueExact();
      return true;
    } catch (NumberFormatException | ArithmeticException e) {
      return false;
    }
  }

  /**
   * Returns whether or not the specified {@code Number} can be converted into an instance of the
   * specified {@code Number} class without loss of information.
   *
   * @param <T> The type of {@code Number} to convert to
   * @param number The {@code Number} to convert
   * @param targetClass The type of {@code Number} to convert to
   * @return Whether or not conversion will be lossless
   */
  public static <T extends Number> boolean fitsInto(Number number, Class<T> targetClass) {
    Class<?> myClass = Check.notNull(number, "number").ok(Object::getClass);
    Check.notNull(targetClass, "targetType");
    if (isOneOf(targetClass, myClass, Double.class)) {
      return true;
    } else if (targetClass == Long.class) {
      if (myClass == Double.class) {
        return (long) number.doubleValue() == number.doubleValue();
      } else if (myClass == Float.class) {
        return (long) number.floatValue() == number.floatValue();
      }
      return true;
    } else if (targetClass == Float.class) {
      if (myClass == Double.class) {
        return (float) number.doubleValue() == number.doubleValue();
      } else if (myClass == Long.class) {
        return (float) number.longValue() == number.longValue();
      }
      return true;
    } else if (targetClass == Integer.class) {
      if (myClass == Double.class) {
        return (int) number.doubleValue() == number.doubleValue();
      } else if (myClass == Long.class) {
        return number.longValue() <= Integer.MAX_VALUE && number.longValue() >= Integer.MIN_VALUE;
      } else if (myClass == Float.class) {
        return (int) number.floatValue() == number.floatValue();
      }
      return true;
    } else if (targetClass == Short.class) {
      if (myClass == Double.class) {
        return (short) number.doubleValue() == number.doubleValue();
      } else if (myClass == Long.class) {
        return number.longValue() <= Short.MAX_VALUE && number.longValue() >= Short.MIN_VALUE;
      } else if (myClass == Float.class) {
        return (short) number.floatValue() == number.floatValue();
      } else if (myClass == Integer.class) {
        return number.intValue() <= Short.MAX_VALUE && number.intValue() >= Short.MIN_VALUE;
      }
      return true;
    } else /* Byte.class */ {
      if (myClass == Double.class) {
        return (byte) number.doubleValue() == number.doubleValue();
      } else if (myClass == Long.class) {
        return number.longValue() <= Byte.MAX_VALUE && number.longValue() >= Byte.MIN_VALUE;
      } else if (myClass == Float.class) {
        return (byte) number.floatValue() == number.floatValue();
      } else if (myClass == Integer.class) {
        return number.intValue() <= Byte.MAX_VALUE && number.intValue() >= Byte.MIN_VALUE;
      } else if (myClass == Short.class) {
        return number.shortValue() <= Byte.MAX_VALUE && number.shortValue() >= Byte.MIN_VALUE;
      }
      return true;
    }
  }

  /**
   * Returns the absolute value of the specified number.
   *
   * @param <T> The type of the number
   * @param number The number
   * @return Its absolute value
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T abs(T number) {
    Check.notNull(number);
    if (number.getClass() == Integer.class) {
      return number.intValue() >= 0 ? number : (T) Integer.valueOf(-number.intValue());
    } else if (number.getClass() == Long.class) {
      return number.longValue() >= 0 ? number : (T) Long.valueOf(-number.longValue());
    } else if (number.getClass() == Double.class) {
      return number.doubleValue() >= 0 ? number : (T) Double.valueOf(-number.doubleValue());
    } else if (number.getClass() == Float.class) {
      return number.floatValue() >= 0 ? number : (T) Float.valueOf(-number.floatValue());
    } else if (number.getClass() == Short.class) {
      return number.shortValue() >= 0 ? number : (T) Short.valueOf((short) -number.shortValue());
    }
    return number.byteValue() >= 0 ? number : (T) Byte.valueOf((byte) -number.byteValue());
  }

  private NumberMethods() {}
}
