package nl.naturalis.common.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;

public class SwapOutputStreamTest {

  class TestSwapOutputStream extends SwapOutputStream {

    public TestSwapOutputStream(int treshold) {
      super(new ByteArrayOutputStream(), treshold);
    }

    public TestSwapOutputStream() {
      super(new ByteArrayOutputStream());
    }

    @Override
    public void collect(OutputStream output) throws IOException {}

    public String getContents() {
      if (hasSwapped()) {
        return new String(((ByteArrayOutputStream) out).toByteArray(), StandardCharsets.UTF_8);
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        writeBuffer(baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** Test with zero-size internal buffer. Pointless but allowed. */
  @Test
  public void test01() {
    TestSwapOutputStream sos = new TestSwapOutputStream(0);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", sos.getContents());
  }

  @Test
  public void test02() {
    TestSwapOutputStream sos = new TestSwapOutputStream(1);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", sos.getContents());
  }

  @Test
  public void test03() {
    TestSwapOutputStream sos = new TestSwapOutputStream(2);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertTrue("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", sos.getContents());
  }

  @Test
  public void test04() {
    TestSwapOutputStream sos = new TestSwapOutputStream(100);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", sos.getContents());
  }

  @Test
  public void test05() {
    int sz = "Hello, world".getBytes(StandardCharsets.UTF_8).length;
    TestSwapOutputStream sos = new TestSwapOutputStream(sz);
    try (PrintWriter pw = new PrintWriter(sos)) {
      pw.append("Hello, world");
    }
    assertFalse("01", sos.hasSwapped());
    assertEquals("02", "Hello, world", sos.getContents());
  }
}
