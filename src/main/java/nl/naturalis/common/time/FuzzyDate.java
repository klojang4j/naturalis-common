package nl.naturalis.common.time;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.OptionalInt;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.*;
import static nl.naturalis.common.ArrayMethods.isPresent;
import static nl.naturalis.common.ObjectMethods.ifNotEmpty;

/**
 * A {@code FuzzyDate} is a date of which at least the year component is set. The {@code FuzzyDate}
 * class and the other classes in this package or no general-purpose date/time utility classes.
 * Instead, they focus on extracting {@code java.time} objects from archival data or historical
 * records with inconsistently formatted dates. You obtain a {@code FuzzyDate} by calling {@link
 * FuzzyDateParser#parse(String)}. The instance returned by the parser is guaranteed to have at
 * least its year set. Month, day, hour, minute, second and time zone may or may not be known. If
 * the parser could not extract a year from the date string a {@link FuzzyDateException} is thrown.
 *
 * <p>Although any number of date/time patterns can be used to parse date strings into date/time
 * objects, {@code FuzzyDate} works best when they are composed of the usual date fields: {@link
 * ChronoField#YEAR YEAR}, {@link ChronoField#MONTH_OF_YEAR MONTH_OF_YEAR}, {@link
 * ChronoField#DAY_OF_MONTH DAY_OF_MONTH}, {@link ChronoField#HOUR_OF_DAY HOUR_OF_DAY}, {@link
 * ChronoField#MINUTE_OF_HOUR MINUTE_OF_HOUR}, {@link ChronoField#SECOND_OF_MINUTE
 * SECOND_OF_MINUTE}, {@link ChronoField#NANO_OF_SECOND NANO_OF_SECOND}, {@link ZoneOffset} and
 * {@link ZoneId}. It may not be accurate when composed of more exotic date fields like {@link
 * ChronoField#DAY_OF_YEAR DAY_OF_YEAR} or {@link ChronoField#SECOND_OF_DAY SECOND_OF_DAY}.
 *
 * <p><b>Performance</b>. Although parsing date strings into {@link
 * java.time.temporal.TemporalAccessor} objects is slow, that's not because of matching them against
 * some date/time pattern, which actually is very fast. It is in what happens next, the assembly of
 * a {@code TemporalAccessor} object, that the cost is incurred. Therefore you shouldn't probably
 * worry about the number of {@link ParseAttempt} instances you deploy to beat the date strings into
 * submission. Only the {@code ParseAttempt} that succeeds will go on to create the {@code
 * TemporalAccessor} and wrap it into a {@code FuzzyDate}.
 *
 * <p>Example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * ParseAttempt try0 = new ParseAttempt("yyyy-MM-dd HH:mm:ss");
 * ParseAttempt try1 = new ParseAttempt("yyyy-MM-dd");
 * FuzzyDateParser parser = new FuzzyDateParser(try0, try1);
 * String dateString = "2020-09-18";
 * FuzzyDate fuzzyDate = parser.parse(dateString);
 * assertTrue(fuzzyDate.isTimeFuzzy());
 * assertFalse(fuzzyDate.isDateFuzzy());
 * assertSame(LocalDate.class, fuzzyDate.bestMatch().getClass());
 * assertSame(try1, fuzzyDate.getParseAttempt());
 * OffsetDateTime realDate = fuzzyDate.toOffsetDateTime();
 * }</pre>
 *
 * </blockquote>
 */
public final class FuzzyDate {

  private final TemporalAccessor ta;
  private final int year;
  private final String verbatim;
  private final ParseAttempt parseAttempt;

