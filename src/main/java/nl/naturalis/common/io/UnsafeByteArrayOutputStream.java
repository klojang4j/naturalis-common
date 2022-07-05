package nl.naturalis.common.io;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.ResizeMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.util.ResizeMethod.MULTIPLY;
import static nl.naturalis.common.util.ResizeMethod.getMinIncrease;

/**
 * An output stream in which the data is written into a byte array. The buffer
 * automatically grows as data is written to it. Contrary to Java's own {@link
 * ByteArrayOutputStream}, the internal byte array is exposed to the client via
 * {@link #getBackingArray()}, which might save you an array copy (e.g. when passing
 * the byte array to the {@link String#String(byte[], int, int) String constructor}).
 * To extract the "live" bytes from the backing array, use the {@link #size()}
 * method.
 *
 * <p>Closing a {@code ByteArrayOutputStream} has no effect. The methods in this
 * class can be called after the stream has been closed without generating an {@code
 * IOException}.
 *
 * @author Ayco Holleman
 */
public final class UnsafeByteArrayOutputStream extends OutputStream {

  private final ResizeMethod resizeMethod;
  private final float resizeAmount;

  private byte[] buf;
  private int sz;

  /**
   * Creates a new {@code UnsafeByteArrayOutputStream}. The buffer capacity is
   * initially 512 bytes, though its size increases if necessary. When it reaches
   * full capacity its replaced with a buffer twice its size.
   */
  public UnsafeByteArrayOutputStream() {
    this(512);
  }

  /**
   * Creates a new {@code UnsafeByteArrayOutputStream}. The buffer initially has the
   * specified number of bytes. When it reaches full capacity it is replaced with a
   * buffer twice its size.
   *
   * @param capacity The initial buffer capacity
   */
  public UnsafeByteArrayOutputStream(int capacity) {
    this(capacity, MULTIPLY, 2F);
  }

  /**
   * Creates a new {@code UnsafeByteArrayOutputStream} that wraps around the
   * specified byte array. When it reaches full capacity it is replaced with a buffer
   * twice its size.
   *
   * @param buf the initial byte array to write to
   * @param offset the offset of the first byte to write.The offset may be equal
   *     to the byte array's length (causing write actions to append to what is
   *     already in the array), but not greater.
   */
  public UnsafeByteArrayOutputStream(byte[] buf, int offset) {
    this(buf, offset, MULTIPLY, 2F);
  }

  /**
   * Creates a new {@code UnsafeByteArrayOutputStream}. The buffer capacity is
   * initially {@code size} bytes, though its size increases if necessary.
   *
   * @param capacity the initial length of the byte array
   * @param resizeMethod The type of expansion. Whichever {@code resizeAmount}
   *     value and {@code ResizeMethod} you choose, the buffer capacity will always
   *     be increased enough to sustain the next {@code write} action.
   * @param resizeAmount The amount by which the increase the size of the byte
   *     array when it reaches full capacity
   */
  public UnsafeByteArrayOutputStream(int capacity,
      ResizeMethod resizeMethod,
      float resizeAmount) {
    Check.that(capacity, "capacity").is(gte(), 0);
    Check.that(resizeMethod, "resizeMethod").is(notNull());
    Check.that(resizeAmount, "resizeAmount").is(GT(), 0F);
    this.buf = new byte[capacity];
    this.resizeMethod = resizeMethod;
    this.resizeAmount = resizeAmount;
  }

