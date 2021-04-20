package nl.naturalis.common;

import java.math.BigDecimal;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notNull;

class NumberParser<T extends Number> {

  private static final String ERR0 = "%s not parsable into %s";

  private final Class<T> targetType;

  NumberParser(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  T parse(String s) {
    Class<T> tt = targetType;
    Check.that(s).is(notNull(), ERR0, s, tt.getSimpleName());
    BigDecimal bd;
    try {
      bd = new BigDecimal(s);
    } catch (NumberFormatException e) {
      return Check.fail(ERR0, s, tt.getSimpleName());
    }
    return new NumberConverter<>(tt).convert(bd);
  }
}
