package nl.naturalis.common.invoke;

public class NoSuchPropertyException extends RuntimeException {

  public NoSuchPropertyException(String property) {
    super("No such property: \"" + property + "\"");
  }
}
