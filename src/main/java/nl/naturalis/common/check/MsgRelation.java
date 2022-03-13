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
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be null or",
            toStr(args.obj()),
            toStr(args.arg()));
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
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "extend/implement",
            className(args.obj()),
            className(args.arg()));
  }

  static Formatter msgSupertypeOf() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be supertype of",
            className(args.obj()),
            className(args.arg()));
  }

  static Formatter msgContains() {
    return args -> format(MUST_OBJ, args.name(), args.not(), "contain", toStr(args.obj()));
  }

  static Formatter msgHasKey() {
    return args -> format(MUST_OBJ, args.name(), args.not(), "contain key", toStr(args.obj()));
  }

  static Formatter msgHasValue() {
    return args -> format(MUST_OBJ, args.name(), args.not(), "contain value", toStr(args.obj()));
  }

  static Formatter msgIn() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be element of",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgKeyIn() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be key in",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgValueIn() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be value in",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgSupersetOf() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be superset of",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgSubsetOf() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be subset of",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgHasSubstring() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "contain",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgSubstringOf() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be substring of",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgEqualsIgnoreCase() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "be equal ignoring case to",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgStartsWith() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "start with",
            toStr(args.obj()),
            toStr(args.arg()));
  }

  static Formatter msgEndsWith() {
    return args ->
        format(
            MUST_OBJ_BUT_WAS,
            args.name(),
            args.not(),
            "end with",
            toStr(args.obj()),
            toStr(args.arg()));
  }
}
