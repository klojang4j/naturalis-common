package nl.naturalis.common.time;

import java.io.InputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static java.time.temporal.ChronoField.YEAR;
import static nl.naturalis.common.check.CommonChecks.noneNull;

/**
 * Parses date strings into {@link FuzzyDate} instances. The minimum requirement for a valid date
 * string is that a year can be extracted from it.
 *
 * <p>Date strings are parsed by iterating over a list of {@link ParseInfo} instances, passed in
 * through one of the constructors . A {@link ParseInfo} instance specifies how to parse the input
 * string. As soon as a {@code ParseSpec} is capable of parsing the date string into a {@code
 * java.time} object, the iteration stops. Therefore the more granular {@link ParseInfo} instances
 * should come first in the list.
 *
 * <p>A {@code FuzzyDateParser} can be instantiated with a list of hard-coded {@link ParseInfo}
 * instances or with a properties file defining the {@link ParseInfo} instances. See the description
 * of the {@link #DEFAULT} {@code FuzzyDateParser} for how to encode {@code ParseInfo} instances.
 *
 * <p>Note about performance: date parsing is relatively expensive, but the cost does not come from
 * the pattern-matching phase, but from the subsequent creation of a {@link TemporalAccessor}
 * object. Therefore the number of {@link ParseInfo} instances with which the parser is instantiated
 * does not greatly impact performance.
 */
public class FuzzyDateParser {

  /**
   * A FuzzyDateParser instance capable of parsing a wide range of date strings. Below is the
   * properties file encoding the {@link ParseInfo} instances with which the default parser is
   * instantiated:
   *
   * <pre>
   * # For each type of date string you anticipate in your source data, you must either specify either a
   * # pattern or the name of a public static final DateTimeFormatter field on the DateTimeFormatter class
   * # (e.g. DateTimeFormatter.ISO_DATE_TIME).
   * #
   * # For patterns you can optionally specify whether parsing should be done in a case sensitive manner
   * # (default false) and whether it should be done in STRICT, SMART or LENIENT manner (default LENIENT).
   * #
   * # You can also optionally specify (both for patterns and named formatters) a date string filter, i.e.
   * # the fully-qualified name of a class implementing DateStringFilter, which causes the parser to parse
   * # the filtered string rather than the original date string.
   *
   * # Global settings. Can be overridden using property names like "LocalDate.2.caseSensitive"
   * caseSensitive=false
   * resolverStyle=LENIENT
   * filter=
   *
   * Instant.0.name=ISO_INSTANT
   *
   * OffsetDateTime.0.pattern=uuuu-M-d'T'HH:mm[:ss]X
   * OffsetDateTime.1.pattern=uuuu/M/d'T'HH:mm[:ss]X
   * OffsetDateTime.2.pattern=uuuu-M-d HH:mm[:ss]X
   * OffsetDateTime.3.pattern=uuuu/M/d HH:mm[:ss]X
   * OffsetDateTime.4.name=ISO_DATE_TIME
   * OffsetDateTime.5.name=RFC_1123_DATE_TIME
   *
   * LocalDateTime.0.pattern=uuuu-M-d HH:mm[:ss]
   * LocalDateTime.1.pattern=uuuu/M/d HH:mm[:ss]
   * LocalDateTime.2.name=ISO_LOCAL_DATE_TIME
   * LocalDateTime.3.pattern=uuuuMMddHHmmss
   * LocalDateTime.4.pattern=yyyy年MM月dd日 hh时mm分ss秒
   *
   * LocalDate.0.pattern=d-MMM-uuuu
   * LocalDate.1.pattern=uuuu-M-d
   * LocalDate.2.pattern=uuuu/M/d
   * LocalDate.3.pattern=uuuu M d
   * LocalDate.4.pattern=uuuu-MMM-d
   * LocalDate.5.pattern=uuuu/MMM/d
   * LocalDate.6.pattern=uuuu MMM d
   * LocalDate.8.name=ISO_LOCAL_DATE
   * LocalDate.9.name=BASIC_ISO_DATE
   * LocalDate.10.pattern=uuuu M d
   * LocalDate.11.pattern=yyyy年mm月dd日
   *
   * YearMonth.0.pattern=MMM/uuuu
   * YearMonth.1.pattern=MMM-uuuu
   * YearMonth.2.pattern=uuuu-M
   * YearMonth.3.pattern=uuuu/M
   *
   * Year.0.pattern=uuuu
   * Year.1.pattern=uuuu
   * Year.1.filter=nl.naturalis.common.time.YearFilter
   * </pre>
   */
  public static final FuzzyDateParser DEFAULT = new FuzzyDateParser();

