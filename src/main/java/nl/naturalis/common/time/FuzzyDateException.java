package nl.naturalis.common.time;

/**
 * Thrown if an error occurs while parsing a date string or if not even a year could be extracted
 * from the date string. A {@code FuzzyDateException} may also be thrown while parsing the XML
 * configuration file configuring the {@link FuzzyDateParser}.
 *
 * @author Ayco Holleman
 */
public class FuzzyDateException extends Exception {

  static final String ERR_CASE_SENSITIVITY_FIXED =
      "Case sensitivity cannot be set for predefined DateTimeFormatter";

  static FuzzyDateException notParsable(String dateString) {
    String fmt = "\"%s\" not parsable using the provided ParseAttempt(s)";
    return new FuzzyDateException(String.format(fmt, dateString));
  }

  static FuzzyDateException missingYear(String dateString) {
    String fmt = "Failed to extract year from \"%s\"";
    return new FuzzyDateException(String.format(fmt, dateString));
  }

  static FuzzyDateException cannotCreateFilter(String name) {
    String fmt = "Cannot create filter for \"%s\"";
    return new FuzzyDateException(String.format(fmt, name));
  }

  static FuzzyDateException noSuchResolverStyle(String name) {
    String fmt = "Invalid resolver style: \"%s\"";
    return new FuzzyDateException(String.format(fmt, name));
  }

  static FuzzyDateException unsupportedDateTimeClass(String className) {
    String fmt = "Unknown or unsupported date/time class: %s";
    return new FuzzyDateException(String.format(fmt, className));
  }

  /**
   * Creates a new {@code FuzzyDateException} with the specified message.
   *
   * @param message The message
   */
  public FuzzyDateException(String message) {
    super(message);
  }
}
