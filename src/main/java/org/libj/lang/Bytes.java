/* Copyright (c) 2012 LibJ
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

package org.libj.lang;

/**
 * Functions implementing common operations on {@code byte[]} references.
 */
public final class Bytes {
  /**
   * Returns the index of the first occurrence of the specified byte
   * {@code sequence} in {@code bytes}.
   *
   * @param bytes The {@code byte} array in which to search.
   * @param sequence The {@code byte} array sequence for which to search.
   * @return The index of the first occurrence of the specified byte
   *         {@code sequence} in {@code bytes}.
   */
  public static int indexOf(final byte[] bytes, final byte ... sequence) {
    return bytes.length == 0 ? -1 : indexOf(bytes, 0, sequence);
  }

  /**
   * Returns the index of the first occurrence (starting the search from the
   * {@code offset} index) of the specified byte {@code sequence} in
   * {@code bytes}.
   *
   * @param bytes The {@code byte} array in which to search.
   * @param offset The index to start the search from.
   * @param sequence The {@code byte} array sequence for which to search.
   * @return The index of the first occurrence of the specified byte
   *         {@code sequence} in {@code bytes}.
   * @throws ArrayIndexOutOfBoundsException If {@code offset} is out of range.
   * @throws NullPointerException If {@code bytes} or {@code sequence} is null.
   */
  public static int indexOf(final byte[] bytes, final int offset, final byte ... sequence) {
    Assertions.assertRangeArray(offset, bytes.length);
    if (sequence.length == 0 || bytes.length < sequence.length)
      return -1;

    for (int i = offset; i < bytes.length; ++i) {
      if (bytes[i] == sequence[0]) {
        boolean match = true;
        for (int j = 0; j < sequence.length && (match = bytes.length > i + j && sequence[j] == bytes[i + j]); ++j);
        if (match)
          return i;
      }
    }

    return -1;
  }

  /**
   * Replace {@code target} with {@code replacement} in {@code bytes}.
   *
   * @param bytes The {@code byte} array in which to perform the replacement.
   * @param target The {@code byte} to search for.
   * @param replacement The {@code byte} to replace with.
   */
  public static void replaceAll(final byte[] bytes, final byte target, final byte replacement) {
    for (int index = 0; (index = Bytes.indexOf(bytes, index + 1, target)) > -1; bytes[index] = replacement);
  }

  /**
   * Replace {@code target}with {@code replacement} in {@code bytes}.
   *
   * @param bytes The {@code byte} array in which to perform the replacement.
   * @param target The {@code byte} array to search for.
   * @param replacement The {@code byte} array to replace with.
   * @throws IllegalArgumentException If the length of {@code target} does not
   *           equal the length of {@code replacement}.
   */
  public static void replaceAll(final byte[] bytes, final byte[] target, final byte[] replacement) {
    if (target.length != replacement.length)
      throw new IllegalArgumentException("target.length != replacement.length");

    if (bytes.length < target.length || target.length == 0)
      return;

    if (target.length == 1) {
      replaceAll(bytes, target[0], replacement[0]);
      return;
    }

    for (int index = -1; (index = indexOf(bytes, index + 1, target)) > -1; System.arraycopy(replacement, 0, bytes, index, replacement.length));
  }

