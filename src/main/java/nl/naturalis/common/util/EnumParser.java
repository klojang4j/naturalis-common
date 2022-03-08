package nl.naturalis.common.util;

import nl.naturalis.common.TypeConversionException;
import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.notNull;

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

  private static final String BAD_KEY = "Non-unique key: ${arg}";

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
   * Creates an <code>EnumParser</code> for the specified enum class, using the {@link
   * #DEFAULT_NORMALIZER}.
   *
   * @param enumClass The enum class
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
    this.enumClass = Check.notNull(enumClass, "enumClass").ok();
    this.normalizer = Check.notNull(normalizer, "normalizer").ok();
    HashMap<String, T> map = new HashMap<>(enumClass.getEnumConstants().length);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              map.put(String.valueOf(e.ordinal()), e);
              if (e.name().equals(e.toString())) {
                String key = normalizer.apply(e.name());
                Check.that(key).isNot(keyIn(), map, BAD_KEY);
                map.put(key, e);
              } else {
                String key0 = normalizer.apply(e.name());
                String key1 = normalizer.apply(e.toString());
                Check.that(key0).isNot(keyIn(), map, BAD_KEY);
                Check.that(key1).isNot(keyIn(), map, BAD_KEY);
                map.put(key0, e);
                map.put(key1, e);
              }
            });
    this.lookups = Map.copyOf(map);
  }

  /**
   * Parses the specified value into an enum constant. The argument is stringified using its {@code
   * toString{}} method, then normalized and then looked up in the internally maintained
   * string-to-constant table.
   *
   * @param value The value to be mapped an enum constant.
   * @return The enum constant
   * @throws TypeConversionException If the value was {@code null} or could not be mapped to one of
   *     the enum's constants.
   */
  public T parse(Object value) throws TypeConversionException {
    Check.that(value).is(notNull(), () -> new TypeConversionException(value, enumClass));
    String key = normalizer.apply(value.toString());
    return Check.that(lookups.get(key))
        .is(notNull(), () -> new TypeConversionException(value, enumClass))
        .ok();
  }
}
