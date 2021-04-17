package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import nl.naturalis.common.ExceptionMethods;

public class Getter<T> {

  private final MethodHandle method;
  private final Class<T> returnType;

  @SuppressWarnings("unchecked")
  Getter(Method method) {
    returnType = (Class<T>) method.getReturnType();
    try {
      this.method = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public MethodHandle getMethod() {
    return method;
  }

  public Class<T> getReturnType() {
    return returnType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, returnType);
  }

  @Override
  @SuppressWarnings("rawtypes")
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
