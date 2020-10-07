package nl.naturalis.common.util;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.util.InvalidEnvironmentException.missingEnvironmentVariable;
import static nl.naturalis.common.util.InvalidEnvironmentException.notConvertible;

/**
 * Allows for easy access to OS environment variables.
 *
 * @author Ayco Holleman
 */
public class EnvManager {

  private static final EnvManager em = new EnvManager();

  /**
   * Returns an {@code EnvManager} instance.
   *
   * @return An {@code EnvManager} instance
   */
  public static EnvManager envManager() {
    return em;
  }

  private EnvManager() {}

  /**
   * Returns the value of the specified environment variable.
   *
   * @param name The name of the environment variable
   * @return An {@code Optional} containing the value of the environment variable or an empty {@code
   *     Optional} if there is no such environment variable
   */
  public Optional<String> get(String name) {
    return ifNotNull(env(name), Optional::of, Optional::empty);
  }

  /**
   * Returns the value of the specified environment variable or a default value if there is no
   * environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @param dfault The value to use if the environment variable is not present
   * @return The value of the environment variable
   */
  public String get(String name, String dfault) {
    return get(name).orElse(dfault);
  }

  /**
   * Returns the value of the specified environment variable or throws an {@code
   * InvalidEnvironmentException} if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If there is no environment variable with the specified name
   */
  public String getRequired(String name) throws InvalidEnvironmentException {
    return get(name).orElseThrow(() -> missingEnvironmentVariable(name));
  }

  /**
   * Returns the value of the specified environment variable as an integer.
   *
   * @param name The name of the environment variable
   * @return An {@code OptionalInt} containing the value of the environment variable or an empty
   *     {@code OptionalInt} if there is no such environment variable
   * @throws InvalidEnvironmentException If the value of the environment variable could not be
   *     converted to an integer
   */
  public OptionalInt getAsInt(String name) throws InvalidEnvironmentException {
    String value = env(name);
    if (value == null) {
      return OptionalInt.empty();
    }
    BigDecimal bd = new BigDecimal(value);
    try {
      return OptionalInt.of(bd.intValueExact());
    } catch (ArithmeticException e) {
      throw notConvertible(name, value, int.class);
    }
  }

  /**
   * Returns the value of the specified environment variable as an integer or a default value if
   * there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @param dfault The value to use if the environment variable is not present
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If the value of the environment variable could not be
   *     converted to an integer
   */
  public int getAsInt(String name, int dfault) throws InvalidEnvironmentException {
    return getAsInt(name).orElse(dfault);
  }

  /**
   * Returns the value of the specified environment variable as an integer or throws an {@code
   * InvalidEnvironmentException} if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @param dfault The value to use if the environment variable is not presentt
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If there is no environment variable with the specified
   *     name, or if the value of the environment variable could not be converted to an integer
   */
  public int getRequiredAsInt(String name) throws InvalidEnvironmentException {
    return getAsInt(name).orElseThrow(() -> missingEnvironmentVariable(name));
  }

  private static String env(String name) {
    return System.getenv(name);
  }
}
