package nl.naturalis.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;

public class TypeMapTest {

  @Test
  public void test00() {
    TypeMap<String> m =
        TypeMap.build(String.class)
            .autoExpand(0)
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
    TypeMap<String> m =
        TypeMap.build(String.class).add(Object.class, "Object").autoExpand().freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test02() {
    TypeMap<String> m =
        TypeMap.build(String.class).autoExpand().add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test03() {
    TypeMap<String> m = TypeMap.build(String.class).add(Object.class, "Object").freeze();
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(1, m.size());
  }

  public static interface MyListInterface extends List<String> {}

  public static class MyArrayList extends ArrayList<String> implements MyListInterface {}

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
}
