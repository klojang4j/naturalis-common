package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;

@SuppressWarnings("rawtypes")
final class MsgRelation {

  private MsgRelation() {}

  static PrefabMsgFormatter msgSameAs() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "identical to " + sysId(x.obj())
        : x.name()
            + MUST_BE
            + "identical to "
            + sysId(x.obj())
            + was(sysId((x.arg())));
  }

  static PrefabMsgFormatter msgNullOr() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "null or" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "null or" + obj2(x) + was2(x);

  }

  static PrefabMsgFormatter msgInstanceOf() {
    return x -> x.negated()
        ? x.name()
        + MUST_NOT_BE
        + "instance of"
        + obj(className(x.obj()))
        + was(x.arg())
        : x.name() + MUST_BE + "instance of" + obj(className(x.obj())) + was(
            className(x.arg()));
  }

  static PrefabMsgFormatter msgSubtypeOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "extend/implement" + obj2(x) + was2(x)
        : x.name() + MUST + "extend/implement" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgSupertypeOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "supertype of" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "supertype of" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgContains() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain" + obj2(x)
        : x.name() + MUST + "contain" + obj2(x);
  }

  static PrefabMsgFormatter msgHasKey() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain key" + obj2(x)
        : x.name() + MUST + "contain key" + obj2(x);
  }

  static PrefabMsgFormatter msgHasValue() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain value" + obj2(x)
        : x.name() + MUST + "contain value" + obj2(x);
  }

  static PrefabMsgFormatter msgIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "element of" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "element of" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgKeyIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "key in" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "key in" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgValueIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "value in" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "value in" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgSupersetOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "superset of" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "superset of" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgSubsetOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "subset of" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "subset of" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgHasSubstring() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain" + obj2(x) + was2(x)
        : x.name() + MUST + "contain" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgSubstringOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "substring of" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "substring of" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgEqualsIgnoreCase() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "equal (ignoring case) to" + obj2(x) + was2(x)
        : x.name() + MUST_BE + "equal (ignoring case) to" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgStartsWith() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "start with" + obj2(x) + was2(x)
        : x.name() + MUST + "start with" + obj2(x) + was2(x);
  }

  static PrefabMsgFormatter msgEndsWith() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "end with" + obj2(x) + was2(x)
        : x.name() + MUST + "end with" + obj2(x) + was2(x);
  }

}
