package nl.naturalis.common;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectMethodsTest {

  // Just to make sure we know Java
  @Test
  @SuppressWarnings("unlikely-arg-type")
  public void test01() {
    int[] ints = new int[] {1, 2, 3, 4, 5};
    long[] longs = new long[] {1L, 2L, 3L, 4L, 5L};
    Integer[] integers = new Integer[] {1, 2, 3, 4, 5};
    Object[] objects = new Object[] {1, 2, 3, 4, 5};
    assertFalse("01", ClassMethods.isA(ints.getClass(), longs.getClass()));
    assertFalse("02", ClassMethods.isA(longs.getClass(), ints.getClass()));
    assertFalse("03", ClassMethods.isA(ints.getClass(), integers.getClass()));
    assertFalse("04", ClassMethods.isA(integers.getClass(), ints.getClass()));
    assertTrue("05", ClassMethods.isA(integers.getClass(), objects.getClass()));
    assertFalse("06", ClassMethods.isA(objects.getClass(), integers.getClass()));
    assertFalse("07", ints.equals(longs));
    assertFalse("08", longs.equals(ints));
    assertFalse("09", ints.equals(integers));
    assertFalse("10", integers.equals(ints));
    assertFalse("11", objects.equals(integers));
    assertFalse("12", integers.equals(objects));
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void setsEquals() {
    Object test = new Object();
    Set set1 = setOf("a", 1, test, "", new ArrayList());
    Set set2 = (Set) set1.stream().collect(Collectors.toSet());
    Set set3 = (Set) set1.stream().filter(ObjectMethods::notEmpty).collect(Collectors.toSet());
    assertEquals(set1, set2);
    assertEquals(setOf("a", 1, test), set3);
  }

  @Test
  public void e2nEqualsRecursive_01() {
    assertTrue("01", ObjectMethods.e2nEqualsRecursive("", null));
    assertTrue("02", ObjectMethods.e2nEqualsRecursive(null, ""));
    assertTrue("03", ObjectMethods.e2nEqualsRecursive(null, new Enum[0]));
    assertTrue("04", ObjectMethods.e2nEqualsRecursive(new int[0], null));
    assertTrue("05", ObjectMethods.e2nEqualsRecursive(new String[0], null));
    assertTrue("06", ObjectMethods.e2nEqualsRecursive(new String[0], null));
    assertTrue("07", ObjectMethods.e2nEqualsRecursive(Collections.emptyList(), null));
    assertTrue("08", ObjectMethods.e2nEqualsRecursive(null, new HashSet<>()));
    assertTrue("09", ObjectMethods.e2nEqualsRecursive(null, null));
    assertTrue("10", ObjectMethods.e2nEqualsRecursive("", ""));
    assertTrue("11", ObjectMethods.e2nEqualsRecursive(List.of(1, 2, 3, 4, 5), List.of(1, 2, 3, 4, 5)));
    assertTrue("12", ObjectMethods.e2nEqualsRecursive(new String[] {"To", "be", "or", "not"}, new String[] {"To", "be", "or", "not"}));
    assertTrue("13", ObjectMethods.e2nEqualsRecursive(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5}));
    assertFalse("14", ObjectMethods.e2nEqualsRecursive(new int[0], new HashSet<>()));
    assertFalse("15", ObjectMethods.e2nEqualsRecursive("", new HashSet<>()));
  }

  @Test // behaviour with sets
  @SuppressWarnings("rawtypes")
  public void e2nEqualsRecursive_02() {

    Set subsubset1 = setOf("John");
    Set subsubset2 = setOf("John", null);
    Set subsubset3 = setOf("John", "", null, new int[0]);
    Set subsubset4 = setOf("John", "", null, new short[0]);
    Set subsubset5 = setOf("John", Collections.emptyList(), null, new short[0]);
    Set subsubset6 = setOf("Mark", Collections.emptyList(), null, new short[0]);
    Set subsubset7 = setOf("John", "Mark", new String[0], null, new short[0]);

    Set subset1 = setOf("Mary", subsubset1, subsubset2, subsubset4);
    Set subset2 = setOf("Mary", subsubset2, subsubset3, subsubset4);
    Set subset3 = setOf("Mary", subsubset2, subsubset3, subsubset4, Collections.emptySet());
    Set subset4 = setOf("Mary", subsubset3, subsubset4, subsubset5, new short[0]);
    Set subset5 = setOf("Mary", subsubset4, subsubset5, new short[] {1, 2});
    Set subset6 = setOf(subsubset4);
    Set subset7 = setOf(subsubset5);

    assertFalse("01", ObjectMethods.e2nEqualsRecursive(subsubset1, subsubset2));
    assertTrue("02", ObjectMethods.e2nEqualsRecursive(subsubset2, subsubset3));
    assertTrue("03", ObjectMethods.e2nEqualsRecursive(subsubset4, subsubset5));
    assertFalse("04", ObjectMethods.e2nEqualsRecursive(subsubset5, subsubset6));
    assertFalse("05", ObjectMethods.e2nEqualsRecursive(subsubset5, subsubset7));

    assertFalse("06", ObjectMethods.e2nEqualsRecursive(subset1, subset2));
    assertFalse("07", ObjectMethods.e2nEqualsRecursive(subset2, subset4));
    assertTrue("08", ObjectMethods.e2nEqualsRecursive(subset3, subset4));
    assertFalse("09", ObjectMethods.e2nEqualsRecursive(subset4, subset5));
    assertTrue("10", ObjectMethods.e2nEqualsRecursive(subset6, subset7));

  }

  @SuppressWarnings("rawtypes")
  private static Set setOf(Object... objs) {
    return Arrays.stream(objs).collect(Collectors.toSet());
  }

  @Test // implicitly also tests ObjectMethods.hashCode
  public void e2nHash() {
    assertEquals("01",
        ObjectMethods.e2nHash(null, 5, "hallo", null),
        ObjectMethods.e2nHash(null, 5, "hallo", null));
    assertEquals("02",
        ObjectMethods.e2nHash(null, 5, "hallo", null),
        ObjectMethods.e2nHash(Collections.emptyList(), 5, "hallo", Collections.emptySet()));
    assertEquals("03",
        ObjectMethods.e2nHash(null, 5, "hallo", null),
        ObjectMethods.e2nHash(new int[0], 5, "hallo", new Enum[0]));
    // Ouch, surprise (but true):
    assertEquals("04",
        ObjectMethods.e2nHash(Collections.emptyList(), 5, "hallo", Collections.emptySet()),
        ObjectMethods.e2nHash(new int[0], 5, "hallo", new Enum[0]));
    assertEquals("05", 0, ObjectMethods.e2nHash());
    assertEquals("06", 0, ObjectMethods.e2nHash((Object[]) null));
  }

}
