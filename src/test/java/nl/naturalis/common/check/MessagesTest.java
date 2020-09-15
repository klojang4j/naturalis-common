package nl.naturalis.common.check;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.Checks.atLeast;
import static nl.naturalis.common.check.Checks.contains;
import static nl.naturalis.common.check.Checks.instanceOf;

/*
 * These tests are especially meant to verify that the IdentityHashMap in Messages works as intended
 */
public class MessagesTest {

  @Test
  public void test01() {
    int argument = 2;
    String argName = "foo";
    int target = 5;
    String s = Messages.get(atLeast(), argument, argName, target);
    assertEquals("foo must be >= 5 (was 2)", s);
  }

  @Test
  public void test02() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = new Object();
    String s = Messages.get(contains(), argument, argName, target);
    String expected = "ArrayList foo must contain element " + target;
    assertEquals(expected, s);
  }

  @Test
  public void test03() {
    Collection<Object> argument = new ArrayList<>();
    String argName = "foo";
    Object target = AutoCloseable.class;
    String s = Messages.get(instanceOf(), argument, argName, target);
    String expected = "foo must be instance of java.lang.AutoCloseable (was java.util.ArrayList)";
    assertEquals(expected, s);
  }
}
