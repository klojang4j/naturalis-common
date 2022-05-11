package nl.naturalis.common;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassMethodsTest {

  @Test
  public void isA00() {
    assertTrue(ClassMethods.isA(String.class, String.class));
    assertTrue(ClassMethods.isA(String.class, Object.class));
    assertTrue(ClassMethods.isA(String.class, CharSequence.class));
    assertFalse(ClassMethods.isA(Object.class, String.class));
    assertFalse(ClassMethods.isA(CharSequence.class, String.class));
    assertTrue(ClassMethods.isA(String.class, String.class));
    assertFalse(ClassMethods.isA(short.class, int.class));
    assertTrue(ClassMethods.isA(Serializable.class, Object.class));
    assertTrue(ClassMethods.isA(Function.class, Object.class));
  }

  public void isA01() {
    assertTrue(ClassMethods.isA("Foo", String.class));
    assertTrue(ClassMethods.isA("Foo", Object.class));
    assertTrue(ClassMethods.isA("Foo", CharSequence.class));
    assertTrue(ClassMethods.isA(new Object(), String.class));
    assertFalse(ClassMethods.isA((short) 42, int.class));
  }

  @Test
  public void getArrayTypeName01() {
    assertEquals("java.lang.String[][][]", ClassMethods.arrayClassName(new String[0][0][0]));
  }

  @Test
  public void getArrayTypeSimpleName01() {
    assertEquals("String[][][]", ClassMethods.arrayClassSimpleName(new String[0][0][0]));
  }

  @Test // Interesting: Enum.class returns false for Class::isEnum
  public void isEnum01() {
    assertFalse(Enum.class.isEnum());
    assertTrue(ClassMethods.isA(Enum.class, Enum.class));
  }

  @Test
  public void getAllInterfaces00() {
    Set<Class<?>> expected = Set.of(NavigableSet.class,
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
