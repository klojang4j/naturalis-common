package nl.naturalis.common.time;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A DateStringFilter implementation used as a very course, last ditch attempt to at least extract a year from date strings like "2007b" or
 * "1914 - 1918". If you are determined to at least extract a year from a piece of text using the {@link FuzzyDateParser}, you can
 * instantiate it with a {@link ParseSpec} that specifies this filter to be applied. The {@code YearFilter} is also used, as a last resort,
 * by the {@link FuzzyDateParser#DEFAULT default} {code FuzzyDateParser}.
 */
public class YearFilter implements UnaryOperator<String> {

  private static final int MIN_YEAR = 1500;
  private static final int MAX_YEAR = 2100;

  private static final Pattern PATTERN = Pattern.compile("(^|\\D+)(\\d{4})($|\\D+)");

  public YearFilter() {}

  @Override
  public String apply(String dateString) {
    Matcher matcher = PATTERN.matcher(dateString);
    if (matcher.find()) {
      String filtered = matcher.group(2);
      int year = Integer.parseInt(filtered);
      if (year >= MIN_YEAR && year <= MAX_YEAR) {
        return filtered;
      }
    }
    return null;
  }

}
