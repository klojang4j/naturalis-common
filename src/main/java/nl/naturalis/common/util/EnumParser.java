package nl.naturalis.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notKeyIn;

/**
 * Parses strings into enum constants by uniformly normalizing input strings and enum constant
 * names. The normalization function is customizable. Internally {@code EnumParser} maintains a
 * string-to-enum map with the normalized versions of {@code Enum#name() Enum.name()} and {@code
 * Enum#toString() Enum.toString()} as keys.
 *
 * <p>
 *
 * <pre>
 * enum TransportType {
 *  CAR, BIKE, TRAIN;
 *
 *  private static EnumParser<TransportType> parser = new EnumParser(TransportType.class);
 *
 *  &#64;JsonCreator
 *  public static TransportType parse(String input) {
 *      return parser.parse(input);
 *  }
 * </pre>
 *
 * @author Ayco Holleman
 */
public class EnumParser<T extends Enum<T>> {

  private static final String INVALID_VALUE = "Invalid value for %s: %s";
  private static final String BAD_NORMALIZER = "Normalizer must produce unique strings";

  /**
   * The default normalization function. Removes spaces, hyphens and underscores and returns an
   * all-lowercase string. The default normalizer does not allow {@code null} as input string.
   */
  public static final UnaryOperator<String> DEFAULT_NORMALIZER =
      s -> Check.notNull(s).ok().replaceAll("[-_ ]", "").toLowerCase();

  private final Class<T> enumClass;
  private final UnaryOperator<String> normalizer;
  private final Map<String, T> lookups;

  /**
   * Creates an <code>EnumParser</code> for the provided enum class, using the {@link
   * #DEFAULT_NORMALIZER}.
   *
   * @param enumClass
   */
  public EnumParser(Class<T> enumClass) {
    this(enumClass, DEFAULT_NORMALIZER);
  }

  /**
   * Creates an {@code EnumParser} for the provided enum class, using the provided {@code
   * normalizer} to normalize the strings to be parsed.
   *
   * @param enumClass The enum class managed by this {@code EnumParser}
   * @param normalizer The normalization function
   */
  public EnumParser(Class<T> enumClass, UnaryOperator<String> normalizer) {
    this.enumClass = Check.notNull(enumClass, "enumClass").ok();
    this.normalizer = Check.notNull(normalizer, "normalizer").ok();
    HashMap<String, T> map = new HashMap<>(enumClass.getEnumConstants().length);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              Check.that(normalizer.apply(e.name()))
                  .is(notKeyIn(), map, BAD_NORMALIZER)
                  .then(s -> map.put(s, e));
              if (!e.name().equals(e.toString())) {
                Check.that(normalizer.apply(e.toString()))
                    .is(notKeyIn(), map, BAD_NORMALIZER)
                    .then(s -> map.put(s, e));
              }
            });
    this.lookups = CollectionMethods.tightHashMap(map);
  }

  /**
   * Parses the provided value into an instant of the enum class managed by this {@code EnumParser}.
   * This method accepts null values, but the normalizer used by this {@code EnumParser} may not.
   * The {@link #DEFAULT_NORMALIZER} does not accept null values.
   *
   * @param value The string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to one of the enum's
   *     constants.
   */
  public T parse(String value) throws IllegalArgumentException {
    T constant = lookups.get(normalizer.apply(value));
    if (constant == null) {
      String msg = String.format(INVALID_VALUE, enumClass.getSimpleName(), value);
      throw new IllegalArgumentException(msg);
    }
    return constant;
  }
}
