/* Copyright (c) 2018 LibJ
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

import java.util.Arrays;

/**
 * Encodes and decodes Base32.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>
 */
public class Base32 extends DataEncoding<byte[],String> {
  private static final long serialVersionUID = 3283110401340470031L;
  private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
  private static final int[] lookup = {
    0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
    0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
    0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
    0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
    0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
    0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
    0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
    0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
    0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
    0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
  };

  /**
   * Returns the base32 encoding of the provided {@code bytes} array.
   *
   * @param bytes The bytes to encode.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes) {
    return encode(bytes, 0, bytes.length);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len) {
    final StringBuilder base32 = new StringBuilder((bytes.length + 7) * 8 / 5);
    for (int i = offset, index = 0, digit, by0, by1; i < len + offset;) {
      by0 = (bytes[i] >= 0) ? bytes[i] : (bytes[i] + 256);
      if (index > 3) {
        by1 = i + 1 < bytes.length ? bytes[i + 1] < 0 ? bytes[i + 1] + 256 : bytes[i + 1] : 0;
        digit = by0 & (0xFF >> index);
        index = (index + 5) % 8;
        digit <<= index;
        digit |= by1 >> (8 - index);
        ++i;
      }
      else {
        digit = (by0 >> (8 - (index + 5))) & 0x1F;
        index = (index + 5) % 8;
        if (index == 0)
          ++i;
      }

      base32.append(alphabet.charAt(digit));
    }

    return base32.toString();
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code base32} string.
   *
   * @param base32 The base32 string.
   * @return A {@code new byte[]} of the decoded {@code base32} string.
   * @throws NullPointerException If {@code base32} is null.
   */
  public static byte[] decode(final String base32) {
    final byte[] bytes = new byte[base32.length() * 5 / 8];
    decode(base32, bytes, 0);
    return bytes;
  }

  /**
   * Decode the {@code base32} string into the provided {@code bytes} array.
   *
   * @param base32 The base32 string.
   * @param bytes The {@code bytes} array.
   * @param offset The offset into the {@code bytes} array.
   * @throws ArrayIndexOutOfBoundsException If the size of {@code bytes} is not
   *           big enough, or if {@code offset} causes the index to go out of
   *           bounds.
   * @throws NullPointerException If {@code base32} or {@code bytes} is null.
   */
  public static void decode(final String base32, final byte[] bytes, int offset) {
    for (int i = 0, len = base32.length(), index = 0, ch, digit; i < len; ++i) {
      ch = base32.charAt(i) - '0';
      if (ch < 0 || lookup.length < ch)
        continue;

      digit = lookup[ch];
      if (digit == 0xFF)
        continue;

      if (index <= 3) {
        index = (index + 5) % 8;
        if (index == 0) {
          bytes[offset] |= digit;
          if (++offset >= bytes.length)
            break;
        }
        else {
          bytes[offset] |= digit << (8 - index);
        }
      }
      else {
        index = (index + 5) % 8;
        bytes[offset] |= (digit >>> index);
        if (++offset >= bytes.length)
          break;

        bytes[offset] |= digit << (8 - index);
      }
    }
  }

  /**
   * Create a new {@link Base32} object with the provided raw bytes.
   *
   * @param bytes The raw bytes.
   */
  public Base32(final byte[] bytes) {
    super(bytes, null);
  }

  /**
   * Create a new {@link Base32} object with the provided base32-encoded string
   * value.
   *
   * @param base32 The base32-encoded string value.
   */
  public Base32(final String base32) {
    super(null, base32);
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

    if (!(obj instanceof Base32))
      return false;

    final Base32 that = (Base32)obj;
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