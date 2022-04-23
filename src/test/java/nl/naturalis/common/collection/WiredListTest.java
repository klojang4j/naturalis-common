package nl.naturalis.common.collection;

import java.util.List;

import org.junit.Test;

import static nl.naturalis.common.ArrayMethods.pack;
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
    wl.insert(0, "John");
    assertEquals(List.of("John"), wl);
    wl.insert(0, "Mark");
    assertEquals(List.of("Mark", "John"), wl);
    wl.insert(2, "Michael");
    assertEquals(List.of("Mark", "John", "Michael"), wl);
    wl.insert(2, "James");
    assertEquals(List.of("Mark", "John", "James", "Michael"), wl);
    wl.insert(1, "Simon");
    assertEquals(List.of("Mark", "Simon", "John", "James", "Michael"), wl);
    wl.insert(1, "Josh");
    assertEquals(List.of("Mark", "Josh", "Simon", "John", "James", "Michael"), wl);
    wl.insert(4, "Mary");
    assertEquals(List.of("Mark", "Josh", "Simon", "John", "Mary", "James", "Michael"), wl);
    wl.insert(3, "Jill");
    assertEquals(List.of("Mark", "Josh", "Simon", "Jill", "John", "Mary", "James", "Michael"), wl);
    wl.insert(4, "Ana");
    assertEquals(List.of("Mark",
        "Josh",
        "Simon",
        "Jill",
        "Ana",
        "John",
        "Mary",
        "James",
        "Michael"), wl);
  }

  @Test
  public void insertAll00() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, "a", "b", "c", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll01() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, List.of("a"));
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll02() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll03() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(wl.size(), List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b", "c"), wl);
  }

  @Test
  public void insertAll04() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(0, List.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll05() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(wl.size(), List.of("a"));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a"), wl);
  }

  @Test
  public void insertAll06() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(0, List.of("a"));
    assertEquals(List.of("a", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll07() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(wl.size(), List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll08() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(0, List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll09() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, WiredList.of("a", "b", "c"));
    assertEquals(List.of(0, 1, "a", "b", "c", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll10() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, WiredList.of("a"));
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll11() {
    var wl = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insertAll(2, new WiredList<>());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
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
    wl.delete(0);
    assertEquals(4, wl.size());
    assertEquals(List.of("Simon", "John", "James", "Michael"), wl);
    wl.delete(2);
    assertEquals(3, wl.size());
    assertEquals(List.of("Simon", "John", "Michael"), wl);
    wl.delete(2);
    assertEquals(2, wl.size());
    assertEquals(List.of("Simon", "John"), wl);
    wl.delete(1);
    assertEquals(1, wl.size());
    assertEquals(List.of("Simon"), wl);
    wl.delete(0);
    assertEquals(0, wl.size());
    assertEquals(List.of(), wl);
  }

  @Test
  public void deleteSegment00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 0);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(new WiredList<>(), wl1);
  }

  @Test
  public void deleteSegment01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0), wl1);
  }

  @Test
  public void deleteSegment02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(9, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
    assertEquals(WiredList.of(9), wl1);
  }

  @Test
  public void deleteSegment03() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(8, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 9), wl0);
    assertEquals(WiredList.of(8), wl1);
  }

  @Test
  public void deleteSegment04() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0, 1), wl1);
  }

  @Test
  public void deleteSegment05() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(8, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7), wl0);
    assertEquals(WiredList.of(8, 9), wl1);
  }

  @Test
  public void deleteSegment06() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(1, 8);
    assertEquals(2, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(WiredList.of(0, 9), wl0);
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8), wl1);
  }

  @Test
  public void deleteSegment07() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(5, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(5), wl1);
  }

  @Test
  public void deleteSegment08() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(5, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 7, 8, 9), wl0);
    assertEquals(WiredList.of(5, 6), wl1);
  }

  @Test
  public void deleteSegment09() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(5, 3);
    assertEquals(7, wl0.size());
    assertEquals(3, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 8, 9), wl0);
    assertEquals(WiredList.of(5, 6, 7), wl1);
  }

  @Test
  public void deleteRegion00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteRegion(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0, 1), wl1);
  }

  @Test
  public void deleteRegion01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteRegion(10, 10);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of(), wl1);
  }

  @Test
  public void deleteRegion02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteRegion(9, 10);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
    assertEquals(List.of(9), wl1);
  }

  @Test
  public void embed00() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of("a", "b");
    wl0.embed(0, wl1);
    assertEquals(12, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(WiredList.of("a", "b", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void embed01() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of("a", "b");
    wl0.embed(10, wl1);
    assertEquals(12, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "a", "b"), wl0);
  }

  @Test
  public void embed02() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of("a", "b");
    wl0.embed(9, wl1);
    assertEquals(12, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, "a", "b", 9), wl0);
  }

  @Test
  public void IteratorTest00() {
    var wl = new WiredList<Integer>();
    var itr = wl.iterator();
    assertFalse(itr.hasNext());
  }

  @Test
  public void IteratorTest01() {
    var wl = WiredList.of(0);
    var itr = wl.iterator();
    assertTrue(itr.hasNext());
    assertEquals(0, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void IteratorTest02() {
    var wl = WiredList.of(0, 1, 2, 3);
    var itr = wl.iterator();
    assertTrue(itr.hasNext());
    assertEquals(0, (int) itr.next());
    assertEquals(1, (int) itr.next());
    assertEquals(2, (int) itr.next());
    assertEquals(3, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void toArray00() {
    var wl = WiredList.of(0, 1, 2, 3);
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray(new Integer[0]));
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray(new Integer[4]));
    Integer[] ints = wl.toArray(new Integer[5]);
    assertNull(ints[4]);
  }

  @Test
  public void toArray01() {
    var wl = WiredList.of(0, 1, 2, 3);
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray());
  }

}
