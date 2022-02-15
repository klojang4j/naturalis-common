package nl.naturalis.common.check;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.random.RandomGenerator;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(
    value = 2,
    jvmArgs = {"-Xms2G", "-Xmx2G"})
// @Warmup(iterations = 3)
// @Measurement(iterations = 8)
public class JMHCheckNotNull {

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder().include(JMHCheckNotNull.class.getSimpleName()).forks(1).build();
    new Runner(opt).run();
  }

  @Param({"10000"})
  private int sampleSize;

  public List<Object> NULLS_PCT_0 = new ArrayList<>(10000);
  public List<Object> NULLS_PCT_1 = new ArrayList<>(10000);
  public List<Object> NULLS_PCT_10 = new ArrayList<>(10000);

  @Benchmark
  public void checkNotNull_0pct_null(Blackhole bh) {
    for (int i = 0; i < sampleSize; ++i) {
      try {
        Object obj = Check.notNull(NULLS_PCT_0.get(i), "foo").ok();
        bh.consume(obj);
      } catch (IllegalArgumentException e) {
        bh.consume(e.getMessage());
      }
    }
  }

  @Setup
  public void setup() {
    createTestData();
  }

  public void createTestData() {
    RandomGenerator g = RandomGenerator.of("L64X128MixRandom");
    AtomicReference<Object> obj0 = new AtomicReference<>(new Object());
    AtomicReference<Object> obj1 = new AtomicReference<>(new Object());
    g.ints(sampleSize, 0, 100)
        .forEach(
            i -> {
              // Lots of really daft code to confuse the compiler
              if (i % 233 == 0) {
                obj0.setPlain(null);
                obj1.setPlain(new Object());
              } else if (i % 577 == 0) {
                obj0.setPlain(new Object());
                obj1.setPlain(null);
              }
              NULLS_PCT_0.add(
                  obj0.getPlain() == null
                      ? obj1.getPlain() == null ? new Object() : obj1.getPlain()
                      : obj0.getPlain());
              if (i % 100 == 0) {
                NULLS_PCT_1.add(null);
              } else {
                NULLS_PCT_1.add(
                    obj0.getPlain() == null
                        ? obj1.getPlain() == null ? new Object() : obj1.getPlain()
                        : obj0.getPlain());
              }
              if (i % 10 == 0) {
                NULLS_PCT_10.add(null);
              } else {
                NULLS_PCT_10.add(
                    obj0.getPlain() == null
                        ? obj1.getPlain() == null ? new Object() : obj1.getPlain()
                        : obj0.getPlain());
              }
            });
  }
}
