package nl.naturalis.common.check;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.collection.TypeSet;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ArrayMethods.implodeAny;
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
    return getMessage(predicate, negated, argName, argValue, null, null);
  }

  static String getMessage(
      Object predicate, boolean negated, String argName, Object argValue, Class<?> argType) {
    return getMessage(predicate, negated, argName, argValue, argType, null);
  }

  static String getMessage(
      Object relation, boolean negated, String argName, Object argValue, Object object) {
    return getMessage(relation, negated, argName, argValue, null, object);
  }

  static String getMessage(
      Object relation,
      boolean negated,
      String argName,
      Object argValue,
      Class<?> argType,
      Object object) {
    return message(new MsgArgs(relation, negated, argName, argValue, argType, object));
  }

  private static String message(MsgArgs args) {
    Formatter formatter = MESSAGE_PATTERNS.get(args.test());
    if (formatter != null) {
      return formatter.apply(args);
    }
    return String.format(ERR_INVALID_VALUE, args.name(), toStr(args.arg()));
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
      String s = ((CharSequence) val).toString();
      if (s.isBlank()) {
        return '"' + s + '"';
      }
      return ellipsis(val.toString());
    } else if (val instanceof Collection) {
      return collectionToString((Collection) val);
    } else if (val instanceof Map) {
      return mapToString((Map) val);
    } else if (type.isArray()) {
      return arrayToString(val);
    } else if (type == Class.class) {
      return type.getSimpleName();
    }
    return classNameAbbrev(type) + '@' + System.identityHashCode(val);
  }

  static String className(Object obj) {
    return obj.getClass() == Class.class ? className((Class) obj) : className(obj.getClass());
  }

  static String simpleClassName(Object obj) {
    return obj.getClass() == Class.class
        ? simpleClassName((Class) obj)
        : simpleClassName(obj.getClass());
  }

  static String className(Class c) {
    return c.isArray() ? arrayClassName(c) : c.getName();
  }

  static String simpleClassName(Class c) {
    return c.isArray() ? simpleArrayClassName(c) : c.getSimpleName();
  }

  private static String collectionToString(Collection c) {
    String scn = c.getClass().getSimpleName() + "[" + c.size() + "]";
    if (c.size() == 0) {
      return scn;
    }
    String s = implode(c, Messages::toStr, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
    return scn + " of [" + trim(s, c.size()) + ']';
  }

  private static String mapToString(Map m) {
    String scn = m.getClass().getSimpleName() + "[" + m.size() + "]";
    if (m.size() == 0) {
      return scn;
    }
    String s = implode(m.entrySet(), Messages::entryToString, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
    return scn + " of {" + trim(s, m.size()) + '}';
  }

  private static String arrayToString(Object array) {
    int len = Array.getLength(array);
    Class c = array.getClass().getComponentType();
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; ; ++i) {
      sb.append('[');
      if (i == 0) {
        sb.append(len);
      }
      sb.append(']');
      if (!c.isArray()) {
        break;
      }
      c = c.getComponentType();
    }
    String scn = c.getSimpleName() + sb;
    if (len == 0) {
      return scn;
    }
    String s = implodeAny(array, Messages::toStr, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
    return scn + " of [" + trim(s, len) + ']';
  }

  private static String entryToString(Map.Entry entry) {
    return toStr(entry.getKey()) + ": " + toStr(entry.getValue());
  }

  private static String classNameAbbrev(Object obj) {
    String[] pkgs = obj.getClass().getPackageName().split("\\.");
    String pkg = ArrayMethods.implode(pkgs, s -> s.substring(0, 1), ".", 0, -1);
    return pkg + '.' + simpleClassName(obj);
  }

  private static String arrayClassName(Class c) {
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

  private static String simpleArrayClassName(Class c) {
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

  private static String ellipsis(String str) {
    if (str.length() <= MAX_DISPLAY_WIDTH) {
      return str;
    }
    int to = Math.max(0, MAX_DISPLAY_WIDTH - 3);
    return str.substring(0, to) + "...";
  }
}
