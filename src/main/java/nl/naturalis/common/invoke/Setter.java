package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.getPrettySimpleClassName;

public class Setter {

  private static final String ERR_BAD_TYPE = "Value for %s.%s must be instance of %s";

  private final MethodHandle method;
  private final String property;
  private final Class<?> paramType;

  Setter(Method method, String property) {
    paramType = method.getParameterTypes()[0];
    this.property = property;
    try {
      this.method = MethodHandles.lookup().unreflect(method);
    } catch (IllegalAccessException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public String getProperty() {
    return property;
  }

  public Class<?> getParamType() {
    return paramType;
  }

  public void write(Object bean, Object value) throws Throwable {
    if (!getParamType().isInstance(value)) {
      String cn = getPrettySimpleClassName(getParamType());
      Check.fail(ERR_BAD_TYPE, bean.getClass(), property, cn);
    }
    method.invoke(bean, value);
  }
}
