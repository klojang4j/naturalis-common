package nl.naturalis.common.time;

/**
 * A {@code DateStringFilter} can optionally be added to a {@link ParseAttempt} to preprocess the
 * date strings before they are parsed by the {@code ParseAttempt}'s {@link
 * java.time.format.DateTimeFormatter}. A {@code DateStringFilter} can be used both to validate and
 * to transform the date strings. If the {@link #validateOrTransform(String) validateOrTransform}
 * method returns {@code null}, the {@code ParseAttempt} is cut short by the {@link FuzzyDateParser}
 * and it will move on to the next {@code ParseAttempt} (if any). If the {@code validateOrTransform}
 * method throws a {@link FuzzyDateException}, the date string is treated as definitely not parsable
 * and the {@code ParseAttempt} and all subsequent {@code ParseAttempt}s are aborted for this date
 * string.
 */
@FunctionalInterface
public interface DateStringFilter {

  /**
   * Validates and/or transforms the date string.
   *
   * @param dateString The date string
   * @return The date string, possibly transformed, or {@code null} to indicate that the {@code
   *     ParseAttempt} should be aborted
   * @throws FuzzyDateException To instruct the parser to give up parsing the date string
   */
  String validateOrTransform(String dateString) throws FuzzyDateException;
}
