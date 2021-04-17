package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;

public class Setter<T> {

  private final MethodHandle method;
  private final Class<T> paramType;

  @SuppressWarnings("unchecked")
  Setter(Method method) {
    paramType = (Class<T>) method.getParameterTypes()[0];
    try {
      this.method = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public MethodHandle getMethod() {
    return method;
  }

  public Class<T> getParamType() {
    return paramType;
  }
}
