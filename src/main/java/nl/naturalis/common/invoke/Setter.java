package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.check.CommonChecks.notNull;

public class Setter {

  private static final String ERR_NULL = "Value for %s.%s (%s) must be not be null";
  private static final String ERR_BAD_TYPE = "Value for %s.%s must be instance of %s (was %s)";

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
    if (value == null) {
      if (getParamType().isPrimitive()) {
        String cn0 = bean.getClass().getSimpleName();
        String cn1 = getParamType().getSimpleName();
        Check.that(value).is(notNull(), ERR_NULL, cn0, property, cn1);
      }
    }
    try {
      method.invoke(bean, value);
    } catch (ClassCastException e) {
      String cn0 = bean.getClass().getSimpleName();
      String cn1 = simpleClassName(getParamType());
      String cn2 = simpleClassName(value.getClass());
      Check.fail(ERR_BAD_TYPE, cn0, property, cn1, cn2);
    }
  }
}
