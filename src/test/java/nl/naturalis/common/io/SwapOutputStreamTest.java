package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SwapOutputStreamTest {

  static class TestSwapOutputStream extends SwapOutputStream {

    private static ByteArrayOutputStream swapTo = new ByteArrayOutputStream();

    public TestSwapOutputStream(int treshold) {
      super(() -> (swapTo = new ByteArrayOutputStream()), treshold);
    }

    public TestSwapOutputStream() {
      super(() -> (swapTo = new ByteArrayOutputStream()));
    }

    @Override
    public void recall(OutputStream output) throws IOException {}

    public String getContents() {
      if (hasSwapped()) {
        return new String(swapTo.toByteArray(), StandardCharsets.UTF_8);
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        readBuffer(baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void test01() throws IOException {
    try (TestSwapOutputStream sos = new TestSwapOutputStream(0)) {}
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
