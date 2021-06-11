package nl.naturalis.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.containingKey;
import static nl.naturalis.common.check.CommonChecks.in;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * Parses strings into enum constants. Internally {@code EnumParser} maintains a string-to-enum map
 * with normalized versions of {@code Enum#name() Enum.name()} and {@code Enum#toString()
 * Enum.toString()} as keys. The strings to be parsed back into enum constants are first normalized
 * using the same normalizer function and then looked up in the map. You can provide your own
 * normalizer function. The stringified ordinal values of the enum constants are also in the map.
 * Thus retrieval by ordinal value will also work.
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
  private static final String BAD_VALUE = "Invalid value: %s";

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
    this.normalizer = Check.notNull(normalizer, "normalizer").ok();
    HashMap<String, T> map = new HashMap<>(enumClass.getEnumConstants().length);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              map.put(String.valueOf(e.ordinal()), e);
              if (e.name().equals(e.toString())) {
                String key = normalizer.apply(e.name());
                Check.that(map).isNot(containingKey(), key, BAD_KEY, key);
                map.put(key, e);
              } else {
                String key0 = normalizer.apply(e.name());
                String key1 = normalizer.apply(e.toString());
                Check.that(map)
                    .isNot(containingKey(), key0, BAD_KEY, key0)
                    .isNot(containingKey(), key1, BAD_KEY, key1);
                map.put(key0, e);
                map.put(key1, e);
              }
            });
    this.lookups = CollectionMethods.tightHashMap(map);
  }

  /**
   * Parses the specified value into an enum constant. The argument must be either an {@code
   * Integer} specifying the enum constant's ordinal value or a {@code String} corresponding to the
   * enum constant's name or {@code toString()} value.
   *
   * @param value The ordinal value or string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to one of the enum's
   *     constants.
   */
  public T parse(Object value) throws IllegalArgumentException {
    Check.that(value)
        .is(notNull(), "Cannot parse null into enum constant")
        .has(type(), in(), List.of(String.class, Integer.class));
    return Check.that(normalizer.apply(value.toString()))
        .is(keyIn(), lookups, BAD_VALUE, value)
        .ok(lookups::get);
  }
}
