package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;
import static nl.naturalis.common.check.MsgUtil.was2;

final class MsgObjIntRelation {

  private MsgObjIntRelation() {
    throw new AssertionError();
  }

  static PrefabMsgFormatter msgEQ() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "equal" + obj2(x)
        : x.name() + MUST + "equal" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgGT() {
    return x -> message(x, ">");
  }

  static PrefabMsgFormatter msgGTE() {
    return x -> message(x, ">=");
  }

  static PrefabMsgFormatter msgLT() {
    return x -> message(x, "<");
  }

  static PrefabMsgFormatter msgLTE() {
    return x -> message(x, "<=");
  }

  private static String message(MsgArgs x, String descr) {
    return x.negated()
        ? x.name() + MUST_NOT_BE + descr + obj2(x) + was2(x)
        : x.name() + MUST_BE + descr + obj2(x) + was2(x);
  }

}
