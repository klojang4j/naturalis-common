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
    MsgArgs args = new MsgArgs(predicate, negated, argName, argValue);
    return message(args);
  }

  static String createMessage(
      Object relation, boolean negated, String argName, Object argValue, Object object) {
    return message(new MsgArgs(relation, negated, argName, argValue, object));
  }

  private static String message(MsgArgs args) {
    Formatter formatter = MESSAGE_PATTERNS.get(args.check());
    if (formatter != null) {
      return formatter.apply(args);
    }
    return String.format(ERR_INVALID_VALUE, args.argName(), toStr(args.arg()));
  }

  static Formatter msgNull() {
    return args -> {
      if (args.negated()) {
        return msgNotNull().apply(args.flip());
      }
      return format("%s must be null (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgNotNull() {
    return args -> {
      if (args.negated()) {
        return msgNull().apply(args.flip());
      }
      return format("%s must not be null", args.argName());
    };
  }

  static Formatter msgYes() {
    return args -> {
      if (args.negated()) {
        return msgNo().apply(args.flip());
      }
      return format("%s must be true (was false)", args.argName());
    };
  }

  static Formatter msgNo() {
    return args -> {
      if (args.negated()) {
        return msgYes().apply(args.flip());
      }
      return format("%s must be false (was true)", args.argName());
    };
  }

  static Formatter msgDeepNotNull() {
    return args -> {
      if (args.negated()) { // Negation is total nonsense, but OK
        return format(
            "%s must be null or contain one or more null values (was %s)",
            args.argName(), toStr(args.arg()));
      }
      return format(
          "%s must not be null or contain null values (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgDeepNotEmpty() {
    return args -> {
      if (args.negated()) { // idem
        String fmt = "%s must be empty or contain or one or more empty values (was %s)";
        return format(fmt, args.argName(), toStr(args.arg()));
      }
      return format(
          "%s must not be empty or contain empty values (was %s)",
          args.typeAndName(), toStr(args.arg()));
    };
  }

  static Formatter msgEmpty() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be null or empty (was %s)", args.argName(), toStr(args.arg()));
      }
      return format("%s must be empty (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgBlank() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be null or blank (was %s)", args.argName(), toStr(args.arg()));
      }
      return format("%s must be null or blank (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgInteger() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be an integer (was %s)", args.argName(), args.arg());
      }
      String fmt = "%s must be an integer (was %s)";
      return format(fmt, args.argName(), args.arg(), className(args.arg()));
    };
  }

  static Formatter msgEqualTo() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be equal to %s", args.argName(), toStr(args.object()));
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgEqualsIgnoreCase() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be equal ignoring case to any of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be equal ignoring case to any of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSameAs() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be %s";
        String id0 = className(args.object()) + '@' + System.identityHashCode(args.object());
        return format(fmt, args.argName(), id0);
      }
      String fmt = "%s must be %s (was %s)";
      String id0 = className(args.object()) + '@' + System.identityHashCode(args.object());
      String id1 = className(args.arg()) + '@' + System.identityHashCode(args.arg());
      return format(fmt, args.argName(), id0, id1);
    };
  }

  static Formatter msgSizeEquals() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s.size() must not be equal to %s";
        return format(fmt, args.argName(), args.object());
      }
      String fmt = "%s.size() must be equal to %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeGT() {
    return args -> {
      if (args.negated()) {
        return msgSizeLTE().apply(args.flip());
      }
      String fmt = "%s.size() must be > %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeGTE() {
    return args -> {
      if (args.negated()) {
        return msgSizeLT().apply(args.flip());
      }
      String fmt = "%s.size() must be >= %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeLT() {
    return args -> {
      if (args.negated()) {
        return msgSizeGTE().apply(args.flip());
      }
      String fmt = "%s.size() must be < %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeLTE() {
    return args -> {
      if (args.negated()) {
        return msgSizeGT().apply(args.flip());
      }
      String fmt = "%s.size() must be <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgBetween() {
    return args -> {
      Pair pair = (Pair) args.object();
      String fmt;
      if (args.negated()) {
        fmt = "%s must be < %s or >= %s (was %s)";
      } else {
        fmt = "%s must be >= %s and < %s (was %s)";
      }
      return format(fmt, args.argName(), pair.one(), pair.two(), args.arg());
    };
  }

  static Formatter msgInRangeClosed() {
    return args -> {
      Pair pair = (Pair) args.object();
      String fmt;
      if (args.negated()) {
        fmt = "%s must be < %s or > %s (was %s)";
      } else {
        fmt = "%s must be >= %s and <= %s (was %s)";
      }
      return format(fmt, args.argName(), pair.one(), pair.two(), args.arg());
    };
  }

  static Formatter msgIndexOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must < 0 or >= %s (was %s)";
        return format(fmt, args.argName(), args.object(), args.arg());
      }
      String fmt = "%s must be >= 0 and < %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgValidFromIndex() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must < 0 or > %s (was %s)";
        return format(fmt, args.argName(), args.object(), args.arg());
      }
      String fmt = "%s must be >= 0 and <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgFile() {
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        return format("File already exists: %s", args.arg());
      } else if (f.isDirectory()) {
        // File indeed exists, but is a directory
        return format("%s must not be a directory (was %s)", args.argName(), f);
      }
      // File not present at all
      return format("File not found: %s", args.arg());
    };
  }

  static Formatter msgDirectory() {
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        return format("Directory already exists: %s", args.arg());
      } else if (f.isFile()) {
        return format("%s must not be a file (was %s)", args.argName(), f);
      }
      return format("Directory not found: %s", args.arg());
    };
  }

  static Formatter msgOnFileSystem() {
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        if (f.isDirectory()) {
          return format("Directory already exists: %s", f.getAbsolutePath());
        }
        return format("File already exists: %s", f.getAbsolutePath());
      }
      return format("Missing file/directory: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgReadable() {
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (args.negated()) {
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
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (args.negated()) {
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
    return args -> {
      if (args.negated()) {
        return msgOdd().apply(args.flip());
      }
      return format("%s must be even (was %s)", args.argName(), args.arg());
    };
  }

  static Formatter msgOdd() {
    return args -> {
      if (args.negated()) {
        return msgEven().apply(args.flip());
      }
      return format("%s must be odd (was %s)", args.argName(), args.arg());
    };
  }

  static Formatter msgPositive() {
    return args -> {
      String not = args.negated() ? " not" : "";
      return format("%s must%s be positive (was %s)", args.argName(), not, args.arg());
    };
  }

  static Formatter msgNegative() {
    return args -> {
      String not = args.negated() ? " not" : "";
      return format("%s must%s be negative (was %s)", args.argName(), not, args.arg());
    };
  }

  static Formatter msgNullOr() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be null or %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be null or %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgContaining() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain %s", args.argName(), toStr(args.object()));
      }
      String fmt = "%s must contain %s";
      return format(fmt, args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be element of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be element of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSupersetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be superset of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be superset of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSubsetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgContainingKey() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain key %s", args.argName(), toStr(args.object()));
      }
      return format("%s must contain key %s", args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgKeyIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be key in %s (parameter \"%s\")";
        return format(fmt, toStr(args.arg()), toStr(args.object()), args.argName());
      }
      String fmt = "%s must be key in %s (parameter \"%s\")";
      return format(fmt, toStr(args.arg()), toStr(args.object()), args.argName());
    };
  }

  static Formatter msgContainingValue() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain value %s", args.argName(), toStr(args.object()));
      }
      return format("%s must contain value %s", args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgValueIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be value in %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be value in %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgZero() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be zero", args.argName());
      }
      String fmt = "%s must be zero (was %s)";
      return format(fmt, args.argName(), args.arg());
    };
  }

  static Formatter msgEq() {
    return args -> {
      if (args.negated()) {
        return msgNe().apply(args.flip());
      }
      String fmt = "%s must be equal to %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgNe() {
    return args -> {
      if (args.negated()) {
        return msgEq().apply(args.flip());
      }
      String fmt = "%s must not be equal to %s";
      return format(fmt, args.argName(), args.object());
    };
  }

  static Formatter msgGreaterThan() {
    return args -> {
      if (args.negated()) {
        return msgAtMost().apply(args.flip());
      }
      String fmt = "%s must be > %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgAtLeast() {
    return args -> {
      if (args.negated()) {
        return msgLessThan().apply(args.flip());
      }
      String fmt = "%s must be >= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgLessThan() {
    return args -> {
      if (args.negated()) {
        return msgAtLeast().apply(args.flip());
      }
      String fmt = "%s must be < %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgAtMost() {
    return args -> {
      if (args.negated()) {
        return msgGreaterThan().apply(args.flip());
      }
      String fmt = "%s must be <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgEndsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not end with \"%s\" (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must end with \"%s\" (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgHasSubstr() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not contain \"%s\" (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must contain \"%s\" (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgInstanceOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be instance of %s";
        return format(fmt, args.argName(), className(args.object()));
      }
      String fmt = "%s must be instance of %s (was %s)";
      String cn0 = className(args.object());
      String cn1 = className(args.arg());
      return format(fmt, args.argName(), cn0, cn1, toStr(args.arg()));
    };
  }

  static Formatter msgArray() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be an array (was %s)";
        return format(fmt, args.argName(), className(args.arg()));
      }
      String fmt = "%s must be an array (was %s)";
      return format(fmt, args.argName(), className(args.arg()));
    };
  }

  static Formatter msgMultipleOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be multiple of %s (was %s)";
        return format(fmt, args.argName(), args.arg());
      }
      String fmt = "%s must be multiple of %s (was %s)";
      return format(fmt, args.argName(), args.arg());
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
    } else if (val instanceof CharSequence cs) {
      if(cs.toString().isEmpty()) {
        return "<EMPTY_STRING>";
      }
      else if (cs.toString().isBlank()) {
        return "<BLANK_STRING["+cs.length()+"]>";
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
