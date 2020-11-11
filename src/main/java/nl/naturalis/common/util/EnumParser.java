package nl.naturalis.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.notHasKey;

/**
 * Parses strings into enum constants. Internally {@code EnumParser} maintains a string-to-enum map
 * with normalized versions of {@code Enum#name() Enum.name()} and {@code Enum#toString()
 * Enum.toString()} as keys. Input strings are normalized in the same way and then looked up in the
 * map. The normalization function is customizable.
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

  private static final String BAD_KEY = "Non-unique key: %s";
  private static final String BAD_VAL = "Invalid value: %s";

  /**
   * The default normalization function. Removes spaces, hyphens and underscores and returns an
   * all-lowercase string. The default normalizer does not allow {@code null} as input string.
   */
  public static final UnaryOperator<String> DEFAULT_NORMALIZER =
      s -> Check.notNull(s).ok().replaceAll("[-_ ]", "").toLowerCase();

  private final UnaryOperator<String> normalizer;
  private final Map<String, T> lookups;

  /**
   * Creates an <code>EnumParser</code> for the specified enum class, using the {@link
   * #DEFAULT_NORMALIZER}.
   *
   * @param enumClass
   */
  public EnumParser(Class<T> enumClass) {
    this(enumClass, DEFAULT_NORMALIZER);
  }

  /**
   * Creates an {@code EnumParser} for the specified enum class, using the specified {@code
   * normalizer} to normalize the strings to be parsed.
   *
   * @param enumClass The enum class managed by this {@code EnumParser}
   * @param normalizer The normalization function
   */
  public EnumParser(Class<T> enumClass, UnaryOperator<String> normalizer) {
    Check.notNull(enumClass, "enumClass");
    HashMap<String, T> map = new HashMap<>(enumClass.getEnumConstants().length);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              if (e.name().equals(e.toString())) {
                String k0 = normalizer.apply(e.name());
                Check.that(map).is(notHasKey(), k0, BAD_KEY, k0);
                map.put(k0, e);
              } else {
                String k0 = normalizer.apply(e.name());
                String k1 = normalizer.apply(e.toString());
                Check.that(map).is(notHasKey(), k0, BAD_KEY, k0).is(notHasKey(), k1, BAD_KEY, k1);
                map.put(k0, e);
                map.put(k1, e);
              }
            });
    this.lookups = CollectionMethods.tightHashMap(map);
    this.normalizer = Check.notNull(normalizer, "normalizer").ok();
  }

  /**
   * Parses the specified value into an enum constant. This method accepts null values, but the
   * normalizer used by this {@code EnumParser} may not. The {@link #DEFAULT_NORMALIZER} does not
   * accept null values.
   *
   * @param value The string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to one of the enum's
   *     constants.
   */
  public T parse(String value) throws IllegalArgumentException {
    return Check.that(normalizer.apply(value))
        .is(keyIn(), lookups, BAD_VAL, value)
        .ok(lookups::get);
  }
}
