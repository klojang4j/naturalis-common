package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An extension of {@code OutputStream} that allows for a recall of the data written to the output
 * stream.
 *
 * @author Ayco Holleman
 */
public abstract class RecallOutputStream extends OutputStream {

  /**
   * Reads back the data written to this instance and writes it to the specified output stream.
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void recall(OutputStream output) throws IOException;
}
