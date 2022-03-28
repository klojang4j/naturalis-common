package nl.naturalis.common;

import static nl.naturalis.common.NumberMethods.fitsInto;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import nl.naturalis.common.invoke.IllegalAssignmentException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberMethodsTest {

  @Test
  public void fitsInto01() {
    assertTrue(fitsInto(Double.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Double.MIN_VALUE, Double.class));
    assertTrue(fitsInto(Float.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Float.MIN_VALUE, Double.class));
    assertTrue(fitsInto(Long.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Long.MIN_VALUE, Double.class));
    assertTrue(fitsInto(Integer.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Integer.MIN_VALUE, Double.class));
    assertTrue(fitsInto(Short.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Short.MIN_VALUE, Double.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Double.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Double.class));
    assertTrue(fitsInto((short) 2, Double.class));
    assertTrue(fitsInto(3L, Double.class));
  }

  @Test
  public void fitsInto02() {
    assertFalse(fitsInto(Double.MAX_VALUE, Float.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Double.valueOf(Float.MAX_VALUE), Float.class));
    assertTrue(fitsInto(Double.valueOf(Float.MIN_VALUE), Float.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Float.MAX_VALUE, Float.class));
    assertTrue(fitsInto(Float.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Long.MAX_VALUE, Float.class));
    assertTrue(fitsInto(Long.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Integer.MAX_VALUE, Float.class));
    assertTrue(fitsInto(Integer.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Short.MAX_VALUE, Float.class));
    assertTrue(fitsInto(Short.MIN_VALUE, Float.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Float.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Float.class));
    assertTrue(fitsInto(3.00000D, Float.class));
    assertFalse(fitsInto(3.00001D, Float.class));
  }

  @Test
  public void fitsInto03() {
    assertFalse(fitsInto(Double.MAX_VALUE, Long.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Long.class));
    assertFalse(fitsInto(Float.MAX_VALUE, Long.class));
    assertFalse(fitsInto(Float.MIN_VALUE, Long.class));
    assertTrue(fitsInto(Double.valueOf(Long.MAX_VALUE), Long.class));
    assertTrue(fitsInto(Double.valueOf(Long.MIN_VALUE), Long.class));
    assertTrue(fitsInto(Float.valueOf(Long.MAX_VALUE), Long.class));
    assertTrue(fitsInto(Float.valueOf(Long.MIN_VALUE), Long.class));
    assertTrue(fitsInto(Long.MAX_VALUE, Long.class));
    assertTrue(fitsInto(Long.MIN_VALUE, Long.class));
    assertTrue(fitsInto(Integer.MAX_VALUE, Long.class));
    assertTrue(fitsInto(Integer.MIN_VALUE, Long.class));
    assertTrue(fitsInto(Short.MAX_VALUE, Long.class));
    assertTrue(fitsInto(Short.MIN_VALUE, Long.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Long.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Long.class));
    assertTrue(fitsInto(3.0000000D, Long.class));
    assertFalse(fitsInto(3.0000001D, Long.class));
  }

  @Test
  public void fitsInto05() {
    assertFalse(fitsInto(Double.MAX_VALUE, Integer.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Integer.class));
    assertFalse(fitsInto(Float.MAX_VALUE, Integer.class));
    assertFalse(fitsInto(Float.MIN_VALUE, Integer.class));
    assertFalse(fitsInto(Long.MAX_VALUE, Integer.class));
    assertFalse(fitsInto(Long.MIN_VALUE, Integer.class));
    assertTrue(fitsInto(Double.valueOf(Integer.MAX_VALUE), Integer.class));
    assertTrue(fitsInto(Double.valueOf(Integer.MIN_VALUE), Integer.class));
    assertTrue(fitsInto(Float.valueOf(Integer.MAX_VALUE), Integer.class));
    assertTrue(fitsInto(Float.valueOf(Integer.MIN_VALUE), Integer.class));
    assertTrue(fitsInto(Long.valueOf(Integer.MAX_VALUE), Integer.class));
    assertTrue(fitsInto(Long.valueOf(Integer.MIN_VALUE), Integer.class));
    assertTrue(fitsInto(Integer.MAX_VALUE, Integer.class));
    assertTrue(fitsInto(Integer.MIN_VALUE, Integer.class));
    assertTrue(fitsInto(Short.MAX_VALUE, Integer.class));
    assertTrue(fitsInto(Short.MIN_VALUE, Integer.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Integer.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Integer.class));
    assertTrue(fitsInto(3.0000000D, Integer.class));
    assertFalse(fitsInto(3.0000001D, Integer.class));
    assertTrue(fitsInto(3.00000D, Integer.class));
    assertFalse(fitsInto(3.00001F, Integer.class));
  }

  @Test
  public void fitsInto06() {
    assertFalse(fitsInto(Double.MAX_VALUE, Short.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Short.class));
    assertFalse(fitsInto(Float.MAX_VALUE, Short.class));
    assertFalse(fitsInto(Float.MIN_VALUE, Short.class));
    assertFalse(fitsInto(Long.MAX_VALUE, Short.class));
    assertFalse(fitsInto(Long.MIN_VALUE, Short.class));
    assertFalse(fitsInto(Integer.MAX_VALUE, Short.class));
    assertFalse(fitsInto(Integer.MIN_VALUE, Short.class));
    assertTrue(fitsInto(Double.valueOf(Short.MAX_VALUE), Short.class));
    assertTrue(fitsInto(Double.valueOf(Short.MIN_VALUE), Short.class));
    assertTrue(fitsInto(Float.valueOf(Short.MAX_VALUE), Short.class));
    assertTrue(fitsInto(Float.valueOf(Short.MIN_VALUE), Short.class));
    assertTrue(fitsInto(Long.valueOf(Short.MAX_VALUE), Short.class));
    assertTrue(fitsInto(Long.valueOf(Short.MIN_VALUE), Short.class));
    assertTrue(fitsInto(Integer.valueOf(Short.MAX_VALUE), Short.class));
    assertTrue(fitsInto(Integer.valueOf(Short.MIN_VALUE), Short.class));
    assertTrue(fitsInto(Short.MAX_VALUE, Short.class));
    assertTrue(fitsInto(Short.MIN_VALUE, Short.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Short.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Short.class));
    assertTrue(fitsInto(3.0000000D, Short.class));
    assertFalse(fitsInto(3.0000001D, Short.class));
    assertTrue(fitsInto(3.00000D, Short.class));
    assertFalse(fitsInto(3.00001F, Short.class));
  }

  @Test
  public void fitsInto07() {
    assertFalse(fitsInto(Double.MAX_VALUE, Byte.class));
    assertFalse(fitsInto(Double.MIN_VALUE, Byte.class));
    assertFalse(fitsInto(Float.MAX_VALUE, Byte.class));
    assertFalse(fitsInto(Float.MIN_VALUE, Byte.class));
    assertFalse(fitsInto(Long.MAX_VALUE, Byte.class));
    assertFalse(fitsInto(Long.MIN_VALUE, Byte.class));
    assertFalse(fitsInto(Integer.MAX_VALUE, Byte.class));
    assertFalse(fitsInto(Integer.MIN_VALUE, Byte.class));
    assertFalse(fitsInto(Short.MAX_VALUE, Byte.class));
    assertFalse(fitsInto(Short.MIN_VALUE, Byte.class));
    assertTrue(fitsInto(Double.valueOf(Byte.MAX_VALUE), Byte.class));
    assertTrue(fitsInto(Double.valueOf(Byte.MIN_VALUE), Byte.class));
    assertTrue(fitsInto(Float.valueOf(Byte.MAX_VALUE), Byte.class));
    assertTrue(fitsInto(Float.valueOf(Byte.MIN_VALUE), Byte.class));
    assertTrue(fitsInto(Long.valueOf(Byte.MAX_VALUE), Byte.class));
    assertTrue(fitsInto(Long.valueOf(Byte.MIN_VALUE), Byte.class));
    assertTrue(fitsInto(Integer.valueOf(Byte.MAX_VALUE), Byte.class));
    assertTrue(fitsInto(Integer.valueOf(Byte.MIN_VALUE), Byte.class));
    assertTrue(fitsInto(Short.valueOf(Byte.MAX_VALUE), Byte.class));
    assertTrue(fitsInto(Short.valueOf(Byte.MIN_VALUE), Byte.class));
    assertTrue(fitsInto(Byte.MAX_VALUE, Byte.class));
    assertTrue(fitsInto(Byte.MIN_VALUE, Byte.class));
    assertTrue(fitsInto(3.0000000D, Byte.class));
    assertFalse(fitsInto(3.0000001D, Byte.class));
    assertTrue(fitsInto(3.00000D, Byte.class));
    assertTrue(fitsInto(3L, Byte.class));
    assertFalse(fitsInto(3.00001F, Byte.class));
    assertFalse(fitsInto(400, Byte.class));
    assertFalse(fitsInto(300L, Byte.class));
  }

  @Test
  public void fitsInto08() {
    assertFalse(fitsInto(Double.valueOf("3.000000000000001"), Integer.class));
  }

  @Test // Ouch - here we go past the precision of double.
  public void fitsInto09() {
    assertTrue(fitsInto(Double.valueOf("3.0000000000000001"), Integer.class));
    assertTrue(fitsInto(3.0000000000000001D, Integer.class));
  }

  @Test(expected = TypeConversionException.class)
  public void convert10() {
    NumberMethods.convert(300345, Byte.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fitsInto11() {
    fitsInto(42L, BigInteger.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fitsInto12() {
    fitsInto(42L, BigDecimal.class);
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

  @Test(expected = TypeConversionException.class)
  public void convert04() {
    NumberMethods.convert(123.02F, Byte.class);
  }

  @Test
  public void convert05() {
    Float f0 = 9.0F;
    Float f1 = NumberMethods.convert(f0, Float.class);
    assertSame(f0, f1);
  }

  @Test(expected = TypeConversionException.class)
  public void convert06() {
    NumberMethods.convert(.3D, Short.class);
  }

  @Test
  public void convert07() {
    short s = NumberMethods.convert(3D, Short.class);
    assertEquals((short) 3, s);
  }

  @Test(expected = TypeConversionException.class)
  public void convert08() {
    NumberMethods.convert(Integer.MIN_VALUE, Short.class);
  }

  @Test
  public void convert09() {
    int i = NumberMethods.convert(0, Integer.class);
    assertEquals(0, i);
  }

  @Test(expected = TypeConversionException.class)
  public void parse01() {
    NumberMethods.parse("300345", Byte.class);
  }

  @Test
  public void parse02() {
    byte b = NumberMethods.parse("123", Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test(expected = TypeConversionException.class)
  public void parse04() {
    NumberMethods.parse("123.02", Byte.class);
  }

  @Test
  public void parse05() {
    Float f1 = NumberMethods.parse("0.9", Float.class);
    assertEquals((Float) .9F, f1);
  }

  @Test(expected = TypeConversionException.class)
  public void parse06() {
    NumberMethods.parse(".3", Short.class);
  }

  @Test
  public void parse07() {
    short s = NumberMethods.parse("3", Short.class);
    assertEquals((short) 3, s);
  }

  @Test(expected = TypeConversionException.class)
  public void parse08() {
    NumberMethods.parse(String.valueOf(Integer.MIN_VALUE), Short.class);
  }

  @Test
  public void parse09() {
    int i = NumberMethods.parse("0", Integer.class);
    assertEquals(0, i);
  }
}
