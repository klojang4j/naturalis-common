package nl.naturalis.common.check;

import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

@SuppressWarnings("rawtypes")
class MessageData {

  private final Object check;
  private final boolean negated;
  private final String argName;
  private final Object argument;
  private final Object object;

  MessageData(Predicate check, boolean negated, String argName, Object argument) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = argument;
    this.object = null;
  }

  MessageData(IntPredicate check, boolean negated, String argName, int argument) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = Integer.valueOf(argument);
    this.object = null;
  }

  MessageData(Relation check, boolean negated, String argName, Object argument, Object object) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = argument;
    this.object = object;
  }

  MessageData(IntRelation check, boolean negated, String argName, int argument, int object) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = Integer.valueOf(argument);
    this.object = Integer.valueOf(object);
  }

  MessageData(ObjIntRelation check, boolean negated, String argName, Object argument, int object) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = argument;
    this.object = Integer.valueOf(object);
  }

  MessageData(IntObjRelation check, boolean negated, String argName, int argument, Object object) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = Integer.valueOf(argument);
    this.object = object;
  }

  private MessageData(
      Object check, boolean negated, String argName, Object argument, Object object) {
    this.check = check;
    this.negated = negated;
    this.argName = argName;
    this.argument = argument;
    this.object = object;
  }

  MessageData flip() {
    return new MessageData(check, !negated, argName, argument, object);
  }

  Object check() {
    return check;
  }

  boolean negated() {
    return negated;
  }

  String argName() {
    return argName;
  }

  Object argument() {
    return argument;
  }

  Object object() {
    return object;
  }
}
