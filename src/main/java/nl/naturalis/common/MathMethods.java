package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.lang.reflect.Array;
import java.util.function.IntBinaryOperator;

import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.negative;

/**
 * Math-related methods.
 *
 * @author Ayco Holleman
 */
public class MathMethods {

  private MathMethods() {}

  /**
   * Converts to arguments to {@code double}, then divides the first argument by the
   * second, and then applies {@link Math#ceil(double) Math.ceil}.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the next integer
   * @implNote Since this is a very low-level operation, no argument-checking is
   *     done
   */
  public static int divUp(int value, int divideBy) {
    return (int) Math.ceil((double) value / (double) divideBy);
  }

  /**
   * Equivalent to {@code value / dividedBy}, but still useful as method reference
   * for an {@link IntBinaryOperator}.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the preceding integer
   * @implNote Since this is a very low-level operation, no argument-checking is
   *     done
   */
  public static int divDown(int value, int divideBy) {
    return value / divideBy;
  }

  /**
   * Divides the specified value by the specified denominator, rounding up if the
   * remainder is exactly {@code 0.5} (given double-precision calculation).
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded up if the remainder is exactly
   *     {@code 0.5}
   * @implNote Since this is a very low-level operation, no argument-checking is
   *     done
   */
  public static int divHalfUp(int value, int divideBy) {
    return (int) Math.floor(0.5D + (double) value / (double) divideBy);
  }

  /**
   * Divides the specified value by the specified denominator, rounding down if the
   * remainder is exactly {@code 0.5} (given double-precision calculation).
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded down if the remainder is exactly
   *     {@code 0.5}
   * @implNote Since this is a very low-level operation, no argument-checking is
   *     done
   */
  public static int divHalfDown(int value, int divideBy) {
    return (int) Math.ceil(-0.5D + (double) value / (double) divideBy);
  }

  /**
   * Returns the number of pages (or tables or matrices) needed to contain the
   * specified number of items, given a {@code rowCount x columnCount} raster.
   *
   * @param itemCount The total number of elements to layout across one or more
   *     matrices.
   * @param rowCount The number of rows
   * @param colCount The number of columns
   * @return
   */
  public static int getPageCount(int itemCount, int rowCount, int colCount) {
    return divUp(itemCount, rowCount * colCount);
  }

  /**
   * Returns the index of the matrix (or table or page) hosting the element with the
   * specified absolute index, given a {@code rowCount x columnCount} matrix. The
   * absolute index is the item's array index of the element if all elements were
   * coalesced into a single array.
   *
   * @param itemIndex
   * @param rowCount
   * @param colCount
   * @return
   */
  public static int getPageIndex(int itemIndex, int rowCount, int colCount) {
    Check.that(itemIndex, "itemIndex").isNot(negative());
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    return itemIndex / (rowCount * colCount);
  }

  /**
   * Returns the row index of the element with the specified absolute index, given a
   * {@code rowCount x columnCount} matrix. The absolute index is the element's array
   * index of the item if all elements were coalesced into a single array. The
   * returned index is relative to the top of the matrix containing the element
   *
   * @param itemIndex
   * @param rowCount
   * @param colCount
   * @return
   */
  public static int getRowIndex(int itemIndex, int rowCount, int colCount) {
    return (itemIndex / colCount) % rowCount;
  }

  /**
   * Returns the column index of the specified element, given a row-major layout of
   * any matrix with the specified number of columns.
   *
   * @param itemIndex
   * @param colCount
   * @return
   */
  public static int getColumnIndex(int itemIndex, int colCount) {
    return itemIndex % colCount;
  }

  /**
   * Returns the row index of the specified element, given a column-major layout of
   * any matrix with the specified number of rows.
   *
   * @param itemIndex
   * @param rowCount
   * @return
   */
  public static int getRowIndexCM(int itemIndex, int rowCount) {
    return itemIndex % rowCount;
  }

  /**
   * Returns the column offset from the left of the page (or matrix), given a
   * column-major layout of a {@code rowCount x columnCount} matrix.
   *
   * @param itemIndex
   * @param rowCount
   * @param colCount
   * @return
   */
  public static int getColumnIndexCM(int itemIndex, int rowCount, int colCount) {
    return (itemIndex / rowCount) % colCount;
  }

  public static int[] getPageRowColumn(int itemIndex, int rowCount, int colCount) {
    return new int[] {getPageIndex(itemIndex, rowCount, colCount),
        getRowIndex(itemIndex, rowCount, colCount),
        getColumnIndex(itemIndex, colCount)};
  }

  public static int[] getPageRowColumnCM(int itemIndex, int rowCount, int colCount) {
    return new int[] {getPageIndex(itemIndex, rowCount, colCount),
        getRowIndexCM(itemIndex, rowCount),
        getColumnIndexCM(itemIndex, rowCount, colCount)};
  }

