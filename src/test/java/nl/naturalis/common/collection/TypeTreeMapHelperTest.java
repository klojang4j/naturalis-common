package nl.naturalis.common.collection;

import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.math.RoundingMode;
import java.util.*;
import org.junit.Test;
import nl.naturalis.common.ClassMethods;
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
    Collections.sort(l, new TypeComparatorFactory(new Class[0]).getComparator());
    l.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
    assertTrue(l.indexOf(Collection.class) < l.indexOf(Iterable.class));
    assertTrue(l.indexOf(Set.class) < l.indexOf(Collection.class));
    assertTrue(l.indexOf(Enum.class) < l.indexOf(Set.class));
    assertTrue(l.indexOf(OutputStream.class) < l.indexOf(Set.class));
    assertTrue(l.indexOf(Number.class) < l.indexOf(Set.class));
  }
}
