package nl.naturalis.common.collection;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import org.junit.Test;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.util.ExpansionType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeTreeMapTest {

  @Test
  public void test00() {
    assertEquals(1, 1);
    assertFalse(false);
    assertTrue(true);
  }

  // @Test
  public void test01() {
    Set<Class<?>> set =
        Set.of(
            FileOutputStream.class,
            StringBuilder.class,
            Map.class,
            Character.class,
            char.class,
            boolean.class,
            Set[].class,
            Double.class,
            Number.class,
            SortedSet.class,
            Closeable.class,
            Short.class,
            NavigableMap.class,
            int[][].class,
            AutoCloseable.class,
            ExpansionType.class,
            LinkedList.class,
            ArrayList.class,
            Iterable.class,
            ByteArrayOutputStream.class,
            DayOfWeek.class,
            Set.class,
            OutputStream.class,
            Object.class,
            NavigableSet.class,
            String.class,
            Month.class,
            Object[].class,
            AbstractTypeMap.class,
            Collection.class,
            RoundingMode.class,
            Integer.class,
            List.class,
            SortedMap.class,
            Enum.class,
            short.class,
            MyArrayList.class,
            Long.class,
            CharSequence.class,
            Function.class,
            int.class,
            MyArrayList2.class);

    Set<Class<?>> set2 = new TreeSet<>(new TTMComparatorFactory(new Class[0]).getComparator());
    set2.addAll(set);
    set2.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
  }

  @Test
  public void test02() {
    List<Class<?>> list = new ArrayList<>();
    list.addAll(
        List.of(
            FileOutputStream.class,
            StringBuilder.class,
            Map.class,
            Character.class,
            char.class,
            boolean.class,
            Set[].class,
            Double.class,
            Number.class,
            SortedSet.class,
            Closeable.class,
            Short.class,
            NavigableMap.class,
            int[][].class,
            AutoCloseable.class,
            ExpansionType.class,
            LinkedList.class,
            ArrayList.class,
            Iterable.class,
            ByteArrayOutputStream.class,
            DayOfWeek.class,
            Set.class,
            OutputStream.class,
            Object.class,
            NavigableSet.class,
            String.class,
            Month.class,
            Object[].class,
            AbstractTypeMap.class,
            Collection.class,
            RoundingMode.class,
            Integer.class,
            List.class,
            SortedMap.class,
            Enum.class,
            short.class,
            MyArrayList.class,
            Long.class,
            CharSequence.class,
            Function.class,
            int.class,
            MyArrayList2.class));

    Collections.sort(list, new BasicTypeMapComparator());
    list.forEach(c -> System.out.println(ClassMethods.simpleClassName(c)));
  }
}
