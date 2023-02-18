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
 * This utility class implements functions to {@code #set(...)}, {@code #clear(...)}, and {@code #get(...)} bit values from an
 * argument {@code byte[]}.
 */
public final class Buffers {
  /**
   * Sets the bit at the specified index in {@code buf} to {@code 1}. If the bit index is greater than the number of bits in the
   * {@code buf} array, the array will be resized based on the {@code resize} argument.
   * <p>
   * The {@code resize} argument behaves by the following rules:
   * <ul>
   * <li>If {@code resize == 0}, do not resize the array. An {@link ArrayIndexOutOfBoundsException} will be thrown.</li>
   * <li>If {@code resize > 0}, set the size of the array to the value of the index of the byte that {@code bit} belongs to, plus 1,
   * multiplied by {@code resize}:
   *
   * <pre>
   * {@code
   * (int)(((bit / 8) + 1) * resize)
   * }
   * </pre>
   *
   * <li>If {@code resize < 0}, set the size of the array to the value of the index of the byte that {@code bit} belongs to, minus
   * {@code (int)resize}:
   *
   * <pre>
   * {@code
   * (bit / 8) - (int)resize
   * }
   * </pre>
   * </ul>
   *
   * @param buf The {@code byte[]} buffer.
   * @param bit The index of the bit to be set.
   * @param resize The resize factor.
   * @return The provided {@code byte[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative, or if {@code resize == 0}. and the bit index is
   *           greater than the number of bits in the {@code buf} array.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static byte[] set(byte[] buf, final int bit, final double resize) {
    final int i = bit / Byte.SIZE;
    if (i >= buf.length) {
      if (resize == 0)
        throw new ArrayIndexOutOfBoundsException(bit);

      final byte[] resized = new byte[resize < 0 ? i - (int)resize : (int)((i + 1) * resize)];
      System.arraycopy(buf, 0, resized, 0, buf.length);
      buf = resized;
    }

    buf[i] |= 1 << (bit % Byte.SIZE);
    return buf;
  }

  /**
   * Sets the bit at the specified index in {@code buf} to {@code 1}. If the bit index is greater than the number of bits in the
   * {@code buf} array, the array will be resized based on the {@code resize} argument.
   * <p>
   * The {@code resize} argument behaves by the following rules:
   * <ul>
   * <li>If {@code resize == 0}, do not resize the array. An {@link ArrayIndexOutOfBoundsException} will be thrown.</li>
   * <li>If {@code resize > 0}, set the size of the array to the value of the index of the char that {@code bit} belongs to, plus 1,
   * multiplied by {@code resize}:
   *
   * <pre>
   * {@code
   * (int)(((bit / 8) + 1) * resize)
   * }
   * </pre>
   *
   * <li>If {@code resize < 0}, set the size of the array to the value of the index of the char that {@code bit} belongs to, minus
   * {@code (int)resize}:
   *
   * <pre>
   * {@code
   * (bit / 8) - (int)resize
   * }
   * </pre>
   * </ul>
   *
   * @param buf The {@code char[]} buffer.
   * @param bit The index of the bit to be set.
   * @param resize The resize factor.
   * @return The provided {@code char[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative, or if {@code resize == 0}. and the bit index is
   *           greater than the number of bits in the {@code buf} array.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static char[] set(char[] buf, final int bit, final double resize) {
    final int i = bit / Character.SIZE;
    if (i >= buf.length) {
      if (resize == 0)
        throw new ArrayIndexOutOfBoundsException(bit);

      final char[] resized = new char[resize < 0 ? i - (int)resize : (int)((i + 1) * resize)];
      System.arraycopy(buf, 0, resized, 0, buf.length);
      buf = resized;
    }

    buf[i] |= 1 << (bit % Character.SIZE);
    return buf;
  }

  /**
   * Sets the bit at the specified index in {@code buf} to {@code 1}. If the bit index is greater than the number of bits in the
   * {@code buf} array, the array will be resized based on the {@code resize} argument.
   * <p>
   * The {@code resize} argument behaves by the following rules:
   * <ul>
   * <li>If {@code resize == 0}, do not resize the array. An {@link ArrayIndexOutOfBoundsException} will be thrown.</li>
   * <li>If {@code resize > 0}, set the size of the array to the value of the index of the short that {@code bit} belongs to, plus
   * 1, multiplied by {@code resize}:
   *
   * <pre>
   * {@code
   * (int)(((bit / 8) + 1) * resize)
   * }
   * </pre>
   *
   * <li>If {@code resize < 0}, set the size of the array to the value of the index of the short that {@code bit} belongs to, minus
   * {@code (int)resize}:
   *
   * <pre>
   * {@code
   * (bit / 8) - (int)resize
   * }
   * </pre>
   * </ul>
   *
   * @param buf The {@code short[]} buffer.
   * @param bit The index of the bit to be set.
   * @param resize The resize factor.
   * @return The provided {@code short[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative, or if {@code resize == 0}. and the bit index is
   *           greater than the number of bits in the {@code buf} array.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static short[] set(short[] buf, final int bit, final double resize) {
    final int i = bit / Short.SIZE;
    if (i >= buf.length) {
      if (resize == 0)
        throw new ArrayIndexOutOfBoundsException(bit);

      final short[] resized = new short[resize < 0 ? i - (int)resize : (int)((i + 1) * resize)];
      System.arraycopy(buf, 0, resized, 0, buf.length);
      buf = resized;
    }

    buf[i] |= 1 << (bit % Short.SIZE);
    return buf;
  }

  /**
   * Sets the bit at the specified index in {@code buf} to {@code 1}. If the bit index is greater than the number of bits in the
   * {@code buf} array, the array will be resized based on the {@code resize} argument.
   * <p>
   * The {@code resize} argument behaves by the following rules:
   * <ul>
   * <li>If {@code resize == 0}, do not resize the array. An {@link ArrayIndexOutOfBoundsException} will be thrown.</li>
   * <li>If {@code resize > 0}, set the size of the array to the value of the index of the int that {@code bit} belongs to, plus 1,
   * multiplied by {@code resize}:
   *
   * <pre>
   * {@code
   * (int)(((bit / 8) + 1) * resize)
   * }
   * </pre>
   *
   * <li>If {@code resize < 0}, set the size of the array to the value of the index of the int that {@code bit} belongs to, minus
   * {@code (int)resize}:
   *
   * <pre>
   * {@code
   * (bit / 8) - (int)resize
   * }
   * </pre>
   * </ul>
   *
   * @param buf The {@code int[]} buffer.
   * @param bit The index of the bit to be set.
   * @param resize The resize factor.
   * @return The provided {@code int[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative, or if {@code resize == 0}. and the bit index is
   *           greater than the number of bits in the {@code buf} array.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int[] set(int[] buf, final int bit, final double resize) {
    final int i = bit / Integer.SIZE;
    if (i >= buf.length) {
      if (resize == 0)
        throw new ArrayIndexOutOfBoundsException(bit);

      final int[] resized = new int[resize < 0 ? i - (int)resize : (int)((i + 1) * resize)];
      System.arraycopy(buf, 0, resized, 0, buf.length);
      buf = resized;
    }

    buf[i] |= 1 << (bit % Integer.SIZE);
    return buf;
  }

  /**
   * Sets the bit at the specified index in {@code buf} to {@code 1}. If the bit index is greater than the number of bits in the
   * {@code buf} array, the array will be resized based on the {@code resize} argument.
   * <p>
   * The {@code resize} argument behaves by the following rules:
   * <ul>
   * <li>If {@code resize == 0}, do not resize the array. An {@link ArrayIndexOutOfBoundsException} will be thrown.</li>
   * <li>If {@code resize > 0}, set the size of the array to the value of the index of the long that {@code bit} belongs to, plus 1,
   * multiplied by {@code resize}:
   *
   * <pre>
   * {@code
   * (int)(((bit / 64) + 1) * resize)
   * }
   * </pre>
   *
   * <li>If {@code resize < 0}, set the size of the array to the value of the index of the long that {@code bit} belongs to, minus
   * {@code (int)resize}:
   *
   * <pre>
   * {@code
   * (bit / 64) - (int)resize
   * }
   * </pre>
   * </ul>
   *
   * @param buf The {@code long[]} buffer.
   * @param bit The index of the bit to be set.
   * @param resize The resize factor.
   * @return The provided {@code long[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative, or if {@code resize == 0}. and the bit index is
   *           greater than the number of bits in the {@code buf} array.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static long[] set(long[] buf, final int bit, final double resize) {
    final int i = bit / Long.SIZE;
    if (i >= buf.length) {
      if (resize == 0)
        throw new ArrayIndexOutOfBoundsException(bit);

      final long[] resized = new long[resize < 0 ? i - (int)resize : (int)((i + 1) * resize)];
      System.arraycopy(buf, 0, resized, 0, buf.length);
      buf = resized;
    }

    buf[i] |= 1L << (bit % Long.SIZE);
    return buf;
  }

  /**
   * Sets the bit in {@code buf} specified by the {@code bit} index to {@code false}.
   *
   * @param buf The {@code byte[]} buffer in which to clear the bit.
   * @param bit The index of the bit to be cleared.
   * @return The provided {@code byte[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static byte[] clear(final byte[] buf, final int bit) {
    final int i = bit / Byte.SIZE;
    if (i >= buf.length)
      return buf;

    buf[bit / Byte.SIZE] &= ~(1 << (bit % Byte.SIZE));
    return buf;
  }

  /**
   * Sets the bit in {@code buf} specified by the {@code bit} index to {@code false}.
   *
   * @param buf The {@code char[]} buffer in which to clear the bit.
   * @param bit The index of the bit to be cleared.
   * @return The provided {@code char[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static char[] clear(final char[] buf, final int bit) {
    final int i = bit / Character.SIZE;
    if (i >= buf.length)
      return buf;

    buf[bit / Character.SIZE] &= ~(1 << (bit % Character.SIZE));
    return buf;
  }

  /**
   * Sets the bit in {@code buf} specified by the {@code bit} index to {@code false}.
   *
   * @param buf The {@code short[]} buffer in which to clear the bit.
   * @param bit The index of the bit to be cleared.
   * @return The provided {@code short[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static short[] clear(final short[] buf, final int bit) {
    final int i = bit / Short.SIZE;
    if (i >= buf.length)
      return buf;

    buf[bit / Short.SIZE] &= ~(1 << (bit % Short.SIZE));
    return buf;
  }

  /**
   * Sets the bit in {@code buf} specified by the {@code bit} index to {@code false}.
   *
   * @param buf The {@code int[]} buffer in which to clear the bit.
   * @param bit The index of the bit to be cleared.
   * @return The provided {@code int[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int[] clear(final int[] buf, final int bit) {
    final int i = bit / Integer.SIZE;
    if (i >= buf.length)
      return buf;

    buf[bit / Integer.SIZE] &= ~(1 << (bit % Integer.SIZE));
    return buf;
  }

  /**
   * Sets the bit in {@code buf} specified by the {@code bit} index to {@code false}.
   *
   * @param buf The {@code long[]} buffer in which to clear the bit.
   * @param bit The index of the bit to be cleared.
   * @return The provided {@code long[]} buffer.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static long[] clear(final long[] buf, final int bit) {
    final int i = bit / Long.SIZE;
    if (i >= buf.length)
      return buf;

    buf[bit / Long.SIZE] &= ~(1L << (bit % Long.SIZE));
    return buf;
  }

  /**
   * Returns the value of the bit at the specified index. The value is {@code true} if the bit with the index {@code bit} is
   * currently set in {@code buf}, otherwise the result is {@code false}.
   *
   * @param buf The {@code byte[]} buffer.
   * @param bit The bit index.
   * @return The value of the bit at the specified index.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static boolean get(final byte[] buf, final int bit) {
    final int i = bit / Byte.SIZE;
    return i < buf.length && ((buf[i] >> (bit % Byte.SIZE)) & 1) != 0;
  }

  /**
   * Returns the value of the bit at the specified index. The value is {@code true} if the bit with the index {@code bit} is
   * currently set in {@code buf}, otherwise the result is {@code false}.
   *
   * @param buf The {@code char[]} buffer.
   * @param bit The bit index.
   * @return The value of the bit at the specified index.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static boolean get(final char[] buf, final int bit) {
    final int i = bit / Character.SIZE;
    return i < buf.length && ((buf[i] >> (bit % Character.SIZE)) & 1) != 0;
  }

  /**
   * Returns the value of the bit at the specified index. The value is {@code true} if the bit with the index {@code bit} is
   * currently set in {@code buf}, otherwise the result is {@code false}.
   *
   * @param buf The {@code short[]} buffer.
   * @param bit The bit index.
   * @return The value of the bit at the specified index.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static boolean get(final short[] buf, final int bit) {
    final int i = bit / Short.SIZE;
    return i < buf.length && ((buf[i] >> (bit % Short.SIZE)) & 1) != 0;
  }

  /**
   * Returns the value of the bit at the specified index. The value is {@code true} if the bit with the index {@code bit} is
   * currently set in {@code buf}, otherwise the result is {@code false}.
   *
   * @param buf The {@code int[]} buffer.
   * @param bit The bit index.
   * @return The value of the bit at the specified index.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static boolean get(final int[] buf, final int bit) {
    final int i = bit / Integer.SIZE;
    return i < buf.length && ((buf[i] >> (bit % Integer.SIZE)) & 1) != 0;
  }

  /**
   * Returns the value of the bit at the specified index. The value is {@code true} if the bit with the index {@code bit} is
   * currently set in {@code buf}, otherwise the result is {@code false}.
   *
   * @param buf The {@code long[]} buffer.
   * @param bit The bit index.
   * @return The value of the bit at the specified index.
   * @throws ArrayIndexOutOfBoundsException If the specified index is negative.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static boolean get(final long[] buf, final int bit) {
    final int i = bit / Long.SIZE;
    return i < buf.length && ((buf[i] >> (bit % Long.SIZE)) & 1) != 0;
  }

  /**
   * Returns the "logical size" of {@code buf}: the index of the byte with the highest set bit in {@code buf}, plus one. Returns
   * zero if {@code buf} contains no set bits.
   *
   * @param buf The {@code byte[]} buffer.
   * @return The logical size of the {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int length(final byte[] buf) {
    int i = buf.length;
    if (i == 0)
      return 0;

    --i;
    while (buf[i] == 0 && --i >= 0);
    return ++i;
  }

  /**
   * Returns the "logical size" of {@code buf}: the index of the char with the highest set bit in {@code buf}, plus one. Returns
   * zero if {@code buf} contains no set bits.
   *
   * @param buf The {@code char[]} buffer.
   * @return The logical size of the {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int length(final char[] buf) {
    int i = buf.length;
    if (i == 0)
      return 0;

    --i;
    while (buf[i] == 0 && --i >= 0);
    return ++i;
  }

  /**
   * Returns the "logical size" of {@code buf}: the index of the short with the highest set bit in {@code buf}, plus one. Returns
   * zero if {@code buf} contains no set bits.
   *
   * @param buf The {@code short[]} buffer.
   * @return The logical size of the {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int length(final short[] buf) {
    int i = buf.length;
    if (i == 0)
      return 0;

    --i;
    while (buf[i] == 0 && --i >= 0);
    return ++i;
  }

  /**
   * Returns the "logical size" of {@code buf}: the index of the int with the highest set bit in {@code buf}, plus one. Returns zero
   * if {@code buf} contains no set bits.
   *
   * @param buf The {@code int[]} buffer.
   * @return The logical size of the {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int length(final int[] buf) {
    int i = buf.length;
    if (i == 0)
      return 0;

    --i;
    while (buf[i] == 0 && --i >= 0);
    return ++i;
  }

  /**
   * Returns the "logical size" of {@code buf}: the index of the long with the highest set bit in {@code buf}, plus one. Returns
   * zero if {@code buf} contains no set bits.
   *
   * @param buf The {@code long[]} buffer.
   * @return The logical size of the {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static int length(final long[] buf) {
    int i = buf.length;
    if (i == 0)
      return 0;

    --i;
    while (buf[i] == 0 && --i >= 0);
    return ++i;
  }

  /**
   * Returns a trimmed byte array of {@code buf}: the length of a trimmed byte array is its "logical size".
   * <ul>
   * <li>If the length of {@code buf} is its "logical size", the reference to {@code buf} is returned.</li>
   * <li>If the length of {@code buf} is greater than its "logical size", a reference to a new byte array is returned.</li>
   * </ul>
   *
   * @param buf The {@code byte[]} buffer.
   * @return A trimmed byte array of {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   * @see #length(byte[])
   */
  public static byte[] trimToLength(final byte[] buf) {
    final int length = length(buf);
    if (buf.length == length)
      return buf;

    final byte[] trimmed = new byte[length];
    System.arraycopy(buf, 0, trimmed, 0, length);
    return trimmed;
  }

  /**
   * Returns a trimmed char array of {@code buf}: the length of a trimmed char array is its "logical size".
   * <ul>
   * <li>If the length of {@code buf} is its "logical size", the reference to {@code buf} is returned.</li>
   * <li>If the length of {@code buf} is greater than its "logical size", a reference to a new char array is returned.</li>
   * </ul>
   *
   * @param buf The {@code char[]} buffer.
   * @return A trimmed char array of {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   * @see #length(char[])
   */
  public static char[] trimToLength(final char[] buf) {
    final int length = length(buf);
    if (buf.length == length)
      return buf;

    final char[] trimmed = new char[length];
    System.arraycopy(buf, 0, trimmed, 0, length);
    return trimmed;
  }

  /**
   * Returns a trimmed short array of {@code buf}: the length of a trimmed short array is its "logical size".
   * <ul>
   * <li>If the length of {@code buf} is its "logical size", the reference to {@code buf} is returned.</li>
   * <li>If the length of {@code buf} is greater than its "logical size", a reference to a new short array is returned.</li>
   * </ul>
   *
   * @param buf The {@code short[]} buffer.
   * @return A trimmed short array of {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   * @see #length(short[])
   */
  public static short[] trimToLength(final short[] buf) {
    final int length = length(buf);
    if (buf.length == length)
      return buf;

    final short[] trimmed = new short[length];
    System.arraycopy(buf, 0, trimmed, 0, length);
    return trimmed;
  }

  /**
   * Returns a trimmed int array of {@code buf}: the length of a trimmed int array is its "logical size".
   * <ul>
   * <li>If the length of {@code buf} is its "logical size", the reference to {@code buf} is returned.</li>
   * <li>If the length of {@code buf} is greater than its "logical size", a reference to a new int array is returned.</li>
   * </ul>
   *
   * @param buf The {@code int[]} buffer.
   * @return A trimmed int array of {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   * @see #length(int[])
   */
  public static int[] trimToLength(final int[] buf) {
    final int length = length(buf);
    if (buf.length == length)
      return buf;

    final int[] trimmed = new int[length];
    System.arraycopy(buf, 0, trimmed, 0, length);
    return trimmed;
  }

  /**
   * Returns a trimmed long array of {@code buf}: the length of a trimmed long array is its "logical size".
   * <ul>
   * <li>If the length of {@code buf} is its "logical size", the reference to {@code buf} is returned.</li>
   * <li>If the length of {@code buf} is greater than its "logical size", a reference to a new long array is returned.</li>
   * </ul>
   *
   * @param buf The {@code long[]} buffer.
   * @return A trimmed long array of {@code buf}.
   * @throws NullPointerException If {@code buf} is null.
   * @see #length(long[])
   */
  public static long[] trimToLength(final long[] buf) {
    final int length = length(buf);
    if (buf.length == length)
      return buf;

    final long[] trimmed = new long[length];
    System.arraycopy(buf, 0, trimmed, 0, length);
    return trimmed;
  }

  private static final char[] digits = {
    '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9', 'a', 'b',
    'c', 'd', 'e', 'f', 'g', 'h',
    'i', 'j', 'k', 'l', 'm', 'n',
    'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z'
  };

  private static void toUnsignedString(long val, final byte[] buf, final int index, final int size) {
    final int offset = index * size;
    int pos = size + offset;
    do {
      buf[--pos] = (byte)digits[((int)val) & 1];
      val >>>= 1;
    }
    while (pos > offset);
  }

  /**
   * Returns a string representing the big-endian binary representation of the specified byte values.
   *
   * @param buf The byte values.
   * @return A string representing the big-endian binary representation of the specified byte values.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static String toString(final byte ... buf) {
    final byte[] bytes = new byte[buf.length * Byte.SIZE];
    for (int i = 0, i$ = buf.length; i < i$; ++i) // [A]
      toUnsignedString(buf[i], bytes, i, Byte.SIZE);

    return new String(bytes);
  }

  /**
   * Returns a string representing the big-endian binary representation of the specified char values.
   *
   * @param buf The char values.
   * @return A string representing the big-endian binary representation of the specified char values.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static String toString(final char ... buf) {
    final byte[] bytes = new byte[buf.length * Character.SIZE];
    for (int i = 0, i$ = buf.length; i < i$; ++i) // [A]
      toUnsignedString(buf[i], bytes, i, Character.SIZE);

    return new String(bytes);
  }

  /**
   * Returns a string representing the big-endian binary representation of the specified short values.
   *
   * @param buf The short values.
   * @return A string representing the big-endian binary representation of the specified short values.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static String toString(final short ... buf) {
    final byte[] bytes = new byte[buf.length * Short.SIZE];
    for (int i = 0, i$ = buf.length; i < i$; ++i) // [A]
      toUnsignedString(buf[i], bytes, i, Short.SIZE);

    return new String(bytes);
  }

  /**
   * Returns a string representing the big-endian binary representation of the specified int values.
   *
   * @param buf The int values.
   * @return A string representing the big-endian binary representation of the specified int values.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static String toString(final int ... buf) {
    final byte[] bytes = new byte[buf.length * Integer.SIZE];
    for (int i = 0, i$ = buf.length; i < i$; ++i) // [A]
      toUnsignedString(buf[i], bytes, i, Integer.SIZE);

    return new String(bytes);
  }

  /**
   * Returns a string representing the big-endian binary representation of the specified long values.
   *
   * @param buf The long values.
   * @return A string representing the big-endian binary representation of the specified long values.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static String toString(final long ... buf) {
    final byte[] bytes = new byte[buf.length * Long.SIZE];
    for (int i = 0, i$ = buf.length; i < i$; ++i) // [A]
      toUnsignedString(buf[i], bytes, i, Long.SIZE);

    return new String(bytes);
  }

  private Buffers() {
  }
}