package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

public class NoSuchPropertyException extends RuntimeException {

  private static final String NO_SUCH_PROPERTY = "No such property in %s: \"%s\"";

  static NoSuchPropertyException noSuchProperty(Object bean, String property) {
    String cn = ClassMethods.prettyClassName(bean.getClass());
    String msg = String.format(NO_SUCH_PROPERTY, cn, property);
    return new NoSuchPropertyException(msg);
  }

  public NoSuchPropertyException(String message) {
    super(message);
  }
}
