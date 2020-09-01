package nl.naturalis.common;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.ObjectMethods.*;

public class ObjectMethodsTest {

  @Test // ** Just to make sure we understand Java **
  @SuppressWarnings("unlikely-arg-type")
  public void foo() {
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
  public void deepNotEmpty_01() {
    assertTrue("01", isDeeptNotEmpty(List.of("Hi", new String[] {"Hi", "There"})));
    assertFalse("02", isDeeptNotEmpty(List.of("Hi", new String[0])));
    assertTrue("03", isDeeptNotEmpty(List.of("Hi", Collections.singletonMap("a", "b"))));
    assertFalse("04", isDeeptNotEmpty(List.of("Hi", Collections.emptyMap())));
    assertFalse("05", isDeeptNotEmpty(List.of("Hi",
        Collections.singletonMap(
            "a",
            Collections.singletonMap(
                "b",
                Collections.emptyMap())))));
    assertTrue("06", isDeeptNotEmpty(List.of("Hi", Set.of(new Object(),new Object()))));
    assertFalse("07", isDeeptNotEmpty(List.of("Hi", Collections.emptySet())));
  }

  @Test
  public void ifEmpty_01() {
    assertEquals("01", "Hi There", ifEmpty("", "Hi There"));
    assertEquals("02", "Hi There", ifEmpty("", () -> "Hi There"));
    assertEquals("03", "World", ifEmpty("World", () -> "Hi There"));
    assertEquals("04", List.of("Hi There"), ifEmpty(Collections.emptyList(), () -> Arrays.asList("Hi There")));
  }

  @Test
  public void ifNull_01() {
    assertEquals("01", "13", ifNull("13", "14"));
    assertEquals("02", "14", ifNull(null, "14"));
    assertEquals("03", "14", ifNull(null, () -> "14"));
  }

  @Test
  public void ifNotNull_01() {
    String s = "7";
    Integer i = ifNotNull(s, Integer::valueOf);
    assertEquals("01", 7, i.intValue());
    s = null;
    i = ifNotNull(s, Integer::valueOf);
    assertNull("02", i);
    i = ifNotNull(s, Integer::valueOf, () -> 8);
    assertEquals("04", 8, i.intValue());
    String[] strs = ifNotNull("Hello Crazy World", x -> x.split(" "));
    assertEquals("05", 3, strs.length);
  }

  @Test
  public void ifTrue_01() {
    boolean ignoreCase = true;
    assertEquals("01", "hello, world!", ifTrue(ignoreCase, "Hello, World!", String::toLowerCase));
    ignoreCase = false;
    assertEquals("02", "Hello, World!", ifTrue(ignoreCase, "Hello, World!", String::toLowerCase));
  }

  @Test
  public void ifFalse_01() {
    boolean keepCapitals = true;
    assertEquals("01", "Hello, World!", ifFalse(keepCapitals, "Hello, World!", String::toLowerCase));
    keepCapitals = false;
    assertEquals("02", "hello, world!", ifFalse(keepCapitals, "Hello, World!", String::toLowerCase));
  }

  @Test
  public void e2nDeepEquals_01() {
    assertTrue("01", e2nDeepEquals(null, ""));
    assertTrue("02", e2nDeepEquals(null, null));
    assertTrue("03", e2nDeepEquals(null, new Enum[0]));
    assertTrue("04", e2nDeepEquals(new int[0], null));
    assertTrue("05", e2nDeepEquals(new String[0], null));
    assertFalse("06", e2nDeepEquals(new String[] {""}, null));
    assertFalse("07", e2nDeepEquals(new String[] {"", null, ""}, null));
    assertFalse("08", e2nDeepEquals(new String[] {"", null, ""}, new String[] {"", null, "", "", ""}));
    assertTrue("09", e2nDeepEquals(Collections.emptyList(), null));
    assertTrue("10", e2nDeepEquals(null, new HashSet<>()));
    assertTrue("11", e2nDeepEquals(null, null));
    assertTrue("12", e2nDeepEquals("", ""));
    assertTrue("13", e2nDeepEquals(List.of(1, 2, 3, 4), List.of(1, 2, 3, 4)));
    assertTrue("14", e2nDeepEquals(new String[] {"To", "be", "or"}, new String[] {"To", "be", "or"}));
    assertTrue("15", e2nDeepEquals(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4}));
    assertFalse("16", e2nDeepEquals(new int[0], new HashSet<>()));
    assertFalse("17", e2nDeepEquals("", new HashSet<>()));
    assertFalse("18", e2nDeepEquals(new ArrayList<>(), new HashSet<>()));
  }

  @Test // behaviour with sets (pretty extreme edge cases)
  @SuppressWarnings("rawtypes")
  public void e2nDeepEquals_02() {

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

    assertFalse("01", e2nDeepEquals(subsubset1, subsubset2));
    assertTrue("02", e2nDeepEquals(subsubset2, subsubset3));
    assertTrue("03", e2nDeepEquals(subsubset4, subsubset5));
    assertFalse("04", e2nDeepEquals(subsubset5, subsubset6));
    assertFalse("05", e2nDeepEquals(subsubset5, subsubset7));

    assertFalse("06", e2nDeepEquals(subset1, subset2));
    assertFalse("07", e2nDeepEquals(subset2, subset4));
    assertTrue("08", e2nDeepEquals(subset3, subset4));
    assertFalse("09", e2nDeepEquals(subset4, subset5));
    assertTrue("10", e2nDeepEquals(subset6, subset7));

  }

  @SuppressWarnings("rawtypes")
  private static Set setOf(Object... objs) {
    return Arrays.stream(objs).collect(Collectors.toSet());
  }

}
