package nl.naturalis.common.check;

import nl.naturalis.common.IntPair;

import java.lang.reflect.Array;
import java.util.List;

import static nl.naturalis.common.check.MsgUtil.*;

import static java.lang.String.format;

final class MsgIntObjRelation {

  private MsgIntObjRelation() {
    throw new AssertionError();
  }

  static PrefabMsgFormatter msgIndexOf() {
    return x -> indexOf(x, Array.getLength(x.obj()));
  }

  static PrefabMsgFormatter msgListIndexOf() {
    return x -> indexOf(x, ((List) x.obj()).size());
  }

  static PrefabMsgFormatter msgStrIndexOf() {
    return x -> indexOf(x, ((String) x.obj()).length());
  }

  private static String indexOf(MsgArgs x, int max) {
    return x.negated()
        ? x.name() + MUST_BE + "< 0 or >= " + max + was(x)
        : x.name() + MUST_BE + ">= 0 and < " + max + was(x);
  }

  static PrefabMsgFormatter msgInRange() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated()
          ? x.name() + MUST_BE + "< " + min + " or >= " + max + was(x)
          : x.name() + MUST_BE + ">= " + min + " and < " + max + was(x);
    };
  }

  static PrefabMsgFormatter msgInRangeClosed() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated()
          ? x.name() + MUST_BE + "< " + min + " or > " + max + was(x)
          : x.name() + MUST_BE + ">= " + min + " and <= " + max + was(x);
    };
  }

}
