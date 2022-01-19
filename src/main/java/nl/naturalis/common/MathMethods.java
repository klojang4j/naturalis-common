package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.lang.reflect.Array;
import java.util.function.IntBinaryOperator;

import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.negative;

/** @author Ayco Holleman */
public class MathMethods {

  private MathMethods() {}

  /**
   * Converts to arguments to {@code double}, then divides the first argument by the second, and
   * then applies {@link Math#ceil(double) Math.ceil}.
   *
   * @param value The initeger to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the next integer
   * @implNote Since this is a very low-level operation, no argument-checking is done
   */
  public static int divUp(int value, int divideBy) {
    return (int) Math.ceil((double) value / (double) divideBy);
  }

  /**
   * Equivalent to {@code value / dividedBy}, but still useful as method reference for an {@link
   * IntBinaryOperator}.
   *
   * @param value The initeger to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the preceding integer
   * @implNote Since this is a very low-level operation, no argument-checking is done
   */
  public static int divDown(int value, int divideBy) {
    return value / divideBy;
  }

  /**
   * Divides the specified value by the specified denominator, rounding up if the remainder is
   * exactly {@code 0.5} (given double-precision calculation).
   *
   * @param value
   * @param divideBy
   * @return
   * @implNote Since this is a very low-level operation, no argument-checking is done
   */
  public static int divHalfUp(int value, int divideBy) {
    return (int) Math.floor(0.5D + (double) value / (double) divideBy);
  }

  /**
   * Divides the specified value by the specified denominator, rounding down if the remainder is
   * exactly {@code 0.5} (given double-precision calculation).
   *
   * @param value
   * @param divideBy
   * @return
   * @implNote Since this is a very low-level operation, no argument-checking is done
   */
  public static int divHalfDown(int value, int divideBy) {
    return (int) Math.ceil(-0.5D + (double) value / (double) divideBy);
  }

  /**
   * Returns the number of matrices (or tables or pages) needed to contain the specified number of
   * items, given a {@code rowCount x columnCount} matrix.
   *
   * @param itemCount The total number of elements to layout across one or more matrices.
   * @param rowCount The number of rows
   * @param columnCount The number of columns
   * @return
   */
  public static int getPageCount(int itemCount, int rowCount, int columnCount) {
    return divUp(itemCount, rowCount * columnCount);
  }

  /**
   * Returns the index of the matrix (or table or page) hosting the element with the specified
   * absolute index, given a {@code rowCount x columnCount} matrix. The absolute index is the item's
   * array index of the element if all elements were coalesced into a single array.
   *
   * @param itemIndex
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int getPageIndex(int itemIndex, int rowCount, int columnCount) {
    Check.that(itemIndex, "itemIndex").isNot(negative());
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(columnCount, "columnCount").is(gt(), 0);
    return itemIndex / (rowCount * columnCount);
  }

  /**
   * Returns the row index of the element with the specified absolute index, given a {@code rowCount
   * x columnCount} matrix. The absolute index is the element's array index of the item if all
   * elements were coalesced into a single array. The returned index is relative to the top of the
   * matrix containing the element
   *
   * @param itemIndex
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int getRowIndex(int itemIndex, int rowCount, int columnCount) {
    return (itemIndex / columnCount) % rowCount;
  }

  /**
   * Returns the column index of the specified element, given a row-major layout of any matrix with
   * the specified number of columns.
   *
   * @param itemIndex
   * @param columnCount
   * @return
   */
  public static int getColumnIndex(int itemIndex, int columnCount) {
    return itemIndex % columnCount;
  }

  /**
   * Returns the row index of the specified element, given a column-major layout of any matrix with
   * the specified number of rows.
   *
   * @param itemIndex
   * @param rowCount
   * @return
   */
  public static int getRowIndexCM(int itemIndex, int rowCount) {
    return itemIndex % rowCount;
  }

  /**
   * Returns the column offset from the left of the page (or matrix), given a column-major layout of
   * a {@code rowCount x columnCount} matrix.
   *
   * @param itemIndex
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int getColumnIndexCM(int itemIndex, int rowCount, int columnCount) {
    return (itemIndex / rowCount) % columnCount;
  }

  public static int[] getPageRowColumn(int itemIndex, int rowCount, int columnCount) {
    return new int[] {
      getPageIndex(itemIndex, rowCount, columnCount),
      getRowIndex(itemIndex, rowCount, columnCount),
      getColumnIndex(itemIndex, columnCount)
    };
  }

  public static int[] getPageRowColumnCM(int itemIndex, int rowCount, int columnCount) {
    return new int[] {
      getPageIndex(itemIndex, rowCount, columnCount),
      getRowIndexCM(itemIndex, rowCount),
      getColumnIndexCM(itemIndex, rowCount, columnCount)
    };
  }

  /**
   * Lays out the elements in the specified array across zero or more @code rowCount x columnCount}
   * matrices, using 0 (zero) to pad out the unused cells of the last matrix.
   *
   * @param values
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int[][][] rasterize(int[] values, int rowCount, int columnCount) {
    if (values.length == 0) {
      return new int[0][0][0];
    }
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    int[][][] pages = new int[numPages][rowCount][columnCount];
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * columnCount;
        for (int column = 0; column < columnCount; ++column) {
          int idx = rowOffset + column;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][column] = values[idx];
        }
      }
    }
    return pages;
  }

  public static int[][][] rasterize(int[] values, int rowCount, int columnCount, int nullValue) {
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    int[][][] pages = new int[numPages][rowCount][columnCount];
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * columnCount;
        for (int column = 0; column < columnCount; ++column) {
          int idx = rowOffset + column;
          if (idx < values.length) {
            pages[page][row][column] = values[idx];
          } else {
            pages[page][row][column] = nullValue;
          }
        }
      }
    }
    return pages;
  }

  public static int[][][] rasterizeCM(int[] values, int rowCount, int columnCount) {
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    int[][][] pages = new int[numPages][rowCount][columnCount];
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int column = 0; column < columnCount; ++column) {
        int columnOffset = pageOffset + column * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][column] = values[idx];
        }
      }
    }
    return pages;
  }

  public static int[][][] rasterizeCM(int[] values, int rowCount, int columnCount, int nullValue) {
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    int[][][] pages = new int[numPages][rowCount][columnCount];
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int column = 0; column < columnCount; ++column) {
        int columnOffset = pageOffset + column * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx < values.length) {
            pages[page][row][column] = values[idx];
          } else {
            pages[page][row][column] = nullValue;
          }
        }
      }
    }
    return pages;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][] rasterize(T[] values, int rowCount, int columnCount) {
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    T[][][] pages =
        (T[][][]) Array.newInstance(values[0].getClass(), numPages, rowCount, columnCount);
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * columnCount;
        for (int column = 0; column < columnCount; ++column) {
          int idx = rowOffset + column;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][column] = values[idx];
        }
      }
    }
    return pages;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][] rasterizeCM(T[] values, int rowCount, int columnCount) {
    int cellsPerPage = rowCount * columnCount;
    int numPages = getPageCount(values.length, rowCount, columnCount);
    T[][][] pages =
        (T[][][]) Array.newInstance(values[0].getClass(), numPages, rowCount, columnCount);
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int column = 0; column < columnCount; ++column) {
        int columnOffset = pageOffset + column * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][column] = values[idx];
        }
      }
    }
    return pages;
  }
}
