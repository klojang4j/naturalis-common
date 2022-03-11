package nl.naturalis.common.check;

import static nl.naturalis.common.check.Messages.simpleClassName;

/*
 * check .....: the check that was executed (e.g. notNull(), gte() or Objects::nonNull or a lambda)
 * negated ...: whether the check was executed in the isNot(..) or notHas(...) methods
 * argName ...: the argument name
 * arg .... ..: the argument and, if the check was implemented as a Relation, the subject of the relationship
 * object ....: the object of the relationship, or null if the check was implemented as a Predicate
 */
record MsgArgs(Object check, boolean negated, String argName, Object arg, Object object) {

  MsgArgs(Object check, boolean negated, String argName, Object argument) {
    this(check, negated, argName, argument, null);
  }

  MsgArgs flip() {
    return new MsgArgs(check, !negated, argName, arg, object);
  }

  String typeAndName() {
    return arg == null? argName: simpleClassName(arg) +  ' ' + argName;
  }

  String not() {
    return negated? " not" : "";
  }

  // For negatively formulated checks like notNull() or deepNotEmpty()
  String notNot() {
    return negated? "" : " not";
  }

}
