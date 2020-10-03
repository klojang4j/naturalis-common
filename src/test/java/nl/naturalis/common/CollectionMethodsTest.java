package nl.naturalis.common;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.CollectionMethods.sublist;

public class CollectionMethodsTest {

  @Test
  public void sublistA() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("234", concat(sublist(chars, 2, 3)));
  }

  @Test
  public void sublistB() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("234", concat(sublist(chars, 4, -3)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void sublistC() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    sublist(chars, 4, -50);
  }

  @Test
  public void sublistD() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("89", concat(sublist(chars, -2, 2)));
  }

  @Test
  public void sublistE() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("78", concat(sublist(chars, -2, -2)));
  }

  @Test
  public void sublistF() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("4567", concat(sublist(chars, -3, -4)));
  }

  @Test
  public void sublistG() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 10, 0)));
  }

  @Test
  public void sublistH() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 9, 0)));
  }

  @Test
  public void sublistI() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("", concat(sublist(chars, 0, 0)));
  }

  private static String concat(List<String> chars) {
    return chars.stream().collect(Collectors.joining());
  }
}
