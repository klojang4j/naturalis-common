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

  public void plusplus() {
    ++i;
  }

  public void minmin() {
    --i;
  }

  public void plusIs(int j) {
    i += j;
  }

  public void minIs(int j) {
    i -= j;
  }

  public void set(int j) {
    i = j;
  }

  public void reset() {
    i = 0;
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
