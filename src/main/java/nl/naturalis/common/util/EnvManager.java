package nl.naturalis.common.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.internal.VisibleForTesting;
import static nl.naturalis.common.CollectionMethods.saturatedEnumMap;
import static nl.naturalis.common.FunctionalMethods.asOptional;
import static nl.naturalis.common.FunctionalMethods.asOptionalInt;
import static nl.naturalis.common.ObjectMethods.ifEmpty;
import static nl.naturalis.common.ObjectMethods.ifNotEmpty;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.notEmpty;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.util.EnvManager.EmptyValue.DEFAULT;
import static nl.naturalis.common.util.EnvManager.EmptyValue.EMPTY;
import static nl.naturalis.common.util.EnvManager.EmptyValue.UNDEFINED;
import static nl.naturalis.common.util.InvalidEnvironmentException.MISSING_ENV_VAR;
import static nl.naturalis.common.util.InvalidEnvironmentException.NOT_CONVERTIBLE;
/**
 * Simple utility class wrapping {@link System#getenv(String) System.getenv()}. {@code EnvManager}
 * instances come in three different flavours, according to how they read environment variables (see
 * {@link EmptyValue}). All flavours are pre-instantiated, so there is no extra cost associated with
 * using different flavours for different environment variables.
 *
 * @author Ayco Holleman
 */
public class EnvManager {

  /**
   * Specifies how to treat environment variables that are present but have no value. In other
   * words, environment variables that were created like {@code export FOO=}.
   *
   * @author Ayco Holleman
   */
  public static enum EmptyValue {
    /**
     * Treat as having value {@code ""} (empty string). This is how {@link System#getenv(String)
     * System.getenv()} works.
     */
    EMPTY,
    /**
     * Treat as though the environment variable was not defined at all. This will cause the {@code
     * getRequiredXXX} methods to throw an {@link InvalidEnvironmentException} if the environment
     * variable is present but has no value.
     */
    UNDEFINED,
    /**
     * For methods that let you specify a default value, treat as having the same value as the
     * specified default value. Otherwise treat just like {@link #EMPTY}. This is how {@code
     * EnvManager} works by default. That is, the {@code EnvManager} you get when calling {@link
     * EnvManager#envManager() envManager()} (without arguments) uses this strategy.
     */
    DEFAULT
  }

  private static final EnumMap<EmptyValue, EnvManager> mgrs =
      saturatedEnumMap(
          EmptyValue.class,
          new EnvManager(EMPTY),
          new EnvManager(UNDEFINED),
          new EnvManager(DEFAULT));

  /**
   * Returns an {@code EnvManager} instance that uses the {@link EmptyValue#DEFAULT} strategy.
   * Equivalent to {@code envManager(EMPTY_IS_DEFAULT)}.
   *
   * @return An {@code EnvManager} instance
   */
  public static EnvManager envManager() {
    return envManager(DEFAULT);
  }

  /**
   * Returns an {@code EnvManager} instance that uses the specified strategy.
   *
   * @param ev The strategy to use for environment variables that are present but without value
   * @return An {@code EnvManager} instance
   */
  public static EnvManager envManager(EmptyValue ev) {
    return mgrs.get(ev);
  }

  private final EmptyValue empty;
  private final Map<String, String> env;

  private EnvManager(EmptyValue empty) {
    this.empty = empty;
    this.env = System.getenv();
  }

  @VisibleForTesting
  EnvManager(EmptyValue empty, Map<String, String> env) {
    this.empty = empty;
    this.env = env;
  }

  /**
   * Returns an {@code Optional} containing the value of the specified environment variable or an
   * empty {@code Optional} if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @return An {@code Optional} containing the value of the environment variable or an empty {@code
   *     Optional} if there is no environment variable with the specified name
   */
  public Optional<String> get(String name) {
    Check.notNull(name, "name");
    return empty == UNDEFINED
        ? ifNotEmpty(getenv(name), Optional::of, Optional::empty)
        : ifNotNull(getenv(name), Optional::of, Optional::empty);
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
    Check.notNull(name, "name");
    return empty == UNDEFINED || empty == DEFAULT
        ? ifEmpty(getenv(name), dfault)
        : ifNull(getenv(name), dfault);
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
    Check.notNull(name, "name");
    return empty == UNDEFINED
        ? check(name).is(notEmpty(), MISSING_ENV_VAR, name).ok()
        : check(name).is(notNull(), MISSING_ENV_VAR, name).ok();
  }

