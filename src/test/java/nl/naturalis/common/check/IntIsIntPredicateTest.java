package nl.naturalis.common.check;

import org.junit.Test;

import java.io.IOException;
import java.util.function.IntPredicate;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntIsIntPredicateTest {

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsIntPredicate00() { // Using asInt utility method
    Check.that(3).is(asInt(i -> i != 3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsIntPredicate01() { // Using cast
    Check.that(3, "foo").is((IntPredicate) i -> i != 3);
  }

  private static boolean notEqualsThree(int i) {
    return i != 3;
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsIntPredicate00() { // Using asInt utility method
    Check.that(3, "foo").is(asInt(IntIsIntPredicateTest::notEqualsThree));
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsIntPredicate01() { // Using cast
    Check.that(3, "foo").is((IntPredicate) IntIsIntPredicateTest::notEqualsThree);
  }

  @Test
  public void intPredicate00() {
    Check.that(4).is(even());
    Check.that(4).isNot(odd());
    Check.that(4).is(positive());
    Check.that(0).isNot(positive());
    Check.that(0).isNot(negative());
    Check.that(-3).is(negative());
    Check.that(-3).isNot(positive());
    Check.that(0).is(zero());
    Check.that(1).isNot(zero());
  }

  @Test
  public void intPredicate01() {
    try {
      Check.that(3, "foo").is(even());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be even (was 3)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate02() {
    try {
      Check.that(-2, "foo").is(odd());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be odd (was -2)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate03() {
    try {
      Check.that(-2, "foo").is(zero());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be 0 (was -2)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate04() {
    try {
      Check.that(0, "foo").isNot(zero());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must not be 0", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate05() {
    try {
      Check.that(0, "foo").is(positive());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be positive (was 0)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate06() {
    try {
      Check.that(1, "foo").is(negative());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be negative (was 1)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate07() { // short values also routed to IntCheck
    try {
      IntCheck check = Check.that((short) 1, "foo").is(negative());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be negative (was 1)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intPredicate08() { // byte values also routed to IntCheck
    IntCheck check = Check.that((byte) 1, "foo").is(positive());
    assertEquals(1, check.ok());
  }

  @Test
  public void intPredicate09() { // char values also routed to IntCheck
    IntCheck check = Check.that('a', "foo").is(positive());
    assertEquals(97, check.ok());
  }

  @Test(expected = IOException.class)
  public void intPredicate10() throws IOException {
    Check.on(IOException::new, -2, "foo").is(odd());
  }

  @Test(expected = IOException.class)
  public void intPredicate11() throws IOException {
    Check.that(-2, "foo").is(odd(), () -> new IOException());
  }

  @Test
  public void intPredicate12() {
    try {
      Check.that(1, "foo").is(negative(), "${name} failed ${check}() test. It was ${arg}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo failed negative() test. It was 1", e.getMessage());
      return;
    }
    fail();
  }
}
