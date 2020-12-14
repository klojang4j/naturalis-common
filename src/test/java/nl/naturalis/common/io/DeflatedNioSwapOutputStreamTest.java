package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeflatedNioSwapOutputStreamTest {

  @Test
  public void test00() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
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
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(3);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test09() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(sos.hasSwapped());
  }

  @Test(expected = IOException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    sos.close();
    sos.swapFile.delete();
    sos.recall(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    sos.cleanup();
    assertFalse(sos.swapFile.exists());
  }

  @Test // Example provided in the class comments of ZipFileSwapOutputStream
  public void test12() throws IOException {
    String data = "Is this going to be swapped???";
    // Create ZipFileSwapOutputStream that swaps to a temp file if more
    // than 8 bytes are written to it
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(8);
    sos.write(data.getBytes());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    sos.cleanup();
    assertEquals(data, baos.toString());
  }
}
