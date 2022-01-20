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
import static nl.naturalis.common.StringMethods.lpad;

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
}
