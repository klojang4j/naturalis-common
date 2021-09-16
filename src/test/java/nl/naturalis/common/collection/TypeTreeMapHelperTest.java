package nl.naturalis.common.collection;

import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.math.RoundingMode;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeTreeMapHelperTest {
  @Test
  public void test00() {
    assertEquals(1, 1);
    assertFalse(false);
    assertTrue(true);
  }

  @Test
  public void test02() {
    assertTrue(int[].class.isArray());
    assertTrue(Modifier.isAbstract(int[].class.getModifiers())); // surprise!
    assertTrue(Modifier.isAbstract(String[].class.getModifiers())); // surprise!
    assertTrue(Modifier.isAbstract(CharSequence[].class.getModifiers())); // surprise!
    assertTrue(Modifier.isAbstract(int.class.getModifiers())); // surprise!
    assertTrue(Modifier.isAbstract(CharSequence.class.getModifiers())); // surprise!
    assertTrue(Modifier.isAbstract(Number.class.getModifiers()));
    assertTrue(Modifier.isAbstract(Enum.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RoundingMode.class.getModifiers()));
    assertFalse(Modifier.isAbstract(String.class.getModifiers()));
    assertFalse(Modifier.isAbstract(Integer.class.getModifiers()));
  }

  @Test
  public void test03() {
    List<Class<?>> l =
        new ArrayList<>(
            List.of(
                OutputStream.class,
                Set.class,
                Collection.class,
                Enum.class,
                Iterable.class,
                Number.class));
    for (int i = 0; i < 20; ++i) {
      Collections.sort(l, new TypeTreeMapHelper(new Class[0]).getComparator());
      assertTrue(l.indexOf(Collection.class) < l.indexOf(Iterable.class));
      assertTrue(l.indexOf(Set.class) < l.indexOf(Collection.class));
      assertTrue(l.indexOf(Enum.class) < l.indexOf(Set.class));
      assertTrue(l.indexOf(OutputStream.class) < l.indexOf(Set.class));
      assertTrue(l.indexOf(Number.class) < l.indexOf(Set.class));
      Collections.shuffle(l);
    }
  }

  @Test
  public void test04() {
    List<Class<?>> l =
        new ArrayList<>(
            List.of(
                RoundingMode.class,
                HashSet.class,
                Set.class,
                Integer.class,
                int.class,
                Enum.class,
                Iterable.class,
                Number.class));
    for (int i = 0; i < 20; ++i) {
      Collections.sort(l, new TypeTreeMapHelper(new Class[0]).getComparator());
      // l.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
      assertTrue(l.indexOf(int.class) < l.indexOf(Integer.class));
      assertTrue(l.indexOf(Integer.class) < l.indexOf(RoundingMode.class));
      assertTrue(l.indexOf(RoundingMode.class) < l.indexOf(HashSet.class));
      assertTrue(l.indexOf(HashSet.class) < l.indexOf(Enum.class));
      assertTrue(l.indexOf(HashSet.class) < l.indexOf(Number.class));
      assertTrue(l.indexOf(Number.class) < l.indexOf(Set.class));
      assertTrue(l.indexOf(Set.class) < l.indexOf(Iterable.class));
      Collections.shuffle(l);
    }
  }

  @Test
  public void test05() {
    List<Class<?>> l =
        new ArrayList<>(
            List.of(
                RoundingMode.class,
                HashSet.class,
                Set.class,
                Integer.class,
                int.class,
                Enum.class,
                Iterable.class,
                Number.class));
    for (int i = 0; i < 20; ++i) {
      Collections.sort(l, new TypeTreeMapHelper(new Class[] {Iterable.class}).getComparator());
      // l.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
      assertTrue(l.indexOf(Iterable.class) < l.indexOf(int.class));
      assertTrue(l.indexOf(int.class) < l.indexOf(Integer.class));
      assertTrue(l.indexOf(Integer.class) < l.indexOf(RoundingMode.class));
      assertTrue(l.indexOf(RoundingMode.class) < l.indexOf(HashSet.class));
      assertTrue(l.indexOf(HashSet.class) < l.indexOf(Enum.class));
      assertTrue(l.indexOf(HashSet.class) < l.indexOf(Number.class));
      assertTrue(l.indexOf(Number.class) < l.indexOf(Set.class));
      Collections.shuffle(l);
    }
  }
}
