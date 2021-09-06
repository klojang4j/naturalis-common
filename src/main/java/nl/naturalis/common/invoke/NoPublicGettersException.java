package nl.naturalis.common.invoke;

public class NoPublicGettersException extends InvokeException {

  public static NoPublicGettersException noPublicGetters(Class<?> clazz) {
    return new NoPublicGettersException(
        "Class %s does not have any public getters", clazz.getName());
  }

  NoPublicGettersException(String message, Object... msgArgs) {
    super(message, msgArgs);
  }
}
