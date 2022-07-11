package nl.naturalis.common;

import org.junit.Test;

import java.time.Month;

import static org.junit.Assert.assertEquals;

public class MorphTable1Test {

  @Test
  public void test00() {
    assertEquals(true, MorphToNumber.getInstance().morph('1', boolean.class));
    assertEquals(false, MorphToNumber.getInstance().morph('0', Boolean.class));
    assertEquals(false, MorphToNumber.getInstance().morph("0", boolean.class));
    assertEquals(true, MorphToNumber.getInstance().morph("true", Boolean.class));
    assertEquals(false, MorphToNumber.getInstance().morph(0L, boolean.class));
    assertEquals(true, MorphToNumber.getInstance().morph((byte) 1, Boolean.class));
  }

  @Test
  public void test01() {
    assertEquals('A', MorphToNumber.getInstance().morph("A", char.class));
    assertEquals('8', MorphToNumber.getInstance().morph(8, Character.class));
    assertEquals('1', MorphToNumber.getInstance().morph(true, Character.class));
    assertEquals('0',
        MorphToNumber.getInstance().morph(Boolean.FALSE, Character.class));
  }

  @Test(expected = TypeConversionException.class)
  public void test02() {
    MorphToNumber.getInstance().morph("Hello, World", char.class);
  }

  @Test(expected = TypeConversionException.class)
  public void test03() {
    MorphToNumber.getInstance().morph((short) 42, Character.class);
  }

  @Test
  public void test04() {
    assertEquals((short) 42, MorphToNumber.getInstance().morph(42L, short.class));
    assertEquals(42L, MorphToNumber.getInstance().morph("42", Long.class));
    assertEquals(7, MorphToNumber.getInstance().morph('7', int.class));
    assertEquals(2, MorphToNumber.getInstance().morph(Month.MARCH, int.class));
  }

  @Test(expected = TypeConversionException.class)
  public void test07() {
    MorphToNumber.getInstance().morph("Z", byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void test08() {
    MorphToNumber.getInstance().morph('Z', Double.class);
  }

}
