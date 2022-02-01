package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Closeable;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.math.RoundingMode;
import java.time.Month;
import java.util.*;

import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.CollectionMethods.implode;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

public class BasicTypeComparatorTest {

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
    Collections.sort(l, TypeComparatorFactory.getComparator(new Class[0]));
    l.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
    assertTrue(l.indexOf(Collection.class) < l.indexOf(Iterable.class));
    assertTrue(l.indexOf(Set.class) < l.indexOf(Collection.class));
    assertTrue(l.indexOf(Enum.class) < l.indexOf(Set.class));
    assertTrue(l.indexOf(OutputStream.class) < l.indexOf(Set.class));
    assertTrue(l.indexOf(Number.class) < l.indexOf(Set.class));
  }

  @Test
  @Ignore
  // just interesting to watch. You cannot use this test in a Maven build b/c in the end
  // it will come down to comparing hash codes for Class objects and these depend (probably)
  // on the other in which they are loaded
  public void test04() {
    List<Class<?>> orig =
        List.of(
            int.class,
            char.class,
            MyArrayList2.class,
            MyArrayList.class,
            ArrayList.class,
            List.class,
            Double.class,
            Integer.class,
            Number.class,
            Month.class,
            Enum.class,
            Closeable.class,
            AutoCloseable.class,
            Object[].class,
            String[].class,
            CharSequence[].class,
            Object.class);
    List<Class<?>> l = new ArrayList<>(orig);
    for (int i = 0; i < 200; ++i) {
      Collections.shuffle(l);
      Collections.sort(l, new BasicTypeComparator());
      assertEquals(0, l.indexOf(MyArrayList2.class));
      assertEquals(l.size() - 1, l.indexOf(AutoCloseable.class));
      // Above List are 2 more interfaces; above Closeable only 1
      assertTrue(l.indexOf(List.class) < l.indexOf(Closeable.class));
      assertTrue(l.indexOf(MyArrayList.class) < l.indexOf(ArrayList.class));
      assertTrue(l.indexOf(String[].class) < l.indexOf(CharSequence[].class));
      assertTrue(l.indexOf(CharSequence[].class) < l.indexOf(Object[].class));
      System.out.println(implode(l, ClassMethods::simpleClassName, " ", 0, -1));
    }
  }
}
