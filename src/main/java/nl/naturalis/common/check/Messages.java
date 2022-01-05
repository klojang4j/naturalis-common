package nl.naturalis.common.check;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.Pair;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.collection.TypeSet;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.asArray;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.StringMethods.concat;
import static nl.naturalis.common.StringMethods.ellipsis;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes"})
class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  static String createMessage(Predicate test, boolean negated, String argName, Object argValue) {
    MessageData md = new MessageData(test, negated, argName, argValue);
    return message(md);
  }

  static String createMessage(IntPredicate test, boolean negated, String argName, int argValue) {
    return message(new MessageData(test, negated, argName, argValue));
  }

  static String createMessage(
      Relation relation, boolean negated, String argName, Object subject, Object object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      IntRelation relation, boolean negated, String argName, int subject, int object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      ObjIntRelation relation, boolean negated, String argName, Object subject, int object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      IntObjRelation relation, boolean negated, String argName, int subject, Object object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  private static String message(MessageData md) {
    Formatter formatter = MESSAGE_PATTERNS.get(md.check());
    if (formatter != null) {
      return formatter.apply(md);
    }
    return String.format(ERR_INVALID_VALUE, md.argName(), toStr(md.argument()));
  }

  static Formatter msgNull() {
    return md -> {
      if (md.negated()) {
        return msgNotNull().apply(md);
      }
      return format("%s must be null (was %s)", md.argName(), toStr(md.argument()));
    };
  }

  static Formatter msgNotNull() {
    return md -> {
      if (md.negated()) {
        return msgNull().apply(md);
      }
      return format("%s must not be null", md.argName());
    };
  }

  static Formatter msgYes() {
    return md -> {
      if (md.negated()) {
        return msgNo().apply(md);
      }
      return format("%s must be true (was false)", md.argName());
    };
  }

  static Formatter msgNo() {
    return md -> {
      if (md.negated()) {
        return msgYes().apply(md);
      }
      return format("%s must be false (was true)", md.argName());
    };
  }

  static Formatter msgNeverNull() {
    return md -> {
      if (md.negated()) { // Negation is nonsense, but OK
        format("%s must be null or contain one or more null values", md.argName());
      }
      return format("%s must not be null or contain null values", md.argName());
    };
  }

  static Formatter msgDeepNotEmpty() {
    return md -> {
      if (md.negated()) { // Idem
        String fmt = "%s must be empty or contain or contain or more empty values";
        return format(fmt, md.argName());
      }
      return format("%s must not be empty or contain empty values", md.argName());
    };
  }

  static Formatter msgEmpty() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be null or empty", md.argName());
      }
      return format("%s must be empty (was %s)", md.argName(), toStr(md.argument()));
    };
  }

  static Formatter msgBlank() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be null or blank", md.argName());
      }
      return format("%s must be null or blank (was %s)", md.argName(), md.argument());
    };
  }

  static Formatter msgInteger() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be an integer (was %s)", md.argName(), md.argument());
      }
      String fmt = "%s must be an integer (was %s)";
      return format(fmt, md.argName(), md.argument(), className(md.argument()));
    };
  }

  static Formatter msgNumber() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be a number (was %s)", md.argName(), md.argument());
      }
      String fmt = "%s must be a number (was %s)";
      return format(fmt, md.argName(), md.argument(), className(md.argument()));
    };
  }

  static Formatter msgValidPort() {
    return md -> {
      if (md.negated()) { // Must be an interesting application
        String fmt = "%s must not be valid TCP/UDP port (was %s)";
        return format(fmt, md.argName(), md.argument());
      }
      String fmt = "%s must be valid TCP/UDP port (was %s)";
      return format(fmt, md.argName(), md.argument());
    };
  }

  static Formatter msgEqualTo() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be equal to %s", toStr(md.object()));
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgEqualsIgnoreCase() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be equal ignoring case to any of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be equal ignoring case to any of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgSameAs() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be %s";
        String id0 = className(md.object()) + '@' + System.identityHashCode(md.object());
        return format(fmt, md.argName(), id0);
      }
      String fmt = "%s must be %s (was %s)";
      String id0 = className(md.object()) + '@' + System.identityHashCode(md.object());
      String id1 = className(md.argument()) + '@' + System.identityHashCode(md.argument());
      return format(fmt, md.argName(), id0, id1);
    };
  }

  static Formatter msgSizeEquals() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s.size() must not be equal to %s";
        return format(fmt, md.argName(), md.object());
      }
      String fmt = "%s.size() must be equal to %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.argument()).size());
    };
  }

  static Formatter msgSizeGT() {
    return md -> {
      if (md.negated()) {
        return msgSizeLTE().apply(md.flip());
      }
      String fmt = "%s.size() must be > %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.argument()).size());
    };
  }

  static Formatter msgSizeGTE() {
    return md -> {
      if (md.negated()) {
        return msgSizeLT().apply(md.flip());
      }
      String fmt = "%s.size() must be >= %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.argument()).size());
    };
  }

  static Formatter msgSizeLT() {
    return md -> {
      if (md.negated()) {
        return msgSizeGTE().apply(md.flip());
      }
      String fmt = "%s.size() must be < %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.argument()).size());
    };
  }

  static Formatter msgSizeLTE() {
    return md -> {
      if (md.negated()) {
        return msgSizeGT().apply(md.flip());
      }
      String fmt = "%s.size() must be <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.argument()).size());
    };
  }

  static Formatter msgBetween() {
    return md -> {
      Pair pair = (Pair) md.object();
      String fmt;
      if (md.negated()) {
        fmt = "%s must be < %s or >= %s (was %s)";
      } else {
        fmt = "%s must be >= %s and < %s (was %s)";
      }
      return format(fmt, md.argName(), pair.getFirst(), pair.getSecond(), md.argument());
    };
  }

  static Formatter msgInRangeClosed() {
    return md -> {
      Pair pair = (Pair) md.object();
      String fmt;
      if (md.negated()) {
        fmt = "%s must be < %s or > %s (was %s)";
      } else {
        fmt = "%s must be >= %s and <= %s (was %s)";
      }
      return format(fmt, md.argName(), pair.getFirst(), pair.getSecond(), md.argument());
    };
  }

  static Formatter msgIndexOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must < 0 or >= %s (was %s)";
        return format(fmt, md.argName(), md.object(), md.argument());
      }
      String fmt = "%s must be >= 0 and < %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgValidFromIndex() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must < 0 or > %s (was %s)";
        return format(fmt, md.argName(), md.object(), md.argument());
      }
      String fmt = "%s must be >= 0 and <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgFile() {
    return md -> {
      File f = (File) md.argument();
      if (md.negated()) {
        return format("File already exists: %s", ((File) md.argument()).getAbsolutePath());
      } else if (f.isDirectory()) {
        // File indeed exists, but alas is a directory
        return format("%s must not be a directory (was %s)", md.argName(), f.getAbsolutePath());
      }
      // File not present at all
      return format("File not found: %s", ((File) md.argument()).getAbsolutePath());
    };
  }

  static Formatter msgDirectory() {
    return md -> {
      File f = (File) md.argument();
      if (md.negated()) {
        return format("Directory already exists: %s", ((File) md.argument()).getAbsolutePath());
      } else if (f.isDirectory()) {
        return format("%s must not be a file (was %s)", md.argName(), f.getAbsolutePath());
      }
      return format("Missing directory: %s", ((File) md.argument()).getAbsolutePath());
    };
  }

  static Formatter msgPresent() {
    return md -> {
      File f = (File) md.argument();
      if (md.negated()) {
        if (f.isDirectory()) {
          return format("Directory already exists: %s", f.getAbsolutePath());
        }
        return format("File already exists: %s", f.getAbsolutePath());
      }
      return format("Missing file/directory: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgReadable() {
    return md -> {
      File f = (File) md.argument();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (md.negated()) {
        if (f.isDirectory()) {
          return format("Directory must not be readable: %s", f.getAbsolutePath());
        }
        return format("File must not be readable: %s", f.getAbsolutePath());
      }
      if (f.isDirectory()) {
        return format("Directory must be readable: %s", f.getAbsolutePath());
      }
      return format("File must be readable: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgWritable() {
    return md -> {
      File f = (File) md.argument();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (md.negated()) {
        if (f.isDirectory()) {
          return format("Directory must not be writable: %s", f.getAbsolutePath());
        }
        return format("File must not be writable: %s", f.getAbsolutePath());
      }
      if (f.isDirectory()) {
        return format("Directory must be writable: %s", f.getAbsolutePath());
      }
      return format("File must be writable: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgEven() {
    return md -> {
      if (md.negated()) {
        return msgOdd().apply(md.flip());
      }
      return format("%s must be even (was %d)", md.argName(), md.argument());
    };
  }

  static Formatter msgOdd() {
    return md -> {
      if (md.negated()) {
        return msgEven().apply(md.flip());
      }
      return format("%s must be odd (was %d)", md.argName(), md.argument());
    };
  }

  static Formatter msgPositive() {
    return md -> {
      String not = md.negated() ? " not" : "";
      return format("%s must%s be positive (was %d)", md.argName(), not, md.argument());
    };
  }

  static Formatter msgNegative() {
    return md -> {
      String not = md.negated() ? " not" : "";
      return format("%s must%s be negative (was %d)", md.argName(), not, md.argument());
    };
  }

  static Formatter msgNullOr() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be null or %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be null or %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgContaining() {
    return md -> {
      if (md.negated()) {
        return format("%s must not contain %s", md.argName(), toStr(md.object()));
      }
      String fmt = "%s must contain %s";
      return format(fmt, md.argName(), toStr(md.object()));
    };
  }

  static Formatter msgIn() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be element of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be element of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgSupersetOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be superset of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be superset of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgSubsetOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgContainingKey() {
    return md -> {
      if (md.negated()) {
        return format("%s must not contain key %s", md.argName(), toStr(md.object()));
      }
      return format("%s must contain key %s", md.argName(), toStr(md.object()));
    };
  }

  static Formatter msgKeyIn() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be key in %s (parameter \"%s\")";
        return format(fmt, toStr(md.argument()), toStr(md.object()), md.argName());
      }
      String fmt = "%s must be key in %s (parameter \"%s\")";
      return format(fmt, toStr(md.argument()), toStr(md.object()), md.argName());
    };
  }

  static Formatter msgContainingValue() {
    return md -> {
      if (md.negated()) {
        return format("%s must not contain value %s", md.argName(), toStr(md.object()));
      }
      return format("%s must contain value %s", md.argName(), toStr(md.object()));
    };
  }

  static Formatter msgValueIn() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be value in %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must be value in %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgZero() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be zero", md.argName());
      }
      String fmt = "%s must be zero (was %s)";
      return format(fmt, md.argName(), md.argument());
    };
  }

  static Formatter msgEq() {
    return md -> {
      if (md.negated()) {
        return msgNe().apply(md.flip());
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgNe() {
    return md -> {
      if (md.negated()) {
        return msgEq().apply(md.flip());
      }
      String fmt = "%s must not be equal to %s";
      return format(fmt, md.argName(), md.object());
    };
  }

  static Formatter msgGreaterThan() {
    return md -> {
      if (md.negated()) {
        return msgAtMost().apply(md.flip());
      }
      String fmt = "%s must be > %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgAtLeast() {
    return md -> {
      if (md.negated()) {
        return msgLessThan().apply(md.flip());
      }
      String fmt = "%s must be >= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgLessThan() {
    return md -> {
      if (md.negated()) {
        return msgAtLeast().apply(md.flip());
      }
      String fmt = "%s must be < %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgAtMost() {
    return md -> {
      if (md.negated()) {
        return msgGreaterThan().apply(md.flip());
      }
      String fmt = "%s must be <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.argument());
    };
  }

  static Formatter msgEndsWith() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not end with \"%s\" (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must end with \"%s\" (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgHasSubstr() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not contain \"%s\" (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
      }
      String fmt = "%s must contain \"%s\" (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgInstanceOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be instance of %s";
        return format(fmt, md.argName(), className(md.object()));
      }
      String fmt = "%s must be instance of %s (was %s)";
      String cn0 = className(md.object());
      String cn1 = className(md.argument());
      return format(fmt, md.argName(), cn0, cn1, toStr(md.argument()));
    };
  }

  static Formatter msgArray() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be an array (was %s)";
        return format(fmt, md.argName(), className(md.argument()));
      }
      String fmt = "%s must be an array (was %s)";
      return format(fmt, md.argName(), className(md.argument()));
    };
  }

  static Formatter msgMultipleOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be multiple of %s (was %s)";
        return format(fmt, md.argName(), md.argument());
      }
      String fmt = "%s must be multiple of %s (was %s)";
      return format(fmt, md.argName(), md.argument());
    };
  }

  private static final Set<Class<?>> DECENT_TO_STRING =
      TypeSet.withTypes(Number.class, Boolean.class, Character.class, Enum.class);

  static String toStr(Object val) {
    if (val == null) {
      return "null";
    } else if (DECENT_TO_STRING.contains(val.getClass())) {
      return val.toString();
    } else if (val instanceof CharSequence) {
      return ellipsis(val.toString(), 40);
    } else if (val.getClass() == Class.class) {
      return simpleClassName(val);
    } else if (val instanceof Collection) {
      Collection c = (Collection) val;
      return concat(
          simpleClassName(val), "[", c.size(), "] of [", StringMethods.implode(c, ", ", 10), "]");
    } else if (val.getClass().isArray()) {
      Object[] a = asArray(val);
      return concat(
          simpleClassName(val), "[", a.length, "] of [", ArrayMethods.implode(a, ", ", 10), "]");
    } else if (val.getClass() != Object.class) {
      try {
        // If the class has its own toString() method, it's probably informative
        val.getClass().getDeclaredMethod("toString");
        return val.toString();
      } catch (Exception e) {
      }
    }
    return simpleClassName(val) + '@' + System.identityHashCode(val);
  }
}
