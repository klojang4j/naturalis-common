package nl.naturalis.common.util;

public class MutableDouble {

  private double d;

  public MutableDouble() {}

  public MutableDouble(double d) {
    this.d = d;
  }

  public double get() {
    return d;
  }

  public void set(double d) {
    this.d = d;
  }
}
