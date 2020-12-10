package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notEqualTo;
import static nl.naturalis.common.check.CommonChecks.notKeyIn;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * An {@link OutputStream} that compresses data according to the zip file format. This class lets
 * you write multiple zip entries at the same time, rather than one at a time as with {@link
 * ZipOutputStream}. This can save a lot of time in scenarios where (for example) a single database
 * record feeds multiple zip entries. With {@link ZipOutputStream} you would have to make the same
 * iteration over the same table for every zip entry that needs to be populated.
 *
 * <p>One zip entry needs to be designated the main entry. This entry is written directly to the
 * {@link #withMainEntry(ZipEntry, OutputStream) client-provided output stream}. The other zip
 * entries are buffered and potentially swapped to file before being merged into the main output
 * stream.
 *
 * <p>Though you could, it makes no sense to wrap a {@code REZipOutputStream} into a {@link
 * BufferedOutputStream}. See {@link SwapOutputStream}. The client-provided output stream, however,
 * is not automatically wrapped into a {@code BufferedOutputStream}, so could benefit from it.
 *
 * <p>Example:
 *
 * <p>
 *
 * <pre>
 * File archive = Path.of(System.getProperty("user.home"), "genesis.zip").toFile();
 * FileOutputStream fos = new FileOutputStream(archive);
 * BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
 * try (REZipOutputStream rezos =
 *   REZipOutputStream.withMainEntry("The Beginning.txt", bos)
 *     .addEntry("Adam and Eve.txt")
 *     .addEntry("The Fall.txt")
 *     .build()) {
 *   rezos.write("In the beginning there was nothing".getBytes());
 *   rezos.setActiveEntry("Adam and Eve.txt");
 *   rezos.write("Then came Adam & Eve".getBytes());
 *   rezos.setActiveEntry("The Fall.txt");
 *   rezos.write("It all went downhill from there".getBytes());
 *   rezos.mergeEntries().close();
 * }
 * // Now we have our archive
 * try(ZipFile zf = new ZipFile(archive)) {
 *   // etc.
 * }
 * </pre>
 *
 * @author Ayco Holleman
 */
public class REZipOutputStream extends OutputStream {

  /**
   * A builder class for {@code REZipOutputStream} instances.
   *
   * @author Ayco Holleman
   */
  public static class Builder {
    private OutputStream out;
    private ZipEntry mainEntry;
    private HashMap<String, Tuple<ZipEntry, RecallOutputStream>> sideEntries = new HashMap<>();

    private Builder(ZipEntry mainEntry, OutputStream out) {
      this.out = Check.notNull(out, "out").ok();
      this.mainEntry = Check.notNull(mainEntry, "mainEntry").ok();
    }

    /**
     * Adds a buffered zip entry with the specified name. The buffer is swapped to file when it
     * grows beyond 1 megabyte.
     *
     * @param name The name of the zip entry
     * @return This {@code Builder}
     */
    public Builder addEntry(String name) {
      return addEntry(name, 1024 * 1024);
    }

    /**
     * Adds a buffered zip entry with the specified name. The buffer is swapped to file when it
     * grows beyond {@code bufSize} bytes.
     *
     * @param name The name of the zip entry
     * @param bufSize The buffer size in bytes
     * @return This {@code Builder}
     */
    public Builder addEntry(String name, int bufSize) {
      return addEntry(new ZipEntry(name), ZipFileSwapOutputStream.newInstance(bufSize));
    }

    /**
     * Adds a buffered zip entry with the specified name. Data is written, buffered and recalled
     * using the specified {@link RecallOutputStream}.
     *
     * @param entry The zip entry
     * @param out The output stream to which data for this entry is written
     * @return This {@code Builder}
     */
    public Builder addEntry(ZipEntry entry, RecallOutputStream out) {
      Check.notNull(entry, "entry");
      Check.notNull(out, "out");
      String name = entry.getName();
      Check.that(name)
          .is(notEqualTo(), mainEntry.getName(), DUPLICATE_ENTRY, name)
          .is(notKeyIn(), sideEntries, DUPLICATE_ENTRY, name);
      sideEntries.put(name, Tuple.of(entry, out));
      return this;
    }

    /**
     * Returns a new {@code REZipOutputStream} instance. The initially {@link
     * REZipOutputStream#setActiveEntry(String) active entry} will be the main entry.
     *
     * @return A new {@code REZipOutputStream} instance
     * @throws IOException If an I/O error occurs
     */
    public REZipOutputStream build() throws IOException {
      return new REZipOutputStream(out, mainEntry, Collections.unmodifiableMap(sideEntries));
    }
  }

  private static final String DUPLICATE_ENTRY = "Duplicate zip entry: %s";
  private static final String NO_SUCH_ENTRY = "Undeclared zip entry: %s";

  /**
   * Returns a {@code Builder} object that lets you configure new {@code REZipOutputStream}
   * instance.
   *
   * @param mainEntry The name of main zip entry
   * @param out The output stream to write the data for this entry to
   * @return A {@code Builder} object for Returns a {@code Builder} object that lets you configure
   *     new {@code REZipOutputStream} instances.
   */
  public static Builder withMainEntry(String mainEntry, OutputStream out) {
    return new Builder(new ZipEntry(mainEntry), out);
  }

  /**
   * Returns a {@code Builder} object that lets you configure a new {@code REZipOutputStream}
   * instance.
   *
   * @param mainEntry The name of main zip entry
   * @param out The output stream to write the data for this entry to
   * @return A {@code Builder} object for Returns a {@code Builder} object that lets you configure
   *     new {@code REZipOutputStream} instances.
   */
  public static Builder withMainEntry(ZipEntry mainEntry, OutputStream out) {
    return new Builder(mainEntry, out);
  }

  private final ZipEntry mainEntry;
  private final ZipOutputStream mainOut;
  private final Map<String, Tuple<ZipEntry, RecallOutputStream>> sideEntries;

  private OutputStream active;

  private REZipOutputStream(
      OutputStream out,
      ZipEntry mainEntry,
      Map<String, Tuple<ZipEntry, RecallOutputStream>> sideEntries)
      throws IOException {
    this.mainEntry = mainEntry;
    this.mainOut = new ZipOutputStream(out);
    this.mainOut.putNextEntry(mainEntry);
    this.sideEntries = sideEntries;
    this.active = mainOut;
  }

  /**
   * Makes the zip entry with the specified name the target for subsequent {@code write} actions.
   *
   * @param name The name of the zip entry
   * @throws IOException If an I/O error occurs
   */
  public void setActiveEntry(String name) throws IOException {
    if (Check.notNull(name).ok().equals(mainEntry.getName())) {
      this.active = this.mainOut;
    } else {
      this.active =
          Check.with(IOException::new, sideEntries.get(name))
              .is(notNull(), NO_SUCH_ENTRY, name)
              .ok(Tuple::getRight);
    }
  }

  /** Writes the specified byte to the active output stream. */
  @Override
  public void write(int b) throws IOException {
    active.write(b);
  }

  /** Writes the specified byte array to the active output stream. */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    active.write(b, off, len);
  }

  /**
   * Flushes all outputstreams. Flushing should be kept to a minimum as it increases the chance that
   * the output streams for the side entries start swapping.
   */
  @Override
  public void flush() throws IOException {
    mainOut.flush();
    for (Tuple<ZipEntry, RecallOutputStream> t : sideEntries.values()) {
      t.getRight().flush();
    }
  }

  /**
   * Flushes the output stream for the active zip entry.
   *
   * @throws IOException If an I/O error occurs
   */
  public void flushActive() throws IOException {
    active.flush();
  }

  /**
   * Merges the output streams for the side entries back into the main (client-provided) output
   * stream and returns the {@link ZipOutputStream} that was wrapped around the main output stream.
   * Neither {@link ZipOutputStream#finish()} nor {@link ZipOutputStream#close()} has been called
   * yet on the {@code ZipOutputStream}. This allows clients to add extra entries in serial fashion.
   * It is up to clients to close the {@code ZipOutputStream}.
   *
   * @return A {@code ZipOutpuStream} wrapping the client-provided output stream
   * @throws IOException If an I/O error occurs
   */
  public ZipOutputStream mergeEntries() throws IOException {
    for (Tuple<ZipEntry, RecallOutputStream> t : sideEntries.values()) {
      mainOut.putNextEntry(t.getLeft());
      t.getRight().recall(mainOut);
    }
    return mainOut;
  }

  /**
   * Closes the outputstreams for all side entries but does not close the main (client-provided)
   * output stream. See {@link #mergeEntries()}.
   */
  public void close() throws IOException {
    for (Tuple<ZipEntry, RecallOutputStream> t : sideEntries.values()) {
      t.getRight().cleanup();
      t.getRight().close();
    }
  }
}
