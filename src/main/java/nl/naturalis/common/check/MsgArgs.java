package nl.naturalis.common.check;

import static nl.naturalis.common.check.Messages.simpleClassName;

/*
 * Message arguments for prefab messages:
 * o test The check that was executed, e.g. notNull(), gte() or Objects::nonNull or a
 *   lambda
 * o negated Whether the check was executed in the isNot(..) or notHas(...)
 *   methods
 * o name The argument name
 * o arg The argument
 * o type The class of the argument. Will only be set if the value being tested is an
 *   int, so in practice will always be either null or int.class. We need to be able to
 *   distinguish between int and Integer.
 * o obj If the check was a Relation or one of its sister interfaces, the value of
 *   the object of the relationship, otherwise null (for Predicate or IntPredicate)
 */
record MsgArgs(Object test, boolean negated, String name, Object arg, Class<?> type, Object obj) {

  MsgArgs flip() {
    return new MsgArgs(test, !negated, name, arg, type, obj);
  }

  public Class<?> type() {
    return type == null? arg.getClass() : type;
  }

  String typeAndName() {
    return arg == null? name : simpleClassName(type()) +  ' ' + name;
  }

  String not() {
    return negated? " not" : "";
  }

  // For negatively formulated checks like notNull() or deepNotEmpty()
  String notNot() {
    return negated? "" : " not";
  }

}
