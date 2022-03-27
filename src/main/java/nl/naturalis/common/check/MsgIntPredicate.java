package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.formatPredicate;

final class MsgIntPredicate {

  private MsgIntPredicate() {
  }

  static Formatter msgEven() {
    return formatPredicate("be even", true);
  }

  static Formatter msgOdd() {
    return formatPredicate("be odd", true);
  }

  static Formatter msgPositive() {
    return formatPredicate("be positive", true);
  }

  static Formatter msgNegative() {
    return formatPredicate("be negative", true);
  }

  static Formatter msgZero() {
    return formatPredicate("be 0", true, false);
  }

}
