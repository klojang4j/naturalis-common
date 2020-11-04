package nl.naturalis.common.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.collection.EnumToIntMapTest.TestEnum.*;

public class EnumToIntMapTest {

  public static enum TestEnum {
    RED,
    BLUE,
    ORANGE,
    GREEN,
    BLACK
  }

  @Test
  public void EnumToIntMap01() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class);
    assertTrue(map.isEmpty());
  }

  @Test
  public void EnumToIntMap02() {
    EnumToIntMap<TestEnum> map0 = new EnumToIntMap<>(TestEnum.class);
    map0.put(BLUE, 7);
    map0.put(GREEN, 8);
    EnumToIntMap<TestEnum> map1 = new EnumToIntMap<>(map0);
    assertEquals(map0, map1);
  }

  @Test
  public void EnumToIntMap03() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class, k -> k.name().length());
    assertEquals(TestEnum.values().length, map.size());
    assertEquals(3, map.get(RED));
    assertEquals(4, map.get(BLUE));
    assertEquals(6, map.get(ORANGE));
    assertEquals(5, map.get(GREEN));
    assertEquals(5, map.get(BLACK));
  }

  @Test
  public void EnumToIntMap04() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class, 0);
    assertTrue(map.isEmpty());
  }

  @Test
  public void put01() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class);
    map.put(RED, 7);
    map.put(BLACK, 9);
    assertEquals(2, map.size());
    assertEquals(7, map.get(RED));
    assertEquals(9, map.get(BLACK));
  }

  @Test(expected = IllegalArgumentException.class)
  public void put02() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class, 1000);
    map.put(RED, 1000);
  }

  @Test
  public void containsKey01() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class);
    map.put(RED, 7);
    map.put(BLACK, 9);
    assertTrue(map.containsKey(RED));
    assertTrue(map.containsKey(BLACK));
    assertFalse(map.containsKey(BLUE));
    assertFalse(map.containsKey(ORANGE));
    assertFalse(map.containsKey(GREEN));
  }

  @Test(expected = IllegalArgumentException.class)
  public void containsValue01() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class, 1000);
    map.put(RED, 7);
    map.put(BLACK, 9);
    map.containsValue(1000);
  }

  @Test
  public void putAll01() {
    EnumToIntMap<TestEnum> map1 = new EnumToIntMap<>(TestEnum.class, -999);
    EnumToIntMap<TestEnum> map2 = new EnumToIntMap<>(TestEnum.class, +999);
    map1.put(RED, 7);
    map1.put(BLACK, 9);
    map1.put(BLACK, 11);
    map1.put(GREEN, 100);
    map2.putAll(map1);
    assertTrue(map2.equals(map2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void putAll02() {
    EnumToIntMap<TestEnum> map1 = new EnumToIntMap<>(TestEnum.class, 5);
    EnumToIntMap<TestEnum> map2 = new EnumToIntMap<>(TestEnum.class, 6);
    map1.put(RED, 6);
    map2.putAll(map1);
  }

  @Test
  public void putAll03() {
    EnumToIntMap<TestEnum> map1 = new EnumToIntMap<>(TestEnum.class, 5);
    EnumToIntMap<TestEnum> map2 = new EnumToIntMap<>(TestEnum.class, 6);
    map1.put(RED, 7);
    map2.putAll(map1);
    assertEquals(map1, map2);
  }

  @Test
  public void values01() {
    EnumToIntMap<TestEnum> map1 = new EnumToIntMap<>(TestEnum.class, -999);
    map1.put(RED, 7);
    map1.put(BLACK, 9);
    map1.put(BLACK, 11);
    map1.put(GREEN, 100);
    map1.put(GREEN, 102);
    map1.put(ORANGE, 102);
    map1.put(BLUE, 102);
    map1.remove(BLACK);
    List<Integer> values = new ArrayList<>(map1.values());
    Collections.sort(values);
    assertEquals(2, values.size());
    assertEquals(7, (int) values.get(0));
    assertEquals(102, (int) values.get(1));
  }

  @Test
  public void size01() {
    EnumToIntMap<TestEnum> map = new EnumToIntMap<>(TestEnum.class);
    assertEquals(0, map.size());
    map.put(BLACK, 9);
    map.put(BLACK, 11);
    map.put(GREEN, 100);
    assertEquals(2, map.size());
    map.remove(BLUE);
    assertEquals(2, map.size());
    map.remove(GREEN);
    assertEquals(1, map.size());
    map.remove(BLACK);
    assertEquals(0, map.size());
    map.remove(ORANGE);
    assertEquals(0, map.size());
  }

  @Test
  public void set01() {
    EnumToIntMap<TestEnum> map =
        new EnumToIntMap<>(TestEnum.class).set(RED, 7).set(ORANGE, 4).set(BLUE, 28);
    assertEquals(3, map.size());
    assertTrue(map.containsKey(RED));
    assertTrue(map.containsKey(ORANGE));
    assertTrue(map.containsKey(BLUE));
  }
}
