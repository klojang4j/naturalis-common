package nl.naturalis.common.check;

import java.text.DecimalFormat;
import java.util.Random;
import org.junit.Test;
import static nl.naturalis.common.StringMethods.duration;
import static nl.naturalis.common.check.CommonChecks.lte;
import static nl.naturalis.common.check.CommonGetters.strlen;

public class PerformanceTests {

  private static final Random random = new Random();
  private static final int WARMUP_REPEATS = 2 * 1000 * 1000 * 1000;
  private static final int TEST_REPEATS = 2 * 1000 * 1000 * 1000;
  private static final int POLL_INTERVAL = 16 * 1024 * 1024;

  @Test // Null check only
  public void test01() throws InterruptedException {

    if (skip()) {
      return;
    }

    System.out.println();
    System.out.println("****** PLAIN NULL CHECK ******");

    // Warm up
    System.out.println("Warming up Check class...");
    System.out.println(doTest01(WARMUP_REPEATS, false));
    System.out.println("Warming up manual check...");
    System.out.println(doTest01(WARMUP_REPEATS, true));

    StringBuilder sb;

    Thread.sleep(3000);
    System.out.println("Starting precondition testing using Check class ...");
    long start = now();
    sb = doTest01(TEST_REPEATS, false);
    long end = now();
    System.out.println(sb);
    System.out.println("Duration: " + duration(start, end));
    double time0 = end - start;

    Thread.sleep(3000);
    System.out.println("Starting manual precondition testing ...");
    start = now();
    sb = doTest01(TEST_REPEATS, true);
    end = now();
    System.out.println(sb);
    System.out.println("Duration: " + duration(start, end));

    double time1 = end - start;

    double diff = time0 / time1 - 1;

    DecimalFormat df = new DecimalFormat("0.0%");
    System.out.println("Pct. diff. ...: " + df.format(diff));

    if (diff > .1) {
      System.out.println("****************************************************************");
      System.out.println("****  WARNING: Check CLASS MORE THAN 10% SLOWER IN test01    ***");
      System.out.println("****************************************************************");
    }
  }

  @Test
  public void test02() throws InterruptedException {

    if (skip()) {
      return;
    }

    System.out.println();
    System.out.println("****** NULL CHECK + LENGTH CHECK ******");

    // Warm up
    System.out.println("Warming up Check class...");
    System.out.println(doTest02(WARMUP_REPEATS, false));
    System.out.println("Warming up manual check...");
    System.out.println(doTest02(WARMUP_REPEATS, true));

    StringBuilder sb;

    Thread.sleep(3000);
    System.out.println("Starting precondition testing using Check class ...");
    long start = now();
    sb = doTest02(TEST_REPEATS, false);
    long end = now();
    System.out.println(sb);
    System.out.println("Duration: " + duration(start, end));
    double time0 = end - start;

    Thread.sleep(3000);
    System.out.println("Starting manual precondition testing ...");
    start = now();
    sb = doTest02(TEST_REPEATS, true);
    end = now();
    System.out.println(sb);
    System.out.println("Duration: " + duration(start, end));

    double time1 = end - start;

    double diff = time0 / time1 - 1;

    DecimalFormat df = new DecimalFormat("0.0%");
    System.out.println("Pct. diff. ...: " + df.format(diff));

    if (diff > .1) {
      System.out.println("****************************************************************");
      System.out.println("****  WARNING: Check CLASS MORE THAN 10% SLOWER IN test02    ***");
      System.out.println("****************************************************************");
    }
  }

  private static StringBuilder doTest01(int repeats, boolean manual) {
    StringBuilder sb0 = new StringBuilder(256);
    StringBuilder sb1 = null;
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      if (manual) {
        if (sb1 == null) {
          throw new IllegalArgumentException("Argument must not be null");
        }
      } else {
        Check.notNull(sb1);
      }
      if (i % POLL_INTERVAL == 0) {
        // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    return sb1; // Let it escape so compiler can't compile the whole thing away
  }

  private static StringBuilder doTest02(int repeats, boolean manual) {
    StringBuilder sb0 = new StringBuilder();
    StringBuilder sb1 = null;
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null;
      if (manual) {
        if (sb1 == null) {
          throw new IllegalArgumentException("Argument must not be null");
        } else if (sb1.length() > 10000) {
          throw new IllegalArgumentException("Argument.length() must be <= 100");
        }
      } else {
        Check.notNull(sb1).has(strlen(), lte(), 10000);
      }
      if (i % POLL_INTERVAL == 0) {
        // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    return sb1; // Let it escape so compiler can't compile the whole thing away
  }

  private static boolean skip() {
    String s = System.getProperty("perftest.skip");
    return s != null && (s.equals("") || s.equalsIgnoreCase("true"));
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
