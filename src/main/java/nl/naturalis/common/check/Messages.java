package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import nl.naturalis.common.Tuple;
import static java.lang.String.format;
import static nl.naturalis.common.check.Checks.*;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.Tuple.*;

class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /* Returns messages associated with predefined Predicate and IntPredicate instances. */
  static String get(Object test, Object arg, String argName) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName));
    }
    return String.format(ERR_INVALID_VALUE, argName, arg);
  }

  /* Returns messages associated with predefined Relation and IntRelation instances. */
  static String get(Object test, Object arg, String argName, Object target) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName, target));
    }
    return String.format(ERR_INVALID_VALUE, argName, arg);
  }

  /*
   * Maps predefined Predicate And Relation instances to message suppliers, which take an array of
   * message arguments and return a complete error message. The 1st message argument must be the
   * argument itself, the 2nd must be the name of the argument and, for Relation instances only, the
   * 3rd message argument must be the object at the other end of the relationship.
   */
  private static final IdentityHashMap<Object, Function<Object[], String>> msgs = createLookups();

  private static IdentityHashMap<Object, Function<Object[], String>> createLookups() {

    List<Tuple<Object, Function<Object[], String>>> tmp = new ArrayList<>();

    tmp.add(tuple(isNull(), (x) -> format("%s must be null (was %s)", x[1], x[0])));
    tmp.add(tuple(notNull(), (x) -> format("%s must not be null", x[1])));
    tmp.add(tuple(isEmpty(), (x) -> format("%s must be empty (was %s)", x[1], x[0])));
    tmp.add(tuple(notEmpty(), (x) -> format("%s must not be empty", x[1])));
    tmp.add(tuple(noneNull(), (x) -> format("%s must not contain null values", x[1])));
    tmp.add(
        tuple(
            deepNotNull(),
            (x) -> format("%s must not be null, empty, or contain null values", x[1])));
    tmp.add(
        tuple(
            deepNotEmpty(),
            (x) -> format("%s must not be null, empty, or contain empty values", x[1])));
    tmp.add(
        tuple(
            isArray(),
            (x) -> format("%s must be an array (was %s)", x[1], x[0].getClass().getName())));
    tmp.add(tuple(isEven(), (x) -> format("%s must be even (was %d)", x[1], x[0])));
    tmp.add(tuple(isOdd(), (x) -> format("%s must be odd (was %d)", x[1], x[0])));
    tmp.add(tuple(contains(), (x) -> format("Missing element in %s: %s", x[1], x[2])));
    tmp.add(
        tuple(
            elementOf(),
            (x) -> format("%s not found in %s", x[1], x[2].getClass().getSimpleName())));
    tmp.add(tuple(objEquals(), (x) -> format("%s must be equal to %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(objNotEquals(), (x) -> format("%s must be not be equal to %s", x[1], x[2])));
    tmp.add(
        tuple(
            instanceOf(),
            (x) -> {
              if (x[2].getClass().isInterface()) {
                return format("%s must implement %s", x[1], x[2].getClass().getName());
              }
              return format("%s must be instance of %s", x[1], x[2].getClass().getName());
            }));
    tmp.add(
        tuple(equalTo(), (x) -> format("%s must not be equal to %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(notEqualTo(), (x) -> format("%s must not be equal to %d", x[1], x[2])));
    tmp.add(tuple(greaterThan(), (x) -> format("%s must be > %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(atLeast(), (x) -> format("%s must be >= %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(lessThan(), (x) -> format("%s must be < %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(atMost(), (x) -> format("%s must be <= %d (was %d)", x[1], x[2], x[0])));

    IdentityHashMap<Object, Function<Object[], String>> map = new IdentityHashMap<>(tmp.size());

    tmp.forEach(t -> t.addTo(map));

    return map;
  }
}
