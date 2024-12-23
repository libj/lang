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

import static org.libj.lang.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;

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
     * Return an <i>unsigned</i> {@code byte} equal to the value of the argument.
     *
     * @param int8 The unsigned 8-bit magnitude of the {@code long} to be returned.
     * @return An <i>unsigned</i> {@code short} equal to the value of the argument.
     * @throws ArithmeticException If {@code int8} is negative or is too big to fit in 8 bits of an unsigned {@code byte}.
     */
    public static byte toUINT8(final short int8) {
      if ((int8 >> 8) != 0)
        throw new ArithmeticException(int8 + " is too big to fit in 8 bits of an unsigned byte");

      return (byte)int8;
    }

    /**
     * Return an <i>unsigned</i> {@code short} equal to the value of the argument.
     *
     * @param int16 The unsigned 16-bit magnitude of the {@code long} to be returned.
     * @return An <i>unsigned</i> {@code int} equal to the value of the argument.
     * @throws ArithmeticException If {@code int16} is negative or is too big to fit in 16 bits of an unsigned {@code short}.
     */
    public static short toUINT16(final int int16) {
      if ((int16 >> 16) != 0)
        throw new ArithmeticException(int16 + " is too big to fit in 16 bits of an unsigned short");

      return (short)int16;
    }

    /**
     * Return an <i>unsigned</i> {@code int} equal to the value of the argument.
     *
     * @param int32 The unsigned 32-bit magnitude of the {@code long} to be returned.
     * @return An <i>unsigned</i> {@code long} equal to the value of the argument.
     * @throws ArithmeticException If {@code int32} is negative or is too big to fit in 32 bits of an unsigned {@code int}.
     */
    public static int toUINT32(final long int32) {
      if ((int32 >> 32) != 0)
        throw new ArithmeticException(int32 + " is too big to fit in 32 bits of an unsigned int");

      return (int)int32;
    }

    /**
     * Return an <i>unsigned</i> {@code long} equal to the value of the argument.
     *
     * @param int64 The unsigned 64-bit magnitude of the {@link BigInteger} to be returned.
     * @return An <i>unsigned</i> {@code long} equal to the value of the argument.
     * @throws ArithmeticException If {@code int64} is negative or is too big to fit in 64 bits of an unsigned {@code long}.
     */
    public static long toUINT64(final BigInteger int64) {
      if (int64.signum() < 0)
        throw new ArithmeticException(int64 + " must be positive");

      if (int64.bitLength() > 64)
        throw new ArithmeticException(int64 + " is too big to fit in 64 bits of an unsigned long");

      return int64.longValue();
    }

    /**
     * Returns the unsigned representation of the provided {@link BigInteger} as a {@code byte} array. The provided {@link BigInteger}
     * must be positive.
     *
     * @param bigInteger The signed value.
     * @return The unsigned representation of the signed value as a {@code byte} array.
     * @throws NullPointerException If {@code bigInteger} is null.
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
     * @param uint64 The unsigned magnitude of the {@link BigInteger} to be returned.
     * @return A {@link BigInteger} equal to the unsigned value of the argument.
     */
    public static BigInteger toUnsignedBigInteger(final long uint64) {
      return BigIntegers.valueOf(1, uint64);
    }

    /**
     * Return a {@link BigInteger} equal to the unsigned value of the argument.
     *
     * @param uint The unsigned magnitude of the {@link BigInteger} to be returned.
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
   * Utility functions for the encoding and decoding of "Composite values" in primitive types. A "Composite value" in a primitive type
   * is one that contains multiple values of a smaller sized primitive type. For example, a {@code short} is 16 bits in size, allowing
   * it to represent a composite value of 2 {@code byte}s, since a {@code byte} is 8 bits in size.
   */
  public static final class Composite {
    /**
     * Encodes two {@code int}s into a {@code long}.
     *
     * @param a The first {@code int}.
     * @param b The second {@code int}.
     * @return A composite {@code long} representing two {@code int}.
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
     * @return A composite {@code long} representing four {@code short}.
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
     * @return A composite {@code long} representing eighth {@code byte}.
     */
    public static long encode(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f, final byte g, final byte h) {
      return ((long)h << Byte.SIZE * 7) & 0xff00000000000000L | ((long)g << Byte.SIZE * 6) & 0xff000000000000L | ((long)f << Byte.SIZE * 5) & 0xff0000000000L | ((long)e << Byte.SIZE * 4) & 0xff00000000L | ((long)d << Byte.SIZE * 3) & 0xff000000L | ((long)c << Byte.SIZE * 2) & 0xff0000L | ((long)b << Byte.SIZE) & 0xff00L | a & 0xffL;
    }

    /**
     * Encodes two {@code float}s into a {@code long}.
     *
     * @param a The first {@code float}.
     * @param b The second {@code float}.
     * @return A composite {@code long} representing two {@code float}s.
     */
    public static long encode(final float a, final float b) {
      return encode(Float.floatToIntBits(a), Float.floatToIntBits(b));
    }

    /**
     * Encodes a {@code float} and an {@code int} into a {@code long}.
     *
     * @param a The {@code float}.
     * @param b The {@code int}.
     * @return A composite {@code long} representing a {@code float} and an {@code int}.
     */
    public static long encode(final float a, final int b) {
      return encode(Float.floatToIntBits(a), b);
    }

    /**
     * Encodes a {@code float} and two {@code short}s into a {@code long}.
     *
     * @param a The {@code float}.
     * @param b The first {@code short}.
     * @param c The second {@code short}.
     * @return A composite {@code long} representing a {@code float} and two {@code short}s.
     */
    public static long encode(final float a, final short b, final short c) {
      return encode(Float.floatToIntBits(a), encode(b, c));
    }

    /**
     * Encodes a {@code float} and four {@code bytes}s into a {@code long}.
     *
     * @param a The {@code float}.
     * @param b The first {@code byte}.
     * @param c The second {@code byte}.
     * @param d The third {@code byte}.
     * @param e The fourth {@code byte}.
     * @return A composite {@code long} representing a {@code float} and four {@code byte}s.
     */
    public static long encode(final float a, final byte b, final byte c, final byte d, final byte e) {
      return encode(Float.floatToIntBits(a), encode(encode(b, c), encode(d, e)));
    }

    /**
     * Encodes an {@code int} and a {@code float} into a {@code long}.
     *
     * @param a The {@code int}.
     * @param b The {@code float}.
     * @return A composite {@code long} representing an {@code int} and a {@code float}.
     */
    public static long encode(final int a, final float b) {
      return encode(a, Float.floatToIntBits(b));
    }

    /**
     * Encodes two {@code short}s into an {@code int}.
     *
     * @param a The first {@code short}.
     * @param b The second {@code short}.
     * @return A composite {@code int} representing two {@code short}.
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
     * @return A composite {@code int} representing two {@code byte}.
     */
    public static int encode(final byte a, final byte b, final byte c, final byte d) {
      return (d << Byte.SIZE * 3) & 0xff000000 | (c << Byte.SIZE * 2) & 0xff0000 | (b << Byte.SIZE) & 0xff00 | a & 0xff;
    }

    /**
     * Encodes two {@code byte}s into a {@code short}.
     *
     * @param a The first {@code byte}.
     * @param b The second {@code byte}.
     * @return A composite {@code short} representing two {@code byte}.
     */
    public static short encode(final byte a, final byte b) {
      return (short)((b << Byte.SIZE) & 0xff00 | a & 0xff);
    }

    /**
     * Decodes the {@code int} value at the specified position that is represented in the provided composite {@code long} value.
     *
     * @param val The composite {@code long} containing {@code int}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code int} value at the specified position that is represented in the provided composite {@code long} value.
     */
    public static int decodeInt(final long val, final int pos) {
      return (int)(val >> Integer.SIZE * pos);
    }

    /**
     * Decodes the {@code float} value at the specified position that is represented in the provided composite {@code long} value.
     *
     * @param val The composite {@code long} containing a {@code float} value.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code float} value at the specified position that is represented in the provided composite {@code long} value.
     */
    public static float decodeFloat(final long val, final int pos) {
      return Float.intBitsToFloat((int)(val >> Integer.SIZE * pos));
    }

    /**
     * Decodes the {@code short} value at the specified position that is represented in the provided composite {@code long} value.
     *
     * @param val The composite {@code long} containing {@code short}.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code short} value at the specified position that is represented in the provided composite {@code long} value.
     */
    public static short decodeShort(final long val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is represented in the provided composite {@code long} value.
     *
     * @param val The composite {@code long} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1, 2, 3, 4, 5, 6, 7).
     * @return The {@code byte} value at the specified position that is represented in the provided composite {@code long} value.
     */
    public static byte decodeByte(final long val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code short} value at the specified position that is represented in the provided composite {@code int} value.
     *
     * @param val The composite {@code int} containing {@code short}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code short} value at the specified position that is represented in the provided composite {@code int} value.
     */
    public static short decodeShort(final int val, final int pos) {
      return (short)((val >> Short.SIZE * pos) & 0xffff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is represented in the provided composite {@code int} value.
     *
     * @param val The composite {@code int} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1, 2, 3).
     * @return The {@code byte} value at the specified position that is represented in the provided composite {@code int} value.
     */
    public static byte decodeByte(final int val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    /**
     * Decodes the {@code byte} value at the specified position that is represented in the provided composite {@code short} value.
     *
     * @param val The composite {@code short} containing {@code byte}.
     * @param pos The position of the value to decode (0, 1).
     * @return The {@code byte} value at the specified position that is represented in the provided composite {@code short} value.
     */
    public static byte decodeByte(final short val, final int pos) {
      return (byte)((val >> Byte.SIZE * pos) & 0xff);
    }

    private Composite() {
    }
  }

  /**
   * Compares the specified numbers, returning a negative integer, zero, or a positive integer as the first argument is less than,
   * equal to, or greater than the second.
   * <p>
   * Null values are considered as less than non-null values.
   *
   * @param a The first {@link Number} to be compared.
   * @param b The second {@link Number} to be compared.
   * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
   */
  public static int compare(final Number a, final Number b) {
    return comparator.compare(a, b);
  }

  // anything past 63 is a guaranteed overflow with base > 1
  private static final int[] highestBitSet = {
    0, 1, 2, 2, 3, 3, 3, 3,
    4, 4, 4, 4, 4, 4, 4, 4,
    5, 5, 5, 5, 5, 5, 5, 5,
    5, 5, 5, 5, 5, 5, 5, 5,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
    255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
  };

  /** {@code double} representation of log(2) */
  public static final double LOG_2 = 0.6931471805599453;

  /** {@code double} representation of log(10) */
  public static final double LOG_10 = 2.302585092994046;

  /**
   * Returns the value of the specified base raised to the power of the specified exponent. The algorithm in this implementation takes
   * advantage of the whole values of the base and exponent to outperform {@link Math#pow(double,double)}.
   *
   * @param base The base.
   * @param exp The exponent.
   * @return The value of the specified base raised to the power of the specified exponent.
   * @throws ArithmeticException If the resulting value cannot be represented as a {@code long} due to overflow.
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
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns {@code null} if
   * the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s) {
    return parseByte(s, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code byte}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final Byte defaultValue) {
    return s == null ? defaultValue : parseByte0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns {@code null} if
   * the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final int fromIndex, final int toIndex) {
    return parseByte(s, fromIndex, toIndex, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code byte}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final int fromIndex, final int toIndex, final Byte defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseByte0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@code byte} representation to be parsed.
   * @param defaultValue The {@code byte} value to be returned if the sequence does not contain a parsable {@code byte}.
   * @return The {@code byte} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final CharSequence s, final byte defaultValue) {
    return s == null ? defaultValue : parseByte0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@code byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code byte} value to be returned if the sequence does not contain a parsable {@code byte}.
   * @return The {@code byte} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final CharSequence s, final int fromIndex, final int toIndex, final byte defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseByte0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@code byte} representation to be parsed.
   * @param defaultValue The {@code byte} value to be returned if the char array does not contain a parsable {@code byte}.
   * @return The {@code byte} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final char[] cbuf, final byte defaultValue) {
    return cbuf == null ? defaultValue : parseByte0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@code byte} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code byte} value to be returned if the char array does not contain a parsable {@code byte}.
   * @return The {@code byte} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final char[] cbuf, final int fromIndex, final int toIndex, final byte defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseByte0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final int radix) {
    if (s == null)
      return null;

    final int i = parseInt0(s, 0, s.length(), radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? null : (byte)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    return parseByte(s, fromIndex, toIndex, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@link Byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code byte}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Byte defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseByte0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static Byte parseByte0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Byte defaultValue) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? defaultValue : Byte.valueOf((byte)i);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@link Byte} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final char[] cbuf, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt0(cbuf, 0, cbuf.length, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? null : (byte)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@link Byte} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static Byte parseByte(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? null : (byte)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@code byte} representation to be parsed.
   * @param defaultValue The {@code byte} value to be returned if the sequence does not contain a parsable {@code byte}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final CharSequence s, final int radix, final byte defaultValue) {
    if (s == null)
      return defaultValue;

    final int i = parseInt0(s, 0, s.length(), radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? defaultValue : (byte)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code byte}.
   *
   * @param s A {@link CharSequence} containing the {@code byte} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code byte} value to be returned if the sequence does not contain a parsable {@code byte}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final byte defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseByte0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static byte parseByte0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final byte defaultValue) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? defaultValue : (byte)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@code byte} representation to be parsed.
   * @param defaultValue The {@code byte} value to be returned if the char array does not contain a parsable {@code byte}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code byte}.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final char[] cbuf, final int radix, final byte defaultValue) {
    if (cbuf == null)
      return defaultValue;

    final int i = parseInt0(cbuf, 0, cbuf.length, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? defaultValue : (byte)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Byte#parseByte(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code byte}.
   *
   * @param cbuf A {@code char} array containing the {@code byte} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code byte} value to be returned if the char array does not contain a parsable {@code byte}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code byte}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Byte#parseByte(String)
   */
  public static byte parseByte(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final byte defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseByte0(cbuf, fromIndex, toIndex, radix, defaultValue);
  }

  private static byte parseByte0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final byte defaultValue) {
    final int i = parseInt0(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Byte.MIN_VALUE || i > Byte.MAX_VALUE ? defaultValue : (byte)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s) {
    return parseShort(s, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code short}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final Short defaultValue) {
    return s == null ? defaultValue : parseShort0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex) {
    return parseShort(s, fromIndex, toIndex, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code short}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final Short defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseShort0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the sequence does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final short defaultValue) {
    return s == null ? defaultValue : parseShort0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the sequence does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final short defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseShort0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the char array does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or {@code defaultValue} if the char array does not contain a
   *         parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final short defaultValue) {
    return cbuf == null ? defaultValue : parseShort0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the char array does not contain a parsable {@code short}.
   * @return The {@code short} value represented by the argument, or {@code defaultValue} if the char array does not contain a
   *         parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final short defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseShort0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code null} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int radix) {
    if (s == null)
      return null;

    final int i = parseInt0(s, 0, s.length(), radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code null} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    return parseShort(s, fromIndex, toIndex, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@link Short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code short}.
   * @return The integer value represented by the argument, or {@code null} if the sequence does not contain a parsable {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Short defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseShort0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static Short parseShort0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Short defaultValue) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : Short.valueOf((short)i);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@link Short} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code short}.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final char[] cbuf, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt0(cbuf, 0, cbuf.length, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@link Short} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static Short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    final int i = parseInt(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? null : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the sequence does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
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
   * Parses the {@link CharSequence} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code short}.
   *
   * @param s A {@link CharSequence} containing the {@code short} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the sequence does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseShort0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static short parseShort0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    final int i = parseInt0(s, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation to be parsed.
   * @param defaultValue The {@code short} value to be returned if the char array does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
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
   * Parses the {@code char[]} argument as per the specification of {@link Short#parseShort(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code short}.
   *
   * @param cbuf A {@code char} array containing the {@code short} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code short} value to be returned if the char array does not contain a parsable {@code short}.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The integer value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code short}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Short#parseShort(String)
   */
  public static short parseShort(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseShort0(cbuf, fromIndex, toIndex, radix, defaultValue);
  }

  private static short parseShort0(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final short defaultValue) {
    final int i = parseInt0(cbuf, fromIndex, toIndex, radix, Integer.MIN_VALUE);
    return i < Short.MIN_VALUE || i > Short.MAX_VALUE ? defaultValue : (short)i;
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s) {
    return parseInteger(s, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final Integer defaultValue) {
    return s == null ? defaultValue : parseInteger0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex) {
    return parseInteger(s, fromIndex, toIndex, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex, final Integer defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseInteger0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation to be parsed.
   * @param defaultValue The {@code int} value to be returned if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int defaultValue) {
    return s == null ? defaultValue : parseInt0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code int} value to be returned if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int fromIndex, final int toIndex, final int defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseInt0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation to be parsed.
   * @param defaultValue The {@code int} value to be returned if the char array does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int defaultValue) {
    return cbuf == null ? defaultValue : parseInt0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code int} value to be returned if the char array does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int fromIndex, final int toIndex, final int defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseInt0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int radix, final int defaultValue) {
    return s == null ? defaultValue : parseInt0(s, 0, s.length(), radix, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@code int} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
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
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the char array does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int radix, final int defaultValue) {
    return cbuf == null ? defaultValue : parseInt0(cbuf, 0, cbuf.length, radix, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@code int} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code int} value to be returned if the char array does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static int parseInt(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final int defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
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
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code null} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int radix) {
    return parseInteger(s, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int radix, final Integer defaultValue) {
    return s == null ? defaultValue : parseInteger0(s, 0, s.length(), radix, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code null} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    return parseInteger(s, fromIndex, toIndex, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code int}.
   *
   * @param s A {@link CharSequence} containing the {@link Integer} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code int}.
   * @return The {@code int} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Integer defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseInteger0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static Integer parseInteger0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Integer defaultValue) {
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
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@link Integer} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code int}.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final char[] cbuf, final int radix) {
    return cbuf == null ? null : parseInteger0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Integer#parseInt(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code int}.
   *
   * @param cbuf A {@code char} array containing the {@link Integer} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code int} value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code int}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Integer#parseInt(String)
   */
  public static Integer parseInteger(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
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
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns {@code null} if
   * the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s) {
    return parseLong(s, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final Long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns {@code null} if
   * the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex) {
    return parseLong(s, fromIndex, toIndex, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final Long defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseLong0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation to be parsed.
   * @param defaultValue The {@code long} value to be returned if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param defaultValue The {@code long} value to be returned if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final long defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseLong0(s, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation to be parsed.
   * @param defaultValue The {@code long} value to be returned if the char array does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final long defaultValue) {
    return cbuf == null ? defaultValue : parseLong0(cbuf, 0, cbuf.length, 10, defaultValue);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String)}, but returns {@code defaultValue}
   * if the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param defaultValue The {@code long} value to be returned if the char array does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final long defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
    return parseLong0(cbuf, fromIndex, toIndex, 10, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int radix) {
    return parseLong(s, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int radix, final Long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), radix, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns {@code null}
   * if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final int radix) {
    return parseLong(s, fromIndex, toIndex, radix, null);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@link Long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code null} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Long defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseLong0(s, fromIndex, toIndex, radix, defaultValue);
  }

  private static Long parseLong0(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final Long defaultValue) {
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
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@link Long} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final char[] cbuf, final int radix) {
    return cbuf == null ? null : parseLong0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String,int)}, but returns {@code null} if
   * the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@link Long} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @return The {@code long} value represented by the argument, or {@code null} if the char array does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static Long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final int radix) {
    if (cbuf == null)
      return null;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
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
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int radix, final long defaultValue) {
    return s == null ? defaultValue : parseLong0(s, 0, s.length(), radix, defaultValue);
  }

  /**
   * Parses the {@link CharSequence} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the sequence does not contain a parsable {@code long}.
   *
   * @param s A {@link CharSequence} containing the {@code long} representation to be parsed.
   * @param fromIndex The index in {@code s} from which to start parsing (inclusive).
   * @param toIndex The index in {@code s} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the sequence does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the sequence does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final CharSequence s, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (s == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "s.length()", s.length());
    return parseLong0(s, fromIndex, toIndex, radix, defaultValue);
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
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation to be parsed.
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the char array does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int radix, final long defaultValue) {
    return cbuf == null ? defaultValue : parseLong0(cbuf, 0, cbuf.length, radix);
  }

  /**
   * Parses the {@code char[]} argument as per the specification of {@link Long#parseLong(String,int)}, but returns
   * {@code defaultValue} if the char array does not contain a parsable {@code long}.
   *
   * @param cbuf A {@code char} array containing the {@code long} representation to be parsed.
   * @param fromIndex The index in {@code cbuf} from which to start parsing (inclusive).
   * @param toIndex The index in {@code cbuf} at which to end parsing (exclusive).
   * @param radix The radix to be used while parsing {@code s}.
   * @param defaultValue The {@code long} value to be returned if the char array does not contain a parsable {@code long}.
   * @return The {@code long} value represented by the argument, or {@code defaultValue} if the char array does not contain a parsable
   *         {@code long}.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
   * @see Long#parseLong(String)
   */
  public static long parseLong(final char[] cbuf, final int fromIndex, final int toIndex, final int radix, final long defaultValue) {
    if (cbuf == null)
      return defaultValue;

    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "cbuf.length", cbuf.length);
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
   * Parses the string argument as per the specification of {@link Float#parseFloat(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be parsed.
   * @return The {@code float} value represented by the argument, or {@code null} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static Float parseFloat(final String s) {
    return parseFloat(s, null);
  }

  /**
   * Parses the string argument as per the specification of {@link Float#parseFloat(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code float}.
   * @return The {@code float} value represented by the argument, or {@code null} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static Float parseFloat(final String s, final Float defaultValue) {
    if (s == null)
      return defaultValue;

    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if the implementation is copied.
    try {
      return Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parses the string argument as per the specification of {@link Float#parseFloat(String)}, but returns {@code defaultValue} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s A {@link String} containing the {@link Float} representation to be parsed.
   * @param defaultValue The {@code float} value to be returned if the string does not contain a parsable {@code float}.
   * @return The {@code float} value represented by the argument, or {@code defaultValue} if the string does not contain a parsable
   *         {@code float}.
   * @see Float#parseFloat(String)
   */
  public static float parseFloat(final String s, final float defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if the implementation is copied.
    try {
      return s == null ? defaultValue : Float.parseFloat(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parses the string argument as per the specification of {@link Double#parseDouble(String)}, but returns {@code null} if the string
   * does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to be parsed.
   * @return The {@code double} value represented by the argument, or {@code null} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static Double parseDouble(final String s) {
    return parseDouble(s, null);
  }

  /**
   * Parses the string argument as per the specification of {@link Double#parseDouble(String)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to be parsed.
   * @param defaultValue The value to return if the sequence does not contain a parsable {@code double}.
   * @return The {@code double} value represented by the argument, or {@code null} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static Double parseDouble(final String s, final Double defaultValue) {
    if (s == null)
      return defaultValue;

    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if the implementation is copied.
    try {
      return Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Parses the string argument as per the specification of {@link Double#parseDouble(String)}, but returns {@code defaultValue} if
   * the string does not contain a parsable {@code double}.
   *
   * @param s A {@link String} containing the {@link Double} representation to be parsed.
   * @param defaultValue The {@code double} value to be returned if the string does not contain a parsable {@code double}.
   * @return The {@code double} value represented by the argument, or {@code defaultValue} if the string does not contain a parsable
   *         {@code double}.
   * @see Double#parseDouble(String)
   */
  public static double parseDouble(final String s, final double defaultValue) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if the implementation is copied.
    try {
      return s == null ? defaultValue : Double.parseDouble(s);
    }
    catch (final NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Returns an {@code int} array representation of the values in the specified {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not contain a parsable integer.
   */
  public static int[] parseInt(final String ... s) {
    final int[] values = new int[s.length];
    for (int i = 0, i$ = s.length; i < i$; ++i) // [A]
      values[i] = Integer.parseInt(s[i]);

    return values;
  }

  /**
   * Returns a {@code double} array representation of the values in the specified {@link String} array.
   *
   * @param s The {@link String} array.
   * @return An {@code int} array representation of the values in the specified {@link String} array.
   * @throws NumberFormatException If a string in the specified array does not contain a parsable double.
   */
  public static double[] parseDouble(final String ... s) {
    final double[] values = new double[s.length];
    for (int i = 0, i$ = s.length; i < i$; ++i) // [A]
      values[i] = Double.parseDouble(s[i]);

    return values;
  }

  /**
   * Returns a {@link Number} of type {@code <T>} of the value for the provided string.
   *
   * @param <N> The type of {@link Number} to be returned.
   * @param s The number to get the value of.
   * @param as The class representing the type of {@link Number} to be returned.
   * @return a {@link Number} of type {@code <T>} of the value for the provided string.
   * @throws NumberFormatException If the string does not contain a parsable {@link Number} of type {@code <T>}.
   * @throws UnsupportedOperationException If the specified {@link Class} represents a type that is not one of: <br>
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
  public static <N extends Number> N parseNumber(final String s, final Class<N> as) {
    if (float.class == as || Float.class == as)
      return (N)Float.valueOf(s);

    if (double.class == as || Double.class == as)
      return (N)Double.valueOf(s);

    if (byte.class == as || Byte.class == as)
      return (N)Byte.valueOf(s);

    if (short.class == as || Short.class == as)
      return (N)Short.valueOf(s);

    if (int.class == as || Integer.class == as)
      return (N)Integer.valueOf(s);

    if (long.class == as || Long.class == as)
      return (N)Long.valueOf(s);

    if (BigInteger.class.isAssignableFrom(as))
      return (N)new BigInteger(s);

    if (BigDecimal.class.isAssignableFrom(as))
      return (N)new BigDecimal(s);

    throw new UnsupportedOperationException("Unsupported Number type: " + as.getName());
  }

  private static final String MIN_LONG = "-9223372036854775808";
  private static final String MAX_LONG = "9223372036854775807";

  public static Number parseNumberWhole(final String str) {
    final int len;
    if (str == null || (len = str.length()) == 0)
      return null;

    char ch = str.charAt(0);
    final boolean neg = ch == '-';
    final int start = neg || ch == '+' ? 1 : 0;
    final int digits = len - start;

    if (digits < 3)
      return parseByte(str);

    if (digits == 3) {
      final Integer i = parseInteger(str);
      if (i == 0)
        return null;

      final byte b = i.byteValue();
      if (b == i)
        return b;

      return i.shortValue();
    }

    if (digits < 5)
      return parseShort(str);

    if (digits == 5) {
      final Integer i = parseInteger(str);
      if (i == null)
        return null;

      final short s = i.shortValue();
      if (s == i)
        return s;

      return i;
    }

    if (digits < 10)
      return parseInteger(str);

    if (digits == 10) {
      final Long l = parseLong(str);
      if (l == null)
        return null;

      final int i = l.intValue();
      if (i == l)
        return i;

      return l;
    }

    if (digits < 19)
      return parseLong(str);

    if (digits == 19) {
      final String limit = neg ? MIN_LONG : MAX_LONG;
      for (int i = start; i < len; ++i) { // [$]
        ch = str.charAt(i);
        if (!Character.isDigit(ch))
          throw new NumberFormatException(str);

        if (ch > limit.charAt(i))
          return new BigInteger(str);
      }

      return parseLong(str);
    }

    return isNumber(str) ? new BigInteger(str) : null;
  }

  /**
   * Returns the specified {@link Number} cast to the provided {@link Class Class&lt;T&gt;}.
   *
   * @param <N> The type parameter of the cast class.
   * @param n The {@link Number} to be cast.
   * @param as The {@link Class} to which the specified {@link Number} is to be cast.
   * @return The specified {@link Number} cast to the provided {@link Class Class&lt;T&gt;}.
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N cast(final Number n, final Class<N> as) {
    if (n == null)
      return null;

    if (long.class == as || Long.class == as)
      return (N)Long.valueOf(n.longValue());

    if (int.class == as || Integer.class == as)
      return (N)Integer.valueOf(n.intValue());

    if (short.class == as || Short.class == as)
      return (N)Short.valueOf(n.shortValue());

    if (byte.class == as || Byte.class == as)
      return (N)Byte.valueOf(n.byteValue());

    if (double.class == as || Double.class == as)
      return (N)Double.valueOf(n.doubleValue());

    if (float.class == as || Float.class == as)
      return (N)Float.valueOf(n.floatValue());

    if (BigInteger.class == as)
      return (N)new BigInteger(n.toString());

    if (BigDecimal.class == as)
      return (N)new BigDecimal(n.toString());

    throw new UnsupportedOperationException("Unsupported type: " + as.getName());
  }

  /**
   * Tests whether the specified string represents a number, or a number with a fraction of two numbers (i.e. {@code 23 3/4}).
   * <p>
   * This method supports exponent form (i.e. {@code 3.2E-5}).
   *
   * @param s The string to test.
   * @return {@code true} if the specified string represents a number, or a number with a fraction of two numbers.
   * @see #isNumber(String)
   */
  public static boolean isNumberWithFraction(String s) {
    final int len;
    if (s == null || (len = (s = s.trim()).length()) == 0)
      return false;

    int spaces = 0;
    for (int i = 0; i < len; ++i)
      if (s.charAt(i) == ' ' && ++spaces > 2)
        return false;

    final String[] parts = Strings.split(s, ' ');
    if (parts.length == 2) {
      final int slash = parts[1].indexOf('/');
      if (slash < 0)
        return false;

      return isNumber(parts[0], false) && isNumber(parts[1], true);
    }

    return isNumber(parts[0], true);
  }

  /**
   * Tests whether the specified string represents a number.
   * <p>
   * This method supports exponent form (i.e. {@code 3.2E-5}).
   *
   * @param s The string to test.
   * @return {@code true} if the specified string represents a number.
   * @see #isNumberWithFraction(String)
   */
  public static boolean isNumber(String s) {
    return isNumber(s, false);
  }

  public static boolean isNumber(String s, final boolean isFraction) {
    final int len;
    if (s == null || (len = (s = s.trim()).length()) == 0)
      return false;

    boolean dotEncountered = false;
    boolean expEncountered = false;
    boolean minusEncountered = false;
    boolean slashEncountered = false;
    int factor = 0;
    for (int i = len - 1; i >= 0; --i) { // [N]
      final char c = s.charAt(i);
      if (c < '0') {
        if (c == '/') {
          if (!isFraction || dotEncountered || expEncountered || minusEncountered || slashEncountered)
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
        else if (c != '+') {
          return false;
        }
      }
      else if ('9' < c) {
        if (c != 'E' && c != 'e')
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
   * @throws NumberFormatException If the specified radix is outside the range of legal values.
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
   * @return {@code true} if the character is a digit in the provided radix; {@code false} otherwise.
   * @throws NumberFormatException If the specified radix is outside the range of legal values.
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
   * Returns the numeric value of the specified character representing a digit. The specified character must be within the following
   * ranges:
   * <ol>
   * <li>{@code '0' <= digit && digit <= '9'}</li>
   * <li>{@code 'a' <= digit && digit <= 'a'}</li>
   * <li>{@code 'A' <= digit && digit <= 'Z'}</li>
   * </ol>
   * <p>
   * If the specified character is outside these ranges, the value {@code -digit} is returned.
   *
   * @param digit The character representing a digit.
   * @param radix The radix to be used to transform the character.
   * @return The numeric value of the specified character representing a digit.
   * @throws NumberFormatException If the specified radix is outside the range of legal values.
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
   * Returns {@code true} if the specified {@code float} can be represented as a whole number without loss of precision.
   *
   * @param n The {@code float} to test.
   * @return {@code true} if the specified {@code float} can be represented as a whole number without loss of precision.
   */
  public static boolean isWhole(final float n) {
    return (int)n == n;
  }

  /**
   * Returns {@code true} if the specified {@code double} can be represented as a whole number without loss of precision.
   *
   * @param n The {@code double} to test.
   * @return {@code true} if the specified {@code double} can be represented as a whole number without loss of precision.
   */
  public static boolean isWhole(final double n) {
    return (long)n == n;
  }

  /**
   * Returns the multiple of the specified values, throwing an {@link ArithmeticException} if the resulting value cannot be
   * represented as a {@code long} due to overflow.
   *
   * @param a The first value.
   * @param b The second value.
   * @return The multiple of the specified values.
   * @throws ArithmeticException If the resulting value cannot be represented as a {@code long} due to overflow.
   */
  public static long checkedMultiple(final long a, final long b) {
    final long maximum = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
    if (a != 0 && (b > 0 && b > maximum / a || b < 0 && b < maximum / a))
      throw new ArithmeticException("long overflow");

    return a * b;
  }

  /**
   * Returns a string representation of the specified {@code double} rounded to the provided number of decimal places.
   *
   * @param n The {@code double}.
   * @param decimals The number of decimal places.
   * @return A string representation of the specified {@code double} rounded to the provided number of decimal places.
   */
  public static String toString(final double n, final int decimals) {
    if (decimals == 0)
      return String.valueOf((int)Math.round(n));

    if (decimals == 1)
      return String.valueOf(Math.round(n * 10) / 10d);

    if (decimals == 2)
      return String.valueOf(Math.round(n * 100) / 100d);

    if (decimals == 3)
      return String.valueOf(Math.round(n * 1000) / 1000d);

    if (decimals == 4)
      return String.valueOf(Math.round(n * 10000) / 10000d);

    if (decimals == 5)
      return String.valueOf(Math.round(n * 100000) / 100000d);

    if (decimals == 6)
      return String.valueOf(Math.round(n * 1000000) / 1000000d);

    final double factor = StrictMath.pow(10, decimals);
    return String.valueOf(Math.round(n * factor) / factor);
  }

  private abstract static class StripTrailingZeros<S extends CharSequence> {
    abstract S substring(S s, int end);
    abstract S delete(S s, int start, int end);
  }

  private static final StripTrailingZeros<String> string = new StripTrailingZeros<String>() {
    @Override
    String substring(final String s, final int end) {
      return s.substring(0, end);
    }

    @Override
    String delete(final String s, final int start, final int end) {
      return s.substring(0, start) + s.substring(end);
    }
  };

  private static final StripTrailingZeros<StringBuilder> stringBuilder = new StripTrailingZeros<StringBuilder>() {
    @Override
    StringBuilder substring(final StringBuilder s, final int end) {
      return s.delete(end, s.length());
    }

    @Override
    StringBuilder delete(final StringBuilder s, final int start, final int end) {
      return s.delete(start, end);
    }
  };

  private static final StripTrailingZeros<StringBuffer> stringBuffer = new StripTrailingZeros<StringBuffer>() {
    @Override
    StringBuffer substring(final StringBuffer s, final int end) {
      return s.delete(end, s.length());
    }

    @Override
    StringBuffer delete(final StringBuffer s, final int start, final int end) {
      return s.delete(start, end);
    }
  };

  public static <T extends CharSequence> T stripTrailingZeros(final T number, final StripTrailingZeros<T> x) {
    final int len;
    if (number == null || (len = number.length()) == 0)
      return number;

    char ch;
    int i = len - 1;
    do {
      ch = number.charAt(i);
      if (ch != '0' && ch != '.')
        break;
    }
    while (--i > 0);

    if (++i == len)
      return number;

    int j = i;
    do {
      ch = number.charAt(j);
      if (ch == 'E' || ch == 'e') {
        i = j;
        do {
          ch = number.charAt(--j);
          if (ch != '0' && ch != '.')
            break;
        }
        while (j > 0);
        return j < 0 ? number : x.delete(number, j + 1, i);
      }

      if (ch == '.')
        return x.substring(number, i);
    }
    while (--j >= 0);
    return number;
  }

  /**
   * Strips trailing zeroes after a decimal point in the specified {@link String}.
   * <p>
   * This method accepts a string that represents a number. The behavior of this method is undefined for a {@link String} that does
   * not represent a number.
   *
   * @param number The {@link String} representing a number.
   * @return The {@link String} with trailing zeroes stripped if the string represents a decimal number; otherwise the original
   *         {@link String} is returned.
   */
  public static String stripTrailingZeros(final String number) {
    return stripTrailingZeros(number, string);
  }

  /**
   * Strips trailing zeroes after a decimal point in the specified {@link StringBuffer}.
   * <p>
   * This method accepts a string that represents a number. The behavior of this method is undefined for a {@link StringBuffer} that
   * does not represent a number.
   *
   * @param number The {@link String} representing a number.
   * @return The {@link StringBuffer} with trailing zeroes stripped if the string represents a decimal number; otherwise the returned
   *         {@link StringBuffer} is unchanged.
   */
  public static StringBuffer stripTrailingZeros(final StringBuffer number) {
    return stripTrailingZeros(number, stringBuffer);
  }

  /**
   * Strips trailing zeroes after a decimal point in the specified {@link StringBuilder}.
   * <p>
   * This method accepts a string that represents a number. The behavior of this method is undefined for a {@link StringBuilder} that
   * does not represent a number.
   *
   * @param number The {@link String} representing a number.
   * @return The {@link StringBuilder} with trailing zeroes stripped if the string represents a decimal number; otherwise the returned
   *         {@link StringBuilder} is unchanged.
   */
  public static StringBuilder stripTrailingZeros(final StringBuilder number) {
    return stripTrailingZeros(number, stringBuilder);
  }

  /**
   * Determines if the difference of the specified {@link Number} values is less than the provided epsilon.
   *
   * @param a The first {@link Number}.
   * @param b The second {@link Number}.
   * @param epsilon The epsilon.
   * @return {@code true} if the difference of the specified {@link Number} values is less than the provided epsilon.
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
   * Returns the {@link BigDecimal} representation of the specified {@link Number}.
   *
   * @param n The {@link Number} to convert to a {@link BigDecimal}.
   * @return The {@link BigDecimal} representation of the specified {@link Number}.
   * @throws NullPointerException If {@code n} is null.
   */
  public static BigDecimal toBigDecimal(final Number n) {
    Objects.requireNonNull(n);

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
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static BigDecimal average(final BigDecimal ... numbers) {
    BigDecimal sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum = sum.add(numbers[i]);

    return sum.divide(BigDecimal.valueOf(numbers.length), RoundingMode.HALF_UP);
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static BigDecimal average(final BigInteger ... numbers) {
    BigDecimal sum = new BigDecimal(numbers[0]);
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum = sum.add(new BigDecimal(numbers[i]));

    return sum.divide(BigDecimal.valueOf(numbers.length), RoundingMode.HALF_UP);
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If {@code numbers} is an empty array.
   */
  public static double average(final byte ... numbers) {
    long sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static double average(final short ... numbers) {
    long sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static double average(final int ... numbers) {
    long sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static double average(final long ... numbers) {
    long sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static double average(final float ... numbers) {
    double sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
      sum += numbers[i];

    return sum / numbers.length;
  }

  /**
   * Computes the average of the specified numbers.
   *
   * @param numbers The numbers to be used to compute the average.
   * @return The average of the specified numbers.
   * @throws NullPointerException If {@code numbers} is null.
   * @throws ArrayIndexOutOfBoundsException If If {@code numbers} is an empty array.
   */
  public static double average(final double ... numbers) {
    double sum = numbers[0];
    for (int i = 1, i$ = numbers.length; i < i$; ++i) // [A]
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
   * Tests whether the provided {@link Class Class&lt;? extends Number&gt;} is an whole number type.
   *
   * @param cls The {@link Class Class&lt;? extends Number&gt;} to test.
   * @return Whether the provided {@link Class Class&lt;? extends Number&gt;} is an whole number type.
   */
  public static boolean isWholeNumberType(final Class<? extends Number> cls) {
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
   * Returns the precision (the number of digits) of the specified {@code byte} value.
   *
   * @param n The {@code byte} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code byte} value.
   */
  public static byte precision(final byte n) {
    final int i = Math.abs(n);
    return (byte)(i < 10 ? 1 : i < 100 ? 2 : 3);
  }

  /**
   * Returns the precision (the number of digits) of the specified {@code short} value.
   *
   * @param n The {@code short} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code short} value.
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
   * Returns the precision (the number of digits) of the specified {@code int} value.
   *
   * @param n The {@code int} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code int} value.
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
   * Returns the precision (the number of digits) of the specified {@code long} value.
   *
   * @param n The {@code long} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@code long} value.
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
   * Returns the precision (the number of digits) of the specified {@link BigInteger} value.
   *
   * @param n The {@link BigInteger} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {@link BigInteger} value.
   */
  public static int precision(final BigInteger n) {
    if (n.signum() == 0)
      return 1;

    /*
     * Same idea as the long version, but we need a better approximation of log10(2). Using 646456993/2^31 is accurate up to max
     * possible reported bitLength.
     */
    final int p = (int)(((n.bitLength() + 1) * 646456993L) >>> 31);
    return n.abs().compareTo(BigInteger.TEN.pow(p)) < 0 ? p : p + 1;
  }

  /**
   * Returns the precision (the number of digits) of the specified {@link BigDecimal} value.
   *
   * @param n The {@link BigDecimal} value whose number of digits is to be returned.
   * @return The count of the number of digits in the specified {link BigDecimal} value.
   */
  public static int precision(final BigDecimal n) {
    return n.signum() == 0 ? 1 : n.precision();
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code byte} value.
   *
   * @param n The {@code byte} value whose number of trailing zeroes is to be returned.
   * @return The count of trailing zeroes in the specified {@code byte} value.
   */
  public static byte trailingZeroes(final byte n) {
    return (byte)(n == 0 ? 1 : n % 10 != 0 ? 0 : n % 100 != 0 ? 1 : 2);
  }

  /**
   * Returns the count of trailing zeroes in the specified {@code short} value.
   *
   * @param n The {@code short} value whose number of trailing zeroes is to be returned.
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
   * @param n The {@code int} value whose number of trailing zeroes is to be returned.
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
   * @param n The {@code long} value whose number of trailing zeroes is to be returned.
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
   * Returns the signum of the provided {@link Number}.
   *
   * @param a The {@code int} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code int} is negative, zero or positive.
   * @throws NullPointerException If the provided {@link Number} is null.
   */
  public static int signum(final Number a) {
    if (a instanceof Integer)
      return signum((int)a);

    if (a instanceof Long)
      return signum((long)a);

    if (a instanceof BigInteger)
      return ((BigInteger)a).signum();

    if (a instanceof BigDecimal)
      return ((BigDecimal)a).signum();

    if (a instanceof Double)
      return signum((double)a);

    if (a instanceof Float)
      return signum((float)a);

    if (a instanceof Short)
      return signum((short)a);

    if (a instanceof Byte)
      return signum((byte)a);

    return signum(a.doubleValue());
  }

  /**
   * Returns the signum of the provided {@code byte}.
   *
   * @param a The {@code byte} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code byte} is negative, zero or positive.
   */
  public static int signum(final byte a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code short}.
   *
   * @param a The {@code short} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code short} is negative, zero or positive.
   */
  public static int signum(final short a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code int}.
   *
   * @param a The {@code int} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code int} is negative, zero or positive.
   */
  public static int signum(final int a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code long}.
   *
   * @param a The {@code long} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code long} is negative, zero or positive.
   */
  public static int signum(final long a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code float}.
   *
   * @param a The {@code float} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code float} is negative, zero or positive.
   */
  public static int signum(final float a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns the signum of the provided {@code double}.
   *
   * @param a The {@code double} whose signum to return.
   * @return {@code -1}, {@code 0}, or {@code 1} as the value of the provided {@code double} is negative, zero or positive.
   */
  public static int signum(final double a) {
    return a < 0 ? -1 : a > 0 ? 1 : 0;
  }

  /**
   * Returns a {@link Number} of type {@code N} whose value is {@code a + b}.
   *
   * @param <N> The type parameter for the numbers to be added.
   * @param a The first {@code N} to be added.
   * @param b The second {@code N} to be added.
   * @return A {@link Number} of type {@code N} whose value is {@code a + b}.
   * @throws NullPointerException If the {@link Number} is null.
   * @throws UnsupportedOperationException If the {@link Number} sub-type is not supported.
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N add(final N a, final N b) {
    if (a instanceof BigDecimal)
      return (N)((BigDecimal)a).add((BigDecimal)b);

    if (a instanceof BigInteger)
      return (N)((BigInteger)a).add((BigInteger)b);

    if (a instanceof Byte)
      return (N)(Integer)((Byte)a + (Byte)b);

    if (a instanceof Short)
      return (N)(Integer)((Short)a + (Short)b);

    if (a instanceof Integer)
      return (N)(Integer)((Integer)a + (Integer)b);

    if (a instanceof Long)
      return (N)(Long)((Long)a + (Long)b);

    if (a instanceof Float)
      return (N)(Float)((Float)a + (Float)b);

    if (a instanceof Double)
      return (N)(Double)((Double)a + (Double)b);

    Objects.requireNonNull(a);
    throw new UnsupportedOperationException("Unsupported type: " + a.getClass().getName());
  }

  private Numbers() {
  }
}