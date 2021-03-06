package nl.naturalis.common.collection;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyListIterator;
import static nl.naturalis.common.ArrayMethods.pack;
import static org.junit.Assert.*;

public class SynchronizedWiredListTest {

  /*
   * Tests whether the SynchronizedWiredList holds up against the onslaught,
   * and that the underlying WiredList remains in a consistent state, without
   * broken links between the nodes (i.e. no NPEs please)
   */
  @Test
  public void test00() throws InterruptedException {
    SynchronizedWiredList<Integer> swl = new SynchronizedWiredList<>();
    ExecutorService pool = Executors.newCachedThreadPool();
    //Collections.shuffle(callables);
    //pool.invokeAll(callables);
    pool.shutdownNow();
  }

  @Test
  public void join00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2);
    var wl1 = SynchronizedWiredList.of(3, 4, 5);
    var wl2 = SynchronizedWiredList.<Integer>of();
    var wl3 = SynchronizedWiredList.of(6);
    var wl4 = SynchronizedWiredList.of(7, 8, 9);
    var wl5 = SynchronizedWiredList.join(List.of(wl0, wl1, wl2, wl3, wl4));
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl5);
  }

  @Test
  public void constructor00() {
    var wl = new SynchronizedWiredList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertEquals(WiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void readIntensive00() {
    var wl = new SynchronizedWiredList<Object>(true);
    wl.insertAll(0, List.of(0, 1, 2, 3, 4, 5));
    wl.embed(0, SynchronizedWiredList.of('a', 'b', 'c'));
    assertEquals(WiredList.of('a', 'b', 'c', 0, 1, 2, 3, 4, 5), wl);
    var segment0 = wl.deleteSegment(1, wl.size() - 1);
    assertEquals(WiredList.of('a', 5), wl);
    assertEquals(WiredList.of('b', 'c', 0, 1, 2, 3, 4), segment0);
    segment0.move(0, 2, 0);
    assertEquals(WiredList.of('b', 'c', 0, 1, 2, 3, 4), segment0);
    segment0.move(0, 2, 3);
    assertEquals(WiredList.of(0, 1, 2, 'b', 'c', 3, 4), segment0);
    SynchronizedWiredList<SynchronizedWiredList<Object>> groups =
        segment0.group(List.of(x -> x instanceof Character,
        x -> ((Integer) x) >= 3));
    assertEquals(3, groups.size());
    assertEquals(List.of('b', 'c'), groups.get(0));
    assertEquals(List.of(3, 4), groups.get(1));
    assertEquals(List.of(0, 1, 2), groups.get(2));
  }

  @Test
  public void append00() {
    var wl = new SynchronizedWiredList<String>();
    assertTrue(wl.isEmpty());
    wl.append("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void append01() {
    var wl = new SynchronizedWiredList<String>();
    wl.append("John");
    wl.append(null);
    assertEquals(2, wl.size());
    assertEquals("John", wl.get(0));
    assertNull(wl.get(1));
  }

  @Test
  public void append02() {
    var wl = new SynchronizedWiredList<String>();
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
    var wl = new SynchronizedWiredList<String>();
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
  public void unshift00() {
    var wl = new SynchronizedWiredList<String>();
    assertTrue(wl.isEmpty());
    wl.prepend("John");
    assertFalse(wl.isEmpty());
    assertEquals(1, wl.size());
    assertEquals("John", wl.get(0));
  }

  @Test
  public void unshift01() {
    var wl = new SynchronizedWiredList<String>();
    wl.prepend("John");
    wl.prepend(null);
    assertEquals(2, wl.size());
    assertNull(wl.get(0));
    assertEquals("John", wl.get(1));
  }

  @Test
  public void unshift02() {
    var wl = new SynchronizedWiredList<String>();
    wl.prepend("John");
    wl.prepend(null);
    wl.prepend("Jim");
    assertEquals(3, wl.size());
    assertEquals("Jim", wl.get(0));
    assertNull(wl.get(1));
    assertEquals("John", wl.get(2));
  }

  @Test
  public void unshift03() {
    var wl = new SynchronizedWiredList<String>();
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
  public void prepend00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertEquals(0, (int) wl.deleteFirst());
    assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
    assertEquals(1, (int) wl.deleteFirst());
    assertEquals(List.of(2, 3, 4, 5, 6, 7, 8, 9), wl);
    wl.prepend(1);
    assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
    wl.prepend(0);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void popPush00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertEquals(9, (int) wl.deleteLast());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl);
    assertEquals(8, (int) wl.deleteLast());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7), wl);
    wl.append(8);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl);
    wl.append(9);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void add00() {
    var wl = new SynchronizedWiredList<String>();
    wl.add(0, "John");
    assertEquals(List.of("John"), wl);
    wl.add(0, "Mark");
    assertEquals(List.of("Mark", "John"), wl);
    wl.add(2, "Michael");
    assertEquals(List.of("Mark", "John", "Michael"), wl);
    wl.add(2, "James");
    assertEquals(List.of("Mark", "John", "James", "Michael"), wl);
    wl.add(1, "Simon");
    assertEquals(List.of("Mark", "Simon", "John", "James", "Michael"), wl);
    wl.add(1, "Josh");
    assertEquals(List.of("Mark", "Josh", "Simon", "John", "James", "Michael"), wl);
    wl.add(4, "Mary");
    assertEquals(List.of("Mark",
        "Josh",
        "Simon",
        "John",
        "Mary",
        "James",
        "Michael"), wl);
    wl.add(3, "Jill");
    assertEquals(List.of("Mark",
        "Josh",
        "Simon",
        "Jill",
        "John",
        "Mary",
        "James",
        "Michael"), wl);
    wl.add(4, "Ana");
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
  public void addAll00() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, "a", "b", "c", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll01() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, List.of("a"));
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll02() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll03() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(wl.size(), List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b", "c"), wl);
  }

  @Test
  public void addAll04() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(0, List.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll05() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(wl.size(), List.of("a"));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a"), wl);
  }

  @Test
  public void addAll06() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(0, List.of("a"));
    assertEquals(List.of("a", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll07() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(wl.size(), List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll08() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(0, List.of());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll09() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, SynchronizedWiredList.of("a", "b", "c"));
    assertEquals(List.of(0, 1, "a", "b", "c", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll10() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, SynchronizedWiredList.of("a"));
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll11() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(2, new SynchronizedWiredList<>());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void addAll12() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.addAll(List.of("a", "b", "c", "d"));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b", "c", "d"), wl);
    wl.addAll(SynchronizedWiredList.of('e', 'f'));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b", "c", "d", 'e', 'f'), wl);
  }

  @Test
  public void insert00() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    var wl2 = wl.insert(2, "a");
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insert01() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insert(2, "a");
    assertEquals(List.of(0, 1, "a", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insert02() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6);
    wl.insert(7, "a");
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a"), wl);
  }

  @Test
  public void insertAll00() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.insertAll(2, List.of("a", "b", "c"));
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, "a", "b", "c", 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll01() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.insertAll(7, List.of("a", "b", "c"));
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b", "c"), wl);
  }

  @Test
  public void insertAll02() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.insertAll(0, List.of("a", "b", "c"));
    assertSame(wl, wl2);
    assertEquals(List.of("a", "b", "c", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll03() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.insertAll(0, List.of());
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void insertAll04() {
    var wl = SynchronizedWiredList.<Object>of();
    var wl2 = wl.insertAll(0, List.of());
    assertSame(wl, wl2);
    assertEquals(List.of(), wl);
  }

  @Test
  public void insertAll05() {
    var wl = SynchronizedWiredList.<Object>of();
    var wl2 = wl.insertAll(0, List.of(1, 2, 3));
    assertSame(wl, wl2);
    assertEquals(List.of(1, 2, 3), wl);
  }

  @Test
  public void insertAll06() {
    var wl0 = SynchronizedWiredList.<Object>of();
    var wl1 = SynchronizedWiredList.of(1, 2, 3);
    var wl2 = wl0.insertAll(0, wl1);
    assertSame(wl0, wl2);
    assertEquals(List.of(1, 2, 3), wl0);
    assertEquals(List.of(1, 2, 3), wl1); // argument is read only
  }

  @Test
  public void prependAll00() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.prependAll(List.of("a"));
    assertSame(wl, wl2);
    assertEquals(List.of("a", 0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void prependAll01() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.prependAll(List.of());
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void prependAll02() {
    var wl0 = SynchronizedWiredList.of(3, 4, 5);
    var wl1 = wl0.prependAll(SynchronizedWiredList.of(0, 1, 2));
    assertSame(wl0, wl1);
    assertEquals(List.of(0, 1, 2, 3, 4, 5), wl0);
  }

  @Test
  public void appendAll00() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.appendAll(List.of("a", "b"));
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, "a", "b"), wl);
  }

  @Test
  public void appendAll01() {
    var wl = SynchronizedWiredList.ofElements(new Object[] {0, 1, 2, 3, 4, 5, 6});
    var wl2 = wl.appendAll(List.of());
    assertSame(wl, wl2);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void appendAll02() {
    var wl = SynchronizedWiredList.<Object>of(0, 1, 2, 3);
    var wl2 = wl.appendAll(SynchronizedWiredList.of(4, 5, 6));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), wl);
  }

  @Test
  public void cut01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 2);
    assertEquals(8, wl0.size());
    assertEquals(2, wl1.size());
    assertEquals(SynchronizedWiredList.of(2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(SynchronizedWiredList.of(0, 1), wl1);
  }

  @Test
  public void cut02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(9, 10);
    assertEquals(9, wl0.size());
    assertEquals(1, wl1.size());
    assertEquals(SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
    assertEquals(List.of(9), wl1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void cut03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 0);
    assertEquals(10, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void cut04() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(6, 10);
    assertEquals(6, wl0.size());
    assertEquals(4, wl1.size());
    assertEquals(SynchronizedWiredList.of(0, 1, 2, 3, 4, 5), wl0);
    assertEquals(SynchronizedWiredList.of(6, 7, 8, 9), wl1);
  }

  @Test
  public void cut05() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(0, 10);
    assertEquals(0, wl0.size());
    assertEquals(10, wl1.size());
    assertEquals(SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl1);
  }

  @Test
  public void cut06() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.deleteSegment(1, 9);
    assertEquals(2, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(SynchronizedWiredList.of(0, 9), wl0);
    assertEquals(SynchronizedWiredList.of(1, 2, 3, 4, 5, 6, 7, 8), wl1);
  }

  @Test
  public void remove00() {
    var wl = new SynchronizedWiredList<String>();
    wl.add(0, "John"); // John
    wl.add(0, "Mark"); // Mark, John
    wl.add(2, "Michael"); // Mark, John, Michael
    wl.add(2, "James"); // Mark, John, James, Michael
    wl.add(1, "Simon"); // Mark, Simon, John, James, Michael
    assertEquals(List.of("Mark", "Simon", "John", "James", "Michael"), wl);
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
  public void remove01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    assertEquals(5, wl0.size());
    wl0.remove(0);
    assertEquals(List.of(1, 2, 3, 4), wl0);
    wl0.remove(0);
    assertEquals(List.of(2, 3, 4), wl0);
    wl0.remove(0);
    assertEquals(List.of(3, 4), wl0);
    wl0.remove(0);
    assertEquals(List.of(4), wl0);
    wl0.remove(0);
    assertEquals(List.of(), wl0);
  }

  @Test
  public void remove02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    assertEquals(5, wl0.size());
    wl0.remove(4);
    assertEquals(List.of(0, 1, 2, 3), wl0);
    wl0.remove(3);
    assertEquals(List.of(0, 1, 2), wl0);
    wl0.remove(2);
    assertEquals(List.of(0, 1), wl0);
    wl0.remove(1);
    assertEquals(List.of(0), wl0);
    wl0.remove(0);
    assertEquals(List.of(), wl0);
  }

  @Test
  public void remove03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    assertEquals(5, wl0.size());
    wl0.remove(2);
    assertEquals(List.of(0, 1, 3, 4), wl0);
    wl0.remove(2);
    assertEquals(List.of(0, 1, 4), wl0);
    wl0.remove(1);
    assertEquals(List.of(0, 4), wl0);
  }

  @Test
  public void remove04() {
    var wl0 = SynchronizedWiredList.of("0", "1", "2", "3", "4");
    assertTrue(wl0.remove("2"));
    assertEquals(List.of("0", "1", "3", "4"), wl0);
    assertFalse(wl0.remove("734"));
    assertEquals(List.of("0", "1", "3", "4"), wl0);
    assertTrue(wl0.remove("0"));
    assertEquals(List.of("1", "3", "4"), wl0);
    assertTrue(wl0.remove("4"));
    assertEquals(List.of("1", "3"), wl0);
  }

  @Test
  public void remove05() {
    var wl0 = SynchronizedWiredList.of("0", null, "2", null, "4");
    assertFalse(wl0.remove("1"));
    assertEquals(SynchronizedWiredList.of("0", null, "2", null, "4"), wl0);
    assertTrue(wl0.remove(null));
    assertEquals(SynchronizedWiredList.of("0", "2", null, "4"), wl0);
    assertTrue(wl0.remove(null));
    assertEquals(SynchronizedWiredList.of("0", "2", "4"), wl0);
  }

  @Test
  public void embed00() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a", "b");
    wl0.embed(0, wl1);
    assertEquals(12, wl0.size());
    assertEquals(0, wl1.size());
    assertEquals(SynchronizedWiredList.of("a", "b", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        wl0);
  }

  @Test
  public void embed01() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a", "b");
    wl0.embed(10, wl1);
    assertEquals(12, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "a", "b"), wl0);
  }

  @Test
  public void embed02() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a", "b");
    wl0.embed(9, wl1);
    assertEquals(12, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, "a", "b", 9), wl0);
  }

  @Test
  public void embed03() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a");
    wl0.embed(10, wl1);
    assertEquals(11, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "a"), wl0);
  }

  @Test
  public void embed05() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a");
    wl0.embed(0, wl1);
    assertEquals(11, wl0.size());
    assertEquals(List.of("a", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void embed06() {
    var wl0 = new SynchronizedWiredList<Object>();
    var wl1 = SynchronizedWiredList.of("a");
    wl0.embed(0, wl1);
    assertEquals(1, wl0.size());
    assertEquals(List.of("a"), wl0);
  }

  @Test
  public void embed07() {
    var wl0 = new SynchronizedWiredList<Object>();
    var wl1 = SynchronizedWiredList.of("a", "b");
    wl0.embed(0, wl1);
    assertEquals(2, wl0.size());
    assertEquals(List.of("a", "b"), wl0);
  }

  @Test
  public void embed08() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = new SynchronizedWiredList<String>();
    wl0.embed(4, wl1);
    assertEquals(10, wl0.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void stitch00() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of("a", "b");
    wl0.join(wl1);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "a", "b"), wl0);
  }

  @Test
  public void transfer00() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(0, wl1, 0, 3);
    assertEquals(13, wl0.size());
    assertEquals(7, wl1.size());
    assertEquals(List.of('a', 'b', 'c', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer01() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(2, wl1, 0, 3);
    assertEquals(13, wl0.size());
    assertEquals(7, wl1.size());
    assertEquals(List.of(0, 1, 'a', 'b', 'c', 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer02() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(0, wl1, 8, 10);
    assertEquals(12, wl0.size());
    assertEquals(8, wl1.size());
    assertEquals(List.of('i', 'j', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'), wl1);
  }

  @Test
  public void transfer03() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(4, wl1, 3, 9);
    assertEquals(16, wl0.size());
    assertEquals(4, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 'd', 'e', 'f', 'g', 'h', 'i', 4, 5, 6, 7, 8, 9),
        wl0);
    assertEquals(List.of('a', 'b', 'c', 'j'), wl1);
  }

  @Test
  public void transfer04() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(4, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 'c', 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer05() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(0, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of('c', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer06() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(10, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'c'), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test
  public void transfer07() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(9, wl1, 2, 3);
    assertEquals(11, wl0.size());
    assertEquals(9, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 'c', 9), wl0);
    assertEquals(List.of('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j'), wl1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void transfer08() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j');
    wl0.excise(9, wl1, 2, 2); // zero-length segment
  }

  @Test
  public void transfer09() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of('0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9');
    wl0.excise(wl1, 0, 4);
    assertEquals(14, wl0.size());
    assertEquals(6, wl1.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, '0', '1', '2', '3'), wl0);
    assertEquals(List.of('4', '5', '6', '7', '8', '9'), wl1);
  }

  @Test
  public void IteratorTest00() {
    var wl = new SynchronizedWiredList<Integer>();
    var itr = wl.iterator();
    assertFalse(itr.hasNext());
  }

  @Test
  public void IteratorTest01() {
    var wl = SynchronizedWiredList.of(0);
    var itr = wl.iterator();
    assertTrue(itr.hasNext());
    assertEquals(0, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void IteratorTest02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3);
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
    var wl = SynchronizedWiredList.of(0, 1, 2, 3);
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray(new Integer[0]));
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray(new Integer[4]));
    Integer[] ints = wl.toArray(new Integer[5]);
    assertNull(ints[4]);
  }

  @Test
  public void toArray01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3);
    assertArrayEquals(pack(0, 1, 2, 3), wl.toArray());
  }

  @Test
  public void set00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertEquals(2, (int) wl.set(2, 222));
    assertEquals(List.of(0, 1, 222, 3, 4, 5, 6, 7, 8, 9), wl);
    assertEquals(0, (int) wl.set(0, -546));
    assertEquals(List.of(-546, 1, 222, 3, 4, 5, 6, 7, 8, 9), wl);
    assertEquals(9, (int) wl.set(9, 999));
    assertEquals(List.of(-546, 1, 222, 3, 4, 5, 6, 7, 8, 999), wl);
  }

  @Test
  public void clear00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertTrue(wl.iterator().hasNext());
    assertTrue(wl.listIterator().hasNext());
    assertFalse(wl.listIterator().hasPrevious());
    assertTrue(wl.wiredIterator(true).hasNext());
    wl.clear();
    assertEquals(0, wl.size());
    assertFalse(wl.iterator().hasNext());
    assertFalse(wl.listIterator().hasNext());
    assertFalse(wl.listIterator().hasPrevious());
    assertFalse(wl.wiredIterator(true).hasNext());
    assertEquals(emptyIterator().getClass(), wl.iterator().getClass());
    assertEquals(emptyListIterator().getClass(), wl.listIterator().getClass());
  }

  @Test
  public void setIf00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.setIf(1, i -> i > 5, 10);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
    wl.setIf(6, i -> i > 5, 10);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 10, 7, 8, 9), wl);
  }

  @Test
  public void indexOf00() {
    var wl = SynchronizedWiredList.of("00",
        "11",
        "22",
        "33",
        "44",
        "55",
        "66",
        "77",
        "33",
        "88");
    assertEquals(0, wl.indexOf("00"));
    assertEquals(3, wl.indexOf("33"));
    assertEquals(5, wl.indexOf("55"));
    assertEquals(9, wl.indexOf("88"));
    assertEquals(-1, wl.indexOf("99"));
  }

  @Test
  public void indexOf01() {
    var wl = SynchronizedWiredList.of(0, 1, null, 3, 4, 5, 6, null, 8, 9);
    assertEquals(2, wl.indexOf(null));
  }

  @Test
  public void lastIndexOf00() {
    var wl = SynchronizedWiredList.of("00",
        "11",
        "22",
        "33",
        "44",
        "55",
        "66",
        "77",
        "33",
        "88");
    assertEquals(0, wl.lastIndexOf("00"));
    assertEquals(8, wl.lastIndexOf("33"));
    assertEquals(5, wl.lastIndexOf("55"));
    assertEquals(9, wl.lastIndexOf("88"));
    assertEquals(-1, wl.lastIndexOf("99"));
  }

  @Test
  public void lastIndexOf01() {
    var wl = SynchronizedWiredList.of(0, 1, null, 3, 4, 5, 6, null, 8, 9);
    assertEquals(7, wl.lastIndexOf(null));
  }

  @Test
  public void lchop00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i != 5);
    assertEquals(List.of(5, 6, 7, 8, 9), wl0);
    assertEquals(List.of(0, 1, 2, 3, 4), wl1);
  }

  @Test
  public void lchop01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i != 0);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of(), wl1);
  }

  @Test
  public void lchop02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i != 9);
    assertEquals(List.of(9), wl0);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), wl1);
  }

  @Test
  public void lchop03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i != 8);
    assertEquals(List.of(8, 9), wl0);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7), wl1);
  }

  @Test
  public void lchop04() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i == 666);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertTrue(wl1.isEmpty());
  }

  @Test
  public void lchop05() {
    SynchronizedWiredList<Integer> wl0 = new SynchronizedWiredList<>();
    var wl1 = wl0.lchop(i -> i == 666);
    assertTrue(wl0.isEmpty());
    assertTrue(wl1.isEmpty());
  }

  @Test
  public void lchop06() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.lchop(i -> i != 666);
    assertSame(wl0, wl1);
  }

  @Test
  public void rtrim00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.rchop(i -> i != 5);
    assertEquals(List.of(0, 1, 2, 3, 4, 5), wl0);
    assertEquals(List.of(6, 7, 8, 9), wl1);
  }

  @Test
  public void rtrim01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.rchop(i -> i != 9);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertEquals(List.of(), wl1);
  }

  @Test
  public void rtrim02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.rchop(i -> i != 0);
    assertEquals(List.of(0), wl0);
    assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), wl1);
  }

  @Test
  public void rtrim03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.rchop(i -> i == 666);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
    assertTrue(wl1.isEmpty());
  }

  @Test
  public void rtrim04() {
    SynchronizedWiredList<Integer> wl0 = new SynchronizedWiredList<>();
    var wl1 = wl0.rchop(i -> i == 666);
    assertTrue(wl0.isEmpty());
    assertTrue(wl1.isEmpty());
  }

  @Test
  public void rtrim06() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = wl0.rchop(i -> i != 666);
    assertSame(wl0, wl1);
  }

  @Test
  public void reverse00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.reverse();
    assertEquals(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0), wl0);
  }

  @Test
  public void reverse01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3);
    wl0.reverse();
    assertEquals(List.of(3, 2, 1, 0), wl0);
  }

  @Test
  public void reverse02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2);
    wl0.reverse();
    assertEquals(List.of(2, 1, 0), wl0);
  }

  @Test
  public void reverse03() {
    var wl0 = SynchronizedWiredList.of(0, 1);
    wl0.reverse();
    assertEquals(List.of(1, 0), wl0);
  }

  @Test
  public void reverse04() {
    var wl0 = SynchronizedWiredList.of(0);
    wl0.reverse();
    assertEquals(List.of(0), wl0);
  }

  @Test
  public void reverse05() {
    var wl0 = SynchronizedWiredList.of();
    wl0.reverse();
    assertEquals(List.of(), wl0);
  }

  @Test
  public void moveToTail00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 1);
    assertEquals(List.of(4, 0, 1, 2, 3, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void moveToTail01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 2);
    assertEquals(List.of(4, 5, 0, 1, 2, 3, 6, 7, 8, 9), wl0);
  }

  @Test
  public void moveToTail02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 3);
    assertEquals(List.of(4, 5, 6, 0, 1, 2, 3, 7, 8, 9), wl0);
  }

  @Test
  public void moveToTail03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 4);
    assertEquals(List.of(4, 5, 6, 7, 0, 1, 2, 3, 8, 9), wl0);
  }

  @Test
  public void moveToTail04() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 5);
    assertEquals(List.of(4, 5, 6, 7, 8, 0, 1, 2, 3, 9), wl0);
  }

  @Test
  public void moveToTail05() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 4, 6);
    assertEquals(List.of(4, 5, 6, 7, 8, 9, 0, 1, 2, 3), wl0);
  }

  @Test
  public void moveToTail06() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 6, 2);
    assertEquals(List.of(0, 6, 1, 2, 3, 4, 5, 7, 8, 9), wl0);
  }

  @Test
  public void moveToTail7() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 6, 3);
    assertEquals(List.of(0, 6, 7, 1, 2, 3, 4, 5, 8, 9), wl0);
  }

  @Test
  public void moveToTail08() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 6, 4);
    assertEquals(List.of(0, 6, 7, 8, 1, 2, 3, 4, 5, 9), wl0);
  }

  @Test
  public void moveToTail09() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 6, 5);
    assertEquals(List.of(0, 6, 7, 8, 9, 1, 2, 3, 4, 5), wl0);
  }

  @Test
  public void moveToTail10() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 1, 4);
    assertEquals(List.of(1, 2, 3, 4, 0, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void moveToTail11() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(7, 9, 8);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 9, 7, 8), wl0);
  }

  @Test
  public void moveToTail12() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(6, 8, 7);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 8, 6, 7, 9), wl0);
  }

  @Test
  public void moveToTail13() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(6, 9, 7);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 9, 6, 7, 8), wl0);
  }

  @Test
  public void moveToTail14() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(7, 8, 8);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 8, 7, 9), wl0);
  }

  @Test
  public void moveToTail15() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(0, 9, 1);
    assertEquals(List.of(9, 0, 1, 2, 3, 4, 5, 6, 7, 8), wl0);
  }

  @Test
  public void moveToHead00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(6, 8, 4);
    assertEquals(List.of(0, 1, 2, 3, 6, 7, 4, 5, 8, 9), wl0);
  }

  @Test
  public void moveToHead01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(8, 10, 6);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 8, 9, 6, 7), wl0);
  }

  @Test
  public void moveToHead02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(8, 10, 6);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 8, 9, 6, 7), wl0);
  }

  @Test
  public void moveToHead03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(2, 4, 0);
    assertEquals(List.of(2, 3, 0, 1, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void moveToHead04() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 2, 0);
    assertEquals(List.of(1, 0, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void moveToHead05() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(9, 10, 8);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 9, 8), wl0);
  }

  @Test
  public void moveToHead07() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(3, 8, 1);
    assertEquals(List.of(0, 3, 4, 5, 6, 7, 1, 2, 8, 9), wl0);
  }

  @Test
  public void moveToHead08() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.move(1, 10, 0);
    assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), wl0);
  }

  @Test
  public void wiredIterator00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator(true);
    while (itr.hasNext()) {
      itr.next();
      itr.remove();
    }
    assertEquals(0, wl0.size());
  }

  @Test
  public void wiredIterator01() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator(true);
    while (itr.hasNext()) {
      int i = itr.next();
      if (i % 2 == 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(1, 3, 5, 7, 9), wl0);
  }

  @Test
  public void wiredIterator02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator(true);
    while (itr.hasNext()) {
      int i = itr.next();
      if (i++ % 2 != 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(0, 2, 4, 6, 8), wl0);
  }

  @Test
  public void wiredIterator03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator(true);
    while (itr.hasNext()) {
      int i = itr.next();
      if (i++ % 3 != 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(0, 3, 6, 9), wl0);
  }

  @Test
  public void wiredIterator04() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    while (itr.hasNext()) {
      itr.next();
      itr.remove();
    }
    assertEquals(0, wl0.size());
  }

  @Test
  public void wiredIterator05() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    while (itr.hasNext()) {
      int i = itr.next();
      if (i % 2 == 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(1, 3, 5, 7, 9), wl0);
  }

  @Test
  public void wiredIterator06() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    while (itr.hasNext()) {
      int i = itr.next();
      if (i % 2 != 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(0, 2, 4, 6, 8), wl0);
  }

  @Test
  public void wiredIterator07() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    while (itr.hasNext()) {
      int i = itr.next();
      if (i++ % 3 != 0) {
        itr.remove();
      }
    }
    assertEquals(List.of(0, 3, 6, 9), wl0);
  }

  @Test
  public void wiredIterator08() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    assertEquals(0, (int) itr.next());
    assertEquals(1, (int) itr.peek());
    assertEquals(1, (int) itr.next());
    assertEquals(2, (int) itr.peek());
    assertEquals(2, (int) itr.next());
    assertEquals(3, (int) itr.peek());
    itr = itr.turn();
    assertEquals(1, (int) itr.peek());
    assertEquals(1, (int) itr.next());
    assertEquals(0, (int) itr.peek());
    assertEquals(0, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test(expected = IllegalStateException.class)
  public void wiredIterator09() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    itr.turn();
  }

  @Test(expected = IllegalStateException.class)
  public void wiredIterator10() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator(true);
    itr.turn();
  }

  @Test
  public void wiredIterator11() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.exclusiveWiredIterator(true);
    assertEquals(9, (int) itr.peek());
    assertEquals(9, (int) itr.next());
    assertEquals(8, (int) itr.peek());
    assertEquals(8, (int) itr.next());
    itr = itr.turn();
    assertEquals(9, (int) itr.peek());
    assertEquals(9, (int) itr.next());
    assertFalse(itr.hasNext());
  }

  @Test(expected = IllegalStateException.class)
  public void wiredIterator12() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    itr.set(666);
  }

  @Test(expected = IllegalStateException.class)
  public void wiredIterator13() {
    var wl0 = SynchronizedWiredList.of(0);
    var itr = wl0.wiredIterator();
    itr.next();
    itr.remove();
    itr.turn();
  }

  @Test
  public void wiredIterator14() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var itr = wl0.wiredIterator();
    itr.next();
    itr.set(100);
    itr.next();
    itr.set(200);
    assertEquals(SynchronizedWiredList.of(100, 200, 2, 3, 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void equals00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5);
    assertTrue(wl.equals(List.of(0, 1, 2, 3, 4, 5)));
    assertFalse(wl.equals(List.of(0, 1, 2, 3, 4)));
    assertFalse(wl.equals(List.of(0, 1, 2, 3, 4, 5, 6)));
    assertFalse(wl.equals(List.of()));
    assertFalse(wl.equals(null));
    assertTrue(SynchronizedWiredList.of().equals(List.of()));
  }

  @Test
  public void defragment00() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<Predicate<? super Object>> filters = List.of(e -> e instanceof Float);
    wl.defragment(filters);
    assertEquals(List.<Object>of(9.5F,
        77.23F,
        10.2F,
        .86F,
        0,
        true,
        "Earth",
        1,
        "Jupiter",
        2,
        'a',
        'b',
        "Neptune",
        true,
        3,
        false,
        'c'), wl);
  }

  @Test
  public void defragment01() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<Predicate<? super Object>> filters = List.of(e -> e instanceof Float,
        e -> e instanceof Boolean);
    wl.defragment(filters);
    assertEquals(List.<Object>of(9.5F,
        77.23F,
        10.2F,
        .86F,
        true,
        true,
        false,
        0,
        "Earth",
        1,
        "Jupiter",
        2,
        'a',
        'b',
        "Neptune",
        3,
        'c'), wl);
  }

  @Test
  public void defragment02() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<Predicate<? super Object>> filters = List.of(e -> e instanceof Float,
        e -> e instanceof Boolean,
        e -> e instanceof String);
    wl.defragment(filters);
    assertEquals(List.<Object>of(9.5F,
        77.23F,
        10.2F,
        .86F,
        true,
        true,
        false,
        "Earth",
        "Jupiter",
        "Neptune",
        0,
        1,
        2,
        'a',
        'b',
        3,
        'c'), wl);
  }

  @Test
  public void defragment03() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<Predicate<? super Object>> filters = List.of(e -> e instanceof Float,
        e -> e instanceof Boolean,
        e -> e instanceof String,
        e -> e instanceof Integer);
    wl.defragment(filters);
    assertEquals(List.<Object>of(9.5F,
        77.23F,
        10.2F,
        .86F,
        true,
        true,
        false,
        "Earth",
        "Jupiter",
        "Neptune",
        0,
        1,
        2,
        3,
        'a',
        'b',
        'c'), wl);
  }

  @Test
  public void defragment04() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    // ALWAYS FALSE
    List<Predicate<? super Object>> filters = List.of(Objects::isNull);
    wl.defragment(filters);
    assertEquals(List.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c'), wl);
  }

  @Test
  public void defragment05() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    // ALWAYS TRUE
    List<Predicate<? super Object>> filters = List.of(Objects::nonNull);
    wl.defragment(filters);
    assertEquals(List.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c'), wl);
  }

  @Test
  public void group00() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<SynchronizedWiredList<Object>> groups =
        wl.group(List.of(e -> e instanceof Float));
    assertEquals(2, groups.size());
    assertEquals(List.of(9.5F, 77.23F, 10.2F, .86F), groups.get(0));
    assertEquals(List.of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        2,
        'a',
        'b',
        "Neptune",
        true,
        3,
        false,
        'c'), groups.get(1));
  }

  @Test
  public void group01() {
    var wl = SynchronizedWiredList.<Object>of(0,
        true,
        "Earth",
        1,
        "Jupiter",
        9.5F,
        2,
        'a',
        'b',
        77.23F,
        10.2F,
        "Neptune",
        true,
        3,
        false,
        .86F,
        'c');
    List<Predicate<? super Object>> filters = List.of(e -> e instanceof Float,
        e -> e instanceof Boolean,
        e -> e instanceof String,
        e -> e instanceof Integer);
    List<SynchronizedWiredList<Object>> groups = wl.group(filters);
    assertEquals(5, groups.size());
    assertEquals(List.of(9.5F, 77.23F, 10.2F, .86F), groups.get(0));
    assertEquals(List.of(true, true, false), groups.get(1));
    assertEquals(List.of("Earth", "Jupiter", "Neptune"), groups.get(2));
    assertEquals(List.of(0, 1, 2, 3), groups.get(3));
    assertEquals(List.of('a', 'b', 'c'), groups.get(4));
  }

  @Test
  public void removeIf00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.removeIf(i -> i % 2 == 0);
    assertEquals(List.of(1, 3, 5, 7, 9), wl);
  }

  @Test
  public void removeIf01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.removeIf(i -> i % 2 != 0);
    assertEquals(List.of(0, 2, 4, 6, 8), wl);
  }

  @Test
  public void removeIf02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.removeIf(Objects::isNull);
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void removeIf03() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.removeIf(Objects::nonNull);
    assertEquals(List.of(), wl);
  }

  @Test
  public void removeAll00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertTrue(wl.removeAll(Set.of(3, 5, 7)));
    assertEquals(List.of(0, 1, 2, 4, 6, 8, 9), wl);
  }

  @Test
  public void removeAll01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertFalse(wl.removeAll(Set.of(13, 17, 23)));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void removeAll02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertTrue(wl.removeAll(SynchronizedWiredList.of(1, 3, 5, 7, 9)));
    assertEquals(List.of(0, 2, 4, 6, 8), wl);
  }

  @Test
  public void retainAll00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertTrue(wl.retainAll(Set.of(3, 5, 7)));
    assertEquals(List.of(3, 5, 7), wl);
  }

  @Test
  public void retainAll01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertTrue(wl.retainAll(Set.of(13, 17, 23)));
    assertEquals(List.of(), wl);
  }

  @Test
  public void retainAll02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertFalse(wl.retainAll(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wl);
  }

  @Test
  public void retainAll03() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    var wl1 = SynchronizedWiredList.of(3, 4, 5);
    assertTrue(wl0.retainAll(wl1));
    assertEquals(List.of(3, 4, 5), wl0);
    assertEquals(List.of(3, 4, 5), wl1); // argument is read only
  }

  @Test
  public void contains00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertFalse(wl.contains(47));
    //assertTrue(wl.contains(6));
  }

  @Test
  public void containsAll00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertFalse(wl.containsAll(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)));
    assertTrue(wl.containsAll(Set.of(3, 4, 5, 6, 7, 8, 9)));
  }

  @Test
  public void containsAll01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    assertFalse(wl.containsAll(SynchronizedWiredList.of(0, 1, 2, 3, 4, 5)));
    assertTrue(wl.containsAll(SynchronizedWiredList.of(1, 3)));
  }

  @Test
  public void partition00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.partition(5);
    assertEquals(2, wls.size());
    assertEquals(List.of(0, 1, 2, 3, 4), wls.get(0));
    assertEquals(List.of(5, 6, 7, 8, 9), wls.get(1));
    assertSame(wl, wls.get(1));
  }

  @Test
  public void partition01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.partition(3);
    assertEquals(4, wls.size());
    assertEquals(List.of(0, 1, 2), wls.get(0));
    assertEquals(List.of(3, 4, 5), wls.get(1));
    assertEquals(List.of(6, 7, 8), wls.get(2));
    assertEquals(List.of(9), wls.get(3));
    assertSame(wl, wls.get(3));
  }

  @Test
  public void partition02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.partition(1); // odd, but allowed
    assertEquals(10, wls.size());
    assertEquals(List.of(0), wls.get(0));
    assertEquals(List.of(4), wls.get(4));
    assertEquals(List.of(8), wls.get(8));
    assertEquals(List.of(9), wls.get(9));
    assertSame(wl, wls.get(9));
  }

  @Test
  public void partition03() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.partition(100);
    assertEquals(1, wls.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wls.get(0));
    assertSame(wl, wls.get(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void partition04() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl.partition(0);
  }

  @Test
  public void split00() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.split(2);
    assertEquals(2, wls.size());
    assertEquals(List.of(0, 1, 2, 3, 4), wls.get(0));
    assertEquals(List.of(5, 6, 7, 8, 9), wls.get(1));
    assertSame(wl, wls.get(1));
  }

  @Test
  public void split01() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.split(1000);
    assertEquals(10, wls.size());
    assertEquals(List.of(0), wls.get(0));
    assertEquals(List.of(4), wls.get(4));
    assertEquals(List.of(8), wls.get(8));
    assertEquals(List.of(9), wls.get(9));
    assertSame(wl, wls.get(9));
  }

  @Test
  public void split02() {
    var wl = SynchronizedWiredList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    List<SynchronizedWiredList<Integer>> wls = wl.split(1);
    assertEquals(1, wls.size());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), wls.get(0));
    assertSame(wl, wls.get(0));
  }

  @Test
  public void copy00() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    var wl1 = wl0.copy();
    assertEquals(wl0, wl1);
    wl1.set(2, 88);
    assertNotEquals(wl0, wl1);
  }

  @Test
  public void copy01() {
    var wl0 = SynchronizedWiredList.of(0);
    var wl1 = wl0.copy();
    assertEquals(wl0, wl1);
  }

  @Test
  public void copy02() {
    var wl0 = SynchronizedWiredList.of(0, 1, 2, 3, 4);
    var wl1 = wl0.copySegment(1, wl0.size());
    assertEquals(SynchronizedWiredList.of(1, 2, 3, 4), wl1);
    wl1 = wl0.copySegment(1, 1);
    assertEquals(SynchronizedWiredList.of(), wl1);
    wl1 = wl0.copySegment(5, 5);
    assertEquals(SynchronizedWiredList.of(), wl1);
    wl1 = wl0.copySegment(0, 5);
    assertEquals(wl0, wl1);
    wl1 = wl0.copySegment(0, 3);
    assertEquals(SynchronizedWiredList.of(0, 1, 2), wl1);
  }

  @Test
  public void copy03() {
    var wl0 = SynchronizedWiredList.of();
    var wl1 = wl0.copySegment(0, 0);
    assertEquals(wl0, wl1);
  }

  @Test
  public void replaceAll00() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceAll(0, 4, List.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c", 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void replaceAll01() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceAll(0, 10, List.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c"), wl0);
  }

  @Test
  public void replaceAll02() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceAll(4, 10, List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, "a", "b", "c"), wl0);
  }

  @Test
  public void replaceAll04() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceAll(4, 8, List.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, "a", "b", "c", 8, 9), wl0);
  }

  @Test
  public void replaceAll05() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceAll(4, 8, SynchronizedWiredList.of("a", "b", "c", "d", "e", "f"));
    assertEquals(List.of(0, 1, 2, 3, "a", "b", "c", "d", "e", "f", 8, 9), wl0);
  }

  @Test
  public void replaceSegment00() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceSegment(0, 4, SynchronizedWiredList.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c", 4, 5, 6, 7, 8, 9), wl0);
  }

  @Test
  public void replaceSegment01() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceSegment(0, 10, SynchronizedWiredList.of("a", "b", "c"));
    assertEquals(List.of("a", "b", "c"), wl0);
  }

  @Test
  public void replaceSegment02() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceSegment(4, 10, SynchronizedWiredList.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, "a", "b", "c"), wl0);
  }

  @Test
  public void replaceSegment04() {
    var wl0 = SynchronizedWiredList.<Object>of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    wl0.replaceSegment(4, 8, SynchronizedWiredList.of("a", "b", "c"));
    assertEquals(List.of(0, 1, 2, 3, "a", "b", "c", 8, 9), wl0);
  }

}
