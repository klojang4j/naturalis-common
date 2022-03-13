package nl.naturalis.common.check;

import static java.lang.String.format;
import static nl.naturalis.common.check.MsgUtil.MSG_PREDICATE;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntPredicate {

  private MsgIntPredicate() {}

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
