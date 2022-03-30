package nl.naturalis.common.check;

import nl.naturalis.common.IntPair;

import java.lang.reflect.Array;
import java.util.List;

import static java.lang.String.format;

final class MsgIntObjRelation {

  private MsgIntObjRelation() {}

  static PrefabMsgFormatter msgIndexOf() {
    return args -> {
      int max;
      if (args.obj().getClass().isArray()) {
        max = Array.getLength(args.obj());
      } else if (args.obj() instanceof List) {
        max = ((List) args.obj()).size();
      } else { // String
        max = ((String) args.obj()).length();
      }
      if (args.negated()) {
        String fmt = "%s must be < 0 or >= %s (was %s)";
        return format(fmt, args.name(), max, args.arg());
      }
      String fmt = "%s must be >= 0 and < %s (was %s)";
      return format(fmt, args.name(), max, args.arg());
    };
  }

  static PrefabMsgFormatter msgInRange() {
    return args ->
        args.negated()
            ? format(
            "%s must be < %s or >= %s (was %s)",
            args.name(), ((IntPair) args.obj()).one(), ((IntPair) args.obj()).two(), args.arg())
            : format(
            "%s must be >= %s and < %s (was %s)",
            args.name(),
            ((IntPair) args.obj()).one(),
            ((IntPair) args.obj()).two(),
            args.arg());
  }

  static PrefabMsgFormatter msgInRangeClosed() {
    return args ->
        args.negated()
            ? format(
            "%s must be < %s or > %s (was %s)",
            args.name(), ((IntPair) args.obj()).one(), ((IntPair) args.obj()).two(), args.arg())
            : format(
            "%s must be >= %s and <= %s (was %s)",
            args.name(),
            ((IntPair) args.obj()).one(),
            ((IntPair) args.obj()).two(),
            args.arg());
  }
}
