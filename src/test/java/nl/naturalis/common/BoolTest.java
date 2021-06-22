package nl.naturalis.common;

import java.io.ByteArrayOutputStream;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoolTest {

  public BoolTest() {}

  @Test
  public void test00() {
    assertTrue(Bool.from("True"));
    assertTrue(Bool.from("ON"));
    assertTrue(Bool.from("enabled"));
    assertTrue(Bool.from("1"));
    assertTrue(Bool.from("yEs"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test01() {
    Bool.from("01");
  }

  @Test
  public void test02() {
    assertFalse(Bool.from("FALSE"));
    assertFalse(Bool.from("off"));
    assertFalse(Bool.from("Disabled"));
    assertFalse(Bool.from("0"));
    assertFalse(Bool.from("NO"));
  }

  @Test
  public void test03() {
    assertTrue(Bool.from(1));
    assertTrue(Bool.from(1F));
    assertTrue(Bool.from(1L));
    assertTrue(Bool.from(1.0));
    assertTrue(Bool.from((short) 1));
    assertTrue(Bool.from((byte) 1));
  }

  @Test
  public void test04() {
    assertFalse(Bool.from(0));
    assertFalse(Bool.from(0F));
    assertFalse(Bool.from(0L));
    assertFalse(Bool.from(0.0));
    assertFalse(Bool.from((short) 0));
    assertFalse(Bool.from((byte) 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test05() {
    Bool.from(42);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test06() {
    Bool.from(0.23);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test07() {
    Bool.from(new ByteArrayOutputStream());
  }

  public void test08() {
    assertTrue(Bool.from(true));
    assertFalse(Bool.from(false));
    assertFalse(Bool.from((Object) null));
  }
}