  /**
   * Returns an {@code OptionalInt} containing the integer value of the specified environment
   * variable or an empty {@code OptionalInt} if there is no environment variable with the specified
   * name. Parsing is substantially stricter than {@link Integer#parseInt(String) Integer.parseInt}.
   * See {@link NumberMethods#asPlainInt(String) NumberMethods.asPlainInt}.
   *
   * @param name The name of the environment variable
   * @return An {@code OptionalInt} containing the value of the environment variable or an empty
   *     {@code OptionalInt} if there is no such environment variable
   * @throws InvalidEnvironmentException If the value of the environment variable could not be
   *     converted to an integer
   */
  public OptionalInt getAsInt(String name) throws InvalidEnvironmentException {
    Check.notNull(name, "name");
    String val = getenv(name);
    try {
      return empty == UNDEFINED
          ? ifNotEmpty(val, asOptionalInt(NumberMethods::asPlainInt), OptionalInt::empty)
          : ifNotNull(val, asOptionalInt(NumberMethods::asPlainInt), OptionalInt::empty);
    } catch (NumberFormatException e) {
      throw notConvertible(name, int.class, e.getMessage());
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
    String val = getenv(name);
    try {
      return empty == UNDEFINED || empty == DEFAULT
          ? ifNotEmpty(val, NumberMethods::asPlainInt, () -> dfault)
          : ifNotNull(val, NumberMethods::asPlainInt, () -> dfault);
    } catch (NumberFormatException e) {
      throw notConvertible(name, int.class, e.getMessage());
    }
  }

  /**
   * Returns the value of the specified environment variable as an {@code int} or throws an {@code
   * InvalidEnvironmentException} if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If there is no environment variable with the specified
   *     name, or if the value of the environment variable could not be converted to an integer
   */
  public int getRequiredAsInt(String name) throws InvalidEnvironmentException {
    Check.notNull(name, "name");
    try {
      return empty == UNDEFINED
          ? check(name).is(notEmpty(), MISSING_ENV_VAR, name).ok(NumberMethods::asPlainInt)
          : check(name).is(notNull(), MISSING_ENV_VAR, name).ok(NumberMethods::asPlainInt);
    } catch (NumberFormatException e) {
      throw notConvertible(name, int.class, e.getMessage());
    }
  }

  /**
   * Returns an {@code Optional} containing the boolean value of the specified environment variable
   * or an empty {@code Optional} if there is no environment variable with the specified name.
   * Parsing is somewhat stricter than {@link Boolean#valueOf(String) Boolean.valueOf} as the only
   * valid values are "true" and "false" (ignoring case); not "true" for {@code true} and anything
   * else for {@code false}.
   *
   * @param name The name of the environment variable
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If the value of the environment variable could not be
   *     converted to a {@code boolean}
   */
  public Optional<Boolean> getAsBoolean(String name) throws InvalidEnvironmentException {
    Check.notNull(name, "name");
    String val = getenv(name);
    try {
      return empty == UNDEFINED
          ? ifNotEmpty(val, asOptional(this::parseBoolean), Optional::empty)
          : ifNotNull(val, asOptional(this::parseBoolean), Optional::empty);
    } catch (IllegalArgumentException e) {
      throw notConvertible(name, boolean.class, e.getMessage());
    }
  }

  /**
   * Returns the value of the specified environment variable as a {@code boolean} or a default value
   * if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @param dfault The value to use if the environment variable is not present
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If the value of the environment variable could not be
   *     converted to an integer
   */
  public boolean getAsBoolean(String name, boolean dfault) throws InvalidEnvironmentException {
    String val = getenv(name);
    try {
      return empty == UNDEFINED || empty == DEFAULT
          ? ifNotEmpty(val, this::parseBoolean, () -> dfault)
          : ifNotNull(val, this::parseBoolean, () -> dfault);
    } catch (NumberFormatException e) {
      throw notConvertible(name, boolean.class, e.getMessage());
    }
  }

  /**
   * Returns the value of the specified environment variable as an {@code int} or throws an {@code
   * InvalidEnvironmentException} if there is no environment variable with the specified name.
   *
   * @param name The name of the environment variable
   * @return The value of the environment variable
   * @throws InvalidEnvironmentException If there is no environment variable with the specified
   *     name, or if the value of the environment variable could not be converted to an integer
   */
  public boolean getRequiredAsBoolean(String name) throws InvalidEnvironmentException {
    Check.notNull(name, "name");
    try {
      return empty == UNDEFINED
          ? check(name).is(notEmpty(), MISSING_ENV_VAR, name).ok(this::parseBoolean)
          : check(name).is(notNull(), MISSING_ENV_VAR, name).ok(this::parseBoolean);
    } catch (NumberFormatException e) {
      throw notConvertible(name, boolean.class, e.getMessage());
    }
  }

  private Boolean parseBoolean(String val) {
    if ("true".equalsIgnoreCase(val)) {
      return Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(val)) {
      return Boolean.FALSE;
    }
    String msg = "Value must be \"true\" or \"false\" (ignoring case)";
    throw new IllegalArgumentException(msg);
  }

  private Check<String, InvalidEnvironmentException> check(String name) {
    return Check.that(getenv(name), InvalidEnvironmentException::new);
  }

  private String getenv(String name) {
    return env.get(name);
  }

  private InvalidEnvironmentException notConvertible(
      String varname, Class<?> into, String message) {
    String reason = ifEmpty(message, message.getClass().getSimpleName());
    String msg = String.format(NOT_CONVERTIBLE, varname, into.getName(), getenv(varname), reason);
    return new InvalidEnvironmentException(msg);
  }
}
