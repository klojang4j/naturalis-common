package nl.naturalis.common;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class ResultTest {

  @Test
  public void get00() {
    var result = Result.of("Hi there");
    assertEquals("Hi there", result.get());
  }

  @Test(expected = NoSuchElementException.class)
  public void get01() {
    var result = Result.none();
    result.get();
  }

  @Test
  public void orElse00() {
    assertEquals("Hi there", Result.of("Hi there").orElse("Where are you?"));
    assertEquals("Where are you?", Result.none().orElse("Where are you?"));
  }

  @Test
  public void hashCode0() {
    assertEquals(42, Result.of(42).hashCode());
  }

  @Test
  public void toString00() {
    assertEquals("Result[Hi there]", Result.of("Hi there").toString());
    assertEquals("Result.none", Result.none().toString());
  }

}
