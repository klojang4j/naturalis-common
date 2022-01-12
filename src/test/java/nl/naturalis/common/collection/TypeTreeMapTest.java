package nl.naturalis.common.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeTreeMapTest {

  @Test
  public void test00() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class)
            .autoExpand(true)
            .add(String.class, "String")
            .add(Number.class, "Number")
            .add(Short.class, "Short")
            .freeze();
    assertEquals(3, m.size());
    String s = m.get(Short.class);
    assertEquals("Short", s);
    s = m.get(Integer.class);
    assertEquals("Number", s);
    assertEquals(4, m.size());
  }

  @Test
  public void test01() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class).add(Object.class, "Object").autoExpand(true).freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test02() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class).autoExpand(true).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test03() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class).autoExpand(false).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(1, m.size());
  }

  interface MyListInterface extends List<String> {}

  static class MyArrayList extends ArrayList<String> implements MyListInterface {}

  @Test
  public void test04() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class)
            .add(ArrayList.class, "ArrayList")
            .add(List.class, "List")
            .add(Collection.class, "Collection")
            .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }

  @Test
  public void test05() {
    TypeTreeMap<String> m =
        TypeTreeMap.build(String.class)
            .add(ArrayList.class, "ArrayList")
            .add(MyListInterface.class, "MyListInterface")
            .freeze();
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }
}
