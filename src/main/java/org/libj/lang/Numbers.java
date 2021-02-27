/* Copyright (c) 2008 LibJ
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Comparator;

/**
 * Utility functions for operations pertaining to {@link Number}.
 */
public final class Numbers {
  /** {@link BigInteger} representation of {@link Long#MIN_VALUE}. */
  public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

  /** {@link BigInteger} representation of {@link Long#MAX_VALUE}. */
  public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  /**
   * Utility functions to convert between signed and unsigned numbers.
   */
  public static final class Unsigned {
    /**
     * Return an <i>unsigned</i> {@code byte} equal to the value of the
     * argument.
     *
     * @param int8 The unsigned 8-bit magnitude of the {@code long} to be
     *          returned.
     * @return An <i>unsigned</i> {@code short} equal to the value of the
     *         argument.
     * @throws ArithmeticException If {@code int8} is negative or is too big to
     *           fit in 8 bits of an unsigned {@code byte}.
     */
    public static byte toUINT8(final short int8) {
      if ((int8 >> 8) != 0)
        throw new ArithmeticException(int8 + " is too big to fit in 8 bits of an unsigned byte");

      return (byte)int8;
    }

    /**
     * Return an <i>unsigned</i> {@code short} equal to the value of the
     * argument.
     *
     * @param int16 The unsigned 16-bit magnitude of the {@code long} to be
     *          returned.
     * @return An <i>unsigned</i> {@code int} equal to the value of the
     *         argument.
     * @throws ArithmeticException If {@code int16} is negative or is too big to
     *           fit in 16 bits of an unsigned {@code short}.
     */
    public static short toUINT16(final int int16) {
      if ((int16 >> 16) != 0)
        throw new ArithmeticException(int16 + " is too big to fit in 16 bits of an unsigned short");

      return (short)int16;
    }

    /**
     * Return an <i>unsigned</i> {@code int} equal to the value of the argument.
     *
     * @param int32 The unsigned 32-bit magnitude of the {@code long} to be
     *          returned.
     * @return An <i>unsigned</i> {@code long} equal to the value of the
     *         argument.
     * @throws ArithmeticException If {@code int32} is negative or is too big to
     *           fit in 32 bits of an unsigned {@code int}.
     */
    public static int toUINT32(final long int32) {
      if ((int32 >> 32) != 0)
        throw new ArithmeticException(int32 + " is too big to fit in 32 bits of an unsigned int");

      return (int)int32;
    }

    /**
     * Return an <i>unsigned</i> {@code long} equal to the value of the
     * argument.
     *
     * @param int64 The unsigned 64-bit magnitude of the {@link BigInteger} to
     *          be returned.
     * @return An <i>unsigned</i> {@code long} equal to the value of the
     *         argument.
     * @throws ArithmeticException If {@code int64} is negative or is too big to
     *           fit in 64 bits of an unsigned {@code long}.
     */
    public static long toUINT64(final BigInteger int64) {
      if (int64.signum() < 0)
        throw new ArithmeticException(int64 + " must be positive");

      if (int64.bitLength() > 64)
        throw new ArithmeticException(int64 + " is too big to fit in 64 bits of an unsigned long");

      return int64.longValue();
    }

    /**
     * Returns the unsigned representation of the provided {@link BigInteger} as
     * a {@code byte} array. The provided {@link BigInteger} must be positive.
     *
     * @param bigInteger The signed value.
     * @return The unsigned representation of the signed value as a {@code byte}
     *         array.
     * @throws IllegalArgumentException If {@code bigInteger} is negative.
     */
    public static byte[] toUINT(final BigInteger bigInteger) {
      if (bigInteger.signum() == -1)
        throw new IllegalArgumentException(bigInteger + " must be positive");

      final byte[] bytes = bigInteger.toByteArray();
      if (bytes[0] != 0)
        return bytes;

      final byte[] trimmed = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, trimmed, 0, trimmed.length);
      return trimmed;
    }

    /**
     * Return a {@link BigInteger} equal to the unsigned value of the argument.
     *
     * @param uint64 The unsigned magnitude of the {@link BigInteger} to be
     *          returned.
     * @return A {@link BigInteger} equal to the unsigned value of the argument.
     */
    public static BigInteger toUnsignedBigInteger(final long uint64) {
      return BigIntegers.valueOf(1, uint64);
    }

    /**
     * Return a {@link BigInteger} equal to the unsigned value of the argument.
     *
     * @param uint The unsigned magnitude of the {@link BigInteger} to be
     *          returned.
     * @return A {@link BigInteger} equal to the unsigned value of the argument.
     */
    public static BigInteger toUnsignedBigInteger(final byte[] uint) {
      return BigIntegers.valueOf(1, uint);
    }

