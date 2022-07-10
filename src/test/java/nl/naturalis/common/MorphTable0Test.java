package nl.naturalis.common;

import nl.naturalis.common.collection.TypeHashMap;
import org.junit.Test;

import java.util.*;

import static nl.naturalis.common.ArrayMethods.pack;
import static org.junit.Assert.*;

public class MorphTable0Test {

  @Test
  public void test00() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph(pack("a", "b", "c"), Collection.class);
    assertSame(ArrayList.class, c.getClass());
    assertEquals(List.of("a", "b", "c"), c);
  }

  @Test
  public void test01() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph("a", Collection.class);
    assertEquals(List.of("a"), c);
  }

  @Test
  public void test02() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph(pack("a", "b", "c"), LinkedList.class);
    assertSame(LinkedList.class, c.getClass());
    assertEquals(List.of("a", "b", "c"), c);
  }

  @Test
  public void test03() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph(1, LinkedList.class);
    assertSame(LinkedList.class, c.getClass());
    assertEquals(Arrays.asList(1), c);
    // Nice, that's type erasure for you
  }

  @Test
  public void test04() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<Integer> c = mt0.morph(List.of(1, 2, 3), LinkedHashSet.class);
    assertSame(LinkedHashSet.class, c.getClass());
    assertEquals(Set.of(1, 2, 3), c);
  }

  @Test
  public void test05() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph(pack("a", "b", "c"), Set.class);
    assertSame(HashSet.class, c.getClass());
    assertEquals(Set.of("a", "b", "c"), c);
  }

  @Test(expected = TypeConversionException.class)
  public void test06() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    mt0.morph(List.of(1, 2, 3), Deque.class);
  }

  @Test
  public void test07() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph(pack("a", "b", "c"), ArrayDeque.class);
    assertSame(ArrayDeque.class, c.getClass());
    assertEquals(List.of("a", "b", "c"), new ArrayList<>(c));
  }

  @Test
  public void test08() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<String> c = mt0.morph("Hello, World", Collection.class);
    assertEquals(List.of("Hello, World"), c);
  }

  @Test
  public void test09() {
    MorphToCollection mt0 = MorphToCollection.getInstance();
    Collection<Long> c = mt0.morph(33L, Set.class);
    assertEquals(Set.of(33L), c);
  }

  @Test(expected = TypeConversionException.class)
  public void test10() {
    // Not a public type
    MorphToCollection.getInstance().morph("Hi there", List.of("foo").getClass());
  }

  @Test(expected = TypeConversionException.class)
  public void test11() {
    // Missing no-arg constructor
    MorphToCollection.getInstance().morph("Hi there", TypeHashMap.class);
  }

}
