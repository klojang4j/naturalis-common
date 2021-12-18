package nl.naturalis.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}