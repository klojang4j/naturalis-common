package nl.naturalis.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MutableIntTest {

  @Test
  public void test00() {
    MutableInt i = new MutableInt();
    int j = i.ipp();
    assertEquals(0, j);
    assertEquals(1, i.get());
  }

  @Test
  public void test01() {
    MutableInt i = new MutableInt(7);
    int j = i.ppi();
    assertEquals(8, j);
    assertEquals(8, i.get());
  }
}
