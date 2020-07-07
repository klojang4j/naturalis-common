package nl.naturalis.common.time;

/**
 * Thrown if an errors occurs while parsing date strings.
 *
 * @author Ayco Holleman
 */
public class FuzzyDateException extends Exception {

  public FuzzyDateException(String message) {
    super(message);
  }

  public FuzzyDateException(Throwable cause) {
    super(cause);
  }

  public FuzzyDateException(String message, Throwable cause) {
    super(message, cause);
  }

}
