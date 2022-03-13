package nl.naturalis.common.check;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.MsgUtil.*;

@SuppressWarnings("rawtypes")
final class MsgRelation {

  private MsgRelation() {}

  static Formatter msgSameAs() {
    return args -> {
      String idObj =
          args.obj() == null
              ? "null"
              : simpleClassName(args.obj()) + '@' + identityHashCode(args.obj());
      if (args.negated()) {
        String fmt = "%s must not be reference to %s";
        return format(fmt, args.name(), idObj);
      }
      String idArg =
          args.arg() == null
              ? "null"
              : simpleClassName(args.arg()) + '@' + identityHashCode(args.arg());
      String fmt = "%s must be reference to %s (was %s)";
      return format(fmt, args.name(), idObj, idArg);
    };
  }

  static Formatter msgNullOr() {
    return formatRelation("be null or", true);
  }

  static Formatter msgInstanceOf() {
    return args -> {
      String cnObj = className(args.obj());
      if (args.negated()) {
        String fmt = "%s must not be instance of %s (was %s)";
        String arg = toStr(args.arg());
        return format(fmt, args.name(), cnObj, arg);
      }
      String fmt = "%s must be instance of %s (was %s)";
      String cnArg = ifNotNull(args.arg(), MsgUtil::className);
      return format(fmt, args.name(), cnObj, cnArg);
    };
  }

  static Formatter msgSubtypeOf() {
    return formatRelation("extend/implement", true);
  }

  static Formatter msgSupertypeOf() {
    return formatRelation("be supertype of", true);
  }

  static Formatter msgContains() {
    return formatRelation("contain", false);
  }

  static Formatter msgHasKey() {
    return formatRelation("contain key", false);
  }

  static Formatter msgHasValue() {
    return formatRelation("contain value", false);
  }

  static Formatter msgIn() {
    return formatRelation("be element of", true);
  }

  static Formatter msgKeyIn() {
    return formatRelation("be key in", true);
  }

  static Formatter msgValueIn() {
    return formatRelation("be value in", true);
  }

  static Formatter msgSupersetOf() {
    return formatRelation("be superset of", true);
  }

  static Formatter msgSubsetOf() {
    return formatRelation("be subset of", true);
  }

  static Formatter msgHasSubstring() {
    return formatRelation("contain", true);
  }

  static Formatter msgSubstringOf() {
    return formatRelation("be substring of", true);
  }

  static Formatter msgEqualsIgnoreCase() {
    return formatRelation("be equal ignoring case to", true);
  }

  static Formatter msgStartsWith() {
    return formatRelation("start with", true);
  }

  static Formatter msgEndsWith() {
    return formatRelation("end with", true);
  }
}
