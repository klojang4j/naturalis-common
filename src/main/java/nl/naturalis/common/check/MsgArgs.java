package nl.naturalis.common.check;

import static nl.naturalis.common.check.Messages.simpleClassName;

/**
 * Standard message argument for prefab messages:
 * <ol>
 *     <li><b>test</b> The check that was executed, e.g. notNull(), gte() or Objects::nonNull or a
 *        lambda
 *     <li><b>negated</b> Whether the check was executed in the isNot(..) or notHas(...)
 *        methods
 *     <li><b>name</b> The argument name
 *     <li><b>arg</b> The argument
 *     <li><b>type</b> The class of the argument. Will only be set by IntCheck, so in practice
 *        will always be either null or int.class. We need to be able to distinguish between
 *        int and Integer.
 *     <li><b>obj</b> The value of the object of a Relation, or null of the check was implemented
 *        as a Predicate or IntPredicate
 * </ol>
 */
record MsgArgs(Object test, boolean negated, String name, Object arg, Class<?> type, Object obj) {

  MsgArgs flip() {
    return new MsgArgs(test, !negated, name, arg, type, obj);
  }

  public Class<?> type() {
    return type != null? type:arg == null? null : arg.getClass();
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
