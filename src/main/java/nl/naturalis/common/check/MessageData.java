package nl.naturalis.common.check;

record MessageData(Object check, boolean negated, String argName, Object argument, Object object) {

  MessageData(Object check, boolean negated, String argName, Object argument) {
    this(check, negated, argName, argument, null);
  }

  MessageData flip() {
    return new MessageData(check, !negated, argName, argument, object);
  }

}
