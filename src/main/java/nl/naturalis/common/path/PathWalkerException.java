package nl.naturalis.common.path;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.invoke.NoSuchPropertyException;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;

/**
 * Runtime exception thrown while the {@link PathWalker} is walking a path through an object.
 *
 * @author Ayco Holleman
 */
public class PathWalkerException extends RuntimeException {

  private PathWalkerException(String message) {
    super(message);
  }

  private PathWalkerException(Throwable cause) {
    super(cause);
  }

  private PathWalkerException(String message, Throwable cause) {
    super(message, cause);
  }

}
