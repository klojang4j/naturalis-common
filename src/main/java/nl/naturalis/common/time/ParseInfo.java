package nl.naturalis.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;
import nl.naturalis.common.check.Check;

/**
 * A {@code ParseInfo} specifies how to parse a date string. It minimally consists of a date/time
 * pattern string or a {@link DateTimeFormatter} instance. A {@link FuzzyDateParser} is instantiated
 * with a list of {@code ParseInfo} instances. When parsing a date string it iterates over these
 * {@code ParseInfo} instances in order the produce a {@link FuzzyDate}.
 *
 * <p>The following can be specified when parsing a date string:
 *
 * <ol>
 *   <li>Either a date/time pattern or a {@link DateTimeFormatter} instance. <i>Required.</i>
 *   <li>One or more {@code TemporalQuery} objects that effectively specify into which type of
 *       date/time object you want the date string to be parsed. <i>Optional.</i> Ordinarily you
 *       would specify them in descending order of granularity, but that is not required.
 *   <li>A {@link UnaryOperator} that transforms the date string <b>before</b> it is passed on to
 *       the {@code DateTimeFormatter}. <i>Optional.</i> If the operator returns null, the date
 *       string will be treated as unparsable.
 * </ol>
 */
public final class ParseInfo {

  /**
   * A ready-made {@code ParseInfo} that parses date strings using the {@link
   * DateTimeFormatter#ISO_INSTANT ISO_INSTANT} formatter into an instance of {@link Instant}.
   */
  public static final ParseInfo ISO_INSTANT =
      new ParseInfo(DateTimeFormatter.ISO_INSTANT, Instant::from);
  /**
   * A ready-made {@code ParseInfo} that parses date strings using the {@link
   * DateTimeFormatter#ISO_INSTANT ISO_LOCAL_DATE} formatter into an instance of {@link LocalDate}.
   */
  public static final ParseInfo ISO_LOCAL_DATE =
      new ParseInfo(DateTimeFormatter.ISO_LOCAL_DATE, LocalDate::from);
  /**
   * A ready-made {@code ParseInfo} that parses date strings using the {@link
   * DateTimeFormatter#ISO_INSTANT ISO_LOCAL_DATE_TIME} formatter into an instance of {@link
   * LocalDateTime}.
   */
  public static final ParseInfo ISO_LOCAL_DATE_TIME =
      new ParseInfo(DateTimeFormatter.ISO_LOCAL_DATE_TIME, LocalDateTime::from);

  final UnaryOperator<String> filter;
  final DateTimeFormatter formatter;
  final TemporalQuery<?>[] parseInto;

  private final String pattern;

  /**
   * Creates a {@code ParseInfo} using the provided date/time pattern and the provided array of
   * {@code TemporalQuery} objects. The array may be null or zero-length.
   *
   * @param pattern
   * @param parseInto
   */
  public ParseInfo(String pattern, TemporalQuery<?>... parseInto) {
    this(null, pattern, parseInto);
  }

  /**
   * Creates a {@code ParseInfo} using the provided {@link DateTimeFormatter} and the provided array
   * of {@code TemporalQuery} objects. The array may be null or zero-length.
   *
   * @param formatter
   * @param parseInto
   */
  public ParseInfo(DateTimeFormatter formatter, TemporalQuery<?>... parseInto) {
    this(null, formatter, parseInto);
  }

  public ParseInfo(UnaryOperator<String> filter, String pattern, TemporalQuery<?>... parseInto) {
    this.filter = filter;
    this.pattern = Check.notNull(pattern, "pattern");
    this.formatter = DateTimeFormatter.ofPattern(pattern);
    this.parseInto = Check.notNull(parseInto, "parseInto");
  }

  public ParseInfo(
      UnaryOperator<String> filter, DateTimeFormatter formatter, TemporalQuery<?>... parseInto) {
    this.filter = filter;
    this.pattern = null;
    this.formatter = Check.notNull(formatter, "formatter");
    this.parseInto = Check.notNull(parseInto, "parseInto");
  }

  /**
   * Returns the filter used to transform the input string, or null if no filter is used by this
   * {@code ParseInfo}. The filter's {@link UnaryOperator#apply(Object) apply} method is explicitly
   * allowed to return null, in which case the input string will be treated as not parsable by this
   * {@code ParseInfo}.
   *
   * @return
   */
  public UnaryOperator<String> getFilter() {
    return filter;
  }

  /**
   * Returns the {@link DateTimeFormatter} used to parse the date string.
   *
   * @return
   */
  public DateTimeFormatter getFormatter() {
    return formatter;
  }

  /**
   * Returns the type of date/time object(s) the date string is allowed to be parsed into.
   *
   * @return
   */
  public TemporalQuery<?>[] getParseInto() {
    return parseInto;
  }

  /**
   * Returns the date/time pattern used to parse the date string or null if the {@code ParseInfo}
   * was instantiated with a ready-made {@link DateTimeFormatter} object.
   *
   * @return
   */
  public String getPattern() {
    return pattern;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(parseInto);
    result = prime * result + Objects.hash(filter, formatter, pattern);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ParseInfo other = (ParseInfo) obj;
    return Objects.equals(filter, other.filter)
        && Objects.equals(formatter, other.formatter)
        && Arrays.equals(parseInto, other.parseInto)
        && Objects.equals(pattern, other.pattern);
  }
}