  /**
   * Create a byte array representation of a short value with big- or little-
   * endian encoding.
   * <p>
   * A Java short is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte, the
   * byte sequence is shifted such that the least significant bytes are skipped
   * first.
   *
   * @param value The {@code short} value.
   * @param bytes The destination {@code byte[]} array.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   */
  public static void toBytes(final short value, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(value & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((value >> 8) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(value & 0xff);
      bytes[offset] = (byte)((value >> 8) & 0xff);
    }
  }

  /**
   * Create a byte array representation of a char value with big- or little-
   * endian encoding.
   * <p>
   * A Java char is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte, the
   * byte sequence is shifted such that the least significant bytes are skipped
   * first.
   *
   * @param c The char value.
   * @param bytes The destination {@code byte[]} array.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   */
  public static void toBytes(final char c, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(c & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((c >> 8) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(c & 0xff);
      bytes[offset] = (byte)((c >> 8) & 0xff);
    }
  }

  /**
   * Create a byte array representation of a int value with big- or little-
   * endian encoding.
   * <p>
   * A Java int is 4 bytes in size. If the byte array is shorter than 4 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte, the
   * byte sequence is shifted such that the least significant bytes are skipped
   * first.
   *
   * @param i The int value.
   * @param bytes The destination {@code byte[]} array.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   */
  public static void toBytes(final int i, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(i & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((i >> 8) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((i >> 16) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((i >> 24) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(i & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((i >> 8) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((i >> 16) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset] = (byte)((i >> 24) & 0xff);
    }
  }

  /**
   * Create a byte array representation of a long value with big- or little-
   * endian encoding.
   * <p>
   * A Java long is 8 bytes in size. If the byte array is shorter than 8 bytes
   * minus the offset, the missing bytes are skipped. For each missing byte, the
   * byte sequence is shifted such that the least significant bytes are skipped
   * first.
   *
   * @param l The long value.
   * @param bytes The destination {@code byte[]} array.
   * @param offset The byte offset into the destination array.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   */
  public static void toBytes(final long l, final byte[] bytes, int offset, final boolean isBigEndian) {
    if (isBigEndian) {
      offset = bytes.length - offset;
      bytes[--offset] = (byte)(l & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 8) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 16) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 24) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 32) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 40) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 48) & 0xff);
      if (offset == 0)
        return;

      bytes[--offset] = (byte)((l >> 56) & 0xff);
    }
    else {
      bytes[offset++] = (byte)(l & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 8) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 16) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 24) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 32) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 40) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset++] = (byte)((l >> 48) & 0xff);
      if (offset == bytes.length)
        return;

      bytes[offset] = (byte)((l >> 56) & 0xff);
    }
  }

  /**
   * Create a signed short representation of a source byte array with big- or
   * little-endian encoding.
   * <p>
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
   * <p>
   * A Java short is 2 bytes in size. If the byte array is shorter than 2 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @param signed If {@code true}, return signed short value. If {@code false},
   *          return unsigned int value.
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
   * <p>
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
   * <p>
   * A Java int is 4 bytes in size. If the byte array is shorter than 4 bytes
   * minus the offset, the missing bytes are considered as the equivalent of
   * 0x0.
   *
   * @param src The source byte array.
   * @param offset The byte offset into the source byte array.
   * @param isBigEndian Is value in big-endian encoding.
   * @param signed If {@code true}, return signed int value. If {@code false},
   *          return unsigned long value.
   * @return A signed int or an unsigned long representation of a byte array.
   */
  public static long toInt(final byte[] src, int offset, final boolean isBigEndian, final boolean signed) {
    long value = 0;
    if (isBigEndian) {
      offset = src.length - offset;
      value |= (src[--offset] & 0xffL);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 8);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 16);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 24);
    }
    else {
      value |= (src[offset++] & 0xffL);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 8);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 16);
      if (offset == src.length)
        return value;

      value |= ((src[offset] & 0xffL) << 24);
    }

    return signed ? (int)value : value;
  }

  /**
   * Create a signed long representation of a source byte array with big- or
   * little-endian encoding.
   * <p>
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
      value |= (src[--offset] & 0xffL);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 8);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 16);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 24);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 32);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 40);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 48);
      if (offset == 0)
        return value;

      value |= ((src[--offset] & 0xffL) << 56);
    }
    else {
      value |= (src[offset++] & 0xffL);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 8);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 16);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 24);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 32);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 40);
      if (offset == src.length)
        return value;

      value |= ((src[offset++] & 0xffL) << 48);
      if (offset == src.length)
        return value;

      value |= ((src[offset] & 0xffL) << 56);
    }

    return value;
  }

  /**
   * Returns a {@code short} representing the base-8 value of the specified
   * {@code byte}.
   *
   * @param b The {@code byte}.
   * @return A {@code short} representing the base-8 value of the specified
   *         {@code byte}.
   */
  public static short toOctal(byte b) {
    short value = 0;
    for (int i = 0; b != 0; ++i) {
      final int remainder = b % 8;
      b /= 8;
      value += remainder * StrictMath.pow(10, i);
    }

    return value;
  }

  /**
   * Returns a {@code short} array representing the base-8 values of the
   * specified {@code byte} array.
   *
   * @param bytes The {@code byte} array.
   * @return A {@code short} array representing the base-8 values of the
   *         specified {@code byte} array.
   */
  public static short[] toOctal(final byte ... bytes) {
    final short[] octal = new short[bytes.length];
    for (int i = 0; i < bytes.length; ++i)
      octal[i] = toOctal(bytes[i]);

    return octal;
  }

  /**
   * Write a number of bits from a source byte to a destination byte array at an
   * offset. The bits are counted from right to left (least significant to most
   * significant, as per big-endian encoding). The offset is counted left to
   * right (most significant to least significant, as per big-endian encoding).
   * <p>
   * Examples:
   * <p>
   * If {@code src=0b00011101}, writing {@code bits=3} at {@code offset=1} will
   * result in: {@code 0b01010000}.
   * <p>
   * If {@code src=0b00011101}, writing {@code bits=5} at {@code offset=7} will
   * result in: {@code [0b00000001, 0b11010000]}
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to begin
   *          writing.
   * @param src The source byte to write.
   * @param bits The number of bits of the byte to write (0 to 8).
   * @return The new offset adjusted by the written bits.
   */
  public static int writeBitsB(final byte[] dest, final int offset, byte src, final byte bits) {
    final int i = offset / 8;
    final int right = offset % 8;
    final int left = 8 - bits;
    src <<= left;
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
   * Write a number of bits from a source byte array to a destination byte array
   * at an offset. The bits are counted from right to left (least significant to
   * most significant, as per big-endian encoding). The offset is counted left
   * to right (most significant to least significant, as per big-endian
   * encoding). If {@code bits > 8}, the starting bit to be read is the least
   * significant bit in the {@code bits % 8} position in the source array.
   * <p>
   * Examples:
   * <p>
   * If {@code src=[0b01011011, 0b01101101]}, writing {@code bits=9} at
   * {@code offset=7} will result in: {@code [0b00000001, 0b01101101]}
   * <p>
   * If {@code src=[0b01011011, 0b01101101]}, writing {@code bits=13} at
   * {@code offset=3} will result in: {@code [0b00000011, (byte)0b01101101]}
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to begin
   *          writing.
   * @param src The source byte array to write.
   * @param bits The number of bits to write from the source array (0 to 8 *
   *          src.length).
   * @return The new offset adjusted by the written bits.
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
   * Write a number of bits from a source byte to a destination byte array at an
   * offset. The bits are counted from right to left (least significant to most
   * significant, as per little-endian encoding). The offset is counted left to
   * right (least significant to most significant, as per little-endian
   * encoding).
   * <p>
   * Examples:
   * <p>
   * If {@code src=0b11101000}, writing {@code bits=3} at {@code offset=1} will
   * result in: {@code 0b01110000}.
   * <p>
   * If {@code src=0b11101000}, writing {@code bits=5} at {@code offset=7} will
   * result in: {@code [0b00000001, 0b11010000]}
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to begin
   *          writing.
   * @param src The source byte to write.
   * @param bits The number of bits of the byte to write (0 to 8).
   * @return The new offset adjusted by the written bits.
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
   * Write a number of bits from a source byte array to a destination byte array
   * at an offset. The bits are counted from left to right (least significant to
   * most significant, as per little-endian encoding). The offset is counted
   * left to right (least significant to most significant, as per little-endian
   * encoding).
   * <p>
   * Examples:
   * <p>
   * If {@code src=[0b01011011, 0b01101101]}, writing {@code bits=10} at
   * {@code offset=5} will result in: {@code [0b000000010, 0b11011010]}
   * <p>
   * If {@code src=[0b01011011, 0b01101101]}, writing {@code bits=13} at
   * {@code offset=3} will result in: {@code [0b00001011, (byte)0b01101101]}
   *
   * @param dest The destination byte array.
   * @param offset The bit offset into the destination byte array where to begin
   *          writing.
   * @param src The source byte array to write.
   * @param bits The number of bits to write from the source array (0 to 8 *
   *          src.length).
   * @return The new offset adjusted by the written bits.
   */
  public static int writeBitsL(final byte[] dest, int offset, final byte[] src, int bits) {
    int i = 0;
    for (; bits > 8; bits -= 8)
      offset = writeBitsL(dest, offset, src[i++], (byte)8);

    return writeBitsL(dest, offset, src[i], (byte)(1 + (bits - 1) % 8));
  }

  /**
   * Returns the byte representation from reading a number of bits (0 to 8) from
   * a source byte array at an offset, read in the direction of most significant
   * bit to least significant bit. This method returns the value of the read
   * byte as shifted to fill the least significant bits first, allowing the
   * front of the byte to encode a value as if the bits were in the tail of the
   * byte. Java uses big-endian encoding, placing the most significant bits at
   * the front of the byte.
   *
   * @param src The source byte array.
   * @param offset The offset in bits.
   * @param bits The number of bits to read (0 to 8).
   * @return The byte representation of the read bits from the source byte array
   *         at the offset.
   */
  public static byte readBitsFromByte(final byte[] src, final int offset, byte bits) {
    final int i = offset / 8;
    final int left = offset % 8;
    bits = (byte)(8 - bits);
    final byte dest = (byte)((src[i] << left & 0xff) >> bits);
    return left <= bits ? dest : (byte)(dest | (src[i + 1] & 0xff) >> 8 + bits - left);
  }

  /**
   * Returns the byte array representation from reading a number of bits from a
   * source byte array at an offset, read in the direction of most significant
   * bit to least significant bit. This method returns the value of the read
   * byte as shifted to fill the least significant bits first, allowing the
   * front of the byte to encode a value as if the bits were in the tail of the
   * byte. Java uses big-endian encoding, placing the most significant bits at
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
    return (byte)(1 + StrictMath.log(value) / StrictMath.log(2));
  }

  /**
   * Get the number of bits necessary to store a value.
   *
   * @param value The value.
   * @return The number of bits necessary to store a value.
   */
  public static byte getSize(final long value) {
    return (byte)(1 + StrictMath.log(value) / StrictMath.log(2));
  }

  private Bytes() {
  }
}