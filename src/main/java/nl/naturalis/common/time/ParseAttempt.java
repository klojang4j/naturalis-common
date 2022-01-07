package nl.naturalis.common.time;

import nl.naturalis.common.check.Check;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.List;

import static java.time.format.DateTimeFormatter.*;
import static java.time.format.ResolverStyle.LENIENT;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;

/**
 * A {@code ParseAttempt} specifies how to parse a date string. The following can be specified when
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
 *   <li>One or more {@link java.util.Locale locales}. <i>Optional.</i> Note though that if you do
 *       not specify a {@code Locale} you make yourself dependent on the host operating system's
 *       locale settings.
 *   <li>A {@link DateStringFilter} that validates and/or transforms the date string before it is
 *       parsed by {@code DateTimeFormatter}. <i>Optional.</i> If the operator returns null, the
 *       date string will be treated as not-parsable using the current {@code ParseAttempt} and the
 *       parse attempt will be cut short. The {@code DateStringFilter} is also allowed to throw a
 *       {@link FuzzyDateException}, in which case the date string will be treated as definitely
 *       not-parsable by this or any subsequent {@code ParseAttempt}.
 * </ol>
 *
 * In addition, you can also optionally specify a "tag" for the {@code ParseAttempt}. When deploying
 * a large amount of {@code ParseAttempt} instances to get your date strings parsed, this might make
 * it more easy to recognize the {@code ParseAttempt} that succeeded in parsing the date string. See
 * {@link FuzzyDate#getParseAttempt()}.
 */
public final class ParseAttempt {

  /**
   * A ready-made {@code ParseAttempt} instance that will attempt to parse date strings into an
   * {@link Instant} using the predefined {@link DateTimeFormatter#ISO_INSTANT} formatter.
   */
  public static final ParseAttempt TRY_ISO_INSTANT =
      configure(ISO_INSTANT).withTag("ISO_INSTANT").parseInto(List.of(Instant::from)).freeze();

  /**
   * A ready-made {@code ParseAttempt} instance that will attempt to parse date strings into a
   * {@link LocalDate} using the predefined {@link DateTimeFormatter#ISO_LOCAL_DATE} formatter.
   */
  public static final ParseAttempt TRY_ISO_LOCAL_DATE =
      configure(ISO_LOCAL_DATE)
          .withTag("ISO_LOCAL_DATE")
          .parseInto(List.of(LocalDate::from))
          .freeze();

  /**
   * A ready-made {@code ParseAttempt} instance that will attempt to parse date strings into a
   * {@link LocalDateTime} using the predefined {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}
   * formatter.
   */
  public static final ParseAttempt TRY_ISO_LOCAL_DATE_TIME =
      configure(ISO_LOCAL_DATE_TIME)
          .withTag("ISO_LOCAL_DATE_TIME")
          .parseInto(List.of(LocalDateTime::from))
          .freeze();
  /**
   * A ready-made {@code ParseAttempt} instance that will attempt to parse date strings into an
   * {@link OffsetDateTime} using the predefined {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}
   * formatter.
   */
  public static final ParseAttempt TRY_ISO_OFFSET_DATE_TIME =
      configure(ISO_OFFSET_DATE_TIME)
          .withTag("ISO_OFFSET_DATE_TIME")
          .parseInto(List.of(OffsetDateTime::from))
          .freeze();

  /**
   * Returns a {@code Builder} instance that give you fine-grained control over the configuration of
   * the {@code ParseAttempt}.
   *
   * @param pattern The date/time pattern
   * @return A {@code Builder} instance that give you fine-grained control over the configuration of
   *     the {@code ParseAttempt}
   */
  public static AttemptBuilder configure(String pattern) {
    return new AttemptBuilder(pattern);
  }

  /**
   * Returns a {@code Builder} instance that give you fine-grained control over the configuration of
   * the {@code ParseAttempt}.
   *
   * @param formatter A predefined {@code DateTimeFormatter} instance
   * @return A {@code Builder} instance that give you fine-grained control over the configuration of
   *     the {@code ParseAttempt}
   */
  public static AttemptBuilder configure(DateTimeFormatter formatter) {
    return new AttemptBuilder(formatter);
  }

  final String tag;
  final String pattern;
  final DateTimeFormatter formatter;
  final List<TemporalQuery<TemporalAccessor>> parseInto;
  final DateStringFilter filter;

  /**
   * Creates a new, simple {@code ParseAttempt} using only the specified date/time pattern to parse
   * date strings.
   *
   * @param pattern The date/time pattern
   */
  public ParseAttempt(String pattern) {
    this(pattern, List.of());
  }

  /**
   * Creates a new {@code ParseAttempt} instance.
   *
   * @param formatter The {@code DateTimeFormatter} used to parse the date string
   */
  public ParseAttempt(DateTimeFormatter formatter) {
    this(formatter, List.of());
  }

