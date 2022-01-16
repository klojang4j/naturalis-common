package nl.naturalis.common;

import org.junit.Test;

import java.time.Month;

import static org.junit.Assert.assertEquals;

public class MorphTable1Test {

  @Test
  public void test00() {
    assertEquals(true, MorphTable1.getInstance().morph('1', boolean.class));
    assertEquals(false, MorphTable1.getInstance().morph('0', Boolean.class));
    assertEquals(false, MorphTable1.getInstance().morph("0", boolean.class));
    assertEquals(true, MorphTable1.getInstance().morph("true", Boolean.class));
    assertEquals(false, MorphTable1.getInstance().morph(0L, boolean.class));
    assertEquals(true, MorphTable1.getInstance().morph((byte) 1, Boolean.class));
  }

  @Test
  public void test01() {
    assertEquals('A', MorphTable1.getInstance().morph("A", char.class));
    assertEquals('8', MorphTable1.getInstance().morph(8, Character.class));
    assertEquals('1', MorphTable1.getInstance().morph(true, Character.class));
    assertEquals('0', MorphTable1.getInstance().morph(Boolean.FALSE, Character.class));
  }

  @Test(expected = TypeConversionException.class)
  public void test02() {
    MorphTable1.getInstance().morph("Hello, World", char.class);
  }

  @Test(expected = TypeConversionException.class)
  public void test03() {
    MorphTable1.getInstance().morph((short) 42, Character.class);
  }

  @Test
  public void test04() {
    assertEquals((short) 42, MorphTable1.getInstance().morph(42L, short.class));
    assertEquals(42L, MorphTable1.getInstance().morph("42", Long.class));
    assertEquals(7, MorphTable1.getInstance().morph('7', int.class));
    assertEquals(2, MorphTable1.getInstance().morph(Month.MARCH, int.class));
  }

  @Test(expected = TypeConversionException.class)
  public void test07() {
    MorphTable1.getInstance().morph("Z", byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void test08() {
    MorphTable1.getInstance().morph('Z', Double.class);
  }
}
