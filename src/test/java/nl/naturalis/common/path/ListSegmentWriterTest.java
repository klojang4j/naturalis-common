package nl.naturalis.common.path;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalker.OnError.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ListSegmentWriterTest {

  @Test
  public void test01a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertNull(lsw.write(l, new Path("2"), 42));
    assertEquals(42, l.get(2));
  }

  @Test
  public void test01b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    assertNull(lsw.write(l, new Path("2"), 42));
    assertEquals(42, l.get(2));
  }

  @Test
  public void test02() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertNull(lsw.write(l, new Path("path.to.list.3"), 42));
    assertEquals(42, l.get(3));
  }

  @Test
  public void test03a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertNull(lsw.write(l, new Path("8"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test03b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    try {
      lsw.write(l, new Path("8"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_OUT_OF_BOUNDS, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test04a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertNull(lsw.write(l, new Path("path.to.list.8"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test04b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    try {
      lsw.write(l, new Path("path.to.list.8"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_OUT_OF_BOUNDS, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test05a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_NULL, null);
    assertNull(lsw.write(l, new Path("path.to.list.foo"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test05b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(THROW_EXCEPTION, null);
    try {
      lsw.write(l, new Path("path.to.list.foo"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_EXPECTED, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test06a() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_CODE, null);
    try {
      lsw.write(l, new Path("path.to.list."), 42);
    } catch (PathWalkerException e) {
      assertEquals(EMPTY_SEGMENT, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test06b() {
    List l = new ArrayList(List.of(1, 2, 3, 4));
    ListSegmentWriter lsw = new ListSegmentWriter(RETURN_CODE, null);
    assertEquals(EMPTY_SEGMENT, lsw.write(l, new Path("path.to.list."), 42));
  }

}
