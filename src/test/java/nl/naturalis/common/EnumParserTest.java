package nl.naturalis.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EnumParserTest {

  private static enum TestEnum {
    DAY_ONE,
    DAY_TWO,
    _THIRD,
    _FOURTH_,
    FIFTH_DAY_IN_A_ROW;
  }

  @Test
  public void testDefaultNormalizer() {
    assertEquals("abcd", EnumParser.DEFAULT_NORMALIZER.apply("ABCD"));
    assertEquals("abcd", EnumParser.DEFAULT_NORMALIZER.apply("abcd"));
    assertEquals("abcdefg", EnumParser.DEFAULT_NORMALIZER.apply("abcd EFG"));
    assertEquals("abcdefg", EnumParser.DEFAULT_NORMALIZER.apply("ab-cd ef_G"));
    assertEquals(
        "abcdefg", EnumParser.DEFAULT_NORMALIZER.apply("ab-c           d ef____-----------G"));
  }

  @Test
  public void parse01() {
    EnumParser<TestEnum> parser = new EnumParser<>(TestEnum.class);
    TestEnum e = parser.parse("day one");
    assertEquals(TestEnum.DAY_ONE, e);
    e = parser.parse("DayTwo");
    assertEquals(TestEnum.DAY_TWO, e);
    e = parser.parse("third");
    assertEquals(TestEnum._THIRD, e);
    e = parser.parse(" fOurTh ");
    assertEquals(TestEnum._FOURTH_, e);
    e = parser.parse("fifthDayInARow");
    assertEquals(TestEnum.FIFTH_DAY_IN_A_ROW, e);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse02() {
    EnumParser<TestEnum> parser = new EnumParser<>(TestEnum.class);
    parser.parse("day*one");
  }
}