  private final List<ParseInfo> parseInfos;

  // Reserved for the default parser
  private FuzzyDateParser() {
    InputStream is = FuzzyDateParser.class.getResourceAsStream("FuzzyDate.properties.txt");
    ParseSpecProperties config = new ParseSpecProperties(is);
    try {
      this.parseInfos = config.createParseInfos();
    } catch (FuzzyDateException e) {
      // That's a bug because we created the properties file ourselves
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a {@code FuzzyDateParser} from the provided input stream, supposedly created from a
   * properties file encoding the {@code ParseInfo} instances. See the description of the {@link
   * #DEFAULT} {@code FuzzyDateParser} for how to encode {@code ParseInfo} instances.
   *
   * @param is The {@code InputStream}
   * @throws FuzzyDateException If there is an encoding error in the {@code InputStream}
   */
  public FuzzyDateParser(InputStream is) throws FuzzyDateException {
    Check.notNull(is);
    ParseSpecProperties config = new ParseSpecProperties(is);
    this.parseInfos = config.createParseInfos();
  }

  /**
   * Creates a {@code FuzzyDateParser} that uses the specified {@code ParseInfo} instances to parse
   * date strings.
   *
   * @param parseInfo0 The 1st {@code ParseInfo} instance
   * @param parseInfo1 The 2nd {@code ParseInfo} instance
   * @param moreParseInfos More {@code ParseInfo} instances
   */
  public FuzzyDateParser(ParseInfo parseInfo0, ParseInfo parseInfo1, ParseInfo... moreParseInfos) {
    Check.that(moreParseInfos, "moreParseInfos").is(noneNull());
    List<ParseInfo> parseSpecs = new ArrayList<>(moreParseInfos.length + 2);
    parseSpecs.add(Check.notNull(parseInfo0, "parseInfo0").ok());
    parseSpecs.add(Check.notNull(parseInfo1, "parseInfo1").ok());
    parseSpecs.addAll(Arrays.asList(moreParseInfos));
    this.parseInfos = parseSpecs;
  }

  /**
   * Creates a {@code FuzzyDateParser} that uses the specified {@code ParseInfo} instances to parse
   * date strings.
   *
   * @param parseInfos The {@code ParseInfo} instances used to parse date strings
   */
  public FuzzyDateParser(List<ParseInfo> parseInfos) {
    this.parseInfos = Check.that(parseInfos, "parseInfos").is(noneNull()).ok();
  }

  /**
   * Parses the provided date string using the {@code ParseSpec} instances passed in through the
   * constructors. The {@code ParseSpec} instances are tried out sequentially, so the most granular
   * ones should come first in the list.
   *
   * @param dateString The string to be parsed
   * @return A {@code FuzzyDate} instance representing th parse result.
   * @throws FuzzyDateException Thrown if parsing failed or if no year could be extracted from the
   *     date string.
   */
  public FuzzyDate parse(String dateString) throws FuzzyDateException {
    Check.notNull(dateString, "dateString");
    String input; // goes into the formatter
    TemporalAccessor ta; // comes out of the formatter
    for (ParseInfo info : parseInfos) {
      if (info.filter == null) {
        input = dateString;
      } else {
        input = info.filter.apply(dateString);
        if (input == null) {
          continue;
        }
      }
      try {
        if (info.parseInto == null || info.parseInto.length == 0) {
          ta = info.formatter.parse(input);
        } else {
          if (info.parseInto.length == 1) {
            ta = (TemporalAccessor) info.formatter.parse(input, info.parseInto[0]);
          } else {
            ta = info.formatter.parseBest(input, info.parseInto);
          }
        }
        Check.that(hasKnownYear(ta), () -> new FuzzyDateException("Missing year: " + dateString));
        return new FuzzyDate(ta, dateString, info);
      } catch (DateTimeException e) { // Next one then
      }
    }
    throw new FuzzyDateException("Could not create FuzzyDate for \": " + dateString + "\"");
  }

  /*
   * Whether or not a year can be extracted from the specified TemporalAccessor. Generally this
   * comes down to calling the isSupported on the TemporalAccessor, but for Instant this will return
   * false even though a year can obviously extracted from them.
   */
  private static boolean hasKnownYear(TemporalAccessor ta) {
    return ta.isSupported(YEAR) || ta.getClass() == Instant.class;
  }
}
