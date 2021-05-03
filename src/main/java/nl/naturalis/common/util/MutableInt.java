package nl.naturalis.common.util;

public class MutableInt {

  private int i;

  public MutableInt() {}

  public MutableInt(int value) {
    i = value;
  }

  public int get() {
    return i;
  }

  public int ipp() {
    return i++;
  }

  public int ppi() {
    return ++i;
  }

  public int mmi() {
    return i--;
  }

  public int imm() {
    return --i;
  }

  public int plusIs(int j) {
    return i += j;
  }

  public int minIs(int j) {
    return i -= j;
  }

  public int set(int j) {
    return i = j;
  }

  public int reset() {
    return i = 0;
  }

  public boolean eq(int j) {
    return i == j;
  }

  public boolean ne(int j) {
    return i != j;
  }

  public boolean gt(int j) {
    return i > j;
  }

  public boolean lt(int j) {
    return i < j;
  }

  public boolean gte(int j) {
    return i >= j;
  }

  public boolean lte(int j) {
    return i <= j;
  }

  @Override
  public int hashCode() {
    return i;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (obj.getClass() == Integer.class) {
      return i == ((Integer) obj);
    }
    if (getClass() != obj.getClass()) return false;
    MutableInt other = (MutableInt) obj;
    return i == other.i;
  }

  @Override
  public String toString() {
    return String.valueOf(i);
  }
}
