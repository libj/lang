/* Copyright (c) 2009 LibJ
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

import static org.libj.lang.Assertions.*;

import java.util.Arrays;

/**
 * Encodes and decodes Hexadecimal.
 */
public class Hexadecimal extends DataEncoding<byte[],String> {
  private static final char[] hexChar = {
    '0', '1', '2', '3',
    '4', '5', '6', '7',
    '8', '9', 'a', 'b',
    'c', 'd', 'e', 'f'
  };

  private static int charToNibble(final char ch) {
    if ('0' <= ch && ch <= '9')
      return ch - '0';

    if ('a' <= ch && ch <= 'f')
      return ch - 'a' + 0xa;

    if ('A' <= ch && ch <= 'F')
      return ch - 'A' + 0xa;

    throw new IllegalArgumentException("Illegal hexadecimal character: " + ch);
  }

  /**
   * Returns the hex encoding of the provided {@code bytes} array.
   *
   * @param bytes The bytes to encode.
   * @return The hex encoding of the provided {@code bytes} array.
   * @throws IllegalArgumentException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes) {
    return encode(bytes, 0, assertNotNull(bytes).length);
  }

  /**
   * Returns the hexadecimal encoding of the provided {@code bytes} array.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @return The hexadecimal encoding of the provided {@code bytes} array.
   * @throws IllegalArgumentException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len) {
    assertNotNull(bytes);
    final StringBuilder builder = new StringBuilder(len * 2);
    for (int i = offset; i < len + offset; ++i) { // [N]
      builder.append(hexChar[(bytes[i] & 0xf0) >>> 4]);
      builder.append(hexChar[bytes[i] & 0x0f]);
    }

    return builder.toString();
  }

  private static void decode0(final String hex, final byte[] bytes, final int offset) {
    for (int i = 0, len = hex.length(), j = offset; i < len; i += 2, ++j) { // [N]
      final int high = charToNibble(hex.charAt(i));
      final int low = charToNibble(hex.charAt(i + 1));
      bytes[j] = (byte)((high << 4) | low);
    }
  }

  /**
   * Decode the {@code hex} string into the provided {@code bytes} array.
   *
   * @param hex The hex string.
   * @param bytes The {@code bytes} array.
   * @param offset The offset into the {@code bytes} array.
   * @throws ArrayIndexOutOfBoundsException If the size of {@code bytes} is not big enough, or if {@code offset} causes the index to
   *           go out of bounds.
   * @throws IllegalArgumentException If {@code hex} or {@code bytes} is null.
   */
  public static void decode(final String hex, final byte[] bytes, final int offset) {
    assertNotNull(hex);
    assertNotNull(bytes);
    final int length = hex.length();
    if (length == 0)
      return;

    if (hex.length() % 2 != 0)
      throw new IllegalArgumentException("Odd hex length: " + hex.length());

    decode0(hex, bytes, offset);
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code hex} string.
   *
   * @param hex The hex string.
   * @return A {@code new byte[]} of the decoded {@code hex} string.
   * @throws IllegalArgumentException If {@code hex} is null.
   */
  public static byte[] decode(final String hex) {
    final int length = assertNotNull(hex).length();
    if (length == 0)
      return new byte[0];

    final byte[] bytes = new byte[length / 2];
    decode(hex, bytes, 0);
    return bytes;
  }

  /**
   * Create a new {@link Hexadecimal} object with the provided raw bytes.
   *
   * @param bytes The raw bytes.
   */
  public Hexadecimal(final byte[] bytes) {
    super(bytes, null);
  }

  /**
   * Create a new {@link Hexadecimal} object with the provided hex-encoded string value.
   *
   * @param hex The hex-encoded string value.
   */
  public Hexadecimal(final String hex) {
    super(null, hex);
  }

  @Override
  public byte[] getData() {
    return data == null ? data = decode(encoded) : data;
  }

  @Override
  public String getEncoded() {
    return encoded == null ? encoded = encode(data) : encoded;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Hexadecimal))
      return false;

    final Hexadecimal that = (Hexadecimal)obj;
    return encoded != null && that.encoded != null ? encoded.equalsIgnoreCase(that.encoded) : Arrays.equals(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return 31 + Arrays.hashCode(getData());
  }

  @Override
  public String toString() {
    return getEncoded();
  }
}