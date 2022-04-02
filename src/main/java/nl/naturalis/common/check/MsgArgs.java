package nl.naturalis.common.check;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.MsgUtil.simpleClassName;

/*
 * Message arguments for prefab messages:
 * o test The check that was executed, e.g. notNull(), gte(), Objects::nonNull or a
 *   lambda
 * o negated Whether the check was executed in an isNot(..) or notHas(...)
 *   method
 * o name The argument name
 * o arg The argument
 * o type The class of the argument. Will only be set if the value being tested is an
 *   int, so in practice will always be either null or int.class. We need to be able to
 *   distinguish between int and Integer.
 * o obj If the check was a Relation or one of its sister interfaces, the value of
 *   the object of the relationship, otherwise null (for Predicate or IntPredicate)
 */
final class MsgArgs {

  private final Object test;
  private final boolean negated;
  private final Object arg;
  private final Class<?> type;
  private final String name;
  private final Object obj;

  MsgArgs(Object test, boolean negated, String name, Object arg, Class<?> type, Object obj) {
    this.test = test;
    this.negated = negated;
    this.arg = arg;
    this.type = type;
    this.name = name;
    this.obj = obj;
  }

  Object test() {
    return test;
  }

  boolean negated() {
    return negated;
  }

  Object arg() {
    return arg;
  }

  Class<?> type() {
    return type == null ? arg == null ? null : arg.getClass() : type;
  }

  String name() {
    Class<?> c;
    return name == null ? (c = type()) == null ? DEF_ARG_NAME : simpleClassName(c) : name;
  }

  Object obj() {
    return obj;
  }

  String typeAndName() {
    Class<?> c = type();
    String n = name();
    if (c == null) {
      return n;
    } else if (n == DEF_ARG_NAME || name != null) {
      return simpleClassName(c) + ' ' + n;
    }
    return simpleClassName(c);
  }

  String not() {
    return negated ? " not" : "";
  }

  // For negatively formulated checks like notNull() or deepNotEmpty()
  String notNot() {
    return negated ? "" : " not";
  }
}
