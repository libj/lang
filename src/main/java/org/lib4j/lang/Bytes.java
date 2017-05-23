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
import java.io.RandomAccessFile;

public final class Bytes {
  public static class Byte {
    public static int indexOf(final byte[] bytes, final byte ... pattern) {
      return indexOf(bytes, 0, pattern);
    }

    public static int indexOf(final byte[] bytes, final int fromIndex, final byte ... pattern) {
      if (bytes == null)
        throw new NullPointerException("data == null");

      if (fromIndex < 0)
        throw new IndexOutOfBoundsException("fromIndex < 0");

      if (bytes.length <= fromIndex)
        throw new IndexOutOfBoundsException(bytes.length + " <= " + fromIndex);

      for (int i = fromIndex; i < bytes.length; i++)
        for (int j = 0; j < pattern.length; j++)
          if (bytes[i] == pattern[j])
            return i;

      return -1;
    }

    public static IntArrayList indicesOf(final byte[] bytes, final char ... pattern) {
      return indicesOf(bytes, 0, pattern);
    }

    public static IntArrayList indicesOf(final byte[] bytes, final int fromIndex, final char ... pattern) {
      if (bytes == null)
        throw new NullPointerException("data == null");

      int index = fromIndex - 1;
      final IntArrayList indices = new IntArrayList();
      while ((index = indexOf(bytes, index + 1, pattern)) != -1)
        indices.add(index);

      return indices;
    }

    public static int indexOf(final byte[] bytes, final char ... pattern) {
      return indexOf(bytes, 0, pattern);
    }

    public static int indexOf(final byte[] bytes, final int fromIndex, final char ... pattern) {
      if (bytes == null)
        throw new NullPointerException("data == null");

      if (fromIndex < 0)
        throw new IndexOutOfBoundsException("fromIndex < 0");

      if (bytes.length <= fromIndex)
        throw new IndexOutOfBoundsException(bytes.length + " <= " + fromIndex);

      for (int i = fromIndex; i < bytes.length; i++)
        for (int j = 0; j < pattern.length; j++)
          if (bytes[i] == pattern[j])
            return i;

      return -1;
    }
  }

  public static void replaceAll(final byte[] bytes, final char target, final char replacement) {
    replaceAll(bytes, (byte)target, (byte)replacement);
  }

  public static void replaceAll(final byte[] bytes, final byte target, final char replacement) {
    replaceAll(bytes, target, (byte)replacement);
  }

  public static void replaceAll(final byte[] bytes, final char target, final byte replacement) {
    replaceAll(bytes, (byte)target, replacement);
  }

  public static void replaceAll(final byte[] bytes, final byte target, final byte replacement) {
    if (bytes == null)
      throw new NullPointerException("bytes == null");

    int index = 0;
    while ((index = Byte.indexOf(bytes, index + 1, target)) != -1)
      bytes[index] = replacement;
  }

