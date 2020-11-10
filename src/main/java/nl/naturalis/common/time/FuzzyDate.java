package nl.naturalis.common.time;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.*;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ObjectMethods.ifNull;

/**
 * A {@code FuzzyDate} represents a date of which at least the year is known. You can retrieve
 * regular {@code java.time} objects from it as well as the verbatim date string from which it was
 * created. You obtain an instance by calling {@link FuzzyDateParser#parse(String)
 * FuzzyDateParser.parse}.
 *
 * <p>A simple example of the workflow and capabilities of {@code FuzzyDate} and friends:
 *
 * <p>
 *
 * <pre>
 * ParseInfo info1 = new ParseInfo("yyyy-MM-dd HH:mm:ss");
 * ParseInfo info2 = new ParseInfo("yyyy-MM-dd");
 * FuzzyDateParser parser = new FuzzyDateParser(info1, info2);
 * String dateString = "2020-09-18";
 * FuzzyDate fuzzyDate = parser.parse(dateString);
 * assertTrue(fuzzyDate.isTimeFuzzy());
 * assertFalse(fuzzyDate.isDateFuzzy());
 * assertSame(LocalDate.class, fuzzyDate.bestMatch().getClass());
 * assertSame(info2, fuzzyDate.getParseInfo());
 * OffsetDateTime realDate = fuzzyDate.toOffsetDateTime();
 * </pre>
 */
public final class FuzzyDate {

  private static Class<?>[] supported =
      pack(
          OffsetDateTime.class,
          LocalDateTime.class,
          LocalDate.class,
          YearMonth.class,
          Year.class,
          Instant.class);

  private final TemporalAccessor ta;
  private final int year;
  private final String verbatim;
  private final ParseInfo parseInfo;

  FuzzyDate(TemporalAccessor ta, int year, String verbatim, ParseInfo parseInfo) {
    this.ta = ta;
    this.year = year;
    this.verbatim = verbatim;
    this.parseInfo = parseInfo;
  }

