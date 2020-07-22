package nl.naturalis.common;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.ObjectMethods.e2nEqualsRecursive;

public class ObjectMethodsTest {

  @Test // Just to make sure we understand Java
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
  public void e2nEqualsRecursive_01() {
    assertTrue("01", e2nEqualsRecursive("", null));
    assertTrue("02", e2nEqualsRecursive(null, ""));
    assertTrue("03", e2nEqualsRecursive(null, new Enum[0]));
    assertTrue("04", e2nEqualsRecursive(new int[0], null));
    assertTrue("05", e2nEqualsRecursive(new String[0], null));
    assertTrue("06", e2nEqualsRecursive(new String[0], null));
    assertTrue("07", e2nEqualsRecursive(Collections.emptyList(), null));
    assertTrue("08", e2nEqualsRecursive(null, new HashSet<>()));
    assertTrue("09", e2nEqualsRecursive(null, null));
    assertTrue("10", e2nEqualsRecursive("", ""));
    assertTrue("11", e2nEqualsRecursive(List.of(1, 2, 3, 4, 5), List.of(1, 2, 3, 4, 5)));
    assertTrue("12", e2nEqualsRecursive(new String[] {"To", "be", "or", "not"}, new String[] {"To", "be", "or", "not"}));
    assertTrue("13", e2nEqualsRecursive(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5}));
    assertFalse("14", e2nEqualsRecursive(new int[0], new HashSet<>()));
    assertFalse("15", e2nEqualsRecursive("", new HashSet<>()));
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

    assertFalse("01", e2nEqualsRecursive(subsubset1, subsubset2));
    assertTrue("02", e2nEqualsRecursive(subsubset2, subsubset3));
    assertTrue("03", e2nEqualsRecursive(subsubset4, subsubset5));
    assertFalse("04", e2nEqualsRecursive(subsubset5, subsubset6));
    assertFalse("05", e2nEqualsRecursive(subsubset5, subsubset7));

    assertFalse("06", e2nEqualsRecursive(subset1, subset2));
    assertFalse("07", e2nEqualsRecursive(subset2, subset4));
    assertTrue("08", e2nEqualsRecursive(subset3, subset4));
    assertFalse("09", e2nEqualsRecursive(subset4, subset5));
    assertTrue("10", e2nEqualsRecursive(subset6, subset7));

  }

  @SuppressWarnings("rawtypes")
  private static Set setOf(Object... objs) {
    return Arrays.stream(objs).collect(Collectors.toSet());
  }

}
