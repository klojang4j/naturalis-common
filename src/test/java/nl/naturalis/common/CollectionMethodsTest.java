package nl.naturalis.common;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.CollectionMethods.sublist;

public class CollectionMethodsTest {

  @Test
  public void sublist01() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("A", "234", concat(sublist(chars, 2, 3)));
    assertEquals("B", "234", concat(sublist(chars, 4, -3)));
    assertEquals("C", "01234", concat(sublist(chars, 4, -50)));
    assertEquals("D", "0", concat(sublist(chars, 0, -50)));
    assertEquals("E", "0", concat(sublist(chars, -20, -50)));
    assertEquals("F", "89", concat(sublist(chars, -2, 2)));
    assertEquals("G", "89", concat(sublist(chars, -2, 50)));
    assertEquals("G", "78", concat(sublist(chars, -2, -2)));
    assertEquals("G", "4567", concat(sublist(chars, -3, -4)));
  }

  private static String concat(List<String> chars) {
    return chars.stream().collect(Collectors.joining());
  }
}
