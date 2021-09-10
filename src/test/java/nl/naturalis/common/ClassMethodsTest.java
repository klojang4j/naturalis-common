package nl.naturalis.common;

import java.io.Serializable;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
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

  @Test
  public void getArrayTypeName_01() {
    assertEquals("java.lang.String[][][]", ClassMethods.arrayClassName(new String[0][0][0]));
  }

  @Test
  public void getArrayTypeSimpleName_01() {
    assertEquals("String[][][]", ClassMethods.arrayClassSimpleName(new String[0][0][0]));
  }

  @Test // Interesting: Enum.class returns false for Class::isEnum
  public void isEnum01() {
    assertFalse(Enum.class.isEnum());
    assertTrue(ClassMethods.isA(Enum.class, Enum.class));
  }

  @Test
  public void getAllInterfaces00() {
    Set<Class<?>> expected =
        Set.of(
            NavigableSet.class,
            Cloneable.class,
            Serializable.class,
            SortedSet.class,
            Set.class,
            Collection.class,
            Iterable.class);
    Set<Class<?>> actual = ClassMethods.getAllInterfaces(TreeSet.class);
    // System.out.println(implode(actual.toArray(), "\n"));
    assertEquals(expected, actual);
  }

  @Test
  public void getAllInterfaces01() {
    Set<Class<?>> expected = Set.of(SortedSet.class, Set.class, Collection.class, Iterable.class);
    Set<Class<?>> actual = ClassMethods.getAllInterfaces(NavigableSet.class);
    // System.out.println(implode(actual.toArray(), "\n"));
    assertEquals(expected, actual);
  }
}
