package nl.naturalis.common.check;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Array;
import java.util.function.Function;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class CommonGettersTest {

  /*
   *  Ensure we always get back the same reference when using CommonGetters
   */
  @Test
  public void test01() {

    assertSame(CommonGetters.length(), CommonGetters.length());

    // Object obj0 = Array::getLength; *** does not compile! Huh? ***
    // Object obj1 = Array::getLength;
    Function obj0 = Array::getLength;
    Function obj1 = Array::getLength;
    assertNotSame(obj0, obj1);
  }
}
