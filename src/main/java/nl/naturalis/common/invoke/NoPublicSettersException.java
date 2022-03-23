package nl.naturalis.common.invoke;

/**
 * Thrown when trying to set values on beans that don't have public setters.
 */
public class NoPublicSettersException extends InvokeException {

  static NoPublicSettersException noPublicSetters(Class<?> clazz) {
    return new NoPublicSettersException(
            "Class %s does not have any public setters", clazz.getName());
  }

  NoPublicSettersException(String message, Object... msgArgs) {
    super(message, msgArgs);
  }
}
