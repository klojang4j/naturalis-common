package nl.naturalis.common.collection;

import java.util.List;

import org.junit.Test;

import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.check.CommonChecks.LTE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

public class WiredListTest {

  @Test
  public void push00() {
    var wl = new WiredList<String>();
    assertTrue(wl.isEmpty());
    wl.push("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void push01() {
    var wl = new WiredList<String>();
    wl.push("John");
    wl.push(null);
    assertEquals(2, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
  }

  @Test
  public void push02() {
    var wl = new WiredList<String>();
    wl.push("John");
    wl.push(null);
    wl.push("Jim");
    assertEquals(3, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("Jim", wl.get(2));
  }

  @Test
  public void push03() {
    var wl = new WiredList<String>();
    wl.push("John");
    wl.push(null);
    wl.push("Jim");
    wl.push(null);
    assertEquals(4, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("Jim", wl.get(2));
    assertNull(wl.get(3));
  }

  @Test
  public void unshift00() {
    var wl = new WiredList<String>();
    assertTrue(wl.isEmpty());
    wl.unshift("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void unshift01() {
    var wl = new WiredList<String>();
    wl.unshift("John");
    wl.unshift(null);
    assertEquals(2, wl.size());
    assertNull(wl.get(0));
    assertEquals("John", wl.get(1));
  }

  @Test
  public void unshift02() {
    var wl = new WiredList<String>();
    wl.unshift("John");
    wl.unshift(null);
    wl.unshift("Jim");
    assertEquals(3, wl.size());
    assertEquals("Jim", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("John", wl.get(2));
  }

  @Test
  public void unshift03() {
    var wl = new WiredList<String>();
    wl.unshift("John");
    wl.unshift(null);
    wl.unshift("Jim");
    wl.unshift(null);
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
  public void remove00() {
    var wl = new WiredList<String>();
    wl.insert(0, "John"); // John
    wl.insert(0, "Mark"); // Mark, John
    wl.insert(2, "Michael"); // Mark, John, Michael
    wl.insert(2, "James"); // Mark, John, James, Michael
    wl.insert(1, "Simon"); // Mark, Simon, John, James, Michael
    assertEquals(5, wl.size());
    wl.remove(0);
    assertEquals(4, wl.size());
    assertEquals(List.of("Simon", "John", "James", "Michael"), wl);
    wl.remove(2);
    assertEquals(3, wl.size());
    assertEquals(List.of("Simon", "John", "Michael"), wl);
    wl.remove(2);
    assertEquals(2, wl.size());
    assertEquals(List.of("Simon", "John"), wl);
    wl.remove(1);
    assertEquals(1, wl.size());
    assertEquals(List.of("Simon"), wl);
    wl.remove(0);
    assertEquals(0, wl.size());
    assertEquals(List.of(), wl);
  }

  @Test
  public void deleteSegment00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(0, 0);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(new WiredList<>(), wl1);
  }

  @Test
  public void deleteSegment01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(0, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0), wl1);
  }

  @Test
  public void deleteSegment02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(9, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
    assertEquals(WiredList.of(9), wl1);
  }

  @Test
  public void deleteSegment03() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(8, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 9), wl0);
    assertEquals(WiredList.of(8), wl1);
  }

  @Test
  public void deleteSegment04() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0, 1), wl1);
  }

  @Test
  public void deleteSegment05() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(8, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7), wl0);
    assertEquals(WiredList.of(8, 9), wl1);
  }

  @Test
  public void deleteSegment06() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(1, 8);
    assertEquals(2, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(WiredList.of(0, 9), wl0);
    assertEquals(WiredList.of(1, 2, 3, 4, 5, 6, 7, 8), wl1);
  }

  @Test
  public void deleteSegment07() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(5, 1);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(5), wl1);
  }

  @Test
  public void deleteSegment08() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(5, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 7, 8, 9), wl0);
    assertEquals(WiredList.of(5, 6), wl1);
  }

  @Test
  public void deleteSegment09() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.removeSegment(5, 3);
    assertEquals(7, wl0.size());
    assertEquals(3, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 8, 9), wl0);
    assertEquals(WiredList.of(5, 6, 7), wl1);
  }

  @Test
  public void deleteRegion00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.remove(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(WiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(WiredList.of(0, 1), wl1);
  }

  @Test
  public void deleteRegion01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.remove(10, 10);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of(), wl1);
  }

  @Test
  public void deleteRegion02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.remove(9, 10);
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
  public void embed03() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of("a");
    wl0.embed(10, wl1);
    assertEquals(11, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "a"), wl0);
  }

  @Test
  public void embed05() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of("a");
    wl0.embed(0, wl1);
    assertEquals(11, wl0.size());
    assertEquals(List.of("a", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void embed06() {
    var wl0 = new WiredList<Object>();
    var wl1 = WiredList.of("a");
    wl0.embed(0, wl1);
    assertEquals(1, wl0.size());
    assertEquals(List.of("a"), wl0);
  }

  @Test
  public void embed07() {
    var wl0 = new WiredList<Object>();
    var wl1 = WiredList.of("a", "b");
    wl0.embed(0, wl1);
    assertEquals(2, wl0.size());
    assertEquals(List.of("a", "b"), wl0);
  }

  @Test
  public void embed08() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = new WiredList<String>();
    wl0.embed(4, wl1);
    assertEquals(10, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void transfer00() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(0, wl1, 0, 3);
    assertEquals(13, wl0.size());
    assertEquals(7, wl1.size());
    assertEquals(List.of('a', 'b', 'c', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer01() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(2, wl1, 0, 3);
    assertEquals(13, wl0.size());
    assertEquals(7, wl1.size());
    assertEquals(List.of(0, 1, 'a', 'b', 'c', 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer02() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(0, wl1, 8, 10);
    assertEquals(12, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(List.of('i', 'j', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'), wl1);
  }

  @Test
  public void transfer03() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(4, wl1, 3, 9);
    assertEquals(16, wl0.size());
    assertEquals(4, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 'd', 'e', 'f', 'g', 'h', 'i', 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'c', 'j'), wl1);
  }

  @Test
  public void transfer04() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(4, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 'c', 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer05() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(0, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of('c', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer06() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(10, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'c'), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer07() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(9, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 'c', 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer08() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = WiredList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
    wl0.transfer(9, wl1, 2, 2); // Allowed !
    assertEquals(10, wl0.size());
    assertEquals(10, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer09() {
    var wl0 = WiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = new WiredList<Character>();
    wl0.transfer(9, wl1, 0, 0); // Allowed !
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
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

  @Test
  public void setIf00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.setIf(1, i -> i > 5, 10);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    wl0.setIf(6, i -> i > 5, 10);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 10, 7, 8, 9), wl0);
  }

  @Test
  public void setIf01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.setIf(3, LTE(), 5);
    assertEquals(List.of(0, 1, 2, 5, 4, 5, 6, 7, 8, 9), wl0);
    wl0.setIf(6, LTE(), 5);
    assertEquals(List.of(0, 1, 2, 5, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void indexOf00() {
    var wl = WiredList.of("000", "111", "222", "333", "444", "555", "666", "777", "888", "999");
    assertEquals(0, wl.indexOf("000"));
    assertEquals(5, wl.indexOf("555"));
    assertEquals(9, wl.indexOf("999"));
    assertEquals(-1, wl.indexOf("foo"));
  }

  @Test
  public void lastIndexOf00() {
    var wl = WiredList.of("000", "111", "222", "333", "444", "555", "666", "777", "888", "999");
    assertEquals(0, wl.indexOf("000"));
    assertEquals(5, wl.indexOf("555"));
    assertEquals(9, wl.indexOf("999"));
    assertEquals(-1, wl.indexOf("foo"));
  }

  @Test
  public void move00() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 1);
    assertEquals(List.of(4, 0, 1, 2, 3, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void move01() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 2);
    assertEquals(List.of(4, 5, 0, 1, 2, 3, 6, 7, 8, 9), wl0);
  }

  @Test
  public void move02() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 3);
    assertEquals(List.of(4, 5, 6, 0, 1, 2, 3, 7, 8, 9), wl0);
  }

  @Test
  public void move03() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 4);
    assertEquals(List.of(4, 5, 6, 7, 0, 1, 2, 3, 8, 9), wl0);
  }

  @Test
  public void move04() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 5);
    assertEquals(List.of(4, 5, 6, 7, 8, 0, 1, 2, 3, 9), wl0);
  }

  @Test
  public void move05() {
    var wl0 = WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 6);
    assertEquals(List.of(4, 5, 6, 7, 8, 9, 0, 1, 2, 3), wl0);
  }

}
