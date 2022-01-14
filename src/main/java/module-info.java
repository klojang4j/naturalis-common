/**
 * Java module containing basic language extensions and utility classes. The module is
 * self-contained, having zero dependencies outside the {@code java.} namespace.
 *
 * @author Ayco Holleman
 */
module nl.naturalis.common {
  exports nl.naturalis.common;
  exports nl.naturalis.common.check;
  exports nl.naturalis.common.collection;
  exports nl.naturalis.common.exception;
  exports nl.naturalis.common.function;
  exports nl.naturalis.common.invoke;
  exports nl.naturalis.common.io;
  exports nl.naturalis.common.path;
  exports nl.naturalis.common.time;
  exports nl.naturalis.common.unsafe;
  exports nl.naturalis.common.util;
  exports nl.naturalis.common.x.invoke;

  requires java.xml;
}
