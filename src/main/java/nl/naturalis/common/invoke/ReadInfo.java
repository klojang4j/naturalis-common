package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;

class ReadInfo {

  final Class<?> returnType;
  final MethodHandle getter;

  ReadInfo(Method method) {
    returnType = method.getReturnType();
    try {
      getter = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }
}
