package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.Getter;
import nl.naturalis.common.invoke.GetterFactory;
import nl.naturalis.common.unsafe.UnsafeByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static nl.naturalis.common.ArrayMethods.implodeAny;
import static nl.naturalis.common.StringMethods.append;
import static nl.naturalis.common.StringMethods.lpad;
import static nl.naturalis.common.check.CommonChecks.*;

public class PrintMethods {

  private static final byte[] NULL = "empty map".getBytes(UTF_8);

  private PrintMethods() {}

  public static void print(Map<?, ?> map, OutputStream out) {
    Check.notNull(out, "out");
    try {
      if (map == null) {
        out.write(NULL);
      } else if (map.isEmpty()) {
        out.write("empty map".getBytes(UTF_8));
      } else {
        PrintStream ps = (out instanceof PrintStream) ? (PrintStream) out : new PrintStream(out);
        Map<String, String> copy = new TreeMap<>((o1, o2) -> toStr(o1).compareTo(toStr(o2)));
        map.entrySet().forEach(e -> copy.put(toStr(e.getKey()), toStr(e.getValue())));
        int maxlen = copy.keySet().stream().mapToInt(String::length).max().getAsInt();
        for (Map.Entry<String, String> e : copy.entrySet()) {
          printKV(ps, e.getKey(), e.getValue(), maxlen);
          ps.println();
        }
      }
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public static String print(Map<?, ?> map) {
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(64);
    print(map, out);
    return new String(out.getBackingArray(), 0, out.size(), UTF_8);
  }

  public static void print(Object obj, OutputStream out) {
    Check.notNull(out, "out");
    try {
      if (obj == null) {
        out.write(NULL);
      } else {
        PrintStream ps = (out instanceof PrintStream) ? (PrintStream) out : new PrintStream(out);
        Map<String, Getter> getters = GetterFactory.INSTANCE.getGetters(obj.getClass(), true);
        int maxlen = getters.keySet().stream().mapToInt(String::length).max().getAsInt();
        Set<Getter> set = new TreeSet<>(comparing(Getter::getProperty));
        set.addAll(getters.values());
        for (Getter getter : set) {
          printKV(ps, getter.getProperty(), getter.read(obj), maxlen);
          ps.println();
        }
      }
    } catch (Throwable e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public static String print(Object obj) {
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(64);
    print(obj, out);
    return new String(out.getBackingArray(), 0, out.size(), UTF_8);
  }
  /**
   * Returns a human-friendly representation of the duration between the specified start and now.
   * Example: 540:00:12.630
   *
   * @param start The start time
   * @return A human-friendly representation of the duration between the specified start and now
   */
  public static String interval(long start) {
    return interval(start, System.currentTimeMillis());
  }

  /**
   * Returns a human-friendly representation of the duration of the specified time interval.
   * Example: 00:08:07.041
   *
   * @param start The start time
   * @param end The end time
   * @return A human-friendly representation of the duration of the specified time interval
   */
  public static String interval(long start, long end) {
    Check.that(end).is(GTE(), start, "Negative time interval");
    return duration(end - start);
  }

  /**
   * Returns a human-friendly representation of the specified duration in milliseconds.
   *
   * @param millis The duration in millisecond
   * @return A human-friendly representation of the specified duration in milliseconds
   */
  public static String duration(long millis) {
    long h = millis / (60 * 60 * 1000);
    millis %= (60 * 60 * 1000);
    long m = millis / (60 * 1000);
    millis %= (60 * 1000);
    long s = millis / 1000;
    millis %= 1000;
    return append(
            new StringBuilder(12),
            lpad(h, 2, '0', ":"),
            lpad(m, 2, '0', ":"),
            lpad(s, 2, '0', "."),
            lpad(millis, 3, '0'))
        .toString();
  }

  private static void printKV(PrintStream ps, Object k, Object v, int maxlen) {
    ps.append(lpad(k, maxlen)).append(" : ").append(toStr(v));
  }

  private static String toStr(Object o) {
    if (o == null) {
      return "null";
    } else if (o.getClass().isArray()) {
      return "[" + implodeAny(o) + "]";
    }
    return o.toString();
  }

  /**
   * Returns the line number and column number of the character at the specified index, given the
   * system-defined line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @return A two-element array containing the line number and column number of the character at
   *     the specified index
   */
  public static int[] getLineAndColumn(String str, int index) {
    return getLineAndColumn(str, index, System.lineSeparator());
  }

  /**
   * Returns the line number and column number of the character at the specified index, given the
   * specified line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @param lineSep The line separator
   * @return A two-element array containing the line number and column number of the character at
   *     the specified index
   */
  public static int[] getLineAndColumn(String str, int index, String lineSep) {
    Check.notNull(str, "str");
    Check.that(index, "index").is(gte(), 0).is(lt(), str.length());
    Check.that(lineSep, "lineSep").isNot(empty());
    if (index == 0) {
      return new int[] {0, 0};
    }
    int line = 0, pos = 0, i = str.indexOf(lineSep);
    while (i != -1 && i < index) {
      ++line;
      pos = i + lineSep.length();
      i = str.indexOf(lineSep, i + lineSep.length());
    }
    return new int[] {line, index - pos};
  }
}
