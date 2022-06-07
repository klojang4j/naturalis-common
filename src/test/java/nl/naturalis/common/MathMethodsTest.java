package nl.naturalis.common;

import static nl.naturalis.common.ArrayMethods.ints;
import static nl.naturalis.common.MathMethods.rasterize;
import static nl.naturalis.common.MathMethods.rasterizeCM;
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

  @Test // 2 x 2 matrix
  public void rasterizeInts00() {
    int[] values = ints();
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(0, pages.length);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts01() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, -1), pages[0][0]);
    assertArrayEquals(ints(-1, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts02() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(-1, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts03() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts04() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts05() {
    int[] values = ints(0, 1, 2, 3, 4);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, -1), pages[1][0]);
    assertArrayEquals(ints(-1, -1), pages[1][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts06() {
    int[] values = ints(0, 1, 2, 3, 4, 5);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, 5), pages[1][0]);
    assertArrayEquals(ints(-1, -1), pages[1][1]);
  }

  @Test // 2 x 2 matrix
  public void rasterizeInts07() {
    int[] values = ints(0, 1, 2, 3, 4, 5, 6);
    int[][][] pages = rasterize(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 1), pages[0][0]);
    assertArrayEquals(ints(2, 3), pages[0][1]);
    assertArrayEquals(ints(4, 5), pages[1][0]);
    assertArrayEquals(ints(6, -1), pages[1][1]);
  }

  @Test // 1 x 3 matrix
  public void rasterizeInts08() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, -1, -1), pages[0][0]);
  }

  @Test // 1 x 3 matrix
  public void rasterizeInts09() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, -1), pages[0][0]);
  }

  @Test // 1 x 3 matrix
  public void rasterizeInts10() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
  }

  @Test // 1 x 3 matrix
  public void rasterizeInts11() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
    assertArrayEquals(ints(3, -1, -1), pages[1][0]);
  }

  @Test // 1 x 3 matrix
  public void rasterizeInts12() {
    int[] values = ints(0, 1, 2, 3, 4);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
    assertArrayEquals(ints(3, 4, -1), pages[1][0]);
  }

  @Test // 3 x 1 matrix
  public void rasterizeInts13() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
  }

  @Test // 3 x 1 matrix
  public void rasterizeInts14() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
  }

  @Test // 3 x 1 matrix
  public void rasterizeInts15() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
    assertArrayEquals(ints(2), pages[0][2]);
  }

  @Test // 3 x 1 matrix
  public void rasterizeInts16() {
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

  @Test // 1 x 1 matrix
  public void rasterizeInts17() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 1, 1, -1);
    assertEquals(4, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[1][0]);
    assertArrayEquals(ints(2), pages[2][0]);
    assertArrayEquals(ints(3), pages[3][0]);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM00() {
    int[] values = ints();
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(0, pages.length);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM01() {
    int[] values = ints(0);
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, -1), pages[0][0]);
    assertArrayEquals(ints(-1, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM02() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, -1), pages[0][0]);
    assertArrayEquals(ints(1, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM03() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 2), pages[0][0]);
    assertArrayEquals(ints(1, -1), pages[0][1]);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM04() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(1, pages.length);
    assertArrayEquals(ints(0, 2), pages[0][0]);
    assertArrayEquals(ints(1, 3), pages[0][1]);
  }

  @Test // 2 x 2 matrix CM
  public void rasterizeIntsCM05() {
    int[] values = ints(0, 1, 2, 3, 4);
    int[][][] pages = rasterizeCM(values, 2, 2, -1);
    assertEquals(2, pages.length);
    assertArrayEquals(ints(0, 2), pages[0][0]);
    assertArrayEquals(ints(1, 3), pages[0][1]);
    assertArrayEquals(ints(4, -1), pages[1][0]);
    assertArrayEquals(ints(-1, -1), pages[1][1]);
  }

  @Test // 3 x 1 matrix CM
  public void rasterizeIntsCM06() {
    int[] values = ints(0);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
  }

  @Test // 3 x 1 matrix CM
  public void rasterizeIntsCM07() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
  }

  @Test // 3 x 1 matrix CM
  public void rasterizeIntsCM08() {
    int[] values = ints(0, 1, 2);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
    assertArrayEquals(ints(2), pages[0][2]);
  }

  @Test // 3 x 1 matrix CM
  public void rasterizeIntsCM09() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 3, 1, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0), pages[0][0]);
    assertArrayEquals(ints(1), pages[0][1]);
    assertArrayEquals(ints(2), pages[0][2]);
    assertArrayEquals(ints(3), pages[1][0]);
  }

  @Test // 1 x 3 matrix CM
  public void rasterizeIntsCM10() {
    int[] values = ints(0, 1);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(1, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    assertArrayEquals(ints(0, 1, -1), pages[0][0]);
  }

  @Test // 1 x 3 matrix CM
  public void rasterizeIntsCM11() {
    int[] values = ints(0, 1, 2, 3);
    int[][][] pages = rasterize(values, 1, 3, -1);
    assertEquals(2, pages.length);
    //System.out.println(IntList.of(pages[0][0]));
    //System.out.println(IntList.of(pages[1][0]));
    assertArrayEquals(ints(0, 1, 2), pages[0][0]);
    assertArrayEquals(ints(3, -1, -1), pages[1][0]);
  }

}
