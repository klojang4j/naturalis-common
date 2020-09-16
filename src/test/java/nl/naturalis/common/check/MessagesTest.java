package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.Checks.*;

/*
 * These tests are especially meant to verify that the IdentityHashMap in Messages works as intended
 */
public class MessagesTest {

  @Test
  public void atLeast01() {
    int argument = 2;
    String argName = "foo";
    int target = 5;
    String expected = "foo must be >= 5 (was 2)";
    // System.out.println(expected);
    String actual = Messages.get(atLeast(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void objAtLeast01() {
    Double argument = 2.0;
    String argName = "foo";
    Short target = 5;
    String expected = "foo must be >= 5 (was 2.0)";
    // System.out.println(expected);
    String actual = Messages.get(numAtLeast(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void objGreaterThan01() {
    Long argument = 7L;
    String argName = "foo";
    Float target = 5F;
    String expected = "foo must be > 5.0 (was 7)";
    // System.out.println(expected);
    String actual = Messages.get(numGreaterThan(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void contains01() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = new Object();
    String s = target.getClass().getSimpleName() + "@" + System.identityHashCode(target);
    String expected = "ArrayList foo must contain " + s;
    // System.out.println(expected);
    String actual = Messages.get(contains(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void elementOf01() {
    Object argument = "Hello world, how are you?";
    String argName = "foo";
    LinkedHashSet<?> target = new LinkedHashSet<>();
    String expected =
        "foo (\"Hello world, how are[...]\") must be element of LinkedHashSet (was empty)";
    // System.out.println(expected);
    String actual = Messages.get(elementOf(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void instanceOf01() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = AutoCloseable.class;
    String expected = "foo must be instance of java.lang.AutoCloseable (was java.util.ArrayList)";
    // System.out.println(expected);
    String actual = Messages.get(instanceOf(), argument, argName, target);
    // System.out.println(actual);
    assertEquals(expected, actual);
  }
}
