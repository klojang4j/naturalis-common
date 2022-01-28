package nl.naturalis.common.collection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArrayCloakListTest {

  public ArrayCloakListTest() {}

  @Test(expected = UnsupportedOperationException.class)
  public void testInit01() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.add(8);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testInit02() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(-1, 8);
  }

  @Test
  public void testInit03() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    assertArrayEquals(new Integer[] {8, null, 4, null}, list.toArray(Integer[]::new));
  }

  @Test
  public void testInit04() {
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
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
    ArrayCloakList<Integer> list = new ArrayCloakList<>(Integer.class, 4);
    list.set(0, 8);
    list.set(2, 4);
    list.remove(4);
  }

  @Test
  public void testInit05b() {
    ArrayCloakList<Long> list = new ArrayCloakList<>(Long.class, 4);
    list.set(0, 8L);
    list.set(2, 4L);
    list.remove(2);
    assertArrayEquals(new Long[] {8L, null, null, null}, list.uncloak());
  }

  @Test
  public void testInit05c() {
    ArrayCloakList<Long> list = new ArrayCloakList<>(Long.class, 4);
    list.set(0, 8L);
    list.set(2, 4L);
    list.remove(8L);
    assertArrayEquals(new Long[] {null, null, 4L, null}, list.uncloak());
  }

  @Test
  public void testInit05d() {
    ArrayCloakList<Short> list = new ArrayCloakList<>(Short.class, 4);
    list.set(0, (short) 8);
    list.set(2, (short) 4);
    list.remove(2);
    assertArrayEquals(new Short[] {8, null, null, null}, list.uncloak());
  }

  @Test
  public void testInit06() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.remove("World");
    assertArrayEquals(new String[] {"Hello", null, null, null}, list.uncloak());
  }

  @Test
  public void testInit07() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World"));
    assertArrayEquals(new String[] {"Hello", null, null, null}, list.uncloak());
  }

  @Test
  public void testInit08() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.removeAll(List.of("a", "b", "World", "Hello"));
    assertArrayEquals(new String[] {null, null, null, null}, list.uncloak());
  }

  @Test
  public void testInit09() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(2, "World");
    list.set(3, "World");
    list.remove("World");
    assertArrayEquals(new String[] {"Hello", null, null, "World"}, list.uncloak());
  }

  @Test
  public void testInit10() {
    ArrayCloakList<String> list = new ArrayCloakList<>(String.class, 4);
    list.set(0, "Hello");
    list.set(1, "Foo");
    list.set(2, "World");
    list.set(3, "Bar");
    list.retainAll(List.of("Foo", "Bar"));
    assertArrayEquals(new String[] {null, "Foo", null, "Bar"}, list.uncloak());
  }
}
