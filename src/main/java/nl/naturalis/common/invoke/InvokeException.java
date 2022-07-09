package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

import static nl.naturalis.common.ArrayMethods.implode;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.ExceptionMethods.getRootCause;

/**
 * A {@code RuntimeException} thrown in response to various dynamic invocation
 * errors.
 */
public sealed class InvokeException extends RuntimeException permits
    IllegalAssignmentException,
    NoPublicGettersException, NoPublicSettersException, NoSuchPropertyException {

  public static InvokeException missingNoArgConstructor(Class<?> clazz) {
    return new InvokeException("missing no-arg constructor for " + clazz);
  }

  public static InvokeException noSuchConstructor(Class<?> clazz,
      Class<?>... params) {
    String msg = String.format("no such constructor: %s(%s)",
        simpleClassName(clazz),
        implode(params, ClassMethods::simpleClassName, ", ", 0, -1));
    return new InvokeException(msg);
  }

  public static InvokeException arrayInspectionFailed(Object array,
      Throwable throwable) {
    String msg = String.format("array inspection failed for %s: %s",
        simpleClassName(array),
        throwable);
    return new InvokeException(msg);
  }

  static InvokeException wrap(Throwable t, Object bean, Getter getter) {
    return new InvokeException("Error while reading %s.%s: %s",
        simpleClassName(bean),
        getter.getProperty(),
        getRootCause(t));
  }

  InvokeException(String message, Object... msgArgs) {
    super(msgArgs.length == 0
        ? message
        : String.format(message, msgArgs));
  }

}
