package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static java.time.DayOfWeek.*;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.CollectionMethods.newHashMap;
import static nl.naturalis.common.CollectionMethods.newArrayList;
import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.*;

public class ObjIsRelationTest {

  private static final Map<String, String> beatles =
      newHashMap(
          0,
          String.class,
          String.class,
          "john",
          "lennon",
          "paul",
          "mccartney",
          "george",
          "harrison",
          "guess who",
          "huh?");

  @Test(expected = IllegalArgumentException.class)
  public void relation00() {
    Check.that(Float.valueOf(7.5F)).is(GT(), 16F);
  }

  @Test
  public void relation01() {
    Map<String, Object> map = new HashMap<>();
    map.put("Greeting", "HelloWorld");
    Check.that(map).is((x, y) -> x.containsKey(y), "Greeting");
    Check.that(map).is(Map::containsKey, "Greeting");
    Check.that(map).is(hasKey(), "Greeting");
    Check.that(map).is((Map<String, Object> x, String y) -> x.containsKey(y), "Greeting");
    Check.that(map).is(objObj((x, y) -> x.containsKey(y)), "Greeting");
    Check.that(map).is(objObj(Map::containsKey), "Greeting");
    Check.that(map).is((Relation<Map<String, Object>, String>) Map::containsKey, "Greeting");
  }

