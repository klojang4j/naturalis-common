package nl.naturalis.common.check;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.collection.TypeSet;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes", "unchecked"})
class Messages {

  // Fall-back error message
  static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  // Max display width (characters) for stringified values.
  private static final int MAX_DISPLAY_WIDTH = 55;

  // Classes that stringify nicely out of the box
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

  //////////////////////////////////////////////////////////////////////////

  static String toStr(Object val) {
    if (val == null) {
      return "null";
    }
    Class type = val.getClass();
    if (DECENT_TO_STRING.contains(type)) {
      return val.toString();
    } else if (val instanceof CharSequence) {
      String cs = ((CharSequence) val).toString();
      if (cs.toString().isBlank()) {
        return '"' + cs + '"';
      }
      return ellipsis(val.toString());
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
        return ellipsis(val.toString());
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
      if (imploded.length() > MAX_DISPLAY_WIDTH) {
        imploded = ellipsis(imploded);
      } else {
        imploded += "...";
      }
    } else if (imploded.length() > MAX_DISPLAY_WIDTH) {
      imploded = ellipsis(imploded);
    }
    return imploded;
  }

  static String classNameAbbrev(Object obj) {
    String[] pkgs = obj.getClass().getPackageName().split("\\.");
    String pkg = ArrayMethods.implode(pkgs, s -> s.substring(0, 1), ".", 0, -1);
    return pkg + '.' + simpleClassName(obj);
  }

  static String className(Object obj) {
    return obj.getClass() == Class.class ? className((Class) obj) : className(obj.getClass());
  }

  static String className(Class c) {
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
      return c.getName() + sb;
    }
    return c.getName();
  }

  static String simpleClassName(Object obj) {
    return obj.getClass() == Class.class
        ? simpleClassName((Class) obj)
        : simpleClassName(obj.getClass());
  }

  static String simpleClassName(Class c) {
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

  private static String ellipsis(String str) {
    if (str.length() <= MAX_DISPLAY_WIDTH) {
      return str;
    }
    int to = Math.max(0, MAX_DISPLAY_WIDTH - 3);
    return str.substring(0, to) + "...";
  }
}
