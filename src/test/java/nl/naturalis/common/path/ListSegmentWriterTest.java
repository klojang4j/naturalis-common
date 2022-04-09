package nl.naturalis.common.path;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.naturalis.common.path.PathWalker.OnDeadEnd.*;
import static org.junit.Assert.*;

public class ListSegmentWriterTest {

  @Test
  public void test01() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    lsw.write(l, new Path("2"), 42);
    assertEquals(42, l.get(2));
  }

  @Test
  public void test02() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    lsw.write(l, new Path("path.to.list.3"), 42);
    assertEquals(42, l.get(3));
  }

  @Test
  public void test03a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    // index out of bounds
    lsw.write(l, new Path("8"), 42);
  }

  @Test(expected = PathWalkerException.class)
  public void test03b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    // index out of bounds
    lsw.write(l, new Path("8"), 42);
  }

  @Test
  public void test04a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_CODE, null);
    lsw.write(l, new Path("path.to.list.8"), 42);
  }

  @Test(expected = PathWalkerException.class)
  public void test04b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    lsw.write(l, new Path("path.to.list.8"), 42);
  }

  @Test
  public void test05a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    // not an array index
    lsw.write(l, new Path("path.to.list.foo"), 42);
  }

  @Test(expected = PathWalkerException.class)
  public void test05b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    // not an array index
    lsw.write(l, new Path("path.to.list.foo"), 42);
  }

  @Test
  public void test06a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_CODE, null);
    // empty segment
    lsw.write(l, new Path("this.is.a.path."), 42);
  }

  //  @Test
  //  public void test06b() {
  //    List l = new ArrayList(List.of(1, 2, 3, 4));
  //    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
  //    // empty segment
  //    lsw.write(l, new Path("this.is.a.path."), 42);
  //  }

}
