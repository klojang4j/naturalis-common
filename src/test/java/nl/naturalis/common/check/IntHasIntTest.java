package nl.naturalis.common.check;

import org.junit.Test;

import java.io.IOException;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntHasIntTest {

  @Test
  public void vanilla00() throws IOException {
    Check.that(-7, "foo").has(abs(), odd());
    Check.that(-7, "foo").has(abs(), "bar", odd());
    Check.that(-7, "foo").has(abs(), odd(), "A custom message");
    Check.that(-7, "foo").has(abs(), odd(), () -> new IOException());
    Check.that(-7, "foo").has(abs(), lt(), 10);
    Check.that(-7, "foo").has(abs(), "foo", lt(), 10);
    Check.that(-7, "foo").has(abs(), lt(), 10, "A custom message");
    Check.that(-7, "foo").has(abs(), lt(), 10, () -> new IOException());
  }

  @Test
  public void vanilla01() throws IOException {
    Check.that(-7, "foo").notHas(abs(), even());
    Check.that(-7, "foo").notHas(abs(), "bar", even());
    Check.that(-7, "foo").notHas(abs(), even(), "A custom message");
    Check.that(-7, "foo").notHas(abs(), even(), () -> new IOException());
    Check.that(-7, "foo").notHas(abs(), lt(), 5);
    Check.that(-7, "foo").notHas(abs(), "foo", lt(), 5);
    Check.that(-7, "foo").notHas(abs(), lt(), 5, "A custom message");
    Check.that(-7, "foo").notHas(abs(), lt(), 5, () -> new IOException());
  }

  @Test
  public void hasPredicate01() {
    try {
      Check.that(-7, "foo").has(abs(), even());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("abs(foo) must be even (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHasPredicate01() {
    try {
      Check.that(-7).notHas(abs(), odd());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("abs(int) must not be odd (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasPredicateCustomMsg00() {
    try {
      Check.that(-7).has(abs(), even(), "Test ${test} did not go as planned for ${type}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Test even did not go as planned for int", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasPredicateCustomMsg01() {
    try {
      Check.that(-7).notHas(abs(), odd(), "Test ${test} did not go as planned for ${arg}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Test odd did not go as planned for 7", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasNamePredicate00() {
    Check.that(-7, "foo").has(abs(), "bar", odd());
  }

  @Test
  public void notHasNamePredicate00() {
    Check.that(-7, "foo").notHas(abs(), "bar", even());
  }

  @Test
  public void hasNamePredicate01() {
    try {
      Check.that(7, "foo").has(i -> i + 3, "bar", negative());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo.bar must be negative (was 10)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHasNamePredicate01() {
    try {
      Check.that(7, "foo").notHas(i -> i + 3, "bar", positive());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo.bar must not be positive (was 10)", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void hasPredicateCustomExc00() throws IOException {
    Check.that(-7).has(abs(), i -> i > 10, () -> new IOException());
  }

  @Test(expected = IOException.class)
  public void hasPredicateCustomExc01() throws IOException {
    Check.that(-7, "foo").notHas(abs(), i -> i == 7, () -> new IOException());
  }

  @Test
  public void hasIntRelation00() {
    Check.that(7).has(i -> i + 3, lt(), 100);
  }

  @Test
  public void hasIntRelation01() {
    Check.that(7).notHas(i -> i + 3, lt(), 5);
  }

  @Test
  public void hasNameIntRelation00() {
    try {
      Check.that(7, "foo").has(i -> i + 3, "bar", gt(), 100);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo.bar must be > 100 (was 10)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasNameIntRelation01() {
    try {
      Check.that(7, "foo").notHas(i -> i + 3, "bar", gt(), 5);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo.bar must not be > 5 (was 10)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasIntRelationCustomMsg00() {
    try {
      Check.that(7).has(i -> i + 3, gt(), 100, "Oops: ${type} ${arg} was invalid");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Oops: int 10 was invalid", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void hasIntRelationCustomMsg01() {
    try {
      Check.that(7).notHas(i -> i + 3, gt(), 5, "This number is fun: ${arg}${arg}${arg}${obj}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("This number is fun: 1010105", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void hasIntRelationCustomExc00() throws IOException {
    Check.that(7).has(i -> i + 3, gt(), 100, () -> new IOException());
  }

  @Test(expected = IOException.class)
  public void hasIntRelationCustomExc01() throws IOException {
    Check.that(7).notHas(i -> i + 3, gt(), 5, () -> new IOException());
  }

  @Test
  public void testLambdas() {
    Check.that(-7).has(intToInt(i -> Math.abs(i)), i -> i % 2 == 1);
    Check.that(-7).has(intToInt(i -> Math.abs(i)), asInt(i -> i % 2 == 1));
    Check.that(-7).has(i -> Math.abs(i), asInt(i -> i % 2 == 1));
  }
}
