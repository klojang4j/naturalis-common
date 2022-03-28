package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

import java.util.function.Function;
import java.util.function.Supplier;

import static nl.naturalis.common.ArrayMethods.implode;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.ExceptionMethods.getRootCause;

public class InvokeException extends RuntimeException {

  static Supplier<InvokeException> noPublicStuff(Class<?> clazz, String stuff) {
    return () -> new InvokeException("class %s does ot have any public %s",
        className(clazz),
        stuff);
  }

  static Supplier<InvokeException> allPropertiesExcluded(Class<?> clazz) {
    return () -> new InvokeException("all properties excluded for %s", clazz);
  }

  public static InvokeException missingNoArgConstructor(Class<?> clazz) {
    return new InvokeException("missing no-arg constructor for class %s", className(clazz));
  }

  public static InvokeException noSuchConstructor(Class<?> clazz, Class<?>... params) {
    return new InvokeException(
        "no such constructor: %s(%s)",
        simpleClassName(clazz),
        implode(params, ClassMethods::simpleClassName, ", ", 0, -1));
  }

  static InvokeException wrap(Throwable t, Object bean, Getter getter) {
    return new InvokeException("Error while reading %s.%s: %s",
        simpleClassName(bean),
        getter.getProperty(),
        getRootCause(t));
  }

  InvokeException(String message, Object... msgArgs) {
    super(msgArgs.length == 0 ? message : String.format(message, msgArgs));
  }
}
