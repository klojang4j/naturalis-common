package nl.naturalis.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

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

  @Test(expected = IllegalArgumentException.class) // null file not allowed
  public void test100() throws IOException {
    try (DeflatedNioSwapOutputStream sos = new DeflatedNioSwapOutputStream(null, 10)) {}
  }

  @Test(expected = IllegalArgumentException.class) // buf size 0 not allowed
  public void test101() {
    DeflatedNioSwapOutputStream.newInstance(0);
  }

  @Test
  public void test102() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(1);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test103() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test104() throws IOException {
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(100);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test
  public void test105() throws IOException {
    int sz = "Hello, world".getBytes(StandardCharsets.UTF_8).length;
    DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(sz);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    // Bit hard to tell when data is compressed:
    // assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", getContents(sos));
  }

  @Test // Write after recall (1)
  public void test106() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance()) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
        sos.recall(baos);
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Write after recall (2)
  public void test107() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance()) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
        sos.recall(baos);
      }
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Write after recall (3)
  public void test108() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance()) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
      }
      sos.recall(baos);
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Write after swap and recall (1)
  public void test109() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2)) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
        sos.recall(baos);
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Write after swap and recall (2)
  public void test110() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2)) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
        sos.recall(baos);
      }
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Write after swap and recall (3)
  public void test111() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedNioSwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(2)) {
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append("Hello, world!");
      }
      sos.recall(baos);
      try (PrintWriter pw = new PrintWriter(sos)) {
        pw.append(" How are you?");
      }
    }
    assertEquals("Hello, world! How are you?", baos.toString());
  }

  @Test // Example provided in the class comments of ZipFileSwapOutputStream
  public void example() throws IOException {
    String data = "Is this going to be swapped???";
    // Create ZipFileSwapOutputStream that swaps to a temp file if more
    // than 8 bytes are written to it
    SwapOutputStream sos = DeflatedNioSwapOutputStream.newInstance(8);
    sos.write(data.getBytes());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    sos.cleanup();
    assertEquals(data, baos.toString());
  }

  private static String getContents(SwapOutputStream sos) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }
}
