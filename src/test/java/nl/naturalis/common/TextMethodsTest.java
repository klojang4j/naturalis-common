package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.TextMethods.plural;

public class TextMethodsTest {

  @Test
  public void plural0() {
    assertEquals("repository", plural("repository", 1));
    assertEquals("repositories", plural("repository", 2));
    assertEquals("database", plural("database", 1));
    assertEquals("databases", plural("database", 2));
  }
}
