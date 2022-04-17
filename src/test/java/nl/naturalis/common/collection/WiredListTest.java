package nl.naturalis.common.collection;

import java.util.Iterator;
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

  @Test
  public void insert00() {
    var wl = new WiredList<String>();
    wl.insert(0, "John"); // John
    wl.insert(0, "Mark"); // Mark, John
    wl.insert(2, "Michael"); // Mark, John, Michael
    wl.insert(2, "James"); // Mark, John, James, Michael
    wl.insert(1, "Simon"); // Mark, Simon, John, James, Michael
    assertEquals(List.of("Mark", "Simon", "John", "James", "Michael"), wl);
  }

  @Test
  public void delete00() {
    var wl = new WiredList<String>();
    wl.insert(0, "John"); // John
    wl.insert(0, "Mark"); // Mark, John
    wl.insert(2, "Michael"); // Mark, John, Michael
    wl.insert(2, "James"); // Mark, John, James, Michael
    wl.insert(1, "Simon"); // Mark, Simon, John, James, Michael
    assertEquals(5, wl.size());
    wl.cut(0);
    assertEquals(4, wl.size());
    assertEquals(List.of("Simon", "John", "James", "Michael"), wl);
    wl.cut(2);
    assertEquals(3, wl.size());
    assertEquals(List.of("Simon", "John", "Michael"), wl);
    wl.cut(2);
    assertEquals(2, wl.size());
    assertEquals(List.of("Simon", "John"), wl);
    wl.cut(1);
    assertEquals(1, wl.size());
    assertEquals(List.of("Simon"), wl);
    wl.cut(0);
    assertEquals(0, wl.size());
    assertEquals(List.of(), wl);
  }

  @Test
  public void cut00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(0, 0);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(new WiredList<>(), wl1);
  }

  @Test
  public void cut01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(0, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0), wl1);
  }

  @Test
  public void cut02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(9, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
    assertEquals(WiredList.of(9), wl1);
  }

  @Test
  public void cut03() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(8, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 9), wl0);
    assertEquals(WiredList.of(8), wl1);
  }

  @Test
  public void cut04() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0, 1), wl1);
  }

  @Test
  public void cut05() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(8, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7), wl0);
    assertEquals(WiredList.of(8, 9), wl1);
  }

  @Test
  public void cut06() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(1, 8);
    assertEquals(2, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(WiredList.of(0, 9), wl0);
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8), wl1);
  }

  @Test
  public void cut07() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.cut(5, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(5), wl1);
  }

  @Test
  public void ItrTest00() {
    var wl = new WiredList<Integer>();
    var itr = wl.iterator();
    assertFalse(itr.hasNext());
  }

  @Test
  public void ItrTest01() {
    var wl = WiredList.of(0);
    var itr = wl.iterator();
    assertTrue(itr.hasNext());
    assertEquals(0, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void ItrTest02() {
    var wl = WiredList.of(0, 1, 2, 3);
    var itr = wl.iterator();
    assertTrue(itr.hasNext());
    assertEquals(0, (int) itr.next());
    assertEquals(1, (int) itr.next());
    assertEquals(2, (int) itr.next());
    assertEquals(3, (int) itr.next());
    assertFalse(itr.hasNext());
  }

}
