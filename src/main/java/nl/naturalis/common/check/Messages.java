package nl.naturalis.common.check;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.Pair;
import nl.naturalis.common.collection.TypeSet;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.StringMethods.ellipsis;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes", "unchecked"})
class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /*
   * Max. display width (characters) for argument values and for values used as the object in
   * Relation-type checks.
   */
  private static final int MAX_ARG_WIDTH = 50;

  static String createMessage(Object predicate, boolean negated, String argName, Object argValue) {
    MsgArgs md = new MsgArgs(predicate, negated, argName, argValue);
    return message(md);
  }

  static String createMessage(
      Object relation, boolean negated, String argName, Object argValue, Object object) {
    return message(new MsgArgs(relation, negated, argName, argValue, object));
  }

  private static String message(MsgArgs md) {
    Formatter formatter = MESSAGE_PATTERNS.get(md.check());
    if (formatter != null) {
      return formatter.apply(md);
    }
    return String.format(ERR_INVALID_VALUE, md.argName(), toStr(md.arg()));
  }

  static Formatter msgNull() {
    return md -> {
      if (md.negated()) {
        return msgNotNull().apply(md.flip());
      }
      return format("%s must be null (was %s)", md.argName(), toStr(md.arg()));
    };
  }

  static Formatter msgNotNull() {
    return md -> {
      if (md.negated()) {
        return msgNull().apply(md.flip());
      }
      return format("%s must not be null", md.argName());
    };
  }

  static Formatter msgYes() {
    return md -> {
      if (md.negated()) {
        return msgNo().apply(md.flip());
      }
      return format("%s must be true (was false)", md.argName());
    };
  }

  static Formatter msgNo() {
    return md -> {
      if (md.negated()) {
        return msgYes().apply(md.flip());
      }
      return format("%s must be false (was true)", md.argName());
    };
  }

  static Formatter msgDeepNotNull() {
    return md -> {
      if (md.negated()) { // Negation is total nonsense, but OK
        return format(
            "%s must be null or contain one or more null values (was %s)",
            md.argName(), toStr(md.arg()));
      }
      return format(
          "%s must not be null or contain null values (was %s)", md.argName(), toStr(md.arg()));
    };
  }

  static Formatter msgDeepNotEmpty() {
    return md -> {
      if (md.negated()) { // idem
        String fmt = "%s must be empty or contain or one or more empty values (was %s)";
        return format(fmt, md.argName(), toStr(md.arg()));
      }
      return format(
          "%s must not be empty or contain empty values (was %s)", md.typeAndName(), md.arg());
    };
  }

  static Formatter msgEmpty() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be null or empty (was %s)", md.argName(), toStr(md.arg()));
      }
      return format("%s must be empty (was %s)", md.argName(), toStr(md.arg()));
    };
  }

  static Formatter msgBlank() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be null or blank (was %s)", md.argName(), toStr(md.arg()));
      }
      return format("%s must be null or blank (was %s)", md.argName(), toStr(md.arg()));
    };
  }

  static Formatter msgInteger() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be an integer (was %s)", md.argName(), md.arg());
      }
      String fmt = "%s must be an integer (was %s)";
      return format(fmt, md.argName(), md.arg(), className(md.arg()));
    };
  }

  static Formatter msgEqualTo() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be equal to %s", md.argName(), toStr(md.object()));
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
    };
  }

  static Formatter msgEqualsIgnoreCase() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be equal ignoring case to any of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be equal ignoring case to any of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
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
      String id1 = className(md.arg()) + '@' + System.identityHashCode(md.arg());
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
      return format(fmt, md.argName(), md.object(), ((Collection) md.arg()).size());
    };
  }

  static Formatter msgSizeGT() {
    return md -> {
      if (md.negated()) {
        return msgSizeLTE().apply(md.flip());
      }
      String fmt = "%s.size() must be > %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.arg()).size());
    };
  }

  static Formatter msgSizeGTE() {
    return md -> {
      if (md.negated()) {
        return msgSizeLT().apply(md.flip());
      }
      String fmt = "%s.size() must be >= %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.arg()).size());
    };
  }

  static Formatter msgSizeLT() {
    return md -> {
      if (md.negated()) {
        return msgSizeGTE().apply(md.flip());
      }
      String fmt = "%s.size() must be < %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.arg()).size());
    };
  }

  static Formatter msgSizeLTE() {
    return md -> {
      if (md.negated()) {
        return msgSizeGT().apply(md.flip());
      }
      String fmt = "%s.size() must be <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), ((Collection) md.arg()).size());
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
      return format(fmt, md.argName(), pair.one(), pair.two(), md.arg());
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
      return format(fmt, md.argName(), pair.one(), pair.two(), md.arg());
    };
  }

  static Formatter msgIndexOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must < 0 or >= %s (was %s)";
        return format(fmt, md.argName(), md.object(), md.arg());
      }
      String fmt = "%s must be >= 0 and < %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgValidFromIndex() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must < 0 or > %s (was %s)";
        return format(fmt, md.argName(), md.object(), md.arg());
      }
      String fmt = "%s must be >= 0 and <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgFile() {
    return md -> {
      File f = (File) md.arg();
      if (md.negated()) {
        return format("File already exists: %s", md.arg());
      } else if (f.isDirectory()) {
        // File indeed exists, but is a directory
        return format("%s must not be a directory (was %s)", md.argName(), f);
      }
      // File not present at all
      return format("File not found: %s", md.arg());
    };
  }

  static Formatter msgDirectory() {
    return md -> {
      File f = (File) md.arg();
      if (md.negated()) {
        return format("Directory already exists: %s", md.arg());
      } else if (f.isFile()) {
        return format("%s must not be a file (was %s)", md.argName(), f);
      }
      return format("Directory not found: %s", md.arg());
    };
  }

  static Formatter msgOnFileSystem() {
    return md -> {
      File f = (File) md.arg();
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
      File f = (File) md.arg();
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
      File f = (File) md.arg();
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
      return format("%s must be even (was %s)", md.argName(), md.arg());
    };
  }

  static Formatter msgOdd() {
    return md -> {
      if (md.negated()) {
        return msgEven().apply(md.flip());
      }
      return format("%s must be odd (was %s)", md.argName(), md.arg());
    };
  }

  static Formatter msgPositive() {
    return md -> {
      String not = md.negated() ? " not" : "";
      return format("%s must%s be positive (was %s)", md.argName(), not, md.arg());
    };
  }

  static Formatter msgNegative() {
    return md -> {
      String not = md.negated() ? " not" : "";
      return format("%s must%s be negative (was %s)", md.argName(), not, md.arg());
    };
  }

  static Formatter msgNullOr() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be null or %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be null or %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
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
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be element of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
    };
  }

  static Formatter msgSupersetOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be superset of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be superset of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
    };
  }

  static Formatter msgSubsetOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
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
        return format(fmt, toStr(md.arg()), toStr(md.object()), md.argName());
      }
      String fmt = "%s must be key in %s (parameter \"%s\")";
      return format(fmt, toStr(md.arg()), toStr(md.object()), md.argName());
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
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must be value in %s (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
    };
  }

  static Formatter msgZero() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be zero", md.argName());
      }
      String fmt = "%s must be zero (was %s)";
      return format(fmt, md.argName(), md.arg());
    };
  }

  static Formatter msgEq() {
    return md -> {
      if (md.negated()) {
        return msgNe().apply(md.flip());
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
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
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgAtLeast() {
    return md -> {
      if (md.negated()) {
        return msgLessThan().apply(md.flip());
      }
      String fmt = "%s must be >= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgLessThan() {
    return md -> {
      if (md.negated()) {
        return msgAtLeast().apply(md.flip());
      }
      String fmt = "%s must be < %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgAtMost() {
    return md -> {
      if (md.negated()) {
        return msgGreaterThan().apply(md.flip());
      }
      String fmt = "%s must be <= %s (was %s)";
      return format(fmt, md.argName(), md.object(), md.arg());
    };
  }

  static Formatter msgEndsWith() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not end with \"%s\" (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must end with \"%s\" (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
    };
  }

  static Formatter msgHasSubstr() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not contain \"%s\" (was %s)";
        return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
      }
      String fmt = "%s must contain \"%s\" (was %s)";
      return format(fmt, md.argName(), toStr(md.object()), toStr(md.arg()));
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
      String cn1 = className(md.arg());
      return format(fmt, md.argName(), cn0, cn1, toStr(md.arg()));
    };
  }

  static Formatter msgArray() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be an array (was %s)";
        return format(fmt, md.argName(), className(md.arg()));
      }
      String fmt = "%s must be an array (was %s)";
      return format(fmt, md.argName(), className(md.arg()));
    };
  }

  static Formatter msgMultipleOf() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must not be multiple of %s (was %s)";
        return format(fmt, md.argName(), md.arg());
      }
      String fmt = "%s must be multiple of %s (was %s)";
      return format(fmt, md.argName(), md.arg());
    };
  }

  private static final Set<Class<?>> DECENT_TO_STRING =
      TypeSet.of(Number.class, Boolean.class, Character.class, Enum.class);

  static String toStr(Object val) {
    if (val == null) {
      return "null";
    }
    Class type = val.getClass();
    if (DECENT_TO_STRING.contains(type)) {
      return val.toString();
    } else if (val instanceof CharSequence) {
      if (((CharSequence) val).toString().isBlank()) {
        return "\"" + val + "\"";
      }
      return ellipsis(val.toString(), MAX_ARG_WIDTH);
    } else if (type == Class.class) {
      return simpleClassName(val);
    } else if (val instanceof Collection) {
      return collectionToString((Collection) val);
    } else if (val instanceof Map) {
      return mapToString((Map) val);
    } else if (type.isArray()) {
      return arrayToString(val);
    } else if (type != Object.class) {
      try {
        // If the class has its own toString() method, it's probably interesting
        type.getDeclaredMethod("toString");
        return ellipsis(val.toString(), MAX_ARG_WIDTH);
      } catch (NoSuchMethodException e) {
        // ...
      }
    }
    return classNameAbbrev(type) + '@' + System.identityHashCode(val);
  }

  private static String collectionToString(Collection c) {
    String scn = simpleClassName(c) + "[" + c.size() + "]";
    if (c.size() == 0) {
      return scn;
    }
    String sep = DEFAULT_IMPLODE_SEPARATOR;
    String imploded = trim(implode(c, Messages::toStr, sep, 0, 10), c.size());
    return new StringBuilder(32)
        .append(scn)
        .append(" of [")
        .append(imploded)
        .append(']')
        .toString();
  }

  private static String mapToString(Map m) {
    String scn = simpleClassName(m) + "[" + m.size() + "]";
    if (m.size() == 0) {
      return scn;
    }
    String sep = DEFAULT_IMPLODE_SEPARATOR;
    String imploded = trim(implode(m.entrySet(), Messages::entryToString, sep, 0, 10), m.size());
    return new StringBuilder(32)
        .append(scn)
        .append(" of {")
        .append(imploded)
        .append('}')
        .toString();
  }

  private static String arrayToString(Object array) {
    int len = Array.getLength(array);
    String scn = simpleClassName(array);
    scn = scn.replaceFirst("\\[]", "[" + len + "]");
    if (len == 0) {
      return scn;
    }
    String sep = DEFAULT_IMPLODE_SEPARATOR;
    String imploded = trim(ArrayMethods.implodeAny(array, Messages::toStr, sep, 0, 10), len);
    return new StringBuilder(32)
        .append(scn)
        .append(" of [")
        .append(imploded)
        .append(']')
        .toString();
  }

  private static String entryToString(Map.Entry entry) {
    return toStr(entry.getKey()) + ": " + toStr(entry.getValue());
  }

  private static String trim(String imploded, int sz) {
    if (sz > 10) {
      if (imploded.length() > MAX_ARG_WIDTH) {
        imploded = ellipsis(imploded, MAX_ARG_WIDTH);
      } else {
        imploded += "...";
      }
    } else if (imploded.length() > MAX_ARG_WIDTH) {
      imploded = ellipsis(imploded, MAX_ARG_WIDTH);
    }
    return imploded;
  }

  private static String classNameAbbrev(Class type) {
    String[] pkgs = type.getPackageName().split("\\.");
    String pkg = ArrayMethods.implode(pkgs, s -> s.substring(0, 1), ".", 0, -1);
    return pkg + '.' + simpleClassName(type);
  }
}
