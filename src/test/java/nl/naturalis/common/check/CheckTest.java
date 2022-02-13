package nl.naturalis.common.check;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
  public void is06() {
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
  public void is07() {
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
  public void is08() {
    try {
      Check.that(List.of(), "list").isNot(asObj(l -> l.isEmpty()));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for list: ListN[0]", e.getMessage());
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
  public void intIs01() {
    try {
      Check.that(3).is(asInt(i -> i != 3));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      // assertEquals("Invalid value for foo: 3", e.getMessage());
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
  public void is101() {
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
  public void is102() {
    try {
      int foo = 3;
      Check.that(foo, "foo").is((IntPredicate) (i -> i != 3));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for foo: 3", e.getMessage());
      return;
    }
    fail("should not be here");
  }
}
