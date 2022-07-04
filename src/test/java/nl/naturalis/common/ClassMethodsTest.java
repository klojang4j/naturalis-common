package nl.naturalis.common;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class ClassMethodsTest {

  @Test
  public void isA00() {
    assertTrue(ClassMethods.isSubtype(String.class, String.class));
    assertTrue(ClassMethods.isSubtype(String.class, Object.class));
    assertTrue(ClassMethods.isSubtype(String.class, CharSequence.class));
    assertFalse(ClassMethods.isSubtype(Object.class, String.class));
    assertFalse(ClassMethods.isSubtype(CharSequence.class, String.class));
    assertTrue(ClassMethods.isSubtype(String.class, String.class));
    assertFalse(ClassMethods.isSubtype(short.class, int.class));
    assertTrue(ClassMethods.isSubtype(Serializable.class, Object.class));
    assertTrue(ClassMethods.isSubtype(Function.class, Object.class));
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
    assertEquals("java.lang.String[][][]",
        ClassMethods.arrayClassName(new String[0][0][0]));
  }

  @Test
  public void getArrayTypeSimpleName01() {
    assertEquals("String[][][]",
        ClassMethods.arrayClassSimpleName(new String[0][0][0]));
  }

  @Test // Interesting: Enum.class returns false for Class::isEnum
  public void isEnum01() {
    assertFalse(Enum.class.isEnum());
    assertTrue(ClassMethods.isSubtype(Enum.class, Enum.class));
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
    Set<Class<?>> expected = Set.of(SortedSet.class,
        Set.class,
        Collection.class,
        Iterable.class);
    Set<Class<?>> actual = ClassMethods.getAllInterfaces(NavigableSet.class);
    // System.out.println(implode(actual.toArray(), "\n"));
    assertEquals(expected, actual);
  }

  @Test
  public void getTypeDefaultIf00() {
    assertNull(ClassMethods.getTypeDefault(Class.class));
    assertNull(ClassMethods.getTypeDefault(Object.class));
    assertNull(ClassMethods.getTypeDefault(Cloneable.class));
    assertEquals((short) 0, (short) ClassMethods.getTypeDefault(short.class));
    assertEquals(0F, (float) ClassMethods.getTypeDefault(float.class), 0F);
  }

}
