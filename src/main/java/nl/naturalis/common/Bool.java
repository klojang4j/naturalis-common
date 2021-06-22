package nl.naturalis.common;

import static java.lang.Boolean.*;

import java.util.Set;
import nl.naturalis.common.check.Check;

/**
 * Converts values from various non-boolean types to boolean values. Where applicable, {@code null}
 * is accepted as an argument and evaluates to {@code false}. Values evaluating to {@code true} and
 * values evaluating to {@code false} will both be tightly defined, rather than (for example) 1
 * counting as {@code true} and anything else as {@code false}. If an argument is neither a {@code
 * true} value nor a {@code false} value, an {@link IllegalArgumentException} is thrown. The static
 * method use the {@link #TRUE_STRINGS} and {@link #FALSE_STRINGS} sets to determine if a {@code
 * String} is true-ish or falsy. You can also instantiate the {@code Bool} class with your own
 * {@code true} strings and {@code false} strings.
 *
 * @author Ayco Holleman
 */
public class Bool {

  /**
   * The default set of strings that count as {@code true} values (ignoring case): "true", "1",
   * "yes", "on", "enabled".
   */
  public static final Set<String> TRUE_STRINGS = Set.of("true", "1", "yes", "on", "enabled");

  /**
   * The default set of strings that count as {@code false} values (ignoring case): "false", "0",
   * "false", "off", "disabled".
   */
  public static final Set<String> FALSE_STRINGS = Set.of("false", "0", "no", "off", "disabled");

  /**
   * Attempts to convert the specified object to a {@code Boolean}. This is done by delegating to
   * one of the more specific {@code from} methods, according to the type of the argument. If the
   * object's type is not covered by any of the other {@code from} methods an {@link
   * IllegalArgumentException} is thrown.
   *
   * @param obj The object to convert
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(Object obj) {
    return INSTANCE.getBoolean(obj);
  }

  /**
   * Converts the specified {@code String} to a {@code Boolean} value. This method checks whether
   * the argument is either one of {@link #TRUE_STRINGS} or one of {@link #FALSE_STRINGS} (ignoring
   * case).
   *
   * @param s The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(String s) {
    return INSTANCE.getBoolean(s);
  }

  /**
   * Converts the specified {@code Number} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(Number n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code int} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(int n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code double} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(double n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code long} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(long n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code float} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(float n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code short} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(short n) {
    return INSTANCE.getBoolean(n);
  }

  /**
   * Converts the specified {@code Number} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public static Boolean from(byte n) {
    return INSTANCE.getBoolean(n);
  }

  private static final Bool INSTANCE = new Bool();

  private final Set<String> trueStrings;
  private final Set<String> falseStrings;

  private Bool() {
    this(TRUE_STRINGS, FALSE_STRINGS);
  }

  /**
   * Creates a new {@code Bool} instance that will use the provided {@code true} strings and {@code
   * false} strings to evaluate {@code String} arguments.
   *
   * @param trueStrings The string values that must count as {@code true}
   * @param falseStrings The string values that must count as {@code false}
   */
  public Bool(Set<String> trueStrings, Set<String> falseStrings) {
    this.trueStrings = trueStrings;
    this.falseStrings = falseStrings;
  }

  /**
   * Attempts to convert the specified object to a {@code Boolean}. This is done by delegating to
   * one of the more specific {@code getBoolean} methods, according to the type of the argument. If
   * the object's type is not covered by any of the other {@code getBoolean} methods an {@link
   * IllegalArgumentException} is thrown.
   *
   * @param obj The object to convert
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(Object obj) {
    return obj == null
        ? FALSE
        : obj.getClass() == Boolean.class
            ? (Boolean) obj
            : obj.getClass() == String.class
                ? getBoolean((String) obj)
                : obj instanceof Number ? getBoolean((Number) obj) : noCanDo(obj);
  }

  /**
   * Converts the specified {@code String} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(String s) {
    if (s == null || falseStrings.contains(s.toLowerCase())) {
      return FALSE;
    }
    if (trueStrings.contains(s.toLowerCase())) {
      return TRUE;
    }
    return Check.fail("Cannot parse \"%s\" into Boolean", s);
  }

  /**
   * Converts the specified {@code Number} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(Number n) {
    if (n == null) {
      return FALSE;
    }
    Integer i = NumberMethods.convert(n, Integer.class);
    return i == 1 ? TRUE : i == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code int} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(int n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code double} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(double n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code long} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(long n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code float} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(float n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code short} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(short n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  /**
   * Converts the specified {@code byte} to a {@code Boolean} value.
   *
   * @param n The argument
   * @return The corresponding {@code Boolean} value
   */
  public Boolean getBoolean(byte n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  private static Boolean noCanDo(Object obj) {
    return Check.fail("Cannot convert %s to Boolean", obj);
  }
}
