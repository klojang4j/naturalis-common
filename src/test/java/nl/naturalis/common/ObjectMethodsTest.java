package nl.naturalis.common;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.ObjectMethods.*;

@SuppressWarnings("rawtypes")
public class ObjectMethodsTest {

  /*
   * Not a real test. Just here so we can test our understanding of Java.
   */
  @Test
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
  public void isDeepNotNull01() {
    assertFalse("01", isDeepNotNull(null));
    assertFalse("02", isDeepNotNull(Collections.emptyList()));
    assertFalse("03", isDeepNotNull(Collections.emptySet()));
    assertFalse("04", isDeepNotNull(Collections.emptyMap()));
    assertFalse("05", isDeepNotNull(new String[0]));
    assertFalse("06", isDeepNotNull(new String[] {null}));
    assertFalse("07", isDeepNotNull(new String[] {null, null}));
    assertFalse("08", isDeepNotNull(new String[] {null, null, null}));
    assertFalse("09", isDeepNotNull(new String[] {null, "", null}));
    assertFalse("10", isDeepNotNull(Arrays.asList(null, null, null)));
    assertTrue("11", isDeepNotNull(Arrays.asList("Hello, world")));
    Map map = CollectionMethods.newHashMap("KEY1", null, "KEY2", null, "KEY3", null);
    assertFalse("12", isDeepNotNull(map));
    map = CollectionMethods.newHashMap(null, "VAL1", "KEY2", null, "KEY3", null);
    assertFalse("13", isDeepNotNull(map));
    assertTrue("10", isDeepNotNull(Arrays.asList("hello", "World")));
    map = CollectionMethods.newHashMap("KEY1", "VAL1", "KEY2", "VAL2", "KEY3", "VAL3");
    assertTrue("10", isDeepNotNull(map));
  }

  @Test
  public void isDeepNotEmpty01() {
    assertTrue("01", isDeepNotEmpty(List.of("Hi", new String[] {"Hi", "There"})));
    assertFalse("02", isDeepNotEmpty(List.of("Hi", new String[0])));
    assertTrue("03", isDeepNotEmpty(List.of("Hi", Collections.singletonMap("a", "b"))));
    assertFalse("04", isDeepNotEmpty(List.of("Hi", Collections.emptyMap())));
    Map map0 = Collections.emptyMap();
    Map map1 = Collections.singletonMap("b", map0);
    Map map2 = Collections.singletonMap("a", map1);
    List list0 = List.of("hi", map2);
    assertFalse("05", isDeepNotEmpty(list0));
    assertFalse("07", isDeepNotEmpty(List.of("Hi", Collections.emptySet())));
  }

  @Test
  public void ifNull01() {
    assertEquals("01", "13", ifNull("13", "14"));
    assertEquals("02", "14", ifNull(null, "14"));
    assertEquals("03", "14", ifNull(null, () -> "14"));
  }

  @Test
  public void ifEmpty01() {
    assertEquals("01", "Hi There", ifEmpty("", "Hi There"));
    assertEquals("02", "Hi There", ifEmpty("", () -> "Hi There"));
    assertEquals("03", "World", ifEmpty("World", () -> "Hi There"));
    List list0 = List.of("Hi There");
    assertEquals("04", list0, ifEmpty(Collections.emptyList(), () -> Arrays.asList("Hi There")));
  }

  @Test
  public void ifNotNull01() {
    String s = "7";
    Integer i = ifNotNull(s, Integer::valueOf);
    assertEquals("01", 7, i.intValue());
    s = null;
    i = ifNotNull(s, Integer::valueOf);
    assertNull("02", i);
    i = ifNotNull(s, Integer::valueOf, 8);
    assertEquals("03", 8, i.intValue());
    String[] strs = ifNotNull("This sentence contains five words", x -> x.split(" "));
    assertEquals("04", 5, strs.length);
  }

  @Test
  public void ifNotEmpty01() {
    Optional<String> opt1 = Optional.empty();
    Optional<String> opt2 = Optional.of("");
    Optional<String> opt3 = Optional.of("Hi");
    assertEquals("01", "FOO", ifNotEmpty(opt1, Optional::get, "FOO"));
    assertEquals("02", "FOO", ifNotEmpty(opt2, Optional::get, "FOO"));
    assertEquals("03", "Hi", ifNotEmpty(opt3, Optional::get, "FOO"));
  }

  @Test
  public void ifTrue01() {
    boolean ignoreCase = true;
    assertEquals("01", "hello, world!", ifTrue(ignoreCase, "Hello, World!", String::toLowerCase));
    ignoreCase = false;
    assertEquals("02", "Hello, World!", ifTrue(ignoreCase, "Hello, World!", String::toLowerCase));
  }

  @Test
  public void ifFalse01() {
    boolean keepCapitals = true;
    assertEquals(
        "01", "Hello, World!", ifFalse(keepCapitals, "Hello, World!", String::toLowerCase));
    keepCapitals = false;
    assertEquals(
        "02", "hello, world!", ifFalse(keepCapitals, "Hello, World!", String::toLowerCase));
  }

  @Test
  public void e2nDeepEquals01() {
    assertTrue("01", e2nDeepEquals(null, ""));
    assertTrue("02", e2nDeepEquals(null, null));
    assertTrue("03", e2nDeepEquals(null, new Enum[0]));
    assertTrue("04", e2nDeepEquals(new int[0], null));
    assertTrue("05", e2nDeepEquals(new String[0], null));
    assertFalse("06", e2nDeepEquals(new String[] {""}, null));
    assertFalse("07", e2nDeepEquals(new String[] {"", null, ""}, null));
    assertFalse(
        "08", e2nDeepEquals(new String[] {"", null, ""}, new String[] {"", null, "", "", ""}));
    assertTrue("09", e2nDeepEquals(Collections.emptyList(), null));
    assertTrue("10", e2nDeepEquals(null, new HashSet<>()));
    assertTrue("11", e2nDeepEquals(null, null));
    assertTrue("12", e2nDeepEquals("", ""));
    assertTrue("13", e2nDeepEquals(List.of(1, 2, 3, 4), List.of(1, 2, 3, 4)));
    assertTrue(
        "14", e2nDeepEquals(new String[] {"To", "be", "or"}, new String[] {"To", "be", "or"}));
    assertTrue("15", e2nDeepEquals(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4}));
    assertFalse("16", e2nDeepEquals(new int[0], new HashSet<>()));
    assertFalse("17", e2nDeepEquals("", new HashSet<>()));
    assertFalse("18", e2nDeepEquals(new ArrayList<>(), new HashSet<>()));
  }

  @Test // behaviour with sets (pretty extreme edge cases)
  public void e2nDeepEquals02() {

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

  private static Set setOf(Object... objs) {
    return Arrays.stream(objs).collect(Collectors.toSet());
  }
}
