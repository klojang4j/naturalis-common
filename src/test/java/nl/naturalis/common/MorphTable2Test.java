package nl.naturalis.common;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class MorphTable2Test {

  @Test
  public void test00() {
    assertEquals(Month.FEBRUARY, MorphToEnum.getInstance().morph('1', Month.class));
    assertEquals(Month.DECEMBER, MorphToEnum.getInstance().morph(11, Month.class));
    assertEquals(Month.MARCH, MorphToEnum.getInstance().morph("march", Month.class));
    assertEquals(Month.JANUARY, MorphToEnum.getInstance().morph("0", Month.class));
    assertEquals(Month.APRIL,
        MorphToEnum.getInstance().morph((short) 3, Month.class));
    assertEquals(DayOfWeek.SATURDAY,
        MorphToEnum.getInstance().morph(5L, DayOfWeek.class));
  }

}
