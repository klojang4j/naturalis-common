package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.DayOfWeek.*;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.*;

public class ObjIsRelationTest {

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
    Check.that(String.class).is(assignableTo(), CharSequence.class);
    Check.that("foo").is(instanceOf(), CharSequence.class);
    Check.that(CharSequence.class).isNot(instanceOf(), String.class);
    Check.that(Set.of("1", "2", "3")).is(contains(), "2");
    Check.that(Set.of("1", "2", "3")).isNot(contains(), "4");
    Check.that((Integer) 2).is(in(), List.of(1, 2, 3));
    Check.that((Integer) 4).isNot(in(), List.of(1, 2, 3));
    Check.that(Set.of("1", "2", "3")).is(containsAll(), List.of("1", "2"));
    Check.that(Set.of("1", "4", "5")).isNot(containsAll(), List.of("1", "2"));
    Check.that(Set.of(MONDAY, TUESDAY, WEDNESDAY))
        .is(allIn(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
    Check.that(Set.of(MONDAY, TUESDAY, SATURDAY))
        .isNot(allIn(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
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
    Check.that("hello").is(hasSubstr(), "lo");
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
      Check.that(null, "mordor").isNot(nullOr(), FRIDAY);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      // assertEquals("zorro must be > FRIDAY (was WEDNESDAY)", e.getMessage());
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
