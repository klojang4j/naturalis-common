package nl.naturalis.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static nl.naturalis.common.CollectionMethods.asList;
import static nl.naturalis.common.CollectionMethods.newLinkedHashSet;
import static nl.naturalis.common.CollectionMethods.sublist;

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
    List<?> list = asList(null);
    assertEquals(1, list.size());
    assertEquals(null, list.get(0));
    list = Arrays.asList("a", "b", null, "d");
    assertSame(list, asList(list));
    assertEquals(Arrays.asList("a", "b", null, "d"), asList(newLinkedHashSet("a", "b", null, "d")));
    assertEquals(List.of(1, 2, 3, 4, 5), asList(new int[] {1, 2, 3, 4, 5}));
    assertEquals(List.of((byte) 1, Byte.valueOf((byte) 2)), asList(new byte[] {1, 2}));
    assertEquals(List.of("Hello World"), asList("Hello World"));
    Object obj = new Object();
    assertEquals(Collections.singletonList(obj), asList(obj));
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
}
