package nl.naturalis.common;

import java.io.File;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalMethodsTest {

  @Test
  public void narrow_01() {
    Optional<CharSequence> opt1 = Optional.of("Hello, world");
    Optional<String> opt2 = OptionalMethods.narrow(opt1);
    assertTrue(opt1.get() == opt2.get());
    assertFalse(System.identityHashCode(opt1) == System.identityHashCode(opt2));
  }

  @Test
  public void narrow_02a() {
    Optional<Object> opt1 = Optional.of(new File("/tmp/test.txt"));
    Optional<String> opt2 = OptionalMethods.narrow(opt1); // will compile and run
    assertTrue(opt1.get() == opt2.get());
    assertFalse(System.identityHashCode(opt1) == System.identityHashCode(opt2));
    // We can make these assertions!
  }

  @Test(expected = ClassCastException.class)
  public void narrow_02b() {
    Optional<Object> opt1 = Optional.of(new File("/tmp/test.txt"));
    Optional<String> opt2 = OptionalMethods.narrow(opt1); // will compile and run
    opt2.get().charAt(0); // but now we get a runtime exception
  }

  @Test
  public void narrow_03() {
    Optional<Object> opt1 = Optional.empty();
    Optional<String> opt2 = OptionalMethods.narrow(opt1);
    assertTrue(System.identityHashCode(opt1) == System.identityHashCode(opt2));
  }

  @Test
  public void widen_01() {
    Optional<Float> opt1 = Optional.of(Float.valueOf(6F));
    Optional<Number> opt2 = OptionalMethods.widen(opt1);
    assertTrue(opt1.get() == opt2.get());
    assertTrue(System.identityHashCode(opt1) == System.identityHashCode(opt2));
  }

  @Test
  public void widen_02() {
    Optional<String> opt1 = Optional.of("Hello, world");
    Optional<CharSequence> opt2 = OptionalMethods.widen(opt1);
    assertTrue(opt1.get() == opt2.get());
    assertTrue(System.identityHashCode(opt1) == System.identityHashCode(opt2));
  }

  @Test
  public void get_01() {
    Optional<CharSequence> opt = Optional.of("Hello, world");
    String s = OptionalMethods.get(opt);
    assertTrue(s == opt.get());
  }

}