  /**
   * Distributes a one-dimensional array of values across zero or more
   * two-dimensional arrays (a&#46;k&#46;a&#46; tables, a&#46;k&#46;a&#46; matrices).
   * See {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with zeros.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterize(int[] values, int rowCount, int colCount) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    if (values.length == 0) {
      return new int[0][0][0];
    }
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    int[][][] pages = new int[numPages][rowCount][colCount];
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * colCount;
        for (int col = 0; col < colCount; ++col) {
          int idx = rowOffset + col;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][col] = values[idx];
        }
      }
    }
    return pages;
  }

  /**
   * Distributes a one-dimensional array of values across zero or more
   * two-dimensional arrays (a&#46;k&#46;a&#46; tables, a&#46;k&#46;a&#46; matrices).
   * This has a practical, commonplace application when generating or populating an
   * HTML table from a {@code List} or array of values. Each two-dimensional array in
   * the returned three-dimensional array would correspond to an HTML page containing
   * a single HTML table, while the third dimension corresponds to the number of
   * pages required to display all values.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param padValue The value to pad the empty remainder of the last raster
   *     with
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterize(int[] values,
      int rowCount,
      int colCount,
      int padValue) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    int[][][] pages = new int[numPages][rowCount][colCount];
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * colCount;
        for (int col = 0; col < colCount; ++col) {
          int idx = rowOffset + col;
          if (idx < values.length) {
            pages[page][row][col] = values[idx];
          } else {
            pages[page][row][col] = padValue;
          }
        }
      }
    }
    return pages;
  }

  /**
   * Performs a row-major rasterization of the values in the specified array. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with {@code null}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param <T> The type of the values to be rasterized
   * @return Zero or more rasters containing the rasterized values
   */
  @SuppressWarnings("unchecked")
  public static <T> T[][][] rasterize(T[] values, int rowCount, int colCount) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = (T[][][]) Array.newInstance(values.getClass().getComponentType(),
        numPages,
        rowCount,
        colCount);
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * colCount;
        for (int col = 0; col < colCount; ++col) {
          int idx = rowOffset + col;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][col] = values[idx];
        }
      }
    }
    return pages;
  }

  public static <T> T[][][] rasterize(T[] values,
      int rowCount,
      int colCount,
      T padValue) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = (T[][][]) Array.newInstance(values.getClass().getComponentType(),
        numPages,
        rowCount,
        colCount);
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int row = 0; row < rowCount; ++row) {
        int rowOffset = pageOffset + row * colCount;
        for (int col = 0; col < colCount; ++col) {
          int idx = rowOffset + col;
          if (idx < values.length) {
            pages[page][row][col] = values[idx];
          } else {
            pages[page][row][col] = padValue;
          }
        }
      }
    }
    return pages;
  }

  /**
   * Performs a column-major rasterization of the values in the specified array. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with zeros.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterizeCM(int[] values, int rowCount, int colCount) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    int[][][] pages = new int[numPages][rowCount][colCount];
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int col = 0; col < colCount; ++col) {
        int columnOffset = pageOffset + col * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][col] = values[idx];
        }
      }
    }
    return pages;
  }

  /**
   * Performs a column-major rasterization of the values in the specified array. See
   * {@link #rasterize(int[], int, int, int)}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param padValue The value to pad the empty remainder of the last raster
   *     with
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterizeCM(int[] values,
      int rowCount,
      int colCount,
      int padValue) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    int[][][] pages = new int[numPages][rowCount][colCount];
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int col = 0; col < colCount; ++col) {
        int columnOffset = pageOffset + col * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx < values.length) {
            pages[page][row][col] = values[idx];
          } else {
            pages[page][row][col] = padValue;
          }
        }
      }
    }
    return pages;
  }

  /**
   * Performs a column-major rasterization of the values in the specified array. See
   * {@link #rasterize(int[], int, int, int)}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param padValue The value to pad the empty remainder of the last raster
   *     with
   * @return Zero or more rasters containing the rasterized values
   */
  public static <T> T[][][] rasterizeCM(T[] values,
      int rowCount,
      int colCount,
      T padValue) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = (T[][][]) Array.newInstance(values.getClass().getComponentType(),
        numPages,
        rowCount,
        colCount);
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int col = 0; col < colCount; ++col) {
        int columnOffset = pageOffset + col * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx < values.length) {
            pages[page][row][col] = values[idx];
          } else {
            pages[page][row][col] = padValue;
          }
        }
      }
    }
    return pages;
  }

  /**
   * Performs a column-major rasterization of the values in the specified array. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with {@code null}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param <T> The type of the values to be rasterized
   * @return Zero or more rasters containing the rasterized values
   */
  @SuppressWarnings("unchecked")
  public static <T> T[][][] rasterizeCM(T[] values, int rowCount, int colCount) {
    Check.notNull(values, "array");
    Check.that(rowCount, "rowCount").is(gt(), 0);
    Check.that(colCount, "colCount").is(gt(), 0);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = (T[][][]) Array.newInstance(values[0].getClass(),
        numPages,
        rowCount,
        colCount);
    MAIN_LOOP:
    for (int page = 0; page < numPages; ++page) {
      int pageOffset = page * cellsPerPage;
      for (int col = 0; col < colCount; ++col) {
        int columnOffset = pageOffset + col * rowCount;
        for (int row = 0; row < rowCount; ++row) {
          int idx = columnOffset + row;
          if (idx >= values.length) {
            break MAIN_LOOP;
          }
          pages[page][row][col] = values[idx];
        }
      }
    }
    return pages;
  }

}
