package nl.naturalis.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CollectionMethodsTest {

  @Test
  public void sublist01() {
    List<String> input = new ArrayList<>();
    List<String> output = CollectionMethods.sublist(input, 0, 10);
    assertEquals(Collections.emptyList(), output);
  }

  @Test
  public void sublist02() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, 0, 10);
    assertEquals(input, output);
  }

  @Test
  public void sublist03() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, -3, 10);
    assertEquals(input, output);
  }

  @Test
  public void sublist04() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, -3, 0);
    assertEquals(Collections.emptyList(), output);
  }

  @Test
  public void sublist05() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, -3, 1);
    assertEquals(Arrays.asList("a"), output);
  }

  @Test
  public void sublist06() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, 8, 1);
    assertEquals(Collections.emptyList(), output);
  }

  @Test
  public void sublist07() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, 1, 1);
    assertEquals(Arrays.asList("b"), output);
  }

  @Test
  public void sublist08() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, 1, 7);
    assertEquals(Arrays.asList("b"), output);
  }

  @Test
  public void sublist09() {
    List<String> input = Arrays.asList("a", "b");
    List<String> output = CollectionMethods.sublist(input, -1, 7);
    assertEquals(Arrays.asList("b"), output);
  }

  @Test
  public void sublist10() {
    List<String> input = Arrays.asList("a", "b","c","d");
    List<String> output = CollectionMethods.sublist(input, -3, 2);
    assertEquals(Arrays.asList("b","c"), output);
  }

  @Test
  public void sublist11() {
    List<String> input = Arrays.asList("a", "b","c","d");
    List<String> output = CollectionMethods.sublist(input, 1, 2);
    assertEquals(Arrays.asList("b","c"), output);
  }

}
