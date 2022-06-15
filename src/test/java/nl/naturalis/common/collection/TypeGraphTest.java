package nl.naturalis.common.collection;

import org.junit.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class TypeGraphTest {

  @Test
  public void keySet00() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object.class, "Object")
        .add(Number.class, "Number")
        .add(Integer.class, "Integer")
        .add(Double.class, "Double")
        .add(Short.class, "Short")
        .add(Iterable.class, "Iterable")
        .add(Collection.class, "Collection")
        .add(List.class, "List")
        .add(ArrayList.class, "ArrayList")
        .add(LinkedList.class, "LinkedList")
        .add(Set.class, "Set")
        .add(HashSet.class, "HashSet")
        .add(LinkedHashSet.class, "LinkedHashSet")
        .freeze();
    List<Class<?>> keys = new ArrayList<>(m.keySet());
    System.out.println(keys);
  }

  @Test
  public void test00() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(String.class, "String")
        .add(Number.class, "Number")
        .add(Short.class, "Short")
        .freeze();
    assertEquals(3, m.size());
    assertTrue(m.containsValue("String"));
    assertTrue(m.containsValue("Number"));
    assertTrue(m.containsValue("Short"));
    assertFalse(m.containsValue("Integer"));
    String s = m.get(Short.class);
    assertEquals("Short", s);
    s = m.get(Integer.class);
    assertEquals("Number", s);
    assertEquals(3, m.size());
  }

  @Test
  public void test01() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
  }

  @Test
  public void test02() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  @Test
  public void test03() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(List.class, "List")
        .add(Collection.class, "Collection")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(MyListInterface.class, "MyListInterface")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test06() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(List.class, "List")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List", m.get(ArrayList.class));
  }

  @Test
  public void test07() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(List[].class, "List[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List[]", m.get(ArrayList[].class));
  }

  @Test
  public void test08() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals(2, m.size());
    assertEquals("Object[]", m.get(ArrayList[].class));
  }

  @Test
  public void test09() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(Object.class));
  }

  @Test
  public void test10() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .autobox(true)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test11() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test12() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int[].class));
  }

  @Test
  public void test13() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Iterable.class, "Iterable")
        .add(Collection.class, "Collection")
        .add(Set.class, "Set")
        .add(SortedSet.class, "SortedSet")
        .add(String.class, "String") // for good measure, add some normal classes
        .add(Integer.class, "integer")
        .freeze();
    assertTrue(m.containsValue("Set"));
    assertFalse(m.containsValue("HashSet"));
    assertTrue(m.containsKey(HashSet.class));
    assertEquals("Set", m.get(HashSet.class));
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
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(A0.class, "A0")
        .add(Serializable.class, "Serializable")
        .freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A0", m.get(A0001.class));
  }

  @Test
  public void test15() {
    TypeGraph<String> m = TypeGraph.build(String.class)
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
    TypeGraph<String> m = TypeGraph.build(String.class)
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
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(A01.class, "A01")
        .add(A0.class, "A0")
        .add(A01.class, "FOO")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test18() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(A0.class, "A0")
        .add(A01.class, "A01")
        .add(A01.class, "A01")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test19() {
    TypeGraph<String> m = TypeGraph.build(String.class)
        .add(Object.class, "FOO")
        .add(A0.class, "A0")
        .add(Object.class, "BAR")
        .freeze();
  }

}
