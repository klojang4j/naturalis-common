package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.NumberMethods.*;

public class NumberMethodsTest {

  @Test
  public void test01() {
    assertTrue(fitsInto(Integer.MAX_VALUE, Float.class));
  }

  @Test
  public void test02() {
    assertTrue(fitsInto(Integer.MIN_VALUE, Float.class));
  }

  @Test
  public void test03() {
    assertFalse(fitsInto(Float.MAX_VALUE, Integer.class));
  }

  @Test
  public void test04() {
    assertFalse(fitsInto(Float.MIN_VALUE, Integer.class));
  }

  @Test
  public void test05() {
    assertTrue(fitsInto(3.00F, Integer.class));
  }

  @Test
  public void test06() {
    assertFalse(fitsInto(3.00001F, Integer.class));
  }

  @Test
  public void test07() {
    assertFalse(fitsInto(Double.valueOf("3.000000000000001"), Integer.class));
  }

  @Test // Apparently, here we go past the precision of double.
  public void test08() {
    assertTrue(fitsInto(Double.valueOf("3.0000000000000001"), Integer.class));
  }

  @Test // Apparently, here we go past the precision of double.
  public void test09() {
    assertTrue(fitsInto(3.0000000000000001D, Integer.class));
  }

  @Test
  public void nvl01() {
    assertTrue(ntz((Integer) null) == ZERO_INT);
    assertTrue(ntz((Double) null) == ZERO_DOUBLE);
    assertTrue(ntz((Long) null) == ZERO_LONG);
    assertTrue(ntz((Float) null) == ZERO_FLOAT);
    assertTrue(ntz((Short) null) == ZERO_SHORT);
    assertTrue(ntz((Byte) null) == ZERO_BYTE);
    assertTrue(ntz(2) == 2);
    assertTrue(ntz(2.0) == 2D);
    assertTrue(ntz(2L) == 2L);
    assertTrue(ntz(2.0F) == 2F);
    assertTrue(ntz((short) 2) == (short) 2);
    assertTrue(ntz((byte) 2) == (byte) 2);
  }
}
