package nl.naturalis.common.io;

import static nl.naturalis.common.check.CommonChecks.equalTo;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.notNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;

/**
 * An alternative to {@link ZipOutputStream} that lets you write multiple zip entries at the same
 * time. This can save a lot of time in scenarios where (for example) a single database record feeds
 * multiple zip entries. With {@code ZipOutputStream} you would have to make the same iteration over
 * the same table for every zip entry that needs to be populated.
 *
 * <p>One zip entry needs to be designated the main entry. This entry is written directly to the
 * {@link #withMainEntry(ZipEntry, OutputStream) client-provided} output stream. The other zip
 * entries are buffered and potentially swapped to file before being merged back into the
 * client-provided output stream.
 *
 * <p>Though you could, it makes no sense to wrap a {@code REZipOutputStream} into a {@code
 * BufferedOutputStream}. See {@link ArraySwapOutputStream} for why this is so. The client-provided
 * output stream, however, is not automatically wrapped into a {@code BufferedOutputStream}, so
 * could benefit from it.
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
    private HashMap<String, Tuple<ZipEntry, SwapOutputStream>> sideEntries;
    private boolean cleanup = true;
    private boolean nio = false;

    private Builder(ZipEntry mainEntry, OutputStream out) {
      this.out = Check.notNull(out, "out").ok();
      this.mainEntry = Check.notNull(mainEntry, "mainEntry").ok();
      this.sideEntries = new HashMap<>();
      String prop = REZipOutputStream.class.getName() + ".nio";
      this.nio = System.getProperty(prop, "false").equalsIgnoreCase("true");
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
     * grows beyond {@code bufSize} bytes. Bytes entering the buffer are compressed to decrease the
     * chance it needs to be swapped out to file.
     *
     * @param name The name of the zip entry
     * @param bufSize The buffer size in bytes
     * @return This {@code Builder}
     */
    public Builder addEntry(String name, int bufSize) {
      return addEntry(name, bufSize, nio);
    }

    /**
     * Adds a buffered zip entry with the specified name. The buffer is swapped to file when it
     * grows beyond {@code bufSize} bytes. Bytes entering the buffer are compressed to decrease the
     * chance it needs to be swapped out to file. You can optional specify that you want to use a
     * implementation based on {@code java.nio} for buffered zip entries. This might be beneficial
     * for very large entries. Alternatively, you can specify a system property to specify the
     * default implementation: {@code -Dnl.naturalis.common.io.REZipOutputStream.nio=true}.
     *
     * @param name The name of the zip entry
     * @param bufSize The buffer size in bytes
     * @param nio Whether or not to use {@code java.nio} for zip buffered entries
     * @return This {@code Builder}
     */
    public Builder addEntry(String name, int bufSize, boolean nio) {
      if (nio) {
        return addEntry(new ZipEntry(name), DeflatedNioSwapOutputStream.newInstance(bufSize));
      }
      return addEntry(new ZipEntry(name), DeflatedArraySwapOutputStream.newInstance(bufSize));
    }

    /**
     * Adds a buffered zip entry with the specified name. Data is written, buffered and recalled
     * using the specified {@link SwapOutputStream}.
     *
     * @param entry The zip entry
     * @param out The output stream to which data for this entry is written
     * @return This {@code Builder}
     */
    public Builder addEntry(ZipEntry entry, SwapOutputStream out) {
      Check.notNull(entry, "entry");
      Check.notNull(out, "out");
      String name = entry.getName();
      Check.that(name)
          .isNot(equalTo(), mainEntry.getName(), DUPLICATE_ENTRY, name)
          .isNot(keyIn(), sideEntries, DUPLICATE_ENTRY, name)
          .then(n -> sideEntries.put(n, Tuple.of(entry, out)));
      return this;
    }

    /**
     * Whether or not to automatically clean up any swap files created by REZipOutputStream. Default
     * true.
     *
     * @param cleanup Whether or not to automatically clean up any swap files
     * @return This {@code Builder}
     */
    public Builder withAutoCleanup(boolean cleanup) {
      this.cleanup = cleanup;
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
      return new REZipOutputStream(
          out, mainEntry, Collections.unmodifiableMap(sideEntries), cleanup);
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
  private final Map<String, Tuple<ZipEntry, SwapOutputStream>> sideEntries;
  private final boolean cleanup;

  private OutputStream active;

  private REZipOutputStream(
      OutputStream out,
      ZipEntry mainEntry,
      Map<String, Tuple<ZipEntry, SwapOutputStream>> sideEntries,
      boolean cleanup)
      throws IOException {
    this.mainEntry = mainEntry;
    this.mainOut = new ZipOutputStream(out);
    this.mainOut.putNextEntry(mainEntry);
    this.sideEntries = sideEntries;
    this.cleanup = cleanup;
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
          Check.on(IOException::new, sideEntries.get(name))
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
    for (Tuple<ZipEntry, SwapOutputStream> t : sideEntries.values()) {
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
   * Merges all zip entries back into the main output stream and returns the {@link ZipOutputStream}
   * that was wrapped around the main output stream. Neither {@link ZipOutputStream#finish()} nor
   * {@link ZipOutputStream#close()} has been called yet on the {@code ZipOutputStream}. This allows
   * clients to add extra entries in serial fashion. It is up to clients to close the {@code
   * ZipOutputStream}.
   *
   * @return A {@code ZipOutpuStream} wrapping the client-provided output stream
   * @throws IOException If an I/O error occurs
   */
  public ZipOutputStream mergeEntries() throws IOException {
    for (Tuple<ZipEntry, SwapOutputStream> t : sideEntries.values()) {
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
    for (Tuple<ZipEntry, SwapOutputStream> t : sideEntries.values()) {
      if (cleanup) {
        t.getRight().cleanup();
      }
      t.getRight().close();
    }
  }
}
