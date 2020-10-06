package nl.naturalis.common.check;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ClassMethods.getArrayTypeName;
import static nl.naturalis.common.ClassMethods.getArrayTypeSimpleName;
import static nl.naturalis.common.check.CommonChecks.messages;

class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /* Returns messages associated with predefined Predicate instances. */
  static String get(Object test, Object arg, String argName) {
    Function<Object[], String> fnc = messages.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName));
    }
    return String.format(ERR_INVALID_VALUE, argName, argVal(arg));
  }

  /* Returns messages associated with predefined Relation instances. */
  static String get(Object test, Object arg, String argName, Object target) {
    Function<Object[], String> fnc = messages.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName, target));
    }
    return String.format(ERR_INVALID_VALUE, argName, argVal(arg));
  }

  static Function<Object[], String> msgIsNull() {
    return x -> format("%s must be null (was %s)", argName(x), argVal(x[0]));
  }

  static Function<Object[], String> msgNotNull() {
    return x -> format("%s must not be null", argName(x));
  }

  static Function<Object[], String> msgNoneNull() {
    return x -> format("%s must not contain null values", argName(x));
  }

  static Function<Object[], String> msgDeepNotEmpty() {
    return x -> format("%s must not be empty or contain empty values", argName(x));
  }

  static Function<Object[], String> msgEmpty() {
    return x -> format("%s must be empty (was %s)", argName(x), argVal(x[0]));
  }

  static Function<Object[], String> msgNotEmpty() {
    return x -> format("%s must not be empty", argName(x));
  }

  static Function<Object[], String> msgNotBlank() {
    return x -> format("%s must not be whitespace-only", argName(x));
  }

  static Function<Object[], String> msgObjEquals() {
    return x -> format("%s must be equal to %s (was %s)", argName(x), argVal(x[2]), argVal(x[0]));
  }

  static Function<Object[], String> msgObjNotEquals() {
    return x -> format("%s must be not be equal to %s", argName(x), x[2]);
  }

  static Function<Object[], String> msgSizeAtMost() {
    return x -> format("%s must be <= %s (was %s)", argSize(x), x[2], x[0]);
  }

  static Function<Object[], String> msgSizeLessThan() {
    return x -> format("%s must be < %s (was %s)", argSize(x), x[2], x[0]);
  }

  static Function<Object[], String> msgSizeAtLeast() {
    return x -> format("%s must be >= %s (was %s)", argSize(x), x[2], x[0]);
  }

  static Function<Object[], String> msgSizeGreaterThan() {
    return x -> format("%s must be > %s (was %s)", argSize(x), x[2], x[0]);
  }

  static Function<Object[], String> msgSizeNotEquals() {
    return x -> format("%s must be not be equal to %s", argSize(x), x[2]);
  }

  static Function<Object[], String> msgSizeEquals() {
    return x -> format("%s must be equal to %s (was %s)", argSize(x), x[2], x[0]);
  }

  static Function<Object[], String> msgValidEndIndexFor() {
    return x -> {
      int i = ((List<?>) x[2]).size();
      return format("%s must be >= 0 and <= %d (was %d)", argName(x), i, x[0]);
    };
  }

  static Function<Object[], String> msgIsFile() {
    String fmt = "%s (%s) must be an existing file";
    return x -> format(fmt, argName(x), ((File) x[0]).getAbsolutePath());
  }

  static Function<Object[], String> msgIsDirectory() {
    String fmt = "%s (%s) must be an existing directory";
    return x -> format(fmt, argName(x), ((File) x[0]).getAbsolutePath());
  }

  static Function<Object[], String> msgFileNotExists() {
    String fmt = "%s (%s) must not exist";
    return x -> format(fmt, argName(x), ((File) x[0]).getAbsolutePath());
  }

  static Function<Object[], String> msgReadable() {
    String fmt = "%s (%s) must be readable";
    return x -> format(fmt, argName(x), ((File) x[0]).getAbsolutePath());
  }

  static Function<Object[], String> msgWritable() {
    String fmt = "%s (%s) must be writable";
    return x -> format(fmt, argName(x), ((File) x[0]).getAbsolutePath());
  }

  static Function<Object[], String> msgIsEven() {
    return x -> format("%s must be even (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgIsOdd() {
    return x -> format("%s must be odd (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgPositive() {
    return x -> format("%s must be positive (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgNotNegative() {
    return x -> format("%s must be zero or positive (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgNegative() {
    return x -> format("%s must be negative (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgNotPositive() {
    return x -> format("%s must be zero or negative (was %d)", argName(x), x[0]);
  }

  static Function<Object[], String> msgNullOr() {
    return x -> format("%s must be null or %s (was (%s)", argName(x), argVal(x[2]), argVal(x[0]));
  }

  static Function<Object[], String> msgContains() {
    return x -> format("%s must contain %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgNotContains() {
    return x -> format("%s must not contain %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgIn() {
    return x -> format("%s must be in %s (was %s)", argName(x), argVal(x[2]), argVal(x[0]));
  }

  static Function<Object[], String> msgNotIn() {
    return x -> format("%s must not be in %s (was %s)", argName(x), argVal(x[2]), argVal(x[0]));
  }

  static Function<Object[], String> msgContainsKey() {
    return x -> format("%s must contain key %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgNotContainsKey() {
    return x -> format("%s must not contain key %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgContainsValue() {
    return x -> format("%s must not contain value %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgNotContainsValue() {
    return x -> format("%s must not contain value %s", argName(x), argVal(x[2]));
  }

  static Function<Object[], String> msgEqualTo() {
    return x -> format("%s must be equal to %d (was %s)", argName(x), x[2], x[0]);
  }

  static Function<Object[], String> msgNotEquals() {
    return x -> format("%s must not be equal to %s", argName(x), x[2]);
  }

  static Function<Object[], String> msgGreaterThan() {
    return x -> format("%s must be > %s (was %s)", argName(x), x[2], x[0]);
  }

  static Function<Object[], String> msgAtLeast() {
    return x -> format("%s must be >= %s (was %s)", argName(x), x[2], x[0]);
  }

  static Function<Object[], String> msgLessThan() {
    return x -> format("%s must be < %s (was %s)", argName(x), x[2], x[0]);
  }

  static Function<Object[], String> msgAtMost() {
    return x -> format("%s must be <= %s (was %s)", argName(x), x[2], x[0]);
  }

  static Function<Object[], String> msgInstanceOf() {
    String fmt = "%s must be instance of %s (was %s)";
    return x -> format(fmt, argName(x), ((Class<?>) x[2]).getName(), cname(x[0]));
  }

  static Function<Object[], String> msgIsArray() {
    return x -> format("%s must be an array (was %s)", argName(x), cname(x[0]));
  }

  static Function<Object[], String> msgMultipleOf() {
    return x -> format("%s must be multiple of %d (was %d)", argName(x), x[2], x[0]);
  }

  private static String argSize(Object[] x) {
    if (x[0] instanceof CharSequence) {
      return argName(x) + '.' + CommonGetters.LENGTH;
    } else if (x[0].getClass().isArray()) {
      return argName(x) + '.' + CommonGetters.LENGTH;
    }
    return argName(x) + '.' + CommonGetters.SIZE;
  }

  private static String argName(Object[] x) {
    return x[1].toString();
  }

  private static String argVal(Object arg) {
    if (arg == null) {
      return "null";
    } else if (arg instanceof Number) {
      return arg.toString();
    } else if (arg == Boolean.class) {
      return arg.toString();
    } else if (arg == Character.class) {
      return arg.toString();
    } else if (arg instanceof Enum) {
      return arg.toString();
    } else if (arg instanceof CharSequence) {
      return truncate(arg);
    }
    if (arg.getClass() != Object.class) {
      try {
        arg.getClass().getDeclaredMethod("toString");
        return truncate(arg);
      } catch (NoSuchMethodException e) {
      }
    }
    return sname(arg) + '@' + System.identityHashCode(arg);
  }

  private static String truncate(Object arg) {
    String s = arg.toString();
    if (s.length() > 20) {
      return '"' + s.substring(0, 20) + "[...]\"";
    }
    return '"' + s + '"';
  }

  private static String cname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeName(obj);
    }
    return obj.getClass().getName();
  }

  private static String sname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeSimpleName(obj);
    }
    return obj.getClass().getSimpleName();
  }
}
