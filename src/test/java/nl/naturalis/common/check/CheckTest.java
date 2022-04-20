package nl.naturalis.common.check;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings({"rawtypes"})
public class CheckTest {

  @Test(expected = IllegalArgumentException.class)
  public void notNull00() {
    Check.notNull(null);
  }

  @Test
  public void notNull01() {
    Object obj = Check.notNull(new Object());
    assertTrue(obj instanceof ObjectCheck);
  }

  @Test
  public void fail00() {
    try {
      Check.fail(IndexOutOfBoundsException::new, "Got that wrong ${0}", "bro");
    } catch (IndexOutOfBoundsException e) {
      assertEquals("Got that wrong bro", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void fail01() {
    try {
      Check.fail("Got that wrong ${0}", "bro");
    } catch (IllegalArgumentException e) {
      assertEquals("Got that wrong bro", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fail02() {
    Check.fail(IndexOutOfBoundsException::new);
  }

  @Test
  public void offsetLength00() {
    Check.offsetLength(new byte[0], 0, 0);
    Check.offsetLength(new byte[1], 0, 0);
    // Allowed: new ByteArrayOutputStream().write(new byte[1], 1, 0) !!
    Check.offsetLength(new byte[1], 1, 0);
    Check.offsetLength(new byte[1], 0, 1);
    Check.offsetLength(new byte[2], 0, 2);
    Check.offsetLength(0, 0, 0);
    Check.offsetLength(1, 0, 0);
    Check.offsetLength(1, 0, 1);
    Check.offsetLength(1, 1, 0);
    Check.offsetLength(2, 0, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength01() {
    Check.offsetLength(new byte[0], 0, 1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength02() {
    Check.offsetLength(new byte[0], 1, 1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength03() {
    Check.offsetLength(new byte[0], 1, 0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength04() {
    Check.offsetLength(new byte[4], 3, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength05() {
    Check.offsetLength(-10, 1, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength06() {
    Check.offsetLength(10, -1, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength07() {
    Check.offsetLength(10, 1, -3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void offsetLength08() {
    Check.offsetLength(10, 1, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void offsetLength09() {
    Check.offsetLength((List) null, 1, 10);
  }

  @Test
  public void fromTo00() {
    Check.fromTo("123", 0, 0);
    Check.fromTo("123", 3, 3);
    Check.fromTo("123", 2, 3);
    Check.fromTo(List.of(1, 2, 3), 0, 0);
    Check.fromTo(List.of(1, 2, 3), 3, 3);
    Check.fromTo(List.of(1, 2, 3), 2, 3);
    Check.fromTo(3, 0, 0);
    Check.fromTo(3, 3, 3);
    Check.fromTo(3, 2, 3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fromTo01() {
    Check.fromTo("123", 3, 4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fromTo02() {
    Check.fromTo((String) null, 3, 4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fromTo03() {
    Check.fromTo((List) null, 3, 4);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fromTo04() {
    Check.fromTo(-10, 3, 4);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fromTo05() {
    Check.fromTo(10, -3, 4);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fromTo06() {
    Check.fromTo(10, 3, -4);
  }

}
