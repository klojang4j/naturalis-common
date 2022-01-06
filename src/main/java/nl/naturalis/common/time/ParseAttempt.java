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

import static java.time.format.DateTimeFormatter.*;
import static nl.naturalis.common.check.CommonChecks.neverNull;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.time.FuzzyDateException.ERR_CASE_SENSITIVITY_FIXED;
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
 * In addition, you can also optionally specify a "tag" for the {@code ParseAttempt}. When employing
 * a large army of {@code ParseAttempt} instances to get your date strings parsed, this will make it
 * more easy to recognize the {@code ParseAttempt} that succeeded in parsing the date string. See
 * {@link FuzzyDate#getParseAttempt()}. If no tag is provided through the constructor or the {@link
 * Builder}, it is auto-generated. The auto-generated tag will probably serve its purpose if the
 * {@code ParseAttempt} is based on a date/time pattern string, but most likely not if the {@code
 * ParseAttempt} is based on a {@code DateTimeFormatter}. See {@link FuzzyDate#getParseAttempt()}.
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
  public static Builder configure(String pattern) {
    return new Builder(pattern);
  }

  /**
   * Returns a {@code Builder} instance that give you fine-grained control over the configuration of
   * the {@code ParseAttempt}.
   *
   * @param formatter A predefined {@code DateTimeFormatter} instance
   * @return A {@code Builder} instance that give you fine-grained control over the configuration of
   *     the {@code ParseAttempt}
   */
  public static Builder configure(DateTimeFormatter formatter) {
    return new Builder(formatter);
  }

  /** A builder class for {@code ParseAttempt} instances. */
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

    /**
     * The {@code Locale} to use when parsing date strings. Important when parsing
     * language-sensitive date formats (e.g. {@code yyyy-MMMM-dd}).
     *
     * @param locale The {@code Locale}
     * @return This {@code Builder}
     */
    public Builder withLocale(Locale locale) {
      this.locale = Check.notNull(locale).ok();
      return this;
    }

    /**
     * Enables case-sensitive parsing. This method must not be called when building a {@code
     * ParseAttempt} from a predefined {@link DateTimeFormatter} instance.
     *
     * @return This {@code Builder}
     * @throws FuzzyDateException If the {@code Builder} was created with a {@code
     *     DateTimeFormatter} instance
     */
    public Builder caseSensitive() throws FuzzyDateException {
      return caseSensitive(true);
    }

    /**
     * Enables or disables case-sensitive parsing. This method must not be called when building a
     * {@code ParseAttempt} from a predefined {@link DateTimeFormatter} instance.
     *
     * @param cs Whether to parse in a case-senitive manner
     * @return This {@code Builder}
     * @throws FuzzyDateException If the {@code Builder} was created with a {@code
     *     DateTimeFormatter} instance
     */
    public Builder caseSensitive(boolean cs) throws FuzzyDateException {
      checkThat(pattern).is(notNull(), ERR_CASE_SENSITIVITY_FIXED);
      this.caseSensitive = cs;
      return this;
    }

    /**
     * Sets the {@link ResolverStyle} of the parser. By default parsing is done in a {@link
     * ResolverStyle#LENIENT LENIET} manner.
     *
     * @param resolverStyle The {@code ResolverStyle}
     * @return This {@code Builder}
     */
    public Builder withResolverStyle(ResolverStyle resolverStyle) {
      this.resolverStyle = Check.notNull(resolverStyle).ok();
      return this;
    }

    /***
     * Species the desired target date/time type. This is usually done by means of a method reference like
     * {@link LocalDate#from(TemporalAccessor) LocalDate::from}.
     *
     * @param parseInto The desired target date/time type
     * @return This {@code Builder}
     */
    public Builder parseInto(TemporalQuery<TemporalAccessor> parseInto) {
      this.parseInto = Check.notNull(parseInto).ok(List::of);
      return this;
    }

    /**
     * Specifies a list of target date/time type alternatives. These will be passed on to {@link
     * DateTimeFormatter#parseBest(CharSequence, TemporalQuery[])}.
     *
     * @param parseInto A list of target date/time type alternatives
     * @return This {@code Builder}
     */
    public Builder parseInto(List<TemporalQuery<TemporalAccessor>> parseInto) {
      this.parseInto = Check.that(parseInto).is(neverNull()).ok();
      return this;
    }

    /**
     * Specifies a {@link DateStringFilter} that will validate and/or transform in the input string
     * before it is parsed.
     *
     * @param filter A {@link DateStringFilter} that will validate and/or transform in the input
     *     string
     * @return This {@code Builder}
     */
    public Builder withFilter(DateStringFilter filter) {
      this.filter = filter;
      return this;
    }

    /**
     * Returns a fully-configured {@link ParseAttempt}.
     *
     * @return A fully-configured {@link ParseAttempt}.
     */
    public ParseAttempt freeze() {
      return pattern == null ? buildFromFormatter() : buildFromPattern();
    }

    /**
     * Instantiates the {@link ParseAttempt} and adds it to the specified {@code List}, which can
     * then be used to {@link FuzzyDateParser#FuzzyDateParser(List) construct} a {@link
     * FuzzyDateParser}.
     *
     * @param parseAttempts The {@code List} to which to add the {@code ParseAttempt}.
     */
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
    this.pattern = null;
    this.tag = "" + formatter.getLocale() + ")";
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
   * used by this {@code ParseAttempt}. The filter's {@link
   * DateStringFilter#validateOrTransform(String) validateOrTransform} method is allowed to return
   * null, in which case the input string will be treated as not parsable by this {@code
   * ParseAttempt} and the {@link FuzzyDateParser} will skip to the next {@code ParseAttempt}, if
   * any.
   *
   * @return
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

  private static String getDefaultTag(String pattern) {
    return pattern + " (" + Locale.getDefault() + ")";
  }
}
