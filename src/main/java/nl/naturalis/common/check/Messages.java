package nl.naturalis.common.check;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.getArrayTypeName;
import static nl.naturalis.common.ClassMethods.getArrayTypeSimpleName;
import static nl.naturalis.common.check.CommonChecks.messages;

@SuppressWarnings("rawtypes")
class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  static String createMessage(Predicate test, String argName, Object argValue) {
    return message(test, argName, argValue);
  }

  static String createMessage(IntPredicate test, String argName, Object argValue) {
    return message(test, argName, argValue);
  }

  static String createMessage(Relation relation, String argName, Object subject, Object object) {
    return message(relation, argName, subject, object);
  }

  static String createMessage(IntRelation relation, String argName, Object subject, Object object) {
    return message(relation, argName, subject, object);
  }

  static String createMessage(
      ObjIntRelation relation, String argName, Object subject, Object object) {
    return message(relation, argName, subject, object);
  }

  static String createMessage(
      IntObjRelation relation, String argName, Object subject, Object object) {
    return message(relation, argName, subject, object);
  }

  private static String message(Object key, Object... msgArgs) {
    Function<Object[], String> fnc = messages.get(key);
    if (fnc != null) {
      return fnc.apply(msgArgs);
    }
    return String.format(ERR_INVALID_VALUE, msgArgs[0], argVal(msgArgs[1]));
  }

  static Function<Object[], String> msgNullRef() {
    return x -> format("%s must be null (was %s)", x[0], argVal(x[1]));
  }

  static Function<Object[], String> msgNotNull() {
    return x -> format("%s must not be null", x[0]);
  }

  static Function<Object[], String> msgNoneNull() {
    return x -> format("%s must not ne null or contain null values", x[0]);
  }

  static Function<Object[], String> msgDeepNotEmpty() {
    return x -> format("%s must not be empty or contain empty values", x[0]);
  }

  static Function<Object[], String> msgEmpty() {
    return x -> format("%s must be empty (was %s)", x[0], argVal(x[1]));
  }

  static Function<Object[], String> msgNotEmpty() {
    return x -> format("%s must not be null or empty", x[0]);
  }

  static Function<Object[], String> msgNotBlank() {
    return x -> format("%s must not be null or whitespace-only", x[0]);
  }

  static Function<Object[], String> msgEqualTo() {
    return x -> format("%s must be equal to %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgNotEqualTo() {
    return x -> format("%s must be not be equal to %s", x[0], x[2]);
  }

  static Function<Object[], String> msgSameAs() {
    return x -> {
      String id0 = cname(x[1]) + '@' + System.identityHashCode(x[1]);
      String id1 = cname(x[2]) + '@' + System.identityHashCode(x[2]);
      return format("%s must be have same identity as %s (was %s)", x[0], id1, id0);
    };
  }

  static Function<Object[], String> msgNotSameAs() {
    return x -> {
      String id = cname(x[2]) + '@' + System.identityHashCode(x[2]);
      return format("%s must be not have same identity as %s", x[0], id);
    };
  }

  static Function<Object[], String> msgSizeAtMost() {
    return x -> format("%s must be <= %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Function<Object[], String> msgSizeLessThan() {
    return x -> format("%s must be < %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Function<Object[], String> msgSizeAtLeast() {
    return x -> format("%s must be >= %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Function<Object[], String> msgSizeGreaterThan() {
    return x -> format("%s must be > %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Function<Object[], String> msgSizeNotEquals() {
    return x -> format("%s must be not be equal to %s", argSize(x), x[2]);
  }

  static Function<Object[], String> msgSizeEquals() {
    return x -> format("%s must be equal to %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Function<Object[], String> msgIndexOf() {
    return x -> {
      int i = ((List<?>) x[2]).size();
      return format("%s must be >= 0 and < %s (was %s)", x[0], i, x[1]);
    };
  }

  static Function<Object[], String> msgToIndexOf() {
    return x -> {
      int i = ((List<?>) x[2]).size();
      return format("%s must be >= 0 and <= %s (was %s)", x[0], i, x[1]);
    };
  }

  static Function<Object[], String> msgFileExists() {
    String fmt = "%s (%s) must be an existing file";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Function<Object[], String> msgDirectoryExists() {
    String fmt = "%s (%s) must be an existing directory";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Function<Object[], String> msgFileNotExists() {
    String fmt = "%s (%s) must not exist";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Function<Object[], String> msgReadable() {
    String fmt = "%s (%s) must be readable";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Function<Object[], String> msgWritable() {
    String fmt = "%s (%s) must be writable";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Function<Object[], String> msgIsEven() {
    return x -> format("%s must be even (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgIsOdd() {
    return x -> format("%s must be odd (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgPositive() {
    return x -> format("%s must be positive (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgNotNegative() {
    return x -> format("%s must be zero or positive (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgNegative() {
    return x -> format("%s must be negative (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgNotPositive() {
    return x -> format("%s must be zero or negative (was %d)", x[0], x[1]);
  }

  static Function<Object[], String> msgNullOr() {
    return x -> format("%s must be null or %s (was (%s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgContains() {
    return x -> format("%s must contain %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgNotContains() {
    return x -> format("%s must not contain %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgIn() {
    return x -> format("%s must be in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgNotIn() {
    return x -> format("%s must not be in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgHasKey() {
    return x -> format("%s must contain key %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgNotHasKey() {
    return x -> format("%s must not contain key %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgKeyIn() {
    return x -> format("%s must be key in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgNotKeyIn() {
    return x -> format("%s must not be key in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgHasValue() {
    return x -> format("%s must not contain value %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgNotHasValue() {
    return x -> format("%s must not contain value %s", x[0], argVal(x[2]));
  }

  static Function<Object[], String> msgValueIn() {
    return x -> format("%s must be value in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgNotValueIn() {
    return x -> format("%s must not be value in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Function<Object[], String> msgEq() {
    return x -> format("%s must be equal to %d (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgNe() {
    return x -> format("%s must not be equal to %s", x[0], x[2]);
  }

  static Function<Object[], String> msgGt() {
    return x -> format("%s must be > %s (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgGte() {
    return x -> format("%s must be >= %s (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgLt() {
    return x -> format("%s must be < %s (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgLte() {
    return x -> format("%s must be <= %s (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgEndsWith() {
    return x -> format("%s must end with \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgNotEndsWith() {
    return x -> format("%s must not end with \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgHasSubstr() {
    return x -> format("%s must contain \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgNotHasSubstr() {
    return x -> format("%s must not contain \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Function<Object[], String> msgInstanceOf() {
    String fmt = "%s must be instance of %s (was %s)";
    return x -> format(fmt, x[0], ((Class<?>) x[2]).getName(), cname(x[1]));
  }

  static Function<Object[], String> msgArray() {
    return x -> format("%s must be an array (was %s)", x[0], cname(x[1]));
  }

  static Function<Object[], String> msgMultipleOf() {
    return x -> format("%s must be multiple of %d (was %d)", x[0], x[2], x[1]);
  }

  private static String argSize(Object[] x) {
    if (x[1] instanceof CharSequence) {
      return x[0] + ".length()";
    } else if (x[1].getClass().isArray()) {
      return x[0] + ".length";
    }
    return x[0] + ".size()";
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
      return stringify(arg);
    } else if (arg.getClass() != Object.class) {
      try {
        arg.getClass().getDeclaredMethod("toString");
        return stringify(arg);
      } catch (Exception e) {
      }
    }
    return sname(arg) + '@' + System.identityHashCode(arg);
  }

  private static String stringify(Object arg) {
    StringBuilder sb = new StringBuilder();
    sb.append(sname(arg)).append('@').append(System.identityHashCode(arg)).append(" \"");
    String s = arg.toString();
    if (s.length() > 20) {
      sb.append(s.substring(0, 20)).append("[...]");
    } else {
      sb.append(s);
    }
    sb.append('"');
    return sb.toString();
  }

  // Returns fully-qualified class name
  private static String cname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeName(obj);
    }
    return obj.getClass().getName();
  }

  // Returns simple class name
  private static String sname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeSimpleName(obj);
    }
    return obj.getClass().getSimpleName();
  }
}
