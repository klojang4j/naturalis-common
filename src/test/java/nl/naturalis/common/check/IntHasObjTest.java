package nl.naturalis.common.check;

import org.junit.Test;

import java.time.DayOfWeek;

import static java.time.DayOfWeek.*;
import static nl.naturalis.common.check.CommonChecks.asObj;
import static nl.naturalis.common.check.CommonChecks.toObj;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntHasObjTest {

  private static final DayOfWeek[] days = DayOfWeek.class.getEnumConstants();

  @Test
  public void vanilla00() {
    Check.that(6).has(toObj(i -> days[i]), d -> d.equals(SUNDAY));
    Check.that(6).has(i -> days[i], asObj(d -> d.equals(SUNDAY)));
  }

  @Test
  public void vanilla01() {
    Check.that(6).notHas(toObj(i -> days[i]), d -> d.equals(SATURDAY));
  }

  @Test
  public void has_Predicate00() {
    try {
      Check.that(6, "shoe").has(toObj(i -> days[i]), d -> d.equals(MONDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for IntFunction.apply(shoe): SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Predicate01() {
    try {
      Check.that(6).has(toObj(i -> days[i]), d -> d.equals(MONDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for IntFunction.apply(int): SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Predicate00() {
    try {
      Check.that(6, "shoe").notHas(toObj(i -> days[i]), d -> d.equals(SUNDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for IntFunction.apply(shoe): SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Name_Predicate00() {
    try {
      Check.that(6, "shoe").has(toObj(i -> days[i]), "laces", d -> d.equals(MONDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for shoe.laces: SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Name_Predicate00() {
    try {
      Check.that(6, "shoe").notHas(toObj(i -> days[i]), "laces", d -> d.equals(SUNDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for shoe.laces: SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Name_Predicate0() {
    try {
      Check.that(6).notHas(toObj(i -> days[i]), "laces", d -> d.equals(SUNDAY));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Invalid value for laces: SUNDAY", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Predicate_CustomMsg00() {
    try {
      Check.that(6).has(toObj(i -> days[i]), d -> d.equals(MONDAY), "FOO");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("FOO", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Predicate_CustomMsg00() {
    try {
      Check.that(6).notHas(toObj(i -> days[i]), d -> d.equals(SUNDAY), "BAR");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("BAR", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void has_Predicate_CustomExc00() {
    Check.that(6)
        .has(toObj(i -> days[i]), d -> d.equals(MONDAY), () -> new UnsupportedOperationException());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void notHas_Predicate_CustomExc00() {
    Check.that(6)
        .notHas(
            toObj(i -> days[i]), d -> d.equals(SUNDAY), () -> new UnsupportedOperationException());
  }
}