  /**
   * Creates a new {@code UnsafeByteArrayOutputStream} that wraps around the
   * specified byte array.
   *
   * @param buf the initial byte array to write to
   * @param offset the offset of the first byte to write. The offset may be equal
   *     to the byte array's length (causing write actions to append to what is
   *     already in the array) but not greater.
   * @param resizeMethod the array resize method. Whichever {@code resizeAmount}
   *     value and {@code ResizeMethod} you choose, the buffer capacity will always
   *     be increased enough to sustain the next {@code write} action.
   * @param resizeAmount the amount by which to increase the size of the byte
   *     array when it reaches full capacity
   */
  public UnsafeByteArrayOutputStream(byte[] buf,
      int offset,
      ResizeMethod resizeMethod,
      float resizeAmount) {
    Check.that(buf, "buf").is(notNull());
    Check.that(offset, "offset").is(subArrayIndexOf(), buf);
    Check.that(resizeMethod, "resizeMethod").is(notNull());
    Check.that(resizeAmount, "resizeAmount").is(GT(), 0F);
    this.buf = buf;
    this.sz = offset;
    this.resizeAmount = resizeAmount;
    this.resizeMethod = resizeMethod;
  }

  /**
   * Writes the specified byte to this {@code ExposedByteArrayOutputStream}.
   *
   * @param b the byte to be written
   */
  @Override
  public void write(int b) {
    int minIncrease = getMinIncrease(buf.length, sz, 1);
    if (minIncrease > 0) {
      increaseCapacity(minIncrease);
    }
    buf[sz++] = (byte) b;
  }

  /**
   * Writes the complete contents of the specified byte array to this {@code
   * ExposedByteArrayOutputStream}.
   *
   * @param b the data
   */
  @Override
  public void write(byte[] b) {
    Check.notNull(b);
    write(b, 0, b.length);
  }

  /**
   * Writes {@code len} bytes from the specified byte array starting at offset {@code
   * off} to this {@code ExposedByteArrayOutputStream}.
   *
   * @param b the data
   * @param off the start offset in the data
   * @param len the number of bytes to write
   */
  public void write(byte[] b, int off, int len) {
    Check.offsetLength(b, off, len);
    int minIncrease = getMinIncrease(buf.length, sz, len);
    if (minIncrease > 0) {
      increaseCapacity(minIncrease);
    }
    System.arraycopy(b, off, buf, sz, len);
    sz += len;
  }

  /**
   * Writes the complete contents of this {@code ByteArrayOutputStream} to the
   * specified output stream argument, as if by calling the output stream's write
   * method using {@code out.write(buf, 0, count)}.
   *
   * @param out the output stream to which to write the data.
   * @throws IOException if an I/O error occurs.
   */
  public synchronized void writeTo(OutputStream out) throws IOException {
    Check.notNull(out).ok().write(buf, 0, sz);
  }

  /**
   * Returns the backing array for this instance. Use the {@link #size()} method to
   * extract the "live" bytes from the backing array.
   *
   * @return the backing array
   */
  public byte[] getBackingArray() {
    return buf;
  }

  /**
   * Returns the bytes that were written to this instance in a new byte array.
   *
   * @return The bytes that were written to this instance in a new byte array
   */
  public byte[] toByteArray() {
    byte[] copy = new byte[sz];
    System.arraycopy(buf, 0, copy, 0, sz);
    return copy;
  }

  /**
   * Returns the number of bytes written to this instance.
   *
   * @return The number of bytes written to this instance.
   */
  public int size() {
    return sz;
  }

  /**
   * Resets the byte count to zero, so that all currently accumulated output in the
   * output stream is discarded. The output stream can be used again, reusing the
   * already allocated buffer space.
   */
  public void reset() {
    sz = 0;
  }

  /**
   * Does nothing. You can keep using the instance after {@code close()} has been
   * called on it.
   */
  public void close() {}

  @Override
  public String toString() {
    return new String(buf, 0, size(), StandardCharsets.UTF_8);
  }

  public String toString(Charset charset) {
    return new String(buf, 0, size(), charset);
  }

  private void increaseCapacity(int minIncrease) {
    int newSize = resizeMethod.resize(buf.length, resizeAmount, minIncrease);
    byte[] newBuf = new byte[newSize];
    System.arraycopy(buf, 0, newBuf, 0, sz);
    buf = newBuf;
  }

}
