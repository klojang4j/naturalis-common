package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

public class Getter {

  private final MethodHandle method;
  private final String property;
  private final Class<?> returnType;

  Getter(Method method, String property) {
    this.property = property;
    this.returnType = method.getReturnType();
    try {
      this.method = lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  Getter(Class<?> beanClass, String methodName, Class<?> returnType, String property)
      throws NoSuchMethodException {
    this.property = property;
    this.returnType = returnType;
    try {
      this.method = lookup().findVirtual(beanClass, methodName, methodType(returnType));
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public String getProperty() {
    return property;
  }

  public Class<?> getReturnType() {
    return returnType;
  }

  public Object read(Object bean) throws Throwable {
    return method.invoke(bean);
  }
}
