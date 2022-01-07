package nl.naturalis.common.time;

import nl.naturalis.common.check.Check;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.List;
import java.util.Locale;

import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.time.FuzzyDateException.ERR_CASE_SENSITIVITY_FIXED;
import static nl.naturalis.common.time.Utils.checkThat;

/** A builder class for {@code ParseAttempt} instances. */
public class AttemptBuilder {

  private final String pattern;
  private final DateTimeFormatter formatter;

  private String tag;
  private Locale locale;
  private Boolean caseSensitive;
  private ResolverStyle resolverStyle;
  private List<TemporalQuery<TemporalAccessor>> parseInto;
  private DateStringFilter filter;

  AttemptBuilder(String pattern) {
    this.pattern = pattern;
    this.formatter = null;
  }

  AttemptBuilder(DateTimeFormatter formatter) {
    this.pattern = null;
    this.formatter = formatter;
  }

  /**
   * @param tag An arbitrary string enabling you to recognize this {@code ParseAttempt} when
   *     returned from {@link FuzzyDate#getParseAttempt()}.
   * @return This {@code Builder}
   */
  public AttemptBuilder withTag(String tag) {
    this.tag = tag;
    return this;
  }

  /**
   * The {@code Locale} to use when parsing date strings. Important when parsing language-sensitive
   * date formats (e.g. {@code yyyy-MMMM-dd}).
   *
   * @param locale The {@code Locale}
   * @return This {@code Builder}
   */
  public AttemptBuilder withLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Enables case-sensitive parsing. This method must not be called when building a {@code
   * ParseAttempt} from a predefined {@link DateTimeFormatter} instance.
   *
   * @return This {@code Builder}
   * @throws FuzzyDateException If the {@code Builder} was created with a {@code DateTimeFormatter}
   *     instance
   */
  public AttemptBuilder caseSensitive() throws FuzzyDateException {
    return caseSensitive(true);
  }

  /**
   * Enables or disables case-sensitive parsing. This method must not be called when building a
   * {@code ParseAttempt} from a predefined {@link DateTimeFormatter} instance.
   *
   * @param cs Whether to parse in a case-sensitive manner
   * @return This {@code Builder}
   * @throws FuzzyDateException If the {@code Builder} was created with a {@code DateTimeFormatter}
   *     instance
   */
  public AttemptBuilder caseSensitive(boolean cs) throws FuzzyDateException {
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
  public AttemptBuilder withResolverStyle(ResolverStyle resolverStyle) {
    this.resolverStyle = resolverStyle;
    return this;
  }

  /***
   * Species the desired target date/time type. This is usually done by means of a method reference like
   * {@link LocalDate#from(TemporalAccessor) LocalDate::from}.
   *
   * @param parseInto The desired target date/time type
   * @return This {@code Builder}
   */
  public AttemptBuilder parseInto(TemporalQuery<TemporalAccessor> parseInto) {
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
  public AttemptBuilder parseInto(List<TemporalQuery<TemporalAccessor>> parseInto) {
    this.parseInto = parseInto;
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
  public AttemptBuilder withFilter(DateStringFilter filter) {
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
   * Instantiates the {@link ParseAttempt} and adds it to the specified {@code List}, which can then
   * be used to {@link FuzzyDateParser#FuzzyDateParser(List) construct} a {@link FuzzyDateParser}.
   *
   * @param parseAttempts The {@code List} to which to add the {@code ParseAttempt}.
   */
  public void addTo(List<ParseAttempt> parseAttempts) {
    parseAttempts.add(freeze());
  }

  private ParseAttempt buildFromPattern() {
    DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder().appendPattern(pattern);
    if (Boolean.TRUE.equals(caseSensitive)) {
      dtfb.parseCaseInsensitive();
    }
    if (resolverStyle == null || resolverStyle == ResolverStyle.LENIENT) {
      dtfb.parseLenient();
    } else if (resolverStyle == ResolverStyle.STRICT) {
      dtfb.parseStrict();
    }
    if (parseInto == null) {
      parseInto = List.of();
    }
    DateTimeFormatter formatter = locale == null ? dtfb.toFormatter() : dtfb.toFormatter(locale);
    return new ParseAttempt(tag, pattern, formatter, parseInto, filter);
  }

  private ParseAttempt buildFromFormatter() {
    DateTimeFormatter formatter = this.formatter;
    if (resolverStyle != null) {
      formatter = formatter.withResolverStyle(resolverStyle);
    }
    if (locale != null) {
      formatter = formatter.withLocale(locale);
    }
    if (parseInto == null) {
      parseInto = List.of();
    }
    return new ParseAttempt(tag, pattern, formatter, parseInto, filter);
  }
}