  @Test
  public void relation02() {
    Check.that(String.class).is(extending(), CharSequence.class);
    Check.that("foo").is(instanceOf(), CharSequence.class);
    Check.that(CharSequence.class).isNot(instanceOf(), String.class);
    Check.that(Set.of("1", "2", "3")).is(contains(), "2");
    Check.that(Set.of("1", "2", "3")).isNot(contains(), "4");
    Check.that((Integer) 2).is(in(), List.of(1, 2, 3));
    Check.that((Integer) 4).isNot(in(), List.of(1, 2, 3));
    Check.that(Set.of("1", "2", "3")).is(supersetOf(), List.of("1", "2"));
    Check.that(Set.of("1", "4", "5")).isNot(supersetOf(), List.of("1", "2"));
    Check.that(Set.of(MONDAY, TUESDAY, WEDNESDAY))
        .is(subsetOf(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
    Check.that(Set.of(MONDAY, TUESDAY, SATURDAY))
        .isNot(subsetOf(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
    Map<Integer, Integer> map = Map.of(1, 1, 2, 4, 3, 6, 4, 8, 5, 10);
    Check.that(map).is(hasKey(), 1);
    Check.that(map).isNot(hasKey(), 11);
    Check.that(map).is(hasValue(), 4);
    Check.that(map).isNot(hasValue(), 7);
    Check.that((Integer) 5).is(keyIn(), map);
    Check.that((Integer) 7).isNot(valueIn(), map);
    Check.that((Integer) 7).is(elementOf(), pack(1, 7, 10));
    Check.that("Hello").is(EQ(), new String("Hello"));
    Check.that("Hello").isNot(sameAs(), new String("Hello"));
    Check.that("Hello").is(equalsIgnoreCase(), "HELLO");
    Check.that(null).is(nullOr(), Boolean.TRUE);
    Check.that(true).is(nullOr(), Boolean.TRUE);
    Check.that(7.23F).is(GT(), 2F);
    Check.that(7.230F).is(LTE(), 7.230F);
    Check.that((Short) (short) 17).is(LT(), (short) 31);
    Check.that((Short) (short) 17).is(GTE(), (short) 17);
    Check.that("ZZZ").is(GT(), "AAA");
    Check.that("hello").isNot(startsWith(), "foo");
    Check.that("hello").is(endsWith(), "lo");
    Check.that("hello").is(hasSubstring(), "lo");
    Check.that("abc").is(substringOf(), "abcde");
    Check.that("abc").is(substringOf(), "abc");
    Check.that("abc").isNot(substringOf(), "ab");
  }

  @Test
  public void EQ00() {
    try {
      Check.that("foo").is(EQ(), 7);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("String must equal 7 (was foo)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void EQ01() {
    try {
      Check.that("foo").isNot(EQ(), "foo");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("String must not equal foo", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void GT00() {
    try {
      Check.that("aaa", "foo").is(GT(), "bbb");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be > bbb (was aaa)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void GT01() {
    try {
      Check.that(9.0).is(GT(), 9.5);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Double must be > 9.5 (was 9.0)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void GTE00() {
    try {
      Check.that("aaa", "foo").is(GTE(), "bbb");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be >= bbb (was aaa)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void GTE01() {
    try {
      Check.that(9.0).is(GTE(), 9.5, "${arg} is not ${check} ${obj} (${0})", "sorry");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("9.0 is not GTE 9.5 (sorry)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void LT00() {
    try {
      Check.that("aaa", "zappa").isNot(LT(), "bbb");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zappa must be >= bbb (was aaa)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void LT01() {
    try {
      Check.that(9.7, "zappa").is(LT(), 9.5);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zappa must be < 9.5 (was 9.7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void LTE00() {
    try {
      Check.that(9.7F, "zorro").is(LTE(), 9.5F);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zorro must be <= 9.5 (was 9.7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void LTE01() {
    try {
      Check.that(WEDNESDAY, "zorro").isNot(LTE(), FRIDAY);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zorro must be > FRIDAY (was WEDNESDAY)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void sameAs00() {
    try {
      Check.that(9.7F, "siphon").is(sameAs(), 9.7D);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertTrue(e.getMessage().startsWith("siphon must be Double@"));
      assertTrue(e.getMessage().contains(" (was Float@"));
      assertTrue(e.getMessage().endsWith(")"));
      return;
    }
    fail();
  }

  @Test
  public void sameAs01() {
    try {
      Check.that(WEDNESDAY, "siphon").isNot(sameAs(), WEDNESDAY);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertTrue(e.getMessage().startsWith("siphon must not be DayOfWeek@"));
      return;
    }
    fail();
  }

  @Test
  public void nullOr00() {
    try {
      Check.that(9.7F, "mordor").is(nullOr(), 9.5F);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("mordor must be null or 9.5 (was 9.7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void nullOr01() {
    try {
      Check.that(null, "xavier").isNot(nullOr(), SUNDAY);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("xavier must not be null or SUNDAY (was null)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void instanceOf00() {
    try {
      Check.that(9.7F, "pipe").is(instanceOf(), String.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "pipe must be instance of java.lang.String (was java.lang.Float)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void instanceOf01() {
    try {
      Check.on(illegalState(), 9.7F, "pipe").isNot(instanceOf(), Float.class);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
      assertEquals("pipe must not be instance of java.lang.Float (was 9.7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending00() {
    try {
      Check.that(OutputStream.class, "babbage").is(extending(), OutputStream[].class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "babbage must extend java.io.OutputStream[] (was java.io.OutputStream)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending01() {
    try {
      Check.that(OutputStream.class, "babbage").is(extending(), Comparable.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "babbage must implement java.lang.Comparable (was java.io.OutputStream)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending02() {
    try {
      Check.that(String.class, "babbage").isNot(extending(), CharSequence.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "babbage must not implement java.lang.CharSequence (was java.lang.String)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending03() {
    try {
      Check.that(Float.class, "babbage").isNot(extending(), Comparable.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "babbage must not implement java.lang.Comparable (was java.lang.Float)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending04() {
    try {
      Check.that(Comparable.class, "babbage").isNot(extending(), Comparable.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("babbage must not extend java.lang.Comparable", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void extending05() {
    try {
      Check.that(Float.class, "babbage").isNot(extending(), Float.class);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("babbage must not extend java.lang.Float", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void contains00() {
    try {
      List<String> names = newArrayList(0, "john", "paul", "george", "guess who");
      Check.on(io(), names, "poseidon").is(contains(), "ringo");
    } catch (IOException e) {
      System.out.println(e.getMessage());
      assertEquals("poseidon must contain ringo", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void contains01() {
    try {
      List<String> names = newArrayList(0, "john", "paul", "george", "guess who");
      Check.on(io(), names, "poseidon").isNot(contains(), "paul");
    } catch (IOException e) {
      System.out.println(e.getMessage());
      assertEquals("poseidon must not contain paul", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasKey00() {
    try {
      Map<String, String> map =
          newHashMap(
              0,
              String.class,
              String.class,
              "john",
              "lennon",
              "paul",
              "mccartney",
              "george",
              "harrison",
              "guess who",
              "huh?");
      Check.on(unsupportedOperation(), map, "thor").is(hasKey(), "ringo");
    } catch (UnsupportedOperationException e) {
      System.out.println(e.getMessage());
      assertEquals("thor must contain key ringo", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasKey01() {
    try {
      Map<String, String> map =
          newHashMap(
              0,
              String.class,
              String.class,
              "john",
              "lennon",
              "paul",
              "mccartney",
              "george",
              "harrison",
              "guess who",
              "huh?");
      Check.on(unsupportedOperation(), map, "thor").isNot(hasKey(), "john");
    } catch (UnsupportedOperationException e) {
      System.out.println(e.getMessage());
      assertEquals("thor must not contain key john", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasValue00() {
    try {
      Map<String, String> map =
          newHashMap(
              0,
              String.class,
              String.class,
              "john",
              "lennon",
              "paul",
              "mccartney",
              "george",
              "harrison",
              "guess who",
              "huh?");
      Check.that(map, "morpheus").is(hasValue(), "star");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("morpheus must contain value star", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasValue01() {
    try {
      Map<String, String> map =
          newHashMap(
              0,
              String.class,
              String.class,
              "john",
              "lennon",
              "paul",
              "mccartney",
              "george",
              "harrison",
              "guess who",
              "huh?");
      Check.that(map, "morpheus").isNot(hasValue(), "huh?");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("morpheus must not contain value huh?", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void in00() {
    try {
      List<String> names = newArrayList(0, "john", "paul", "george", "guess who");
      Check.that("ringo", "tetrapod").is(in(), names);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "tetrapod must be element of ArrayList[4] of [john, paul, george, guess who] (was ringo)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void in01() {
    try {
      List<String> names = newArrayList(0, "john", "paul", "george", "guess who");
      Check.that("paul", "tetrapod").isNot(in(), names);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "tetrapod must not be element of ArrayList[4] of [john, paul, george, guess who] (was paul)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void keyIn00() {
    try {
      Check.that("ringo", "flavius").is(keyIn(), beatles);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "flavius must be key in HashMap[4] of {george: harrison, john: lennon, paul: mccartney, gue...} (was ringo)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void keyIn01() {
    try {
      Check.that("john", "flavius").isNot(keyIn(), beatles);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "flavius must not be key in HashMap[4] of {george: harrison, john: lennon, paul: mccartney, gue...} (was john)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void valueIn00() {
    try {
      Check.that("star", "werner").is(valueIn(), beatles);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "werner must be value in HashMap[4] of {george: harrison, john: lennon, paul: mccartney, gue...} (was star)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void valueIn01() {
    try {
      Check.that("lennon", "werner").isNot(valueIn(), beatles);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "werner must not be value in HashMap[4] of {george: harrison, john: lennon, paul: mccartney, gue...} (was lennon)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void elementOf00() {
    try {
      Check.that("lennon", "tolstoy").is(elementOf(), pack("mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "tolstoy must be element of String[3] of [mccartney, harrisson, star] (was lennon)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void elementOf01() {
    try {
      Check.that("star", "tolstoy").isNot(elementOf(), pack("mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "tolstoy must not be element of String[3] of [mccartney, harrisson, star] (was star)",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void supersetOf00() {
    try {
      Check.that(List.of("mccartney", "harrisson", "lennon"), "frodo")
          .is(supersetOf(), List.of("mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "frodo must be superset ListN[3] of [mccartney, harrisson, star] "
              + "(was ListN[3] of [mccartney, harrisson, lennon])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void superset01() {
    try {
      Check.that(List.of("lennon", "mccartney", "harrisson", "star"), "frodo")
          .isNot(supersetOf(), List.of("mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "frodo must not be superset ListN[3] of [mccartney, harrisson, star] "
              + "(was ListN[4] of [lennon, mccartney, harrisson, star])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void subsetOf00() {
    try {
      Check.that(List.of("mccartney", "harrisson", "lennon"), "kremlin")
          .is(subsetOf(), List.of("mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "kremlin must be subset of ListN[3] of [mccartney, harrisson, star] "
              + "(was ListN[3] of [mccartney, harrisson, lennon])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void subsetOf01() {
    try {
      Check.that(List.of("lennon", "mccartney", "harrisson", "star"), "kremlin")
          .isNot(subsetOf(), List.of("lennon", "mccartney", "harrisson", "star"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "kremlin must not be subset of ListN[4] of [lennon, mccartney, harrisson, star] "
              + "(was ListN[4] of [lennon, mccartney, harrisson, star])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void substringOf01() {
    try {
      Check.that("xyz", "foo").is(substringOf(), "abcde");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be substring of \"abcde\" (was \"xyz\")", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void substringOf02() throws IOException {
    Check.that("xyz").is(substringOf(), "abcde", () -> new IOException());
  }

  @Test
  public void substringOf03() {
    try {
      Check.on(IOException::new, "xyz")
          .is(substringOf(), "abcde", "${arg} is not substring of ${obj}");
    } catch (IOException e) {
      System.out.println(e.getMessage());
      assertEquals("xyz is not substring of abcde", e.getMessage());
      return;
    }
    fail();
  }
}
