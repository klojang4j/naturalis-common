package nl.naturalis.common.check;

import nl.naturalis.common.IntPair;

import java.lang.reflect.Array;
import java.util.List;

import static nl.naturalis.common.check.MsgUtil.*;

import static java.lang.String.format;

final class MsgIntObjRelation {

  private MsgIntObjRelation() {}

  static PrefabMsgFormatter msgIndexOf() {
    return x -> {
      int max;
      if (x.obj().getClass().isArray()) {
        max = Array.getLength(x.obj());
      } else if (x.obj() instanceof List) {
        max = ((List) x.obj()).size();
      } else { // String
        max = ((String) x.obj()).length();
      }
      return x.negated()
          ? x.name() + MUST_BE + "< 0 or >= " + max + was(x.arg())
          : x.name() + MUST_BE + ">= 0 and < " + max + was(x.arg());
    };
  }

  static PrefabMsgFormatter msgInRange() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated()
          ? x.name() + MUST_BE + "< " + min + " or >= " + max + was(x.arg())
          : x.name() + MUST_BE + ">= " + min + " and < " + max + was(x.arg());
    };
  }

  static PrefabMsgFormatter msgInRangeClosed() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated()
          ? x.name() + MUST_BE + "< " + min + " or > " + max + was(x.arg())
          : x.name() + MUST_BE + ">= " + min + " and <= " + max + was(x.arg());
    };
  }

}
