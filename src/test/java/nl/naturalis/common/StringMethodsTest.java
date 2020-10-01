package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.StringMethods.*;

public class StringMethodsTest {

  @Test
  public void count01() {
    String s = "This is This is This is BLISS!";
    assertEquals("01", 3, count(s, "This is"));
    assertEquals("02", 6, count(s, "is"));
    assertEquals("03", 7, count(s, "is", true));
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
  public void substr_3args() {
    assertEquals("01", "", substring(null, 0, 5));
    assertEquals("02", "", substring("", 0, -1));
    assertEquals("03", "", substring("whatever", 0, -1));
    assertEquals("04", "", substring("whatever", 1, 0));
    assertEquals("05", "", substring("whatever", 250, 3));
    assertEquals("06", "what", substring("whatever", 0, 4));
    assertEquals("07", "ever", substring("whatever", -4, 4));
    assertEquals("08", "ever", substring("whatever", -4, 250));
    assertEquals("09", "", substring("whatever", 200, 250));
    assertEquals("10", "eve", substring("whatever", -4, 3));
    assertEquals("11", "e", substring("whatever", -4, 1));
    assertEquals("12", "e", substring("whatever", 4, 1));
    assertEquals("13", "what", substring("whatever", -250, 4));
    assertEquals("14", "whatever", substring("whatever", -250, 1000));
  }

  @Test
  public void substr_2args() {
    assertEquals("01", "", substring(null, 0));
    assertEquals("02", "", substring(null, -3));
    assertEquals("03", "", substring(null, 500));
    assertEquals("04", "", substring("", -1));
    assertEquals("05", "ever", substring("whatever", -4));
    assertEquals("05", "ever", substring("whatever", 4));
    assertEquals("06", "tever", substring("whatever", -5));
    assertEquals("07", "ver", substring("whatever", 5));
    assertEquals("08", "", substring("what", 4));
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
}
