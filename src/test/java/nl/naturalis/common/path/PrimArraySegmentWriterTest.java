package nl.naturalis.common.path;

import org.junit.Test;

import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalker.OnError.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PrimArraySegmentWriterTest {

  @Test
  public void test01a() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_NULL, null);
    assertNull(writer.write(array, new Path("2"), 42));
    assertEquals(42, array[2]);
  }

  @Test
  public void test01b() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(THROW_EXCEPTION, null);
    assertNull(writer.write(array, new Path("2"), 42));
    assertEquals(42, array[2]);
  }

  @Test
  public void test02() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_NULL, null);
    assertNull(writer.write(array, new Path("path.to.array.3"), 42));
    assertEquals(42, array[3]);
  }

  @Test
  public void test03a() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_NULL, null);
    assertNull(writer.write(array, new Path("8"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test03b() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(THROW_EXCEPTION, null);
    try {
      writer.write(array, new Path("8"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_OUT_OF_BOUNDS, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test04a() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_NULL, null);
    assertNull(writer.write(array, new Path("path.to.array.8"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test04b() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(THROW_EXCEPTION, null);
    try {
      writer.write(array, new Path("path.to.array.8"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_OUT_OF_BOUNDS, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test05a() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_NULL, null);
    assertNull(writer.write(array, new Path("path.to.array.foo"), 42));
  }

  @Test(expected = PathWalkerException.class)
  public void test05b() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(THROW_EXCEPTION, null);
    try {
      writer.write(array, new Path("path.to.array.foo"), 42);
    } catch (PathWalkerException e) {
      assertEquals(INDEX_EXPECTED, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test06a() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_CODE, null);
    try {
      writer.write(array, new Path("path.to.array."), 42);
    } catch (PathWalkerException e) {
      assertEquals(EMPTY_SEGMENT, e.getErrorCode());
      throw e;
    }
  }

  @Test
  public void test06b() throws Throwable {
    int[] array = new int[] {1, 2, 3, 4};
    PrimitiveArraySegmentWriter writer = new PrimitiveArraySegmentWriter(RETURN_CODE, null);
    assertEquals(EMPTY_SEGMENT, writer.write(array, new Path("path.to.array."), 42));
  }

}
