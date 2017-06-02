/* Copyright (c) 2008 lib4j
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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author seva
 *
 */
public final class Numbers {
  public static class Unsigned {
    private static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("18446744073709551999");

    public static short toSigned(final byte unsigned) {
      return (short)(unsigned - Byte.MIN_VALUE);
    }

    public static int toSigned(final short unsigned) {
      return unsigned - Short.MIN_VALUE;
    }

    public static long toSigned(final int unsigned) {
      return (long)unsigned - Integer.MIN_VALUE;
    }

    public static BigInteger toSigned(final long unsigned) {
      return BigInteger.valueOf(unsigned).subtract(BigInteger.valueOf(Long.MIN_VALUE));
    }

    public static byte toUnsigned(final byte signed) {
      if (signed < 0)
        throw new IllegalArgumentException("signed < 0");

      return (byte)(signed + Byte.MIN_VALUE);
    }

    public static byte toUnsigned(final short signed) {
      if (signed < 0 || Byte.MAX_VALUE - Byte.MIN_VALUE < signed)
        throw new IllegalArgumentException("signed < 0 || 256 < signed");

      return (byte)(signed + Byte.MIN_VALUE);
    }

    public static short toUnsigned(final int signed) {
      if (signed < 0 || Short.MAX_VALUE - Short.MIN_VALUE < signed)
        throw new IllegalArgumentException("signed < 0 || 65535 < signed");

      return (short)(signed + Short.MIN_VALUE);
    }

    public static int toUnsigned(final long signed) {
      if (signed < 0 || Integer.MAX_VALUE - Integer.MIN_VALUE < signed)
        throw new IllegalArgumentException("signed < 0 || 4294967295 < signed");

      return (int)(signed + Integer.MIN_VALUE);
    }

    public static long toUnsigned(final BigInteger signed) {
      if (signed.signum() == -1 || UNSIGNED_LONG_MAX_VALUE.compareTo(signed) == -1)
        throw new IllegalArgumentException("signed < 0 || " + UNSIGNED_LONG_MAX_VALUE + " < signed");

      return signed.subtract(BigInteger.valueOf(Long.MIN_VALUE)).longValue();
    }

    private Unsigned() {
    }
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

