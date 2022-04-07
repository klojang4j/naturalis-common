package nl.naturalis.common.path;

import nl.naturalis.common.ExceptionMethods;
import org.junit.Test;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nl.naturalis.common.CollectionMethods.newHashMap;
import static nl.naturalis.common.path.PathWalker.OnDeadEnd.*;
import static org.junit.Assert.*;

public class ListSegmentWriterTest {

  @Test
  public void test01() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertTrue(lsw.write(l, new Path("2"), 42));
    assertEquals(42, l.get(2));
  }

  @Test
  public void test02() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertTrue(lsw.write(l, new Path("path.to.list.3"), 42));
    assertEquals(42, l.get(3));
  }

  @Test
  public void test03() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    // index out of bounds
    assertFalse(lsw.write(l, new Path("8"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test04() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    assertFalse(lsw.write(l, new Path("path.to.list.8"), 42));
  }

  @Test
  public void test05() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    // not an array index
    assertFalse(lsw.write(l, new Path("path.to.list.foo"), 42));
  }

  @Test
  public void test06() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    // empty segment
    assertFalse(lsw.write(l, new Path("this.is.a.path."), 42));
  }

}
