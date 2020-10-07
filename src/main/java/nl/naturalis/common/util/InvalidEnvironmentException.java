package nl.naturalis.common.util;

/**
 * A {@code RuntimeException} thrown by the {@link EnvManager} class if an error occurs while
 * retrieving the value of an environment variable.
 *
 * @author Ayco Holleman
 */
public class InvalidEnvironmentException extends RuntimeException {

  static InvalidEnvironmentException missingEnvironmentVariable(String name) {
    String fmt = "Missing environment variable \"%s\"";
    return new InvalidEnvironmentException(String.format(fmt, name));
  }

  static InvalidEnvironmentException notConvertible(String name, String value, Class<?> target) {
    String fmt = "Environment variable \"%s\" not convertible to %s. Value is \"%s\"";
    String msg = String.format(fmt, name, target.getClass().getName(), value);
    return new InvalidEnvironmentException(msg);
  }

  private InvalidEnvironmentException(String message, Throwable cause) {
    super(message, cause);
  }

  private InvalidEnvironmentException(String message) {
    super(message);
  }
}
