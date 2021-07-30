package nl.naturalis.common.exception;

import static org.junit.Assert.assertEquals;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.path.Path;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExceptionOriginTest {

  private static Exception exception1;
  private static Exception exception2 = new Exception("Something is really wrong");

  @BeforeClass
  public static void beforeClass() {
    StackTraceElement ste0 =
        new StackTraceElement(ExceptionOriginTest.class.getName(), "fooMethod1", "Foo.java", 8);
    StackTraceElement ste1 =
        new StackTraceElement(ExceptionOriginTest.class.getName(), "fooMethod2", "Foo.java", 17);
    StackTraceElement ste2 =
        new StackTraceElement(StringMethods.class.getName(), "barMethod1", "Bar.java", 211);
    StackTraceElement ste3 =
        new StackTraceElement(Path.class.getName(), "bazMethod1", "Baz.java", 180);
    exception1 = new Exception("Something is wrong");
    exception1.setStackTrace(ArrayMethods.pack(ste0, ste1, ste2, ste3));

    exception2.setStackTrace(new StackTraceElement[0]);
  }

  @Test
  public void test01() {
    ExceptionOrigin eo = new ExceptionOrigin(exception1, "nl.naturalis.common");
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is wrong. java.lang.Exception at nl.naturalis.common.exception.ExceptionOriginTest.fooMethod1 (line 8)",
        eo.getDetailedMessage());
  }

  @Test
  public void test02() {
    ExceptionOrigin eo = new ExceptionOrigin(exception1, "naturalis");
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is wrong. java.lang.Exception at nl.naturalis.common.exception.ExceptionOriginTest.fooMethod1 (line 8)",
        eo.getDetailedMessage());
  }

  @Test
  public void test03() {
    ExceptionOrigin eo = new ExceptionOrigin(exception1, "nl.naturalis.common.path");
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is wrong. java.lang.Exception at nl.naturalis.common.path.Path.bazMethod1 (line 180)",
        eo.getDetailedMessage());
  }

  @Test
  public void test04() {
    ExceptionOrigin eo = new ExceptionOrigin(exception1, "com.example");
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is wrong. java.lang.Exception (not originating from com.example)",
        eo.getDetailedMessage());
  }

  @Test
  public void test05() {
    ExceptionOrigin eo = new ExceptionOrigin(exception1);
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is wrong. java.lang.Exception at nl.naturalis.common.exception.ExceptionOriginTest.fooMethod1 (line 8)",
        eo.getDetailedMessage());
  }

  @Test
  public void test06() {
    ExceptionOrigin eo = new ExceptionOrigin(exception2);
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is really wrong. java.lang.Exception (no stack trace available)",
        eo.getDetailedMessage());
  }

  @Test
  public void test07() {
    ExceptionOrigin eo = new ExceptionOrigin(exception2, "com.example");
    System.out.println(eo.getDetailedMessage());
    assertEquals(
        "Something is really wrong. java.lang.Exception (not originating from com.example)",
        eo.getDetailedMessage());
  }
}
