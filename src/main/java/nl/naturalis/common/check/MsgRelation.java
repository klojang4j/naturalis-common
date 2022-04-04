package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.*;

@SuppressWarnings("rawtypes")
final class MsgRelation {

  private MsgRelation() {}

  static PrefabMsgFormatter msgSameAs() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "reference to " + sysId(x.obj())
        : x.name() + MUST_BE + "reference to " + sysId(x.obj()) + was(sysId((x.arg())));
  }

  static PrefabMsgFormatter msgNullOr() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "null or" + obj(x) + was(x)
        : x.name() + MUST_BE + "null or" + obj(x) + was(x);

  }

  static PrefabMsgFormatter msgInstanceOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "instance of" + obj(x) + was(x)
        : x.name() + MUST_BE + "instance of" + obj(x) + was(className(x.arg()));
  }

  static PrefabMsgFormatter msgSubtypeOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "extend/implement" + obj(x) + was(x)
        : x.name() + MUST + "extend/implement" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgSupertypeOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "supertype of" + obj(x) + was(x)
        : x.name() + MUST_BE + "supertype of" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgContains() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain" + obj(x)
        : x.name() + MUST + "contain" + obj(x);
  }

  static PrefabMsgFormatter msgHasKey() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain key" + obj(x)
        : x.name() + MUST + "contain key" + obj(x);
  }

  static PrefabMsgFormatter msgHasValue() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain value" + obj(x)
        : x.name() + MUST + "contain value" + obj(x);
  }

  static PrefabMsgFormatter msgIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "element of" + obj(x) + was(x)
        : x.name() + MUST_BE + "element of" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgKeyIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "key in" + obj(x) + was(x)
        : x.name() + MUST_BE + "key in" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgValueIn() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "value in" + obj(x) + was(x)
        : x.name() + MUST_BE + "value in" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgSupersetOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "superset of" + obj(x) + was(x)
        : x.name() + MUST_BE + "superset of" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgSubsetOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "subset of" + obj(x) + was(x)
        : x.name() + MUST_BE + "subset of" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgHasSubstring() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "contain" + obj(x) + was(x)
        : x.name() + MUST + "contain" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgSubstringOf() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "substring of" + obj(x) + was(x)
        : x.name() + MUST_BE + "substring of" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgEqualsIgnoreCase() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "equal (ignoring case) to" + obj(x) + was(x)
        : x.name() + MUST_BE + "equal (ignoring case) to" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgStartsWith() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "start with" + obj(x) + was(x)
        : x.name() + MUST + "start with" + obj(x) + was(x);
  }

  static PrefabMsgFormatter msgEndsWith() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "end with" + obj(x) + was(x)
        : x.name() + MUST + "end with" + obj(x) + was(x);
  }

}
