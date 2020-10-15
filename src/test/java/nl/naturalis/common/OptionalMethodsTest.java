package nl.naturalis.common;

import java.io.File;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static nl.naturalis.common.OptionalMethods.contentsOf;
import static nl.naturalis.common.OptionalMethods.narrow;
import static nl.naturalis.common.OptionalMethods.widen;

public class OptionalMethodsTest {

  @Test
  public void narrow01() {
    Optional<CharSequence> opt1 = Optional.of("Hello, world");
    Optional<String> opt2 = narrow(opt1);
    assertNotSame("01", opt1, opt2);
    assertSame("02", opt1.get(), opt2.get());
  }

  @Test
  public void narrow02a() {
    Optional<Object> opt1 = Optional.of(new File("/tmp/test.txt"));
    Optional<String> opt2 = OptionalMethods.narrow(opt1); // Yikes - compiles and runs!
    assertNotSame("01", opt1, opt2);
    assertSame("02", opt1.get(), opt2.get());
    // Too bad we got to this point in the 1st place
  }

  @Test(expected = ClassCastException.class)
  public void narrow02b() {
    Optional<Object> opt1 = Optional.of(new File("/tmp/test.txt"));
    Optional<String> opt2 = OptionalMethods.narrow(opt1); // compiles and runs
    opt2.get().charAt(0); // only now we get a ClassCastException
  }

  @Test
  public void narrow03() {
    Optional<Object> opt1 = Optional.empty();
    Optional<String> opt2 = narrow(opt1);
    assertSame("01", opt1, opt2);
  }

  @Test
  public void widen01() {
    Optional<Float> opt1 = Optional.of(Float.valueOf(6F));
    Optional<Number> opt2 = widen(opt1);
    assertSame("01", opt1, opt2);
    assertSame("02", opt1.get(), opt2.get());
  }

  @Test
  public void widen02() {
    Optional<String> opt1 = Optional.of("Hello, world");
    Optional<CharSequence> opt2 = widen(opt1);
    assertSame("01", opt1, opt2);
    assertSame("02", opt1.get(), opt2.get());
  }

  @Test
  public void contentsOf01() {
    CharSequence cs = "Hello, world";
    Optional<CharSequence> opt = Optional.of(cs);
    String s = contentsOf(opt);
    assertSame("01", cs, s);
  }
}
