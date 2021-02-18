package nl.naturalis.common.check;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static java.lang.String.format;
import static nl.naturalis.common.check.CommonChecks.messages;

@SuppressWarnings({"rawtypes", "unchecked"})
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
    Formatter fnc = messages.get(key);
    if (fnc != null) {
      return fnc.apply(msgArgs);
    }
    return String.format(ERR_INVALID_VALUE, msgArgs[0], argVal(msgArgs[1]));
  }

  static Formatter msgNullPointer() {
    return x -> format("%s must be null (was %s)", x[0], argVal(x[1]));
  }

  static Formatter msgNotNull() {
    return x -> format("%s must not be null", x[0]);
  }

  static Formatter msgYes() {
    return x -> format("%s must be true (was false)", x[0]);
  }

  static Formatter msgNo() {
    return x -> format("%s must be false (was true)", x[0]);
  }

  static Formatter msgNoneNull() {
    return x -> format("%s must not ne null or contain null values", x[0]);
  }

  static Formatter msgDeepNotEmpty() {
    return x -> format("%s must not be empty or contain empty values", x[0]);
  }

  static Formatter msgEmpty() {
    return x -> format("%s must be empty (was %s)", x[0], argVal(x[1]));
  }

  static Formatter msgNotEmpty() {
    return x -> format("%s must not be null or empty", x[0]);
  }

  static Formatter msgNotBlank() {
    return x -> format("%s must not be null or whitespace-only", x[0]);
  }

  static Formatter msgInteger() {
    return x -> format("%s must be an integer (was %s)", x[0], x[1]);
  }

  static Formatter msgValidPortNumber() {
    return x -> format("%s must be a valid TCP/UDP port number (was %s)", x[0], x[1]);
  }

  static Formatter msgEqualTo() {
    return x -> format("%s must be equal to %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgNotEqualTo() {
    return x -> format("%s must be not be equal to %s", x[0], x[2]);
  }

  static Formatter msgEqualsIgnoreCase() {
    return x -> {
      String fmt = "%s must be equal ignoring case to any of %s (was %s)";
      return format(fmt, x[0], argVal(x[2]), argVal(x[1]));
    };
  }

  static Formatter msgSameAs() {
    return x -> {
      String id0 = cname(x[1]) + '@' + System.identityHashCode(x[1]);
      String id1 = cname(x[2]) + '@' + System.identityHashCode(x[2]);
      return format("%s must be have same identity as %s (was %s)", x[0], id1, id0);
    };
  }

  static Formatter msgNotSameAs() {
    return x -> {
      String id = cname(x[2]) + '@' + System.identityHashCode(x[2]);
      return format("%s must be not have same identity as %s", x[0], id);
    };
  }

  static Formatter msgSizeAtMost() {
    return x -> format("%s must be <= %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Formatter msgSizeLessThan() {
    return x -> format("%s must be < %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Formatter msgSizeAtLeast() {
    return x -> format("%s must be >= %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Formatter msgSizeGreaterThan() {
    return x -> format("%s must be > %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Formatter msgSizeNotEquals() {
    return x -> format("%s must be not be equal to %s", argSize(x), x[2]);
  }

  static Formatter msgSizeEquals() {
    return x -> format("%s must be equal to %s (was %s)", argSize(x), x[2], x[1]);
  }

  static Formatter msgIndexOf() {
    return x -> {
      int i = ((List<?>) x[2]).size();
      return format("%s must be >= 0 and < %s (was %s)", x[0], i, x[1]);
    };
  }

  static Formatter msgToIndexOf() {
    return x -> {
      int i = ((List<?>) x[2]).size();
      return format("%s must be >= 0 and <= %s (was %s)", x[0], i, x[1]);
    };
  }

  static Formatter msgFileExists() {
    return x -> {
      File f = (File) x[1];
      String s = f.getAbsolutePath();
      if (f.exists()) {
        return format("%s must be a regular file (was %s)", x[0], s);
      } else if (noArgNameProvided(x)) {
        return format("Missing file: %s", s);
      }
      return format("Missing file: %s (%s)", s, x[0]);
    };
  }

  static Formatter msgDirectory() {
    return x -> {
      File f = (File) x[1];
      String s = f.getAbsolutePath();
      if (f.exists()) {
        return format("%s must be a directory (was %s)", x[0], s);
      } else if (noArgNameProvided(x)) {
        return format("Missing directory: %s", s);
      }
      return format("Missing directory: %s (%s)", s, x[0]);
    };
  }

  static Formatter msgFileNotExists() {
    return x -> {
      File f = (File) x[1];
      String s = f.getAbsolutePath();
      if (noArgNameProvided(x)) {
        return format("File already exists: %s", s);
      }
      return format("File already exists: %s (%s)", s, x[0]);
    };
  }

  static Formatter msgReadable() {
    String fmt = "%s (%s) must be readable";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Formatter msgWritable() {
    String fmt = "%s (%s) must be writable";
    return x -> format(fmt, x[0], ((File) x[1]).getAbsolutePath());
  }

  static Formatter msgEven() {
    return x -> format("%s must be even (was %d)", x[0], x[1]);
  }

  static Formatter msgOdd() {
    return x -> format("%s must be odd (was %d)", x[0], x[1]);
  }

  static Formatter msgPositive() {
    return x -> format("%s must be positive (was %d)", x[0], x[1]);
  }

  static Formatter msgNegative() {
    return x -> format("%s must be negative (was %d)", x[0], x[1]);
  }

  static Formatter msgNullOr() {
    return x -> format("%s must be null or %s (was (%s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgContaining() {
    return x -> format("%s must contain %s", x[0], argVal(x[2]));
  }

  static Formatter msgNotContaining() {
    return x -> format("%s must not contain %s", x[0], argVal(x[2]));
  }

  static Formatter msgIn() {
    return x -> format("%s must be in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgNotIn() {
    return x -> format("%s must not be in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgSupersetOf() {
    return x -> {
      Set set = Set.copyOf((Collection) x[2]);
      set.removeAll((Collection) x[1]);
      return format("%s must contain all of %s", x[0], argVal(set));
    };
  }

  static Formatter msgSubsetOf() {
    return x -> {
      Set set = Set.copyOf((Collection) x[1]);
      set.removeAll((Collection) x[2]);
      return format("%s must contain all of %s", x[0], argVal(set));
    };
  }

  static Formatter msgContainingKey() {
    return x -> format("%s must contain key %s", x[0], argVal(x[2]));
  }

  static Formatter msgNotContainingKey() {
    return x -> format("%s must not contain key %s", x[0], argVal(x[2]));
  }

  static Formatter msgKeyIn() {
    return x -> format("%s must be key in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgNotKeyIn() {
    return x -> format("%s must not be key in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgContainingValue() {
    return x -> format("%s must not contain value %s", x[0], argVal(x[2]));
  }

  static Formatter msgNotContainingValue() {
    return x -> format("%s must not contain value %s", x[0], argVal(x[2]));
  }

  static Formatter msgValueIn() {
    return x -> format("%s must be value in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgNotValueIn() {
    return x -> format("%s must not be value in %s (was %s)", x[0], argVal(x[2]), argVal(x[1]));
  }

  static Formatter msgEq() {
    return x -> format("%s must be equal to %d (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgNe() {
    return x -> format("%s must not be equal to %s", x[0], x[2]);
  }

  static Formatter msgGt() {
    return x -> format("%s must be > %s (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgGte() {
    return x -> format("%s must be >= %s (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgLt() {
    return x -> format("%s must be < %s (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgLte() {
    return x -> format("%s must be <= %s (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgEndsWith() {
    return x -> format("%s must end with \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgNotEndsWith() {
    return x -> format("%s must not end with \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgHasSubstr() {
    return x -> format("%s must contain \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgNotHasSubstr() {
    return x -> format("%s must not contain \"%s\" (was %s)", x[0], x[2], x[1]);
  }

  static Formatter msgInstanceOf() {
    return x -> {
      String cn = cname(x[1]);
      String fmt = "%s must be instance of %s (was %s)";
      return format(fmt, x[0], ((Class<?>) x[2]).getName(), cn);
    };
  }

  static Formatter msgNotInstanceOf() {
    return x -> {
      String cn = cname(x[1]);
      String fmt = "%s must not be instance of %s (was %s)";
      return format(fmt, x[0], ((Class<?>) x[2]).getName(), cn);
    };
  }

  static Formatter msgArray() {
    return x -> format("%s must be an array (was %s)", x[0], cname(x[1]));
  }

  static Formatter msgMultipleOf() {
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
    } else if (arg instanceof Collection && ((Collection) arg).size() <= 10) {
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
    String s = arg.toString();
    if (s.length() > 40) {
      sb.append(s.substring(0, 40)).append("[...]");
    } else {
      sb.append(s);
    }
    // sb.append(" ::: ").append(sname(arg)).append('@').append(System.identityHashCode(arg));
    if (arg instanceof Collection) {
      Collection c = (Collection) arg;
      if (c.isEmpty()) {
        sb.append(" (empty ").append(sname(c)).append(")");
      } else {
        sb.append(" (size=").append(c.size()).append(")");
      }
    }
    return sb.toString();
  }

  private static String cname(Object obj) {
    return ClassMethods.prettyClassName(obj);
  }

  private static String sname(Object obj) {
    return ClassMethods.prettySimpleClassName(obj);
  }

  private static boolean noArgNameProvided(Object[] msgArgs) {
    return msgArgs[0].equals(Check.DEF_ARG_NAME);
  }
}
