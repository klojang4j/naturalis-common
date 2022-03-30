package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.formatPredicate;

final class MsgIntPredicate {

  private MsgIntPredicate() {
  }

  static PrefabMsgFormatter msgEven() {
    return formatPredicate("be even", true);
  }

  static PrefabMsgFormatter msgOdd() {
    return formatPredicate("be odd", true);
  }

  static PrefabMsgFormatter msgPositive() {
    return formatPredicate("be positive", true);
  }

  static PrefabMsgFormatter msgNegative() {
    return formatPredicate("be negative", true);
  }

  static PrefabMsgFormatter msgZero() {
    return formatPredicate("be 0", true, false);
  }

}
