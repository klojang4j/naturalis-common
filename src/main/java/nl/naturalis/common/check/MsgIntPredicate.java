package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntPredicate {

  private MsgIntPredicate() {
    throw new AssertionError();
  }

  static PrefabMsgFormatter msgEven() {
    return x -> message(x, "even");
  }

  static PrefabMsgFormatter msgOdd() {
    return x -> message(x, "odd");
  }

  static PrefabMsgFormatter msgPositive() {
    return x -> message(x, "positive");
  }

  static PrefabMsgFormatter msgNegative() {
    return x -> message(x, "negative");
  }

  static PrefabMsgFormatter msgZero() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "0"
        : x.name() + MUST_BE + "0" + was2(x);
  }

  private static String message(MsgArgs x, String descr) {
    return x.negated()
        ? x.name() + MUST_NOT_BE + descr + was2(x)
        : x.name() + MUST_BE + descr + was2(x);
  }

}