  public static final double LOG2 = Math.log(2);

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
   * Parses the string argument as a boolean. The {@code Boolean}
   * returned is {@code null} if the string argument is {@code null}, and
   * the value {@code true} if the string argument is equal, ignoring case,
   * to the string {@code "true"}. <p>
   * Example: {@code Boolean.parseBoolean("True")} returns {@code true}.<br>
   * Example: {@code Boolean.parseBoolean("yes")} returns {@code false}.
   *
   * @param      s   the {@code String} containing the boolean
   *                 representation to be parsed
   * @return     the {@code Boolean} represented by the string argument
   * @see        java.lang.Boolean#parseBoolean(String)
   */
  public static Boolean parseBoolean(final String s) {
    return s == null ? null : Boolean.parseBoolean(s);
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Short#parseShort(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code short}.
   *
   * @param s    a {@code String} containing the {@code Short}
   *             representation to be parsed
   * @return     the integer value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code short}.
   * @see        java.lang.Short#parseShort(String)
   */
  public static Short parseShort(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Short.parseShort(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Integer#parseInt(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code int}.
   *
   * @param s    a {@code String} containing the {@code Integer}
   *             representation to be parsed
   * @return     the {@code int} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code int}.
   * @see        java.lang.Integer#parseInt(String)
   */
  public static Integer parseInteger(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Integer.parseInt(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Long#parseLong(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code long}.
   *
   * @param s    a {@code String} containing the {@code Long}
   *             representation to be parsed.
   * @return     the {@code long} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code long}.
   * @see        java.lang.Long#parseLong(String)
   */
  public static Long parseLong(final String s) {
    // FIXME: Can a NumberFormatException be avoided altogether? Yes, if
    // FIXME: the implementation is copied.
    try {
      return s == null ? null : Long.parseLong(s);
    }
    catch (final NumberFormatException e) {
      return null;
    }
  }

  /**
   * Parses the string argument as per the specification of
   * {@code Float#parseFloat(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code float}.
   *
   * @param s    a {@code String} containing the {@code Float}
   *             representation to be parsed.
   * @return     the {@code float} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code float}.
   * @see        java.lang.Float#parseFloat(String)
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
   * {@code Double#parseDouble(String)}, but returns {@code null} if the
   * string does not contain a parsable {@code double}.
   *
   * @param s    a {@code String} containing the {@code Double}
   *             representation to be parsed.
   * @return     the {@code double} value represented by the argument, or
   *             {@code null} if the string does not contain a parsable
   *             {@code double}.
   * @see        java.lang.Long#parseLong(String)
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

  public static int[] parseInt(final String ... s) {
    final int[] values = new int[s.length];
    for (int i = 0; i < s.length; i++)
      values[i] = Integer.parseInt(s[i]);

    return values;
  }

  public static double[] parseDouble(final String ... s) {
    final double[] values = new double[s.length];
    for (int i = 0; i < s.length; i++)
      values[i] = Double.parseDouble(s[i]);

    return values;
  }

  public static double parseNumber(String string) {
    if (string == null || (string = string.trim()).length() == 0 || !isNumber(string))
      return Double.NaN;

    double scalar = 0;
    final String[] parts = string.split(" ");
    if (parts.length == 2) {
      scalar += new BigDecimal(parts[0]).doubleValue();
      string = parts[1];
    }

    final int slash = string.indexOf('/');
    if (slash == 1)
      scalar += (double)Integer.parseInt(string.substring(0, slash)) / (double)Integer.parseInt(string.substring(slash + 1, string.length()));
    else
      scalar += new BigDecimal(string).doubleValue();

    return scalar;
  }

  public static boolean isNumber(String string) {
    if (string == null || (string = string.trim()).length() == 0)
      return false;

    final String[] parts = string.split(" ");
    if (parts.length > 2)
      return false;

    if (parts.length == 2) {
      final int slash = parts[1].indexOf('/');
      if (slash == -1)
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
    for (int i = string.length() - 1; i >= 0; i--) {
      char c = string.charAt(i);
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
        else
          return false;
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

        factor++;
      }
    }

    return true;
  }

  public static boolean isInteger(final float number) {
    return (int)number == number;
  }

  public static boolean isInteger(final double number) {
    return (int)number == number;
  }

  public static long checkedMultiple(final long a, final long b) {
    final long maximum = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
    if (a != 0 && (b > 0 && b > maximum / a || b < 0 && b < maximum / a))
      throw new ArithmeticException("long overflow");

    return a * b;
  }

  public static int rotateBits(final int value, final int sizeof, final int distance) {
    return (int)((distance == 0 ? value : (distance < 0 ? value << -distance | value >> (sizeof + distance) : value >> distance | value << (sizeof - distance))) & (pow(2, sizeof) - 1));
  }

  public static String toString(final double value, final int decimals) {
    final double factor = Math.pow(10, decimals);
    return String.valueOf(Math.round(value * factor) / factor);
  }

  public static String roundInsignificant(final String value) {
    if (value == null)
      return null;

    if (value.length() == 0)
      return value;

    int i = value.length();
    while (i-- > 0)
      if (value.charAt(i) != '0' && value.charAt(i) != '.')
        break;

    return value.substring(0, i + 1);
  }

  // FIXME: This is not working! it is returning incorrect results
  public static double log2(BigInteger value) {
    final int blex = value.bitLength() - 1022; // any value in 60..1023 is ok
    if (blex > 0)
      value = value.shiftRight(blex);

    final double result = Math.log(value.doubleValue());
    return blex > 0 ? result + blex * LOG2 : result;
  }

  private static final double EPSILON = 0.000000000000001d;

  public static boolean equivalent(final Number a, final Number b) {
    if (a == null)
      return b == null;

    if (b == null)
      return false;

    return Math.abs(a.doubleValue() - b.doubleValue()) < EPSILON;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Number>T cast(final Number number, final Class<T> type) {
    if (type == Byte.class)
      return (T)new Byte(number.byteValue());

    if (type == Short.class)
      return (T)new Short(number.shortValue());

    if (type == Integer.class)
      return (T)new Integer(number.intValue());

    if (type == Float.class)
      return (T)new Float(number.floatValue());

    if (type == Double.class)
      return (T)new Double(number.doubleValue());

    if (type == Long.class)
      return (T)new Long(number.longValue());

    throw new UnsupportedOperationException("Unsupported Numebr type: " + type.getName());
  }

  public static int precision(final int number) {
    return (int)Math.log10(Math.abs(number)) + 1;
  }

  public static int precision(final long number) {
    return (int)Math.log10(Math.abs(number)) + 1;
  }

  public static int precision(final BigInteger number) {
    return number.abs().toString().length();
  }

  public static int precision(final BigDecimal number) {
    return number.precision();
  }

  public static int trailingZeroes(int number) {
    int zeros = 0;
    while (number % 10 == 0 && number != 0) {
      zeros++;
      number /= 10;
    }

    return zeros;
  }

  public static int trailingZeroes(long number) {
    int zeroes = 0;
    while (number % 10 == 0 && number != 0) {
      zeroes++;
      number /= 10;
    }

    return zeroes;
  }

  public static int numberOfDecimalPlaces(final BigDecimal bigDecimal) {
    return Math.max(0, bigDecimal.stripTrailingZeros().scale());
  }

  private Numbers() {
  }
}