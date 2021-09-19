package nl.naturalis.common;

import java.lang.reflect.Array;

public class MathMethods {

  private MathMethods() {}

  public static int divUp(int value, int dividedBy) {
    return (int) Math.ceil((double) value / (double) dividedBy);
  }

  public static int divDown(int value, int dividedBy) {
    return value / dividedBy;
  }

  public static int divHalfUp(int value, int dividedBy) {
    return (int) Math.floor(0.5D + (double) value / (double) dividedBy);
  }

  public static int divHalfDown(int value, int dividedBy) {
    return (int) Math.ceil(-0.5D + (double) value / (double) dividedBy);
  }

  /**
   * Returns the number of pages (or pages) needed to contain the specified number of items (or
   * cells), given a {@code rowCount x columnCount} matrix.
   *
   * @param itemCount
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int getPageCount(int itemCount, int rowCount, int columnCount) {
    return 1 + getPageIndex(itemCount, rowCount, columnCount);
  }

  /**
   * Return the index of the page (or matrix) hosting the specified item, given a {@code rowCount x
   * columnCount} matrix.
   *
   * @param itemIndex
   * @param rowCount
   * @param columnCount
   * @return
   */
  public static int getPageIndex(int itemIndex, int rowCount, int columnCount) {
    return itemIndex / (rowCount * columnCount);
  }

  /**
   * Returns the row offset from the top of the page (or matrix), given a {@code rowCount x
   * columnCount} matrix.
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
   * Returns the column index of the specified item, given a row-major layout of any matrix with the
   * specified number of columns.
   *
   * @param itemIndex
   * @param columnCount
   * @return
   */
  public static int getColumnIndex(int itemIndex, int columnCount) {
    return itemIndex % columnCount;
  }

  /**
   * Returns the row index of the specified item, given a column-major layout of any matrix with the
   * specified number of rows.
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

  public static int[][][] rasterize(int[] values, int rowCount, int columnCount) {
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
