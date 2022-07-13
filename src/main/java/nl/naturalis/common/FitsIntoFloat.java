package nl.naturalis.common;

class FitsIntoFloat {

  static boolean test(Number number) {
    if (number.getClass() == Float.class) {
      return Float.isFinite(number.floatValue());
    } else if (number.getClass() == Double.class) {
      double d = number.doubleValue();
      if (Double.isFinite(d)) {
        double min = Double.valueOf(Float.toString(Float.MIN_VALUE));
        double max = Double.valueOf(Float.toString(Float.MAX_VALUE));
        return d >= min && d <= max;
      }
      return false;
    }
    return true;
  }

}
