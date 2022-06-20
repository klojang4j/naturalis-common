package nl.naturalis.common.collection;

import org.junit.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

public class TypeHashMapTest {

  //////////////////////////////////////////////////////////////
  // Specific for TypeHashMap. DON'T OVERWRITE when copy-pasting
  //////////////////////////////////////////////////////////////
  @Test
  public void autoExpand00() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, true,
        Map.of(Integer.class, "Integer"));
    assertEquals(1, m.size());
    assertTrue(m.containsKey(int.class));
    assertEquals(2, m.size());
  }

  @Test
  public void autoExpand01() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, false,
        Map.of(Integer.class, "Integer"));
    assertEquals(1, m.size());
    assertTrue(m.containsKey(int.class));
    assertEquals(1, m.size());
  }
  //////////////////////////////////////////////////////////////

  @Test
  public void entrySet00() {
    // Works just like keysSet(), so we only include it as a sanity
    // check, and for test coverage.
    TypeHashMap<String> m = TypeHashMap.build(String.class)
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
    Set entries = m.entrySet();
    assertEquals(13, entries.size());
    // we can't say much more about the order of the keys with TypeHashMap
  }

  @Test
  public void values00() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object.class, "Foo")
        .add(
            Number.class,
            "Bar")
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
    assertEquals(Set.of("Foo",
        "HashSet",
        "Bar",
        "Set",
        "Double",
        "List",
        "Collection",
        "Integer"), m.values());
    //System.out.println(m.values());
  }

  @Test
  public void copyOf00() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true,
        true,
        Map.of(Number.class, "Number"));
    assertFalse(m.isEmpty());
    assertTrue(m.containsKey(int.class));
    assertEquals("Number", m.get(int.class));
  }

  @Test
  public void copyOf01() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, false,
        Map.of(Number.class, "Number"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Number", m.get(int.class));
  }

  @Test
  public void copyOf02() {
    TypeHashMap<String> m = TypeHashMap.copyOf(false,
        false,
        Map.of(Number.class, "Number"));
    assertFalse(m.containsKey(int.class));
    assertNull(m.get(int.class));
  }

  @Test
  public void size00() {
    TypeHashMap<String> m = TypeHashMap.copyOf(false, false, Map.of());
    assertEquals(0, m.size());
    assertTrue(m.isEmpty());
    assertFalse(m.containsKey(Object.class));
  }

  @Test
  public void size01() {
    TypeHashMap<String> m = TypeHashMap.copyOf(false,
        false,
        Map.of(Serializable.class, "FOO"));
    assertEquals(1, m.size());
    assertFalse(m.isEmpty());
    assertFalse(m.containsKey(Object.class));
    assertTrue(m.containsKey(Serializable.class));
    assertTrue(m.containsValue("FOO"));
  }

  @Test
  public void autobox00() {
    // When Object.class is present, anything goes. You don't even
    // need to have autoboxing turned on! See comments for TypeMap.
    TypeHashMap<String> m = TypeHashMap.copyOf(false, false, Map.of(Object.class,
        "Object",
        Serializable.class,
        "Serializable",
        Number.class,
        "Number",
        Integer.class,
        "Integer"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Object", m.get(int.class));
  }

  @Test // sanity check
  public void autobox01() {
    TypeHashMap<String> m = TypeHashMap.copyOf(false, false, Map.of(int.class,
        "int",
        Serializable.class,
        "Serializable",
        Number.class,
        "Number",
        Integer.class,
        "Integer"));
    assertTrue(m.containsKey(int.class));
    assertEquals("int", m.get(int.class));
  }

  @Test
  public void autobox02() {
    TypeHashMap<String> m = TypeHashMap.copyOf(false,
        false,
        Map.of(Serializable.class,
            "Serializable",
            Number.class,
            "Number",
            Integer.class,
            "Integer"));
    assertFalse(m.containsKey(int.class));
    assertNull(m.get(int.class));
  }

  @Test
  public void autobox03() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, true, Map.of(Object.class,
        "Object",
        Serializable.class,
        "Serializable",
        Number.class,
        "Number",
        Integer.class,
        "Integer"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Integer", m.get(int.class));
  }

  @Test
  public void autobox04() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, true, Map.of(Object.class,
        "Object",
        Serializable.class,
        "Serializable",
        Number.class,
        "Number"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Number", m.get(int.class));
  }

  @Test
  public void autobox05() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true,
        true,
        Map.of(Object.class, "Object", Serializable.class, "Serializable"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Serializable", m.get(int.class));
  }

  @Test
  public void autobox06() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true,
        true,
        Map.of(Object.class, "Object"));
    assertTrue(m.containsKey(int.class));
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void autobox07() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true, true, Map.of());
    assertFalse(m.containsKey(int.class));
    assertNull(m.get(int.class));
  }

  @Test
  public void autobox08() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true,
        true,
        Map.of(int[][][].class, "int[][][]"));
    assertTrue(m.containsKey(int[][][].class));
    assertEquals("int[][][]", m.get(int[][][].class));
  }

  @Test
  public void autobox09() {
    TypeHashMap<String> m = TypeHashMap.copyOf(true,
        true,
        Map.of(Integer[][][].class, "Integer[][][]"));
    assertTrue(m.containsKey(int[][][].class));
    assertEquals("Integer[][][]", m.get(int[][][].class));
  }

  @Test
  public void test00() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
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
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
  }

  @Test
  public void test02() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  @Test
  public void test03() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object.class, "Object")
        .freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(ArrayList.class,
            "ArrayList")
        .add(List.class, "List")
        .add(Collection.class, "Collection")
        .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(ArrayList.class, "ArrayList")
        .add(MyListInterface.class, "MyListInterface").freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test06() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(List.class, "List")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List", m.get(ArrayList.class));
  }

  @Test
  public void test07() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(List[].class, "List[]")
        .add(Object.class, "Object")
        .freeze();
    assertEquals("List[]", m.get(ArrayList[].class));
  }

  @Test
  public void test08() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object[].class, "Object[]")
        .add(Object.class, "Object").freeze();
    assertEquals(2, m.size());
    assertEquals("Object[]", m.get(ArrayList[].class));
  }

  @Test
  public void test09() {
    TypeHashMap<String> m = TypeHashMap.build(String.class).add(Object[].class,
        "Object[]").add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(Object.class));
  }

  @Test
  public void test10() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .autobox(true)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test11() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test12() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .autobox(false)
        .add(Object.class, "Object")
        .freeze();
    assertEquals("Object", m.get(int[].class));
  }

  @Test
  public void test13() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
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
    TypeHashMap<String> m = TypeHashMap.build(String.class).add(A0.class, "A0").add(
        Serializable.class,
        "Serializable").freeze();
    assertTrue(m.containsKey(A000.class));
    assertTrue(m.containsKey(A0001.class));
    assertEquals("A0", m.get(A0001.class));
  }

  @Test
  public void test15() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
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
    TypeHashMap<String> m = TypeHashMap.build(String.class)
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
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(A01.class, "A01")
        .add(A0.class, "A0")
        .add(A01.class, "FOO")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test18() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(A0.class, "A0")
        .add(A01.class, "A01")
        .add(A01.class, "A01")
        .freeze();
  }

  @Test(expected = DuplicateKeyException.class)
  public void test19() {
    TypeHashMap<String> m = TypeHashMap.build(String.class)
        .add(Object.class, "FOO")
        .add(
            A0.class,
            "A0")
        .add(Object.class, "BAR")
        .freeze();
  }

}
