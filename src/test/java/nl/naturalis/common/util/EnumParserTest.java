package nl.naturalis.common.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnumParserTest {

  private static enum TestEnum {
    DAY_ONE,
    DAY_TWO,
    _THIRD,
    _FOURTH_("^^^fourth^^^"),
    FIFTH_DAY_IN_A_ROW;
    private String s;

    private TestEnum() {
      this.s = name();
    }

    private TestEnum(String s) {
      this.s = s;
    }

    public String toString() {
      return s;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  @SuppressWarnings("unused")
  public void testBadNormalizer() {
    new EnumParser<>(TestEnum.class, s -> "Hi");
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
    assertSame("01", TestEnum.DAY_ONE, e);
    e = parser.parse("DayTwo");
    assertSame("02", TestEnum.DAY_TWO, e);
    e = parser.parse("third");
    assertSame("03", TestEnum._THIRD, e);
    e = parser.parse(" fOurTh ");
    assertSame("04", TestEnum._FOURTH_, e);
    e = parser.parse("^^^fOurTh^^^");
    assertSame("05", TestEnum._FOURTH_, e);
    e = parser.parse("fifthDayInARow");
    assertSame("06", TestEnum.FIFTH_DAY_IN_A_ROW, e);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse02() {
    EnumParser<TestEnum> parser = new EnumParser<>(TestEnum.class);
    parser.parse("day*one");
  }
}
