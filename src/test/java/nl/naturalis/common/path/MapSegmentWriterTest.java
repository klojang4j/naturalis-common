package nl.naturalis.common.path;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.path.ErrorCode.EMPTY_SEGMENT;
import static nl.naturalis.common.path.ErrorCode.EXCEPTION;
import static nl.naturalis.common.path.PathWalker.OnError.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapSegmentWriterTest {

  Function<Path, Object> kds = p -> Integer.valueOf(p.segment(-1));

  @Test
  public void test01a() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_NULL, kds);
    assertNull(writer.write(m, new Path("2"), "MARK"));
    assertEquals("MARK", m.get(2));
  }

  @Test
  public void test01b() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(THROW_EXCEPTION, kds);
    assertNull(writer.write(m, new Path("2"), 42));
    assertEquals(42, m.get(2));
  }

  @Test
  public void test02() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_NULL, kds);
    assertNull(writer.write(m, new Path("path.to.map.3"), 42));
    assertEquals(42, m.get(3));
  }

  @Test
  public void test03a() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_NULL, kds);
    assertNull(writer.write(m, new Path("8"), 42));
  }

  @Test
  public void test03b() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(THROW_EXCEPTION, kds);
    writer.write(m, new Path("8"), 42);
    assertEquals(42, m.get(8));
  }

  @Test
  public void test04a() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_NULL, kds);
    assertNull(writer.write(m, new Path("path.to.map.8"), 42));
  }

  @Test
  public void test04b() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(THROW_EXCEPTION, kds);
    writer.write(m, new Path("path.to.map.8"), 42);
    assertEquals(42, m.get(8));
  }

  @Test
  public void test05a() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_NULL, kds);
    // Cannot deserialize "foo" into integer
    assertNull(writer.write(m, new Path("path.to.map.foo"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test05b() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(THROW_EXCEPTION, kds);
    try {
      writer.write(m, new Path("path.to.map.foo"), 42);
    } catch (PathWalkerException e) {
      System.out.println(e.getMessage());
      // Cannot deserialize "foo" into integer
      assertEquals(EXCEPTION, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test06a() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(RETURN_CODE, kds);
    // Cannot deserialize empty string into integer
    assertEquals(EXCEPTION, writer.write(m, new Path("path.to.map."), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test06b() {
    Map m = new HashMap(Map.of(1, "john", 2, "mark", 3, "tom", 4, "jim"));
    MapSegmentWriter writer = new MapSegmentWriter(THROW_EXCEPTION, kds);
    try {
      // Cannot deserialize empty string into integer
      writer.write(m, new Path("path.to.map."), 42);
    } catch (PathWalkerException e) {
      System.out.println(e.getMessage());
      // Cannot deserialize "foo" into integer
      assertEquals(EXCEPTION, e.getErrorCode());
      throw e;
    }
  }

}
