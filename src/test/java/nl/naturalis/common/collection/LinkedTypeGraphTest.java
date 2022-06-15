package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import org.junit.Test;

import java.io.Closeable;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

public class LinkedTypeGraphTest {

  @Test
  public void keySet00() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(Object.class, "Object")
        .add(Number.class, "Number")
        .add(Integer.class, "Integer")
        .add(Double.class, "Double")
        .add(
            Short.class,
            "Short")
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
    //System.out.println(keys);
    assertEquals(13, keys.size());
    assertEquals(Object.class, keys.get(0));
    assertTrue(keys.indexOf(Number.class) < keys.indexOf(Double.class));
    assertTrue(keys.indexOf(Number.class) < keys.indexOf(Integer.class));
    assertTrue(keys.indexOf(Number.class) < keys.indexOf(Short.class));
    assertTrue(keys.indexOf(Iterable.class) < keys.indexOf(Collection.class));
    assertTrue(keys.indexOf(Collection.class) < keys.indexOf(List.class));
    assertTrue(keys.indexOf(List.class) < keys.indexOf(ArrayList.class));
    assertTrue(keys.indexOf(List.class) < keys.indexOf(LinkedList.class));
    assertTrue(keys.indexOf(Collection.class) < keys.indexOf(Set.class));
    assertTrue(keys.indexOf(Set.class) < keys.indexOf(HashSet.class));
    assertTrue(keys.indexOf(Set.class) < keys.indexOf(LinkedHashSet.class));
  }

  @Test
  public void entrySet00() {
    // Works just like keysSet(), so we only include it as a sanity
    // check, and for test coverage.
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(Object.class, "Object")
        .add(Number.class, "Number")
        .add(Integer.class, "Integer")
        .add(Double.class, "Double")
        .add(
            Short.class,
            "Short")
        .add(Iterable.class, "Iterable")
        .add(Collection.class, "Collection")
        .add(List.class, "List")
        .add(ArrayList.class, "ArrayList")
        .add(LinkedList.class, "LinkedList")
        .add(Set.class, "Set")
        .add(HashSet.class, "HashSet")
        .add(LinkedHashSet.class, "LinkedHashSet")
        .freeze();
    Set entries = m.entrySet();
    assertEquals(13, entries.size());
    System.out.println(entries);
  }

  @Test
  public void values00() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(
            Object.class,
            "Foo")
        .add(Number.class, "Bar")
        .add(Integer.class, "Integer")
        .add(Double.class, "Double")
        .add(Short.class, "Foo")
        .add(Iterable.class, "Bar")
        .add(Collection.class, "Collection")
        .add(List.class, "List")
        .add(ArrayList.class, "Foo")
        .add(LinkedList.class, "Bar")
        .add(Set.class, "Set")
        .add(HashSet.class, "HashSet")
        .add(LinkedHashSet.class, "Foo")
        .freeze();
    assertEquals(
        Set.of("Foo",
            "HashSet",
            "Bar",
            "Set",
            "Double",
            "List",
            "Collection",
            "Integer"),
        m.values());
    //System.out.println(m.values());
  }

  @Test
  public void test00() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
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
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(Object.class,
        "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
  }

  @Test
  public void test02() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(Object.class,
        "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  @Test
  public void test03() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(Object.class,
        "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(List.class, "List")
        .add(Collection.class, "Collection")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(MyListInterface.class, "MyListInterface")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test06() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(List.class,
        "List").add(Object.class, "Object").freeze();
    assertEquals("List", m.get(ArrayList.class));
  }

  @Test
  public void test07() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(List[].class,
        "List[]").add(Object.class, "Object").freeze();
    assertEquals("List[]", m.get(ArrayList[].class));
  }

  @Test
  public void test08() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals(2, m.size());
    assertEquals("Object[]", m.get(ArrayList[].class));
  }

  @Test
  public void test09() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(Object.class));
  }

  @Test
  public void test10() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .autobox(true)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test11() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test12() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int[].class));
  }

  @Test
  public void test13() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
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
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(A0.class,
        "A0").add(Serializable.class, "Serializable").freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A0", m.get(A0001.class));
  }

  @Test
  public void test15() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(A0.class,
        "A0").add(A000.class, "A000").add(
        Serializable.class,
        "Serializable").freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A000", m.get(A0001.class));
  }

  @Test
  public void test16() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(A0.class,
        "A0").add(A0000.class, "A0000").add(
        Serializable.class,
        "Serializable").freeze();
    assertFalse(m.containsKey(Object.class));
    assertTrue(m.containsKey(Serializable.class));
    assertEquals("A0000", m.get(A0000.class));
  }

  @Test(expected = DuplicateKeyException.class)
  public void test17() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(A01.class,
        "A01").add(A0.class, "A0").add(A01.class, "FOO").freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test18() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(A0.class,
        "A0").add(A01.class, "A01").add(A01.class, "A01").freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test19() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class).add(Object.class,
        "FOO").add(A0.class, "A0").add(Object.class, "BAR").freeze();
  }

  @Test
  public void test20() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(A0.class,
            "A0")
        .add(A00.class, "A00")
        .add(A01.class, "A01")
        .add(
            A000.class,
            "A000")
        .add(A0000.class, "A0000")
        .add(A0001.class, "A0001")
        .add(Serializable.class, "Serializable")
        .add(Closeable.class, "Closeable")
        .add(AutoCloseable.class, "AutoCloseable")
        .freeze();
    //System.out.println(m.simpleTypeNames());
    List<Class<?>> keys = new ArrayList<>(m.keySet());
    assertEquals(List.of(Serializable.class,
        AutoCloseable.class,
        Closeable.class,
        A0.class,
        A00.class,
        A000.class,
        A0000.class,
        A0001.class,
        A01.class), keys);
  }

  @Test
  public void test21() {
    LinkedTypeGraph<String> m = LinkedTypeGraph.build(String.class)
        .add(A0.class,
            "A0")
        .add(A00.class, "A00")
        .add(A01.class, "A01")
        .add(
            A000.class,
            "A000")
        .add(A0000.class, "A0000")
        .add(A0001.class, "A0001")
        .add(Serializable.class, "Serializable")
        .add(Closeable.class, "Closeable")
        .add(AutoCloseable.class, "AutoCloseable")
        .freeze();
    List<Class<?>> keys = new ArrayList<>(m.breadthFirstKeySet());
    System.out.println(CollectionMethods.implode(keys, Class::getSimpleName));
    assertEquals(List.of(Serializable.class,
        AutoCloseable.class,
        A0.class,
        Closeable.class,
        A00.class,
        A01.class,
        A000.class,
        A0000.class,
        A0001.class), keys);
  }

}
