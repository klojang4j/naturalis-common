package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SwapFileOutputStreamTest {

  @Test
  public void test00() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write((byte) 1);
    sfos.write((byte) 2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write((byte) 1);
    sfos.write((byte) 2);
    sfos.write((byte) 3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write((byte) 1);
    sfos.write((byte) 2);
    sfos.write((byte) 3);
    sfos.write((byte) 4);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3, (byte) 4}, baos.toByteArray());
  }

  @Test
  public void test05() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write((byte) 1);
    sfos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2});
    sfos.write((byte) 3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    assertTrue(sfos.hasSwapped());
  }

  @Test
  public void test09() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(sfos.hasSwapped());
  }

  @Test(expected = IllegalStateException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    sfos.close();
    sfos.getSwapFile().delete();
    sfos.collect(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(2);
    sfos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    sfos.cleanup();
    assertFalse(sfos.getSwapFile().exists());
  }

  @Test // Example provided in the class comments of SwapFileOutputStream
  public void test12() throws IOException {
    String data = "Is this going to be swapped???";
    // Create SwapFileOutputStream that swaps to a temp file if more
    // than 8 bytes are written to it
    SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(8);
    sfos.write(data.getBytes());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sfos.collect(baos);
    sfos.cleanup();
    assertEquals(data, baos.toString());
  }
}
