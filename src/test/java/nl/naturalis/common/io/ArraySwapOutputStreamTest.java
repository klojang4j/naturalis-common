package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArraySwapOutputStreamTest {

  @Test
  public void test00() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(1);
    asos.write(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(1);
    asos.write(2);
    asos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(1);
    asos.write(2);
    asos.write(3);
    asos.write(4);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3, (byte) 4}, baos.toByteArray());
  }

  @Test
  public void test05() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(1);
    asos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2});
    asos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    assertTrue(asos.hasSwapped());
  }

  @Test
  public void test09() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(asos.hasSwapped());
  }

  @Test(expected = IOException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    asos.close();
    asos.swapFile.delete();
    asos.recall(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    asos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    asos.cleanup();
    assertFalse(asos.swapFile.exists());
  }

  @Test(expected = IllegalArgumentException.class) // null file not allowed
  public void test100() throws IOException {
    try (ArraySwapOutputStream sos = new ArraySwapOutputStream(null, 10)) {}
  }

  @Test(expected = IllegalArgumentException.class) // buf size 0 not allowed
  public void test101() {
    ArraySwapOutputStream.newInstance(0);
  }

  @Test
  public void test102() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(1);
    try (PrintWriter pw = new PrintWriter(asos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", asos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(asos));
  }

  @Test
  public void test103() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(2);
    try (PrintWriter pw = new PrintWriter(asos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", asos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(asos));
  }

  @Test
  public void test104() throws IOException {
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(100);
    try (PrintWriter pw = new PrintWriter(asos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", asos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(asos));
  }

  @Test
  public void test105() throws IOException {
    int sz = "Hello, world".getBytes(StandardCharsets.UTF_8).length;
    ArraySwapOutputStream asos = ArraySwapOutputStream.newInstance(sz);
    try (PrintWriter pw = new PrintWriter(asos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", asos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(asos));
  }

  @Test // Example provided in the class comments of ArraySwapOutputStream
  public void example() throws IOException {
    String data = "Will this be swapped or not? It doesn't matter";
    try (SwapOutputStream ros = ArraySwapOutputStream.newInstance()) {
      ros.write(data.getBytes());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ros.recall(baos);
      ros.cleanup(); // delete swap file if created
      assertEquals(data, baos.toString());
    }
  }

  private static String getContents(ArraySwapOutputStream asos) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    asos.recall(baos);
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }
}
