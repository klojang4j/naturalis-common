package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import org.junit.Test;

import java.io.Closeable;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.naturalis.common.CollectionMethods.implode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrettyTypeMapComparatorTest {

  @Test
  public void test00() {
    String expected =
        "char int Double Integer Month MyArrayList2 MyArrayList ArrayList Enum "
            + "Number Closeable AutoCloseable String[] CharSequence[] Object[] Object";
    List<Class<?>> orig =
        List.of(
            int.class,
            char.class,
            MyArrayList2.class,
            MyArrayList.class,
            ArrayList.class,
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
    List<Class<?>> types = new ArrayList<>(orig);
    for (int i = 0; i < 200; ++i) {
      Collections.shuffle(types);
      List<String> sorted = List.copyOf(TypeTreeSet.sortAndGetSimpleNames(types));
      // System.out.println(implode(sorted, " "));
      assertEquals(expected, implode(sorted, " "));
    }
  }

  @Test
  public void test01() {
    assertTrue(ClassMethods.isA(String[].class, Object.class));
  }
}
