package nl.naturalis.common;

import static nl.naturalis.common.ArrayMethods.ints;
import static nl.naturalis.common.MathMethods.rasterize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl.naturalis.common.collection.ArraySet;
import nl.naturalis.common.collection.IntList;
import org.junit.Test;

@SuppressWarnings("unused")
public class MathMethodsTest {

  @Test
  public void divHalfUp00() {
    int x = 2;
    int y = 6;
    assertEquals(0, MathMethods.divHalfUp(x, y));
  }

  @Test
  public void divHalfUp01() {
    int x = 8;
    int y = 6;
    assertEquals(1, MathMethods.divHalfUp(x, y));
  }

  @Test
  public void divHalfUp02() {
    int x = 9;
    int y = 6;
    assertEquals(2, MathMethods.divHalfUp(x, y));
  }

  @Test
  public void divHalfUp03() {
    int x = 10;
    int y = 6;
    assertEquals(2, MathMethods.divHalfUp(x, y));
  }

  @Test
  public void divHalfDown00() {
    int x = 2;
    int y = 6;
    assertEquals(0, MathMethods.divHalfDown(x, y));
  }

  @Test
  public void divHalfDown01() {
    int x = 8;
    int y = 6;
    assertEquals(1, MathMethods.divHalfDown(x, y));
  }

  @Test
  public void divHalfDown02() {
    int x = 9;
    int y = 6;
    assertEquals(1, MathMethods.divHalfDown(x, y));
  }

  @Test
  public void divHalfDown03() {
    int x = 10;
    int y = 6;
    assertEquals(2, MathMethods.divHalfDown(x, y));
  }

  @Test
  public void getPageCount00() {
    int itemCount = 12;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(1, MathMethods.getPageCount(itemCount, rowCount, columnCount));
  }

  @Test
  public void getPageCount01() {
    int itemCount = 20;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(1, MathMethods.getPageCount(itemCount, rowCount, columnCount));
  }

  @Test
  public void getPageCount02() {
    int itemCount = 21;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(2, MathMethods.getPageCount(itemCount, rowCount, columnCount));
  }

  @Test
  public void getPageIndex00() {
    int itemCount = 12;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(0, MathMethods.getPageIndex(itemCount, rowCount, columnCount));
  }

  @Test
  public void getRowIndex00() {
    int itemIndex = 0;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(0, MathMethods.getRowIndex(itemIndex, rowCount, columnCount));
  }

  @Test
  public void getRowIndex01() {
    int itemIndex = 12;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(3, MathMethods.getRowIndex(itemIndex, rowCount, columnCount));
  }

  @Test
  public void getRowIndex02() {
    int itemIndex = 72;
    int rowCount = 5;
    int columnCount = 4;
    assertEquals(3, MathMethods.getRowIndex(itemIndex, rowCount, columnCount));
  }

  @Test
  public void rasterize00() {
    int[] values = ints();
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(0, pages.length);
  }

  @Test
  public void rasterize01() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, -1), pages[0][0]);
    assertArrayEquals(ints(-1, -1), pages[0][1]);
  }

  @Test
  public void rasterize02() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(-1, -1), pages[0][1]);
  }

  @Test
  public void rasterize03() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, -1), pages[0][1]);
  }

  @Test
  public void rasterize04() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
  }

  @Test
  public void rasterize05() {
    int[] values = ints(0, 1, 2, 3, 4);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, -1), pages[1][0]);
    assertArrayEquals(ints(-1, -1), pages[1][1]);
  }

  @Test
  public void rasterize06() {
    int[] values = ints(0, 1, 2, 3, 4, 5);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, 5), pages[1][0]);
    assertArrayEquals(ints(-1, -1), pages[1][1]);
  }

  @Test
  public void rasterize07() {
    int[] values = ints(0, 1, 2, 3, 4, 5, 6);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, 5), pages[1][0]);
    assertArrayEquals(ints(6, -1), pages[1][1]);
  }

  @Test
  public void rasterize08() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, -1, -1), pages[0][0]);
  }

  @Test
  public void rasterize09() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, -1), pages[0][0]);
  }

  @Test
  public void rasterize10() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
  }

  @Test
  public void rasterize11() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
    assertArrayEquals(ints(3, -1, -1), pages[1][0]);
  }

  @Test
  public void rasterize12() {
    int[] values = ints(0, 1, 2, 3, 4);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
    assertArrayEquals(ints(3, 4, -1), pages[1][0]);
  }

  @Test
  public void rasterize13() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
  }

  @Test
  public void rasterize14() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
  }

  @Test
  public void rasterize15() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
    assertArrayEquals(ints(2), pages[0][2]);
  }

  @Test
  public void rasterize16() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
    assertArrayEquals(ints(2), pages[0][2]);
    assertArrayEquals(ints(3), pages[1][0]);
    assertArrayEquals(ints(-1), pages[1][1]);
    assertArrayEquals(ints(-1), pages[1][2]);
  }

}
