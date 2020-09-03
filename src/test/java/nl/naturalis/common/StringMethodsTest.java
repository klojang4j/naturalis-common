package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringMethodsTest {

  @Test
  public void count() {
    String s = "This is This is This is BLISS!";
    assertEquals("01", 3, StringMethods.count(s, "This is"));
    assertEquals("02", 6, StringMethods.count(s, "is"));
    assertEquals("03", 7, StringMethods.count(s, "is", true));
  }

  @Test
  public void endsWith_01() {
    String s = "The cat is both dead and alive";
    assertTrue("01", null != StringMethods.endsWith(s, true, "ALIVE", "test"));
    assertTrue("02", null != StringMethods.endsWith(s, true, "test", "ALIVE"));
    assertTrue("03", null != StringMethods.endsWith(s, true, "test", "a", "b", "ALIVE", "c"));
    assertTrue("04", null == StringMethods.endsWith(s, false, "DEAD", "ALIVE"));
    assertTrue("05", null == StringMethods.endsWith(s, true, "dead", "and"));
  }

  @Test
  public void substr_3args() {
    assertEquals("01", "", StringMethods.substr(null, 0, 5));
    assertEquals("02", "", StringMethods.substr("", 0, -1));
    assertEquals("03", "", StringMethods.substr("whatever", 0, -1));
    assertEquals("04", "", StringMethods.substr("whatever", 1, 0));
    assertEquals("05", "", StringMethods.substr("whatever", 250, 3));
    assertEquals("06", "what", StringMethods.substr("whatever", 0, 4));
    assertEquals("07", "ever", StringMethods.substr("whatever", -4, 4));
    assertEquals("08", "ever", StringMethods.substr("whatever", -4, 250));
    assertEquals("09", "", StringMethods.substr("whatever", 200, 250));
    assertEquals("10", "eve", StringMethods.substr("whatever", -4, 3));
    assertEquals("11", "e", StringMethods.substr("whatever", -4, 1));
    assertEquals("12", "e", StringMethods.substr("whatever", 4, 1));
    assertEquals("13", "what", StringMethods.substr("whatever", -250, 4));
    assertEquals("14", "whatever", StringMethods.substr("whatever", -250, 1000));
  }

  @Test
  public void substr_2args() {
    assertEquals("01", "", StringMethods.substr(null, 0));
    assertEquals("02", "", StringMethods.substr(null, -3));
    assertEquals("03", "", StringMethods.substr(null, 500));
    assertEquals("04", "", StringMethods.substr("", -1));
    assertEquals("05", "ever", StringMethods.substr("whatever", -4));
    assertEquals("05", "ever", StringMethods.substr("whatever", 4));
    assertEquals("06", "tever", StringMethods.substr("whatever", -5));
    assertEquals("07", "ver", StringMethods.substr("whatever", 5));
    assertEquals("08", "", StringMethods.substr("what", 4));
  }

  @Test
  public void ltrim_one_char() {
    assertEquals("01", "", StringMethods.ltrim(null, 'a'));
    assertEquals("02", "", StringMethods.ltrim("", 'a'));
    assertEquals("03", "", StringMethods.ltrim("a", 'a'));
    assertEquals("04", "", StringMethods.ltrim("aa", 'a'));
    assertEquals("05", "", StringMethods.ltrim("aaa", 'a'));
    assertEquals("06", "b", StringMethods.ltrim("b", 'a'));
    assertEquals("07", "b", StringMethods.ltrim("aaab", 'a'));
    assertEquals("08", "bb", StringMethods.ltrim("aaabb", 'a'));
    assertEquals("09", "bb", StringMethods.ltrim("bb", 'a'));
    assertEquals("10", "bba", StringMethods.ltrim("bba", 'a'));
    assertEquals("11", "bbaa", StringMethods.ltrim("bbaa", 'a'));
  }

  @Test
  public void ltrim_string() {
    assertEquals("01", "", StringMethods.ltrim(null, "a"));
    assertEquals("02", "", StringMethods.ltrim("", "abc"));
    assertEquals("03", "", StringMethods.ltrim("a", "abc"));
    assertEquals("04", "", StringMethods.ltrim("ab", "abc"));
    assertEquals("05", "", StringMethods.ltrim("abc", "abc"));
    assertEquals("06", "", StringMethods.ltrim("acb", "abc"));
    assertEquals("07", "db", StringMethods.ltrim("adb", "abc"));
    assertEquals("07", "da", StringMethods.ltrim("abcda", "abc"));
  }

  @Test
  public void rchop() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals(
        "TheCatIsBoth",
        StringMethods.rchop(s, true, "dead", "and", "alive", "but", "more", "than"));
  }

  @Test
  public void rtrim01() {
    assertEquals("01", "", StringMethods.ltrim(null, 'a'));
    assertEquals("02", "", StringMethods.rtrim("", 'a'));
    assertEquals("03", "", StringMethods.rtrim("a", 'a'));
    assertEquals("04", "", StringMethods.rtrim("aa", 'a'));
    assertEquals("05", "", StringMethods.rtrim("aaa", 'a'));
    assertEquals("06", "b", StringMethods.rtrim("b", 'a'));
    assertEquals("07", "b", StringMethods.rtrim("baaa", 'a'));
    assertEquals("08", "bb", StringMethods.rtrim("bbaaa", 'a'));
    assertEquals("09", "bb", StringMethods.rtrim("bb", 'a'));
    assertEquals("10", "abb", StringMethods.rtrim("abb", 'a'));
    assertEquals("11", "aabb", StringMethods.rtrim("aabb", 'a'));
  }

  @Test
  public void rtrim02() {
    assertEquals("01", "", StringMethods.ltrim(null, "a"));
    assertEquals("02", "", StringMethods.rtrim("", "a"));
    assertEquals("03", "", StringMethods.rtrim("a", "a"));
    assertEquals("04", "", StringMethods.rtrim("aab", "ab"));
    assertEquals("05", "", StringMethods.rtrim("aab", "ba"));
    assertEquals("06", "", StringMethods.rtrim("aab", "cba"));
    assertEquals("07", "aabd", StringMethods.rtrim("aabd", "cba"));
    assertEquals("08", "aab", StringMethods.rtrim("aabdef", "fde"));
    assertEquals("09", "a", StringMethods.rtrim("abdef", "gfdeb"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim03() {
    assertEquals("01", "", StringMethods.rtrim("a", null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rtrim04() {
    assertEquals("01", "", StringMethods.rtrim("a", ""));
  }

  @Test
  public void lchop() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals(
        "BothDeadAndAliveButMoreDeadThanAlive", StringMethods.lchop(s, true, "the", "cat", "is"));
    assertEquals(
        "BothDeadAndAliveButMoreDeadThanAlive",
        StringMethods.lchop(s, true, "dog", "is", "the", "cat"));
    assertEquals(
        "",
        StringMethods.lchop(
            s, true, "dog", "is", "the", "cat", "both", "dead", "and", "alive", "more", "BUT",
            "than"));
    assertEquals(
        "", StringMethods.lchop(s, true, "TheCatIsBothDeadAndAliveButMoreDeadThanAlive", ""));
    assertEquals(
        "TheCatIsBothDeadAndAliveButMoreDeadThanAlive", StringMethods.lchop(s, true, "", ""));
  }
}
