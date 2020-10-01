package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;
import nl.naturalis.common.ClassMethods;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;

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
    System.out.println(expected);
    String actual = Messages.get(atLeast(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void objAtLeast01() {
    Double argument = 2.0;
    String argName = "foo";
    Short target = 5;
    String expected = "foo must be >= 5 (was 2.0)";
    System.out.println(expected);
    String actual = Messages.get(nAtLeast(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void objGreaterThan01() {
    Long argument = 4L;
    String argName = "foo";
    Float target = 5F;
    String expected = "foo must be > 5.0 (was 4)";
    System.out.println(expected);
    String actual = Messages.get(nGreaterThan(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void contains01() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = new Object();
    String s = target.getClass().getSimpleName() + "@" + System.identityHashCode(target);
    String expected = "foo must contain " + s;
    System.out.println(expected);
    String actual = Messages.get(contains(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void in01() {
    Object argument = "Hello world, how are you?";
    String argName = "foo";
    LinkedHashSet<?> target = new LinkedHashSet<>();
    String s = target.getClass().getSimpleName() + "@" + System.identityHashCode(target);
    String expected = String.format("foo must be in %s (was \"Hello world, how are[...]\")", s);
    System.out.println(expected);
    String actual = Messages.get(in(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void in02() {
    Object argument = new float[7];
    String argName = "foo";
    LinkedHashSet<?> target = new LinkedHashSet<>();
    String s0 =
        ClassMethods.getArrayTypeSimpleName(argument) + "@" + System.identityHashCode(argument);
    String s1 = target.getClass().getSimpleName() + "@" + System.identityHashCode(target);
    String expected = String.format("foo must be in %s (was %s)", s1, s0);
    System.out.println(expected);
    String actual = Messages.get(in(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void instanceOf01() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = AutoCloseable.class;
    String expected = "foo must be instance of java.lang.AutoCloseable (was java.util.ArrayList)";
    System.out.println(expected);
    String actual = Messages.get(instanceOf(), argument, argName, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void size01() {
    Collection<Object> argument = new ArrayList<>();
    String expected = "list.size must be equal to 3 (was 0)";
    System.out.println(expected);
    try {
      Check.notNull(argument, "list").and(size(), equalTo(), 3);
    } catch (IllegalArgumentException e) {
      String actual = e.getMessage();
      System.out.println(actual);
      assertEquals(expected, actual);
      return;
    }
    Assert.fail();
  }

  @Test
  public void testWithNonRegisteredGetter() {
    Object argument = new Object();
    int hash = argument.hashCode();
    String expected = String.format("foo.? must be equal to 3 (was %d)", hash);
    System.out.println(expected);
    try {
      // No getter for Object::hashCode in CommonGetters class
      Check.notNull(argument, "foo").and(Object::hashCode, equalTo(), 3);
    } catch (IllegalArgumentException e) {
      String actual = e.getMessage();
      System.out.println(actual);
      assertEquals(expected, actual);
      return;
    }
    Assert.fail();
  }
}
