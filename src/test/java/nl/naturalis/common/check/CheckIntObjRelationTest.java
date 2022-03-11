package nl.naturalis.common.check;

import org.junit.Test;

import static nl.naturalis.common.ArrayMethods.ints;
import static nl.naturalis.common.CollectionMethods.initializeList;
import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckIntObjRelationTest {

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsIntObjRelation00() {
    Check.that(7).is((x, y) -> y.length() > x, "Foo");
  }

  @Test
  public void intObjRelation00() {
    Check.that(7).is(indexOf(), new float[10]);
    Check.that(7).is(listIndexOf(), initializeList("foo", 10));
    Check.that(7).is(strIndexOf(), "Hello, Sam");
    Check.that(7).is(intElementOf(), ints(3, 5, 7, 9));
  }

  @Test
  public void indexOf00() {
    try {
      Check.that(7, "pepsi").is(indexOf(), new Object[5]);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("pepsi must be >= 0 and < 5 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void indexOf01() {
    try {
      Check.that(7, "pepsi").isNot(indexOf(), new Object[10]);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("pepsi must be < 0 or >= 10 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void listIndexOf00() {
    try {
      Check.that(7, "cola").is(listIndexOf(), initializeList("foo", 5));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("cola must be >= 0 and < 5 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void listIndexOf01() {
    try {
      Check.that(7, "cola").isNot(listIndexOf(), initializeList("foo", 10));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("cola must be < 0 or >= 10 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void strIndexOf00() {
    try {
      Check.that(7, "corona").is(strIndexOf(), "Hello");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("corona must be >= 0 and < 5 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void strIndexOf01() {
    try {
      Check.that(7, "corona").isNot(strIndexOf(), "Hello, Sam");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("corona must be < 0 or >= 10 (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intElementOf00() {
    try {
      Check.that(7, "tapioka").is(intElementOf(), ints(3, 5, 9));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("tapioka must be element of int[3] of [3, 5, 9] (was 7)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void intElementOf01() {
    try {
      Check.that(7, "tapioka").isNot(intElementOf(), ints(3, 5, 7));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("tapioka must not be element of int[3] of [3, 5, 7] (was 7)", e.getMessage());
      return;
    }
    fail();
  }
}