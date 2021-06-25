package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import nl.naturalis.common.ExceptionMethods;

public class Getter {

  private final MethodHandle method;
  private final String property;
  private final Class<?> returnType;

  Getter(Method method, String property) {
    this.property = property;
    this.returnType = method.getReturnType();
    try {
      this.method = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public MethodHandle getMethod() {
    return method;
  }

  public String getProperty() {
    return property;
  }

  public Class<?> getReturnType() {
    return returnType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, returnType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (getClass() != obj.getClass()) {
      return false;
    }
    Getter other = (Getter) obj;
    return method == other.method && returnType == other.returnType;
  }
}
