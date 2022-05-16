package nl.naturalis.common.collection;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SimpleTypeSetTest {

  @Test
  public void test00() {
    SimpleTypeSet ts =
        SimpleTypeSet.of(true, true, Integer.class, Short.class, Number.class, CharSequence.class);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(Short.class));
    assertTrue(ts.contains(String.class));
    assertEquals(5, ts.size());
  }

  @Test
  public void test01() {
    SimpleTypeSet ts =
        SimpleTypeSet.of(true, false, Integer.class, Short.class, Number.class, CharSequence.class);
    assertEquals(4, ts.size());
    assertFalse(ts.contains(int.class));
    assertEquals(4, ts.size());
  }

  @Test
  public void test02() {
    SimpleTypeSet ts =
        SimpleTypeSet.of(true, true, Integer.class, Short.class, Number.class, CharSequence.class);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(double.class));
    assertEquals(5, ts.size());
  }

  @Test
  public void test03() {
    SimpleTypeSet ts =
        SimpleTypeSet.of(false, true, Integer.class, Short.class, Number.class, CharSequence.class);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(double.class));
    assertEquals(4, ts.size());
  }

  @Test
  public void test04() {
    SimpleTypeSet ts = SimpleTypeSet.of(false,
        false,
        Integer.class,
        Short.class,
        Number.class,
        CharSequence.class);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(String.class));
    assertFalse(ts.contains(double.class));
    assertEquals(4, ts.size());
  }

  @Test
  public void test05() {
    Set<Class<?>> s = Set.of(Integer.class, Short.class, Number.class, CharSequence.class);
    SimpleTypeSet ts = SimpleTypeSet.copyOf(s, true, false);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(Short.class));
    assertFalse(ts.contains(short.class));
    assertEquals(4, ts.size());
  }

  @Test
  public void test06() {
    Set<Class<?>> s = Set.of(Integer.class, Short.class, Number.class, CharSequence.class);
    SimpleTypeSet ts = SimpleTypeSet.copyOf(s, false, true);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(Short.class));
    assertTrue(ts.contains(short.class));
    assertEquals(4, ts.size());
  }

  @Test
  public void test07() {
    Set<Class<?>> s = Set.of(Integer.class, Short.class, Number.class, CharSequence.class);
    SimpleTypeSet ts = SimpleTypeSet.copyOf(s, false, false);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(String.class));
    assertTrue(ts.contains(Short.class));
    assertFalse(ts.contains(short.class));
    assertEquals(4, ts.size());
  }

}
