package nl.naturalis.common.time;

@FunctionalInterface
public interface DateStringFilter {

  String validateOrTransform(String dateString) throws FuzzyDateException;

}
