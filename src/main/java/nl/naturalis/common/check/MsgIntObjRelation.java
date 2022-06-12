package nl.naturalis.common.check;

import nl.naturalis.common.IntPair;
import nl.naturalis.common.collection.IntList;

import java.lang.reflect.Array;
import java.util.List;

import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.MsgUtil.*;

import static java.lang.String.format;

final class MsgIntObjRelation {

  private MsgIntObjRelation() {
    throw new UnsupportedOperationException();
  }

  static PrefabMsgFormatter msgIndexOf() {
    return x -> x.obj().getClass().isArray()
        ? indexOf(x, Array.getLength(x.obj()))
        : x.obj() instanceof List l
            ? indexOf(x, l.size())
            : x.obj() instanceof String s
                ? indexOf(x, s.length())
                : x.obj() instanceof IntList il
                    ? indexOf(x, il.size())
                    : fail(AssertionError::new);
  }

  static PrefabMsgFormatter msgIndexInclusiveOf() {
    return x -> x.obj().getClass().isArray()
        ? indexInclusiveOf(x, Array.getLength(x.obj()))
        : x.obj() instanceof List l
            ? indexInclusiveOf(x, l.size())
            : x.obj() instanceof String s
                ? indexInclusiveOf(x, s.length())
                : x.obj() instanceof IntList il
                    ? indexInclusiveOf(x, il.size())
                    : fail(AssertionError::new);
  }

  private static String indexOf(MsgArgs x, int max) {
    return x.negated()
        ? x.name() + MUST_BE + "< 0 or >= " + max + was2(x)
        : x.name() + MUST_BE + ">= 0 and < " + max + was2(x);
  }

  private static String indexInclusiveOf(MsgArgs x, int max) {
    return x.negated()
        ? x.name() + MUST_BE + "< 0 or > " + max + was2(x)
        : x.name() + MUST_BE + ">= 0 and <= " + max + was2(x);
  }

  static PrefabMsgFormatter msgInRange() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated() ? x.name()
          + MUST_BE
          + "< "
          + min
          + " or >= "
          + max
          + was2(x) : x.name() + MUST_BE + ">= " + min + " and < " + max + was2(x);
    };
  }

  static PrefabMsgFormatter msgInRangeClosed() {
    return x -> {
      int min = ((IntPair) x.obj()).one();
      int max = ((IntPair) x.obj()).two();
      return x.negated()
          ? x.name() + MUST_BE + "< " + min + " or > " + max + was2(x)
          : x.name() + MUST_BE + ">= " + min + " and <= " + max + was2(x);
    };
  }

}
