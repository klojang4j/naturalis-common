package nl.naturalis.common.time;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.*;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ObjectMethods.ifNotEmpty;

/**
 * A {@code FuzzyDate} represents the result of parsing a date string into a date/time object. You
 * obtain a {@code FuzzyDate} by calling {@link FuzzyDateParser#parse(String)}. The instance
 * returned by the parser is guaranteed to have at least a known year. Month, day, hour, minute,
 * second and time zone may or may not be known. If the parser could not extract a year from the
 * date string a {@link FuzzyDateException} is thrown.
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
   * Returns an {@code OptionalInt} containing the month of this {@code FuzzyDate} or an empty
   * {@code OptionalInt} if no month could be extracted from the date string.
   *
   * @return The month of this {@code FuzzyDate}
   */
  public OptionalInt getMonth() {
    return get(MONTH_OF_YEAR);
  }

  /**
   * Returns an {@code OptionalInt} containing the day of this {@code FuzzyDate} or an empty {@code
   * OptionalInt} if no day could be extracted from the date string.
   *
   * @return The day of this {@code FuzzyDate}
   */
  public OptionalInt getDay() {
    return get(DAY_OF_MONTH);
  }

  /**
   * Returns an {@code OptionalInt} containing the hour of this {@code FuzzyDate} or an empty {@code
   * OptionalInt} if no hour could be extracted from the date string.
   *
   * @return The hour of this {@code FuzzyDate}
   */
  public OptionalInt getHour() {
    return get(HOUR_OF_DAY);
  }

  /**
   * Returns an {@code OptionalInt} containing the minute of this {@code FuzzyDate} or an empty
   * {@code OptionalInt} if no minute could be extracted from the date string.
   *
   * @return The minute of this {@code FuzzyDate}
   */
  public OptionalInt getMinute() {
    return get(MINUTE_OF_HOUR);
  }

  /**
   * Returns an {@code OptionalInt} containing the second of this {@code FuzzyDate} or an empty
   * {@code OptionalInt} if no second could be extracted from the date string.
   *
   * @return The second of this {@code FuzzyDate}
   */
  public OptionalInt getSecond() {
    return get(SECOND_OF_MINUTE);
  }

  /**
   * Returns an {@code OptionalInt} containing the nano second of this {@code FuzzyDate} or an empty
   * {@code OptionalInt} if no nano second could be extracted from the date string.
   *
   * @return The nano second of this {@code FuzzyDate}
   */
  public OptionalInt getNano() {
    return get(NANO_OF_SECOND);
  }

  /**
   * Returns an {@code Optional} containing the time zone or an empty {@code Optional} if no nano
   * second could be extracted from the date string.
   *
   * @return An {@code Optional} containing the time zone
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
          int min = getMinute().orElse(0);
          int sec = getSecond().orElse(0);
          int nano = getNano().orElse(0);
          return ifNotEmpty(
              getTimeZone(),
              z -> OffsetDateTime.of(year, month, day, hour, min, sec, nano, z.get()),
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
   * regarded as fuzzy if hour and minute are known but the second is not.
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
   * Returns whether or not the date or time is fuzzy. Equivalent to {@code isDateFuzzy() ||
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
    if (ta.equals(other.ta)) {
      return true;
    }
    return year == other.year
        && Objects.equals(get(MONTH_OF_YEAR), other.get(MONTH_OF_YEAR))
        && Objects.equals(get(DAY_OF_MONTH), other.get(DAY_OF_MONTH))
        && Objects.equals(get(HOUR_OF_DAY), other.get(HOUR_OF_DAY))
        && Objects.equals(get(MINUTE_OF_HOUR), other.get(MINUTE_OF_HOUR))
        && Objects.equals(get(SECOND_OF_MINUTE), other.get(SECOND_OF_MINUTE))
        && Objects.equals(get(NANO_OF_SECOND), other.get(NANO_OF_SECOND))
        && Objects.equals(getTimeZone(), other.getTimeZone());
  }

  @Override
  public int hashCode() {
    return toOffsetDateTime().hashCode();
  }

  private OffsetDateTime assemble(ZoneOffset dfault) {
    return OffsetDateTime.of(
        year,
        getMonth().orElse(1),
        getDay().orElse(1),
        getHour().orElse(0),
        getMinute().orElse(0),
        getSecond().orElse(0),
        getNano().orElse(0),
        getZone(dfault));
  }

  private OptionalInt get(ChronoField field) {
    return ta.isSupported(field) ? OptionalInt.of(ta.get(field)) : OptionalInt.empty();
  }

  private ZoneOffset getZone(ZoneOffset dfault) {
    return getTimeZone().orElse(dfault);
  }
}