  /**
   * Returns the year of this {@code FuzzyDate}, which is the one date/time field that is guaranteed
   * to be known.
   *
   * @return The year of this {@code FuzzyDate}
   */
  public int getYear() {
    return year;
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link Instant java.time.Instant} object.
   *
   * @return An instance of {@code Instant}
   */
  public Instant toInstant() {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atDay(1).atStartOfDay(UTC).toInstant();
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay(UTC).toInstant();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay(UTC).toInstant();
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).atOffset(UTC).toInstant();
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toInstant();
    } else if (ta.getClass() == Instant.class) {
      return ((Instant) ta);
    } else {
      return assemble().toInstant();
    }
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link OffsetDateTime} object, setting month and day to 1
   * if unknown; hour, minute and second to 0 if unknown; and the time zone to UTC if unknown.
   *
   * @return An instance of {@code LocalDateTime}
   */
  public OffsetDateTime toOffsetDateTime() {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atDay(1).atStartOfDay(UTC).toOffsetDateTime();
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay(UTC).toOffsetDateTime();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay().atOffset(UTC);
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).atOffset(UTC);
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta);
    } else if (ta.getClass() == Instant.class) {
      return OffsetDateTime.ofInstant((Instant) ta, UTC);
    } else {
      return assemble();
    }
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime} object, setting month and day to 1
   * if unknown, and hour, minute and second to 0 if unknown.
   *
   * @return An instance of {@code LocalDateTime}
   */
  public LocalDateTime toLocalDateTime() {
    if (ta.getClass() == Year.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay();
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay();
    } else if (ta.getClass() == LocalDateTime.class) {
      return (LocalDateTime) ta;
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toLocalDateTime();
    } else if (ta.getClass() == Instant.class) {
      return LocalDateTime.ofInstant((Instant) ta, UTC);
    } else {
      return assemble().toLocalDateTime();
    }
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDate} object, setting month and day to 1 if
   * unknown.
   *
   * @return An instance of {@code LocalDate}
   */
  public LocalDate toLocalDate() {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atDay(1);
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1);
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toLocalDate();
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).toLocalDate();
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toLocalDate();
    } else if (ta.getClass() == Instant.class) {
      return LocalDate.ofInstant((Instant) ta, UTC);
    } else {
      return assemble().toLocalDate();
    }
  }

  /**
   * Returns the most granular date/time object that could be parsed out of the date string.
   *
   * @return An instance of {@code TemporalAccessor} that best matches the date string
   */
  public TemporalAccessor bestMatch() {
    if (isOneOf(ta.getClass(), supported)) {
      return ta;
    }
    if (ta.isSupported(MONTH_OF_YEAR)) {
      int month = ta.get(MONTH_OF_YEAR);
      if (ta.isSupported(DAY_OF_MONTH)) {
        int day = ta.get(DAY_OF_MONTH);
        if (ta.isSupported(HOUR_OF_DAY)) {
          int hour = ta.get(HOUR_OF_DAY);
          int minute = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
          int second = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
          ZoneOffset z = getZoneOffset();
          if (z == null) {
            return LocalDateTime.of(year, month, day, hour, minute, second);
          }
          return OffsetDateTime.of(year, month, day, hour, minute, second, 0, z);
        }
        return LocalDate.of(year, month, day);
      }
      return YearMonth.of(year, month);
    }
    return Year.of(year);
  }

  /**
   * Returns the raw {@link TemporalAccessor} object that was created from the verbatim date string.
   *
   * @return An instance of {@code TemporalAccessor}
   */
  public TemporalAccessor getTemporalAccessor() {
    return ta;
  }

  /**
   * Returns the original date string from which this FuzzyDate instance was created.
   *
   * @return The original date string from which this FuzzyDate instance was created
   */
  public String getVerbatim() {
    return verbatim;
  }

  /**
   * Returns the {@link ParseInfo} instance used to parse the date string into this {@code
   * FuzzyDate} instance.
   *
   * @return
   */
  public ParseInfo getParseInfo() {
    return parseInfo;
  }

  /**
   * Returns whether or not the date of this instance is fuzzy. Returns true if either month or day
   * is unknown, false if both are known.
   *
   * @return Whether or not the date of this instance is fuzzy
   */
  public boolean isDateFuzzy() {
    return !(ta.isSupported(MONTH_OF_YEAR) && ta.isSupported(DAY_OF_MONTH));
  }

  /**
   * Returns whether or not the time of this instance is fuzzy. Returns true if either the hour or
   * the minute is unknown, false if both are known. Seconds are ignored, so time is <i>not</i>
   * regarded as fuzzy if hour and minute are known but the second is not. If seconds are important,
   * use the <code>isSupported</code> method on the {@link TemporalAccessor} instance returned by
   * {@link #getTemporalAccessor() getTemporalAccessor}.
   *
   * @return Whether or not the date of this instance is fuzzy
   */
  public boolean isTimeFuzzy() {
    return !(ta.isSupported(HOUR_OF_DAY) && ta.isSupported(MINUTE_OF_HOUR));
  }

  /**
   * Returns whether or not there is anything fuzzy about this instance except for (perhaps) its
   * second. Equivalent to {@code isDateFuzzy() || isTimeFuzzy()}.
   *
   * @return Whether or not there is anything fuzzy about this instance
   */
  public boolean isFuzzy() {
    return isDateFuzzy() || isTimeFuzzy();
  }

  /**
   * Returns true if this object's {@link TemporalAccessor} equals the other object's {@code
   * TemporalAccessor}, or if this object's {@link OffsetDateTime} representation equals the other
   * object's {@link OffsetDateTime} representation. The verbatim date string and the {@code
   * ParseInfo} objects from which the two {@code FuzzyDate} instances were created are ignored.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    FuzzyDate other = (FuzzyDate) obj;
    if (ta.equals(other.ta) || toOffsetDateTime().equals(other.toOffsetDateTime())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ta.hashCode();
  }

  private OffsetDateTime assemble() {
    int month = ta.isSupported(MONTH_OF_YEAR) ? ta.get(MONTH_OF_YEAR) : 1;
    int day = ta.isSupported(DAY_OF_MONTH) ? ta.get(DAY_OF_MONTH) : 1;
    int hour = ta.isSupported(HOUR_OF_DAY) ? ta.get(HOUR_OF_DAY) : 0;
    int minute = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
    int second = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
    ZoneOffset z = ifNull(getZoneOffset(), UTC);
    return OffsetDateTime.of(year, month, day, hour, minute, second, 0, z);
  }

  private ZoneOffset getZoneOffset() {
    try {
      return ZoneOffset.from(ta);
    } catch (DateTimeException e) {
      return null;
    }
  }
}
