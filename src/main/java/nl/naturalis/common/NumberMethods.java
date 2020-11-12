package nl.naturalis.common;

import java.math.BigDecimal;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notBlank;
import static nl.naturalis.common.check.CommonChecks.*;

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
   * Returns {@link #ZERO_INT} if the argument is null, else the argument itself.
   *
   * @param i The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Integer ntz(Integer i) {
    return ObjectMethods.ifNull(i, ZERO_INT);
  }

  /**
   * Returns {@link #ZERO_DOUBLE} the argument is null, else the argument itself.
   *
   * @param d The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Double ntz(Double d) {
    return ObjectMethods.ifNull(d, ZERO_DOUBLE);
  }

  /**
   * Returns {@link #ZERO_LONG} if the argument is null, else the argument itself.
   *
   * @param l The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Long ntz(Long l) {
    return ObjectMethods.ifNull(l, ZERO_LONG);
  }

  /**
   * Returns {@link #ZERO_FLOAT} if the argument is null, else the argument itself.
   *
   * @param f The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Float ntz(Float f) {
    return ObjectMethods.ifNull(f, ZERO_FLOAT);
  }

  /**
   * Returns {@link #ZERO_SHORT} if the argument is null, else the argument itself.
   *
   * @param s The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Short ntz(Short s) {
    return ObjectMethods.ifNull(s, ZERO_SHORT);
  }

  /**
   * Returns {@link #ZERO_BYTE} if the argument is null, else the argument itself.
   *
   * @param b The primitive wrapper
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Byte ntz(Byte b) {
    return ObjectMethods.ifNull(b, ZERO_BYTE);
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
    final T n = Check.notNull(number).ok();
    if (n.getClass() == Integer.class) {
      return n.intValue() >= 0 ? n : (T) Integer.valueOf(-n.intValue());
    } else if (n.getClass() == Long.class) {
      return n.longValue() >= 0 ? n : (T) Long.valueOf(-n.longValue());
    } else if (n.getClass() == Double.class) {
      return n.doubleValue() >= 0 ? n : (T) Double.valueOf(-n.doubleValue());
    } else if (n.getClass() == Float.class) {
      return n.floatValue() >= 0 ? n : (T) Float.valueOf(-n.floatValue());
    } else if (n.getClass() == Short.class) {
      return n.shortValue() >= 0 ? n : (T) Short.valueOf((short) -n.shortValue());
    }
    return n.byteValue() >= 0 ? n : (T) Byte.valueOf((byte) -n.byteValue());
  }

  /**
   * Parses the specified string into an {@code int}. This method is substantially more restrictive
   * than {@link Integer#parseInt(String) Integer.parseInt}. It uses {@link
   * BigDecimal#intValueExact() BigDecimal.intValueExact()} and {@link BigDecimal#scale()
   * BigDecimal.scale()} to make sure the string represents an integer. It does not accept a decimal
   * point or any decimal fraction (even if consisting of zeros only). It also does not accept
   * strings using scientific notation.
   *
   * <p>Examples of invalid input:
   *
   * <p>
   *
   * <table>
   * <tr><td><pre>7F</pre></td><td><i>Not an integer</i></td></tr>
   * <tr><td><pre>.7</pre></td><td><i>Not an integer</i></td></tr>
   * <tr><td><pre>7.0</pre></td><td><i>Scale is 1</i></td></tr>
   * <tr><td><pre>7.</pre></td><td><i>Decimal point not allowed</i></td></tr>
   * <tr><td><pre>123456789123456789</pre></td><td><i>Overflow</i></td></tr>
   * <tr><td><pre>3.4e+2</pre></td><td><i>Scientific notation</i></td></tr>
   * </table>
   *
   * <p>Any error occuring while parsing the argument will be converted to a {@code
   * NumberFormatException}. This includes the argument being null and any {@link
   * ArithmeticException} thrown by {@code BigDecimal.intValueExact}.
   *
   * @param s The string to parse
   * @return An integer
   * @throws NumberFormatException If anything goes wrong while parsing the string
   */
  public static int asPlainInt(String s) throws NumberFormatException {
    Check<String, NumberFormatException> check =
        Check.that(s, NumberFormatException::new).is(notBlank());
    BigDecimal bd = new BigDecimal(s);
    if (bd.scale() != 0) {
      throw new NumberFormatException("Decimal fraction not allowed");
    }
    int i;
    try {
      i = new BigDecimal(s).intValueExact();
    } catch (ArithmeticException e) {
      throw new NumberFormatException(e.getMessage());
    }
    check
        .is(notHasSubstr(), "e", "Scientific notation not allowed")
        .is(notEndsWith(), ".", "Decimal point not allowed");
    return i;
  }

  private NumberMethods() {}
}
