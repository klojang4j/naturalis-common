package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import nl.naturalis.common.ExceptionMethods;

class GetInvoker {

  final Class<?> returnType;
  final MethodHandle getter;

  GetInvoker(Method method) {
    returnType = method.getReturnType();
    try {
      getter = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getter, returnType);
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
    GetInvoker other = (GetInvoker) obj;
    return getter == other.getter && returnType == other.returnType;
  }
}
