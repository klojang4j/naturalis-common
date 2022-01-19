package nl.naturalis.common.check;

import nl.naturalis.common.Sizeable;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.ArrayMethods.EMPTY_STRING_ARRAY;
import static nl.naturalis.common.Emptyable.EMPTY_INSTANCE;
import static nl.naturalis.common.Emptyable.NON_EMPTY_INSTANCE;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;
import static org.junit.Assert.*;

public class CommonChecksTest {

  @Test(expected = IllegalArgumentException.class)
  public void NULL01() {
    Check.that(new Object()).is(NULL());
  }

  @Test(expected = IllegalArgumentException.class)
  public void NULL02() {
    Check.that("Hello, world").is(NULL());
    assertTrue(true);
  }

  @Test
  public void NULL03() {
    Check.that(null).is(NULL());
  }

  @Test(expected = IllegalArgumentException.class)
  public void NULL04() {
    // Yes, this one will work, too
    Check.that(1).is(NULL());
  }

  @Test(expected = IllegalArgumentException.class)
  public void notNull01() {
    Check.that(null).is(notNull());
    assertTrue(true);
  }

  @Test
  public void notNull02() {
    Check.that(new Object()).is(notNull());
    assertTrue(true);
  }

  @Test
  public void notNull03() {
    Check.that(Optional.empty()).is(notNull());
    assertTrue(true);
  }

  @Test
  public void notNull05() {
    Check.that(42).is(notNull());
    assertTrue(true);
  }

  @Test
  public void yes01() {
    Check.that(true).is(yes());
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void yes02() {
    Check.that(false).is(yes());
  }

  @Test
  public void no01() {
    Check.that(false).is(no());
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void no02() {
    Check.that(true).is(no());
  }

  @Test
  public void empty01() {
    Check.that(null).is(empty());
    Check.that("").is(empty());
    Check.that(EMPTY_INSTANCE).is(empty());
    Check.that(Optional.empty()).is(empty());
    Check.that(Optional.of("")).is(empty());
    Check.that(List.of()).is(empty());
    Check.that(Set.of()).is(empty());
    Check.that(Map.of()).is(empty());
    Check.that(EMPTY_OBJECT_ARRAY).is(empty());
    Check.that(EMPTY_STRING_ARRAY).is(empty());
    Check.that(new char[0]).is(empty());
    Check.that((Sizeable) () -> 0).is(empty());
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty02() {
    Check.that(NON_EMPTY_INSTANCE).is(empty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty03() {
    Check.that("foo").is(empty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty04() {
    Check.that(List.of("")).is(empty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty05() {
    Check.that(new byte[] {07}).is(empty());
  }
}
