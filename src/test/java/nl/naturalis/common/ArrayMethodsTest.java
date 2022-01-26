package nl.naturalis.common;

import org.junit.Test;

import java.util.List;

import static nl.naturalis.common.ArrayMethods.*;
import static org.junit.Assert.*;

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
    assertTrue(inIntArray(1, array));
    assertTrue(inIntArray(16, array));
    assertFalse(inIntArray(23, array));
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
  public void indexOf00() {
    assertEquals(2, indexOf(new String[] {"a", "b", "c", "d", "e"}, "c"));
    assertEquals(2, indexOf(new String[] {"a", "b", null, "d", "e"}, null));
    assertEquals(-1, indexOf(new String[] {"a", "b", null, "d", "e"}, "FOO"));
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
    // Use constructor. Otherwise compiler detects we're using the same
    // string twice and creates just one instance.
    String s1 = new String("World");
    String s2 = new String("World");
    String[] strings = pack(s0, s1, s2);
    assertEquals(0, find(strings, s0));
    assertEquals(1, find(strings, s1));
    assertEquals(2, find(strings, s2));
    assertEquals(-1, find(strings, new String("Hello")));
    assertEquals(-1, find(strings, new String("World")));
  }

  @Test
  public void inIntArray00() {
    assertTrue(inIntArray(2, 0, 4, 6, 3, 2));
    assertFalse(inIntArray(2, 0, 4, 6, 3, 9));
  }

  @Test
  public void inArray00() {
    assertTrue(inArray("a", null, "a", "b", "c", "d"));
    assertTrue(inArray(null, null, "a", "b", "c", "d"));
    assertTrue(inArray("c", null, "a", "b", "c", "d"));
    assertTrue(inArray("", null, ""));
    assertFalse(inArray("FOO", null, "a", "b", "c", "d"));
    assertFalse(inArray("FOO"));
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

  @Test
  public void implodeInts00() {
    int[] ints = {1, 2, 3, 4, 5};
    assertEquals("2|4", implodeInts(ints, i -> "" + (2 * i), "|", 0, 2));
    assertEquals("2|4|6|8|10", implodeInts(ints, i -> "" + (2 * i), "|", 0, -1));
    assertEquals("2|4|6|8|10", implodeInts(ints, i -> "" + (2 * i), "|", 0, 100));
    assertEquals("4|6|8|10", implodeInts(ints, i -> "" + (2 * i), "|", 1, 100));
    assertEquals("4|6|8", implodeInts(ints, i -> "" + (2 * i), "|", 1, 4));
    assertEquals("1|2|3", implodeInts(ints, "|", 3));
    assertEquals("1|2|3|4|5", implodeInts(ints, "|"));
    assertEquals("1, 2, 3", implodeInts(ints, 3));
    assertEquals("1, 2, 3, 4, 5", implodeInts(ints));
  }

  @Test
  public void implodeAny00() {
    long[] longs = {1, 2, 3, 4, 5};
    assertEquals("2|4", implodeAny(longs, l -> "" + (2 * (long) l), "|", 0, 2));
    assertEquals("2|4|6|8|10", implodeAny(longs, l -> "" + (2 * (long) l), "|", 0, -1));
    assertEquals("2|4|6|8|10", implodeAny(longs, l -> "" + (2 * (long) l), "|", 0, 100));
    assertEquals("4|6|8|10", implodeAny(longs, l -> "" + (2 * (long) l), "|", 1, 100));
    assertEquals("4|6|8", implodeAny(longs, l -> "" + (2 * (long) l), "|", 1, 4));
    assertEquals("1|2|3", implodeAny(longs, "|", 3));
    assertEquals("1|2|3|4|5", implodeAny(longs, "|"));
    assertEquals("1, 2, 3", implodeAny(longs, 3));
    assertEquals("1, 2, 3, 4, 5", implodeAny(longs));
  }

  @Test
  public void implode00() {
    Integer[] ints = {1, 2, 3, 4, 5};
    assertEquals("2|4", implode(ints, i -> "" + (2 * (int) i), "|", 0, 2));
    assertEquals("2|4|6|8|10", implode(ints, i -> "" + (2 * (int) i), "|", 0, -1));
    assertEquals("2|4|6|8|10", implode(ints, i -> "" + (2 * (int) i), "|", 0, 100));
    assertEquals("4|6|8|10", implode(ints, i -> "" + (2 * (int) i), "|", 1, 100));
    assertEquals("4|6|8", implode(ints, i -> "" + (2 * (int) i), "|", 1, 4));
    assertEquals("1|2|3", implode(ints, "|", 3));
    assertEquals("1|2|3|4|5", implode(ints, "|"));
    assertEquals("1, 2, 3", implode(ints, 3));
    assertEquals("1, 2, 3, 4, 5", implode(ints));
  }

  @Test
  public void asPrimitiveArray00() {
    Integer[] ints = {1, 2, 3, 4, 5};
    int[] expected = {1, 2, 3, 4, 5};
    assertArrayEquals(expected, asPrimitiveArray(ints));
  }

  @Test(expected = IllegalArgumentException.class)
  public void asPrimitiveArray01() {
    Integer[] ints = {1, 2, null, 4, 5};
    asPrimitiveArray(ints);
  }

  @Test
  public void asPrimitiveArray02() {
    Integer[] ints = {1, 2, null, 4, 5};
    int[] expected = {1, 2, 42, 4, 5};
    assertArrayEquals(expected, asPrimitiveArray(ints, 42));
  }

  @Test
  public void asWrapperArray00() {
    assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, asWrapperArray(new int[] {1, 2, 3, 4, 5}));
    assertArrayEquals(new Long[] {1L, 2L, 3L, 4L, 5L}, asWrapperArray(new long[] {1, 2, 3, 4, 5}));
    assertArrayEquals(
        new Double[] {1D, 2D, 3D, 4D, 5D}, asWrapperArray(new double[] {1, 2, 3, 4, 5}));
    assertArrayEquals(
        new Float[] {1F, 2F, 3F, 4F, 5F}, asWrapperArray(new float[] {1, 2, 3, 4, 5}));
    assertArrayEquals(new Short[] {1, 2, 3, 4, 5}, asWrapperArray(new short[] {1, 2, 3, 4, 5}));
    assertArrayEquals(new Byte[] {1, 2, 3, 4, 5}, asWrapperArray(new byte[] {1, 2, 3, 4, 5}));
    assertArrayEquals(new Character[] {1, 2, 3, 4, 5}, asWrapperArray(new char[] {1, 2, 3, 4, 5}));
    assertArrayEquals(
        new Boolean[] {Boolean.FALSE, Boolean.FALSE, Boolean.TRUE},
        asWrapperArray(new boolean[] {false, false, true}));
    assertArrayEquals(new Integer[0], asWrapperArray(new int[0]));
  }

  @Test
  public void asList00() {
    assertEquals(List.of(1, 2, 3), asList(new int[] {1, 2, 3}));
    assertEquals(List.of(1L, 2L, 3L), asList(new long[] {1, 2, 3}));
    assertEquals(List.of(1D, 2D, 3D), asList(new double[] {1, 2, 3}));
    assertEquals(List.of(1F, 2F, 3F), asList(new float[] {1, 2, 3}));
    assertEquals(List.of((short) 1, (short) 2, (short) 3), asList(new short[] {1, 2, 3}));
    assertEquals(List.of((byte) 1, (byte) 2, (byte) 3), asList(new byte[] {1, 2, 3}));
    assertEquals(List.of((char) 1, (char) 2, (char) 3), asList(new char[] {1, 2, 3}));
    assertEquals(List.of(Boolean.FALSE, Boolean.TRUE), asList(new boolean[] {false, true}));
    assertNotEquals(List.of(1, 2, 3), new long[] {1, 2, 3});
  }

  @Test
  public void cloak00() {
    assertEquals(List.of(1, 2, 3), cloak(new int[] {1, 2, 3}));
    assertEquals(List.of(1L, 2L, 3L), cloak(new long[] {1, 2, 3}));
    assertEquals(List.of(1D, 2D, 3D), cloak(new double[] {1, 2, 3}));
    assertEquals(List.of(1F, 2F, 3F), cloak(new float[] {1, 2, 3}));
    assertEquals(List.of((short) 1, (short) 2, (short) 3), cloak(new short[] {1, 2, 3}));
    assertEquals(List.of((byte) 1, (byte) 2, (byte) 3), cloak(new byte[] {1, 2, 3}));
    assertEquals(List.of((char) 1, (char) 2, (char) 3), cloak(new char[] {1, 2, 3}));
    assertEquals(List.of(Boolean.FALSE, Boolean.TRUE), cloak(new boolean[] {false, true}));
    assertNotEquals(List.of(1, 2, 3), new long[] {1, 2, 3});
  }
}
