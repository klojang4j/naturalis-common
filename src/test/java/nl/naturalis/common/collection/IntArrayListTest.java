package nl.naturalis.common.collection;

import static nl.naturalis.common.ArrayMethods.ints;
import static nl.naturalis.common.util.ResizeMethod.PERCENTAGE;
import static org.junit.Assert.*;

import nl.naturalis.common.util.ResizeMethod;
import org.junit.Test;

import java.util.List;

public class IntArrayListTest {

  @Test
  public void constructor00() {
    IntList il = IntList.of(0, 1, 2, 3, 4);
    IntArrayList ial = new IntArrayList(il);
    assertEquals(il, ial);
    assertEquals(List.of(0, 1, 2, 3, 4), ial.toGenericList());
  }

  @Test
  public void size00() {
    IntArrayList list = new IntArrayList(2);
    assertTrue("01", list.isEmpty());
    list.add(1);
    assertEquals("02", 1, list.size());
    list.addAll(ints(2, 3));
    assertEquals("03", 3, list.size());
    assertEquals("04", 4, list.capacity());
  }

  @Test
  public void get01() {
    IntArrayList list = new IntArrayList(2);
    list.addAll(ints(42, 42, 7, 8, 13));
    assertEquals("01", 5, list.size());
    assertEquals("02", 5, list.capacity());
    list.add(12);
    assertEquals("03", 10, list.capacity());
    assertEquals("04", 42, list.get(1));
    assertEquals("05", 13, list.get(4));
    assertEquals("06", 12, list.get(5));
  }

  @Test
  public void equals00() {
    IntList list0 = new IntArrayList();
    list0.addAll(ints(0, 1, 2, 3, 4, 5));
    IntList list1 = new IntArrayList();
    list1.addAll(ints(0, 1, 2, 3, 4, 5));
    IntList list2 = new IntArrayList();
    list2.addAll(ints(1, 2, 3, 4, 5));
    IntList list3 = IntList.of(0, 1, 2, 3, 4, 5);
    IntList list4 = IntList.of(0, 1, 2, 3, 4, 5);
    IntList list5 = IntList.of(1, 2, 3, 4, 5);
    assertTrue(list0.equals(list0));
    assertFalse(list0.equals(null));
    assertFalse(list0.equals("hello"));
    assertTrue(list0.equals(list1));
    assertFalse(list0.equals(list2));
    assertTrue(list0.equals(list4));
    assertFalse(list0.equals(list5));
  }

  @Test
  public void hashCode00() {
    IntList list0 = new IntArrayList();
    list0.addAll(ints(0, 1, 2, 3, 4, 5));
    assertEquals(986115, list0.hashCode());
  }

  @Test
  public void addAll00() {
    IntList list0 = new IntArrayList(100);
    list0.addAll(ints(0, 1, 2, 3, 4, 5));
    assertEquals(6, list0.size());
    list0.addAll(ints(6, 7, 8, 9));
    assertEquals(10, list0.size());
    list0.addAll(IntList.of(10, 11));
    assertEquals(12, list0.size());
    IntList list1 = new IntArrayList(IntList.of(12, 13, 14));
    list0.addAll(list1);
    assertEquals(15, list0.size());
    assertEquals(IntList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14), list0);
    assertEquals(list0, IntList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
  }

  @Test
  public void addAll01() {
    IntList list0 = new IntArrayList(10, PERCENTAGE, 10);
    list0.addAll(ints(0, 1, 2, 3, 4, 5));
    assertEquals(6, list0.size());
    list0.addAll(ints(6, 7, 8, 9));
    assertEquals(10, list0.size());
    list0.addAll(IntList.of(10, 11));
    assertEquals(12, list0.size());
    IntList list1 = new IntArrayList(IntList.of(12, 13, 14));
    list0.addAll(list1);
    assertEquals(15, list0.size());
    assertEquals(IntList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14), list0);
    assertEquals(list0, IntList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
  }

  @Test
  public void addAll02() {
    IntList list0 = new IntArrayList(10, PERCENTAGE, 120);
    list0.addAll(ints(0, 1, 2, 3, 4, 5));
    assertEquals(6, list0.size());
    IntList list1 = new IntArrayList(10, PERCENTAGE, 120);
    list1.addAll(ints(6, 7));
    assertEquals(2, list1.size());
    IntList list2 = new IntArrayList(list1);
    assertEquals(2, list2.size());
    list0.addAll(list2);
    assertEquals(8, list0.size());
    assertEquals(IntList.of(0, 1, 2, 3, 4, 5, 6, 7), list0);
    list0.addAll(list0);
    assertEquals(16, list0.size());
  }

}
