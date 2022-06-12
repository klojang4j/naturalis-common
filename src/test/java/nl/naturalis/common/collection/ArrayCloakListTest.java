package nl.naturalis.common.collection;

import java.io.File;
import java.util.*;

import org.junit.Test;

import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.ArrayMethods.pack;
import static org.junit.Assert.*;

public class ArrayCloakListTest {

  public ArrayCloakListTest() {}

  @Test(expected = UnsupportedOperationException.class)
  public void add00() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.add(8);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void add01() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.add(1, 8);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void addAll00() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.addAll(List.of(1, 2, 3));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void addAll01() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.addAll(2, List.of(1, 2, 3));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void set00() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(-1, 8);
  }

  @Test
  public void set01() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    assertArrayEquals(pack(8, null, 4, null), list.uncloak());
    assertEquals(8, (int) list.get(0));
    assertEquals(null, list.get(1));
    assertEquals(4, (int) list.get(2));
    assertEquals(null, list.get(3));
  }

  @Test
  public void contains00() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    assertTrue(list.contains(null));
    assertTrue(list.contains(8));
    assertTrue(list.contains(8));
    assertFalse(list.contains(3));
  }

  @Test
  public void containsAll00() {
    ArrayCloakList<Integer> list = ArrayCloakList.of(Integer.class, 1, 2, 3, 4, 5);
    assertTrue(list.containsAll(List.of(2, 3, 4)));
    assertTrue(list.containsAll(Set.of(1, 2, 3, 4)));
    assertFalse(list.containsAll(Set.of(0, 1, 2, 3, 4)));
    assertFalse(list.containsAll(List.of(100, 200)));
    assertTrue(list.containsAll(List.of()));
  }

  @Test
  public void removeAll00() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World"));
    assertFalse(list.isEmpty());
    assertEquals(4, list.size());
    assertArrayEquals(pack("Hello", null, null, null), list.uncloak());
  }

  @Test
  public void equals00() {
    var list = ArrayCloakList.of(Integer.class, 0, 1, 2, 3, 4);
    assertTrue(list.equals(list));
    assertFalse(list.equals(ArrayCloakList.of(Integer.class, 0, 1, 2, 3, 4, 5)));
    assertFalse(list.equals(ArrayCloakList.of(Integer.class, 0, 1, 2, 3, null)));
    assertFalse(list.equals(ArrayCloakList.of(Integer.class, 0, 1, 2, 3)));
    assertFalse(list.equals(ArrayCloakList.of(String.class)));
    assertTrue(list.equals(List.of(0, 1, 2, 3, 4)));
    assertFalse(list.equals(List.of(0, 1, 1, 3, 4)));
    assertFalse(list.equals("FOO"));
    assertFalse(list.equals(null));
  }

  @Test
  public void remove00() {
    var list = ArrayCloakList.of(Integer.class, 100, 100, 200, 300, 400);
    assertEquals(5, list.size());
    assertEquals(200, (int) list.remove(2));
    assertArrayEquals(pack(100, 100, null, 300, 400), list.uncloak());
    assertEquals(100, (int) list.remove(1));
    assertNull(list.remove(1));
    assertArrayEquals(pack(100, null, null, 300, 400), list.uncloak());
    list.remove(4);
    assertArrayEquals(pack(100, null, null, 300, null), list.uncloak());
    assertEquals(5, list.size());
  }

  @Test
  public void remove01() {
    var list = ArrayCloakList.of(Integer.class, 100, 100, 200, 300, 400);
    assertTrue(list.remove((Integer) 100));
    assertArrayEquals(pack(null, 100, 200, 300, 400), list.uncloak());
    assertTrue(list.remove((Integer) 100));
    assertArrayEquals(pack(null, null, 200, 300, 400), list.uncloak());
    assertFalse(list.remove((Integer) 100));
    assertTrue(list.remove((Integer) 400));
    assertArrayEquals(pack(null, null, 200, 300, null), list.uncloak());
  }

  @Test
  public void remove02() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.set(3, "World");
    list.remove("World");
    assertArrayEquals(new String[] {"Hello", null, null, "World"}, list.uncloak());
  }

  @Test
  public void removeAll01() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World", "Hello"));
    assertArrayEquals(new String[] {null, null, null, null}, list.uncloak());
  }

  @Test
  public void retainAll00() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(1, "Foo");
    list.set(2, "World");
    list.set(3, "Bar");
    list.retainAll(List.of("Foo", "Bar"));
    assertArrayEquals(new String[] {null, "Foo", null, "Bar"}, list.uncloak());
  }

  @Test
  public void toArray00() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    assertArrayEquals(pack("to", "be", "or", "not", "to", "be"), list.toArray());
    assertArrayEquals(EMPTY_OBJECT_ARRAY, ArrayCloakList.of(String.class).toArray());
  }

  @Test
  public void toArray01() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    assertArrayEquals(pack("to", "be", "or", "not", "to", "be"), list.toArray(String[]::new));
    assertArrayEquals(pack("to", "be", "or", "not", "to", "be", null), list.toArray(new String[7]));
  }

  @Test
  public void indexOf00() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    assertEquals(-1, list.indexOf("McBeth"));
    assertEquals(0, list.indexOf("to"));
    assertEquals(1, list.indexOf("be"));
  }

  @Test
  public void lastIndexOf00() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    assertEquals(-1, list.lastIndexOf("McBeth"));
    assertEquals(4, list.lastIndexOf("to"));
    assertEquals(list.size() - 1, list.lastIndexOf("be"));
  }

  @Test
  public void iterator00() {
    var list = new ArrayCloakList<>(pack(0, 1, 2, 3, 4));
    int i = 0;
    for (Iterator<Integer> itr = list.iterator(); itr.hasNext(); ) {
      assertEquals(i++, (int) itr.next());
    }
  }

  @Test
  public void sublist00() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    assertEquals(List.of("or", "not"), list.subList(2, 4));
  }

  @Test
  public void clear00() {
    var list = ArrayCloakList.cloak(pack("to", "be", "or", "not", "to", "be"));
    list.clear();
    assertArrayEquals(pack(null, null, null, null, null, null), list.uncloak());
  }

  @Test
  public void hashCode00() {
    var list = ArrayCloakList.of(Integer.class, 0, 1, 2, null);
    assertEquals(924575, list.hashCode());
    list.set(3, 0);
    assertEquals(924575, list.hashCode());
  }

  @Test
  public void toString00() {
    var list = ArrayCloakList.of(Integer.class, 0, 1, 2, null);
    assertEquals("[0, 1, 2, null]", list.toString());
  }

}
