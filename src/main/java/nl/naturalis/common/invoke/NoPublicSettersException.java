package nl.naturalis.common.invoke;

public class NoPublicSettersException extends InvokeException {

  public static NoPublicSettersException noPublicSetters(Class<?> clazz) {
    return new NoPublicSettersException(
        "Class %s does not have any public setters", clazz.getName());
  }

  NoPublicSettersException(String message, Object... msgArgs) {
    super(message, msgArgs);
  }
}
