/* Copyright (c) 2012 lib4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.lib4j.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public final class Bytes {
  public static int indexOf(final byte[] bytes, final byte ... pattern) {
    return indexOf(bytes, 0, pattern);
  }

  public static int indexOf(final byte[] bytes, final int fromIndex, final byte ... pattern) {
    if (bytes == null)
      throw new IllegalArgumentException("data == null");

    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex < 0");

    if (bytes.length == 0 || pattern.length == 0 || bytes.length < pattern.length)
      return -1;

    for (int i = fromIndex; i < bytes.length; i++) {
      if (bytes[i] == pattern[0]) {
        boolean match = true;
        for (int j = 0; j < pattern.length && (match = bytes.length > i + j && pattern[j] == bytes[i + j]); j++);
        if (match)
          return i;
      }
    }

    return -1;
  }

  public static int[] indicesOf(final byte[] bytes, final byte ... pattern) {
    return indicesOf(0, 0, bytes, pattern);
  }

  public static int[] indicesOf(final byte[] bytes, final int fromIndex, final byte ... pattern) {
    if (bytes == null)
      throw new IllegalArgumentException("data == null");

    if (fromIndex < 0)
      throw new IllegalArgumentException("fromIndex < 0");

    if (fromIndex >= bytes.length)
      throw new IllegalArgumentException("fromIndex >= bytes.length");

    if (pattern.length == 0)
      return new int[0];

   return indicesOf(0, fromIndex, bytes, pattern);
  }

  private static int[] indicesOf(final int depth, final int fromIndex, final byte[] bytes, final byte ... pattern) {
    final int index = indexOf(bytes, fromIndex, pattern);
    final int[] indices;
    if (index > -1) {
      indices = fromIndex < bytes.length - 1 ? indicesOf(depth + 1, index + 1, bytes, pattern) : new int[depth + 1];
      indices[depth] = index;
    }
    else {
      indices = new int[depth];
    }

    return indices;
  }

  public static void replaceAll(final byte[] bytes, final byte target, final byte replacement) {
    if (bytes == null)
      throw new IllegalArgumentException("bytes == null");

    int index = 0;
    while ((index = Bytes.indexOf(bytes, index + 1, target)) != -1)
      bytes[index] = replacement;
  }

  public static void replaceAll(final byte[] bytes, final byte[] target, final byte[] replacement) {
    if (bytes == null)
      throw new IllegalArgumentException("bytes == null");

    if (target == null)
      throw new IllegalArgumentException("target == null");

    if (replacement == null)
      throw new IllegalArgumentException("replacement == null");

    if (target.length != replacement.length)
      throw new IllegalArgumentException("target.length != replacement.length");

    if (bytes.length < target.length || target.length == 0)
      return;

    if (target.length == 1) {
      replaceAll(bytes, target[0], replacement[0]);
      return;
    }

    int index = -1;
    while ((index = indexOf(bytes, index + 1, target)) != -1)
      System.arraycopy(replacement, 0, bytes, index, replacement.length);
  }

  public static int indexOf(final byte[] bytes, final byte[] ... pattern) {
    return indexOf(bytes, 0, pattern);
  }

  public static int indexOf(final byte[] bytes, final int fromIndex, final byte[] ... pattern) {
    if (bytes == null)
      throw new IllegalArgumentException("data == null");

    if (pattern == null)
      throw new IllegalArgumentException("pattern == null");

    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex < 0");

    if (bytes.length <= fromIndex)
      throw new IndexOutOfBoundsException(bytes.length + " <= " + fromIndex);

    final int[][] failure = computeFailure(pattern);
    final int[] k = new int[pattern.length];
    for (int i = fromIndex; i < bytes.length; i++) {
      for (int j = 0; j < pattern.length; j++) {
        while (k[j] > 0 && pattern[j][k[j]] != bytes[i])
          k[j] = failure[j][k[j] - 1];

        if (pattern[j][k[j]] == bytes[i])
          k[j]++;

        if (k[j] == pattern[j].length)
          return i - pattern[j].length + 1;
      }
    }

    return -1;
  }

  private static int[][] computeFailure(final byte[] ... pattern) {
    final int[][] failure = new int[pattern.length][];

    int k = 0;
    for (int i = 0; i < pattern.length; i++) {
      failure[i] = new int[pattern[i].length];
      for (int j = 1; j < pattern[i].length; j++) {
        while (k > 0 && pattern[i][k] != pattern[i][j])
          k = failure[i][k - 1];

        if (pattern[i][k] == pattern[i][j])
          k++;

        failure[i][j] = k;
      }
    }

    return failure;
  }

  /**
   * Create a byte array representation of a short value with big- or little-
   * endian encoding.
   *
   * A Java short is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte,
   * the byte sequence is shifted such that the least significant bytes
   * are skipped first.
   *
   * @param value The value.
   * @param dest The destination bytes.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian Is destination byte array in big-endian encoding.
   */
  public static void toBytes(final short data, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(data & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((data >> 8) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(data & 0xff);
      bytes[offset] = (byte)((data >> 8) & 0xff);
    }
  }

  public static void toBytes(final short data, final ByteArrayOutputStream out, final boolean isBigEndian) {
    if (isBigEndian) {
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
    }
  }

  public static void toBytes(final short data, final RandomAccessFile out, final boolean isBigEndian) throws IOException {
    if (isBigEndian) {
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
    }
  }

  /**
   * Create a byte array representation of a int value with big- or little-
   * endian encoding.
   *
   * A Java int is 4 bytes in size. If the byte array is shorter than 4 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte,
   * the byte sequence is shifted such that the least significant bytes
   * are skipped first.
   *
   * @param value The value.
   * @param dest The destination bytes.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian Is destination byte array in big-endian encoding.
   */
  public static void toBytes(final int data, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(data & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((data >> 8) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((data >> 16) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((data >> 24) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(data & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((data >> 8) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((data >> 16) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset] = (byte)((data >> 24) & 0xff);
    }
  }

  public static void toBytes(final int data, final ByteArrayOutputStream out, final boolean isBigEndian) {
    if (isBigEndian) {
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
    }
  }

  public static void toBytes(final int data, final RandomAccessFile out, final boolean isBigEndian) throws IOException {
    if (isBigEndian) {
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
    }
  }

  /**
   * Create a byte array representation of a long value with big- or little-
   * endian encoding.
   *
   * A Java long is 8 bytes in size. If the byte array is shorter than 8 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte,
   * the byte sequence is shifted such that the least significant bytes
   * are skipped first.
   *
   * @param value The value.
   * @param dest The destination bytes.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian Is destination byte array in big-endian encoding.
   */
  public static void toBytes(final long value, final byte[] dest, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = dest.length - offset;
      dest[--offset] = (byte)(value & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 8) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 16) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 24) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 32) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 40) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 48) & 0xff);
      if (offset == 0)
        return;

      dest[--offset] = (byte)((value >> 56) & 0xff);
    }
    else {
      dest[offset++] = (byte)(value & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 8) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 16) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 24) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 32) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 40) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 48) & 0xff);
      if (offset == dest.length)
        return;

      dest[offset++] = (byte)((value >> 56) & 0xff);
    }
  }

  public static void toBytes(final long data, final ByteArrayOutputStream out, final boolean isBigEndian) {
    if (isBigEndian) {
      out.write((byte)((data >> 56) & 0xff));
      out.write((byte)((data >> 48) & 0xff));
      out.write((byte)((data >> 40) & 0xff));
      out.write((byte)((data >> 32) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 32) & 0xff));
      out.write((byte)((data >> 40) & 0xff));
      out.write((byte)((data >> 48) & 0xff));
      out.write((byte)((data >> 56) & 0xff));
    }
  }

  public static void toBytes(final long data, final RandomAccessFile out, final boolean isBigEndian) throws IOException {
    if (isBigEndian) {
      out.write((byte)((data >> 56) & 0xff));
      out.write((byte)((data >> 48) & 0xff));
      out.write((byte)((data >> 40) & 0xff));
      out.write((byte)((data >> 32) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)(data & 0xff));
    }
    else {
      out.write((byte)(data & 0xff));
      out.write((byte)((data >> 8) & 0xff));
      out.write((byte)((data >> 16) & 0xff));
      out.write((byte)((data >> 24) & 0xff));
      out.write((byte)((data >> 32) & 0xff));
      out.write((byte)((data >> 40) & 0xff));
      out.write((byte)((data >> 48) & 0xff));
      out.write((byte)((data >> 56) & 0xff));
    }
  }

  public static void toBytes(final int byteLength, final long value, final byte[] bytes, final int offset, final boolean isBigEndian) {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)value, bytes, offset, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)value, bytes, offset, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(value, bytes, offset, isBigEndian);
  }

  public static void toBytes(final int byteLength, final long value, final ByteArrayOutputStream out, final boolean isBigEndian) {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)value, out, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)value, out, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(value, out, isBigEndian);
  }

  public static void toBytes(final int byteLength, final long value, final RandomAccessFile out, final boolean isBigEndian) throws IOException {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)value, out, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)value, out, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(value, out, isBigEndian);
  }

  /**
   * Create a signed short representation of a source byte array with big- or
   * little-endian encoding.
   *
   * A Java short is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @return A signed short representation of a byte array.
   */
  public static short toShort(final byte[] src, final int offset, final boolean isBigEndian) {
    return (short)toShort(src, offset, isBigEndian, true);
  }

  /**
   * Create a signed short or an unsigned int representation of a source byte
   * array with big- or little-endian encoding.
   *
   * A Java short is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @param signed If <code>true</code>, return signed short value. If
   *        <code>false</code>, return unsigned int value.
   * @return A signed short or an unsigned int representation of a byte array.
   */
  public static int toShort(final byte[] src, int offset, final boolean isBigEndian, final boolean signed) {
    int value = 0;
    if (isBigEndian) {
      offset = src.length - offset;
      value |= (src[--offset] & 0xff);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xff) << 8);
      if (offset == 0)
        return value;
    }
    else {
      value |= (src[offset++] & 0xff);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xff) << 8);
      if (offset == src.length)
        return value;
    }

    return signed ? (short)value : value;
  }

  /**
   * Create a signed int representation of a source byte array with big- or
   * little-endian encoding.
   *
   * A Java int is 4 bytes in size. If the byte array is shorter than 4 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @return A signed int representation of a byte array.
   */
  public static int toInt(final byte[] src, final int offset, final boolean isBigEndian) {
    return (int)toInt(src, offset, isBigEndian, true);
  }

  /**
   * Create a signed int or an unsigned long representation of a source byte
   * array with big- or little-endian encoding.
   *
   * A Java int is 4 bytes in size. If the byte array is shorter than 4 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @param signed If <code>true</code>, return signed int value. If
   *        <code>false</code>, return unsigned long value.
   * @return A signed int or an unsigned long representation of a byte array.
   */
  public static long toInt(final byte[] src, int offset, final boolean isBigEndian, final boolean signed) {
    long value = 0;
    if (isBigEndian) {
      offset = src.length - offset;
      value |= (src[--offset] & 0xffl);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 8);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 16);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 24);
    }
    else {
      value |= (src[offset++] & 0xffl);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 8);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 16);
      if (offset == src.length)
        return value;

      value |= ((src[offset] & 0xffl) << 24);
    }

    return signed ? (int)value : value;
  }

  /**
   * Create a signed long representation of a source byte array with big- or
   * little-endian encoding.
   *
   * A Java long is 8 bytes in size. If the byte array is shorter than 8 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @return A signed long representation of a byte array.
   */
  // FIXME: Support unsigned
  public static long toLong(final byte[] src, int offset, final boolean isBigEndian) {
    long value = 0;
    if (isBigEndian) {
      offset = src.length - offset;
      value |= (src[--offset] & 0xffl);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 8);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 16);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 24);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 32);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 40);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 48);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffl) << 56);
    }
    else {
      value |= (src[offset++] & 0xffl);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 8);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 16);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 24);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 32);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 40);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffl) << 48);
      if (offset == src.length)
        return value;

      value |= ((src[offset] & 0xffl) << 56);
    }

    return value;
  }

  public static long toArbitraryType(final int byteLength, final byte[] bytes, final int offset, final boolean isBigEndian) {
    return toArbitraryType(byteLength, bytes, offset, isBigEndian, true);
  }

  public static long toArbitraryType(final int byteLength, final byte[] bytes, final int offset, final boolean isBigEndian, final boolean signed) {
    if (byteLength == java.lang.Byte.SIZE / 8)
      return bytes[offset];

    if (byteLength == Short.SIZE / 8)
      return toShort(bytes, offset, isBigEndian, signed);

    if (byteLength == Integer.SIZE / 8)
      return toInt(bytes, offset, isBigEndian, signed);

    if (byteLength == Long.SIZE / 8) {
      if (signed)
        return toLong(bytes, offset, isBigEndian);

      throw new UnsupportedOperationException("Unsigned long is not currently supported");
    }

    throw new UnsupportedOperationException(byteLength + " is not supported");
  }

  public static String toString(final byte ... bytes) {
    final StringBuilder builder = new StringBuilder();
    for (byte b : bytes)
      builder.append(Integer.toBinaryString(b & 255 | 256)).delete(builder.length() - 9, builder.length() - 8);

    return builder.toString();
  }

  /**
   * Print the binary representation of bytes to a <code>PrintStream</code>.
   *
   * @param ps The target <code>PrintStream</code>.
   * @param bytes The bytes to print.
   */
  public static void println(final PrintStream ps, final byte ... bytes) {
    for (int i = 0; i < bytes.length; i++) {
      if (i % 8 == 0) {
        if (i != 0)
          ps.println();

        ps.print(String.format("%8s", Integer.toHexString(i)).replace(' ', '0'));
        ps.print(':');
      }

      ps.print(' ');
      ps.print(Integer.toBinaryString(bytes[i] & 255 | 256).substring(1));
    }

    ps.println();
  }

  /**
   * Print the binary representation of bytes to <code>System.out</code>.
   *
   * @param bytes The bytes to print.
   */
  public static void println(final byte ... bytes) {
    println(System.out, bytes);
  }

  public static short toOctal(byte b) {
    int i = 0;
    short value = 0;
    while (b != 0) {
      final int remainder = b % 8;
      b /= 8;
      value += remainder * Math.pow(10, i++);
    }

    return value;
  }

  public static short[] toOctal(final byte ... bytes) {
    final short[] octal = new short[bytes.length];
    for (int i = 0; i < bytes.length; i++)
      octal[i] = toOctal(bytes[i]);

    return octal;
  }

  /**
   * Write a number of bits from a source byte to a destination byte array at
   * an offset. The bits are counted from right to left (least significant to
   * most significant, as per big-endian encoding). The offset is counted left
   * to right (most significant to least significant, as per big-endian
   * encoding).
   * <p>
   * Examples:
   * <p>
   * If <code>src=0b00011101</code>, writing <code>bits=3</code> at
   * <code>offset=1</code> will result in: <code>0b01010000</code>.
   * <p>
   * If <code>src=0b00011101</code>, writing <code>bits=5</code> at
   * <code>offset=7</code> will result in: <code>{0b00000001, 0b11010000}</code>.
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to
   *        begin writing.
   * @param src The source byte to write.
   * @param bits The number of bits of the byte to write (0 to 8).
   * @return The new offset considering written bits.
   */
  public static int writeBitsB(final byte[] dest, final int offset, byte src, final byte bits) {
    final int i = offset / 8;
    final int right = offset % 8;
    final int left = 8 - bits;
    src = (byte)(src << left);
    if (left >= right) {
      dest[i] |= (byte)((src & 0xff) >> right);
    }
    else {
      dest[i] |= (src & 0xff) >> right;
      dest[i + 1] |= src << 8 - right;
    }

    return offset + bits;
  }

  /**
   * Write a number of bits from a source byte array to a destination byte
   * array at an offset. The bits are counted from right to left (least
   * significant to most significant, as per big-endian encoding). The offset
   * is counted left to right (most significant to least significant, as per
   * big-endian encoding). If <code>bits &gt; 8</code>, the starting bit to be
   * read is the least significant bit in the <code>bits % 8</code> position in
   * the source array.
   * <p>
   * Examples:
   * <p>
   * If <code>src={0b01011011, 0b01101101}</code>, writing <code>bits=9</code> at
   * <code>offset=7</code> will result in: <code>{0b00000001, 0b01101101}</code>.
   * <p>
   * If <code>src={0b01011011, 0b01101101}</code>, writing <code>bits=13</code> at
   * <code>offset=3</code> will result in: <code>{0b00000011, (byte)0b01101101}</code>.
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to
   *        begin writing.
   * @param src The source byte array to write.
   * @param bits The number of bits to write from the source array (0 to 8 * src.length).
   * @return The new offset considering written bits.
   */
  public static int writeBitsB(final byte[] dest, int offset, final byte[] src, long bits) {
    final byte remainder = (byte)(1 + (bits - 1) % 8);
    offset = writeBitsB(dest, offset, src[0], remainder);
    bits -= remainder;
    for (int i = 1; bits > 0; bits -= 8)
      offset = writeBitsB(dest, offset, src[i++], (byte)8);

    return offset;
  }

  /**
   * Write a number of bits from a source byte to a destination byte array at
   * an offset. The bits are counted from right to left (least significant to
   * most significant, as per little-endian encoding). The offset is counted left
   * to right (least significant to most significant, as per little-endian
   * encoding).
   * <p>
   * Examples:
   * <p>
   * If <code>src=0b11101000</code>, writing <code>bits=3</code> at
   * <code>offset=1</code> will result in: <code>0b01110000</code>.
   * <p>
   * If <code>src=0b11101000</code>, writing <code>bits=5</code> at
   * <code>offset=7</code> will result in: <code>{0b00000001, 0b11010000}</code>.
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to
   *        begin writing.
   * @param src The source byte to write.
   * @param bits The number of bits of the byte to write (0 to 8).
   * @return The new offset considering written bits.
   */
  public static int writeBitsL(final byte[] dest, final int offset, byte src, final byte bits) {
    final int i = offset / 8;
    final int r = offset % 8;
    final int left = 8 - bits;
    src >>= left;
    if (left >= r) {
      dest[i] |= (byte)((src & 0xff) << left - r);
    }
    else {
      final int right = r - left;
      dest[i] |= (src & 0xff) >> right;
      dest[i + 1] |= src << 8 - right;
    }

    return offset + bits;
  }

  /**
   * Write a number of bits from a source byte array to a destination byte
   * array at an offset. The bits are counted from left to right (least
   * significant to most significant, as per little-endian encoding). The offset
   * is counted left to right (least significant to most significant, as per
   * little-endian encoding).
   * <p>
   * Examples:
   * <p>
   * If <code>src={0b01011011, 0b01101101}</code>, writing <code>bits=10</code> at
   * <code>offset=5</code> will result in: <code>{0b000000010, 0b11011010}</code>.
   * <p>
   * If <code>src={0b01011011, 0b01101101}</code>, writing <code>bits=13</code> at
   * <code>offset=3</code> will result in: <code>{0b00001011, (byte)0b01101101}</code>.
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to
   *        begin writing.
   * @param src The source byte array to write.
   * @param bits The number of bits to write from the source array (0 to 8 * src.length).
   * @return The new offset considering written bits.
   */
  public static int writeBitsL(final byte[] dest, int offset, final byte[] src, int bits) {
    int i = 0;
    for (; bits > 8; bits -= 8)
      offset = writeBitsL(dest, offset, src[i++], (byte)8);

    return writeBitsL(dest, offset, src[i], (byte)(1 + (bits - 1) % 8));
  }

  /**
   * Return the byte representation from reading a number of bits (0 to 8) from
   * a source byte array at an offset, read in the direction of most
   * significant bit to least significant bit. This method returns the value of
   * the read byte as shifted to fill the least significant bits first, allowing
   * the front of the byte to encode a value as if the bits were in the tail of
   * the byte. Java uses big-engian encoding, placing the most significant bits
   * at the front of the byte.
   *
   * @param src The source byte array.
   * @param offset The offset in bits.
   * @param bits The number of bits to read (0 to 8).
   * @return The byte representation of the read bits from the source byte
   *         array at the offset.
   */
  public static byte readBitsFromByte(final byte[] src, final int offset, byte bits) {
    final int i = offset / 8;
    final int left = offset % 8;
    bits = (byte)(8 - bits);
    final byte dest = (byte)((src[i] << left & 0xff) >> bits);
    return left <= bits ? dest : (byte)(dest | (src[i + 1] & 0xff) >> 8 + bits - left);
  }

  /**
   * Return the byte array representation from reading a number of bits from a
   * source byte array at an offset, read in the direction of most significant
   * bit to least significant bit. This method returns the value of the read
   * byte as shifted to fill the least significant bits first, allowing the
   * front of the byte to encode a value as if the bits were in the tail of the
   * byte. Java uses big-engian encoding, placing the most significant bits at
   * the front of the byte.
   *
   * @param src The source byte array.
   * @param offset The offset in bits.
   * @param bits The number of bits to read.
   * @return The byte array representation of the read bits from the source byte
   *         array at the offset.
   */
  public static byte[] readBitsFromBytes(final byte[] src, int offset, final long bits) {
    if (bits <= 8)
      return new byte[] {readBitsFromByte(src, offset, (byte)bits)};

    final byte[] dest = new byte[(int)(1 + (bits - 1) / 8)];
    final byte remainder = (byte)(1 + (bits - 1) % 8);
    dest[0] = readBitsFromByte(src, offset, remainder);
    offset += remainder;
    for (int i = 1; i < dest.length; i++, offset += 8)
      dest[i] = readBitsFromByte(src, offset, (byte)8);

    return dest;
  }

  /**
   * Get the number of bits necessary to store a value.
   *
   * @param value The value.
   * @return The number of bits necessary to store a value.
   */
  public static byte getSize(final int value) {
    return (byte)(1 + Math.log(value) / Math.log(2));
  }

  /**
   * Get the number of bits necessary to store a value.
   *
   * @param value The value.
   * @return The number of bits necessary to store a value.
   */
  public static byte getSize(final long value) {
    return (byte)(1 + Math.log(value) / Math.log(2));
  }

  private Bytes() {
  }
}