  /**
   * Creates a new {@code ParseAttempt} instance.
   *
   * @param pattern A date/time pattern
   * @param parseInto The {@link TemporalQuery} object(s) that specify the target date/time type.
   *     For example: {@code LocalDate::from}. Must not be null, but may be empty.
   */
  public ParseAttempt(String pattern, List<TemporalQuery<TemporalAccessor>> parseInto) {
    this(pattern, parseInto, null);
  }

  /**
   * Creates a new {@code ParseAttempt} instance.
   *
   * @param formatter The {@code DateTimeFormatter} used to parse the date string
   * @param parseInto The {@link TemporalQuery} object(s) that specify the target date/time type.
   *     For example: {@code LocalDate::from}. Must not be null, but may be empty.
   */
  public ParseAttempt(
      DateTimeFormatter formatter, List<TemporalQuery<TemporalAccessor>> parseInto) {
    this(formatter, parseInto, null);
  }

  /**
   * Creates a new {@code ParseAttempt} instance.
   *
   * @param pattern A date/time pattern
   * @param parseInto The {@link TemporalQuery} object(s) that specify the target date/time type.
   *     For example: {@code LocalDate::from}. Must not be null, but may be empty.
   * @param filter An optional filter to be applied to the date string before it is parsed. May be
   *     null.
   */
  public ParseAttempt(
      String pattern, List<TemporalQuery<TemporalAccessor>> parseInto, DateStringFilter filter) {
    this.pattern = Check.notNull(pattern, "pattern").ok();
    this.formatter = DateTimeFormatter.ofPattern(pattern).withResolverStyle(LENIENT);
    this.parseInto = Check.that(parseInto, "parseInto").is(deepNotNull()).ok(List::copyOf);
    this.filter = filter;
    this.tag = pattern + " (" + formatter.getLocale() + ")";
  }

  /**
   * Creates a new {@code ParseAttempt} instance.
   *
   * @param formatter The {@code DateTimeFormatter} used to parse the date string
   * @param parseInto The {@link TemporalQuery} object(s) that specify the target date/time type.
   *     For example: {@code LocalDate::from}. Must not be null, but may be empty.
   * @param filter An optional filter to be applied to the date string before it is parsed. May be
   *     null.
   */
  public ParseAttempt(
      DateTimeFormatter formatter,
      List<TemporalQuery<TemporalAccessor>> parseInto,
      DateStringFilter filter) {
    this.pattern = null;
    this.formatter = Check.notNull(formatter, "formatter").ok();
    this.parseInto = Check.that(parseInto, "parseInto").is(deepNotNull()).ok(List::copyOf);
    this.filter = filter;
    this.tag = null;
  }

  ParseAttempt(
      String tag,
      String pattern,
      DateTimeFormatter formatter,
      List<TemporalQuery<TemporalAccessor>> parseInto,
      DateStringFilter filter) {
    this.tag = tag;
    this.pattern = pattern;
    this.formatter = formatter;
    this.parseInto = parseInto;
    this.filter = filter;
  }

  /**
   * Returns a user-defined string identifying this {@code ParseAttempt}.
   *
   * @return A user-defined string identifying this {@code ParseAttempt}
   */
  public String getTag() {
    return tag;
  }

  /**
   * Returns the filter used to validate and/or transform the input string, or null if no filter is
   * used by this {@code ParseAttempt}. The filter's {@link
   * DateStringFilter#validateOrTransform(String) validateOrTransform} method is allowed to return
   * null, in which case the input string will be treated as not parsable by this {@code
   * ParseAttempt} and the {@link FuzzyDateParser} will skip to the next {@code ParseAttempt}, if
   * any.
   *
   * @return The filter used to validate and/or transform the input string
   */
  public DateStringFilter getFilter() {
    return filter;
  }

  /**
   * Returns the {@link DateTimeFormatter} used to parse the date string.
   *
   * @return The {@code DateTimeFormatter} used to parse the date string.
   */
  public DateTimeFormatter getFormatter() {
    return formatter;
  }

  /**
   * Returns a list of date/time type alternatives that the date string is to be parsed into. See
   * {@link DateTimeFormatter#parseBest(CharSequence, TemporalQuery[])}.
   *
   * @return A list of date/time type alternatives that the date string is to be parsed into
   */
  public List<TemporalQuery<TemporalAccessor>> getParseInto() {
    return parseInto;
  }

  /**
   * Returns the date/time pattern used to parse the date string. or null if the {@code
   * ParseAttempt} was instantiated with a ready-made {@link DateTimeFormatter} object.
   *
   * @return The date/time pattern used to parse the date string
   */
  public String getPattern() {
    return pattern;
  }

  @Override
  public String toString() {
    return tag;
  }
}
