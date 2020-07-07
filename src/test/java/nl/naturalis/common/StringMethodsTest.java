package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringMethodsTest {

  @Test
  public void endsWith() {
    String s = "The cat is both dead and alive";
    assertTrue("01", StringMethods.endsWith(s, true, "ALIVE", "test"));
    assertTrue("02", StringMethods.endsWith(s, true, "test", "ALIVE"));
    assertTrue("03", StringMethods.endsWith(s, true, "test", "a", "b", "ALIVE", "c"));
    assertFalse("04", StringMethods.endsWith(s, false, "DEAD", "ALIVE"));
    assertFalse("05", StringMethods.endsWith(s, true, "dead", "and"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void endsWith2() {
    String s = "The cat is both dead and alive";
    assertTrue("01", StringMethods.endsWith(s, true, "", "both"));
  }

  @Test
  public void substr_3_args() {
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
  public void substr_2_args() {
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
  public void zpad_3_args() {
    assertEquals("01", "00whatever|", StringMethods.zpad("whatever", 10, "|"));
    assertEquals("02", "00whatever|||", StringMethods.zpad("whatever", 10, "|||"));
    assertEquals("03", "0whatever|", StringMethods.zpad("whatever", 9, "|"));
    assertEquals("04", "whatever|", StringMethods.zpad("whatever", 8, "|"));
    assertEquals("05", "whatever", StringMethods.zpad("whatever", 7));
    assertEquals("06", "whatever", StringMethods.zpad("whatever", 6));
    String hour = StringMethods.zpad(7, 2, ":");
    String minute = StringMethods.zpad(38, 2, ":");
    String sec = StringMethods.zpad(6, 2);
    String time = hour + minute + sec;
    assertEquals("07", "07:38:06", time);
  }

  @Test
  public void zpad_2_args() {
    assertEquals("01", "00whatever", StringMethods.zpad("whatever", 10));
    assertEquals("02", "0whatever", StringMethods.zpad("whatever", 9));
    assertEquals("03", "whatever", StringMethods.zpad("whatever", 8));
    assertEquals("04", "whatever", StringMethods.zpad("whatever", 7));
    assertEquals("05", "whatever", StringMethods.zpad("whatever", 6));
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
    assertEquals("TheCatIsBoth", StringMethods.rchop(s, true, "dead", "and", "alive", "but", "more", "than"));
  }

  @Test
  public void rtrim() {
    assertEquals("01 Should be empty.", "", StringMethods.ltrim(null, 'a'));
    assertEquals("02 Should be empty.", "", StringMethods.rtrim("", 'a'));
    assertEquals("03 Should be empty.", "", StringMethods.rtrim("a", 'a'));
    assertEquals("04 Should be empty.", "", StringMethods.rtrim("aa", 'a'));
    assertEquals("05 Should be empty.", "", StringMethods.rtrim("aaa", 'a'));
    assertEquals("06 Should be \"b\".", "b", StringMethods.rtrim("b", 'a'));
    assertEquals("07 Should be \"b\".", "b", StringMethods.rtrim("baaa", 'a'));
    assertEquals("08 Should be \"bb\".", "bb", StringMethods.rtrim("bbaaa", 'a'));
    assertEquals("09 Should be \"bb\".", "bb", StringMethods.rtrim("bb", 'a'));
    assertEquals("10 Should be \"abb\".", "abb", StringMethods.rtrim("abb", 'a'));
    assertEquals("11 Should be \"aabb\".", "aabb", StringMethods.rtrim("aabb", 'a'));
  }

  @Test
  public void lchop() {
    String s = "TheCatIsBothDeadAndAliveButMoreDeadThanAlive";
    assertEquals("BothDeadAndAliveButMoreDeadThanAlive", StringMethods.lchop(s, true, "the", "cat", "is"));
    assertEquals("BothDeadAndAliveButMoreDeadThanAlive", StringMethods.lchop(s, true, "dog", "is", "the", "cat"));
    assertEquals("", StringMethods.lchop(s, true, "dog", "is", "the", "cat", "both", "dead", "and", "alive", "more", "BUT", "than"));
    assertEquals("", StringMethods.lchop(s, true, "TheCatIsBothDeadAndAliveButMoreDeadThanAlive", ""));
    assertEquals("TheCatIsBothDeadAndAliveButMoreDeadThanAlive", StringMethods.lchop(s, true, "", ""));
  }

}
