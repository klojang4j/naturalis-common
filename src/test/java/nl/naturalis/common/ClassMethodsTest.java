package nl.naturalis.common;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
