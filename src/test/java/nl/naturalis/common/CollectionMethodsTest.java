package nl.naturalis.common;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static nl.naturalis.common.CollectionMethods.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CollectionMethodsTest {

  @Test
  public void fromToIndex00() {
    List<Integer> l = List.of(0, 1, 2);
    assertEquals(Collections.emptyList(), l.subList(2, 2));
  }

  @Test
  public void fromToIndex01() {
    List<Integer> l = List.of(0, 1, 2);
    // One position past the end of the list - still allowed with subXXXXX methods in the JDK
    assertEquals(Collections.emptyList(), l.subList(3, 3));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void fromToIndex03() {
    List<Integer> l = List.of(0, 1, 2);
    l.subList(3, 4);
  }

  @Test
  public void asList00() {
    assertEquals(Collections.singletonList(null), asList(null));
    assertEquals(List.of("Hello World"), asList("Hello World"));
    assertEquals(List.of("Hello", "World"), asList(new String[] {"Hello", "World"}));
    Object obj = new Object();
    assertEquals(Collections.singletonList(obj), asList(obj));
    assertEquals(List.of(1, 2, 3, 4, 5), asList(new int[] {1, 2, 3, 4, 5}));
    assertEquals(List.of(1D, 2D, 3D, 4D, 5D), asList(new double[] {1, 2, 3, 4, 5}));
    assertEquals(List.of(1L, 2L, 3L, 4L, 5L), asList(new long[] {1, 2, 3, 4, 5}));
    assertEquals(List.of(1F, 2F, 3F, 4F, 5F), asList(new float[] {1, 2, 3, 4, 5}));
    assertEquals(List.of((short) 1, (short) 2, (short) 3), asList(new short[] {1, 2, 3}));
    assertEquals(List.of((byte) 1, (byte) 2, (byte) 3), asList(new byte[] {1, 2, 3}));
    assertEquals(List.of((char) 1, (char) 2, (char) 3), asList(new char[] {1, 2, 3}));
    assertEquals(
        List.of(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE),
        asList(new boolean[] {true, false, true}));
    List l = new ArrayList();
    l.add(10);
    assertSame(l, asList(l));
  }

  @Test
  public void sublistA() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("234", concat(sublist(chars, 2, 3)));
  }

  @Test
  public void sublistB() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("234", concat(sublist(chars, 4, -3)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void sublistC() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    sublist(chars, 4, -50);
  }

  @Test
  public void sublistD() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("89", concat(sublist(chars, -2, 2)));
  }

  @Test
  public void sublistE() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("78", concat(sublist(chars, -2, -2)));
  }

  @Test
  public void sublistF() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("4567", concat(sublist(chars, -3, -4)));
  }

  @Test
  public void sublistG() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 10, 0)));
  }

  @Test
  public void sublistH() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 9, 0)));
  }

  @Test
  public void sublistI() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 0, 0)));
  }

  private static String concat(List<String> chars) {
    return chars.stream().collect(Collectors.joining());
  }

  @Test
  public void implode01() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("234", implode(chars, Objects::toString, "", 2, 5));
  }

  @Test
  public void implode02() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("2/3/4", implode(chars, Objects::toString, "/", 2, 5));
  }

  @Test
  public void implode03() {
    Collection<Class<?>> coll =
        Arrays.asList(StringMethods.class, ArrayMethods.class, ClassMethods.class);
    assertEquals(
        "StringMethods, ArrayMethods, ClassMethods", implode(coll, ClassMethods::simpleClassName));
  }

  @Test
  public void freeze00() {
    Map<String, Integer> src = initializeMap(10, "foo", 1, "bar", 2, "baz", 3);
    Map<String, String> map0 = freeze(src, String::valueOf);
    Map<String, String> expected0 = Map.of("foo", "1", "bar", "2", "baz", "3");
    Map<String, Short> map1 = freeze(src, i -> (short) (i * 3));
    Map<String, Short> expected1 = Map.of("foo", (short) 3, "bar", (short) 6, "baz", (short) 9);
    assertEquals(expected1, map1);
  }

  @Test
  public void freezeIntoList00() {
    List<Integer> src = Arrays.asList(1, 2, 3);
    List<Integer> list0 = freezeIntoList(src, i -> i * 2);
    assertEquals(List.of(2, 4, 6), list0);
    List<String> list1 = freezeIntoList(src, String::valueOf);
    assertEquals(List.of("1", "2", "3"), list1);
  }

  @Test
  public void freezeIntoSet00() {
    List<Integer> src = Arrays.asList(1, 2, 3);
    Set<Integer> set0 = freezeIntoSet(src, i -> i * 2);
    assertEquals(Set.of(2, 4, 6), set0);
    Set<String> set1 = freezeIntoSet(src, String::valueOf);
    assertEquals(Set.of("1", "2", "3"), set1);
  }

  @Test
  public void freezeIntoMap00() {
    List<Integer> src = Arrays.asList(1, 2, 3);
    Map<String, Integer> map0 = freezeIntoMap(src, i -> String.valueOf(i * 2));
    Map<String, Integer> expected = Map.of("2", 1, "4", 2, "6", 3);
    assertEquals(expected, map0);
  }
}
