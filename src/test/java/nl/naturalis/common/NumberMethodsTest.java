package nl.naturalis.common;

import static nl.naturalis.common.NumberMethods.fitsInto;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumberMethodsTest {

  @Test
  public void fitsInto01() {
    assertTrue(fitsInto(Integer.MAX_VALUE, Float.class));
  }

  @Test
  public void fitsInto02() {
    assertTrue(fitsInto(Integer.MIN_VALUE, Float.class));
  }

  @Test
  public void fitsInto03() {
    assertFalse(fitsInto(Float.MAX_VALUE, Integer.class));
  }

  @Test
  public void fitsInto04() {
    assertFalse(fitsInto(Float.MIN_VALUE, Integer.class));
  }

  @Test
  public void fitsInto05() {
    assertTrue(fitsInto(3.00F, Integer.class));
  }

  @Test
  public void fitsInto06() {
    assertFalse(fitsInto(3.00001F, Integer.class));
  }

  @Test
  public void fitsInto07() {
    assertFalse(fitsInto(Double.valueOf("3.000000000000001"), Integer.class));
  }

  @Test // Ouch - apparently, here we go past the precision of double.
  public void fitsInto08() {
    assertTrue(fitsInto(Double.valueOf("3.0000000000000001"), Integer.class));
  }

  @Test // Ouch - apparently, here we go past the precision of double.
  public void fitsInto09() {
    assertTrue(fitsInto(3.0000000000000001D, Integer.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void convert01() {
    NumberMethods.convert(300345, Byte.class);
  }

  @Test
  public void convert02() {
    byte b = NumberMethods.convert((short) 123, Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test
  public void convert03() {
    byte b = NumberMethods.convert(123F, Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convert04() {
    NumberMethods.convert(123.02F, Byte.class);
  }

  @Test
  public void convert05() {
    Float f0 = 9.0F;
    Float f1 = NumberMethods.convert(f0, Float.class);
    assertSame(f0, f1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convert06() {
    NumberMethods.convert(.3D, Short.class);
  }

  @Test
  public void convert07() {
    short s = NumberMethods.convert(3D, Short.class);
    assertEquals((short) 3, s);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convert08() {
    NumberMethods.convert(Integer.MIN_VALUE, Short.class);
  }

  @Test
  public void convert09() {
    int i = NumberMethods.convert(0, Integer.class);
    assertEquals(0, i);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse01() {
    NumberMethods.parse("300345", Byte.class);
  }

  @Test
  public void parse02() {
    byte b = NumberMethods.parse("123", Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse04() {
    NumberMethods.parse("123.02", Byte.class);
  }

  @Test
  public void parse05() {
    Float f1 = NumberMethods.parse("0.9", Float.class);
    assertEquals((Float) .9F, f1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse06() {
    NumberMethods.parse(".3", Short.class);
  }

  @Test
  public void parse07() {
    short s = NumberMethods.parse("3", Short.class);
    assertEquals((short) 3, s);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse08() {
    NumberMethods.parse(String.valueOf(Integer.MIN_VALUE), Short.class);
  }

  @Test
  public void parse09() {
    int i = NumberMethods.parse("0", Integer.class);
    assertEquals(0, i);
  }
}
