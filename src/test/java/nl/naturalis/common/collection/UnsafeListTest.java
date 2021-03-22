package nl.naturalis.common.collection;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnsafeListTest {

  public UnsafeListTest() {}

  @Test
  public void testInit00() {
    List<String> list0 = List.of("Hello", ", ", "World", "!");
    List<String> list1 = new UnsafeList<>(list0);
    assertEquals(4, list1.size());
    assertEquals("Hello", list1.get(0));
    assertEquals(", ", list1.get(1));
    assertEquals("World", list1.get(2));
    assertEquals("!", list1.get(3));
  }
}
