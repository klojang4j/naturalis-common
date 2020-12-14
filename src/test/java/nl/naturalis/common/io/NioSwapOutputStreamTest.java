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

public class NioSwapOutputStreamTest {

  @Test
  public void test00() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    sos.write(3);
    sos.write(4);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3, (byte) 4}, baos.toByteArray());
  }

  @Test
  public void test05() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    assertTrue(sos.hasSwapped());
  }

  @Test
  public void test09() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(sos.hasSwapped());
  }

  @Test(expected = IOException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    sos.close();
    sos.swapFile.delete();
    sos.recall(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    sos.cleanup();
    assertFalse(sos.swapFile.exists());
  }

  @Test(expected = IllegalArgumentException.class) // null file not allowed
  public void test100() throws IOException {
    try (NioSwapOutputStream sos = new NioSwapOutputStream(null, 10)) {}
  }

  @Test(expected = IllegalArgumentException.class) // buf size 0 not allowed
  public void test101() {
    NioSwapOutputStream.newInstance(0);
  }

  @Test
  public void test102() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(1);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test103() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(2);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test104() throws IOException {
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(100);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test105() throws IOException {
    int sz = "Hello, world".getBytes(StandardCharsets.UTF_8).length;
    NioSwapOutputStream sos = NioSwapOutputStream.newInstance(sz);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test // Example provided in the class comments of NioSwapOutputStream
  public void example() throws IOException {
    String data = "Will this be swapped or not? It doesn't matter";
    try (SwapOutputStream ros = NioSwapOutputStream.newInstance()) {
      ros.write(data.getBytes());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ros.recall(baos);
      ros.cleanup(); // delete swap file if created
      assertEquals(data, baos.toString());
    }
  }

  private static String getContents(NioSwapOutputStream sos) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }
}