  FuzzyDate(TemporalAccessor ta, int year, String verbatim, ParseAttempt parseAttempt) {
    this.ta = ta;
    this.year = year;
    this.verbatim = verbatim;
    this.parseAttempt = parseAttempt;
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
   * Returns an {@code OptionalInt} containing the nanosecond of this {@code FuzzyDate} or an empty
   * {@code OptionalInt} if no nanosecond, microsecond or millisecond could be extracted from the
   * date string.
   *
   * @return The nanosecond of this {@code FuzzyDate}
   */
  public OptionalInt getNano() {
    return ta.isSupported(NANO_OF_SECOND)
        ? OptionalInt.of(ta.get(NANO_OF_SECOND))
        : ta.isSupported(MICRO_OF_SECOND)
            ? OptionalInt.of(ta.get(MICRO_OF_SECOND) * 1000)
            : ta.isSupported(MILLI_OF_SECOND)
                ? OptionalInt.of(ta.get(MILLI_OF_SECOND) * 1000 * 1000)
                : OptionalInt.empty();
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
   * Converts this {@code FuzzyDate} to a {@link LocalDateTime}. Month and day are set to 1 if
   * unknown. Hour, minute and second are set to 0 if unknown. The time zone is set to the specified
   * time zone if unknown.
   *
   * @param zone The {@code ZoneOffset} to use if no {@code ZoneOffset} could be extracted from the
   *     date string
   * @return The {@code Instant} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDateTime toLocalDateTime(ZoneOffset zone) {
    if (ta.getClass() == Year.class) {
      return ((Year) ta).atMonth(1).atDay(1).atStartOfDay();
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
   * The time zone is set to {@link ZoneOffset#UTC UTC} if unknown.
   *
   * @return The {@code LocalDate} most closely corresponding to this {@code FuzzyDate}
   */
  public LocalDate toLocalDate() {
    return toLocalDate(UTC);
  }

  /**
   * Converts this {@code FuzzyDate} to a {@link LocalDate}. Month and day are set to 1 if unknown.
   * The time zone is set to {@link ZoneOffset#UTC UTC} if unknown.
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
   * FuzzyDate}. This method will always return one of the following types:
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
   * <p>If the raw {@link TemporalAccessor} encapsulated by this {@code FuzzyDate} already is an
   * instance of one of these types, it will simply be returned as-is. If it is an instance of a
   * {@link ZonedDateTime}, it will be converted to an {@code OffsetDateTime}. Otherwise a subclass
   * of {@code TemporalAccessor} will be assembled whose granularity depends on the availability of
   * {@link ChronoField#MONTH_OF_YEAR MONTH_OF_YEAR}, {@link ChronoField#DAY_OF_MONTH DAY_OF_MONTH},
   * {@link ChronoField#HOUR_OF_DAY HOUR_OF_DAY}, {@link ChronoField#MINUTE_OF_HOUR MINUTE_OF_HOUR},
   * {@link ChronoField#SECOND_OF_MINUTE SECOND_OF_MINUTE}, {@link ChronoField#NANO_OF_SECOND
   * NANO_OF_SECOND}, {@link ZoneOffset} and {@link ZoneId}. More exotic date fields like {@link
   * ChronoField#DAY_OF_WEEK} are not considered.
   *
   * @return A date/time object whose type depends on the actual granularity of this {@code
   *     FuzzyDate}
   */
  public TemporalAccessor bestMatch(ZoneOffset timeZone) {
    if (isPresent(
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
   * Returns the {@link ParseAttempt} instance from which this {@code FuzzyDate} was created.
   *
   * @return The {@link ParseAttempt} instance from which this {@code FuzzyDate} was created
   */
  public ParseAttempt getParseAttempt() {
    return parseAttempt;
  }

  /**
   * Returns whether the date of this instance is fuzzy. Returns true if either month or day is
   * unknown, false if both are known.
   *
   * @return Whether the date of this instance is fuzzy
   */
  public boolean isDateFuzzy() {
    return !(ta.isSupported(MONTH_OF_YEAR) && ta.isSupported(DAY_OF_MONTH));
  }

  /**
   * Returns whether the time of this instance is fuzzy. Returns true if either the hour or the
   * minute is unknown, false if both are known. Seconds are ignored, so time is <i>not</i> regarded
   * as fuzzy if hour and minute are known but the second is not.
   *
   * @return Whether the date of this instance is fuzzy
   */
  public boolean isTimeFuzzy() {
    return !(ta.isSupported(HOUR_OF_DAY) && ta.isSupported(MINUTE_OF_HOUR));
  }

  /**
   * Returns whether the date or time is fuzzy. Equivalent to {@code isDateFuzzy() ||
   * isTimeFuzzy()}.
   *
   * @return Whether the date and/or time is fuzzy.
   */
  public boolean isFuzzy() {
    return isDateFuzzy() || isTimeFuzzy();
  }

  /**
   * Returns true if the {@code TemporalAccessor} encapsulated by this instance equals the @code
   * TemporalAccessor} encapsulated by the other instance, or if date fields listed in the comments
   * for this class are pair-wise equal.
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
        && getMonth().equals(other.getMonth())
        && getDay().equals(other.getDay())
        && getHour().equals(other.getHour())
        && getMinute().equals(other.getMinute())
        && getSecond().equals(other.getSecond())
        && getNano().equals(other.getNano())
        && getTimeZone().equals(other.getTimeZone());
  }

  /** Returns a hash code based on the date/time fields listed in the comments for this class. */
  @Override
  public int hashCode() {
    int hash = year;
    hash = hash * 31 + getMonth().orElse(0);
    hash = hash * 31 + getDay().orElse(0);
    hash = hash * 31 + getHour().orElse(-1);
    hash = hash * 31 + getMinute().orElse(-1);
    hash = hash * 31 + getNano().orElse(-1);
    hash = hash * 31 + (getTimeZone().isPresent() ? getTimeZone().get().getTotalSeconds() : -1);
    return hash;
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
