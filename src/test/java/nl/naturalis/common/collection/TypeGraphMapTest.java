package nl.naturalis.common.collection;

import org.junit.Test;

import java.io.Closeable;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class TypeGraphMapTest {

  @Test
  public void test00() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(String.class, "String")
        .add(Number.class, "Number")
        .add(Short.class, "Short")
        .freeze();
    assertEquals(3, m.size());
    String s = m.get(Short.class);
    assertEquals("Short", s);
    s = m.get(Integer.class);
    assertEquals("Number", s);
    assertEquals(3, m.size());
  }

  @Test
  public void test01() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
  }

  @Test
  public void test02() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  @Test
  public void test03() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(List.class, "List")
        .add(Collection.class, "Collection")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(MyListInterface.class, "MyListInterface")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test06() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(List.class, "List")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List", m.get(ArrayList.class));
  }

  @Test
  public void test07() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(List[].class, "List[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List[]", m.get(ArrayList[].class));
  }

  @Test
  public void test08() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals(2, m.size());
    assertEquals("Object[]", m.get(ArrayList[].class));
  }

  @Test
  public void test09() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(Object.class));
  }

  @Test
  public void test10() {
    TypeGraphMap<String> m =
        TypeGraphMap.build(String.class).autobox(true).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test11() {
    TypeGraphMap<String> m =
        TypeGraphMap.build(String.class).autobox(false).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test12() {
    TypeGraphMap<String> m =
        TypeGraphMap.build(String.class).autobox(false).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int[].class));
  }

  @Test
  public void test13() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(Iterable.class, "Iterable")
        .add(Collection.class, "Collection")
        .add(Set.class, "Set")
        .add(SortedSet.class, "SortedSet")
        .add(String.class, "String") // for good measure, add some normal classes
        .add(Integer.class, "integer")
        .freeze();
    assertEquals("SortedSet", m.get(NavigableSet.class));
    assertEquals("Set", m.get(Set.class));
  }

  static class A0 {}

  static class A00 extends A0 {}

  static class A01 extends A0 {}

  static class A000 extends A00 {}

  static class A0000 extends A000 {}

  static class A0001 extends A0000 implements Serializable {}

  @Test
  public void test14() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(A0.class, "A0")
        .add(Serializable.class, "Serializable")
        .freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A0", m.get(A0001.class));
  }

  @Test
  public void test15() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(A0.class, "A0")
        .add(A000.class, "A000")
        .add(Serializable.class, "Serializable")
        .freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A000", m.get(A0001.class));
  }

  @Test
  public void test16() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(A0.class, "A0")
        .add(A0000.class, "A0000")
        .add(Serializable.class, "Serializable")
        .freeze();
    assertFalse(m.containsKey(Object.class));
    assertTrue(m.containsKey(Serializable.class));
    assertEquals("A0000", m.get(A0000.class));
  }

  @Test(expected = DuplicateKeyException.class)
  public void test17() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(A01.class, "A01")
        .add(A0.class, "A0")
        .add(A01.class, "FOO")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test18() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(A0.class, "A0")
        .add(A01.class, "A01")
        .add(A01.class, "A01")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test19() {
    TypeGraphMap<String> m = TypeGraphMap.build(String.class)
        .add(Object.class, "FOO")
        .add(A0.class, "A0")
        .add(Object.class, "BAR")
        .freeze();
  }

}
