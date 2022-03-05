package nl.naturalis.common.check;

import org.junit.Test;

import java.io.IOException;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;

public class IntIsIntRelation {

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsIntRelation() {
    Check.that(7).is(intInt((x, y) -> x > y), 9);
  }

  @Test
  public void intRelation00() {
    Check.that(9).is(gt(), 7);
    Check.that(9).is(gte(), 9);
    Check.that(9).is(eq(), 9);
    Check.that(9).is(ne(), 11);
    Check.that(9).is(lt(), 11);
    Check.that(11).is(lte(), 11);
  }

  @Test
  public void intRelation01() {
    try {
      Check.that(7).is(gt(), 9);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("int must be > 9 (was 7)", e.getMessage());
    }
  }

  @Test
  public void intRelation02() {
    try {
      Check.that(9, "foo").is(lt(), 7);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be < 7 (was 9)", e.getMessage());
    }
  }

  @Test
  public void intRelation03() {
    try {
      Check.on(IOException::new, 9, "foo").is(lte(), 8);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must be <= 8 (was 9)", e.getMessage());
    }
  }

  @Test
  public void intRelation04() {
    try {
      Check.on(IOException::new, 8, "foo")
          .is(gte(), 9, "${name} incorrect: ${arg}. Required: ${obj}");
    } catch (IOException e) {
      System.out.println(e.getMessage());
      assertEquals("foo incorrect: 8. Required: 9", e.getMessage());
    }
  }

  @Test
  public void intRelation05() {
    try {
      Check.that(7, "foo").is(ne(), 7);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must not be equal to 7", e.getMessage());
    }
  }

  @Test
  public void intRelation06() {
    try {
      Check.that(7, "foo").isNot(eq(), 7);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("foo must not be equal to 7", e.getMessage());
    }
  }
}
