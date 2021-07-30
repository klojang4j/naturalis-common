package nl.naturalis.common.collection;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class UnsafeListTest {

  public UnsafeListTest() {}

  @Test
  public void testInit00() {
    List<String> list0 = List.of("Hello", ", ", "World", "!");
    UnsafeList<String> list1 = new UnsafeList<>(list0);
    assertEquals(4, list1.size());
    assertEquals("Hello", list1.get(0));
    assertEquals(", ", list1.get(1));
    assertEquals("World", list1.get(2));
    assertEquals("!", list1.get(3));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInit01() {
    UnsafeList<Integer> list = new UnsafeList<>(Integer.class, 4);
    list.add(8);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testInit02() {
    UnsafeList<Integer> list = new UnsafeList<>(Integer.class, 4);
    list.set(-1, 8);
  }

  @Test
  public void testInit03() {
    UnsafeList<Integer> list = new UnsafeList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    assertArrayEquals(new Integer[] {8, null, 4, null}, list.toArray(Integer[]::new));
  }

  @Test
  public void testInit04() {
    UnsafeList<Integer> list = new UnsafeList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    assertTrue(list.contains(null));
    assertTrue(list.contains(8));
    assertTrue(list.contains(8));
    assertTrue(list.containsAll(List.of(4, 8)));
    assertFalse(list.contains(3));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInit05a() {
    UnsafeList<Integer> list = new UnsafeList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    list.remove(4);
  }

  @Test
  public void testInit05b() {
    UnsafeList<Long> list = new UnsafeList<>(Long.class, 4);
    list.set(0, 8L);
    list.set(2, 4L);
    list.remove(2);
    assertArrayEquals(new Long[] {8L, null, null, null}, list.getArray());
  }

  @Test
  public void testInit05c() {
    UnsafeList<Long> list = new UnsafeList<>(Long.class, 4);
    list.set(0, 8L);
    list.set(2, 4L);
    list.remove(8L);
    assertArrayEquals(new Long[] {null, null, 4L, null}, list.getArray());
  }

  @Test
  public void testInit05d() {
    UnsafeList<Short> list = new UnsafeList<>(Short.class, 4);
    list.set(0, (short) 8);
    list.set(2, (short) 4);
    list.remove(2);
    assertArrayEquals(new Short[] {8, null, null, null}, list.getArray());
  }

  @Test
  public void testInit06() {
    UnsafeList<String> list = new UnsafeList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.remove("World");
    assertArrayEquals(new String[] {"Hello", null, null, null}, list.getArray());
  }

  @Test
  public void testInit07() {
    UnsafeList<String> list = new UnsafeList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World"));
    assertArrayEquals(new String[] {"Hello", null, null, null}, list.getArray());
  }

  @Test
  public void testInit08() {
    UnsafeList<String> list = new UnsafeList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World", "Hello"));
    assertArrayEquals(new String[] {null, null, null, null}, list.getArray());
  }

  @Test
  public void testInit09() {
    UnsafeList<String> list = new UnsafeList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.set(3, "World");
    list.remove("World");
    assertArrayEquals(new String[] {"Hello", null, null, "World"}, list.getArray());
  }

  @Test
  public void testInit10() {
    UnsafeList<String> list = new UnsafeList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(1, "Foo");
    list.set(2, "World");
    list.set(3, "Bar");
    list.retainAll(List.of("Foo", "Bar"));
    assertArrayEquals(new String[] {null, "Foo", null, "Bar"}, list.getArray());
  }
}
