package nl.naturalis.common.check;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import org.junit.Test;
import nl.naturalis.common.IOMethods;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.StringMethods.duration;
import static nl.naturalis.common.check.CommonChecks.lte;

public class PerformanceTests {

  // @Test // Null check only
  public void test01() throws InterruptedException, IOException {

    if (skip()) {
      return;
    }

    System.out.println();
    System.out.println("Null check only");

    int repeats = 20 * 1000 * 1000;

    // Warm up
    System.out.println("Warming up Check class...");
    doTest01(repeats, false);
    System.out.println("Warming up manual check...");
    doTest01(repeats, true);

    repeats = 25 * 1000 * 1000;

    Thread.sleep(3000);
    System.out.println("Starting precondition testing using Check class ...");
    long start = now();
    doTest01(repeats, false);
    long end = now();
    System.out.println("Duration: " + duration(start, end));
    double time0 = end - start;

    Thread.sleep(3000);
    System.out.println("Starting manual precondition testing ...");
    start = now();
    doTest01(repeats, true);
    end = now();
    System.out.println("Duration: " + duration(start, end));

    double time1 = end - start;

    double diff = time0 / time1 - 1;

    DecimalFormat df = new DecimalFormat("0.0%");
    System.out.println("Pct. diff. ...: " + df.format(diff));

    // At most 10% slower
    assertTrue(diff <= .1);
  }

  @Test // Null check + string length check
  public void test02() throws InterruptedException {

    if (skip()) {
      return;
    }

    System.out.println();
    System.out.println("Null check plus size check");

    int repeats = 50 * 1000 * 1000;

    // Warm up
    System.out.println("Warming up Check class...");
    doTest02(repeats, false);
    System.out.println("Warming up manual check...");
    doTest02(repeats, true);

    repeats = 100 * 1000 * 1000;

    Thread.sleep(3000);
    System.out.println("Starting precondition testing using Check class ...");
    long start = now();
    doTest02(repeats, false);
    long end = now();
    System.out.println("Duration: " + duration(start, end));
    double time0 = end - start;

    Thread.sleep(3000);
    System.out.println("Starting manual precondition testing ...");
    start = now();
    doTest02(repeats, true);
    end = now();
    System.out.println("Duration: " + duration(start, end));

    double time1 = end - start;

    double diff = time0 / time1 - 1;

    DecimalFormat df = new DecimalFormat("0.0%");
    System.out.println("Pct. diff. ...: " + df.format(diff));

    // At most 10% slower
    assertTrue(diff <= .1);
  }

  private static void doTest01(int repeats, boolean manual) throws IOException {
    File foo = IOMethods.createTempFile();
    String s0 = "Hello World, what's up?";
    String s1;
    try (FileOutputStream fos = new FileOutputStream(foo); ) {
      for (int i = 0; i < repeats; ++i) {
        s1 = foo.length() < 8192 ? s0 : null;
        if (manual) {
          if (s1 == null) {
            throw new IllegalArgumentException("Argument must not be null");
          }
        } else {
          Check.notNull(s1);
        }
        if (i % 8388608 == 0) { // 8 * 1024 * 1024
          fos.write((byte) (i % 31));
          System.out.print(foo.length());
        }
      }
      System.out.println();
    } finally {
      foo.delete();
    }
  }

  @SuppressWarnings("resource")
  private static void doTest02(int repeats, boolean manual) {
    ByteArrayOutputStream b0 = new ByteArrayOutputStream();
    ByteArrayOutputStream b1;
    for (int i = 0; i < repeats; ++i) {
      b1 = b0.size() < 8192 ? b0 : null;
      if (manual) {
        if (b1 == null) {
          throw new IllegalArgumentException("Argument must not be null");
        } else if (b1.size() > 10000) {
          throw new IllegalArgumentException("Argument.length() must be <= 100");
        }
      } else {
        Check.notNull(b1).has(x -> x.size(), lte(), 10000);
      }
      if (i % 8388608 == 0) { // 8 * 1024 * 1024
        b0.write((byte) (i % 31));
        System.out.print(b0.size());
      }
    }
    System.out.println();
  }

  private static boolean skip() {
    String s = System.getProperty("perftests");
    return s == null || !(s.equals("") || s.equalsIgnoreCase("true"));
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
