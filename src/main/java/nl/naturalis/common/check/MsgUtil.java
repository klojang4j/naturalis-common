package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ArrayMethods.implodeAny;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.ObjectMethods.throwIf;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;
import static nl.naturalis.common.check.CommonChecks.NAMES;

@SuppressWarnings({"rawtypes", "unchecked"})
final class MsgUtil {

  // Common message patterns:
  static final String MSG_PREDICATE = "%s must%s %s";
  static final String MSG_PREDICATE_WAS = MSG_PREDICATE + " (was %s)";
  static final String MSG_RELATION = "%s must%s %s %s";
  static final String MSG_RELATION_WAS = MSG_RELATION + " (was %s)";

  // Max display width (characters) for stringified values.
  private static final int MAX_DISPLAY_WIDTH = 55;

  static String getPrefabMessage(
      Object test, boolean negated, String argName, Object argVal, Class<?> argType, Object obj) {
    Formatter formatter = MESSAGE_PATTERNS.get(test);
    if (formatter == null) {
      if (obj == null) {
        return "Invalid value for " + argName + ": " + toStr(argVal);
      }
      return "Invalid value for "
          + argName
          + ": "
          + toStr(argVal)
          + " and "
          + toStr(obj)
          + " must "
          + (negated ? "not " : "")
          + "have the specified relationship";
    }
    Class<?> type = argType != null ? argType : argVal != null ? argVal.getClass() : null;
    return formatter.apply(new MsgArgs(test, negated, argName, argVal, type, obj));
  }

  static String getCustomMessage(
      String pattern,
      Object[] msgArgs,
      Object test,
      String argName,
      Object argVal,
      Class<?> argType,
      Object obj) {
    throwIf(pattern == null, () -> new InvalidCheckException("message pattern must not be null"));
    throwIf(msgArgs == null, () -> new InvalidCheckException("message arguments must not be null"));
    //String fmt = FormatNormalizer.normalize(pattern);
    Object[] all = new Object[msgArgs.length + 5];
    all[0] = NAMES.getOrDefault(test, test.getClass().getSimpleName());
    all[1] = toStr(argVal);
    all[2] =
        argType != null
            ? simpleClassName(argType)
            : argVal != null ? simpleClassName(argVal.getClass()) : null;
    all[3] = argName;
    all[4] = toStr(obj);
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return FormatNormalizer.format(pattern, all);
  }

  //////////////////////////////////////////////////////////////////////////

  // Default message for predicates
  static Formatter formatPredicate(String predicate, boolean showArgument) {
    return showArgument
        ? args -> formatPredicateShowArg(args, predicate, false)
        : args -> formatPredicate(args, predicate, false);
  }

  // Default message for negatively formulated predicates like notNull()
  static Formatter formatNegativePredicate(String predicate, boolean showArgument) {
    return showArgument
        ? args -> formatPredicateShowArg(args, predicate, true)
        : args -> formatPredicate(args, predicate, true);
  }

  // showArgIfAffirmative: show argument when executed in is() or has() method
  // showArgIfNegated: show argument when executed in isNot() or notHas() method
  static Formatter formatPredicate(
      String predicate, boolean showArgIfAffirmative, boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatPredicateShowArg(args, predicate, false);
      }
      return args ->
          args.negated()
              ? formatPredicate(args, predicate, false)
              : formatPredicateShowArg(args, predicate, false);
    } else if (showArgIfNegated) {
      return args ->
          args.negated()
              ? formatPredicateShowArg(args, predicate, false)
              : formatPredicate(args, predicate, false);
    }
    return args -> formatPredicate(args, predicate, false);
  }

  static Formatter formatNegativePredicate(
      String predicate, boolean showArgIfAffirmative, boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatPredicateShowArg(args, predicate, true);
      }
      return args ->
          args.negated()
              ? formatPredicate(args, predicate, true)
              : formatPredicateShowArg(args, predicate, true);
    } else if (showArgIfNegated) {
      return args ->
          args.negated()
              ? formatPredicateShowArg(args, predicate, true)
              : formatPredicate(args, predicate, true);
    }
    return args -> formatPredicate(args, predicate, true);
  }

  static Formatter formatRelation(String relation, boolean showArgument) {
    return showArgument
        ? args -> formatRelationShowArg(args, relation, false)
        : args -> formatRelation(args, relation, false);
  }

  static Formatter formatRelation(
      String relation, boolean showArgIfAffirmative, boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatRelationShowArg(args, relation, false);
      }
      return args ->
          args.negated()
              ? formatRelation(args, relation, false)
              : formatRelationShowArg(args, relation, false);
    } else if (showArgIfNegated) {
      return args ->
          args.negated()
              ? formatRelationShowArg(args, relation, false)
              : formatRelation(args, relation, false);
    }
    return args -> formatRelation(args, relation, false);
  }

  static Formatter formatNegativeRelation(
      String relation, boolean showArgIfAffirmative, boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatRelationShowArg(args, relation, true);
      }
      return args ->
          args.negated()
              ? formatRelation(args, relation, true)
              : formatRelationShowArg(args, relation, true);
    } else if (showArgIfNegated) {
      return args ->
          args.negated()
              ? formatRelationShowArg(args, relation, true)
              : formatRelation(args, relation, true);
    }
    return args -> formatRelation(args, relation, true);
  }

  private static String formatPredicate(MsgArgs args, String predicate, boolean negative) {
    return negative
        ? format(MSG_PREDICATE, args.name(), args.notNot(), predicate)
        : format(MSG_PREDICATE, args.name(), args.not(), predicate);
  }

  private static String formatPredicateShowArg(MsgArgs args, String predicate, boolean negative) {
    return negative
        ? format(MSG_PREDICATE_WAS, args.name(), args.notNot(), predicate, toStr(args.arg()))
        : format(MSG_PREDICATE_WAS, args.name(), args.not(), predicate, toStr(args.arg()));
  }

  private static String formatRelation(MsgArgs args, String relation, boolean negative) {
    return negative
        ? format(MSG_RELATION, args.name(), args.notNot(), relation, toStr(args.obj()))
        : format(MSG_RELATION, args.name(), args.not(), relation, toStr(args.obj()));
  }

  private static String formatRelationShowArg(MsgArgs args, String relation, boolean negative) {
    return negative
        ? format(
        MSG_RELATION_WAS,
        args.name(),
        args.notNot(),
        relation,
        toStr(args.obj()),
        toStr(args.arg()))
        : format(
        MSG_RELATION_WAS,
        args.name(),
        args.not(),
        relation,
        toStr(args.obj()),
        toStr(args.arg()));
  }

  //////////////////////////////////////////////////////////////////////////

  static String toStr(Object val) {
    if (val == null) {
      return "null";
    }
    Class type = val.getClass();
    if (val instanceof CharSequence) {
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
      return className(val);
    }
    return ellipsis(val.toString());
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
    String s = implode(c, MsgUtil::toStr, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
    return scn + " of [" + trim(s, c.size()) + ']';
  }

  private static String mapToString(Map m) {
    String scn = m.getClass().getSimpleName() + "[" + m.size() + "]";
    if (m.size() == 0) {
      return scn;
    }
    String s = implode(m.entrySet(), MsgUtil::entryToString, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
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
    String s = implodeAny(array, MsgUtil::toStr, DEFAULT_IMPLODE_SEPARATOR, 0, 10);
    return scn + " of [" + trim(s, len) + ']';
  }

  private static String entryToString(Map.Entry entry) {
    return toStr(entry.getKey()) + ": " + toStr(entry.getValue());
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
