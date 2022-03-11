package nl.naturalis.common.check;

import static java.lang.String.format;

final class MsgIntPredicate {

  private MsgIntPredicate() {}

  static Formatter msgEven() {
    return args -> {
      if (args.negated()) {
        return msgOdd().apply(args.flip());
      }
      return format("%s must be even (was %s)", args.name(), args.arg());
    };
  }

  static Formatter msgOdd() {
    return args -> {
      if (args.negated()) {
        return msgEven().apply(args.flip());
      }
      return format("%s must be odd (was %s)", args.name(), args.arg());
    };
  }

  static Formatter msgPositive() {
    return args -> {
      String not = args.negated() ? " not" : "";
      return format("%s must%s be positive (was %s)", args.name(), not, args.arg());
    };
  }

  static Formatter msgNegative() {
    return args -> {
      String not = args.negated() ? " not" : "";
      return format("%s must%s be negative (was %s)", args.name(), not, args.arg());
    };
  }

  static Formatter msgZero() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be 0", args.name());
      }
      String fmt = "%s must be 0 (was %s)";
      return format(fmt, args.name(), args.arg());
    };
  }
}
