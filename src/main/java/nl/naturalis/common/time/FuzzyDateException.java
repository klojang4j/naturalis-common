package nl.naturalis.common.time;

/**
 * Thrown if an error occurs while parsing a date string or if no year could be extract from the
 * date string.
 *
 * @author Ayco Holleman
 */
public class FuzzyDateException extends Exception {

  static final String ERR_CASE_SENSITIVTY_FIXED =
      "Case sensitivity cannot be set for pre-built DateTimeFormatter";

  static FuzzyDateException notParsable(String dateString) {
    String fmt = "Date string \"%s\" could not be parsed using the provided ParseAttempt(s)";
    return new FuzzyDateException(String.format(fmt, dateString));
  }

  static FuzzyDateException missingYear(String dateString) {
    String fmt = "Failed to extract year from date string \"%s\"";
    return new FuzzyDateException(String.format(fmt, dateString));
  }

  static FuzzyDateException cannotCreateFormatter(String name) {
    String fmt = "Cannot create DateTimeFormatter for \"%s\"";
    return new FuzzyDateException(String.format(fmt, name));
  }

  static FuzzyDateException cannotCreateFormatter(String name, String reason) {
    String fmt = "Cannot create DateTimeFormatter for \"%s\": %s";
    return new FuzzyDateException(String.format(fmt, name, reason));
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

  FuzzyDateException(String message) {
    super(message);
  }
}
