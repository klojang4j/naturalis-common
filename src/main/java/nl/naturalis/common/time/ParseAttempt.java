package nl.naturalis.common.time;

import nl.naturalis.common.check.Check;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

import static java.time.format.DateTimeFormatter.*;
import static nl.naturalis.common.check.CommonChecks.neverNull;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.time.FuzzyDateException.ERR_CASE_SENSITIVTY_FIXED;
import static nl.naturalis.common.time.Utils.checkThat;

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
 *   <li>One or more {@link java.util.Locale locale}. <i>Optional.</i> Note though that if you do
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
 * In addition, you can also "tag" a {@code ParseAttempt} so that you can easily recognize the parse
 * attempt that succeeded in parsing the date string, and hence was responsible for the {@link
 * FuzzyDate} created from it.
 */
public final class ParseAttempt {

  /**
   * A ready-made {@code ParseAttempt} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_INSTANT ISO_INSTANT} formatter into an {@link Instant} object.
   */
  public static final ParseAttempt TRY_ISO_INSTANT =
      configure(ISO_INSTANT).withTag("ISO_INSTANT").parseInto(List.of(Instant::from)).freeze();
  /**
   * A ready-made {@code ParseAttempt} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_LOCAL_DATE ISO_LOCAL_DATE} formatter into a {@link LocalDate} object.
   */
  public static final ParseAttempt TRY_ISO_LOCAL_DATE =
      configure(ISO_LOCAL_DATE)
          .withTag("ISO_LOCAL_DATE")
          .parseInto(List.of(LocalDate::from))
          .freeze();

  /**
   * A ready-made {@code ParseAttempt} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_LOCAL_DATE_TIME ISO_LOCAL_DATE_TIME} formatter into a {@link
   * LocalDateTime} object.
   */
  public static final ParseAttempt TRY_ISO_LOCAL_DATE_TIME =
      configure(ISO_LOCAL_DATE_TIME)
          .withTag("ISO_LOCAL_DATE_TIME")
          .parseInto(List.of(LocalDateTime::from))
          .freeze();
  /**
   * A ready-made {@code ParseAttempt} instance that parses date strings using the {@link
   * DateTimeFormatter#ISO_OFFSET_DATE_TIME ISO_OFFSET_DATE_TIME} formatter into an {@link
   * OffsetDateTime} object.
   */
  public static final ParseAttempt TRY_ISO_OFFSET_DATE_TIME =
      configure(ISO_OFFSET_DATE_TIME)
          .withTag("ISO_OFFSET_DATE_TIME")
          .parseInto(List.of(OffsetDateTime::from))
          .freeze();

  public static Builder configure(String pattern) {
    return new Builder(pattern);
  }

  public static Builder configure(DateTimeFormatter formatter) {
    return new Builder(formatter);
  }

  public static class Builder {
    private final String pattern;
    private final DateTimeFormatter formatter;

    private String tag;
    private Locale locale = Locale.getDefault();
    private boolean caseSensitive;
    private ResolverStyle resolverStyle = ResolverStyle.LENIENT;
    private List<TemporalQuery<TemporalAccessor>> parseInto = List.of();
    private DateStringFilter filter;

    private Builder(String pattern) {
      this.pattern = pattern;
      this.formatter = null;
    }

    private Builder(DateTimeFormatter formatter) {
      this.pattern = null;
      this.formatter = formatter;
    }

    /**
     * @param tag An arbitrary string enabling you to recognize this {@code ParseAttempt} when
     *     returned from {@link FuzzyDate#getParseAttempt()}.
     * @return This {@code Builder}
     */
    public Builder withTag(String tag) {
      this.tag = Check.notNull(tag).ok();
      return this;
    }

    public Builder withLocale(Locale locale) throws FuzzyDateException {
      this.locale = Check.notNull(locale).ok();
      return this;
    }

    public Builder caseSensitive() throws FuzzyDateException {
      return caseSensitive(true);
    }

    public Builder caseSensitive(boolean cs) throws FuzzyDateException {
      checkThat(pattern).is(notNull(), ERR_CASE_SENSITIVTY_FIXED);
      this.caseSensitive = cs;
      return this;
    }

    public Builder withResolverStyle(ResolverStyle resolverStyle) {
      this.resolverStyle = Check.notNull(resolverStyle).ok();
      return this;
    }

    public Builder parseInto(TemporalQuery<TemporalAccessor> parseInto) {
      this.parseInto = Check.notNull(parseInto).ok(List::of);
      return this;
    }

    public Builder parseInto(List<TemporalQuery<TemporalAccessor>> parseInto) {
      this.parseInto = Check.that(parseInto).is(neverNull()).ok(List::copyOf);
      return this;
    }

    public Builder withFilter(DateStringFilter filter) {
      this.filter = Check.notNull(filter).ok();
      return this;
    }

    public ParseAttempt freeze() {
      return pattern == null ? buildFromFormatter() : buildFromPattern();
    }

    public void addTo(List<ParseAttempt> parseAttempts) {
      parseAttempts.add(freeze());
    }

    private ParseAttempt buildFromPattern() {
      DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder().appendPattern(pattern);
      if (caseSensitive) {
        dtfb.parseCaseInsensitive();
      }
      return buildFromFormatter(dtfb.toFormatter());
    }

    private ParseAttempt buildFromFormatter() {
      return buildFromFormatter(formatter);
    }

    private ParseAttempt buildFromFormatter(DateTimeFormatter formatter) {
      formatter = formatter.withLocale(locale).withResolverStyle(resolverStyle);
      return new ParseAttempt(tag, pattern, formatter, parseInto, filter);
    }
  }

  final String tag;
  final String pattern;
  final DateTimeFormatter formatter;
  final List<TemporalQuery<TemporalAccessor>> parseInto;
  final DateStringFilter filter;

  public ParseAttempt(String pattern) {
    this(pattern, List.of());
  }

  public ParseAttempt(DateTimeFormatter formatter) {
    this(formatter, List.of());
  }

  public ParseAttempt(String pattern, List<TemporalQuery<TemporalAccessor>> parseInto) {
    this(pattern, parseInto, null);
  }

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
    this.formatter = DateTimeFormatter.ofPattern(pattern);
    this.parseInto = Check.notNull(parseInto, "parseInto").ok(List::copyOf);
    this.filter = filter;
    this.tag = getDefaultTag(pattern);
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
    this.formatter = Check.notNull(formatter, "formatter").ok();
    this.parseInto = Check.notNull(parseInto, "parseInto").ok(List::copyOf);
    this.filter = filter;
    this.pattern = formatter.toFormat().toString();
    this.tag = getDefaultTag(pattern);
  }

  private ParseAttempt(
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
   * used by this {@code ParseAttempt}. The filter's {@link UnaryOperator#apply(Object) apply}
   * method is allowed to return null, in which case the input string will be treated as not
   * parsable by the {@link FuzzyDateParser}.
   *
   * @return
   */
  public DateStringFilter getFilter() {
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
   * Returns the date/time pattern used to parse the date string or null if the {@code ParseAttempt}
   * was instantiated with a ready-made {@link DateTimeFormatter} object.
   *
   * @return
   */
  public String getPattern() {
    return pattern;
  }

  @Override
  public String toString() {
    return tag;
  }

  private static String getDefaultTag(String pattern) {
    return pattern + " (" + Locale.getDefault() + ")";
  }
}
