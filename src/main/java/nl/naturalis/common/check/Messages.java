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
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.StringMethods.ellipsis;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes", "unchecked"})
class Messages {

  static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /*
   * Max. display width (characters) for argument values and for values used as the object in
   * Relation-type checks.
   */
  private static final int MAX_ARG_WIDTH = 50;

  private static final Set<Class<?>> DECENT_TO_STRING =
      TypeSet.of(Number.class, Boolean.class, Character.class, Enum.class);

  static String getMessage(Object predicate, boolean negated, String argName, Object argValue) {
    MsgArgs args = new MsgArgs(predicate, negated, argName, argValue);
    return message(args);
  }

  static String getMessage(
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
        String fmt = "%s must not be equal ignoring case to %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be equal ignoring case to %s (was %s)";
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

  static Formatter msgInRangeFrom() {
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

  static Formatter msgFromIndexOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must < 0 or > %s (was %s)";
        return format(fmt, args.argName(), args.object(), args.arg());
      }
      String fmt = "%s must be >= 0 and <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
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

  static Formatter msgStartsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not start with \"%s\" (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must start with \"%s\" (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
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

  static Formatter msgContains() {
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

  //////////////////////////////////////////////////////////////////////////

  static Formatter msgMultipleOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be multiple of %s (was %s)";
        return format(fmt, args.argName(), args.object(), args.arg());
      }
      String fmt = "%s must be multiple of %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static String toStr(Object val) {
    if (val == null) {
      return "null";
    }
    Class type = val.getClass();
    if (DECENT_TO_STRING.contains(type)) {
      return val.toString();
    } else if (val instanceof CharSequence) {
      CharSequence cs = (CharSequence) val;
      if (cs.toString().isEmpty()) {
        return "<EMPTY_STRING>";
      } else if (cs.toString().isBlank()) {
        return "<BLANK_STRING[" + cs.length() + "]>";
      }
      return ellipsis(val.toString(), MAX_ARG_WIDTH);
    } else if (type == Class.class) {
      return type.getSimpleName();
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
    String scn = c.getClass().getSimpleName() + "[" + c.size() + "]";
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
    String scn = m.getClass().getSimpleName() + "[" + m.size() + "]";
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

  private static String arrayTypeToString(Object array) {
    int len = Array.getLength(array);
    String scn = simpleClassName(array);
    return scn.replaceFirst("\\[]", "[" + len + "]");
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

  static String simpleClassName(Object obj) {
    Class<?> c = obj.getClass();
    if (c.isArray()) {
      c = c.getComponentType();
      StringBuilder sb = new StringBuilder(6);
      do {
        sb.append("[]");
        if (!c.isArray()) {
          break;
        }
        c = c.getComponentType();
      } while (true);
      return c.getSimpleName() + sb;
    }
    return c.getSimpleName();
  }
}
