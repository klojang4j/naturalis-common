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
    assertEquals("A", "234", concat(sublist(chars, 2, 3)));
  }

  @Test
  public void sublistB() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("B", "123", concat(sublist(chars, 4, -3)));
  }

  @Test
  public void sublistC() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("C", "0123", concat(sublist(chars, 4, -50)));
  }

  @Test
  public void sublistD() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("D", "", concat(sublist(chars, 0, -50)));
  }

  @Test
  public void sublistE() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("E", "", concat(sublist(chars, -20, -50)));
  }

  @Test
  public void sublistF() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("F", "89", concat(sublist(chars, -2, 2)));
  }

  @Test
  public void sublistG() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("G", "89", concat(sublist(chars, -2, 50)));
  }

  @Test
  public void sublistH() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("H", "67", concat(sublist(chars, -2, -2)));
  }

  @Test
  public void sublistI() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("I", "3456", concat(sublist(chars, -3, -4)));
  }

  @Test
  public void sublistJ() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("J", "9", concat(sublist(chars, 9, 3)));
  }

  @Test
  public void sublistK() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("K", "", concat(sublist(chars, 10, 3)));
  }

  @Test
  public void sublistL() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("L", "", concat(sublist(chars, 10, 0)));
  }

  @Test
  public void sublistM() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("M", "", concat(sublist(chars, 9, 0)));
  }

  @Test
  public void sublistN() {
    List<String> chars = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    assertEquals("N", "", concat(sublist(chars, 0, 0)));
  }

  private static String concat(List<String> chars) {
    return chars.stream().collect(Collectors.joining());
  }
}
