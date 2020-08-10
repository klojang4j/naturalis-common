package nl.naturalis.common.internal;

public class StringCheck extends ObjectCheck<String> {

  public StringCheck(String arg, String argName) {
    super(arg, argName);
  }

  @Override
  public StringCheck notBlank() {
    notBlank(arg, argName);
    return this;
  }
}
