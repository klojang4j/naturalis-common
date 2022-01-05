package nl.naturalis.common.util;

import nl.naturalis.common.Pair;
import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.check.CommonChecks.*;

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
  private static final String BAD_VALUE = "Cannot parse ${arg} into enum constant of ${0}";
  private static final String BAD_ORDINAL = "Invalid ordinal value for enum ${0}: ${arg}";

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
   * Parses the specified value into an enum constant. The argument must be either an {@code
   * Integer} specifying the enum constant's ordinal value or a {@code String} corresponding to the
   * enum constant's name or {@code toString()} value.
   *
   * @param value The ordinal value or string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to one of the enum's
   *     constants.
   */
  @SuppressWarnings("unchecked")
  public T parse(Object value) throws IllegalArgumentException {
    String name = enumClass.getName();
    Check.that(value).is(notNull(), BAD_VALUE, name);
    if (value.getClass() == enumClass) {
      return (T) value;
    } else if (value.getClass() == Integer.class) {
      int i = (Integer) value;
      T[] consts = enumClass.getEnumConstants();
      Check.that(i).is(between(), Pair.of(0, consts.length), BAD_ORDINAL, name);
      return consts[i];
    }
    return Check.that(normalizer.apply(value.toString()))
        .is(keyIn(), lookups, BAD_VALUE, value, name)
        .ok(lookups::get);
  }
}
