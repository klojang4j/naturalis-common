package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An extension of {@code OutputStream} that allows for data written to it to be recalled.
 *
 * @author Ayco Holleman
 */
public abstract class RecallOutputStream extends OutputStream {

  /**
   * Collects the data written to this instance and writes it to the specified output stream.
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void recall(OutputStream output) throws IOException;

  /**
   * Deletes the resource that the {@code RecallOutputStream} was writing to. Can be called if,
   * after the data has been recalled, the resource is no longer needed. You might also want to call
   * this method in the {@code catch} block of an exception. The {@code cleanup} method of {@code
   * RecallOutputStream} itself does nothing.
   *
   * @throws IOException
   */
  public void cleanup() throws IOException {}
}
