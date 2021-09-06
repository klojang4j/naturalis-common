package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

public class NoSuchPropertyException extends InvokeException {

  private static final String NO_SUCH_PROPERTY = "No such property in %s: \"%s\"";

  public static NoSuchPropertyException noSuchProperty(Object bean, String property) {
    String cn = ClassMethods.getPrettyClassName(bean.getClass());
    return new NoSuchPropertyException(NO_SUCH_PROPERTY, cn, property);
  }

  public NoSuchPropertyException(String message, Object... msgArgs) {
    super(message, msgArgs);
  }
}
