package nl.naturalis.common;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static nl.naturalis.common.Check.notNull;

/**
 * Parses strings into enum constants by using a normalizer for both enum
 * constant names and the strings to be parsed into the enum constants.
 * Internally the <code>EnumParser</code> creates a string-to-enum map with the
 * normalized enum constant names as keys to optimize the speed of parsing.
 *
 * @author Ayco Holleman
 *
 */
public class EnumParser<T extends Enum<T>> {

  /**
   * The default way of normalizing enum constant names and client-provided
   * strings to be parsed into enum constants. Removes ' ', '-' and '_', and makes
   * it an all-lowercase string.
   */
  public static final UnaryOperator<String> DEFAULT_NORMALIZER =
      s -> notNull(s, "enum string").replaceAll("[-_ ]", "").toLowerCase();

  private final Class<T> enumClass;
  private final UnaryOperator<String> normalizer;
  private final Map<String, T> lookups;

  /**
   * Creates an <code>EnumParser</code> for the provided enum class, using the
   * {@link #DEFAULT_NORMALIZER} to normalize enum constant names the strings
   * passed to the {@link #parse(String) parse} method.
   *
   * @param enumClass
   */
  public EnumParser(Class<T> enumClass) {
    this(enumClass, DEFAULT_NORMALIZER);
  }

  /**
   * Creates an <code>EnumParser</code> for the provided enum class, using the
   * provided <code>normalizer</code> to normalize enum constant names as well as
   * the strings passed to the {@link #parse(String) parse} method.
   *
   * @param enumClass
   * @param normalizer
   */
  public EnumParser(Class<T> enumClass, UnaryOperator<String> normalizer) {
    this.enumClass = enumClass;
    this.normalizer = normalizer;
    this.lookups = Arrays.stream(enumClass.getEnumConstants())
        .map(e -> entry(normalizer.apply(e.name()), e))
        .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  /**
   * Parses the provided value into an instant of the enum class managed by this
   * <code>EnumParser</code>.
   *
   * @param value
   * @return
   */
  public T parse(String value) {
    Check.notNull(value, "Cannot parse null into %s", enumClass.getSimpleName());
    T constant = lookups.get(normalizer.apply(value));
    if (constant == null) {
      String msg = String.format("Invalid value for %s: \"%s\". Valid values: %s",
          enumClass.getSimpleName(),
          value,
          Arrays.stream(enumClass.getEnumConstants()).map(Enum::toString).collect(joining(", ")));
      throw new IllegalArgumentException(msg);
    }
    return constant;
  }

}
