package nl.naturalis.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import nl.naturalis.common.check.Check;

/**
 * A {@code ParseInfo} specifies how to parse a date string. The following can be specified when
 * parsing a date string:
 *
 * <p>
 *
 * <ol>
 *   <li>Either a date/time pattern or a {@link DateTimeFormatter} instance. <i>Required.</i>
 *   <li>One or more {@code TemporalQuery} objects that effectively specify into which type of
 *       date/time object you want the date string to be parsed. For example: {@code
 *       LocalDate::from}. <i>Optional.</i> Ordinarily you would specify them in descending order of
 *       granularity, but that is not required. Unless you are dealing with an exotic date/time
 *       pattern it is recommended that you provide at least one {@code TemporalQuery} object.
 *   <li>A {@link UnaryOperator} that transforms the date string <b>before</b> it is passed on to
 *       the {@code DateTimeFormatter}. <i>Optional.</i> If the operator returns null, the date
 *       string will be treated as unparsable.
 * </ol>
 */
public final class ParseInfo {

  /**
   * A ready-made {@code ParseInfo} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_INSTANT ISO_INSTANT} formatter into an {@link Instant} object.
   */
  public static final ParseInfo ISO_INSTANT =
      new ParseInfo(DateTimeFormatter.ISO_INSTANT, List.of(Instant::from));
  /**
   * A ready-made {@code ParseInfo} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_LOCAL_DATE ISO_LOCAL_DATE} formatter into a {@link LocalDate} object.
   */
  public static final ParseInfo ISO_LOCAL_DATE =
      new ParseInfo(DateTimeFormatter.ISO_LOCAL_DATE, List.of(LocalDate::from));

  /**
   * A ready-made {@code ParseInfo} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_LOCAL_DATE_TIME ISO_LOCAL_DATE_TIME} formatter into a {@link
   * LocalDateTime} object.
   */
  public static final ParseInfo ISO_LOCAL_DATE_TIME =
      new ParseInfo(DateTimeFormatter.ISO_LOCAL_DATE_TIME, List.of(LocalDateTime::from));

  /**
   * A ready-made {@code ParseInfo} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_OFFSET_DATE_TIME ISO_OFFSET_DATE_TIME} formatter into aN {@link
   * OffsetDateTime} object.
   */
  public static final ParseInfo ISO_OFFSET_DATE_TIME =
      new ParseInfo(DateTimeFormatter.ISO_OFFSET_DATE_TIME, List.of(OffsetDateTime::from));

  final UnaryOperator<String> filter;
  final DateTimeFormatter formatter;
  final List<TemporalQuery<TemporalAccessor>> parseInto;

  private final String pattern;

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param pattern The date/time pattern according to which to parse the date strings
   */
  public ParseInfo(String pattern) {
    this(pattern, Collections.emptyList());
  }

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param formatter The {@link DateTimeFormatter} to parse the date strings with
   */
  public ParseInfo(DateTimeFormatter formatter) {
    this(formatter, Collections.emptyList());
  }

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param pattern The date/time pattern according to which to parse the date strings
   * @param parseInto The {@link TemporalQuery} object(s) that specify the type of the date/time
   *     object (e.g. {@code LocalDate::from}). Must not be null, but may be empty.
   */
  public ParseInfo(String pattern, List<TemporalQuery<TemporalAccessor>> parseInto) {
    this(pattern, parseInto, null);
  }

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param formatter The {@link DateTimeFormatter} to parse the date strings with
   * @param parseInto The {@link TemporalQuery} object(s) that specify the type of the date/time
   *     object (e.g. {@code LocalDate::from}). Must not be null, but may be empty.
   */
  public ParseInfo(DateTimeFormatter formatter, List<TemporalQuery<TemporalAccessor>> parseInto) {
    this(formatter, parseInto, null);
  }

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param pattern The date/time pattern according to which to parse the date strings
   * @param parseInto The {@link TemporalQuery} object(s) that specify the type of the date/time
   *     object (e.g. {@code LocalDate::from}). Must not be null, but may be empty.
   * @param filter An optional filter to be applied to the date string before it is parsed. May be
   *     null.
   */
  public ParseInfo(
      String pattern,
      List<TemporalQuery<TemporalAccessor>> parseInto,
      UnaryOperator<String> filter) {
    this.pattern = Check.notNull(pattern, "pattern").ok();
    this.formatter = DateTimeFormatter.ofPattern(pattern);
    this.parseInto = Check.notNull(parseInto, "parseInto").ok(Collections::unmodifiableList);
    this.filter = filter;
  }

  /**
   * Creates a new {@code ParseInfo} instance.
   *
   * @param formatter The {@link DateTimeFormatter} to parse the date strings with
   * @param parseInto The {@link TemporalQuery} object(s) that specify the type of the date/time
   *     object (e.g. {@code LocalDate::from}). Must not be null, but may be empty.
   * @param filter An optional filter to be applied to the date string before it is parsed. May be
   *     null.
   */
  public ParseInfo(
      DateTimeFormatter formatter,
      List<TemporalQuery<TemporalAccessor>> parseInto,
      UnaryOperator<String> filter) {
    this.formatter = Check.notNull(formatter, "formatter").ok();
    this.parseInto = Check.notNull(parseInto, "parseInto").ok(Collections::unmodifiableList);
    this.filter = filter;
    this.pattern = null;
  }

  /**
   * Returns the filter used to transform the input string, or null if no filter is used by this
   * {@code ParseInfo}. The filter's {@link UnaryOperator#apply(Object) apply} method is allowed to
   * return null, in which case the input string will be treated as not parsable by this {@code
   * ParseInfo}.
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
  public List<TemporalQuery<TemporalAccessor>> getParseInto() {
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
}
