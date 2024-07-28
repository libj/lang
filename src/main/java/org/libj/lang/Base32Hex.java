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

/**
 * Encodes and decodes Base32hex.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>
 */
public class Base32Hex extends Base32 {
  private static final String lower = "0123456789abcdefghijklmnopqrstuv=";
  private static final String upper = "0123456789ABCDEFGHIJKLMNOPQRSTUV=";

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with padding, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes) {
    return encode(bytes, 0, bytes.length, lower, upper, true, true);
  }

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with the provided {@code padding} argument, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param padding Whether to include padding.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final boolean padding) {
    return encode(bytes, 0, bytes.length, lower, upper, padding, true);
  }

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with the provided {@code padding} and {@code uppercase}
   * arguments.
   *
   * @param bytes The bytes to encode.
   * @param padding Whether to include padding.
   * @param uppercase Whether to encode with uppercase characters.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final boolean padding, final boolean uppercase) {
    return encode(bytes, 0, bytes.length, lower, upper, padding, uppercase);
  }

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with padding, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len) {
    return encode(bytes, offset, len, lower, upper, true, false);
  }

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with the provided {@code padding} argument, and in uppercase.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @param padding Whether to include padding.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len, final boolean padding) {
    return encode(bytes, offset, len, lower, upper, padding, false);
  }

  /**
   * Returns the base32hex encoding of the provided {@code bytes} array, with the provided {@code padding} and {@code uppercase}
   * arguments.
   *
   * @param bytes The bytes to encode.
   * @param offset The initial offset.
   * @param len The length.
   * @param padding Whether to include padding.
   * @param uppercase Whether to encode with uppercase characters.
   * @return The base32hex encoding of the provided {@code bytes} array.
   * @throws NullPointerException If {@code bytes} is null.
   */
  public static String encode(final byte[] bytes, final int offset, final int len, final boolean padding, final boolean uppercase) {
    return encode(bytes, offset, len, lower, upper, padding, uppercase);
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code base32hex} string.
   *
   * @param base32hex The base32hex string.
   * @return A {@code new byte[]} of the decoded {@code base32hex} string.
   * @throws NullPointerException If {@code base32hex} is null.
   */
  public static byte[] decode(final String base32hex) {
    return decode(base32hex, upper, true);
  }

  /**
   * Returns a {@code new byte[]} of the decoded {@code base32hex} string.
   *
   * @param base32hex The base32hex string.
   * @param padding Whether to consider padding when decoding.
   * @return A {@code new byte[]} of the decoded {@code base32hex} string.
   * @throws NullPointerException If {@code base32hex} is null.
   */
  public static byte[] decode(final String base32hex, final boolean padding) {
    return decode(base32hex, upper, padding);
  }

  Base32Hex() {
  }
}