package nl.naturalis.common.time;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.*;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ObjectMethods.ifNotEmpty;

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
   * Returns the year of this {@code FuzzyDate}.
   *
   * @return The year of this {@code FuzzyDate}
   */
  public int getYear() {
    return year;
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link Instant}. Month and day are set to 1 if unknown.
   * Hour, minute and second are set to 0 if unknown. The time zone is set to {@link ZoneOffset#UTC
   * UTC} if unknown.
   *
   * @return The {@code Instant} most closely corresponding to this {@code FuzzyDate}
   */
  public Instant toInstant() {
    return toInstant(UTC);
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link Instant}. Month and day are set to 1 if unknown.
   * Hour, minute and second are set to 0 if unknown. The time zone is set to the specified time
   * zone if unknown.
   *
   * @param zone The {@code ZoneOffset} to use if no {@code ZoneOffset} could be extracted from the
   *     date string
   * @return The {@code Instant} most closely corresponding to this {@code FuzzyDate}
   */
  public Instant toInstant(ZoneOffset zone) {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atDay(1).atStartOfDay(getZone(zone)).toInstant();
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay(getZone(zone)).toInstant();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay(getZone(zone)).toInstant();
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).atOffset(getZone(zone)).toInstant();
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta).toInstant();
    } else if (ta.getClass() == Instant.class) {
      return ((Instant) ta);
    } else {
      return assemble(zone).toInstant();
    }
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link OffsetDateTime}. Month and day are set to 1 if
   * unknown. Hour, minute and second are set to 0 if unknown. The time zone is set to {@link
   * ZoneOffset#UTC UTC} if unknown.
   *
   * @return The {@code OffsetDateTime} most closely corresponding to this {@code FuzzyDate}
   */
  public OffsetDateTime toOffsetDateTime() {
    return toOffsetDateTime(UTC);
  }

  /**
   * Converts this {@code FuzzyDate} to an {@link OffsetDateTime}. Month and day are set to 1 if
   * unknown. Hour, minute and second are set to 0 if unknown. The time zone is set to the specified
   * time zone if unknown.
   *
   * @param zone The {@code ZoneOffset} to use if no {@code ZoneOffset} could be extracted from the
   *     date string
   * @return The {@code Instant} most closely corresponding to this {@code FuzzyDate}
   */
  public OffsetDateTime toOffsetDateTime(ZoneOffset zone) {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atDay(1).atStartOfDay(getZone(zone)).toOffsetDateTime();
    } else if (ta.getClass() == YearMonth.class) {
      return ((YearMonth) ta).atDay(1).atStartOfDay(getZone(zone)).toOffsetDateTime();
    } else if (ta.getClass() == LocalDate.class) {
      return ((LocalDate) ta).atStartOfDay().atOffset(getZone(zone));
    } else if (ta.getClass() == LocalDateTime.class) {
      return ((LocalDateTime) ta).atOffset(getZone(zone));
    } else if (ta.getClass() == OffsetDateTime.class) {
      return ((OffsetDateTime) ta);
    } else if (ta.getClass() == Instant.class) {
      return OffsetDateTime.ofInstant((Instant) ta, getZone(zone));
    }
    return assemble(zone);
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime}. Month and day are set to 1 if
   * unknown. Hour, minute and second are set to 0 if unknown. The time zone is set to {@link
   * ZoneOffset#UTC UTC} if unknown.
   *
   * @return The {@code LocalDateTime} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDateTime toLocalDateTime() {
    return toLocalDateTime(UTC);
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime}, setting month and day to 1 if
   * unknown, and hour, minute and second to 0 if unknown.
   *
   * @param zone The {@code ZoneOffset} to use if no {@code ZoneOffset} could be extracted from the
   *     date string
   * @return The {@code Instant} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDateTime toLocalDateTime(ZoneOffset zone) {
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
      return LocalDateTime.ofInstant((Instant) ta, zone);
    }
    return assemble(zone).toLocalDateTime();
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDate}. Month and day are set to 1 if unknown.
   * Hour, minute and second are set to 0 if unknown. The time zone is set to {@link ZoneOffset#UTC
   * UTC} if unknown.
   *
   * @return The {@code LocalDate} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDate toLocalDate() {
    return toLocalDate(UTC);
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime}. Month and day are set to 1 if
   * unknown. Hour, minute and second are set to 0 if unknown. The time zone is set to {@link
   * ZoneOffset#UTC UTC} if unknown.
   *
   * @return The {@code LocalDateTime} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDate toLocalDate(ZoneOffset timeZone) {
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
      return LocalDate.ofInstant((Instant) ta, getZone(timeZone));
    }
    return assemble(timeZone).toLocalDate();
  }

  /**
   * Returns a date/time object whose type depends on the actual granularity of this {@code
   * FuzzyDate}. See {@link #bestMatch(ZoneOffset)}.
   *
   * @return A date/time object whose type depends on the actual granularity of this {@code
   *     FuzzyDate}
   */
  public TemporalAccessor bestMatch() {
    return bestMatch(UTC);
  }

  /**
   * Returns a date/time object whose type depends on the actual granularity of this {@code
   * FuzzyDate}. The following {@code java.time} objects are supported:
   *
   * <p>
   *
   * <ul>
   *   <li>{@link OffsetDateTime}
   *   <li>{@link LocalDateTime}
   *   <li>{@link LocalDate}
   *   <li>{@link YearMonth}
   *   <li>{@link Year}
   * </ul>
   *
   * <p>This method will never return a {@link ZonedDateTime}, even if the date string was actually
   * parsed into such an object. If desirable, you can use {@link #getTemporalAccessor()
   * getTemporalAccessor()} and check if the returned object is {@code ZonedDateTime}.
   *
   * @return A date/time object whose type depends on the actual granularity of this {@code
   *     FuzzyDate}
   */
  public TemporalAccessor bestMatch(ZoneOffset timeZone) {
    if (isOneOf(
        ta.getClass(),
        OffsetDateTime.class,
        LocalDateTime.class,
        LocalDate.class,
        YearMonth.class,
        Year.class)) {
      return ta;
    } else if (ta.getClass() == ZonedDateTime.class) {
      return ((ZonedDateTime) ta).toOffsetDateTime();
    } else if (ta.getClass() == Instant.class) {
      return LocalDateTime.ofInstant((Instant) ta, timeZone);
    }
    if (ta.isSupported(MONTH_OF_YEAR)) {
      int month = ta.get(MONTH_OF_YEAR);
      if (ta.isSupported(DAY_OF_MONTH)) {
        int day = ta.get(DAY_OF_MONTH);
        if (ta.isSupported(HOUR_OF_DAY)) {
          int hour = ta.get(HOUR_OF_DAY);
          int min = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
          int sec = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
          return ifNotEmpty(
              getTimeZone(),
              z -> OffsetDateTime.of(year, month, day, hour, min, sec, 0, z.get()),
              LocalDateTime.of(year, month, day, hour, min, sec));
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
   * Returns an {@code Optional} containing the time zone or an empty {@code Optional} if unknown.
   *
   * @return An {@code Optional} containing the time zone or an empty {@code Optional} if unknown
   */
  public Optional<ZoneOffset> getTimeZone() {
    try {
      return Optional.of(ZoneOffset.from(ta));
    } catch (DateTimeException e) {
      try {
        return Optional.of(ZoneId.from(ta).getRules().getOffset(Instant.from(ta)));
      } catch (DateTimeException e2) {
        return Optional.empty();
      }
    }
  }

  /**
   * Returns the original date string from which this instance was created.
   *
   * @return The original date string from which this instance was created
   */
  public String getVerbatim() {
    return verbatim;
  }

  /**
   * Returns the {@link ParseInfo} instance that was used to parse the date string.
   *
   * @return The {@code ParseInfo} instance that was used to parse the date string
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
   * Returns whether or not the time zone of this instance is fuzzy. Returns true if no {@link
   * ZoneId} or {@link ZoneOffset} could be extracted from the date string.
   *
   * @return Whether or not the time zone of this instance is fuzzy
   */
  public boolean isTimeZoneFuzzy() {
    return getTimeZone().isEmpty();
  }

  /**
   * Returns whether or not the date and/or time are fuzzy. Equivalent to {@code isDateFuzzy() ||
   * isTimeFuzzy()}.
   *
   * @return Whether or not the date and/or time are fuzzy.
   */
  public boolean isFuzzy() {
    return isDateFuzzy() || isTimeFuzzy();
  }

  /**
   * Returns true if this object's {@link #bestMatch()} equals the other object's {@code
   * bestMatch()}. The verbatim date string and the {@code ParseInfo} objects from which the two
   * {@code FuzzyDate} instances were created are ignored.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    FuzzyDate other = (FuzzyDate) obj;
    return bestMatch().equals(other.bestMatch());
  }

  @Override
  public int hashCode() {
    return toOffsetDateTime().hashCode();
  }

  private OffsetDateTime assemble(ZoneOffset dfault) {
    int month = ta.isSupported(MONTH_OF_YEAR) ? ta.get(MONTH_OF_YEAR) : 1;
    int day = ta.isSupported(DAY_OF_MONTH) ? ta.get(DAY_OF_MONTH) : 1;
    int hour = ta.isSupported(HOUR_OF_DAY) ? ta.get(HOUR_OF_DAY) : 0;
    int minute = ta.isSupported(MINUTE_OF_HOUR) ? ta.get(MINUTE_OF_HOUR) : 0;
    int second = ta.isSupported(SECOND_OF_MINUTE) ? ta.get(SECOND_OF_MINUTE) : 0;
    return OffsetDateTime.of(year, month, day, hour, minute, second, 0, getZone(dfault));
  }

  private ZoneOffset getZone(ZoneOffset dfault) {
    return getTimeZone().orElse(dfault);
  }
}
