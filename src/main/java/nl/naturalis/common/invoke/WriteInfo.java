package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;

class WriteInfo {

  final Class<?> paramType;
  final MethodHandle setter;

  WriteInfo(Method method) {
    paramType = method.getParameterTypes()[0];
    try {
      setter = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }
}
