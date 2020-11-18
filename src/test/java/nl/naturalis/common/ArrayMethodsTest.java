package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.ArrayMethods.*;

public class ArrayMethodsTest {

  @Test
  public void append01() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1"};
    String[] actual = append(a, "1");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void append02() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1", "2"};
    String[] actual = append(a, "1", "2");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void append03() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1", "2", "3", "4", "5", "6", "7"};
    String[] actual = append(a, "1", "2", "3", "4", "5", "6", "7");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void concat01() {
    String[] a = {"a", "b", "c"};
    String[] b = {"1", "2", "3"};
    String[] c = {"A", "B", "C"};
    String[] d = {"*", "&", "$"};
    String[] expected = {"a", "b", "c", "1", "2", "3", "A", "B", "C", "*", "&", "$"};
    String[] actual = concat(a, b, c, d);
    assertArrayEquals(expected, actual);
  }

  @Test // With interfaces.
  public void concat02() {
    CharSequence[] a = {"a", "b", "c"};
    CharSequence[] b = {"1", "2", "3"};
    CharSequence[] c = {"A", "B", "C"};
    CharSequence[] d = {"*", "&", "$"};
    CharSequence[] expected = {"a", "b", "c", "1", "2", "3", "A", "B", "C", "*", "&", "$"};
    CharSequence[] actual = concat(a, b, c, d);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void inArray01() {
    int[] array = {1, 2, 4, 8, 16};
    assertTrue(inArray(1, array));
    assertTrue(inArray(16, array));
    assertFalse(inArray(23, array));
  }

  @Test
  public void fromTemplate01() {
    String[] a = {"a", "b", "c"};
    Object[] b = fromTemplate(a);
    assertEquals(3, b.length);
    assertEquals(String.class, b.getClass().getComponentType());
  }

  @Test
  public void fromTemplate02() {
    CharSequence[] a = {"a", "b", "c"};
    Object[] b = fromTemplate(a, 7);
    assertEquals(7, b.length);
    assertEquals(CharSequence.class, b.getClass().getComponentType());
  }

  @Test
  public void indexOf01() {
    int[] array = {1, 2, 4, 8, 16};
    assertEquals(0, indexOf(array, 1));
    assertEquals(4, indexOf(array, 16));
    assertEquals(-1, indexOf(array, 23));
  }

  @Test
  public void find01() {
    String s0 = "Hello";
    String s1 = "World";
    /*
     * Interesting, we have to use brute force to get a new string, otherwise the compiler
     * apparently detects and coalesces the two occurrences of "World".
     */
    String s2 = new String("World");
    String[] ss = pack(s0, s1, s2);
    assertEquals("01", 0, find(ss, s0));
    assertEquals("02", 1, find(ss, s1));
    assertEquals("03", 2, find(ss, s2));
    assertEquals("04", -1, find(ss, new String("Hello")));
  }

  @Test
  public void prefix01() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "a", "b", "c"};
    String[] actual = prefix(a, "1");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void prefix02() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "2", "a", "b", "c"};
    String[] actual = prefix(a, "1", "2");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void prefix03() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "2", "3", "4", "5", "6", "7", "a", "b", "c"};
    String[] actual = prefix(a, "1", "2", "3", "4", "5", "6", "7");
    assertArrayEquals(expected, actual);
  }
}
