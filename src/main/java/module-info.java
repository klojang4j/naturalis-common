/**
 * Java module containing basic language extensions and utility classes. The
 * module is designed to be self-contained and have a small footprint. It has
 * zero dependencies outside the {@code java.} namespace. It avoids as much as
 * possible replicating functionality already present in libraries such as
 * Apache Commons and Google Guava, but since it is designed to be
 * self-contained, some overlap in inevitable.
 *
 * @author Ayco Holleman
 *
 */
module nl.naturalis.common {

  exports nl.naturalis.common;
  exports nl.naturalis.common.collection;
  exports nl.naturalis.common.exception;
  exports nl.naturalis.common.function;
  exports nl.naturalis.common.path;
  exports nl.naturalis.common.time;

}
