package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeflatedArraySwapOutputStreamTest {

  @Test
  public void test00() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[0], baos.toByteArray());
  }

  @Test
  public void test01() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write((byte) 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1}, baos.toByteArray());
  }

  @Test
  public void test02() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, baos.toByteArray());
  }

  @Test
  public void test03() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(2);
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test04() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
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
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(1);
    sos.write(new byte[] {(byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test06() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    sos.write(3);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test07() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test08() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(3);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
  }

  @Test
  public void test09() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2});
    assertFalse(sos.hasSwapped());
  }

  @Test(expected = IOException.class)
  @SuppressWarnings("resource")
  public void test10() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    sos.close();
    sos.swapFile.delete();
    sos.recall(new ByteArrayOutputStream()); // Oops, swap file gone
  }

  @Test
  public void test11() throws IOException {
    DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2);
    sos.write(new byte[] {(byte) 1, (byte) 2, (byte) 3});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    sos.recall(baos);
    assertArrayEquals(new byte[] {(byte) 1, (byte) 2, (byte) 3}, baos.toByteArray());
    sos.cleanup();
    assertFalse(sos.swapFile.exists());
  }

  @Test // Write after recall (1)
  public void test106() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance()) {
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
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance()) {
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
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance()) {
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
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2)) {
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
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2)) {
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
    try (DeflatedArraySwapOutputStream sos = DeflatedArraySwapOutputStream.newInstance(2)) {
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
}
