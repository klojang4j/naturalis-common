package nl.naturalis.common;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static nl.naturalis.common.IOMethods.*;
import static org.junit.Assert.*;

public class IOMethodsTest {

  @Test
  public void toString00() throws IOException {
    String s = getContents(getClass(), "/IOMethodsTest.foo.txt");
    assertEquals("This is a test.", s);
    // Bad, but not forbidden:
    s = getContents(getClass(), "/IOMethodsTest.foo.txt", 1);
    assertEquals("This is a test.", s);
    s = getContents(getClass(), "/IOMethodsTest.foo.txt", 2048);
    assertEquals("This is a test.", s);
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertEquals("This is a test.", getContents(is));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertEquals("This is a test.", getContents(is, 13));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertEquals("This is a test.", getContents(is, 1));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertEquals("This is a test.", getContents(is, 2));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertEquals("This is a test.", getContents(is, "This is a test.".length()));
    }

  }

  @Test
  public void read00() throws IOException {
    byte[] expected = "This is a test.".getBytes(StandardCharsets.UTF_8);
    byte[] bytes = read(getClass(), "/IOMethodsTest.foo.txt");
    assertArrayEquals(expected, bytes);
    // Bad, but not forbidden:
    bytes = read(getClass(), "/IOMethodsTest.foo.txt", 1);
    assertArrayEquals(expected, bytes);
    bytes = read(getClass(), "/IOMethodsTest.foo.txt", 2048);
    assertArrayEquals(expected, bytes);
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertArrayEquals(expected, read(is));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertArrayEquals(expected, read(is, 13));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertArrayEquals(expected, read(is, 1));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertArrayEquals(expected, read(is, 2));
    }
    try (InputStream is = getClass().getResourceAsStream("/IOMethodsTest.foo.txt")) {
      assertArrayEquals(expected, read(is, expected.length));
    }

  }

}
