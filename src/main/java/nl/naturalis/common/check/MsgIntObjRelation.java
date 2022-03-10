package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.List;

import static java.lang.String.format;

final class MsgIntObjRelation {

  private MsgIntObjRelation() {}

  static Formatter msgIndexOf() {
    return args -> {
      int max;
      if (args.object().getClass().isArray()) {
        max = Array.getLength(args.object());
      } else if (args.object() instanceof List) {
        max = ((List) args.object()).size();
      } else { // String
        max = ((String) args.object()).length();
      }
      if (args.negated()) {
        String fmt = "%s must be < 0 or >= %s (was %s)";
        return format(fmt, args.argName(), max, args.arg());
      }
      String fmt = "%s must be >= 0 and < %s (was %s)";
      return format(fmt, args.argName(), max, args.arg());
    };
  }
}
