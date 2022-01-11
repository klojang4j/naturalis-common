package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.util.ExpansionType;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;
import java.util.function.Function;

import static nl.naturalis.common.ArrayMethods.find;
import static org.junit.Assert.*;

public class TypeTreeMapTest {

  @Test
  public void test00() {
    assertEquals(1, 1);
    assertFalse(false);
    assertTrue(true);
  }

  @Test
  public void test01() {
    List<Class<?>> orig =
        List.of(
            short.class,
            char.class,
            int.class,
            boolean.class,
            Character.class,
            Long.class,
            Double.class,
            Integer.class,
            Number.class,
            Short.class,
            FileOutputStream.class,
            ByteArrayOutputStream.class,
            OutputStream.class,
            RoundingMode.class,
            DayOfWeek.class,
            Month.class,
            Enum.class,
            StringBuilder.class,
            String.class,
            CharSequence.class,
            NavigableMap.class,
            AbstractTypeMap.class,
            SortedMap.class,
            Map.class,
            MyArrayList2.class,
            MyArrayList.class,
            ArrayList.class,
            LinkedList.class,
            List.class,
            SortedSet.class,
            NavigableSet.class,
            Set.class,
            Collection.class,
            Iterable.class,
            MyArrayList2[].class,
            MyArrayList[].class,
            ArrayList[].class,
            int[][].class,
            Set[].class,
            List[].class,
            Object[].class,
            Function.class,
            Closeable.class,
            AutoCloseable.class,
            Object.class);
    List list = new ArrayList<>(orig);
    for (int i = 0; i < 100; ++i) {
      Collections.shuffle(list);
      TreeSet<Class<?>> set = new TreeSet<>(new BasicTypeMapComparator());
      set.addAll(list);
      Class<?>[] types = set.toArray(Class[]::new);
      assertEquals(types.length - 1, find(types, Object.class));
      assertTrue(find(types, DayOfWeek.class) < find(types, Enum.class));
      assertTrue(find(types, Month.class) < find(types, Enum.class));
      assertTrue(find(types, RoundingMode.class) < find(types, Enum.class));
      assertTrue(find(types, FileOutputStream.class) < find(types, OutputStream.class));
      assertTrue(find(types, ByteArrayOutputStream.class) < find(types, OutputStream.class));
      assertTrue(find(types, Closeable.class) < find(types, AutoCloseable.class));
      assertTrue(find(types, StringBuilder.class) < find(types, CharSequence.class));
      assertTrue(find(types, String.class) < find(types, CharSequence.class));
      assertTrue(find(types, Short.class) < find(types, Number.class));
      assertTrue(find(types, Long.class) < find(types, Number.class));
      assertTrue(find(types, NavigableSet.class) < find(types, Set.class));
      assertTrue(find(types, Set.class) < find(types, Collection.class));
      assertTrue(find(types, Collection.class) < find(types, Iterable.class));
      assertTrue(find(types, MyArrayList2[].class) < find(types, MyArrayList[].class));
      assertTrue(find(types, MyArrayList[].class) < find(types, ArrayList[].class));
      assertTrue(find(types, ArrayList[].class) < find(types, List[].class));
      assertTrue(find(types, List[].class) < find(types, Object[].class));
    }
  }
}
