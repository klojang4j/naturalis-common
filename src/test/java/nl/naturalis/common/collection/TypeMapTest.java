package nl.naturalis.common.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeMapTest {

  @Test
  public void test00() {
    TypeMap<String> m = new TypeMap<>();
    m.put(String.class, "String");
    m.put(Number.class, "Number");
    m.put(Short.class, "Short");
    assertEquals(3, m.size());
    String s = m.get(Short.class);
    assertEquals("Short", s);
    s = m.get(Integer.class);
    assertEquals("Number", s);
    assertEquals(4, m.size());
  }

  @Test
  public void test01() {
    TypeMap<String> m = new TypeMap<>();
    m.put(Object.class, "Object");
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Integer.class));
    assertEquals(2, m.size());
  }

  @Test
  public void test02() {
    TypeMap<String> m = new TypeMap<>();
    m.put(Object.class, "Object");
    assertEquals(1, m.size());
    assertTrue(m.containsKey(Collection.class));
    assertEquals(2, m.size());
  }

  public static class MyArrayList extends ArrayList<String> {}

  @Test
  public void test03() {
    TypeMap<String> m = new TypeMap<>();
    m.put(ArrayList.class, "ArrayList");
    m.put(List.class, "List");
    m.put(Collection.class, "Collection");
    assertEquals("ArrayList", m.get(MyArrayList.class));
  }
}
