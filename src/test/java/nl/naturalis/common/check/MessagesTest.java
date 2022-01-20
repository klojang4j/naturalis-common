package nl.naturalis.common.check;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.Messages.*;
import static org.junit.Assert.assertEquals;

public class MessagesTest {

  /*
   * If we change something that will affect all messages, it's best to disable the assertions, so
   * we can update the expected strings without being distracted by all the test failures.
   *
   * SET TO FALSE BEFORE GIT PUSH !!!
   */
  private static final boolean SYSOUT_ONLY = false;

  @Test
  public void msgNull00() {
    MsgArgs args = new MsgArgs(NULL(), false, "foo", "bar");
    System.out.println(msgNull().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("foo must be null (was bar)", msgNull().apply(args));
    }
  }

  @Test
  public void msgNull00_not() {
    MsgArgs args = new MsgArgs(NULL(), true, "foo", "bar");
    System.out.println(msgNull().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("foo must not be null", msgNull().apply(args));
    }
  }

  @Test
  public void msgNotNull00() {
    MsgArgs args = new MsgArgs(notNull(), false, "zombie", null);
    System.out.println(msgNotNull().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zombie must not be null", msgNotNull().apply(args));
    }
  }

  @Test
  public void msgNotNull00_not() {
    MsgArgs args = new MsgArgs(notNull(), true, "zombie", "killer");
    System.out.println(msgNotNull().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zombie must be null (was killer)", msgNotNull().apply(args));
    }
  }

  @Test
  public void msgYes00() {
    MsgArgs args = new MsgArgs(yes(), false, "sultan", false);
    System.out.println(msgYes().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("sultan must be true (was false)", msgYes().apply(args));
    }
  }

  @Test
  public void msgYes00_not() {
    MsgArgs args = new MsgArgs(yes(), true, "sultan", true);
    System.out.println(msgYes().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("sultan must be false (was true)", msgYes().apply(args));
    }
  }

  @Test
  public void msgNo00() {
    MsgArgs args = new MsgArgs(no(), false, "zipper", true);
    System.out.println(msgNo().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zipper must be false (was true)", msgNo().apply(args));
    }
  }

  @Test
  public void msgNo00_not() {
    MsgArgs args = new MsgArgs(no(), true, "zipper", false);
    System.out.println(msgNo().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zipper must be true (was false)", msgNo().apply(args));
    }
  }

  @Test
  public void msgEmpty00() {
    Object argument = List.of(1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 12, 13, 14, 15);
    MsgArgs args = new MsgArgs(empty(), false, "alpha", argument);
    String expected = "alpha must be empty (was ListN[15] of [1, 2, 3, 4, 5, 6, 7, 9, 10, 11...])";
    System.out.println(msgEmpty().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals(expected, msgEmpty().apply(args));
    }
  }

  @Test
  public void msgEmpty00_not() {
    MsgArgs args = new MsgArgs(empty(), true, "alpha", null);
    System.out.println(msgEmpty().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("alpha must not be null or empty", msgEmpty().apply(args));
    }
  }

  @Test
  public void msgEmpty01() {
    Object argument =
        Arrays.asList(
            "violin",
            "flute",
            "basin",
            "car",
            "BMW",
            "666",
            "Machiavelli",
            "cellar",
            "dungeon",
            "dragon",
            "train",
            "ticket");
    MsgArgs args = new MsgArgs(empty(), false, "zorro", argument);
    String expected =
        "zorro must be empty (was ArrayList[12] of [violin, flute, basin, car, BMW, 666, Machiavell...])";
    System.out.println(msgEmpty().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals(expected, msgEmpty().apply(args));
    }
  }

  @Test
  public void msgDeepNotNull00() {
    Map<String, Object> arg = new HashMap<>();
    arg.put("john", "smith");
    arg.put("peter", LocalDate.now());
    arg.put("mike", null);
    MsgArgs args = new MsgArgs(deepNotNull(), false, "achilles", arg);
    if (!SYSOUT_ONLY) {
      System.out.println(msgDeepNotNull().apply(args));
    }
    String expected =
        "achilles must not be null or contain null values (was HashMap[3] of {peter: 2022-01-20, mike: null, john: smith})";
    assertEquals(expected, msgDeepNotNull().apply(args));
  }

  @Test
  public void msgDeepNotNull00_not() {
    Map<String, Object> arg = new HashMap<>();
    arg.put("john", "smith");
    arg.put("peter", LocalDate.now());
    arg.put("mike", 42.5);
    MsgArgs args = new MsgArgs(deepNotNull(), true, "achilles", arg);
    if (!SYSOUT_ONLY) {
      System.out.println(msgDeepNotNull().apply(args));
    }
    String expected =
        "achilles must not be null or contain null values (was HashMap[3] of {peter: 2022-01-20, mike: 42.5, john: smith})";
    // assertEquals(expected, msgDeepNotNull().apply(args));
  }

  @Test
  public void msgAtLeast00() {
    MsgArgs args = new MsgArgs(gte(), false, "zebra", 2, 5);
    System.out.println(msgAtLeast().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zebra must be >= 5 (was 2)", msgAtLeast().apply(args));
    }
  }

  @Test
  public void msgAtLeast00_not() {
    MsgArgs args = new MsgArgs(gte(), true, "zebra", 2, 5);
    System.out.println(msgAtLeast().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("zebra must be < 5 (was 2)", msgAtLeast().apply(args));
    }
  }
}
