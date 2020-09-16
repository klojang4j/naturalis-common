package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import nl.naturalis.common.Tuple;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.Tuple.tuple;
import static nl.naturalis.common.check.Checks.*;
import static nl.naturalis.common.StringMethods.*;

class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /* Returns messages associated with predefined Predicate and IntPredicate instances. */
  static String get(Object test, Object arg, String argName) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName));
    }
    return String.format(ERR_INVALID_VALUE, argName, str(arg));
  }

  /* Returns messages associated with predefined Relation and IntRelation instances. */
  static String get(Object test, Object arg, String argName, Object target) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName, target));
    }
    return String.format(ERR_INVALID_VALUE, argName, str(arg));
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

    tmp.add(tuple(isNull(), x -> format("%s must be null (was %s)", x[1], str(x[0]))));
    tmp.add(tuple(notNull(), x -> format("%s must not be null", x[1])));
    tmp.add(tuple(isEmpty(), x -> format("%s must be empty (was %s)", x[1], str(x[0]))));
    tmp.add(tuple(notEmpty(), x -> format("%s must not be empty", x[1])));
    tmp.add(tuple(noneNull(), x -> format("%s must not contain null values", x[1])));
    tmp.add(tuple(isArray(), x -> format("%s must be an array (was %s)", x[1], cname(x[0]))));
    tmp.add(tuple(isEven(), x -> format("%s must be even (was %d)", x[1], x[0])));
    tmp.add(tuple(isOdd(), x -> format("%s must be odd (was %d)", x[1], x[0])));
    tmp.add(tuple(positive(), x -> format("%s must be positive (was %d)", x[1], x[0])));
    tmp.add(tuple(notPositive(), x -> format("%s must be zero or negative (was %d)", x[1], x[0])));
    tmp.add(tuple(negative(), x -> format("%s must be negative (was %d)", x[1], x[0])));
    tmp.add(tuple(notNegative(), x -> format("%s must be zero or positive (was %d)", x[1], x[0])));
    tmp.add(tuple(contains(), x -> format("%s %s must contain %s", sname(x[0]), x[1], str(x[2]))));
    tmp.add(
        tuple(
            elementOf(), x -> format("%s (%s) must be element of %s", x[1], str(x[0]), str(x[2]))));
    tmp.add(tuple(objEquals(), x -> format("%s must be equal to %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(objNotEquals(), x -> format("%s must be not be equal to %s", x[1], x[2])));
    tmp.add(tuple(objGreaterThan(), x -> format("%s must be > %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(objAtLeast(), x -> format("%s must be >= %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(objLessThan(), x -> format("%s must be < %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(objAtMost(), x -> format("%s must be <= %s (was %s)", x[1], x[2], x[0])));
    tmp.add(
        tuple(
            instanceOf(),
            x ->
                format(
                    "%s must be instance of %s (was %s)",
                    x[1], ((Class<?>) x[2]).getName(), cname(x[0]))));
    tmp.add(tuple(equalTo(), x -> format("%s must not be equal to %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(notEqualTo(), x -> format("%s must not be equal to %d", x[1], x[2])));
    tmp.add(tuple(greaterThan(), x -> format("%s must be > %d (was %d)", x[1], x[2], x[0])));
    tmp.add(tuple(atLeast(), x -> format("%s must be >= %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(lessThan(), x -> format("%s must be < %s (was %s)", x[1], x[2], x[0])));
    tmp.add(tuple(atMost(), x -> format("%s must be <= %s (was %s)", x[1], x[2], x[0])));
    tmp.add(
        tuple(multipleOf(), x -> format("%s must be multiple of %d (was %d)", x[1], x[2], x[0])));

    IdentityHashMap<Object, Function<Object[], String>> map = new IdentityHashMap<>(tmp.size());

    tmp.forEach(t -> t.addTo(map));

    return map;
  }

  private static String cname(Object obj) {
    return obj.getClass().getName();
  }

  private static String sname(Object obj) {
    return obj.getClass().getSimpleName();
  }

  @SuppressWarnings("rawtypes")
  private static String str(Object msgArg) {
    if (msgArg == null) {
      return null;
    } else if (msgArg instanceof CharSequence) {
      String s = msgArg.toString();
      if (s.length() > 20) {
        return '"' + substr(s, 0, 20) + "[...]\"";
      }
      return '"' + s + '"';
    } else if (msgArg instanceof Collection) {
      Collection c = (Collection) msgArg;
      if (c.size() == 0) {
        return sname(msgArg) + " (was empty)";
      } else if (c.size() > 3) {
        String elems = (String) c.stream().map(e -> str(e)).collect(joining(", "));
        return sname(msgArg) + "[" + elems + " ...]";
      }
      return sname(msgArg) + " " + msgArg;
    }
    return sname(msgArg) + "@" + System.identityHashCode(msgArg);
  }
}
