package nl.naturalis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.AugmentationType;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.util.AugmentationType.MULTIPLY;

/**
 * An output stream in which the data is written into a byte array. The buffer automatically grows
 * as data is written to it. Contrary to Java's own {@link ByteArrayOutputStream}, the internal byte
 * array is exposed to the client: calling {@link #toByteArray()} returns the byte array rather than
 * a copy of it. Note, however, that you must therefore use {@link #toByteArray()} in combination
 * with the {@link #count()} method to extracte the "live" bytes - e.g <code>
 * new String(out.toArray(), 0, out.count())</code>.
 *
 * <p>This class also lets you specify how to increase the size of the byte array once it reaches
 * full capacity.
 *
 * <p>Closing a {@code ByteArrayOutputStream} has no effect. The methods in this class can be called
 * after the stream has been closed without generating an {@code IOException}.
 *
 * @author Ayco Holleman
 */
public class ExposedByteArrayOutputStream extends OutputStream {

  private final float ib;
  private final AugmentationType it;

  private byte[] buf;
  private int cnt;

  /**
   * Creates a new {@code SeeThruByteArrayOutputStream}. The buffer capacity is initially 512 bytes,
   * though its size increases if necessary. When it reaches full capacity its replaced with a
   * buffer twice its size.
   */
  public ExposedByteArrayOutputStream() {
    this(512);
  }

  /**
   * Creates a new {@code SeeThruByteArrayOutputStream}. The buffer initially has the specified
   * number of bytes. When it reaches full capacity its replaced with a buffer twice its size.
   *
   * @param capacity The initial buffer capacity
   */
  public ExposedByteArrayOutputStream(int capacity) {
    this(new byte[capacity], 0);
  }

  /**
   * Creates a new {@code SeeThruByteArrayOutputStream} that wraps around the specified byte array.
   * When it reaches full capacity its replaced with a buffer twice its size.
   *
   * @param buf The initial byte array to write to
   * @param offset The offset of the first byte to write.The offset may be equal to the byte array's
   *     length (causing write actions to append to what is already in the array) but not greater.
   */
  public ExposedByteArrayOutputStream(byte[] buf, int offset) {
    this(buf, offset, 2F, MULTIPLY);
  }

  /**
   * Creates a new {@code SeeThruByteArrayOutputStream}. The buffer capacity is initially {@code
   * size} bytes, though its size increases if necessary.
   *
   * @param size The initial length of the byte array
   * @param incrementBy incrementBy The amount by which the increase the size of the byte array
   * @param incrementType The type of increase. Whichever {@code IncrementBy} value and {@code
   *     IncrementType} you choose, the buffer capacity will always be increased enough to sustain
   *     the {@code write} action.
   */
  public ExposedByteArrayOutputStream(int size, float incrementBy, AugmentationType incrementType) {
    this(new byte[size], 0, incrementBy, incrementType);
  }

  /**
   * Creates a new {@code SeeThruByteArrayOutputStream} that wraps around the specified byte array.
   *
   * @param buf The initial byte array to write to
   * @param offset The offset of the first byte to write. The offset may be equal to the byte
   *     array's length (causing write actions to append to what is already in the array) but not
   *     greater.
   * @param incrementBy The amount by which the increase the size of the byte array
   * @param incrementType The type of increase. Whichever {@code IncrementBy} value and {@code
   *     IncrementType} you choose, the buffer capacity will always be increased by at least 1.
   */
  public ExposedByteArrayOutputStream(
      byte[] buf, int offset, float incrementBy, AugmentationType incrementType) {
    this.buf = Check.notNull(buf, "buf").has(length(), gt(), 0).ok();
    this.cnt = Check.that(offset, "offset").is(lte(), buf.length).ok();
    this.ib = Check.that(incrementBy, "incrementBy").is(greaterThan(), 0).ok();
    this.it = Check.notNull(incrementType, "incrementType").ok();
  }

  /**
   * Writes the specified byte to this {@code ExposedByteArrayOutputStream}.
   *
   * @param b the byte to be written
   */
  @Override
  public void write(int b) {
    if (cnt == buf.length) {
      increaseCapacity(1);
    }
    buf[cnt++] = (byte) b;
  }

  /**
   * Writes the complete contents of the specified byte array to this {@code
   * ExposedByteArrayOutputStream}.
   *
   * @param b the data
   */
  @Override
  public void write(byte b[]) {
    write(b, 0, b.length);
  }

  /**
   * Writes {@code len} bytes from the specified byte array starting at offset {@code off} to this
   * {@code ExposedByteArrayOutputStream}.
   *
   * @param b the data
   * @param off the start offset in the data
   * @param len the number of bytes to write
   */
  public void write(byte[] b, int off, int len) {
    Check.notNull(b, "b").has(length(), gte(), off + len).given(off >= 0, len >= 0);
    if (cnt + len > buf.length) {
      increaseCapacity(cnt + len - buf.length);
    }
    System.arraycopy(b, off, buf, cnt, len);
    cnt += len;
  }

  /**
   * Writes the complete contents of this {@code ByteArrayOutputStream} to the specified output
   * stream argument, as if by calling the output stream's write method using {@code out.write(buf,
   * 0, count)}.
   *
   * @param out the output stream to which to write the data.
   * @throws IOException if an I/O error occurs.
   */
  public synchronized void writeTo(OutputStream out) throws IOException {
    Check.notNull(out).ok().write(buf, 0, cnt);
  }

  /**
   * Returns the backing array for this instance (not a copy of it). Note that you must use this
   * method <i>in combination with</i>the {@link #count()} method to retrieve the valid bytes only.
   *
   * @return
   */
  public byte[] toByteArray() {
    return buf;
  }

  /**
   * Returns the number of bytes written to this instance.
   *
   * @return
   */
  public int count() {
    return cnt;
  }

  /**
   * Resets the byte count to zero, so that all currently accumulated output in the output stream is
   * discarded. The output stream can be used again, reusing the already allocated buffer space.
   */
  public synchronized void reset() {
    cnt = 0;
  }

  /**
   * Closing a {@code ByteArrayOutputStream} has no effect. The methods in this class can be called
   * after the stream has been closed without generating an {@code IOException}.
   */
  public void close() {}

  private void increaseCapacity(int minIncrease) {
    long newSize;
    switch (it) {
      case ADD:
        newSize = buf.length + Math.max(minIncrease, (int) ib);
        break;
      case MULTIPLY:
        newSize = Math.max(buf.length + minIncrease, buf.length * (int) ib);
        break;
      case PERCENTAGE:
      default:
        newSize = Math.max(buf.length + minIncrease, buf.length * ((100 + (int) ib) / 100));
        break;
    }
    Check.with(s -> new BufferOverflowException(), newSize).is(atMost(), Integer.MAX_VALUE);
    byte[] newBuf = new byte[(int) newSize];
    System.arraycopy(buf, 0, newBuf, 0, cnt);
    buf = newBuf;
  }
}
