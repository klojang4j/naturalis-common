package nl.naturalis.common.check;

import org.junit.Test;

import static nl.naturalis.common.check.FormatNormalizer.normalize;
import static org.junit.Assert.assertEquals;

public class FormatNormalizerTest {

  @Test
  public void test00() {
    String in = "Check \"${test}\" did not go wel for argument ${name}";
    String out = normalize(in);
    assertEquals("Check \"%1$s\" did not go wel for argument %4$s", out);
  }

  @Test
  public void test01() {
    String in = "Watch out for ${0} when using ${5}";
    String out = normalize(in);
    assertEquals("Watch out for %6$s when using %11$s", out);
  }

  @Test
  public void test02() {
    String in = "Check \"${test2}\" did not go wel for argument ${name0}";
    String out = normalize(in);
    assertEquals("Check \"${test2}\" did not go wel for argument ${name0}", out);
  }
}
