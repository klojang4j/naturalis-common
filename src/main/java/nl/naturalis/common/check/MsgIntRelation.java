package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntRelation {

  private MsgIntRelation() {}

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
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + ">" + obj(x) + was(x)
        : x.name() + MUST_BE + ">" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgGte() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + ">=" + obj(x) + was(x)
        : x.name() + MUST_BE + ">=" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgLt() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "<" + obj(x) + was(x)
        : x.name() + MUST_BE + "<" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgLte() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "<=" + obj(x) + was(x)
        : x.name() + MUST_BE + "<=" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgMultipleOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "multiple of" + obj(x) + was(x)
        : x.name() + MUST_BE + "multiple of" + obj(x) + was(x);
  }

}
