package nl.naturalis.common.util;

public final class MutableInt {

  private int i;

  public MutableInt() {}

  public MutableInt(int value) {
    i = value;
  }

  public MutableInt(MutableInt other) {
    i = other.i;
  }

  public int get() {
    return i;
  }

  public int pp() {
    return i++;
  }

  public int ppi() {
    return ++i;
  }

  public int mm() {
    return i--;
  }

  public int mmi() {
    return --i;
  }

  public int plusIs(int j) {
    return i += j;
  }

  public int plusIs(MutableInt other) {
    return i += other.i;
  }

  public int minIs(int j) {
    return i -= j;
  }

  public int minIs(MutableInt other) {
    return i -= other.i;
  }

  public int set(int j) {
    return i = j;
  }

  public boolean eq(int j) {
    return i == j;
  }

  public boolean eq(MutableInt other) {
    return i == other.i;
  }

  public boolean ne(int j) {
    return i != j;
  }

  public boolean ne(MutableInt other) {
    return i != other.i;
  }

  public boolean gt(int j) {
    return i > j;
  }

  public boolean gt(MutableInt other) {
    return i > other.i;
  }

  public boolean lt(int j) {
    return i < j;
  }

  public boolean lt(MutableInt other) {
    return i < other.i;
  }

  public boolean gte(int j) {
    return i >= j;
  }

  public boolean gte(MutableInt other) {
    return i >= other.i;
  }

  public boolean lte(int j) {
    return i <= j;
  }

  public boolean lte(MutableInt other) {
    return i <= other.i;
  }

  public int reset() {
    return i = 0;
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
