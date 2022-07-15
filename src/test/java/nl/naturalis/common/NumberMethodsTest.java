package nl.naturalis.common;

import org.junit.Test;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static nl.naturalis.common.NumberMethods.*;
import static org.junit.Assert.*;

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
    assertTrue(fitsInto((Number) null, Double.class));
    assertTrue(fitsInto((Number) null, Float.class));
    assertTrue(fitsInto((Number) null, Long.class));
    assertTrue(fitsInto((Number) null, Integer.class));
    assertTrue(fitsInto((Number) null, Short.class));
    assertTrue(fitsInto((Number) null, Byte.class));
    assertTrue(fitsInto((Number) null, AtomicLong.class));
    assertTrue(fitsInto((Number) null, AtomicInteger.class));
    assertTrue(fitsInto((Number) null, BigDecimal.class));
    assertTrue(fitsInto((Number) null, BigInteger.class));
  }

  @Test
  public void fitsInto02() {
    //    assertFalse(fitsInto(Double.MAX_VALUE, Float.class));
    //    assertFalse(fitsInto(Double.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Double.valueOf(Float.MAX_VALUE), Float.class));
    //    assertTrue(fitsInto(Double.valueOf(Float.MIN_VALUE), Float.class));
    //    assertFalse(fitsInto(Double.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Float.MAX_VALUE, Float.class));
    //    assertTrue(fitsInto(Float.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Long.MAX_VALUE, Float.class));
    //    assertTrue(fitsInto(Long.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Integer.MAX_VALUE, Float.class));
    //    assertTrue(fitsInto(Integer.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Short.MAX_VALUE, Float.class));
    //    assertTrue(fitsInto(Short.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(Byte.MAX_VALUE, Float.class));
    //    assertTrue(fitsInto(Byte.MIN_VALUE, Float.class));
    //    assertTrue(fitsInto(3.00000D, Float.class));
    assertTrue(fitsInto(3.00001D, Float.class));
    assertTrue(fitsInto(3.00001D, Float.class));
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
    assertFalse(fitsInto(Float.valueOf(Integer.MAX_VALUE), Integer.class));
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
  public void convert00() {
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

  @Test
  public void convert10() {
    byte b = NumberMethods.convert(new AtomicLong(123L), Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test
  public void convert11() {
    byte b = NumberMethods.convert(new AtomicInteger(123), Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test
  public void convert12() {
    byte b = NumberMethods.convert(new AtomicInteger(123), Byte.class);
    assertEquals((byte) 123, b);
  }

  @Test
  public void convert13() {
    byte b = NumberMethods.convert(new AtomicInteger(-123), Byte.class);
    assertEquals((byte) -123, b);
  }

  @Test(expected = TypeConversionException.class)
  public void convert14() {
    NumberMethods.convert(new AtomicInteger(1000), Byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void convert15() {
    NumberMethods.convert(new AtomicLong(1000), Byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void convert16() {
    NumberMethods.convert(1000, Byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void convert17() {
    NumberMethods.convert(-1000, Byte.class);
  }

  @Test(expected = TypeConversionException.class)
  public void convert18() {
    NumberMethods.convert(Double.MAX_VALUE, Float.class);
  }

  @Test
  public void convert19() {
    Float f = NumberMethods.convert(Float.MAX_VALUE, Float.class);
    assertTrue(f.equals(Float.MAX_VALUE));
  }

  @Test(expected = TypeConversionException.class)
  public void convert20() {
    NumberMethods.convert(Double.MIN_VALUE, Float.class);
  }

  @Test
  public void convert21() {
    Float f = NumberMethods.convert(Float.MIN_VALUE, Float.class);
    assertTrue(f.equals(Float.MIN_VALUE));
  }

  @Test(expected = TypeConversionException.class) // OUCH, FLOATING POINT STUFF
  public void convert22() {
    String s = Integer.toString(Integer.MAX_VALUE);
    Integer i = NumberMethods.convert(Float.valueOf(s), Integer.class);
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

  @Test
  public void parse10() {
    BigInteger i = NumberMethods.parse("42", BigInteger.class);
    assertEquals(42, i.intValueExact());
  }

  @Test
  public void parse11() {
    BigDecimal i = NumberMethods.parse("42.337", BigDecimal.class);
    assertEquals(42.337F, i.floatValue(), 0F);
  }

  @Test
  public void parse12() {
    Integer i = NumberMethods.parse("42.0000", Integer.class);
    assertEquals(42, (int) i);
  }

  @Test
  public void parse13() {
    Integer i = NumberMethods.convert(42.000F, Integer.class);
    assertEquals(42, (int) i);
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt00() {
    NumberMethods.parseInt("  22");
  }

  @Test
  public void parseInt01() {
    assertEquals(-22, NumberMethods.parseInt("-00000000000022"));
    assertEquals(+22, NumberMethods.parseInt("+00000000000022"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt02() {
    NumberMethods.parseInt("-200000000000022");
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt03() {
    NumberMethods.parseInt(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt04() {
    NumberMethods.parseInt("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt05() {
    NumberMethods.parseInt("42.6");
  }

  @Test(expected = TypeConversionException.class)
  public void parseInt06() {
    NumberMethods.parseInt("12foo");
  }

  @Test
  public void toInt01() {
    assertEquals(OptionalInt.of(-22), NumberMethods.toInt("-00000000000022"));
    assertEquals(OptionalInt.of(+22), NumberMethods.toInt("+00000000000022"));
  }

  @Test
  public void toInt02() {
    assertEquals(OptionalInt.empty(), NumberMethods.toInt("-200000000000022"));
  }

  @Test
  public void toInt03() {
    assertEquals(OptionalInt.empty(), NumberMethods.toInt(null));
  }

  @Test
  public void toInt04() {
    assertEquals(OptionalInt.empty(), NumberMethods.toInt(""));
  }

  @Test
  public void toInt05() {
    assertEquals(OptionalInt.empty(), NumberMethods.toInt("42.6"));
  }

  @Test
  public void toInt06() {
    assertEquals(OptionalInt.empty(), NumberMethods.toInt("12foo"));
  }

  @Test
  public void isInt01() {
    assertTrue(NumberMethods.isInt("42"));
    assertTrue(NumberMethods.isInt("-42"));
  }

  @Test
  public void isInt02() {
    assertFalse(NumberMethods.isInt(null));
  }

  @Test
  public void isInt03() {
    assertFalse(NumberMethods.isInt(""));
  }

  @Test
  public void isInt04() {
    assertFalse(NumberMethods.isInt("1.3"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseDouble00() {
    NumberMethods.parseDouble("  22");
  }

  @Test
  public void parseDouble01() {
    assertEquals(-22.3D, NumberMethods.parseDouble("-00000000000022.3"), 0D);
    assertEquals(+22.3D, NumberMethods.parseDouble("+00000000000022.3"), 0D);
    assertFalse(Double.isNaN(NumberMethods.parseDouble("1.0E292")));
    assertFalse(Double.isInfinite(NumberMethods.parseDouble("1.0E292")));
    assertFalse(Double.isInfinite(NumberMethods.parseDouble("1.0E292")));
    assertFalse(Double.isInfinite(NumberMethods.parseDouble("1.0E-44")));
  }

  @Test(expected = TypeConversionException.class)
  public void parseDouble02() {
    NumberMethods.parseDouble("1"
        + ".67E299999999999999999999999999999999999999999999999999");
  }

  @Test(expected = TypeConversionException.class)
  public void parseDouble03() {
    NumberMethods.parseDouble(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseDouble04() {
    NumberMethods.parseDouble("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseDouble05() {
    NumberMethods.parseDouble("12foo");
  }

  @Test
  public void toDouble00() {
    assertEquals(OptionalDouble.of(-22), NumberMethods.toDouble("-00000000000022"));
    assertEquals(OptionalDouble.of(+22), NumberMethods.toDouble("+00000000000022"));
  }

  @Test
  public void toDouble01() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toDouble("3.0D"));
  }

  @Test
  public void toDouble02() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toDouble(null));
  }

  @Test
  public void toDouble03() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toDouble(""));
  }

  @Test
  public void toDouble04() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toDouble("12foo"));
  }

  @Test
  public void isDouble00() {
    assertTrue(NumberMethods.isDouble("42"));
    assertTrue(NumberMethods.isDouble("-42.8989"));
  }

  @Test
  public void isDouble01() {
    assertFalse(NumberMethods.isDouble(null));
  }

  @Test
  public void isDouble02() {
    assertFalse(NumberMethods.isDouble(""));
  }

  @Test
  public void isDouble03() {
    assertFalse(NumberMethods.isDouble("1.3D"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseFloat00() {
    NumberMethods.parseFloat("  22");
  }

  @Test
  public void parseFloat01() {
    assertEquals(-22F, NumberMethods.parseFloat("-00000000000022"), 0F);
    assertEquals(+22.5F, NumberMethods.parseFloat("+00000000000022.5"), 0F);
  }

  @Test(expected = TypeConversionException.class)
  public void parseFloat02() {
    NumberMethods.parseFloat("-3.6F");
  }

  @Test(expected = TypeConversionException.class)
  public void parseFloat03() {
    NumberMethods.parseFloat(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseFloat04() {
    NumberMethods.parseFloat("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseFloat05() {
    NumberMethods.parseFloat("12foo");
  }

  @Test
  public void toFloat01() {
    assertEquals(OptionalDouble.of(-1.3E1), NumberMethods.toFloat("-1.3E1"));
  }

  @Test
  public void toFloat02() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toFloat(null));
  }

  @Test
  public void toFloat03() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toFloat(""));
  }

  @Test
  public void toFloat04() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toFloat("42.+6"));
  }

  @Test
  public void toFloat05() {
    assertEquals(OptionalDouble.empty(), NumberMethods.toFloat("12foo"));
  }

  @Test
  public void isFloat01() {
    assertTrue(NumberMethods.isFloat("42"));
    assertTrue(NumberMethods.isFloat("-42"));
  }

  @Test
  public void isFloat02() {
    assertFalse(NumberMethods.isFloat(null));
  }

  @Test
  public void isFloat03() {
    assertFalse(NumberMethods.isFloat(""));
  }

  @Test
  public void isFloat04() {
    assertTrue(NumberMethods.isFloat("1.3"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong00() {
    NumberMethods.parseLong("  22");
  }

  @Test
  public void parseLong01() {
    assertEquals(-22, NumberMethods.parseLong("-00000000000022"));
    assertEquals(+22, NumberMethods.parseLong("+00000000000022"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong02() {
    NumberMethods.parseLong("-9999999999999999999999999999992");
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong03() {
    NumberMethods.parseLong(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong04() {
    NumberMethods.parseLong("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong05() {
    NumberMethods.parseLong("42.6");
  }

  @Test(expected = TypeConversionException.class)
  public void parseLong06() {
    NumberMethods.parseLong("12foo");
  }

  @Test
  public void toLong01() {
    assertEquals(OptionalLong.of(-22), NumberMethods.toLong("-00000000000022"));
    assertEquals(OptionalLong.of(+22), NumberMethods.toLong("+00000000000022"));
  }

  @Test
  public void toLong02() {
    assertEquals(OptionalLong.empty(), NumberMethods.toLong(
        "99999999999999999999999999"));
  }

  @Test
  public void toLong03() {
    assertEquals(OptionalLong.empty(), NumberMethods.toLong(null));
  }

  @Test
  public void toLong04() {
    assertEquals(OptionalLong.empty(), NumberMethods.toLong(""));
  }

  @Test
  public void toLong05() {
    assertEquals(OptionalLong.empty(), NumberMethods.toLong("42.6"));
  }

  @Test
  public void toLong06() {
    assertEquals(OptionalLong.empty(), NumberMethods.toLong("12foo"));
  }

  @Test
  public void isLong01() {
    assertTrue(NumberMethods.isLong("42"));
    assertTrue(NumberMethods.isLong("-42"));
  }

  @Test
  public void isLong02() {
    assertFalse(NumberMethods.isLong(null));
  }

  @Test
  public void isLong03() {
    assertFalse(NumberMethods.isLong(""));
  }

  @Test
  public void isLong04() {
    assertFalse(NumberMethods.isLong("1.3"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort00() {
    NumberMethods.parseShort("  22");
  }

  @Test
  public void parseShort01() {
    assertEquals(-22, NumberMethods.parseShort("-00000000000022"));
    assertEquals(+22, NumberMethods.parseShort("+00000000000022"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort02() {
    NumberMethods.parseShort("-200000000000022");
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort03() {
    NumberMethods.parseShort(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort04() {
    NumberMethods.parseShort("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort05() {
    NumberMethods.parseShort("42.6");
  }

  @Test(expected = TypeConversionException.class)
  public void parseShort06() {
    NumberMethods.parseShort("12foo");
  }

  @Test
  public void toShort01() {
    assertEquals(OptionalInt.of(-22), NumberMethods.toShort("-00000000000022"));
    assertEquals(OptionalInt.of(+22), NumberMethods.toShort("+00000000000022"));
  }

  @Test
  public void toShort02() {
    assertEquals(OptionalInt.empty(), NumberMethods.toShort("-200000000000022"));
  }

  @Test
  public void toShort03() {
    assertEquals(OptionalInt.empty(), NumberMethods.toShort(null));
  }

  @Test
  public void toShort04() {
    assertEquals(OptionalInt.empty(), NumberMethods.toShort(""));
  }

  @Test
  public void toShort05() {
    assertEquals(OptionalInt.empty(), NumberMethods.toShort("42.6"));
  }

  @Test
  public void toShort06() {
    assertEquals(OptionalInt.empty(), NumberMethods.toShort("12foo"));
  }

  @Test
  public void isShort01() {
    assertTrue(NumberMethods.isShort("42"));
    assertTrue(NumberMethods.isShort("-42"));
  }

  @Test
  public void isShort02() {
    assertFalse(NumberMethods.isShort(null));
  }

  @Test
  public void isShort03() {
    assertFalse(NumberMethods.isShort(""));
  }

  @Test
  public void isShort04() {
    assertFalse(NumberMethods.isShort("1.3"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte00() {
    NumberMethods.parseByte("  22");
  }

  @Test
  public void parseByte01() {
    assertEquals(-22, NumberMethods.parseByte("-00000000000022"));
    assertEquals(+22, NumberMethods.parseByte("+00000000000022"));
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte02() {
    NumberMethods.parseByte("-200000000000022");
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte03() {
    NumberMethods.parseByte(null);
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte04() {
    NumberMethods.parseByte("");
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte05() {
    NumberMethods.parseByte("42.6");
  }

  @Test(expected = TypeConversionException.class)
  public void parseByte06() {
    NumberMethods.parseByte("12foo");
  }

  @Test
  public void toByte01() {
    assertEquals(OptionalInt.of(-22), NumberMethods.toByte("-00000000000022"));
    assertEquals(OptionalInt.of(+22), NumberMethods.toByte("+00000000000022"));
  }

  @Test
  public void toByte02() {
    assertEquals(OptionalInt.empty(), NumberMethods.toByte("-200000000000022"));
  }

  @Test
  public void toByte03() {
    assertEquals(OptionalInt.empty(), NumberMethods.toByte(null));
  }

  @Test
  public void toByte04() {
    assertEquals(OptionalInt.empty(), NumberMethods.toByte(""));
  }

  @Test
  public void toByte05() {
    assertEquals(OptionalInt.empty(), NumberMethods.toByte("42.6"));
  }

  @Test
  public void toByte06() {
    assertEquals(OptionalInt.empty(), NumberMethods.toByte("12foo"));
  }

  @Test
  public void isByte01() {
    assertTrue(NumberMethods.isByte("42"));
    assertTrue(NumberMethods.isByte("-42"));
  }

  @Test
  public void isByte02() {
    assertFalse(NumberMethods.isByte(null));
  }

  @Test
  public void isByte03() {
    assertFalse(NumberMethods.isByte(""));
  }

  @Test
  public void isByte04() {
    assertFalse(NumberMethods.isByte("1.3"));
  }

  @Test
  public void isInteger00() {
    assertTrue(isIntegral(7));
    assertTrue(isIntegral(Integer.class));
    assertTrue(isIntegral(new BigInteger("56")));
    assertTrue(isIntegral(new AtomicLong(56)));
    assertFalse(isIntegral(int.class));
    assertFalse(isIntegral(7.0));
    assertFalse(isIntegral(OutputStream.class));
    assertFalse(isIntegral(new BigDecimal("6")));
  }

  @Test
  public void isWrapper00() {
    assertTrue(isWrapper(Integer.class));
    assertFalse(isWrapper(int.class));
    assertFalse(isWrapper(AtomicLong.class));
    assertFalse(isWrapper(OutputStream.class));
  }

}
