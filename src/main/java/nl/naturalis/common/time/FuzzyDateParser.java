package nl.naturalis.common.time;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.time.temporal.ChronoField.YEAR;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Parses date strings into {@link FuzzyDate} instances. The minimum requirement for a valid date
 * string is that a year can be extracted from it.
 *
 * <p>Date strings are parsed by iterating over a list of {@link ParseAttempt} instances, passed in
 * through one of the constructors . A {@link ParseAttempt} instance specifies how to parse the
 * input string. As soon as a {@code ParseAttempt} is capable of parsing the date string into a
 * {@code java.time} object, the iteration stops. Therefore the more granular {@link ParseAttempt}
 * instances should come first in the list.
 *
 * <p>A {@code FuzzyDateParser} can be instantiated with a list of hard-coded {@link ParseAttempt}
 * instances or with a properties file defining the {@link ParseAttempt} instances. See the
 * description of the {@link #DEFAULT} {@code FuzzyDateParser} for how to encode {@code
 * ParseAttempt} instances.
 *
 * <p>Note about performance: date parsing is relatively expensive, but the cost does not come from
 * the pattern-matching phase, but from the subsequent creation of a {@link TemporalAccessor}
 * object. Therefore the number of {@link ParseAttempt} instances with which the parser is
 * instantiated does not greatly impact performance.
 */
public class FuzzyDateParser {

  private static FuzzyDateParser DEFAULT;

  private static final Map<Locale, FuzzyDateParser> parsers = new HashMap<>();

  public static FuzzyDateParser getDefaultParser() {
    return parsers.computeIfAbsent(null, k -> new FuzzyDateParser());
  }

  public static FuzzyDateParser getDefaultParser(Locale defaultLocale) {
    return parsers.computeIfAbsent(defaultLocale, FuzzyDateParser::new);
  }

  private final List<ParseAttempt> parseAttempts;

  // Reserved for the default parser
  private FuzzyDateParser() {
    InputStream is = FuzzyDateParser.class.getResourceAsStream("FuzzyDate.xml");
    try {
      this.parseAttempts = new ConfigReader(is).getConfig();
    } catch (Exception e) {
      // That's a bug because we created the XML file ourselves
      throw ExceptionMethods.uncheck(e);
    }
  }

  private FuzzyDateParser(Locale locale) {
    InputStream is = FuzzyDateParser.class.getResourceAsStream("FuzzyDate.xml");
    try {
      this.parseAttempts = new ConfigReader(is, List.of(locale)).getConfig();
    } catch (Exception e) {
      // That's a bug because we created the XML file ourselves
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a {@code FuzzyDateParser} from the provided input stream, supposedly created from a
   * properties file encoding the {@code ParseAttempt} instances. See the description of the {@link
   * #DEFAULT} {@code FuzzyDateParser} for how to encode {@code ParseAttempt} instances.
   *
   * @param is The {@code InputStream}
   * @throws FuzzyDateException If there is an encoding error in the {@code InputStream}
   */
  public FuzzyDateParser(InputStream is)
      throws FuzzyDateException, ParserConfigurationException, IOException, SAXException {
    Check.notNull(is);
    this.parseAttempts = new ConfigReader(is).getConfig();
  }

  public FuzzyDateParser(InputStream is, Locale defaultLocale)
      throws FuzzyDateException, ParserConfigurationException, IOException, SAXException {
    Check.notNull(is);
    Check.notNull(defaultLocale);
    this.parseAttempts = new ConfigReader(is, List.of(defaultLocale)).getConfig();
  }

  /**
   * Creates a {@code FuzzyDateParser} that uses the specified {@code ParseAttempt} instances to
   * parse date strings.
   *
   * @param parseAttempts More {@code ParseAttempt} instances
   */
  public FuzzyDateParser(ParseAttempt... parseAttempts) {
    this.parseAttempts = Check.that(parseAttempts).is(deepNotEmpty()).ok(List::of);
  }

  /**
   * Creates a {@code FuzzyDateParser} that uses the specified {@code ParseAttempt} instances to
   * parse date strings.
   *
   * @param parseAttempts The {@code ParseAttempt} instances used to parse date strings
   */
  public FuzzyDateParser(List<ParseAttempt> parseAttempts) {
    this.parseAttempts = Check.that(parseAttempts).isNot(empty()).is(neverNull()).ok(List::copyOf);
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
    boolean parsable = false;
    for (ParseAttempt pa : parseAttempts) {
      String input = pa.filter == null ? dateString : pa.filter.validateOrTransform(dateString);
      if (input == null) {
        continue;
      }
      try {
        TemporalAccessor ta;
        if (pa.parseInto.isEmpty()) {
          ta = pa.formatter.parse(input);
        } else if (pa.parseInto.size() == 1) {
          ta = pa.formatter.parse(input, pa.parseInto.get(0));
        } else {
          ta = pa.formatter.parseBest(input, pa.parseInto.toArray(TemporalQuery[]::new));
        }
        parsable = true;
        Integer year = getYear(ta);
        if (year != null) {
          return new FuzzyDate(ta, year, dateString, pa);
        }
      } catch (DateTimeException e) { // Next one then
      }
    }
    if (parsable) {
      throw FuzzyDateException.missingYear(dateString);
    }
    throw FuzzyDateException.notParsable(dateString);
  }

  private static Integer getYear(TemporalAccessor ta) {
    if (ta.isSupported(YEAR)) {
      return ta.get(YEAR);
    } else if (ta.getClass() == Instant.class) {
      return ((Instant) ta).atOffset(ZoneOffset.UTC).getYear();
    }
    return null;
  }
}
