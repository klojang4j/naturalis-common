package nl.naturalis.common.time;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import static java.time.temporal.ChronoField.*;

/**
 * A {@code FuzzyDate} represents a date of which at least the year is known. You can retrieve
 * regular {@code java.time} objects from it as well as the verbatim date string from which it was
 * created as well. You obtain an instance by calling {@link FuzzyDateParser#parse(String)
 * FuzzyDateParser.parse}.
 */
public final class FuzzyDate {

  private final TemporalAccessor ta;
  private final String verbatim;
  private final ParseInfo parseSpec;

  FuzzyDate(TemporalAccessor ta, String verbatim, ParseInfo parseSpec) {
    this.ta = ta;
    this.verbatim = verbatim;
    this.parseSpec = parseSpec;
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link Instant java.time.Instant} object.
   *
   * @return An instance of {@code Instant}
   */
  public Instant toInstant() {
    if (ta.getClass() == Instant.class) {
      return (Instant) ta;
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay(ZoneOffset.UTC).toInstant();
    } else {
      return Instant.from(ta);
    }
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime} object, setting month and day to 1
   * if unknown, and setting hour, minute and second to 0 if unknown.
   *
   * @return An instance of {@code LocalDateTime}
   */
  public LocalDateTime toLocalDateTime() {
    if (ta.getClass() == LocalDateTime.class) {
      return (LocalDateTime) ta;
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toLocalDateTime();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay();
    } else if (ta.getClass() == Instant.class) {
      return LocalDateTime.ofInstant((Instant) ta, ZoneOffset.UTC);
    } else {
      int year = ta.get(YEAR);
      int month = ta.isSupported(MONTH_OF_YEAR) ? ta.get(MONTH_OF_YEAR) : 1;
      int day = ta.isSupported(DAY_OF_MONTH) ? ta.get(DAY_OF_MONTH) : 1;
      int hour = ta.isSupported(HOUR_OF_DAY) ? ta.get(HOUR_OF_DAY) : 0;
      int minute = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
      int second = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
      return LocalDateTime.of(year, month, day, hour, minute, second);
    }
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDate} object, setting month and/or day to 1 if
   * unknown.
   *
   * @return An instance of {@code LocalDateTime}
   */
  public LocalDate toLocalDate() {
    if (ta.getClass() == LocalDate.class) {
      return (LocalDate) ta;
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toLocalDate();
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).toLocalDate();
    } else if (ta.getClass() == Instant.class) {
      return LocalDate.ofInstant((Instant) ta, ZoneId.of("Z"));
    } else {
      int year = ta.get(YEAR);
      int month = ta.isSupported(MONTH_OF_YEAR) ? ta.get(MONTH_OF_YEAR) : 1;
      int day = ta.isSupported(DAY_OF_MONTH) ? ta.get(DAY_OF_MONTH) : 1;
      return LocalDate.of(year, month, day);
    }
  }

  /**
   * Returns the most granular date/time object that could be parsed out of the date string.
   *
   * @return An instance of {@code TemporalAccessor} that best matches the date string
   */
  public TemporalAccessor bestMatch() {
    int year = ta.get(YEAR);
    if (ta.isSupported(MONTH_OF_YEAR)) {
      int month = ta.get(MONTH_OF_YEAR);
      if (ta.isSupported(DAY_OF_MONTH)) {
        int day = ta.get(DAY_OF_MONTH);
        if (ta.isSupported(HOUR_OF_DAY)) {
          int hour = ta.get(HOUR_OF_DAY);
          int minute = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
          int second = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
          return LocalDateTime.of(year, month, day, hour, minute, second);
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
    return parseSpec;
  }

  /**
   * Returns whether or not this instance contains a fuzzy date. Returns true if either month or day
   * is unknown, false if both are known.
   *
   * @return Whether or not this instance contains a fuzzy date
   */
  public boolean isFuzzyDate() {
    return !(ta.isSupported(MONTH_OF_YEAR) && ta.isSupported(DAY_OF_MONTH));
  }

  /**
   * Returns whether or not this instance contains a fuzzy time. Returns true if either the hour or
   * the minute is unknown, false if both are known. Seconds are ignored, so time is <i>not</i>
   * regarded as fuzzy if hour and minute are known but the second is not. If seconds are important,
   * use the <code>isSupported</code> method on the {@link TemporalAccessor} instance returned by
   * {@link #getTemporalAccessor() getTemporalAccessor}.
   *
   * @return Whether or not this instance contains a fuzzy time
   */
  public boolean isFuzzyTime() {
    return !(ta.isSupported(HOUR_OF_DAY) && ta.isSupported(MINUTE_OF_HOUR));
  }

  /**
   * Returns whether or not there is anything fuzzy about this instance except for (perhaps) its
   * second. Equivalent to {@code isFuzzyDate() || isFuzzyTime()}.
   *
   * @return Whether or not there is anything fuzzy about this instance
   */
  public boolean isFuzzyDateTime() {
    return isFuzzyDate() || isFuzzyTime();
  }
}
