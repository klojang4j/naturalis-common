package nl.naturalis.common.check;

import nl.naturalis.common.IOMethods;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static nl.naturalis.common.ArrayMethods.pack;
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
      assertEquals("alpha must not be null or empty (was null)", msgEmpty().apply(args));
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
    arg.put("peter", LocalDate.of(2022, 5, 3));
    arg.put("mike", null);
    MsgArgs args = new MsgArgs(deepNotNull(), false, "achilles", arg);
    System.out.println(msgDeepNotNull().apply(args));
    if (!SYSOUT_ONLY) {
      String expected =
          "achilles must not be null or contain null values (was HashMap[3] "
              + "of {peter: 2022-05-03, mike: null, john: smith})";
      assertEquals(expected, msgDeepNotNull().apply(args));
    }
  }

  @Test
  public void msgDeepNotNull00_not() {
    Map<String, Object> arg = new HashMap<>();
    arg.put("john", "smith");
    arg.put("peter", LocalDate.of(2022, 5, 3));
    arg.put("mike", 42.5);
    MsgArgs args = new MsgArgs(deepNotNull(), true, "achilles", arg);
    System.out.println(msgDeepNotNull().apply(args));
    if (!SYSOUT_ONLY) {
      String expected =
          "achilles must be null or contain one or more null values (was HashMap[3] "
              + "of {peter: 2022-05-03, mike: 42.5, john: smith})";
      assertEquals(expected, msgDeepNotNull().apply(args));
    }
  }

  @Test
  public void msgDeepNotEmpty00() {
    Set arg = new LinkedHashSet(List.of("church", pack("1", "2", "3"), List.of()));
    MsgArgs args = new MsgArgs(deepNotEmpty(), true, "torino", arg);
    System.out.println(msgDeepNotEmpty().apply(args));
    if (!SYSOUT_ONLY) {
      String expected =
          "torino must be empty or contain or one or more empty values (was LinkedHashSet[3] "
              + "of [church, String[3] of [1, 2, 3], ListN[0]])";
      assertEquals(expected, msgDeepNotEmpty().apply(args));
    }
  }

  @Test
  public void msgBlank00() {
    MsgArgs args = new MsgArgs(blank(), false, "morpheus", "$100");
    System.out.println(msgBlank().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("morpheus must be null or blank (was $100)", msgBlank().apply(args));
    }
  }

  @Test
  public void msgBlank00_not() {
    MsgArgs args = new MsgArgs(blank(), true, "morpheus", "");
    System.out.println(msgBlank().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("morpheus must not be null or blank (was \"\")", msgBlank().apply(args));
    }
  }

  @Test
  public void msgInteger00() {
    MsgArgs args = new MsgArgs(integer(), false, "mozart", "composer");
    System.out.println(msgInteger().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("mozart must be an integer (was composer)", msgInteger().apply(args));
    }
  }

  @Test
  public void msgInteger00_not() {
    MsgArgs args = new MsgArgs(integer(), true, "mozart", "35");
    System.out.println(msgInteger().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("mozart must not be an integer (was 35)", msgInteger().apply(args));
    }
  }

  @Test
  public void msgFile00() {
    MsgArgs args = new MsgArgs(file(), false, "zembla", new File("/foo/bar/blob.txt"));
    System.out.println(msgFile().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("File not found: /foo/bar/blob.txt", msgFile().apply(args));
    }
  }

  @Test
  public void msgFile00_not() {
    MsgArgs args = new MsgArgs(file(), true, "zembla", new File("/foo/bar/blob.txt"));
    System.out.println(msgFile().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("File already exists: /foo/bar/blob.txt", msgFile().apply(args));
    }
  }

  @Test
  public void msgFile01() {
    long time = System.currentTimeMillis();
    File dir = new File(System.getProperty("java.io.tmpdir") + "/__foo_123_456__");
    dir.mkdir();
    File file = new File(System.getProperty("java.io.tmpdir") + "/__foo_123_456__");
    MsgArgs args = new MsgArgs(file(), false, "nero", file);
    System.out.println(msgFile().apply(args)); //
    if (!SYSOUT_ONLY) {
      assertEquals(
          "nero must not be a directory (was /tmp/__foo_123_456__)", msgFile().apply(args));
    }
    dir.delete();
  }

  @Test
  public void msgFile01_not() {
    File dir = new File(System.getProperty("java.io.tmpdir") + "/__foo_234_457__");
    dir.mkdir();
    File file = new File(System.getProperty("java.io.tmpdir") + "/__foo_234_457__");
    MsgArgs args = new MsgArgs(file(), true, "nero", file);
    System.out.println(msgFile().apply(args));
    if (!SYSOUT_ONLY) {
      assertEquals("File already exists: /tmp/__foo_234_457__", msgFile().apply(args));
    }
    dir.delete();
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
