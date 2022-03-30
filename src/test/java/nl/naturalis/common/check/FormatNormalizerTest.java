package nl.naturalis.common.check;

import org.junit.Test;

import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.check.CustomMsgFormatter.*;
import static nl.naturalis.common.check.MsgUtil.simpleClassName;
import static org.junit.Assert.assertEquals;

public class FormatNormalizerTest {

  private static final Object[] args =
      pack("TEST",
          "VALUE",
          "TYPE",
          "ARG_NAME",
          "OBJ",
          "extra1",
          "extra2",
          "extra3");

  @Test
  public void test00() {
    String in = "Check \"${test}\" did not go wel for argument ${name}";
    String out = format(in, args);
    assertEquals("Check \"TEST\" did not go wel for argument ARG_NAME", out);
  }

  @Test
  public void test01() {
    String in = "Watch out for ${0} when using ${2}";
    String out = format(in, args);
    assertEquals("Watch out for extra1 when using extra3", out);
  }

  @Test
  public void test02() {
    String in = "Check \"${test2}\" did not go wel for argument ${name0}";
    String out = format(in, args);
    assertEquals(in, out);
  }

  @Test
  public void test03() {
    String in = "Unexpected type: ${type}";
    String out = format(in, args);
    assertEquals("Unexpected type: TYPE", out);
  }

  @Test
  public void test04() {
    String in = "${arg} did not have required relation to ${obj}";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to OBJ", out);
  }

  @Test
  public void test05() {
    String in = "${arg} did not have required relation to ${9}";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to ${9}", out);
  }

  @Test
  public void test07() {
    String in = "${arg} did not have required relation to $";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to $", out);
  }

  @Test
  public void test08() {
    String in = "${arg} did not have required relation to ${";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to ${", out);
  }

  @Test
  public void test09() {
    String in = "${arg} did not have required relation to ${a";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to ${a", out);
  }

  @Test
  public void test10() {
    String in = "${arg} did not have required relation to ${arg";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to ${arg", out);
  }

  @Test
  public void test11() {
    String in = "${arg} did not have required relation to ${skunk} (sorry)";
    String out = format(in, args);
    assertEquals("VALUE did not have required relation to ${skunk} (sorry)", out);
  }

  @Test
  public void test12() {
    String in = "";
    String out = format(in, args);
    assertEquals("", out);
  }
}
