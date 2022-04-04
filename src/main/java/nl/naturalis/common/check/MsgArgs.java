package nl.naturalis.common.check;

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
record MsgArgs(Object test, boolean negated, String name, Object arg, Class<?> type, Object obj) {

  @Override
  public Class<?> type() {
    if (type != null) {
      return type;
    }
    if (arg != null) {
      return arg.getClass();
    }
    return null;
  }

  @Override
  public String name() {
    if (name != null) {
      return name;
    }
    Class<?> clazz = type();
    if (clazz != null) {
      return simpleClassName(clazz);
    }
    return DEF_ARG_NAME;
  }

  String typeAndName() {
    Class<?> clazz = type();
    String name = name();
    if (clazz == null) {
      return name;
    } else if (name == DEF_ARG_NAME || this.name != null) {
      return simpleClassName(clazz) + ' ' + name;
    }
    return simpleClassName(clazz);
  }

  String not() {
    return negated ? " not" : "";
  }

  // For negatively formulated checks like notNull() or deepNotEmpty()
  String notNot() {
    return negated ? "" : " not";
  }

}
