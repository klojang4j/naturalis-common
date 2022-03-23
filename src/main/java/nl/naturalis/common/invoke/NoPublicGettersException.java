package nl.naturalis.common.invoke;

/**
 * Thrown when trying to read values from beans that don't have public getters.
 */
public class NoPublicGettersException extends InvokeException {

  static NoPublicGettersException noPublicGetters(Class<?> clazz) {
    return new NoPublicGettersException("Class %s does not have any public getters", clazz.getName());
  }

  NoPublicGettersException(String message, Object... msgArgs) {
    super(message, msgArgs);
  }
}
