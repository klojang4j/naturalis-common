package nl.naturalis.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import nl.naturalis.common.check.Check;

/**
 * Parses strings into enum constants by uniformly normalizing both the strings to be parsed and the
 * names of the enum constants. The normalization function is customizable. Internally {@code
 * EnumParser} maintains a string-to-enum map with normalized versions of {@code Enum.name()} and
 * {@code Enum.toString()} as keys. The strings to be parsed are normalized in the same way and then
 * looked up in the map.
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

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: \"%s\"";
  private static final String ERR_BAD_NORMALIZER =
      "Normalizer must produce unique strings for enum constants";

  /**
   * The default normalization function. Removes spaces, hyphens and underscores and returns an
   * all-lowercase string.
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
    HashMap<String, T> tmp = new HashMap<>(enumClass.getEnumConstants().length);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              if (null != tmp.put(normalizer.apply(e.toString()), e)) {
                throw new IllegalArgumentException(ERR_BAD_NORMALIZER);
              }
              if (e.toString() != e.name()) {
                if (null != tmp.put(normalizer.apply(e.name()), e)) {
                  throw new IllegalArgumentException(ERR_BAD_NORMALIZER);
                }
              }
            });
    this.lookups = tmp;
  }

  /**
   * Parses the provided value into an instant of the enum class managed by this {@code EnumParser}.
   *
   * @param value The string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to one of the enum's
   *     constants.
   */
  public T parse(String value) throws IllegalArgumentException {
    Check.notNull(value);
    T constant = lookups.get(normalizer.apply(value));
    if (constant == null) {
      throw new IllegalArgumentException(
          String.format(ERR_INVALID_VALUE, enumClass.getSimpleName(), value));
    }
    return constant;
  }
}
