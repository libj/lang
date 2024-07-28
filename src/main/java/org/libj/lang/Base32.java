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

import java.io.ByteArrayOutputStream;

/**
 * Encodes and decodes Base32.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>
 */
public class Base32 {
  private static final String lower = "abcdefghijklmnopqrstuvwxyz234567=";
  private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=";

  private static int blockLenToPadding(final int blocklen) {
    switch (blocklen) {
      case 1:
        return 6;

      case 2:
        return 4;

      case 3:
        return 3;

      case 4:
        return 1;

      case 5:
        return 0;

      default:
        return -1;
    }
  }

  private static int paddingToBlockLen(final int padlen) {
    switch (padlen) {
      case 6:
        return 1;

      case 4:
        return 2;

      case 3:
        return 3;

      case 1:
        return 4;

      case 0:
        return 5;

      default:
        return -1;
    }
  }

  static String encode(final byte[] data, final int offset, final int len, final String lower, final String upper, final boolean padding, final boolean uppercase) {
    final String alphabet = uppercase ? upper : lower;
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    for (int i = 0, i$ = (len + 4) / 5; i < i$; ++i) {
      final short s[] = new short[5];
      final int t[] = new int[8];
      int blocklen = 5;
      for (int j = 0; j < 5; ++j) {
        if ((i * 5 + j) < len) {
          s[j] = (short)(data[offset + i * 5 + j] & 0xFF);
        }
        else {
          s[j] = 0;
          --blocklen;
        }
      }

      final int padlen = blockLenToPadding(blocklen);
      // convert the 5 byte block into 8 characters (values 0-31).
      // upper 5 bits from first byte
      t[0] = (byte)((s[0] >> 3) & 0x1F);
      // lower 3 bits from 1st byte, upper 2 bits from 2nd.
      t[1] = (byte)(((s[0] & 0x07) << 2) | ((s[1] >> 6) & 0x03));
      // bits 5-1 from 2nd.
      t[2] = (byte)((s[1] >> 1) & 0x1F);
      // lower 1 bit from 2nd, upper 4 from 3rd
      t[3] = (byte)(((s[1] & 0x01) << 4) | ((s[2] >> 4) & 0x0F));
      // lower 4 from 3rd, upper 1 from 4th.
      t[4] = (byte)(((s[2] & 0x0F) << 1) | ((s[3] >> 7) & 0x01));
      // bits 6-2 from 4th
      t[5] = (byte)((s[3] >> 2) & 0x1F);
      // lower 2 from 4th, upper 3 from 5th;
      t[6] = (byte)(((s[3] & 0x03) << 3) | ((s[4] >> 5) & 0x07));
      // lower 5 from 5th;
      t[7] = (byte)(s[4] & 0x1F);
      // write out the actual characters.
      for (int j = 0, j$ = t.length - padlen; j < j$; ++j)
        os.write(alphabet.charAt(t[j]));

      // write out the padding (if any)
      if (padding)
        for (int j = t.length - padlen, j$ = t.length; j < j$; ++j)
          os.write('=');
    }

    return new String(os.toByteArray());
  }

  static byte[] decode(final String base32, final String alphabet, final boolean padding) {
    final byte[] bytes = base32.getBytes();
    final ByteArrayOutputStream bs = new ByteArrayOutputStream();
    for (int i = 0, i$ = bytes.length; i < i$; ++i) {
      final char c = (char)bytes[i];
      if (!Character.isWhitespace(c))
        bs.write((byte)Character.toUpperCase(c));
    }

    if (!padding) {
      while (bs.size() % 8 != 0)
        bs.write('=');
    }
    else if (bs.size() % 8 != 0) {
      return null;
    }

    final byte[] in = bs.toByteArray();
    bs.reset();
    for (int i = 0, i$ = in.length / 8; i < i$; ++i) {
      final short[] s = new short[8];
      final int[] t = new int[5];
      int padlen = 8;
      for (int j = 0; j < 8; ++j) {
        final char c = (char)in[i * 8 + j];
        if (c == '=')
          break;

        s[j] = (short)alphabet.indexOf(in[i * 8 + j]);
        if (s[j] < 0)
          return null;

        --padlen;
      }

      final int blocklen = paddingToBlockLen(padlen);
      if (blocklen < 0)
        return null;

      // all 5 bits of 1st, high 3 (of 5) of 2nd
      t[0] = (s[0] << 3) | s[1] >> 2;
      // lower 2 of 2nd, all 5 of 3rd, high 1 of 4th
      t[1] = ((s[1] & 0x03) << 6) | (s[2] << 1) | (s[3] >> 4);
      // lower 4 of 4th, high 4 of 5th
      t[2] = ((s[3] & 0x0F) << 4) | ((s[4] >> 1) & 0x0F);
      // lower 1 of 5th, all 5 of 6th, high 2 of 7th
      t[3] = (s[4] << 7) | (s[5] << 2) | (s[6] >> 3);
      // lower 3 of 7th, all of 8th
      t[4] = ((s[6] & 0x07) << 5) | s[7];
      for (int j = 0; j < blocklen; ++j)
        bs.write((byte)(t[j] & 0xFF));
    }

    return bs.toByteArray();
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with padding, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes) {
    return encode(bytes, 0, bytes.length, lower, upper, true, true);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with the provided {@code padding} argument, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param padding Whether to include padding.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final boolean padding) {
    return encode(bytes, 0, bytes.length, lower, upper, padding, true);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with the provided {@code padding} and {@code uppercase}
   * arguments.
   *
   * @param bytes The bytes to encode.
   * @param padding Whether to include padding.
   * @param uppercase Whether to encode with uppercase characters.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final boolean padding, final boolean uppercase) {
    return encode(bytes, 0, bytes.length, lower, upper, padding, uppercase);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with padding, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len) {
    return encode(bytes, offset, len, lower, upper, true, false);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with the provided {@code padding} argument, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @param padding Whether to include padding.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len, final boolean padding) {
    return encode(bytes, offset, len, lower, upper, padding, false);
  }

  /**
   * Returns the base32 encoding of the provided {@code bytes} array, with the provided {@code padding} and {@code uppercase}
   * arguments.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @param padding Whether to include padding.
   * @param uppercase Whether to encode with uppercase characters.
   * @return The base32 encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len, final boolean padding, final boolean uppercase) {
    return encode(bytes, offset, len, lower, upper, padding, uppercase);
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code base32} string.
   *
   * @param base32 The base32 string.
   * @return A {@code new byte[]} of the decoded {@code base32} string.
   * @throws NullPointerException If {@code base32} is null.
   */
  public static byte[] decode(final String base32) {
    return decode(base32, upper, true);
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code base32} string.
   *
   * @param base32 The base32 string.
   * @param padding Whether to consider padding when decoding.
   * @return A {@code new byte[]} of the decoded {@code base32} string.
   * @throws NullPointerException If {@code base32} is null.
   */
  public static byte[] decode(final String base32, final boolean padding) {
    return decode(base32, upper, padding);
  }

  Base32() {
  }
}