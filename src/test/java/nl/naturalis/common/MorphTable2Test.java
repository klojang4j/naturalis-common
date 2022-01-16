package nl.naturalis.common;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class MorphTable2Test {

  @Test
  public void test00() {
    assertEquals(Month.FEBRUARY, MorphTable2.getInstance().morph('1', Month.class));
    assertEquals(Month.DECEMBER, MorphTable2.getInstance().morph(11, Month.class));
    assertEquals(Month.MARCH, MorphTable2.getInstance().morph("march", Month.class));
    assertEquals(Month.JANUARY, MorphTable2.getInstance().morph("0", Month.class));
    assertEquals(Month.APRIL, MorphTable2.getInstance().morph((short) 3, Month.class));
    assertEquals(DayOfWeek.SATURDAY, MorphTable2.getInstance().morph(5L, DayOfWeek.class));
  }
}
