package nl.naturalis.common.collection;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class WiredIteratorTest {

  @Test
  public void test01() {
    var wl0 = WiredList.of(0, 1, 2, 3);
    var itr = wl0.wiredIterator();
    itr.next();
    itr.remove(); // 0
    itr.next();
    itr.remove(); // 1
    itr.next();
    itr.remove(); // 2
    itr.next();
    itr.remove(); // 3
    assertEquals(0, wl0.size());
  }

  @Test
  public void test02() {
    var wl0 = WiredList.of(0);
    var itr = wl0.wiredIterator();
    itr.next();
    itr.remove();
    assertEquals(0, wl0.size());
  }

}
