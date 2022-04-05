package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static nl.naturalis.common.ArrayMethods.DEFAULT_IMPLODE_SEPARATOR;
import static nl.naturalis.common.ArrayMethods.implodeAny;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes", "unchecked"})
final class MsgUtil {

  static final String MUST = " must ";
  static final String MUST_NOT = " must not ";
  static final String MUST_BE = " must be ";
  static final String MUST_NOT_BE = " must not be ";
  static final String WAS = " (was ";

  private MsgUtil() {
    throw new AssertionError();
  }

  // Common message patterns:
  static final String MSG_PREDICATE = "%s must%s %s";
  static final String MSG_PREDICATE_WAS = MSG_PREDICATE + " (was %s)";
  static final String MSG_RELATION = "%s must%s %s %s";
  static final String MSG_RELATION_WAS = MSG_RELATION + " (was %s)";

  // Max display width (characters) for stringified values.
  private static final int MAX_DISPLAY_WIDTH = 55;

  static String getPrefabMessage(Object test,
      boolean negated,
      String argName,
      Object argVal,
      Class<?> argType,
      Object obj) {
    PrefabMsgFormatter formatter = MESSAGE_PATTERNS.get(test);
    if (formatter == null) {
      String s = argName == null
          ? DEF_ARG_NAME
          : argName;
      return "Invalid value for " + s + ": " + toStr(argVal);
    }
    return formatter.apply(new MsgArgs(test, negated, argName, argVal, argType, obj));
  }

  static String getCustomMessage(String message,
      Object[] msgArgs,
      Object test,
      String argName,
      Object argVal,
      Class<?> argType,
      Object obj) {
    if (message == null) {
      throw new InvalidCheckException("message must not be null");
    }
    if (msgArgs == null) {
      return message;
    }
    Object[] all = new Object[msgArgs.length + 5];
    all[0] = test;
    all[1] = argVal;
    all[2] = argType;
    all[3] = argName;
    all[4] = obj;
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return CustomMsgFormatter.format(message, all);
  }

  //////////////////////////////////////////////////////////////////////////

  static String was(String actual) {
    return WAS + actual + ')';
  }

  static String was(Object actual) {
    return was(String.valueOf(actual));
  }

  static String was(MsgArgs args) {
    return was(toStr(args.arg()));
  }

  static String obj(String obj) {
    return ' ' + obj;
  }

  static String obj(Object obj) {
    return obj(String.valueOf(obj));
  }

  static String obj(MsgArgs args) {
    return obj(toStr(args.obj()));
  }

  // Default message for predicates
  static PrefabMsgFormatter formatPredicate(String predicate, boolean showArgument) {
    return showArgument
        ? args -> formatPredicateShowArg(args, predicate, false)
        : args -> formatPredicate(args, predicate, false);
  }

  // Default message for negatively formulated predicates like notNull()
  static PrefabMsgFormatter formatNegativePredicate(String predicate, boolean showArgument) {
    return showArgument
        ? args -> formatPredicateShowArg(args, predicate, true)
        : args -> formatPredicate(args, predicate, true);
  }

  // showArgIfAffirmative: show argument when executed in is() or has() method
  // showArgIfNegated: show argument when executed in isNot() or notHas() method
  static PrefabMsgFormatter formatPredicate(String predicate,
      boolean showArgIfAffirmative,
      boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatPredicateShowArg(args, predicate, false);
      }
      return args -> args.negated()
          ? formatPredicate(args, predicate, false)
          : formatPredicateShowArg(args, predicate, false);
    } else if (showArgIfNegated) {
      return args -> args.negated()
          ? formatPredicateShowArg(args, predicate, false)
          : formatPredicate(args, predicate, false);
    }
    return args -> formatPredicate(args, predicate, false);
  }

  static PrefabMsgFormatter formatNegativePredicate(String predicate,
      boolean showArgIfAffirmative,
      boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatPredicateShowArg(args, predicate, true);
      }
      return args -> args.negated()
          ? formatPredicate(args, predicate, true)
          : formatPredicateShowArg(args, predicate, true);
    } else if (showArgIfNegated) {
      return args -> args.negated()
          ? formatPredicateShowArg(args, predicate, true)
          : formatPredicate(args, predicate, true);
    }
    return args -> formatPredicate(args, predicate, true);
  }

  static PrefabMsgFormatter formatRelation(String relation, boolean showArgument) {
    return showArgument
        ? args -> formatRelationShowArg(args, relation, false)
        : args -> formatRelation(args, relation, false);
  }

  static PrefabMsgFormatter formatRelation(String relation,
      boolean showArgIfAffirmative,
      boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatRelationShowArg(args, relation, false);
      }
      return args -> args.negated()
          ? formatRelation(args, relation, false)
          : formatRelationShowArg(args, relation, false);
    } else if (showArgIfNegated) {
      return args -> args.negated()
          ? formatRelationShowArg(args, relation, false)
          : formatRelation(args, relation, false);
    }
    return args -> formatRelation(args, relation, false);
  }

  static PrefabMsgFormatter formatNegativeRelation(String relation,
      boolean showArgIfAffirmative,
      boolean showArgIfNegated) {
    if (showArgIfAffirmative) {
      if (showArgIfNegated) {
        return args -> formatRelationShowArg(args, relation, true);
      }
      return args -> args.negated()
          ? formatRelation(args, relation, true)
          : formatRelationShowArg(args, relation, true);
    } else if (showArgIfNegated) {
      return args -> args.negated()
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
        ? format(MSG_RELATION_WAS,
        args.name(),
        args.notNot(),
        relation,
        toStr(args.obj()),
        toStr(args.arg()))
        : format(MSG_RELATION_WAS,
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
    } else if (val instanceof String s) {
      return s.isBlank()
          ? '"' + s + '"'
          : ellipsis(s);
    } else if (val instanceof Integer i) {
      return i.toString();
    } else if (val instanceof Collection c) {
      return collectionToString(c);
    } else if (val instanceof Map m) {
      return mapToString(m);
    } else if (val.getClass().isArray()) {
      return arrayToString(val);
    } else if (val instanceof Class c) {
      return className(c);
    }
    return ellipsis(val.toString());
  }

  static String className(Object obj) {
    return obj.getClass() == Class.class
        ? className((Class) obj)
        : className(obj.getClass());
  }

  static String simpleClassName(Object obj) {
    return obj.getClass() == Class.class
        ? simpleClassName((Class) obj)
        : simpleClassName(obj.getClass());
  }

  static String className(Class c) {
    return c.isArray()
        ? arrayClassName(c)
        : c.getName();
  }

  static String simpleClassName(Class c) {
    return c.isArray()
        ? simpleArrayClassName(c)
        : c.getSimpleName();
  }

  static String sysId(Object arg) {
    return arg == null
        ? "null"
        : simpleClassName(arg) + '@' + identityHashCode(arg);
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
