package nl.naturalis.common.check;

import static java.lang.String.format;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntPredicate {

  private MsgIntPredicate() {}

  static Formatter msgEven() {
    return args -> format(MUST_BUT_WAS, args.name(), args.not(), "be even", args.arg());
  }

  static Formatter msgOdd() {
    return args -> format(MUST_BUT_WAS, args.name(), args.not(), "be odd", args.arg());
  }

  static Formatter msgPositive() {
    return args -> format(MUST_BUT_WAS, args.name(), args.not(), "be positive", args.arg());
  }

  static Formatter msgNegative() {
    return args -> format(MUST_BUT_WAS, args.name(), args.not(), "be negative", args.arg());
  }

  static Formatter msgZero() {
    return args ->
        args.negated()
            ? format(PLAIN_MUST, args.name(), args.not(), "be 0")
            : format(MUST_BUT_WAS, args.name(), args.not(), "be 0", args.arg());
  }
}
