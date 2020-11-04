package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;
import nl.naturalis.common.ClassMethods;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.size;

/*
 * These tests are especially meant to verify that the IdentityHashMap in Messages works as intended
 */
public class MessagesTest {

  @Test
  public void gte01() {
    int argument = 2;
    String argName = "foo";
    int target = 5;
    String expected = "foo must be >= 5 (was 2)";
    System.out.println(expected);
    String actual = Messages.createMessage(gte(), argName, argument, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void atLeast01() {
    Double argument = 2.0;
    String argName = "foo";
    Short target = 5;
    String expected = "foo must be >= 5 (was 2.0)";
    System.out.println(expected);
    String actual = Messages.createMessage(atLeast(), argName, argument, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void greaterThan01() {
    Long argument = 4L;
    String argName = "foo";
    Float target = 5F;
    String expected = "foo must be > 5.0 (was 4)";
    System.out.println(expected);
    String actual = Messages.createMessage(greaterThan(), argName, argument, target);
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
    String actual = Messages.createMessage(contains(), argName, argument, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void in01() {
    Object argument = "Hello world, how are you?";
    String argName = "foo";
    LinkedHashSet<?> target = new LinkedHashSet<>();
    String s1 = target.getClass().getSimpleName() + "@" + System.identityHashCode(target);
    String s2 = argument.getClass().getSimpleName() + "@" + System.identityHashCode(argument);
    String expected =
        String.format("foo must be in %s (was %s \"Hello world, how are[...]\")", s1, s2);
    System.out.println(expected);
    String actual = Messages.createMessage(in(), argName, argument, target);
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
    String actual = Messages.createMessage(in(), argName, argument, target);
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
    String actual = Messages.createMessage(instanceOf(), argName, argument, target);
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void size01() {
    Collection<Object> argument = new ArrayList<>();
    String expected = "list.size() must be equal to 3 (was 0)";
    System.out.println(expected);
    try {
      Check.notNull(argument, "list").has(size(), eq(), 3);
    } catch (IllegalArgumentException e) {
      String actual = e.getMessage();
      System.out.println(actual);
      assertEquals(expected, actual);
      return;
    }
    Assert.fail();
  }

  @Test
  public void testCustomGetter() {
    Object argument = new Object();
    int hash = argument.hashCode();
    String expected = String.format("foo.? must be equal to 3 (was %d)", hash);
    System.out.println(expected);
    try {
      // Object::hashCode not in CommonGetters class
      Check.notNull(argument, "foo").has(Object::hashCode, eq(), 3);
    } catch (IllegalArgumentException e) {
      String actual = e.getMessage();
      System.out.println(actual);
      assertEquals(expected, actual);
      return;
    }
    Assert.fail();
  }
}
