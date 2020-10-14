package nl.naturalis.common.util;

/**
 * A {@code RuntimeException} thrown by the {@link EnvManager} class if an error occurs while
 * retrieving the value of an environment variable.
 *
 * @author Ayco Holleman
 */
public class InvalidEnvironmentException extends RuntimeException {

  static final String MISSING_ENV_VAR = "Missing environment variable \"%s\"";
  static final String NOT_CONVERTIBLE =
      "Environment variable \"%s\" not convertible to %s: \"%s\" (Reason: %s)";

  InvalidEnvironmentException(String message, Throwable cause) {
    super(message, cause);
  }

  InvalidEnvironmentException(String message) {
    super(message);
  }
}
