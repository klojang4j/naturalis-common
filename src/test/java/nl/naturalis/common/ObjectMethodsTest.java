package nl.naturalis.common;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Ignore;
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
    Set set1 = Set.of("a", 1, test, "", new ArrayList());
    Set set2 = (Set) set1.stream().collect(Collectors.toSet());
    Set set3 = (Set) set1.stream().filter(ObjectMethods::notEmpty).collect(Collectors.toSet());
    assertEquals(set1, set2);
    assertEquals(Set.of("a", 1, test), set3);
  }

  @Test
  @Ignore
  public void e2nEqualsRecursive() {
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
