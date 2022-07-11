package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.Param;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.UnaryOperator;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Math-related methods.
 *
 * @author Ayco Holleman
 */
public final class MathMethods {

  private static final Map<Class, UnaryOperator<? extends Number>> absFunctions = Map.of(
      Integer.class,
      n -> n.intValue() >= 0 ? n : Integer.valueOf(-n.intValue()),
      Double.class,
      n -> n.doubleValue() >= 0 ? n : Double.valueOf(-n.doubleValue()),
      Long.class,
      n -> n.longValue() >= 0 ? n : Long.valueOf(-n.longValue()),
      Float.class,
      n -> n.floatValue() >= 0 ? n : Float.valueOf(-n.floatValue()),
      Short.class,
      n -> n.shortValue() >= 0 ? n : Short.valueOf((short) -n.shortValue()),
      Byte.class,
      n -> n.byteValue() >= 0 ? n : Byte.valueOf((byte) -n.byteValue()),
      BigInteger.class,
      n -> ((BigInteger) n).abs(),
      BigDecimal.class,
      n -> ((BigDecimal) n).abs());

  private MathMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Converts to arguments to {@code double}, then divides the first argument by the
   * second, and then applies {@link Math#ceil(double) Math.ceil}. Since this is a
   * very low-level operation, no argument-checking is performed.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the next integer
   */
  public static int divUp(int value, int divideBy) {
    return (int) Math.ceil((double) value / (double) divideBy);
  }

  /**
   * Equivalent to {@code value / dividedBy}. Usable a method reference. Since this
   * is a very low-level operation, no argument-checking is performed.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded to the preceding integer
   */
  public static int divDown(int value, int divideBy) {
    return value / divideBy;
  }

  /**
   * Divides the specified value by the specified denominator, rounding up if the
   * remainder is exactly {@code 0.5} (given double-precision calculation). Since
   * this is a very low-level operation, no argument-checking is performed.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded up if the remainder is exactly
   *     {@code 0.5}
   */
  public static int divHalfUp(int value, int divideBy) {
    return (int) Math.floor(0.5D + (double) value / (double) divideBy);
  }

  /**
   * Divides the specified value by the specified denominator, rounding down if the
   * remainder is exactly {@code 0.5} (given double-precision calculation). Since
   * this is a very low-level operation, no argument-checking is performed.
   *
   * @param value The integer to divide
   * @param divideBy The integer to divide it by
   * @return The result of the division, rounded down if the remainder is exactly
   *     {@code 0.5}
   */
  public static int divHalfDown(int value, int divideBy) {
    return (int) Math.ceil(-0.5D + (double) value / (double) divideBy);
  }

  /**
   * Returns the number of pages needed to contain the specified number of items.
   *
   * @param itemCount The total number of items
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The number of pages needed to contain the specified number of items
   */
  public static int getPageCount(int itemCount, int rowCount, int colCount) {
    Check.that(itemCount, "itemCount").isNot(negative());
    checkRowCount(rowCount);
    checkColCount(colCount);
    return divUp(itemCount, rowCount * colCount);
  }

  /**
   * Returns the page number of an item.
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The page number of an item
   */
  public static int getPageIndex(int itemIndex, int rowCount, int colCount) {
    checkItemRowCol(itemIndex, rowCount, colCount);
    return itemIndex / (rowCount * colCount);
  }

  /**
   * Returns the row number of an item, modulo the page that it finds itself on.
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The row number of an item
   */
  public static int getRowIndex(int itemIndex, int rowCount, int colCount) {
    checkItemRowCol(itemIndex, rowCount, colCount);
    return (itemIndex / colCount) % rowCount;
  }

  /**
   * Returns the column number of an item.
   *
   * @param itemIndex The array index of the item
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The column number of an item
   */
  public static int getColumnIndex(int itemIndex, int colCount) {
    checkItemIndex(itemIndex);
    checkColCount(colCount);
    return itemIndex % colCount;
  }

  /**
   * Returns the row number of an item in a column-major layout. That is: items are
   * laid out top-to-bottom first, left-to-right second.
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @return The row number of an item in a column-major layout
   */
  public static int getRowIndexCM(int itemIndex, int rowCount) {
    checkItemIndex(itemIndex);
    checkRowCount(rowCount);
    return itemIndex % rowCount;
  }

  /**
   * Returns the column number of an item in a column-major layout
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The column number of an item in a column-major layout
   */
  public static int getColumnIndexCM(int itemIndex, int rowCount, int colCount) {
    checkItemRowCol(itemIndex, rowCount, colCount);
    return (itemIndex / rowCount) % colCount;
  }

  /**
   * Returns the page, row, and column number of an item in a row-major layout.
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The page, row, and column number of an item in a row-major layout
   */
  public static int[] getPageRowColumn(int itemIndex, int rowCount, int colCount) {
    checkItemRowCol(itemIndex, rowCount, colCount);
    return new int[] {itemIndex / (rowCount * colCount),
        (itemIndex / colCount) % rowCount,
        itemIndex % colCount};
  }

  /**
   * Returns the page, row, and column number of an item in a column-major layout.
   *
   * @param itemIndex The array index of the item
   * @param rowCount The number of rows per page (or table, or matrix, or
   *     raster)
   * @param colCount The number of columns per page (or table, or matrix, or
   *     raster)
   * @return The page, row, and column number of an item in a column-major layout
   */
  public static int[] getPageRowColumnCM(int itemIndex, int rowCount, int colCount) {
    checkItemRowCol(itemIndex, rowCount, colCount);
    return new int[] {itemIndex / (rowCount * colCount),
        itemIndex % rowCount,
        (itemIndex / rowCount) % colCount};
  }

  /**
   * Performs a row-major rasterization of a one-dimensional array of values. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with zeros.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterize(int[] values, int rowCount, int colCount) {
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
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
   * Performs a row-major rasterization of a one-dimensional array of values. In
   * other words, this method distributes a one-dimensional array of values across
   * zero or more two-dimensional arrays (a.k.a. tables, a.k.a. matrices). This has a
   * practical, commonplace application when generating or populating an HTML table
   * from a {@code List} or array of values. Each element of the returned
   * three-dimensional array corresponds to an HTML page, while each two-dimensional
   * array corresponds to the HTML table on that page.
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
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
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
   * Performs a row-major rasterization of a one-dimensional array of values. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with {@code null}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param <T> The type of the values to be rasterized
   * @return Zero or more rasters containing the rasterized values
   */
  public static <T> T[][][] rasterize(T[] values, int rowCount, int colCount) {
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = createEmptyPages(values, numPages, rowCount, colCount);
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
   * Performs a row-major rasterization of a one-dimensional array of values. See
   * {@link #rasterize(int[], int, int, int)}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param padValue The value to pad the empty remainder of the last raster
   *     with
   * @return Zero or more rasters containing the rasterized values
   */
  public static <T> T[][][] rasterize(T[] values,
      int rowCount,
      int colCount,
      T padValue) {
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = createEmptyPages(values, numPages, rowCount, colCount);
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
   * Performs a column-major rasterization of a one-dimensional array of values. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with zeros.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @return Zero or more rasters containing the rasterized values
   */
  public static int[][][] rasterizeCM(int[] values, int rowCount, int colCount) {
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
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
   * Performs a column-major rasterization of a one-dimensional array of values. See
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
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
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
   * Performs a column-major rasterization of a one-dimensional array of values. See
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
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = createEmptyPages(values, numPages, rowCount, colCount);
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
   * Performs a column-major rasterization of a one-dimensional array of values. See
   * {@link #rasterize(int[], int, int, int)}. The empty remainder of the last
   * two-dimensional array will be padded with {@code null}.
   *
   * @param values The values to rasterize
   * @param rowCount The number of rows per raster (or table, or matrix)
   * @param colCount The number of columns per raster (or table, or matrix)
   * @param <T> The type of the values to be rasterized
   * @return Zero or more rasters containing the rasterized values
   */
  public static <T> T[][][] rasterizeCM(T[] values, int rowCount, int colCount) {
    checkArray(values);
    checkRowCount(rowCount);
    checkColCount(colCount);
    int cellsPerPage = rowCount * colCount;
    int numPages = getPageCount(values.length, rowCount, colCount);
    T[][][] pages = createEmptyPages(values, numPages, rowCount, colCount);
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

  @SuppressWarnings("unchecked")
  private static <T> T[][][] createEmptyPages(T[] values,
      int numPages,
      int rowCount,
      int colCount) {
    return (T[][][]) Array.newInstance(values.getClass()
        .getComponentType(), numPages, rowCount, colCount);
  }

  private static void checkItemRowCol(int itemIndex, int rowCount, int colCount) {
    checkItemIndex(itemIndex);
    checkRowCount(rowCount);
    checkColCount(colCount);
  }

  private static void checkArray(Object array) {
    Check.notNull(array, Param.ARRAY);
  }

  private static void checkItemIndex(int itemIndex) {
    Check.that(itemIndex, "item index").isNot(negative());
  }

  private static void checkRowCount(int rowCount) {
    Check.that(rowCount, "row count").is(gt(), 0);
  }

  private static void checkColCount(int colCount) {
    Check.that(colCount, "column count").is(gt(), 0);
  }

  /**
   * Returns the absolute value of an arbitrary type of number.
   *
   * @param <T> the type of the number
   * @param number the number
   * @return Its absolute value
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T abs(T number) {
    UnaryOperator op = Check.notNull(number).ok(n -> absFunctions.get(n.getClass()));
    return (T) op.apply(number);
  }

  /**
   * Returns the zero-based number (a.k.a. index) of the last page, given the
   * specified row count and page size (rows per page).
   *
   * @param rowCount The total number of rows to divide up into pages
   * @param rowsPerPage The maximum number of rows per page
   * @return the zero-based (a.k.a. index) number of the last page
   */
  public static int getIndexOfLastPage(int rowCount, int rowsPerPage) {
    Check.that(rowCount, "rowCount").isNot(gte(), 0);
    Check.that(rowsPerPage, "rowsPerPage").is(gt(), 0);
    if (rowCount == 0) {
      return 0;
    }
    return ((rowCount - 1) / rowsPerPage);
  }

  /**
   * Returns the number of pages need to contain the specified number of rows, given
   * the specified number of rows per page.
   *
   * @param rowCount The total number of rows to divide up into pages
   * @param rowsPerPage The maximum number of rows per page
   * @return the total number of pages you need for the specified row count
   */
  public static int getPageCount(int rowCount, int rowsPerPage) {
    return getIndexOfLastPage(rowCount, rowsPerPage) + 1;
  }

  /**
   * Returns the number of populated rows on the last page, given the specified row
   * count and page size (rows per page). That's just {@code rowCount % pageSize}.
   *
   * @param rowCount The total number of rows to divide up into pages
   * @param rowsPerPage The maximum number of rows per page
   * @return the number of rows in the last page
   */
  public static int countRowsOnLastPage(int rowCount, int rowsPerPage) {
    Check.that(rowCount, "rowCount").is(gte(), 0);
    Check.that(rowsPerPage, "rowsPerPage").is(gt(), 0);
    return rowCount % rowsPerPage;
  }

  /**
   * Returns the number of trailing, empty rows in the last page.
   *
   * @param rowCount The total number of rows to divide up into pages
   * @param rowsPerPage The maximum number of rows per page
   * @return the number of unoccupied rows in the last page
   */
  public static int countEmptyRowsOnLastPage(int rowCount, int rowsPerPage) {
    return rowsPerPage - countRowsOnLastPage(rowCount, rowsPerPage);
  }

}
