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
    //    assertEquals("01", "hello", rpad("hello", 5));
    //    assertEquals("02", "hello ", rpad("hello", 6));
    //    assertEquals("03", "hello  ", rpad("hello", 7));
    //    assertEquals("04", "       ", rpad("", 7));
    assertEquals("05", "       ", rpad(null, 7));
    //    assertEquals("06", "hello", rpad("hello", 0));
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
}
