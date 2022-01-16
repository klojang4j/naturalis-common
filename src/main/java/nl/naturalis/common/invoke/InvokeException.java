package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

import java.util.function.Function;

import static nl.naturalis.common.ArrayMethods.implode;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.ExceptionMethods.getRootCause;

public class InvokeException extends RuntimeException {

  private static final String ERR_NOT_PUBLIC = "Class %s is not public";
  private static final String ERR_INCLUDES = "At least one of %s must be a property of %s";
  private static final String ERR_EXCLUDES = "No properties remain after excluding %s from %s";
  private static final String ERR_NOT_READABLE =
      "Cannot read beans of type %s (bean must be instance of %s)";

  public static InvokeException missingNoArgConstructor(Class<?> clazz) {
    return new InvokeException("Missing no-arg constructor on %s", simpleClassName(clazz));
  }

  public static Function<String, InvokeException> cannotInstantiate(Class<?> clazz) {
    return s -> new InvokeException("Cannot instantiate %s", simpleClassName(clazz));
  }

  public static <T> InvokeException typeMismatch(BeanReader<? super T> reader, T bean) {
    String name0 = ClassMethods.className(bean);
    String name1 = ClassMethods.className(reader.getBeanClass());
    return new InvokeException(ERR_NOT_READABLE, name0, name1);
  }

  public static <T> InvokeException typeMismatch(SaveBeanReader<? super T> reader, T bean) {
    String name0 = ClassMethods.className(bean);
    String name1 = ClassMethods.className(reader.getBeanClass());
    return new InvokeException(ERR_NOT_READABLE, name0, name1);
  }

  public static Function<String, InvokeException> classNotPublic(Class<?> clazz) {
    return s -> new InvokeException(ERR_NOT_PUBLIC, clazz.getName());
  }

  public static Function<String, InvokeException> noPropertiesSelected(
      Class<?> clazz, boolean exclude, String... properties) {
    if (exclude) {
      return s -> new InvokeException(ERR_EXCLUDES, implode(properties), clazz);
    }
    return s -> new InvokeException(ERR_INCLUDES, implode(properties), clazz);
  }

  public static InvokeException wrap(Throwable t) {
    if(t instanceof InvokeException ie) {
      return ie;
    }
    return new InvokeException(getRootCause(t).toString());
  }

  InvokeException(String message, Object... msgArgs) {
    super(msgArgs.length == 0 ? message : String.format(message, msgArgs));
  }
}
