package nl.naturalis.common;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static nl.naturalis.common.ArrayMethods.append;
import static nl.naturalis.common.ArrayMethods.concat;
import static nl.naturalis.common.ArrayMethods.contains;
import static nl.naturalis.common.ArrayMethods.fromTemplate;
import static nl.naturalis.common.ArrayMethods.indexOf;
import static nl.naturalis.common.ArrayMethods.prefix;

public class ArrayMethodsTest {

  @Test
  public void append1() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1"};
    String[] actual = append(a, "1");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void append2() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1", "2"};
    String[] actual = append(a, "1", "2");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void append3() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"a", "b", "c", "1", "2", "3", "4", "5", "6", "7"};
    String[] actual = append(a, "1", "2", "3", "4", "5", "6", "7");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void concat1() {
    String[] a = {"a", "b", "c"};
    String[] b = {"1", "2", "3"};
    String[] c = {"A", "B", "C"};
    String[] d = {"*", "&", "$"};
    String[] expected = {"a", "b", "c", "1", "2", "3", "A", "B", "C", "*", "&", "$"};
    String[] actual = concat(a, b, c, d);
    assertArrayEquals(expected, actual);
  }

  @Test // With interfaces.
  public void concat2() {
    CharSequence[] a = {"a", "b", "c"};
    CharSequence[] b = {"1", "2", "3"};
    CharSequence[] c = {"A", "B", "C"};
    CharSequence[] d = {"*", "&", "$"};
    CharSequence[] expected = {"a", "b", "c", "1", "2", "3", "A", "B", "C", "*", "&", "$"};
    CharSequence[] actual = concat(a, b, c, d);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void contains1() {
    int[] array = {1, 2, 4, 8, 16};
    assertTrue(contains(array, 1));
    assertTrue(contains(array, 16));
    assertFalse(contains(array, 23));
  }

  @Test
  public void fromTemplate1() {
    String[] a = {"a", "b", "c"};
    Object[] b = fromTemplate(a);
    assertEquals(3, b.length);
    assertEquals(String.class, b.getClass().getComponentType());
  }

  @Test
  public void fromTemplate2() {
    CharSequence[] a = {"a", "b", "c"};
    Object[] b = fromTemplate(a, 7);
    assertEquals(7, b.length);
    assertEquals(CharSequence.class, b.getClass().getComponentType());
  }

  @Test
  public void indexOf1() {
    int[] array = {1, 2, 4, 8, 16};
    assertEquals(0, indexOf(array, 1));
    assertEquals(4, indexOf(array, 16));
    assertEquals(-1, indexOf(array, 23));
  }

  @Test
  public void prefix1() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "a", "b", "c"};
    String[] actual = prefix(a, "1");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void prefix2() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "2", "a", "b", "c"};
    String[] actual = prefix(a, "1", "2");
    assertArrayEquals(expected, actual);
  }

  @Test
  public void prefix3() {
    String[] a = {"a", "b", "c"};
    String[] expected = {"1", "2", "3", "4", "5", "6", "7", "a", "b", "c"};
    String[] actual = prefix(a, "1", "2", "3", "4", "5", "6", "7");
    assertArrayEquals(expected, actual);
  }
}
