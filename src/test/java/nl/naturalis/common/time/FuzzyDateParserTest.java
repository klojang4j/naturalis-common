package nl.naturalis.common.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FuzzyDateParserTest {

  /*
   * These tests yield that, when parsing, you should use "d-M-uuuu" as date pattern rather than "dd-MM-uuuu", because "d-M-uuuu" will match
   * "12-04-2004", but "dd-MM-uuuu" will not match "2-4-2012"
   */
  @Test
  public void test1() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("d-M-uuuu", LocalDate::from);
    FuzzyDate parsed = parser.parse("12-04-2004");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertFalse("03", parsed.isFuzzyDate());
    assertEquals("04", LocalDate.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2004, 4, 12));
  }

  @Test
  public void test1b() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("d-M-uuuu", LocalDate::from);
    FuzzyDate parsed = parser.parse("22-7-2004");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertFalse("03", parsed.isFuzzyDate());
    assertEquals("04", LocalDate.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2004, 7, 22));
  }

  @Test
  public void test1c() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("d-M-uu", LocalDate::from);
    FuzzyDate parsed = parser.parse("3-03-04");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertFalse("03", parsed.isFuzzyDate());
    assertEquals("04", LocalDate.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2004, 3, 3));
  }

  @Test(expected = FuzzyDateException.class)
  public void test1d() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("dd-MM-uuuu", LocalDate::from);
    parser.parse("3-5-1904");
  }

  @Test(expected = FuzzyDateException.class)
  public void test1e() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("[[d-]M-]uuuu", LocalDate::from);
    parser.parse("3-03-04");
  }

  /*
   * Strangely, "04-2004" does not match "[[dd-]MM-]uuuu", but "2004" does.
   */
  @Test(expected = FuzzyDateException.class)
  public void test2() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("[[d-]M-]uuuu", LocalDate::from);
    parser.parse("04-2004");
  }

  @Test
  public void test3() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("uuuu", Year::from);
    FuzzyDate parsed = parser.parse("2004");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertTrue("03", parsed.isFuzzyDate());
    assertEquals("04", Year.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2004, 1, 1));
  }

  @Test
  public void test4() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("uuuu-M", YearMonth::from);
    FuzzyDate parsed = parser.parse("2004-04");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertTrue("03", parsed.isFuzzyDate());
    assertEquals("04", YearMonth.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2004, 4, 1));
  }

  @Test
  public void test5() throws FuzzyDateException {
    // 'Z' is just a string literal here!
    FuzzyDateParser parser = simpleParser("uuuu-M-d'T'HH:mm[:ss]'Z'", LocalDateTime::from);
    FuzzyDate parsed = parser.parse("2007-10-13T13:02Z");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertFalse("03", parsed.isFuzzyDate());
    assertEquals("04", LocalDateTime.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2007, 10, 13));
  }

  @Test
  public void test6() throws FuzzyDateException {
    FuzzyDateParser parser = simpleParser("uuuu-M-d'T'HH:mm[:ss]'Z'", LocalDateTime::from);
    FuzzyDate parsed = parser.parse("2007-10-13T13:02Z");
    assertNotNull("01", parsed);
    assertNotNull("02", parsed);
    assertFalse("03", parsed.isFuzzyDate());
    assertEquals("04", LocalDateTime.class, parsed.bestMatch().getClass());
    assertEquals("05", parsed.toLocalDate(), LocalDate.of(2007, 10, 13));
  }

  @Test
  public void test7() {
    DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:30+01:00:20", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:30+01:00:20", ta.toString());
  }

  /*
   * ISO_DATE_TIME also takes care of date strings ending in Z
   */
  @Test
  public void test7b() {
    DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:30Z", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:30Z", ta.toString());
  }

  @Test
  public void test8() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]X");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:22+0100", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:22+01:00", ta.toString());
  }

  /*
   * uuuu-M-d'T'HH:mm[:ss]X takes care of date strings ending in Z, but also date strings with zone offsets without colon like
   * 2011-10-03T10:15:22+0300
   */
  @Test
  public void test8b() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]X");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:22Z", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:22Z", ta.toString());
  }

  /*
   * Zone offset +0300
   */
  @Test
  public void test8c() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]X");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:22+0300", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:22+03:00", ta.toString());
  }

  /*
   * Zone offset +03; date string without seconds
   */
  @Test
  public void test8d() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]X");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:22+03", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15:22+03:00", ta.toString());
  }

  @Test
  public void test8e() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]X");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15+03", OffsetDateTime::from);
    assertEquals("2011-10-03T10:15+03:00", ta.toString());
  }

  @Test
  public void test9() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm[:ss]Z");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15+0100", OffsetDateTime::from);
    // Lesson: if seconds not present, then also not printed (not rounded to :00)
    assertEquals("2011-10-03T10:15+01:00", ta.toString());
  }

  @Test
  public void test10() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm:ss'a'");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:18a", LocalDateTime::from);
    assertEquals("2011-10-03T10:15:18", ta.toString());
  }

  @Test
  public void test10b() {
    DateTimeFormatter dtf = formatter("uuuu-M-d'T'HH:mm:ss'a'");
    TemporalAccessor ta = dtf.parse("2011-10-03T10:15:18a", LocalDate::from);
    assertEquals("2011-10-03", ta.toString());
  }

  @Test
  public void test11() {
    DateTimeFormatter dtf = formatter("uuuu[/M]");
    TemporalAccessor ta = dtf.parse("2011/10", Year::from);
    assertEquals("01", Year.class, ta.getClass());
  }

  @Test
  public void test12() throws FuzzyDateException {
    UnaryOperator<String> filter = new YearFilter();
    DateTimeFormatter formatter = formatter("uuuu");
    TemporalQuery<?>[] parseInto = new TemporalQuery[] {Year::from};
    List<ParseInfo> parseSpecs = Arrays.asList(new ParseInfo(filter, formatter, parseInto));
    FuzzyDateParser parser = new FuzzyDateParser(parseSpecs);
    FuzzyDate date = parser.parse("2008a");
    assertTrue("01", date.isFuzzyDate());
    assertEquals("02", Year.class, date.bestMatch().getClass());
    assertEquals("03", date.toLocalDate(), LocalDate.of(2008, 1, 1));
  }

  ////////////////////////////////////////////////////////////////
  // Tests with the "default" parser (using fuzzy-date.properties)
  ////////////////////////////////////////////////////////////////

  @Test(expected = FuzzyDateException.class)
  public void test100() throws FuzzyDateException {
    @SuppressWarnings("unused")
    FuzzyDate date = FuzzyDateParser.DEFAULT.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void test101() throws FuzzyDateException {
    @SuppressWarnings("unused")
    FuzzyDate date = FuzzyDateParser.DEFAULT.parse(null);
  }

  @Test(expected = FuzzyDateException.class)
  public void test102() throws FuzzyDateException {
    FuzzyDateParser.DEFAULT.parse("^&*");
  }

  @Test
  public void test103() throws FuzzyDateException {
    FuzzyDate date = FuzzyDateParser.DEFAULT.parse("2005");
    assertNotNull("01", date);
    assertEquals("02", Year.class, date.bestMatch().getClass());
    assertEquals("03", LocalDate.of(2005, 01, 01), date.toLocalDate());
  }

  @Test
  public void test104() throws FuzzyDateException {
    FuzzyDateParser.DEFAULT.parse("2005-02-29");
    // Default is to parse lenient (no FuzzyDateException expected)
  }

  @Test
  public void test105() throws FuzzyDateException {
    FuzzyDate date = FuzzyDateParser.DEFAULT.parse("05-May-2014");
    assertEquals("01", 5, date.toLocalDate().get(ChronoField.DAY_OF_MONTH));
    assertEquals("02", 5, date.toLocalDate().get(ChronoField.MONTH_OF_YEAR));
  }

  @Test
  public void test110() throws FuzzyDateException {
    FuzzyDate date = FuzzyDateParser.DEFAULT.parse("1996");
    assertEquals(LocalDate.of(1996, 1, 1), date.toLocalDate());
    date = FuzzyDateParser.DEFAULT.parse("May-1996");
    assertEquals(LocalDate.of(1996, 5, 1), date.toLocalDate());
    date = FuzzyDateParser.DEFAULT.parse("1996/1997");
    assertEquals(LocalDate.of(1996, 1, 1), date.toLocalDate());
  }

  ////////////////////////////////////////////////////////////////////////////////////
  // Miscellaneous aspects of the FyzzyDate library
  ////////////////////////////////////////////////////////////////////////////////////
  @Test
  public void test200() throws FuzzyDateException {

    ParseInfo ps0 = new ParseInfo("uuuu/MM/dd HH:mm:ss", LocalDateTime::from);
    ParseInfo ps1 = new ParseInfo("uuuu/MM/dd", LocalDate::from);
    ParseInfo ps2 = new ParseInfo("uuuu/MM/dd[ HH:mm:ss]", LocalDate::from);

    FuzzyDateParser parser = new FuzzyDateParser(ps0, ps1, ps2);
    FuzzyDate date = parser.parse("2018/08/14 13:42:33");
    assertEquals("01", ps0, date.getParseSpec());

    parser = new FuzzyDateParser(ps2, ps0, ps1);
    date = parser.parse("2018/08/14 13:42:33");
    assertEquals("02", ps2, date.getParseSpec());

    parser = new FuzzyDateParser(ps1, ps2, ps0);
    date = parser.parse("2018/08/14 13:42:33");
    assertEquals("03", ps2, date.getParseSpec());

    parser = new FuzzyDateParser(ps0, ps1, ps2);
    date = parser.parse("2018/08/14");
    assertEquals("04", ps1, date.getParseSpec());

    parser = new FuzzyDateParser(ps2, ps1, ps0);
    date = parser.parse("2018/08/14");
    assertEquals("05", ps2, date.getParseSpec());
  }

  private static FuzzyDateParser simpleParser(String pattern, TemporalQuery<?> parseInto) {
    List<ParseInfo> parseSpecs = new ArrayList<>(1);
    DateTimeFormatter formatter = formatter(pattern);
    TemporalQuery<?>[] into = new TemporalQuery[] {parseInto};
    parseSpecs.add(new ParseInfo(null, formatter, into));
    return new FuzzyDateParser(parseSpecs);
  }

  private static DateTimeFormatter formatter(String pattern) {
    return new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter();
  }
}
