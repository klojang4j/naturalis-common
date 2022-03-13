package nl.naturalis.common.check;

import static java.lang.String.format;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntPredicate {

  private MsgIntPredicate() {}

  static Formatter msgEven() {
    return args -> format(MUST_BE_BUT_WAS, args.name(), args.not(), "even", args.arg());
  }

  static Formatter msgOdd() {
    return args -> format(MUST_BE_BUT_WAS, args.name(), args.not(), "odd", args.arg());
  }

  static Formatter msgPositive() {
    return args -> format(MUST_BE_BUT_WAS, args.name(), args.not(), "positive", args.arg());
  }

  static Formatter msgNegative() {
    return args -> format(MUST_BE_BUT_WAS, args.name(), args.not(), "negative", args.arg());
  }

  static Formatter msgZero() {
    return args ->
        args.negated()
            ? format(PLAIN_MUST_BE, args.name(), args.not(), "0")
            : format(MUST_BE_BUT_WAS, args.name(), args.not(), "0", args.arg());
  }
}
