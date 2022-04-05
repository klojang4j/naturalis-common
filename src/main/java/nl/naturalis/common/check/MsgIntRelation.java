package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntRelation {

  private MsgIntRelation() {
    throw new AssertionError();
  }

  static PrefabMsgFormatter msgEq() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "equal" + obj(x)
        : x.name() + MUST + "equal" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgNe() {
    return x -> x.negated()
        ? x.name() + MUST + "equal" + obj(x) + was(x)
        : x.name() + MUST_NOT + "equal" + obj(x);
  }

  static PrefabMsgFormatter msgGt() {
    return x -> message(x, ">");
  }

  static PrefabMsgFormatter msgGte() {
    return x -> message(x, ">=");
  }

  static PrefabMsgFormatter msgLt() {
    return x -> message(x, "<");
  }

  static PrefabMsgFormatter msgLte() {
    return x -> message(x, "<=");
  }

  static PrefabMsgFormatter msgMultipleOf() {
    return x -> message(x, "multiple of");
  }

  private static String message(MsgArgs x, String descr) {
    return x.negated()
        ? x.name() + MUST_NOT_BE + descr + obj(x) + was(x)
        : x.name() + MUST_BE + descr + obj(x) + was(x);
  }

}
