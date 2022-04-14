package nl.naturalis.common.collection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

public class WiredListTest {

  @Test
  public void append00() {
    var wl = new WiredList<String>();
    assertTrue(wl.isEmpty());
    wl.append("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void append01() {
    var wl = new WiredList<String>();
    wl.append("John");
    wl.append(null);
    assertEquals(2, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
  }

  @Test
  public void append02() {
    var wl = new WiredList<String>();
    wl.append("John");
    wl.append(null);
    wl.append("Jim");
    assertEquals(3, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("Jim", wl.get(2));
  }

  @Test
  public void append03() {
    var wl = new WiredList<String>();
    wl.append("John");
    wl.append(null);
    wl.append("Jim");
    wl.append(null);
    assertEquals(4, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("Jim", wl.get(2));
    assertNull(wl.get(3));
  }

  @Test
  public void prepend00() {
    var wl = new WiredList<String>();
    assertTrue(wl.isEmpty());
    wl.prepend("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void prepend01() {
    var wl = new WiredList<String>();
    wl.prepend("John");
    wl.prepend(null);
    assertEquals(2, wl.size());
    assertNull(wl.get(0));
    assertEquals("John", wl.get(1));
  }

  @Test
  public void prepend02() {
    var wl = new WiredList<String>();
    wl.prepend("John");
    wl.prepend(null);
    wl.prepend("Jim");
    assertEquals(3, wl.size());
    assertEquals("Jim", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("John", wl.get(2));
  }

  @Test
  public void prepend03() {
    var wl = new WiredList<String>();
    wl.prepend("John");
    wl.prepend(null);
    wl.prepend("Jim");
    wl.prepend(null);
    assertEquals(4, wl.size());
    assertNull(wl.get(0));
    assertEquals("Jim", wl.get(1));
    assertNull(wl.get(2));
    assertEquals("John", wl.get(3));
  }

}
