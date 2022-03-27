package nl.naturalis.common.invoke;

import nl.naturalis.common.ExceptionMethods;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Represents a getter for a single property.
 *
 * @author Ayco Holleman
 */
public final class Getter {

  private final Method method;
  private final MethodHandle handle;
  private final String property;

  Getter(Method method, String property) {
    this.method = method;
    this.property = property;
    try {
      this.handle = lookup().unreflect(method);
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
  public Class<?> getReturnType() {
    return method.getReturnType();
  }

  /**
   * Reads the value of the property off the specified bean
   *
   * @param bean The bean off which to read the property's value
   * @return The value of the property
   * @throws Throwable The unspecified {@code Throwable} associated with calls to {@link
   *     MethodHandle#invoke(Object...) MethodHandle.invoke}.
   */
  public Object read(Object bean) throws Throwable {
    return handle.invoke(bean);
  }
}
