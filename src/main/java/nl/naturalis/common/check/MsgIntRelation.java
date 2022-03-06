package nl.naturalis.common.check;

import static java.lang.String.format;

class MsgIntRelation {

  private MsgIntRelation() {}

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

  static Formatter msgGt() {
    return args -> {
      if (args.negated()) {
        return msgLte().apply(args.flip());
      }
      String fmt = "%s must be > %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgGte() {
    return args -> {
      if (args.negated()) {
        return msgLt().apply(args.flip());
      }
      String fmt = "%s must be >= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgLt() {
    return args -> {
      if (args.negated()) {
        return msgGte().apply(args.flip());
      }
      String fmt = "%s must be < %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }

  static Formatter msgLte() {
    return args -> {
      if (args.negated()) {
        return msgGt().apply(args.flip());
      }
      String fmt = "%s must be <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), args.arg());
    };
  }
}
