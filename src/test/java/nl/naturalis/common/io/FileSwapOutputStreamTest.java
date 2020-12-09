package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileSwapOutputStreamTest {

  @Test
  public void test00() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(1);
    sfos.write(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(1);
    sfos.write(2);
    sfos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(1);
    sfos.write(2);
    sfos.write(3);
    sfos.write(4);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3, (byte) 4}, baos.toByteArray());
  }

  @Test
  public void test05() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(1);
    sfos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2});
    sfos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    assertTrue(sfos.hasSwapped());
  }

  @Test
  public void test09() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(sfos.hasSwapped());
  }

  @Test(expected = IOException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    sfos.close();
    sfos.getSwapFile().delete();
    sfos.recall(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    sfos.cleanup();
    assertFalse(sfos.getSwapFile().exists());
  }

  @Test // Example provided in the class comments of SimpleFileSwapOutputStream
  public void test12() throws IOException {
    String data = "Is this going to be swapped???";
    // Create SimpleFileSwapOutputStream that swaps to a temp file if more
    // than 8 bytes are written to it
    SimpleFileSwapOutputStream sfos = SimpleFileSwapOutputStream.newInstance(8);
    sfos.write(data.getBytes());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.recall(baos);
    sfos.cleanup();
    assertEquals(data, baos.toString());
  }
}
