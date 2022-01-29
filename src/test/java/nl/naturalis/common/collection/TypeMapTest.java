package nl.naturalis.common.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class TypeMapTest {

  @Test
  public void test00() {
    TypeMap<String> m =
        TypeMap.build(String.class)
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
    TypeMap<String> m =
        TypeMap.build(String.class).add(Object.class, "Object").autoExpand(true).freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test02() {
    TypeMap<String> m =
        TypeMap.build(String.class).autoExpand(2).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test03() {
    TypeMap<String> m =
        TypeMap.build(String.class).autoExpand(false).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(1, m.size());
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .add(ArrayList.class, "ArrayList")
            .add(List.class, "List")
            .add(Collection.class, "Collection")
            .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .add(ArrayList.class, "ArrayList")
            .add(MyListInterface.class, "MyListInterface")
            .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test06() {
    TypeMap<String> m =
        TypeMap.build(String.class).add(List.class, "List").add(Object.class, "Object").freeze();
    assertEquals("List", m.get(ArrayList.class));
  }

  @Test
  public void test07() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .add(List[].class, "List[]")
            .add(Object.class, "Object")
            .freeze();
    assertEquals("List[]", m.get(ArrayList[].class));
  }

  @Test
  public void test08() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .add(Object[].class, "Object[]")
            .add(Object.class, "Object")
            .freeze();
    assertEquals(2, m.size());
    assertEquals("Object[]", m.get(ArrayList[].class));
  }

  @Test
  public void test09() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .add(Object[].class, "Object[]")
            .add(Object.class, "Object")
            .freeze();
    assertEquals("Object", m.get(Object.class));
  }

  @Test
  public void test10() {
    TypeMap<String> m =
        TypeMap.build(String.class).autobox(true).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test11() {
    TypeMap<String> m =
        TypeMap.build(String.class).autobox(false).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int.class));
  }

  @Test
  public void test12() {
    TypeMap<String> m =
        TypeMap.build(String.class).autobox(false).add(Object.class, "Object").freeze();
    assertEquals("Object", m.get(int[].class));
  }
}
