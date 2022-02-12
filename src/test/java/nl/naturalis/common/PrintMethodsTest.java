package nl.naturalis.common;

import org.junit.Test;

import static nl.naturalis.common.PrintMethods.interval;
import static org.junit.Assert.assertEquals;

public class PrintMethodsTest {

  @Test(expected = IllegalArgumentException.class)
  public void duration01() {
    String s = interval(1000L, 10001L);
    assertEquals("00:00:09.001", s);
    s = interval(1000L, 40080L);
    assertEquals("00:00:39.080", s);
    s = interval(1000L, 3641689L);
    assertEquals("01:00:40.689", s);
    s = interval(1000L, 123641689L);
    assertEquals("34:20:40.689", s);
    s = interval(1000L, 5123641689L);
    assertEquals("1423:14:00.689", s);
    s = interval(1000L, 995123641689L);
    assertEquals("276423:14:00.689", s);
    s = interval(1000L, 1000L);
    assertEquals("00:00:00.000", s);
    s = interval(9000L, 1000L); // Negative time interval
  }
}
