package nl.naturalis.common.time;

import java.io.InputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.common.Check;
import nl.naturalis.common.ExceptionMethods;

import static java.time.temporal.ChronoField.YEAR;

/**
 * Parses date strings into {@code FuzzyDate} instances. Fuzzy dates will have at least their year
 * set, but any other field may be unknown. The {@link #DEFAULT} parser parses a wide variety of
 * date strings. See below. If you know which types of date strings to expect, it might be better to
 * specify your own {@link ParseInfo} instances, either manually or through a properties file.
 *
 * <p>Date strings are parsed by iterating over a list of {@link ParseInfo} instances, which specify
 * how to parse the input string. As soon as a {@code ParseSpec} is capable of parsing the date
 * string into a {@code java.time} object, the iteration stops. Therefore the more granular {@link
 * ParseInfo} instances should come first in the list. A {@code FuzzyDateParser} can be instantiated
 * with a list of hard-coded {@link ParseInfo} instances or with a properties file defining the
 * {@link ParseInfo} instances. The layout of the properties file is shown below.
 *
 * <p>Note about performance: date parsing is said to be relatively expensive, but the cost does not
 * come from the pattern-matching phase, but in what happens next: the construction of a {@link
 * TemporalAccessor} object. Therefore the number of {@link ParseInfo} instances with which the
 * parser is instantiated does not greatly impact performance.
 *
 * <p>Below is the properties file for the {@link #DEFAULT} parser:
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
public class FuzzyDateParser {

  /** A FuzzyDateParser instance capable of parsing a wide range of date strings. */
  public static final FuzzyDateParser DEFAULT = new FuzzyDateParser();

  private final List<ParseInfo> parseSpecs;

  // Reserved for the default parser
  private FuzzyDateParser() {
    InputStream is = FuzzyDateParser.class.getResourceAsStream("FuzzyDate.properties.txt");
    ParseSpecProperties config = new ParseSpecProperties(is);
    try {
      this.parseSpecs = config.createParseSpecs();
    } catch (FuzzyDateException e) {
      // That's a bug because we created that properties file ourselves
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a {@code DateParser} from the provided input stream, supposedly created from a
   * properties file defining the {@code ParseSpec} instances as described above.
   *
   * @param is
   * @throws FuzzyDateException
   */
  public FuzzyDateParser(InputStream is) throws FuzzyDateException {
    ParseSpecProperties config = new ParseSpecProperties(is);
    this.parseSpecs = config.createParseSpecs();
  }

  /**
   * Creates a {@code DateParser} that uses the provided {@code ParseSpec} instances to parse date
   * strings.
   *
   * @param parseSpec0
   * @param parseSpec1
   * @param moreParseSpecs
   */
  public FuzzyDateParser(ParseInfo parseSpec0, ParseInfo parseSpec1, ParseInfo... moreParseSpecs) {
    List<ParseInfo> parseSpecs = new ArrayList<>(moreParseSpecs.length + 2);
    parseSpecs.add(parseSpec0);
    parseSpecs.add(parseSpec1);
    parseSpecs.addAll(Arrays.asList(moreParseSpecs));
    this.parseSpecs = parseSpecs;
  }

  /**
   * Creates a {@code DateParser} that uses the provided {@code ParseSpec} instances to parse date
   * strings.
   *
   * @param parseSpecs
   */
  public FuzzyDateParser(List<ParseInfo> parseSpecs) {
    this.parseSpecs = parseSpecs;
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
    Check.notNull(dateString, "text");
    String input; // goes into the formatter
    TemporalAccessor ta; // comes out of the formatter
    for (ParseInfo parseSpec : parseSpecs) {
      if (parseSpec.filter == null) {
        input = dateString;
      } else {
        input = parseSpec.filter.apply(dateString);
        if (input == null) {
          continue;
        }
      }
      try {
        if (parseSpec.parseInto == null || parseSpec.parseInto.length == 0) {
          ta = parseSpec.formatter.parse(input);
        } else {
          if (parseSpec.parseInto.length == 1) {
            ta = (TemporalAccessor) parseSpec.formatter.parse(input, parseSpec.parseInto[0]);
          } else {
            ta = parseSpec.formatter.parseBest(input, parseSpec.parseInto);
          }
        }
        Check.that(hasKnownYear(ta), () -> new FuzzyDateException("Missing year: " + dateString));
        return new FuzzyDate(ta, dateString, parseSpec);
      } catch (DateTimeException e) { // Next one then
      }
    }
    throw new FuzzyDateException("Could not create FuzzyDate for \": " + dateString + "\"");
  }

  /*
   * Whether or not a year can be extracted from the provided <code>TemporalAccessor</code>. Generally this comes down to calling the
   * isSupported on the TemporalAccessor, but for Instant objects this will return false even though a year can obviously extracted from
   * them.
   */
  private static boolean hasKnownYear(TemporalAccessor ta) {
    return ta.isSupported(YEAR) || ta.getClass() == Instant.class;
  }
}