    private Unsigned() {
    }
  }

  private static final Comparator<Number> comparator = (o1, o2) -> {
    if (o1 == null)
      return o2 == null ? 0 : 1;

    if (o2 == null)
      return -1;

    if (o1 instanceof BigDecimal) {
      if (o2 instanceof BigDecimal)
        return ((BigDecimal)o1).compareTo((BigDecimal)o2);

      if (o2 instanceof BigInteger)
        return ((BigDecimal)o1).compareTo(new BigDecimal((BigInteger)o2));

      if (o2 instanceof Byte || o2 instanceof Short || o2 instanceof Integer || o2 instanceof Long)
        return ((BigDecimal)o1).compareTo(BigDecimal.valueOf(o2.longValue()));

      return ((BigDecimal)o1).compareTo(BigDecimal.valueOf(o2.doubleValue()));
    }

    if (o1 instanceof BigInteger) {
      if (o2 instanceof BigInteger)
        return ((BigInteger)o1).compareTo((BigInteger)o2);

      if (o2 instanceof BigDecimal)
        return new BigDecimal((BigInteger)o1).compareTo((BigDecimal)o2);

      if (o2 instanceof Byte || o2 instanceof Short || o2 instanceof Integer || o2 instanceof Long)
        return ((BigInteger)o1).compareTo(BigInteger.valueOf(o2.longValue()));

      return new BigDecimal((BigInteger)o1).compareTo(BigDecimal.valueOf(o2.doubleValue()));
    }

    if (o1 instanceof Byte || o1 instanceof Short || o1 instanceof Integer || o1 instanceof Long) {
      if (o2 instanceof BigInteger)
        return BigInteger.valueOf(o1.longValue()).compareTo((BigInteger)o2);

      if (o2 instanceof BigDecimal)
        return BigDecimal.valueOf(o1.doubleValue()).compareTo((BigDecimal)o2);

      return Double.compare(o1.doubleValue(), o2.doubleValue());
    }

    if (o2 instanceof BigInteger)
      return BigDecimal.valueOf(o1.doubleValue()).compareTo(new BigDecimal((BigInteger)o2));

    if (o2 instanceof BigDecimal)
      return BigDecimal.valueOf(o1.doubleValue()).compareTo((BigDecimal)o2);

    return Double.compare(o1.doubleValue(), o2.doubleValue());
  };

  /**
   * Utility functions for the encoding and decoding of "compound values" in
   * primitive types. A "compound value" in a primitive type is one that
   * contains multiple values of a smaller sized primitive type. For example, a
   * {@code short} is 16 bits in size, allowing it to represent a compound value
   * of 2 {@code byte}s, since a {@code byte} is 8 bits in size.
   */
  public static final class Compound {
    /**
     * Encodes two {@code int}s into a {@code long}.
     *
     * @param a The first {@code int}.
     * @param b The second {@code int}.
     * @return A compounded {@code long} representing two {@code int}.
     */
    public static long encode(final int a, final int b) {
      return ((long)b << Integer.SIZE) & 0xffffffff00000000L | a & 0xffffffffL;
    }

    /**
     * Encodes four {@code short}s into a {@code long}.
     *
     * @param a The first {@code short}.
     * @param b The second {@code short}.
     * @param c The third {@code short}.
     * @param d The fourth {@code short}.
     * @return A compounded {@code long} representing four {@code short}.
     */
    public static long encode(final short a, final short b, final short c, final short d) {
      return ((long)d << Short.SIZE * 3) & 0xffff000000000000L | ((long)c << Short.SIZE * 2) & 0xffff00000000L | ((long)b << Short.SIZE) & 0xffff0000L | a & 0xffffL;
    }

    /**
     * Encodes eight {@code byte}s into a {@code long}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @param c The third {@code byte}.
     * @param d The fourth {@code byte}.
     * @param e The fifth {@code byte}.
     * @param f The sixth {@code byte}.
     * @param g The seventh {@code byte}.
     * @param h The eighth {@code byte}.
     * @return A compounded {@code long} representing eighth {@code byte}.
     */
    public static long encode(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f, final byte g, final byte h) {
      return ((long)h << Byte.SIZE * 7) & 0xff00000000000000L | ((long)g << Byte.SIZE * 6) & 0xff000000000000L | ((long)f << Byte.SIZE * 5) & 0xff0000000000L | ((long)e << Byte.SIZE * 4) & 0xff00000000L | ((long)d << Byte.SIZE * 3) & 0xff000000L | ((long)c << Byte.SIZE * 2) & 0xff0000L | ((long)b << Byte.SIZE) & 0xff00L | a & 0xffL;
    }

    /**
     * Encodes two {@code float}s into a {@code long}.
     *
     * @param a The first {@code float}.
     * @param b The second {@code float}.
     * @return A compounded {@code long} representing two {@code float}s.
     */
    public static long encode(final float a, final float b) {
      return encode(Float.floatToIntBits(a), Float.floatToIntBits(b));
    }

    /**
     * Encodes a {@code float} and an {@code int} into a {@code long}.
     *
     * @param a The {@code float}.
     * @param b The {@code int}.
     * @return A compounded {@code long} representing a {@code float} and an
     *         {@code int}.
     */
    public static long encode(final float a, final int b) {
      return encode(Float.floatToIntBits(a), b);
    }

    /**
     * Encodes an {@code int} and a {@code float} into a {@code long}.
     *
     * @param a The {@code int}.
     * @param b The {@code float}.
     * @return A compounded {@code long} representing an {@code int} and a
     *         {@code float}.
     */
    public static long encode(final int a, final float b) {
      return encode(a, Float.floatToIntBits(b));
    }

    /**
     * Encodes two {@code short}s into an {@code int}.
     *
     * @param a The first {@code short}.
     * @param b The second {@code short}.
     * @return A compounded {@code int} representing two {@code short}.
     */
    public static int encode(final short a, final short b) {
      return b << Short.SIZE | a & 0xffff;
    }

    /**
     * Encodes four {@code byte}s into an {@code int}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @param c The third {@code byte}.
     * @param d The fourth {@code byte}.
     * @return A compounded {@code int} representing two {@code byte}.
     */
    public static int encode(final byte a, final byte b, final byte c, final byte d) {
      return (d << Byte.SIZE * 3) & 0xff000000 | (c << Byte.SIZE * 2) & 0xff0000 | (b << Byte.SIZE) & 0xff00 | a & 0xff;
    }

    /**
     * Encodes two {@code byte}s into a {@code short}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @return A compounded {@code short} representing two {@code byte}.
     */
    public static short encode(final byte a, final byte b) {
      return (short)((b << Byte.SIZE) & 0xff00 | a & 0xff);
    }

    /**
     * Decodes the {@code int} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code int}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code int} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static int decodeInt(final long val, final int pos) {
      return (int)(val >> Integer.SIZE * pos);
    }

    /**
     * Decodes the {@code float} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing a {@code float} value.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code float} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static float decodeFloat(final long val, final int pos) {
      return Float.intBitsToFloat((int)(val >> Integer.SIZE * pos));
    }

    /**
     * Decodes the {@code short} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code short}.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code short} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static short decodeShort(final long val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code long} value.
     *
     * @param val The compounded {@code long} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1, 2, 3, 4, 5, 6, 7).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code long} value.
     */
    public static byte decodeByte(final long val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code short} value at the specified position that is
     * represented in the provided compounded {@code int} value.
     *
     * @param val The compounded {@code int} containing {@code short}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code short} value at the specified position that is
     *         represented in the provided compounded {@code int} value.
     */
    public static short decodeShort(final int val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code int} value.
     *
     * @param val The compounded {@code int} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code int} value.
     */
    public static byte decodeByte(final int val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is
     * represented in the provided compounded {@code short} value.
     *
     * @param val The compounded {@code short} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code byte} value at the specified position that is
     *         represented in the provided compounded {@code short} value.
     */
    public static byte decodeByte(final short val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    private Compound() {
    }
  }

  /**
   * Compares the specified numbers, returning a negative integer, zero, or a
   * positive integer as the first argument is less than, equal to, or greater
   * than the second.
   * <p>
   * Null values are considered as less than non-null values.
   *
   * @param a The first {@link Number} to be compared.
   * @param b The second {@link Number} to be compared.
   * @return A negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
  public static int compare(final Number a, final Number b) {
    return comparator.compare(a, b);
  }

  private static final int[] highestBitSet = {
    0, 1, 2, 2, 3, 3, 3, 3,
    4, 4, 4, 4, 4, 4, 4, 4,
    5, 5, 5, 5, 5, 5, 5, 5,
    5, 5, 5, 5, 5, 5, 5, 5,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 255, // anything past 63 is a guaranteed overflow with base > 1
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255,
  };

  /** {@code double} representation of log(2) */
  public static final double LOG_2 = 0.6931471805599453;

  /** {@code double} representation of log(10) */
  public static final double LOG_10 = 2.302585092994046;

  /**
   * Returns the value of the specified base raised to the power of the
   * specified exponent. The algorithm in this implementation takes advantage of
   * the whole values of the base and exponent to outperform
   * {@link Math#pow(double,double)}.
   *
   * @param base The base.
   * @param exp The exponent.
   * @return The value of the specified base raised to the power of the
   *         specified exponent.
   * @throws ArithmeticException If the resulting value cannot be represented as
   *           a {@code long} due to overflow.
   */
  public static long pow(long base, int exp) {
    long result = 1;

    switch (highestBitSet[exp]) {
      case 255: // we use 255 as an overflow marker and return 0 on overflow/underflow
        return base == 1 ? 1 : base == -1 ? 1 - 2 * (exp & 1) : 0;

      case 6:
        if ((exp & 1) != 0)
          result = checkedMultiple(result, base);

        exp >>= 1;
        base *= base;
      case 5:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 4:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 3:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 2:
        if ((exp & 1) != 0)
          result *= base;

        exp >>= 1;
        base *= base;
      case 1:
        if ((exp & 1) != 0)
          result *= base;

      default:
        return result;
    }
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation
   *          to be parsed.
   * @return The integer value represented by the argument, or {@code null} if
   *         the sequence does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s) {
    return s == null ? null : parseShort0(s, 0, s.length(), 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The integer value represented by the argument, or {@code null} if
   *         the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseShort0(s, fromIndex, toIndex, 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation
   *          to be parsed.
   * @param defaultValue The {@code short} value to be returned if the sequence
   *          does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final short defaultValue) {
    return s == null ? defaultValue : parseShort0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the sequence
   *          does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final short defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseShort0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short}
   *          representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the char
   *          array does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final short defaultValue) {
    return cbuf == null ? defaultValue : parseShort0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short}
   *          representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param defaultValue The {@code short} value to be returned if the char
   *          array does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final short defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseShort0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the sequence does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int radix) {
    if (s == null)
      return null;

    final int i = parseInt0(s, 0, s.length(), radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseShort0(s, fromIndex, toIndex, radix);
  }

  private static Short parseShort0(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@link Short}
   *          representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the char array does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final char[] cbuf, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt0(cbuf, 0, cbuf.length, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@link Short}
   *          representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if
   *         the char array does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation
   *          to be parsed.
   * @param defaultValue The {@code short} value to be returned if the sequence
   *          does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final int radix, final short defaultValue) {
    if (s == null)
      return defaultValue;

    final int i = parseInt0(s, 0, s.length(), radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the sequence
   *          does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseShort0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static short parseShort0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short}
   *          representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the char
   *          array does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int radix, final short defaultValue) {
    if (cbuf == null)
      return defaultValue;

    final int i = parseInt0(cbuf, 0, cbuf.length, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Short#parseShort(String,int)}, but returns {@code defaultValue} if
   * the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short}
   *          representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param defaultValue The {@code short} value to be returned if the char
   *          array does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseShort0(cbuf, fromIndex, toIndex, radix, defaultValue);
  }

  private static short parseShort0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    final int i = parseInt0(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer}
   *          representation to be parsed.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s) {
    return s == null ? null : parseInteger0(s, 0, s.length(), 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer}
   *          representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseInteger0(s, fromIndex, toIndex, 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation
   *          to be parsed.
   * @param defaultValue The {@code int} value to be returned if the sequence
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int defaultValue) {
    return s == null ? defaultValue : parseInt0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code int} value to be returned if the sequence
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int fromIndex, final int toIndex, final int defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseInt0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param defaultValue The {@code int} value to be returned if the char array
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int defaultValue) {
    return cbuf == null ? defaultValue : parseInt0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param defaultValue The {@code int} value to be returned if the char array
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int fromIndex, final int toIndex, final int defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseInt0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the sequence
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int radix, final int defaultValue) {
    return s == null ? defaultValue : parseInt0(s, 0, s.length(), radix, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the sequence
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseInt0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static int parseInt0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return defaultValue;

    boolean negative = false;
    int i = fromIndex;
    int limit = -Integer.MAX_VALUE;

    final char firstChar = s.charAt(i);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the char array
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int radix, final int defaultValue) {
    if (cbuf == null)
      return defaultValue;

    return parseInt0(cbuf, 0, cbuf.length, radix, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code defaultValue} if
   * the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation
   *          to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the char array
   *          does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseInt0(cbuf, fromIndex, toIndex, radix, defaultValue);
  }

  private static int parseInt0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return defaultValue;

    boolean negative = false;
    int i = fromIndex;
    int limit = -Integer.MAX_VALUE;

    final char firstChar = cbuf[i];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer}
   *          representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int radix) {
    return s == null ? null : parseInteger0(s, 0, s.length(), radix);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer}
   *          representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseInteger0(s, fromIndex, toIndex, radix);
  }

  private static Integer parseInteger0(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return null;

    boolean negative = false;
    int i = fromIndex;
    int limit = -Integer.MAX_VALUE;

    final char firstChar = s.charAt(i);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@link Integer}
   *          representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the char array does not contain a parsable {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final char[] cbuf, final int radix) {
    if (cbuf == null)
      return null;

    return parseInteger0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Integer#parseInt(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@link Integer}
   *          representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null}
   *         if the char array does not contain a parsable {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseInteger0(cbuf, fromIndex, toIndex, radix);
  }

  private static Integer parseInteger0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return null;

    boolean negative = false;
    int i = fromIndex;
    int limit = -Integer.MAX_VALUE;

    final char firstChar = cbuf[i];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Integer.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final int multmin = limit / radix;
    int result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation
   *          to be parsed.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s) {
    if (s == null)
      return null;

    return parseLong0(s, 0, s.length(), 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code null} if the sequence
   * does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseLong0(s, fromIndex, toIndex, 10);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation
   *          to be parsed.
   * @param defaultValue The {@code long} value to be returned if the sequence
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code long} value to be returned if the sequence
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final long defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseLong0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param defaultValue The {@code long} value to be returned if the char array
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final long defaultValue) {
    return cbuf == null ? defaultValue : parseLong0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param defaultValue The {@code long} value to be returned if the char array
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final long defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseLong0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int radix) {
    return s == null ? null : parseLong0(s, 0, s.length(), radix);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the sequence does not contain a parsable {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    if (s == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseLong0(s, fromIndex, toIndex, radix);
  }

  private static Long parseLong0(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return null;

    boolean negative = false;
    int i = fromIndex;
    long limit = -Long.MAX_VALUE;

    final char firstChar = s.charAt(i);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@link Long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the char array does not contain a parsable {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final char[] cbuf, final int radix) {
    return cbuf == null ? null : parseLong0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code null} if the char
   * array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@link Long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null}
   *         if the char array does not contain a parsable {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseLong0(cbuf, fromIndex, toIndex, radix);
  }

  private static Long parseLong0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return null;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return null;

    boolean negative = false;
    int i = fromIndex;
    long limit = -Long.MAX_VALUE;

    final char firstChar = cbuf[i];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return null;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return null;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return null;

      result *= radix;
      if (result < limit + digit)
        return null;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the sequence
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int radix, final long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), radix);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the sequence
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (s == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, s.length());
    return parseLong0(s, fromIndex, toIndex, radix);
  }

  private static long parseLong0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return defaultValue;

    boolean negative = false;
    int i = fromIndex;
    long limit = -Long.MAX_VALUE;

    final char firstChar = s.charAt(i);
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(s.charAt(i++), radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the char array
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int radix, final long defaultValue) {
    return cbuf == null ? defaultValue : parseLong0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of
   * {@link Long#parseLong(String,int)}, but returns {@code defaultValue} if the
   * char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation
   *          to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing
   *          (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing
   *          (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the char array
   *          does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or
   *         {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (cbuf == null)
      return defaultValue;

    Assertions.assertRange(fromIndex, toIndex, cbuf.length);
    return parseLong0(cbuf, fromIndex, toIndex, radix);
  }

  private static long parseLong0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      return defaultValue;

    final int len = toIndex - fromIndex;
    if (len == 0)
      return defaultValue;

    boolean negative = false;
    int i = fromIndex;
    long limit = -Long.MAX_VALUE;

    final char firstChar = cbuf[i];
    if (firstChar < '0') { // Possible leading "+" or "-"
      if (firstChar == '-') {
        negative = true;
        limit = Long.MIN_VALUE;
      }
      else if (firstChar != '+') {
        return defaultValue;
      }

      if (len == 1) { // Cannot have lone "+" or "-"
        return defaultValue;
      }

      ++i;
    }

    final long multmin = limit / radix;
    long result = 0;
    while (i < toIndex) {
      // Accumulating negatively avoids surprises near MAX_VALUE
      final int digit = Character.digit(cbuf[i++], radix);
      if (digit < 0 || result < multmin)
        return defaultValue;

      result *= radix;
      if (result < limit + digit)
        return defaultValue;

      result -= digit;
    }

    return negative ? result : -result;
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Float#parseFloat(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be
   *          parsed.
   * @return The {@code float} value represented by the argument, or
   *         {@code null} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static Float parseFloat(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Float#parseFloat(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be
   *          parsed.
   * @param defaultValue The {@code float} value to be returned if the string
   *          does not contain a parsable {@code float}.
   * @return The {@code float} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static float parseFloat(final String s, final float defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? defaultValue : Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Double#parseDouble(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to
   *          be parsed.
   * @return The {@code double} value represented by the argument, or
   *         {@code null} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static Double parseDouble(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@link Double#parseDouble(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to
   *          be parsed.
   * @param defaultValue The {@code double} value to be returned if the string
   *          does not contain a parsable {@code double}.
   * @return The {@code double} value represented by the argument, or
   *         {@code defaultValue} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static double parseDouble(final String s, final double defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? defaultValue : Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Returns an {@code int} array representation of the values in the specified
   * {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified
   *         {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not
   *           contain a parsable integer.
   */
  public static int[] parseInt(final String ... s) {
    final int[] values = new int[s.length];
    for (int i = 0; i < s.length; ++i)
      values[i] = Integer.parseInt(s[i]);

    return values;
  }

  /**
   * Returns a {@code double} array representation of the values in the
   * specified {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified
   *         {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not
   *           contain a parsable double.
   */
  public static double[] parseDouble(final String ... s) {
    final double[] values = new double[s.length];
    for (int i = 0; i < s.length; ++i)
      values[i] = Double.parseDouble(s[i]);

    return values;
  }

  /**
   * Returns a {@link Number} of type {@code <T>} of the value for the provided
   * string.
   *
   * @param <T> The type of {@link Number} to be returned.
   * @param s The number to get the value of.
   * @param as The class representing the type of {@link Number} to be returned.
   * @return a {@link Number} of type {@code <T>} of the value for the provided
   *         string.
   * @throws NumberFormatException If the string does not contain a parsable
   *           {@link Number} of type {@code <T>}.
   * @throws UnsupportedOperationException If the specified {@link Class}
   *           represents a type that is not one of: <br>
   *           <blockquote>{@code byte}, {@link Byte},<br>
   *           {@code short}, {@link Short},<br>
   *           {@code char}, {@link Character},<br>
   *           {@code int}, {@link Integer},<br>
   *           {@code long}, {@link Long},<br>
   *           {@code double}, {@link Double},<br>
   *           {@code float}, {@link Float},<br>
   *           {@code short}, {@link Short},<br>
   *           {@link BigInteger}, {@link BigDecimal}</blockquote>
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number>T parseNumber(final String s, final Class<T> as) {
    if (float.class == as || Float.class == as)
      return (T)Float.valueOf(s);

    if (double.class == as || Double.class == as)
      return (T)Double.valueOf(s);

    if (byte.class == as || Byte.class == as)
      return (T)Byte.valueOf(s);

    if (short.class == as || Short.class == as)
      return (T)Short.valueOf(s);

    if (int.class == as || Integer.class == as)
      return (T)Integer.valueOf(s);

    if (long.class == as || Long.class == as)
      return (T)Long.valueOf(s);

    if (BigInteger.class.isAssignableFrom(as))
      return (T)new BigInteger(s);

    if (BigDecimal.class.isAssignableFrom(as))
      return (T)new BigDecimal(s);

    throw new UnsupportedOperationException("Unsupported Number type: " + as.getName());
  }

  public static double parseNumber(String s) {
    if (s == null || (s = s.trim()).length() == 0 || !isNumber(s))
      return Double.NaN;

    double scalar = 0;
    final String[] parts = s.split(" ");
    if (parts.length == 2) {
      scalar += new BigDecimal(parts[0]).doubleValue();
      s = parts[1];
    }

    final int slash = s.indexOf('/');
    if (slash == 1)
      scalar += (double)Integer.parseInt(s.substring(0, slash)) / Integer.parseInt(s.substring(slash + 1));
    else
      scalar += new BigDecimal(s).doubleValue();

    return scalar;
  }

  /**
   * Returns the specified {@link Number} cast to the provided {@link Class
   * Class&lt;T&gt;}.
   *
   * @param <T> The type parameter of the cast class.
   * @param n The {@link Number} to be cast.
   * @param as The {@link Class} to which the specified {@link Number} is to be
   *          cast.
   * @return The specified {@link Number} cast to the provided {@link Class
   *         Class&lt;T&gt;}.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number>T cast(final Number n, final Class<T> as) {
    if (n == null)
      return null;

    if (long.class == as || Long.class == as)
      return (T)Long.valueOf(n.longValue());

    if (int.class == as || Integer.class == as)
      return (T)Integer.valueOf(n.intValue());

    if (short.class == as || Short.class == as)
      return (T)Short.valueOf(n.shortValue());

    if (byte.class == as || Byte.class == as)
      return (T)Byte.valueOf(n.byteValue());

    if (double.class == as || Double.class == as)
      return (T)Double.valueOf(n.doubleValue());

    if (float.class == as || Float.class == as)
      return (T)Float.valueOf(n.floatValue());

    if (BigInteger.class == as)
      return (T)new BigInteger(n.toString());

    if (BigDecimal.class == as)
      return (T)new BigDecimal(n.toString());

    throw new UnsupportedOperationException("Unsupported type: " + as.getName());
  }

  /**
   * Tests whether the specified string represents a number, or a number with a
   * fraction of two numbers (i.e. {@code 23 3/4}).
   * <p>
   * This method supports exponent form (i.e. {@code 3.2E-5}).
   *
   * @param s The string to test.
   * @return {@code true} if the specified string represents a number, or a
   *         number with a fraction of two numbers.
   */
  public static boolean isNumber(String s) {
    if (s == null || (s = s.trim()).length() == 0)
      return false;

    final String[] parts = s.split(" ");
    if (parts.length > 2)
      return false;

    if (parts.length == 2) {
      final int slash = parts[1].indexOf('/');
      if (slash < 0)
        return false;

      return isNumber(parts[0], false) && isNumber(parts[1], true);
    }

    return isNumber(parts[0], true);
  }

  private static boolean isNumber(String string, final boolean fraction) {
    if (string == null || (string = string.trim()).length() == 0)
      return false;

    boolean dotEncountered = false;
    boolean expEncountered = false;
    boolean minusEncountered = false;
    boolean slashEncountered = false;
    int factor = 0;
    for (int i = string.length() - 1; i >= 0; --i) {
      final char c = string.charAt(i);
      if (c < '0') {
        if (c == '/') {
          if (!fraction || dotEncountered || expEncountered || minusEncountered || slashEncountered)
            return false;

          slashEncountered = true;
        }
        else if (c == '.') {
          if (dotEncountered || slashEncountered)
            return false;

          dotEncountered = true;
        }
        else if (c == '-') {
          if (minusEncountered)
            return false;

          minusEncountered = true;
        }
        else if (!expEncountered && c != '+') {
          return false;
        }
      }
      else if ('9' < c) {
        if (c != 'E')
          return false;

        if (factor == 0 || expEncountered)
          return false;

        expEncountered = true;
        factor = 0;
        minusEncountered = false;
      }
      else {
        if (minusEncountered)
          return false;

        ++factor;
      }
    }

    return true;
  }

  /**
   * Assert the specified radix is within legal range.
   *
   * @param radix The radix to assert.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  private static void assertRadix(final int radix) {
    if (radix < Character.MIN_RADIX)
      throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");

    if (radix > Character.MAX_RADIX)
      throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
  }

  /**
   * Determines if the specified character is a digit in the provided radix.
   *
   * @param digit The character to test.
   * @param radix The radix to test against.
   * @return {@code true} if the character is a digit in the provided radix;
   *         {@code false} otherwise.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  public static boolean isDigit(final char digit, final int radix) {
    final int val = digit(digit, radix);
    return 0 <= val && val < radix;
  }

  /**
   * Returns {@code true} if the specified {@code int} is a power of 2.
   *
   * @param a The {@code int}.
   * @return {@code true} if the specified {@code int} is a power of 2.
   */
  public static boolean isPowerOf2(final int a) {
    return a > 0 && Integer.bitCount(a) == 1;
  }

  /**
   * Returns {@code true} if the specified {@code long} is a power of 2.
   *
   * @param a The {@code long}.
   * @return {@code true} if the specified {@code long} is a power of 2.
   */
  public static boolean isPowerOf2(final long a) {
    return a > 0 && Long.bitCount(a) == 1;
  }

  /**
   * Returns the numeric value of the specified character representing a digit.
   * The specified character must be within the following ranges:
   * <ol>
   * <li>{@code '0' <= digit && digit <= '9'}</li>
   * <li>{@code 'a' <= digit && digit <= 'a'}</li>
   * <li>{@code 'A' <= digit && digit <= 'Z'}</li>
   * </ol>
   * <p>
   * If the specified character is outside these ranges, the value
   * {@code -digit} is returned.
   *
   * @param digit The character representing a digit.
   * @param radix The radix to be used to transform the character.
   * @return The numeric value of the specified character representing a digit.
   * @throws NumberFormatException If the specified radix is outside the range
   *           of legal values.
   */
  public static int digit(final char digit, final int radix) {
    assertRadix(radix);
    if ('0' <= digit && digit <= '9')
      return digit - '0';

    if ('a' <= digit && digit <= 'z')
      return digit + 10 - 'a';

    if ('A' <= digit && digit <= 'Z')
      return digit + 10 - 'A';

    return -digit;
  }

  /**
   * Returns {@code true} if the specified {@code float} can be represented as a
   * whole number without loss of precision.
   *
   * @param n The {@code float} to test.
   * @return {@code true} if the specified {@code float} can be represented as a
   *         whole number without loss of precision.
   */
  public static boolean isWhole(final float n) {
    return (int)n == n;
  }

  /**
   * Returns {@code true} if the specified {@code double} can be represented as
   * a whole number without loss of precision.
   *
   * @param n The {@code double} to test.
   * @return {@code true} if the specified {@code double} can be represented as
   *         a whole number without loss of precision.
   */
  public static boolean isWhole(final double n) {
    return (long)n == n;
  }

  /**
   * Returns the multiple of the specified values, throwing an
   * {@link ArithmeticException} if the resulting value cannot be represented as
   * a {@code long} due to overflow.
   *
   * @param a The first value.
   * @param b The second value.
   * @return The multiple of the specified values.
   * @throws ArithmeticException If the resulting value cannot be represented as
   *           a {@code long} due to overflow.
   */
  public static long checkedMultiple(final long a, final long b) {
    final long maximum = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
    if (a != 0 && (b > 0 && b > maximum / a || b < 0 && b < maximum / a))
      throw new ArithmeticException("long overflow");

    return a * b;
  }

  /**
   * Returns a string representation of the specified {@code double} to the
   * provided number of decimal places.
   *
   * @param n The {@code double}.
   * @param decimals The number of decimal places.
   * @return A string representation of the specified {@code double} to the
   *         provided number of decimal places.
   */
  public static String toString(final double n, final int decimals) {
    final double factor = StrictMath.pow(10, decimals);
    return String.valueOf(Math.round(n * factor) / factor);
  }

  /**
   * Strips trailing zeroes after a decimal point in the specified string.
   * <p>
   * This method accepts a string that represents a number. The behavior of this
   * method is undefined for non-number string.
   *
   * @param number The {@link String} representing a number.
   * @return The string with trailing zeroes stripped if the string represents a
   *         decimal number; otherwise the original string is returned.
   */
  public static String stripTrailingZeros(final String number) {
    if (number == null || number.length() == 0)
      return number;

    final int len = number.length();
    int i = len;
    while (i-- > 1)
      if (number.charAt(i) != '0' && number.charAt(i) != '.')
        break;

    ++i;
    return i == len || number.lastIndexOf('.', i + 1) < 0 ? number : number.substring(0, i);
  }

  /**
   * Determines if the difference of the specified {@link Number} values is less
   * than the provided epsilon.
   *
   * @param a The first {@link Number}.
   * @param b The second {@link Number}.
   * @param epsilon The epsilon.
   * @return {@code true} if the difference of the specified {@link Number}
   *         values is less than the provided epsilon.
   */
  public static boolean equivalent(final Number a, final Number b, final double epsilon) {
    if (a == null)
      return b == null;

    if (b == null)
      return false;

    if (a instanceof Byte) {
      if (b instanceof Byte)
        return a.byteValue() == b.byteValue();

      if (b instanceof Short)
        return a.byteValue() == b.shortValue();

      if (b instanceof Integer)
        return a.byteValue() == b.intValue();

      if (b instanceof Long)
        return a.byteValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.byteValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.byteValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.byteValue() - b.floatValue()) < epsilon;

      return Math.abs(a.byteValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Short) {
      if (b instanceof Byte)
        return a.shortValue() == b.byteValue();

      if (b instanceof Short)
        return a.shortValue() == b.shortValue();

      if (b instanceof Integer)
        return a.shortValue() == b.intValue();

      if (b instanceof Long)
        return a.shortValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.shortValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.shortValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.shortValue() - b.floatValue()) < epsilon;

      return Math.abs(a.shortValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Integer) {
      if (b instanceof Byte)
        return a.intValue() == b.byteValue();

      if (b instanceof Short)
        return a.intValue() == b.shortValue();

      if (b instanceof Integer)
        return a.intValue() == b.intValue();

      if (b instanceof Long)
        return a.intValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.intValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.intValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.intValue() - b.floatValue()) < epsilon;

      return Math.abs(a.intValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof Long) {
      if (b instanceof Byte)
        return a.longValue() == b.byteValue();

      if (b instanceof Short)
        return a.longValue() == b.shortValue();

      if (b instanceof Integer)
        return a.longValue() == b.intValue();

      if (b instanceof Long)
        return a.longValue() == b.longValue();

      if (b instanceof BigInteger)
        return BigInteger.valueOf(a.longValue()).equals(b);

      if (b instanceof BigDecimal)
        return BigDecimal.valueOf(a.longValue()).equals(b);

      if (b instanceof Float)
        return Math.abs(a.longValue() - b.floatValue()) < epsilon;

      return Math.abs(a.longValue() - b.doubleValue()) < epsilon;
    }

    if (a instanceof BigInteger) {
      if (b instanceof Byte)
        return a.equals(BigInteger.valueOf(b.byteValue()));

      if (b instanceof Short)
        return a.equals(BigInteger.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return a.equals(BigInteger.valueOf(b.intValue()));

      if (b instanceof Long)
        return a.equals(BigInteger.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return a.equals(b);

      if (b instanceof BigDecimal)
        return new BigDecimal((BigInteger)a).equals(b);

      if (b instanceof Float)
        return a.equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return a.equals(BigDecimal.valueOf(b.doubleValue()).toBigInteger());
    }
    else if (a instanceof BigDecimal) {
      if (b instanceof Byte)
        return a.equals(BigDecimal.valueOf(b.byteValue()));

      if (b instanceof Short)
        return a.equals(BigDecimal.valueOf(b.shortValue()));

      if (b instanceof Integer)
        return a.equals(BigDecimal.valueOf(b.intValue()));

      if (b instanceof Long)
        return a.equals(BigDecimal.valueOf(b.longValue()));

      if (b instanceof BigInteger)
        return a.equals(new BigDecimal((BigInteger)b));

      if (b instanceof BigDecimal)
        return a.equals(b);

      if (b instanceof Float)
        return a.equals(BigDecimal.valueOf(b.floatValue()).toBigInteger());

      if (b instanceof Double)
        return a.equals(BigDecimal.valueOf(b.doubleValue()));
    }

    return Math.abs(a.doubleValue() - b.doubleValue()) < epsilon;
  }

  /**
   * Returns the {@link BigDecimal} representation of the specified
   * {@link Number}.
   *
   * @param n The {@link Number} to convert to a {@link BigDecimal}.
   * @return The {@link BigDecimal} representation of the specified
   *         {@link Number}.
   * @throws NullPointerException If {@code n} is null.
   */
  public static BigDecimal toBigDecimal(final Number n) {
    if (n instanceof BigDecimal)
      return (BigDecimal)n;

    if (n instanceof BigInteger)
      return new BigDecimal((BigInteger)n);

    if (n instanceof Byte)
      return BigDecimal.valueOf(n.byteValue());

    if (n instanceof Short)
      return BigDecimal.valueOf(n.shortValue());

    if (n instanceof Integer)
      return BigDecimal.valueOf(n.intValue());

    if (n instanceof Long)
      return BigDecimal.valueOf(n.longValue());

    if (n instanceof Float)
      return BigDecimal.valueOf(n.floatValue()).stripTrailingZeros();

    return BigDecimal.valueOf(n.doubleValue()).stripTrailingZeros();
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static BigDecimal average(final BigDecimal ... numbers) {
    BigDecimal sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum = sum.add(numbers[i]);

    return sum.divide(BigDecimal.valueOf(numbers.length), RoundingMode.HALF_UP);
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static BigDecimal average(final BigInteger ... numbers) {
    BigDecimal sum = new BigDecimal(numbers[0]);
    for (int i = 1; i < numbers.length; ++i)
      sum = sum.add(new BigDecimal(numbers[i]));

    return sum.divide(BigDecimal.valueOf(numbers.length), RoundingMode.HALF_UP);
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final byte ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final short ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final int ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final long ... numbers) {
    long sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final float ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers.length == 0}.
   */
  public static double average(final double ... numbers) {
    double sum = numbers[0];
    for (int i = 1; i < numbers.length; ++i)
      sum += numbers[i];

    return sum / numbers.length;
  }

  public static Number reduce(final Number number) {
    if (number == null || number instanceof Float || number instanceof Double || number instanceof BigDecimal || number instanceof Byte)
      return number;

    final double value = number.doubleValue();
    if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE)
      return number.byteValue();

    if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE)
      return number.shortValue();

    if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
      return number.intValue();

    if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
      return number.longValue();

    return number;
  }

  /**
   * Tests whether the provided {@link Class Class&lt;? extends Number&gt;} is
   * an exact number type.
   *
   * @param cls The {@link Class Class&lt;? extends Number&gt;} to test.
   * @return Whether the provided {@link Class Class&lt;? extends Number&gt;} is
   *         an exact number type.
   */
  public static boolean isExactType(final Class<? extends Number> cls) {
    if (cls == null)
      return false;

    if (byte.class == cls || Byte.class == cls)
      return true;

    if (short.class == cls || Short.class == cls)
      return true;

    if (int.class == cls || Integer.class == cls)
      return true;

    if (long.class == cls || Long.class == cls)
      return true;

    if (BigInteger.class.isAssignableFrom(cls))
      return true;

    return false;
  }

  /**
   * Returns the precision (the number of digits) of the specified {@code byte}
   * value.
   *
   * @param n The {@code byte} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code byte}
   *         value.
   */
  public static byte precision(final byte n) {
    final int i = Math.abs(n);
    return (byte)(i < 10 ? 1 : i < 100 ? 2 : 3);
  }

  /**
   * Returns the precision (the number of digits) of the specified {@code short}
   * value.
   *
   * @param n The {@code short} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code short}
   *         value.
   */
  public static byte precision(final short n) {
    final int i = Math.abs(n);
    if (i < 10000) {
      if (i < 100) {
        if (i < 10)
          return 1;

        return 2;
      }

      if (i < 1000)
        return 3;

      return 4;
    }

    return 5;
  }

  /**
   * Returns the precision (the number of digits) of the specified {@code int}
   * value.
   *
   * @param n The {@code int} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code int}
   *         value.
   */
  public static byte precision(int n) {
    n = Math.abs(n);
    if (n < 1000000000) {
      if (n < 10000) {
        if (n < 100) {
          if (n < 10) {
            // Special case for Integer.MIN_VALUE, because Math.abs() keeps it negative
            if (n < 0)
              return 10;

            return 1;
          }

          return 2;
        }

        if (n < 1000)
          return 3;

        return 4;
      }

      if (n < 10000000) {
        if (n < 100000)
          return 5;

        if (n < 1000000)
          return 6;

        return 7;
      }

      if (n < 100000000)
        return 8;

      return 9;
    }

    return 10;
  }

  /**
   * Returns the precision (the number of digits) of the specified {@code long}
   * value.
   *
   * @param n The {@code long} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code long}
   *         value.
   */
  public static byte precision(long n) {
    n = Math.abs(n);
    if (n < 1000000000) {
      if (n < 10000) {
        if (n < 100) {
          if (n < 10) {
            // Special case for Long.MIN_VALUE, because Math.abs() keeps it negative
            if (n < 0)
              return 19;

            return 1;
          }

          return 2;
        }

        if (n < 1000)
          return 3;

        return 4;
      }

      if (n < 10000000) {
        if (n < 100000)
          return 5;

        if (n < 1000000)
          return 6;

        return 7;
      }

      if (n < 100000000)
        return 8;

      return 9;
    }

    if (n < 1000000000000000L) {
      if (n < 1000000000000L) {
        if (n < 10000000000L)
          return 10;

        if (n < 100000000000L)
          return 11;

        return 12;
      }

      if (n < 10000000000000L)
        return 13;

      if (n < 100000000000000L)
        return 14;

      return 15;
    }

    if (n < 100000000000000000L) {
      if (n < 10000000000000000L)
        return 16;

      return 17;
    }

    if (n < 1000000000000000000L)
      return 18;

    return 19;
  }

  /**
   * Returns the precision (the number of digits) of the specified
   * {@link BigInteger} value.
   *
   * @param n The {@link BigInteger} value whose number of digits is to be
   *          returned.
   * @return The count of the number of digits in the specified
   *         {@link BigInteger} value.
   */
  public static int precision(final BigInteger n) {
    if (n.signum() == 0)
      return 1;

    /*
     * Same idea as the long version, but we need a better approximation of
     * log10(2). Using 646456993/2^31 is accurate up to max possible reported
     * bitLength.
     */
    final int p = (int)(((n.bitLength() + 1) * 646456993L) >>> 31);
    return n.abs().compareTo(BigInteger.TEN.pow(p)) < 0 ? p : p + 1;
  }

  /**
   * Returns the precision (the number of digits) of the specified
   * {@link BigDecimal} value.
   *
   * @param n The {@link BigDecimal} value whose number of digits is to be
   *          returned.
   * @return The count of the number of digits in the specified {link
   *         BigDecimal} value.
   */
  public static int precision(final BigDecimal n) {
    return n.signum() == 0 ? 1 : n.precision();
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code byte} value.
   *
   * @param n The {@code byte} value whose number of trailing zeroes is to
   *          be returned.
   * @return The count of trailing zeroes in the specified {@code byte} value.
   */
  public static byte trailingZeroes(final byte n) {
    return (byte)(n == 0 ? 1 : n % 10 != 0 ? 0 : n % 100 != 0 ? 1 : 2);
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code short} value.
   *
   * @param n The {@code short} value whose number of trailing zeroes is to
   *          be returned.
   * @return The count of trailing zeroes in the specified {@code short} value.
   */
  public static byte trailingZeroes(final short n) {
    if (n == 0)
      return 1;

    if (n % 10000 != 0) {
      if (n % 100 != 0) {
        if (n % 10 != 0)
          return 0;

        return 1;
      }

      if (n % 1000 != 0)
        return 2;

      return 3;
    }

    return 4;
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code int} value.
   *
   * @param n The {@code int} value whose number of trailing zeroes is to
   *          be returned.
   * @return The count of trailing zeroes in the specified {@code int} value.
   */
  public static byte trailingZeroes(final int n) {
    if (n == 0)
      return 1;

    if (n % 1000000000 != 0) {
      if (n % 10000 != 0) {
        if (n % 100 != 0) {
          if (n % 10 != 0)
            return 0;

          return 1;
        }

        if (n % 1000 != 0)
          return 2;

        return 3;
      }

      if (n % 10000000 != 0) {
        if (n % 100000 != 0)
          return 4;

        if (n % 1000000 != 0)
          return 5;

        return 6;
      }

      if (n % 100000000 != 0)
        return 7;

      return 8;
    }

    return 9;
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code long} value.
   *
   * @param n The {@code long} value whose number of trailing zeroes is to
   *          be returned.
   * @return The count of trailing zeroes in the specified {@code long} value.
   */
  public static byte trailingZeroes(final long n) {
    if (n == 0)
      return 1;

    if (n % 1000000000 != 0) {
      if (n % 10000 != 0) {
        if (n % 100 != 0) {
          if (n % 10 != 0)
            return 0;

          return 1;
        }

        if (n % 1000 != 0)
          return 2;

        return 3;
      }

      if (n % 10000000 != 0) {
        if (n % 100000 != 0)
          return 4;

        if (n % 1000000 != 0)
          return 5;

        return 6;
      }

      if (n % 100000000 != 0)
        return 7;

      return 8;
    }

    if (n % 1000000000000000L != 0) {
      if (n % 1000000000000L != 0) {
        if (n % 10000000000L != 0)
          return 9;

        if (n % 100000000000L != 0)
          return 10;

        return 11;
      }

      if (n % 10000000000000L != 0)
        return 12;

      if (n % 100000000000000L != 0)
        return 13;

      return 14;
    }

    if (n % 100000000000000000L != 0) {
      if (n % 10000000000000000L != 0)
        return 15;

      return 16;
    }

    if (n % 1000000000000000000L != 0)
      return 17;

    return 18;
  }

  /**
   * Returns the signum of the provided {@code int}.
   *
   * @param a The {@code int} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided
   *         {@code int} is negative, zero or positive.
   */
  public static int signum(final int a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code long}.
   *
   * @param a The {@code long} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided
   *         {@code long} is negative, zero or positive.
   */
  public static int signum(final long a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  private Numbers() {
  }
}