  public static void replaceAll(final byte[] bytes, final byte[] target, final byte[] replacement) {
    if (bytes == null)
      throw new NullPointerException("bytes == null");

    if (target == null)
      throw new NullPointerException("target == null");

    if (replacement == null)
      throw new NullPointerException("replacement == null");

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

  public static void replaceAll(final byte[] bytes, final char[] target, final char[] replacement) {
    if (bytes == null)
      throw new NullPointerException("bytes == null");

    if (target == null)
      throw new NullPointerException("target == null");

    if (replacement == null)
      throw new NullPointerException("replacement == null");

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
      throw new NullPointerException("data == null");

    if (pattern == null)
      throw new NullPointerException("pattern == null");

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

  public static int indexOf(final byte[] bytes, final int fromIndex, final char[] ... pattern) {
    if (bytes == null)
      throw new NullPointerException("data == null");

    if (pattern == null)
      throw new NullPointerException("pattern == null");

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

  private static int[][] computeFailure(final char[] ... pattern) {
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

  public static void toBytes(final short data, final byte[] bytes, final int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      bytes[offset] = (byte)((data >> 8) & 0xff);
      bytes[offset + 1] = (byte)(data & 0xff);
    }
    else {
      bytes[offset] = (byte)(data & 0xff);
      bytes[offset + 1] = (byte)((data >> 8) & 0xff);
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

  public static void toBytes(final int data, final byte[] bytes, final int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      bytes[offset] = (byte)((data >> 24) & 0xff);
      bytes[offset + 1] = (byte)((data >> 16) & 0xff);
      bytes[offset + 2] = (byte)((data >> 8) & 0xff);
      bytes[offset + 3] = (byte)(data & 0xff);
    }
    else {
      bytes[offset] = (byte)(data & 0xff);
      bytes[offset + 1] = (byte)((data >> 8) & 0xff);
      bytes[offset + 2] = (byte)((data >> 16) & 0xff);
      bytes[offset + 3] = (byte)((data >> 24) & 0xff);
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

  public static void toBytes(final long data, final byte[] bytes, final int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      bytes[offset] = (byte)((data >> 56) & 0xff);
      bytes[offset + 1] = (byte)((data >> 48) & 0xff);
      bytes[offset + 2] = (byte)((data >> 40) & 0xff);
      bytes[offset + 3] = (byte)((data >> 32) & 0xff);
      bytes[offset + 4] = (byte)((data >> 24) & 0xff);
      bytes[offset + 5] = (byte)((data >> 16) & 0xff);
      bytes[offset + 6] = (byte)((data >> 8) & 0xff);
      bytes[offset + 7] = (byte)(data & 0xff);
    }
    else {
      bytes[offset] = (byte)(data & 0xff);
      bytes[offset + 1] = (byte)((data >> 8) & 0xff);
      bytes[offset + 2] = (byte)((data >> 16) & 0xff);
      bytes[offset + 3] = (byte)((data >> 24) & 0xff);
      bytes[offset + 4] = (byte)((data >> 32) & 0xff);
      bytes[offset + 5] = (byte)((data >> 40) & 0xff);
      bytes[offset + 6] = (byte)((data >> 48) & 0xff);
      bytes[offset + 7] = (byte)((data >> 56) & 0xff);
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

  public static void toBytes(final int byteLength, final long data, final byte[] bytes, final int offset, final boolean isBigEndian) {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)data, bytes, offset, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)data, bytes, offset, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(data, bytes, offset, isBigEndian);
  }

  public static void toBytes(final int byteLength, final long data, final ByteArrayOutputStream out, final boolean isBigEndian) {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)data, out, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)data, out, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(data, out, isBigEndian);
  }

  public static void toBytes(final int byteLength, final long data, final RandomAccessFile out, final boolean isBigEndian) throws IOException {
    if (byteLength == Short.SIZE / 8)
      toBytes((short)data, out, isBigEndian);
    else if (byteLength == Integer.SIZE / 8)
      toBytes((int)data, out, isBigEndian);
    else if (byteLength == Long.SIZE / 8)
      toBytes(data, out, isBigEndian);
  }

  /**
   * Build a Java short from a 2-byte signed binary representation. Depending on machine type, byte orders are Big Endian (AS/400, Unix, final System/390
   * byte-order) for signed binary representations, and Little Endian (final Intel 80/86 reversed byte-order) for signed binary representations.
   *
   * @exception IllegalArgumentException if the specified byte order is not recognized.
   */
  public static short toShort(final byte[] bytes, final int offset, final boolean isBigEndian) {
    return (short)toShort(bytes, offset, isBigEndian, true);
  }

  public static int toShort(final byte[] bytes, final int offset, final boolean isBigEndian, final boolean signed) {
    final int value = isBigEndian ? ((bytes[offset] & 0xff) << 8) | (bytes[offset + 1] & 0xff) : (bytes[offset] & 0xff) | ((bytes[offset + 1] & 0xff) << 8);
    return signed ? (short)value : value;
  }

  /**
   * Build a Java int from a 4-byte signed binary representation. Depending on machine type, byte orders are Big Endian (AS/400, Unix, final System/390
   * byte-order) for signed binary representations, and Little Endian (final Intel 80/86 reversed byte-order) for signed binary representations.
   *
   * @exception IllegalArgumentException if the specified byte order is not recognized.
   */
  public static int toInt(final byte[] bytes, final int offset, final boolean isBigEndian) {
    return (int)toInt(bytes, offset, isBigEndian, true);
  }

  public static long toInt(final byte[] bytes, final int offset, final boolean isBigEndian, final boolean signed) {
    final long value = isBigEndian ? ((bytes[offset] & 0xffl) << 24) | ((bytes[offset + 1] & 0xffl) << 16) | ((bytes[offset + 2] & 0xffl) << 8) | (bytes[offset + 3] & 0xffl) : (bytes[offset] & 0xffl) | ((bytes[offset + 1] & 0xffl) << 8) | ((bytes[offset + 2] & 0xffl) << 16) | ((bytes[offset + 3] & 0xffl) << 24);
    return signed ? (int)value : value;
  }

  /**
   * Build a Java long from an 8-byte signed binary representation. Depending on machine type, byte orders are Big Endian (AS/400, Unix, final System/390
   * byte-order) for signed binary representations, and Little Endian (final Intel 80/86 reversed byte-order) for signed binary representations.
   *
   * @exception IllegalArgumentException if the specified byte order is not recognized.
   */
  // FIXME: Support unsigned
  public static long toLong(final byte[] bytes, final int offset, final boolean isBigEndian) {
    return isBigEndian ? ((bytes[offset] & 0xffl) << 56) | ((bytes[offset + 1] & 0xffl) << 48) | ((bytes[offset + 2] & 0xffl) << 40) | ((bytes[offset + 3] & 0xffl) << 32) | ((bytes[offset + 4] & 0xffl) << 24) | ((bytes[offset + 5] & 0xffl) << 16) | ((bytes[offset + 6] & 0xffl) << 8) | (bytes[offset + 7] & 0xffl) : (bytes[offset] & 0xffl) | ((bytes[offset + 1] & 0xffl) << 8) | ((bytes[offset + 2] & 0xffl) << 16) | ((bytes[offset + 3] & 0xffl) << 24) | ((bytes[offset + 4] & 0xffl) << 32) | ((bytes[offset + 5] & 0xffl) << 40) | ((bytes[offset + 6] & 0xffl) << 48) | ((bytes[offset + 7] & 0xffl) << 56);
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
    String out = "";
    for (byte b : bytes)
      out += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');

    return bytes.length > 0 ? out.substring(1) : "";
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

  private Bytes() {
  }
}