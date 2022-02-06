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
    assertEquals(3, count(s, "This is"));
    assertEquals(6, count(s, "is"));
    assertEquals(7, count(s, "is", true));
    s = "aaaaaa";
    assertEquals(5, count(s, "aa"));
  }

  @Test
  public void countDiscrete01() {
    String s = "This is This is This is BLISS!";
    assertEquals(3, countDiscrete(s, "This is"));
    assertEquals(6, countDiscrete(s, "is"));
    assertEquals(7, countDiscrete(s, "is", true));
    s = "aaaaaa";
    assertEquals(3, countDiscrete(s, "aa"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void duration01() {
    String s = interval(1000L, 10001L);
    assertEquals("00:00:09.001", s);
    s = interval(1000L, 40080L);
    assertEquals("00:00:39.080", s);
    s = interval(1000L, 3641689L);
    assertEquals("01:00:40.689", s);
    s = interval(1000L, 123641689L);
    assertEquals("34:20:40.689", s);
    s = interval(1000L, 5123641689L);
    assertEquals("1423:14:00.689", s);
    s = interval(1000L, 995123641689L);
    assertEquals("276423:14:00.689", s);
    s = interval(1000L, 1000L);
    assertEquals("00:00:00.000", s);
    s = interval(9000L, 1000L); // Negative time interval
  }

  @Test
  public void ellipsis01() {
    String hello = "Hello World, how are you?";
    assertEquals("Hello W...", ellipsis(hello, 10));
    assertEquals("H...", ellipsis(hello, 4));
    assertEquals("Hello World, how are you?", ellipsis(hello, 100));
  }

  @Test(expected = IllegalArgumentException.class)
  public void ellipsis02() {
    // maxWidth must be greater than the length of the ellipsis dots (3)
    assertEquals("Hello W...", ellipsis("Hello World, how are you?", 3));
  }

  @Test
  public void endsWith01() {
    String s = "The cat is both dead and alive";
    assertTrue(null != endsWith(s, true, "ALIVE", "test"));
    assertTrue(null != endsWith(s, true, "test", "ALIVE"));
    assertTrue(null != endsWith(s, true, "test", "a", "b", "ALIVE", "c"));
    assertTrue(null == endsWith(s, false, "DEAD", "ALIVE"));
    assertTrue(null == endsWith(s, true, "dead", "and"));
  }

  @Test
  public void substr00() {
    assertEquals("ever", substr("whatever", -4));
    assertEquals("ever", substr("whatever", 4));
    assertEquals("tever", substr("whatever", -5));
    assertEquals("ver", substr("whatever", 5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr01() {
    substr("", -1);
  }

  @Test
  public void substr02() {
    assertEquals("", substr("", 0, 0));
    assertEquals("what", substr("whatever", 0, 4));
    assertEquals("ever", substr("whatever", -4, 4));
    assertEquals("eve", substr("whatever", -4, 3));
    assertEquals("e", substr("whatever", -4, 1));
    assertEquals("e", substr("whatever", 4, 1));
    assertEquals("", substr("whatever", 0, 0));
    assertEquals("", substr("whatever", 1, 0));
    assertEquals("", substr("whatever", 7, 0));
    assertEquals("r", substr("whatever", 7, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr03() {
    substr("whatever", 250, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr04() {
    substr("whatever", -4, 250);
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr05() {
    substr("whatever", -250, 4);
  }

  @Test
  public void substr06() {
    assertEquals("w", substr("whatever", 0, -1));
    assertEquals("h", substr("whatever", 1, -1));
    assertEquals("ha", substr("whatever", 2, -2));
    assertEquals("hate", substr("whatever", 4, -4));
    assertEquals("r", substr("whatever", 7, -1));
    assertEquals("r", substr("whatever", -1, -1));
    assertEquals("er", substr("whatever", -1, -2));
    assertEquals("eve", substr("whatever", -2, -3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void substr07() {
    substr("whatever", 2, -100);
  }

  @Test
  public void indexOf00() {
    assertEquals(0, indexOf("012345678901234", "0", 1));
    assertEquals(0, indexOf("012345678901234", "012", 1));
    assertEquals(0, indexOf("012345678901234", "0123456", 1));

    assertEquals(10, indexOf("012345678901234", "0", 2));
    assertEquals(10, indexOf("012345678901234", "012", 2));
    assertEquals(-1, indexOf("012345678901234", "0123456", 2));

    assertEquals(20, indexOf("01234567890123456789012", "0", 3));
    assertEquals(20, indexOf("01234567890123456789012", "012", 3));
    assertEquals(-1, indexOf("01234567890123456789012", "0123", 3));

    assertEquals(9, indexOf("01234567890123456789012", "9", 1));
    assertEquals(9, indexOf("01234567890123456789012", "90", 1));

    assertEquals(-1, indexOf(null, "0123", 1));
    assertEquals(-1, indexOf("", "0123", 1));
    assertEquals(0, indexOf("0", "0", 1));
    assertEquals(0, indexOf("01", "01", 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void indexOf01() {
    indexOf("01345", null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void indexOf02() {
    indexOf("01345", "", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void indexOf03() {
    indexOf("01345", "1", 0);
  }

  @Test
  public void ltrim_char() {
    assertEquals("", ltrim(null, 'a'));
    assertEquals("", ltrim("", 'a'));
    assertEquals("", ltrim("a", 'a'));
    assertEquals("", ltrim("aa", 'a'));
    assertEquals("", ltrim("aaa", 'a'));
    assertEquals("b", ltrim("b", 'a'));
    assertEquals("b", ltrim("aaab", 'a'));
    assertEquals("bb", ltrim("aaabb", 'a'));
    assertEquals("bb", ltrim("bb", 'a'));
    assertEquals("bba", ltrim("bba", 'a'));
    assertEquals("bbaa", ltrim("bbaa", 'a'));
  }

  @Test
  public void ltrim_string() {
    assertEquals("", ltrim(null, "a"));
    assertEquals("", ltrim("", "abc"));
    assertEquals("", ltrim("a", "abc"));
    assertEquals("", ltrim("ab", "abc"));
    assertEquals("", ltrim("abc", "abc"));
    assertEquals("", ltrim("acb", "abc"));
    assertEquals("db", ltrim("adb", "abc"));
    assertEquals("da", ltrim("abcda", "abc"));
  }

  @Test
  public void rchop01() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals("TheCatIsBoth", rchop(s, true, "dead", "and", "alive", "but", "more", "than"));
  }

  @Test
  public void rtrim00() {
    assertEquals("", ltrim(null, 'a'));
    assertEquals("", rtrim("", 'a'));
    assertEquals("", rtrim("a", 'a'));
    assertEquals("", rtrim("aa", 'a'));
    assertEquals("", rtrim("aaa", 'a'));
    assertEquals("b", rtrim("b", 'a'));
    assertEquals("b", rtrim("baaa", 'a'));
    assertEquals("bb", rtrim("bbaaa", 'a'));
    assertEquals("bb", rtrim("bb", 'a'));
    assertEquals("abb", rtrim("abb", 'a'));
    assertEquals("aabb", rtrim("aabb", 'a'));
  }

  @Test
  public void rtrim01() {
    assertEquals("", ltrim(null, "a"));
    assertEquals("", rtrim("", "a"));
    assertEquals("", rtrim("a", "a"));
    assertEquals("", rtrim("aab", "ab"));
    assertEquals("", rtrim("aab", "ba"));
    assertEquals("", rtrim("aab", "cba"));
    assertEquals("aabd", rtrim("aabd", "cba"));
    assertEquals("aab", rtrim("aabdef", "fde"));
    assertEquals("a", rtrim("abdef", "gfdeb"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim02() {
    assertEquals("", rtrim("a", null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim03() {
    assertEquals("", rtrim("a", ""));
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
    assertEquals("hello", lpad("hello", 5));
    assertEquals(" hello", lpad("hello", 6));
    assertEquals("  hello", lpad("hello", 7));
    assertEquals("       ", lpad("", 7));
    assertEquals("       ", lpad(null, 7));
    assertEquals("hello", lpad("hello", 0));
  }

  @Test
  public void lpad02() {
    assertEquals("hello|", lpad("hello", 5, '.', "|"));
    assertEquals(".hello|", lpad("hello", 6, '.', "|"));
    assertEquals("..hello|", lpad("hello", 7, '.', "|"));
    assertEquals(".......|", lpad("", 7, '.', "|"));
    assertEquals(".......|", lpad(null, 7, '.', "|"));
    assertEquals("hello|", lpad("hello", 0, '.', "|"));
  }

  @Test
  public void rpad01() {
    assertEquals("hello", rpad("hello", 5));
    assertEquals("hello ", rpad("hello", 6));
    assertEquals("hello  ", rpad("hello", 7));
    assertEquals("       ", rpad("", 7));
    assertEquals("       ", rpad(null, 7));
    assertEquals("hello", rpad("hello", 0));
  }

  @Test
  public void rpad02() {
    assertEquals("hello|", rpad("hello", 5, '.', "|"));
    assertEquals("hello.|", rpad("hello", 6, '.', "|"));
    assertEquals("hello..|", rpad("hello", 7, '.', "|"));
    assertEquals(".......|", rpad("", 7, '.', "|"));
    assertEquals(".......|", rpad(null, 7, '.', "|"));
    assertEquals("hello|", rpad("hello", 0, '.', "|"));
  }

  @Test
  public void pad01() {
    assertEquals("hello", pad("hello", 5));
    assertEquals("hello ", pad("hello", 6));
    assertEquals(" hello ", pad("hello", 7));
    assertEquals(" hello  ", pad("hello", 8));
    assertEquals("  hello  ", pad("hello", 9));
    assertEquals("hello", pad("hello", 0));
  }

  @Test
  public void pad02() {
    assertEquals("hello|", pad("hello", 5, '.', "|"));
    assertEquals("hello.|", pad("hello", 6, '.', "|"));
    assertEquals(".hello.|", pad("hello", 7, '.', "|"));
    assertEquals(".......|", pad("", 7, '.', "|"));
    assertEquals(".......|", pad(null, 7, '.', "|"));
    assertEquals("hello|", pad("hello", 0, '.', "|"));
  }

  @Test
  public void substrFrom00() {
    String input = "/home/john/tmp/test.html";
    assertEquals("john/tmp/test.html", substrFrom(input, "john"));
    assertEquals("html", substrFrom(input, "html"));
    assertEquals("/test.html", substrFrom(input, "/te", true));
  }

  @Test
  public void substrAfter00() {
    String input = "/home/john/tmp/test.html";
    assertEquals("home/john/tmp/test.html", substrAfter(input, "/"));
    assertEquals("/tmp/test.html", substrAfter(input, "john"));
    assertEquals("", substrAfter(input, "html"));
    assertEquals(input, substrAfter(input, "x"));
    assertEquals("test.html", substrAfter(input, "/", true));
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
