package nl.naturalis.common.invoke;

import nl.naturalis.common.ExceptionMethods;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Represents a setter for a single property.
 *
 * @author Ayco Holleman
 */
public class Setter {

  private final Method method;
  private final MethodHandle handle;
  private final String property;

  Setter(Method method, String property) {
    this.method = method;
    this.property = property;
    try {
      this.handle = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Returns the name of the property.
   *
   * @return The name of the property
   */
  public String getProperty() {
    return property;
  }

  /**
   * Returns the type of the property.
   *
   * @return The type of the property
   */
  public Class<?> getParamType() {
    return method.getParameterTypes()[0];
  }

  /**
   * Sets the property on the specified bean to the specified value.
   *
   * @param bean The object receiving the value
   * @param value The value
   * @throws IllegalAssignmentException If the value cannot be cast to the type of the property,
   *     or if the value is {@code null} and the property has a primitive type. This is a {@link
   *     RuntimeException}, but you might still want to catch it as it can often be handled in a
   *     meaningful way.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void write(Object bean, Object value) throws IllegalAssignmentException, Throwable {
    if (value == null) {
      if (getParamType().isPrimitive()) {
        throw illegalAssignment(null);
      }
    }
    try {
      handle.invoke(bean, value);
    } catch (ClassCastException e) {
      throw illegalAssignment(value);
    }
  }

  /**
   * Generates an {@link IllegalAssignmentException} indicating that the specified value cannot be
   * assigned to the property encapsulated by this {@code Setter}.
   *
   * @param value The value
   * @return An {@link IllegalAssignmentException} for the specified value
   */
  public IllegalAssignmentException illegalAssignment(Object value) {
    return new IllegalAssignmentException(
        method.getDeclaringClass(),
        property,
        getParamType(),
        value);
  }
}
