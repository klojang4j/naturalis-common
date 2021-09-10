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
import nl.naturalis.common.util.ExpansionType;
import static nl.naturalis.common.StringMethods.implode;

public class TypeTreeSetTest {

  // @Test
  public void test00() {
    TypeTreeSet tts =
        TypeTreeSet.withTypes(
            MyArrayList.class,
            FileOutputStream.class,
            StringBuilder.class,
            Map.class,
            SortedSet.class,
            ExpansionType.class,
            NavigableMap.class,
            NavigableSet.class,
            ArrayList.class,
            Enum.class,
            List.class,
            SortedMap.class,
            CharSequence.class,
            ByteArrayOutputStream.class,
            OutputStream.class,
            Object.class,
            Long.class,
            Set.class,
            String.class,
            Month.class,
            Iterable.class,
            AbstractTypeMap.class,
            Collection.class,
            LinkedList.class,
            RoundingMode.class,
            AbstractTypeSet.class,
            Number.class,
            Function.class,
            DayOfWeek.class,
            MyArrayList2.class);
    System.out.println();
    System.out.println(implode(tts.prettySimpleTypeNames(), "\n"));
  }

  @Test
  public void test01() {
    TypeTreeSet tts =
        TypeTreeSet.withTypes(
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
            MyArrayList.class,
            Long.class,
            CharSequence.class,
            Function.class,
            int.class,
            MyArrayList2.class);
    System.out.println(implode(tts.prettySimpleTypeNames(), "\n"));
  }
}
