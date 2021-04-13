package nl.naturalis.common.io;

import org.junit.Test;
import nl.naturalis.common.util.AugmentationType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExposedByteArrayOutputStreamTest {
  @Test
  public void test00() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2);
    out.write(new byte[0]);
    byte[] arr = out.toByteArray();
    assertEquals("01", 2, arr.length);
    assertEquals("02", 0, out.count());
  }

  @Test
  public void test01() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2);
    out.write(new byte[] {123});
    byte[] arr = out.toByteArray();
    assertEquals("01", 2, arr.length);
    assertArrayEquals("02", new byte[] {123, 0}, out.toByteArray());
  }

  @Test
  public void test02() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2);
    out.write(new byte[] {123, 7});
    byte[] arr = out.toByteArray();
    assertEquals("01", 2, arr.length);
    assertArrayEquals("02", new byte[] {123, 7}, out.toByteArray());
  }

  @Test
  public void test03() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(new byte[] {123, 7, 22});
    byte[] arr = out.toByteArray();
    assertEquals("01", 3, arr.length);
    assertArrayEquals("02", new byte[] {123, 7, 22}, out.toByteArray());
  }

  @Test
  public void test04() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(new byte[] {123, 7, 22, 16});
    byte[] arr = out.toByteArray();
    assertEquals("01", 4, arr.length);
    assertArrayEquals("02", new byte[] {123, 7, 22, 16}, out.toByteArray());
  }

  @Test
  public void test05() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(33);
    out.write(new byte[] {123, 7, 22, 16});
    byte[] arr = out.toByteArray();
    assertEquals("01", 5, arr.length);
    assertArrayEquals("02", new byte[] {33, 123, 7, 22, 16}, out.toByteArray());
  }

  @Test
  public void test06() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(33);
    out.write(34);
    out.write(new byte[] {123, 7, 22, 16});
    byte[] arr = out.toByteArray();
    assertEquals("01", 6, arr.length);
    assertArrayEquals("02", new byte[] {33, 34, 123, 7, 22, 16}, out.toByteArray());
  }

  @Test
  public void test07() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(33);
    out.write(34);
    out.write(35);
    out.write(new byte[] {123, 7, 22, 16});
    byte[] arr = out.toByteArray();
    assertEquals("01", 7, arr.length);
    assertArrayEquals("02", new byte[] {33, 34, 35, 123, 7, 22, 16}, out.toByteArray());
  }

  @Test
  public void test08() {
    @SuppressWarnings("resource")
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(2, 1, AugmentationType.ADD);
    out.write(33);
    out.write(new byte[] {123, 7, 22, 16});
    out.write(34);
    out.write(35);
    byte[] arr = out.toByteArray();
    assertEquals("01", 7, arr.length);
    assertArrayEquals("02", new byte[] {33, 123, 7, 22, 16, 34, 35}, out.toByteArray());
  }
}
