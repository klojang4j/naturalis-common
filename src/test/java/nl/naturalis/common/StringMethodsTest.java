package nl.naturalis.common;

import org.junit.Test;

import static nl.naturalis.common.StringMethods.*;
import static org.junit.Assert.*;

public class StringMethodsTest {

  @Test
  public void fromToIndex00() {
    String s = "012";
    assertEquals("", s.substring(2, 2));
  }

  @Test
  public void fromToIndex01() {
    String s = "012";
    // One position past the end of the string - still allowed with subXXXXX methods in the JDK
    assertEquals("", s.substring(3, 3));
  }

  @Test
  public void fromToIndex02() {
    String s = "012";
    assertEquals("", s.substring(3));
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void fromToIndex03() {
    String s = "012";
    s.substring(3, 4);
  }

  @Test
  public void count01() {
    String s = "This is This is This is BLISS!";
    assertEquals("01", 3, count(s, "This is"));
    assertEquals("02", 6, count(s, "is"));
    assertEquals("03", 7, count(s, "is", true));
    s = "aaaaaa";
    assertEquals("03", 5, count(s, "aa"));
  }

  @Test
  public void countDiscrete01() {
    String s = "This is This is This is BLISS!";
    assertEquals("01", 3, countDiscrete(s, "This is"));
    assertEquals("02", 6, countDiscrete(s, "is"));
    assertEquals("03", 7, countDiscrete(s, "is", true));
    s = "aaaaaa";
    assertEquals("03", 3, countDiscrete(s, "aa"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void duration01() {
    String s = interval(1000L, 10001L);
    assertEquals("01", "00:00:09.001", s);
    s = interval(1000L, 40080L);
    assertEquals("02", "00:00:39.080", s);
    s = interval(1000L, 3641689L);
    assertEquals("03", "01:00:40.689", s);
    s = interval(1000L, 123641689L);
    assertEquals("04", "34:20:40.689", s);
    s = interval(1000L, 5123641689L);
    assertEquals("05", "1423:14:00.689", s);
    s = interval(1000L, 995123641689L);
    assertEquals("05", "276423:14:00.689", s);
    s = interval(1000L, 1000L);
    assertEquals("05", "00:00:00.000", s);
    s = interval(9000L, 1000L); // Negative time interval
  }

  @Test
  public void ellipsis_01() {
    String hello = "Hello World, how are you?";
    assertEquals("Hello W...", ellipsis(hello, 10));
    assertEquals("H...", ellipsis(hello, 4));
    assertEquals("Hello World, how are you?", ellipsis(hello, 100));
  }

  @Test(expected = IllegalArgumentException.class)
  public void ellipsis_02() {
    // maxWidth must be greater than the length of the ellipsis dots (3)
    assertEquals("Hello W...", ellipsis("Hello World, how are you?", 3));
  }

  @Test
  public void endsWith_01() {
    String s = "The cat is both dead and alive";
    assertTrue("01", null != endsWith(s, true, "ALIVE", "test"));
    assertTrue("02", null != endsWith(s, true, "test", "ALIVE"));
    assertTrue("03", null != endsWith(s, true, "test", "a", "b", "ALIVE", "c"));
    assertTrue("04", null == endsWith(s, false, "DEAD", "ALIVE"));
    assertTrue("05", null == endsWith(s, true, "dead", "and"));
  }

  @Test
  public void substr_2args01() {
    assertEquals("05", "ever", substring("whatever", -4));
    assertEquals("05", "ever", substring("whatever", 4));
    assertEquals("06", "tever", substring("whatever", -5));
    assertEquals("07", "ver", substring("whatever", 5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr_2args02() {
    substring("", -1);
  }

  @Test
  public void substr_3args01() {
    assertEquals("01", "", substring("", 0, 0));
    assertEquals("02", "what", substring("whatever", 0, 4));
    assertEquals("03", "ever", substring("whatever", -4, 4));
    assertEquals("04", "eve", substring("whatever", -4, 3));
    assertEquals("05", "e", substring("whatever", -4, 1));
    assertEquals("06", "e", substring("whatever", 4, 1));
    assertEquals("07", "", substring("whatever", 0, 0));
    assertEquals("08", "", substring("whatever", 1, 0));
    assertEquals("09", "", substring("whatever", 7, 0));
    assertEquals("10", "r", substring("whatever", 7, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr_3args02() {
    substring("whatever", 250, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr_3args03() {
    substring("whatever", -4, 250);
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr_3args04() {
    substring("whatever", -250, 4);
  }

  @Test
  public void substr_3args05() {
    assertEquals("01", "w", substring("whatever", 0, -1));
    assertEquals("02", "h", substring("whatever", 1, -1));
    assertEquals("03", "ha", substring("whatever", 2, -2));
    assertEquals("04", "hate", substring("whatever", 4, -4));
    assertEquals("05", "r", substring("whatever", 7, -1));
    assertEquals("06", "r", substring("whatever", -1, -1));
    assertEquals("07", "er", substring("whatever", -1, -2));
    assertEquals("08", "eve", substring("whatever", -2, -3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr_3args06() {
    substring("whatever", 2, -100);
  }

  @Test
  public void ltrim_one_char() {
    assertEquals("01", "", ltrim(null, 'a'));
    assertEquals("02", "", ltrim("", 'a'));
    assertEquals("03", "", ltrim("a", 'a'));
    assertEquals("04", "", ltrim("aa", 'a'));
    assertEquals("05", "", ltrim("aaa", 'a'));
    assertEquals("06", "b", ltrim("b", 'a'));
    assertEquals("07", "b", ltrim("aaab", 'a'));
    assertEquals("08", "bb", ltrim("aaabb", 'a'));
    assertEquals("09", "bb", ltrim("bb", 'a'));
    assertEquals("10", "bba", ltrim("bba", 'a'));
    assertEquals("11", "bbaa", ltrim("bbaa", 'a'));
  }

  @Test
  public void ltrim_string() {
    assertEquals("01", "", ltrim(null, "a"));
    assertEquals("02", "", ltrim("", "abc"));
    assertEquals("03", "", ltrim("a", "abc"));
    assertEquals("04", "", ltrim("ab", "abc"));
    assertEquals("05", "", ltrim("abc", "abc"));
    assertEquals("06", "", ltrim("acb", "abc"));
    assertEquals("07", "db", ltrim("adb", "abc"));
    assertEquals("07", "da", ltrim("abcda", "abc"));
  }

  @Test
  public void rchop01() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals("TheCatIsBoth", rchop(s, true, "dead", "and", "alive", "but", "more", "than"));
  }

  @Test
  public void rtrim01() {
    assertEquals("01", "", ltrim(null, 'a'));
    assertEquals("02", "", rtrim("", 'a'));
    assertEquals("03", "", rtrim("a", 'a'));
    assertEquals("04", "", rtrim("aa", 'a'));
    assertEquals("05", "", rtrim("aaa", 'a'));
    assertEquals("06", "b", rtrim("b", 'a'));
    assertEquals("07", "b", rtrim("baaa", 'a'));
    assertEquals("08", "bb", rtrim("bbaaa", 'a'));
    assertEquals("09", "bb", rtrim("bb", 'a'));
    assertEquals("10", "abb", rtrim("abb", 'a'));
    assertEquals("11", "aabb", rtrim("aabb", 'a'));
  }

  @Test
  public void rtrim02() {
    assertEquals("01", "", ltrim(null, "a"));
    assertEquals("02", "", rtrim("", "a"));
    assertEquals("03", "", rtrim("a", "a"));
    assertEquals("04", "", rtrim("aab", "ab"));
    assertEquals("05", "", rtrim("aab", "ba"));
    assertEquals("06", "", rtrim("aab", "cba"));
    assertEquals("07", "aabd", rtrim("aabd", "cba"));
    assertEquals("08", "aab", rtrim("aabdef", "fde"));
    assertEquals("09", "a", rtrim("abdef", "gfdeb"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim03() {
    assertEquals("01", "", rtrim("a", null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim04() {
    assertEquals("01", "", rtrim("a", ""));
  }

  @Test
  public void lchop01() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals("BothDeadAndAliveButMoreDeadThanAlive", lchop(s, true, "the", "cat", "is"));
    assertEquals("BothDeadAndAliveButMoreDeadThanAlive", lchop(s, true, "dog", "is", "the", "cat"));
    assertEquals(
        "",
        lchop(
            s, true, "dog", "is", "the", "cat", "both", "dead", "and", "alive", "more", "BUT",
            "than"));
    assertEquals("", lchop(s, true, "TheCatIsBothDeadAndAliveButMoreDeadThanAlive", ""));
    assertEquals("TheCatIsBothDeadAndAliveButMoreDeadThanAlive", lchop(s, true, "", ""));
  }

  @Test
  public void lpad01() {
    assertEquals("01", "hello", lpad("hello", 5));
    assertEquals("02", " hello", lpad("hello", 6));
    assertEquals("03", "  hello", lpad("hello", 7));
    assertEquals("04", "       ", lpad("", 7));
    assertEquals("05", "       ", lpad(null, 7));
    assertEquals("06", "hello", lpad("hello", 0));
  }

  @Test
  public void lpad02() {
    assertEquals("01", "hello|", lpad("hello", 5, '.', "|"));
    assertEquals("02", ".hello|", lpad("hello", 6, '.', "|"));
    assertEquals("03", "..hello|", lpad("hello", 7, '.', "|"));
    assertEquals("04", ".......|", lpad("", 7, '.', "|"));
    assertEquals("05", ".......|", lpad(null, 7, '.', "|"));
    assertEquals("06", "hello|", lpad("hello", 0, '.', "|"));
  }

  @Test
  public void rpad01() {
    assertEquals("01", "hello", rpad("hello", 5));
    assertEquals("02", "hello ", rpad("hello", 6));
    assertEquals("03", "hello  ", rpad("hello", 7));
    assertEquals("04", "       ", rpad("", 7));
    assertEquals("05", "       ", rpad(null, 7));
    assertEquals("06", "hello", rpad("hello", 0));
  }

  @Test
  public void rpad02() {
    assertEquals("01", "hello|", rpad("hello", 5, '.', "|"));
    assertEquals("02", "hello.|", rpad("hello", 6, '.', "|"));
    assertEquals("03", "hello..|", rpad("hello", 7, '.', "|"));
    assertEquals("04", ".......|", rpad("", 7, '.', "|"));
    assertEquals("05", ".......|", rpad(null, 7, '.', "|"));
    assertEquals("06", "hello|", rpad("hello", 0, '.', "|"));
  }

  @Test
  public void pad01() {
    assertEquals("01", "hello", pad("hello", 5));
    assertEquals("02", "hello ", pad("hello", 6));
    assertEquals("03", " hello ", pad("hello", 7));
    assertEquals("04", " hello  ", pad("hello", 8));
    assertEquals("05", "  hello  ", pad("hello", 9));
    assertEquals("06", "hello", pad("hello", 0));
  }

  @Test
  public void pad02() {
    assertEquals("01", "hello|", pad("hello", 5, '.', "|"));
    assertEquals("02", "hello.|", pad("hello", 6, '.', "|"));
    assertEquals("03", ".hello.|", pad("hello", 7, '.', "|"));
    assertEquals("04", ".......|", pad("", 7, '.', "|"));
    assertEquals("05", ".......|", pad(null, 7, '.', "|"));
    assertEquals("06", "hello|", pad("hello", 0, '.', "|"));
  }

  @Test
  public void substrFromAfter() {
    String input = "/home/john/tmp/test.html";
    assertEquals("01", "home/john/tmp/test.html", substrAfter(input, '/'));
    assertEquals("02", "home/john/tmp/test.html", substrAfter(input, "/"));
    assertEquals("03", "john/tmp/test.html", substrFrom(input, 'j'));
    assertEquals("04", "john/tmp/test.html", substrFrom(input, "john"));
    assertEquals("05", "ohn/tmp/test.html", substrAfter(input, 'j'));
    assertEquals("06", "/tmp/test.html", substrAfter(input, "john"));
    assertEquals("07", "l", substrFrom(input, 'l'));
    assertEquals("08", "html", substrFrom(input, "html"));
    assertEquals("09", "", substrAfter(input, 'l'));
    assertEquals("10", "", substrAfter(input, "html"));
    assertEquals("11", input, substrAfter(input, 'x'));
    assertEquals("12", input, substrAfter(input, "x"));
    assertEquals("13", "test.html", substrAfter(input, '/', true));
    assertEquals("14", "test.html", substrAfter(input, "/", true));
    assertEquals("15", "/test.html", substrFrom(input, '/', true));
    assertEquals("16", "/test.html", substrFrom(input, "/te", true));
  }

  @Test
  public void getLineAndColumn00() {
    String s = "To be\nOr not to be\nThat is the question\n Whether 't is nobler\nIn the mind";
    int idx = 0;
    assertArrayEquals(new int[] {0, 0}, getLineAndColumn(s, idx, "\n"));
    idx = s.indexOf("not");
    assertArrayEquals(new int[] {1, 3}, getLineAndColumn(s, idx, "\n"));
    idx = s.indexOf("is");
    assertArrayEquals(new int[] {2, 5}, getLineAndColumn(s, idx, "\n"));
    idx = s.indexOf("mind");
    assertArrayEquals(new int[] {4, 7}, getLineAndColumn(s, idx, "\n"));
    idx = s.indexOf("\n"); // hmmm ...
    assertArrayEquals(new int[] {0, 5}, getLineAndColumn(s, idx, "\n"));
  }

  @Test
  public void initCap00() {
    String s = initCap("helloWorld");
    assertEquals("HelloWorld", s);
    s = initCap(" helloWorld");
    assertEquals(" helloWorld", s);
  }

  @Test
  public void concat00() {
    assertEquals("There are 7 days in a week", concat("There are ", 7, ' ', "days in a ", "week"));
  }
}
