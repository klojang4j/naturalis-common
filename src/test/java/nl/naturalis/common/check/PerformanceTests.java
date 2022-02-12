package nl.naturalis.common.check;

import static nl.naturalis.common.PrintMethods.duration;
import static nl.naturalis.common.check.CommonChecks.lt;
import static nl.naturalis.common.check.CommonChecks.lte;
import static nl.naturalis.common.check.CommonChecks.ne;
import static nl.naturalis.common.check.CommonGetters.strlen;

import java.text.DecimalFormat;
import java.util.Random;
import org.junit.Test;

public class PerformanceTests {

  private static final Random random = new Random();
  private static final int WARMUP_REPEATS = Integer.MAX_VALUE;
  private static final int TEST_REPEATS = Integer.MAX_VALUE;
  private static final int POLL_INTERVAL = 32 * 1024 * 1024;
  private static final DecimalFormat PERCENTAGE = new DecimalFormat("0.0%");

  @Test
  public void test01() throws InterruptedException {
    if (skip()) {
      return;
    }
    System.out.println();
    System.out.println("****** Plain null check ******");

    System.out.println("Warming up Check class...");
    test01Check(WARMUP_REPEATS);
    System.out.println("Warming up manual check...");
    test01Manual(WARMUP_REPEATS);

    Thread.sleep(3000);
    System.out.println("Measuring Check class ...");
    long time0 = test01Check(TEST_REPEATS);
    System.out.println("Duration: " + duration(time0));

    Thread.sleep(3000);
    System.out.println("Measuring manual check  ...");
    long time1 = test01Manual(TEST_REPEATS);
    System.out.println("Duration: " + duration(time1));

    double diff = (double) time0 / (double) time1 - 1;
    System.out.println("Pct. diff. ...: " + PERCENTAGE.format(diff));
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
    System.out.println("****** Two checks on StringBuilder object ******");

    System.out.println("Warming up Check class...");
    test02Check(WARMUP_REPEATS);
    System.out.println("Warming up manual check...");
    test02Manual(WARMUP_REPEATS);

    Thread.sleep(3000);
    System.out.println("Measuring Check class ...");
    long time0 = test02Check(TEST_REPEATS);
    System.out.println("Duration: " + duration(time0));

    Thread.sleep(3000);
    System.out.println("Measuring manual check  ...");
    long time1 = test02Manual(TEST_REPEATS);
    System.out.println("Duration: " + duration(time1));

    double diff = (double) time0 / (double) time1 - 1;
    System.out.println("Pct. diff. ...: " + PERCENTAGE.format(diff));
    if (diff > .1) {
      System.out.println("****************************************************************");
      System.out.println("****  WARNING: Check CLASS MORE THAN 10% SLOWER IN test01    ***");
      System.out.println("****************************************************************");
    }
  }

  @Test
  public void test03() throws InterruptedException {
    if (skip()) {
      return;
    }
    System.out.println();
    System.out.println("****** Three checks on StringBuilder ******");

    System.out.println("Warming up Check class...");
    test03Check(WARMUP_REPEATS);
    System.out.println("Warming up manual check...");
    test03Manual(WARMUP_REPEATS);

    Thread.sleep(3000);
    System.out.println("Measuring Check class ...");
    long time0 = test03Check(TEST_REPEATS);
    System.out.println("Duration: " + duration(time0));

    Thread.sleep(3000);
    System.out.println("Measuring manual check  ...");
    long time1 = test03Manual(TEST_REPEATS);
    System.out.println("Duration: " + duration(time1));

    double diff = (double) time0 / (double) time1 - 1;
    System.out.println("Pct. diff. ...: " + PERCENTAGE.format(diff));
    if (diff > .1) {
      System.out.println("****************************************************************");
      System.out.println("****  WARNING: Check CLASS MORE THAN 10% SLOWER IN test01    ***");
      System.out.println("****************************************************************");
    }
  }

  @Test
  public void test04() throws InterruptedException {
    if (skip()) {
      return;
    }
    System.out.println();
    System.out.println("****** Single check on int ******");

    System.out.println("Warming up Check class...");
    test04Check(WARMUP_REPEATS);
    System.out.println("Warming up manual check...");
    test04Manual(WARMUP_REPEATS);

    Thread.sleep(3000);
    System.out.println("Measuring Check class ...");
    long time0 = test04Check(TEST_REPEATS);
    System.out.println("Duration: " + duration(time0));

    Thread.sleep(3000);
    System.out.println("Measuring manual check  ...");
    long time1 = test04Manual(TEST_REPEATS);
    System.out.println("Duration: " + duration(time1));

    double diff = (double) time0 / (double) time1 - 1;
    System.out.println("Pct. diff. ...: " + PERCENTAGE.format(diff));
    if (diff > .1) {
      System.out.println("****************************************************************");
      System.out.println("****  WARNING: Check CLASS MORE THAN 10% SLOWER IN test01    ***");
      System.out.println("****************************************************************");
    }
  }

  private static long test01Check(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      Check.notNull(sb1);
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test01Manual(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      if (sb1 == null) {
        throw new IllegalArgumentException("Argument must not be null");
      }
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test02Check(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      Check.notNull(sb1).has(strlen(), lte(), 10000);
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test02Manual(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      if (sb1 == null) {
        throw new IllegalArgumentException("Argument must not be null");
      }
      if (sb1.length() > 10000) {
        throw new IllegalArgumentException("Argument.length() must be >= 100");
      }
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test03Check(int repeats) {
    StringBuilder sb0 = new StringBuilder(256).append('0');
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      Check.notNull(sb1)
          .has(strlen(), lte(), 10000)
          .has(sb -> sb.charAt(sb.length() - 1), ne(), '?');
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test03Manual(int repeats) {
    StringBuilder sb0 = new StringBuilder(256).append('0');
    StringBuilder sb1 = null;
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      sb1 = sb0.length() < 8192 ? sb0 : null; // Make sure it's always gonna be sb0
      if (sb1 == null) {
        throw new IllegalArgumentException("Argument must not be null");
      }
      if (sb1.length() > 10000) {
        throw new IllegalArgumentException("Argument.length() must be >= 100");
      }
      if (sb1.charAt(sb1.length() - 1) == '?') {
        throw new IllegalArgumentException("Argument must not end with ?");
      }
      if (i % POLL_INTERVAL == 0) { // Just append some digit
        sb1.append((char) (48 + random.nextInt(10)));
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb1); // Print so compiler can't optimize the whole thing away
    return t;
  }

  private static long test04Check(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      Check.that(sb0.length()).is(lt(), 256);
      if (i % POLL_INTERVAL == 0) {
        sb0.append('0');
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb0);
    return t;
  }

  private static long test04Manual(int repeats) {
    StringBuilder sb0 = new StringBuilder(256);
    long t = System.currentTimeMillis();
    for (int i = 0; i < repeats; ++i) {
      if (sb0.length() >= 256) {
        throw new IllegalArgumentException("Argument must be < 256");
      }
      if (i % POLL_INTERVAL == 0) {
        sb0.append('0');
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.println(sb0);
    return t;
  }

  private static boolean skip() {
    String s = System.getProperty("perftest");
    return s == null || s.equalsIgnoreCase("true");
  }
}
