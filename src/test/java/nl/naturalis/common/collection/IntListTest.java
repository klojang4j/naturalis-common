package nl.naturalis.common.collection;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntListTest {

  @Test
  public void test00() {
    ExposedIntList list = new ExposedIntList(2);
    assertTrue("01", list.isEmpty());
    list.add(1);
    assertEquals("02", 1, list.size());
    list.addAll(2, 3);
    assertEquals("03", 3, list.size());
    assertEquals("04", 4, list.capacity());
  }

  @Test
  public void test01() {
    ExposedIntList list = new ExposedIntList(2);
    list.addAll(42, 42, 7, 8, 13);
    assertEquals("01", 5, list.size());
    assertEquals("02", 5, list.capacity());
    list.add(12);
    assertEquals("03", 10, list.capacity());
    assertEquals("04", 42, list.get(1));
    assertEquals("05", 13, list.get(4));
    assertEquals("06", 12, list.get(5));
  }
}
