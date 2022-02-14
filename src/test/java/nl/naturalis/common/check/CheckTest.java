package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.*;

@SuppressWarnings({"rawtypes"})
public class CheckTest {

  @Test
  public void is00() {
    try {
      String foo = null;
      Check.that(foo, "foo").is(notNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must not be null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is01() {
    try {
      String foo = null;
      Check.that(foo, "foo").is((String s) -> s != null);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is02() {
    try {
      String foo = null;
      Check.that(foo, "foo").is(asObj(s -> s != null));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is03() {
    try {
      String foo = null;
      Check.that(foo, "foo").is((Predicate<String>) (s -> s != null));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is04() {
    try {
      String foo = null;
      Check.that(foo, "foo").is(asObj(Objects::nonNull));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is05() {
    try {
      String foo = null;
      Check.that(foo, "foo").is((Predicate<String>) Objects::nonNull);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: null", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void isNot00() {
    try {
      Check.that(List.of(), "list").isNot(empty());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("list must not be null or empty (was ListN[0])", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void is09() {
    try {
      Check.that("abc").is((String s) -> s.endsWith("xyz"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for String: abc", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void intIs00() {
    try {
      Check.that(3, "foo").is(even());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be even (was 3)", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void intIs01() {
    try {
      Check.that(3).is(asInt(i -> i != 3));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for int: 3", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void intIs02() {
    try {
      Check.that(3, "foo").is(asInt(i -> i != 3));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: 3", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  private static boolean notEqualsThree(int i) {
    return i != 3;
  }

  @Test
  public void intIs03() {
    try {
      Check.that(3, "foo").is(asInt(CheckTest::notEqualsThree));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: 3", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void intIs04() {
    try {
      Check.that(3, "foo").is((IntPredicate) (i -> i != 3));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: 3", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void relation00() {
    try {
      Check.that(7.5F, "zappa").is(greaterThan(), (short) 16);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zappa must be > 16 (was 7.5)", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void relation00a() {
    try {
      Check.that(7.5F, "zappa")
          .is(greaterThan(), (short) 16, "${name} had trouble with ${arg} and ${obj}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("zappa had trouble with 7.5 and 16", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void relation01() {
    Map<String, Object> map = new HashMap<>();
    map.put("Greeting", "HelloWorld");
    // Check.that(map).is((x, y) -> x.containsKey(y), "Hello World");
    // Check.that(map).is(Map::containsKey, "Greeting");
    Check.that(map).is((Map<String, Object> x, String y) -> x.containsKey(y), "Greeting");
    Check.that(map).is(objObj((x, y) -> x.containsKey(y)), "Greeting");
    Check.that(map).is(objObj(Map::containsKey), "Greeting");
    Check.that(map).is((Relation<Map<String, Object>, String>) Map::containsKey, "Greeting");
    assertTrue("Made it all the way to the end", true);
  }

  @Test
  public void isNot01() {
    try {
      Check.that(List.of(), "list").isNot(asObj(List::isEmpty));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for list: ListN[0]", e.getMessage());
      return;
    }
    fail("should not be here");
  }

  @Test
  public void isNot02() {
    try {
      Check.that(List.of(), "list").isNot(asObj(l -> l.isEmpty()));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for list: ListN[0]", e.getMessage());
      return;
    }
    fail("should not be here");
  }
}
