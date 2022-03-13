package nl.naturalis.common.check;

import static java.lang.String.format;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntRelation {

  private MsgIntRelation() {}

  static Formatter msgEq() {
    return args ->
        args.negated()
            ? format(PLAIN_MUST_OBJ, args.name(), args.not(), "equal", toStr(args.obj()))
            : format(
                MUST_OBJ_BUT_WAS,
                args.name(),
                args.not(),
                "equal",
                toStr(args.obj()),
                toStr(args.arg()));
  }

  static Formatter msgNe() {
    return args ->
        args.negated()
            ? format(MUST_OBJ_BUT_WAS, args.name(), args.notNot(), "equal", toStr(args.obj()))
            : format(
                PLAIN_MUST_OBJ,
                args.name(),
                args.notNot(),
                "equal",
                toStr(args.obj()),
                toStr(args.arg()));
  }

  static Formatter msgGt() {
    return args -> {
      if (args.negated()) {
        return msgLte().apply(args.flip());
      }
      String fmt = "%s must be > %s (was %s)";
      return format(fmt, args.name(), args.obj(), args.arg());
    };
  }

  static Formatter msgGte() {
    return args -> {
      if (args.negated()) {
        return msgLt().apply(args.flip());
      }
      String fmt = "%s must be >= %s (was %s)";
      return format(fmt, args.name(), args.obj(), args.arg());
    };
  }

  static Formatter msgLt() {
    return args -> {
      if (args.negated()) {
        return msgGte().apply(args.flip());
      }
      String fmt = "%s must be < %s (was %s)";
      return format(fmt, args.name(), args.obj(), args.arg());
    };
  }

  static Formatter msgLte() {
    return args -> {
      if (args.negated()) {
        return msgGt().apply(args.flip());
      }
      String fmt = "%s must be <= %s (was %s)";
      return format(fmt, args.name(), args.obj(), args.arg());
    };
  }

  static Formatter msgMultipleOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be multiple of %s (was %s)";
        return format(fmt, args.name(), args.obj(), args.arg());
      }
      String fmt = "%s must be multiple of %s (was %s)";
      return format(fmt, args.name(), args.obj(), args.arg());
    };
  }
}
