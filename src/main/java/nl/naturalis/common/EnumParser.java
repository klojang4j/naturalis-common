package nl.naturalis.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import static nl.naturalis.common.Check.notNull;

/**
 * Parses strings into enum constants. Parsing is done via an string-to-enum map where the keys are
 * normalized versions of {@code Enum.name()} and {@code Enum.toString()}. The strings to be parsed
 * are normalized in the same way and then looked up in the map.
 *
 * <pre>
 * enum TransportType {
 *  CAR, BIKE, TRAIN;
 *
 *  private static EnumParser<TransportType> parser = new EnumParser(TransportType.class);
 *
 *  public static TransportType parse(String input) {
 *      return parser.parse(input);
 *  }
 * </pre>
 *
 * @author Ayco Holleman
 */
public class EnumParser<T extends Enum<T>> {

  private static final String ERR_NULL_VALUE = "Cannot parse null into enum constant";
  private static final String ERR_INVALID_VALUE = "Invalid value for %s: \"%s\"";

  /**
   * The default normalization function. Removes ' ', '-' and '_', and makes it an all-lowercase
   * string.
   */
  public static final UnaryOperator<String> DEFAULT_NORMALIZER =
      s -> notNull(s, ERR_NULL_VALUE, "").replaceAll("[-_ ]", "").toLowerCase();

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
    this.enumClass = Check.notNull(enumClass, "enumClass");
    this.normalizer = Check.notNull(normalizer, "normalizer");
    HashMap<String, T> tmp = new HashMap<>(enumClass.getEnumConstants().length * 2);
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            e -> {
              tmp.put(normalizer.apply(e.toString()), e);
              if (e.toString() != e.name()) {
                tmp.put(normalizer.apply(e.name()), e);
              }
            });
    this.lookups = tmp;
  }

  /**
   * Parses the provided value into an instant of the enum class managed by this {@code EnumParser}.
   *
   * @param value The string to be parsed into an enum constant.
   * @return The enum constant
   * @throws IllegalArgumentException If the string could not be mapped to any of the enum's
   *     constants.
   */
  public T parse(String value) throws IllegalArgumentException {
    Check.notNull(value, "value");
    T constant = lookups.get(normalizer.apply(value));
    if (constant == null) {
      throw new IllegalArgumentException(
          String.format(ERR_INVALID_VALUE, enumClass.getSimpleName(), value));
    }
    return constant;
  }
}
