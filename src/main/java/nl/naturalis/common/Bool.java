package nl.naturalis.common;

import static java.lang.Boolean.*;

import java.util.Set;
import nl.naturalis.common.check.Check;

public class Bool {

  public static final Set<String> TRUE_STRINGS = Set.of("true", "1", "yes", "on", "enabled");
  public static final Set<String> FALSE_STRINGS = Set.of("false", "0", "no", "off", "disabled");

  public static Boolean from(Object obj) {
    return INSTANCE.getBoolean(obj);
  }

  public static Boolean from(String s) {
    return INSTANCE.getBoolean(s);
  }

  public static Boolean from(Number n) {
    return INSTANCE.getBoolean(n);
  }

  public static Boolean from(int n) {
    return INSTANCE.getBoolean(n);
  }

  public static Boolean from(double n) {
    return INSTANCE.getBoolean(n);
  }

  public static Boolean from(float n) {
    return INSTANCE.getBoolean(n);
  }

  public static Boolean from(short n) {
    return INSTANCE.getBoolean(n);
  }

  public static Boolean from(byte n) {
    return INSTANCE.getBoolean(n);
  }

  private static final Bool INSTANCE = new Bool();

  private final Set<String> trueStrings;
  private final Set<String> falseStrings;

  private Bool() {
    this(TRUE_STRINGS, FALSE_STRINGS);
  }

  public Bool(Set<String> trueStrings, Set<String> falseStrings) {
    this.trueStrings = trueStrings;
    this.falseStrings = falseStrings;
  }

  public Boolean getBoolean(Object obj) {
    return obj == null
        ? FALSE
        : obj.getClass() == Boolean.class
            ? (Boolean) obj
            : obj.getClass() == String.class
                ? getBoolean((String) obj)
                : obj instanceof Number ? getBoolean((Number) obj) : noCanDo(obj);
  }

  public Boolean getBoolean(String s) {
    if (s == null || falseStrings.contains(s.toLowerCase())) {
      return FALSE;
    }
    if (trueStrings.contains(s.toLowerCase())) {
      return TRUE;
    }
    return Check.fail("Cannot parse \"%s\" into Boolean", s);
  }

  public Boolean getBoolean(Number n) {
    if (n == null) {
      return FALSE;
    }
    Integer i = NumberMethods.convert(n, Integer.class);
    return i == 1 ? TRUE : i == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(int n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(double n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(float n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(long n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(short n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  public Boolean getBoolean(byte n) {
    return n == 1 ? TRUE : n == 0 ? FALSE : noCanDo(n);
  }

  private static Boolean noCanDo(Object obj) {
    return Check.fail("Cannot convert %s to Boolean", obj);
  }
}
