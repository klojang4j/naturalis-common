package nl.naturalis.common;

class FitsIntoDouble {

  static boolean test(Number number) {
    if (number instanceof Double d) {
      return Double.isFinite(d);
    } else if (number instanceof Float f) {
      return Float.isFinite(f);
    }
    return true;
  }

}
