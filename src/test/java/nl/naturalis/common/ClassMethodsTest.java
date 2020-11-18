package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

public class ClassMethodsTest {

  @Test
  public void isA_01() {
    assertTrue(ClassMethods.isA(String.class, Object.class));
  }

  @Test
  public void isA_02() {
    assertTrue(ClassMethods.isA(String.class, CharSequence.class));
  }

  @Test
  public void isA_03() {
    assertFalse(ClassMethods.isA(Object.class, String.class));
  }

  @Test
  public void isA_04() {
    assertFalse(ClassMethods.isA(CharSequence.class, String.class));
  }

  @Test
  public void isA_05() {
    assertTrue(ClassMethods.isA(String.class, String.class));
  }

  @Test
  public void getArrayTypeName_01() {
    assertEquals("java.lang.String[][][]", ClassMethods.getArrayTypeName(new String[0][0][0]));
  }

  @Test
  public void getArrayTypeSimpleName_01() {
    assertEquals("String[][][]", ClassMethods.getArrayTypeSimpleName(new String[0][0][0]));
  }
}
