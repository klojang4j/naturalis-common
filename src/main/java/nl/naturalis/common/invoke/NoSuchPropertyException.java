package nl.naturalis.common.invoke;

public class NoSuchPropertyException extends RuntimeException {

  private static final String NO_SUCH_PROPERTY = "No such property: \"%s\" in %s";

  static NoSuchPropertyException noSuchProperty(String property) {
    return new NoSuchPropertyException(String.format(NO_SUCH_PROPERTY, property));
  }

  public NoSuchPropertyException(String message) {
    super(message);
  }
}
